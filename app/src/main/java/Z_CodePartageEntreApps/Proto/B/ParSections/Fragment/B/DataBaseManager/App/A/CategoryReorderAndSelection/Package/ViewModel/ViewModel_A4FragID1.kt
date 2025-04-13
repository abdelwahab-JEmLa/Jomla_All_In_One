package Z_CodePartageEntreApps.Proto.B.ParSections.Fragment.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.I_CategorieProduitsRepository
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.I_CategorieProduitsRepositoryImpl
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("MutableCollectionMutableState")
class ViewModel_A4FragID1(
    val mainRepository: I_CategorieProduitsRepository,
    val a_ProduitModelRepository: A_ProduitRepository,
) : ViewModel() {
    val a_ProduitModel = a_ProduitModelRepository.modelDatas
    val TAG = "ViewModel_A4FragID1"

    var isDataLoading by mutableStateOf(true)
        private set

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    var filterProduits by mutableStateOf(false)

    init {
        viewModelScope.launch {
            waitForDataInitialization()
        }
    }

    fun updateUneSeulDataDeCategories(category: I_CategorieProduits) {
        viewModelScope.launch {
            try {
                mainRepository.updateUnSeulData(category)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    fun ProduitUpdateUnSeul(data: A_Produit) {
        viewModelScope.launch {
            try {
                a_ProduitModelRepository.updateUnSeulData(data)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    private suspend fun waitForDataInitialization() {
        try {
            mainRepository.progressRepo.collect { progress ->
                isDataLoading = progress < 1.0f
                if (progress >= 1.0f && !_isInitialized.value) {
                    _isInitialized.value = true
                }
            }
        } catch (e: Exception) {
            isDataLoading = false
            _isInitialized.value = true
        }
    }

    val i_CategoriesProduits by lazy {
        viewModelScope.launch {
            ensureDataIsInitialized()
        }
        try {
            mainRepository.modelDatas
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private suspend fun ensureDataIsInitialized() {
        try {
            val repoImpl = mainRepository as? I_CategorieProduitsRepositoryImpl
            if (repoImpl != null && !repoImpl.initialDataLoaded) {
                withContext(Dispatchers.IO) {
                    _isInitialized.first { it }
                }
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    fun addNewCategory(categoryName: String) {
        viewModelScope.launch {
            try {
                ensureDataIsInitialized()
                val newCategory = createNewCategory(categoryName)
                val updatedCategories = updateExistingCategoriesPositions()
                mainRepository.updateMultiDatas((listOf(newCategory) + updatedCategories).toMutableStateList())
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    private fun createNewCategory(categoryName: String): I_CategorieProduits {
        val maxId = i_CategoriesProduits
            .maxOfOrNull { it.id }
            ?: 0

        return I_CategorieProduits(
            id = maxId + 1,
            nom = categoryName,
            indexDonsParentList = 0,
        )
    }

    private fun updateExistingCategoriesPositions(): List<I_CategorieProduits> {
        return i_CategoriesProduits.map { category ->
            val updatedCategory = I_CategorieProduits(
                id = category.id,
                indexDonsParentList = category.indexDonsParentList + 1,
            )
            updatedCategory
        }
    }

    fun moveArticlesBetweenCategories(
        fromCategoryId: Long,
        toCategoryId: Long
    ) {
        viewModelScope.launch {
            try {
                ensureDataIsInitialized()

                val maxClassificationId = a_ProduitModel
                    .filter { it.parentCategoryId == toCategoryId }
                    .maxOfOrNull { it.indexInParentCategorie } ?: 0

                val updatedArticles = a_ProduitModel.map { article ->
                    when (article.parentCategoryId) {
                        fromCategoryId -> article.apply {
                            parentCategoryId = toCategoryId
                            indexInParentCategorie = maxClassificationId + 1
                        }
                        else -> article
                    }
                }

                a_ProduitModelRepository.updateMultiDatas(updatedArticles.toMutableStateList())
                deleteCategorie(fromCategoryId)

            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    private fun deleteCategorie(fromCategoryId: Long) {
        viewModelScope.launch {
            try {
                val updatedCategories = i_CategoriesProduits
                    .filter { it.indexDonsParentList != fromCategoryId }

                updateClassmentsCategories(updatedCategories)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    private fun updateClassmentsCategories(updatedCategories: List<I_CategorieProduits>) {
        viewModelScope.launch {
            try {
                val updatedClassmentCategories = updatedCategories.mapIndexed { index, category ->
                    val updatedCategory = I_CategorieProduits(
                        id = category.id,
                        category.nom,
                        category.groupeParentId
                    )
                    updatedCategory.indexDonsParentList = (index + 1).toLong()
                    updatedCategory
                }

                mainRepository.updateMultiDatas(updatedClassmentCategories.toMutableStateList())
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    fun handleCategoryMove(
        holdedIdCate: Long,
        clickedCategoryId: Long,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                ensureDataIsInitialized()

                val categories = i_CategoriesProduits.toMutableList()

                val fromIndex = categories.indexOfFirst { it.id == holdedIdCate }
                val toIndex = categories.indexOfFirst { it.id == clickedCategoryId }

                if (fromIndex != -1 && toIndex != -1) {
                    val movedCategory = categories[fromIndex]

                    categories.removeAt(fromIndex)
                    categories.add(toIndex, movedCategory)

                    categories.forEachIndexed { index, category ->
                        category.indexDonsParentList = (index + 1).toLong()
                    }

                    mainRepository.updateMultiDatas(categories.toMutableStateList())
                    onComplete()
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    fun movePlusieurCategories(
        selectedCategories: List<I_CategorieProduits>,
        targetCategory: I_CategorieProduits? = null
    ) {
        viewModelScope.launch {
            try {
                ensureDataIsInitialized()

                if (selectedCategories.isEmpty()) {
                    return@launch
                }

                val allCategories = i_CategoriesProduits.toMutableList()

                val sortedSelectedCategories = selectedCategories.sortedBy {
                    allCategories.indexOfFirst { cat -> cat.id == it.id }
                }

                val remainingCategories = allCategories.filter { category ->
                    !selectedCategories.any { it.id == category.id }
                }.toMutableList()

                val targetIndex = if (targetCategory != null) {
                    val index = remainingCategories.indexOfFirst { it.id == targetCategory.id }
                    if (index != -1) index else 0
                } else {
                    0
                }

                remainingCategories.addAll(targetIndex, sortedSelectedCategories)

                remainingCategories.forEachIndexed { index, category ->
                    category.indexDonsParentList = (index + 1).toLong()
                }

                mainRepository.updateMultiDatas(remainingCategories.toMutableStateList())

            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }


}
