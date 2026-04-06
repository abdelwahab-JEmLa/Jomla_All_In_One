package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Modules.Loading_Datas.Init.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.deleteData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiStateSec9Frag1(
    val a_ProduitInfosList: List<M01Produit> = emptyList(),
    val selectionePourDeplacement_Categorie: M16CategorieProduit? = null,
    val mainLoadingProgressPJuin3: Float = 0f,
    val activeCatalogue: M21CataloguesCategorie = get_ListM21CataloguesCategorie().first(),
    var currentMode: EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage =
        EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage.REORDER_GRID,
    val clickItemMode: ClickItemMode = ClickItemMode.FastMove,
) {
    enum class ClickItemMode(val couleur: Color, val icon: ImageVector) {
        Standart(Color.Gray, Icons.Default.Refresh),
        FastMove(Color.Blue, Icons.Default.TouchApp);

        fun toggle() = when (this) {
            Standart -> FastMove
            FastMove -> Standart
        }
    }
}

class EditeBaseDonneMainScreenIdS9ViewModel(
    val aCentralFacade: ACentralFacade,
    private val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    val a_CentralDatasHandlerProtoJuin9 = aCentralFacade.repositorysMainGetter
    val a_ProduitDataBaseComposeRepositoryPJ17 =
        a_CentralDatasHandlerProtoJuin9.repo1ProduitInfos
    private val dataHandler = aCentralFacade.repositorysMainGetter
    private val setter = aCentralFacade.repositorysMainSetter
    val categoriesCompoRepository = dataHandler.repoM16CategorieProduit
    private val produitRepository = dataHandler.repo1ProduitInfos

    private val _uiState = MutableStateFlow(UiStateSec9Frag1())
    val uiState: StateFlow<UiStateSec9Frag1> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        a_ProduitInfosList = model.repoStateA_ProduitInfos?.modelListFlow
                            ?: emptyList(),
                        mainLoadingProgressPJuin3 = model.progress
                    )
                }
            }
        }
    }

    enum class ModeAffichage { CATEGORIES_LIST, PRODUCTS_LIST, REORDER_GRID }

    fun updateCate_cSelectionePourDeplace(categorie: M16CategorieProduit? = null) {
        _uiState.value = _uiState.value.copy(
            selectionePourDeplacement_Categorie =
                if (_uiState.value.selectionePourDeplacement_Categorie == null
                    && categorie != null
                )
                    categorie else null
        )
    }

    fun new_currentMode(currentMode: ModeAffichage) {
        _uiState.value = _uiState.value.copy(currentMode = currentMode)
    }

    fun toggleEntreEntitiesClickItemMode() {
        _uiState.value = _uiState.value.copy(clickItemMode = _uiState.value.clickItemMode.toggle())
    }

    fun updateActiveCatalogue(catalogue: M21CataloguesCategorie) {
        _uiState.value = _uiState.value.copy(activeCatalogue = catalogue)
    }

    fun toggleToCatalogue(catalogueId: Long) {
        val catalogues = get_ListM21CataloguesCategorie()
        val newCatalogue = catalogues.find { it.id == catalogueId } ?: catalogues.first()
        updateActiveCatalogue(newCatalogue)
    }

    fun moveCategoriesAuCatalogue(targetCatalogueId: Long) {
        val categoriesToMove =
            categoriesCompoRepository.datasValue.filter {
                it.cSelectionePourDeplace ||
                        it.id == _uiState.value.selectionePourDeplacement_Categorie?.id
            }
        if (categoriesToMove.isEmpty()) return

        val updatedCategories = categoriesToMove.map {
            it.copy(catalogueParentId = targetCatalogueId, cSelectionePourDeplace = false)
        }
        addOrUpdateCategories(updatedCategories)

        // Clear the UI state selection after moving
        if (_uiState.value.selectionePourDeplacement_Categorie != null) {
            _uiState.value = _uiState.value.copy(selectionePourDeplacement_Categorie = null)
        }
    }

    // FIXED: Removed unused parameter and corrected function signature
    fun moveSelectedCategoriesRelativeToTarget(
        targetCategoryId: Long,
        moveBefore: Boolean
    ) {
        val currentCategories = categoriesCompoRepository.datasValue
        val selectedCategories = currentCategories.filter { it.cSelectionePourDeplace }

        if (selectedCategories.isEmpty() || selectedCategories.any { it.id == targetCategoryId }) return

        val reorderedCategories = performCategoryReorder(
            categories = currentCategories,
            selectedIds = selectedCategories.map { it.id }.toSet(),
            targetId = targetCategoryId,
            moveBefore = moveBefore
        )

        val finalCategories = reorderedCategories.map { category ->
            if (selectedCategories.any { it.id == category.id }) {
                category.copy(cSelectionePourDeplace = false)
            } else category
        }

        addOrUpdateCategories(finalCategories)
    }

    private fun performCategoryReorder(
        categories: List<M16CategorieProduit>,
        selectedIds: Set<Long>,
        targetId: Long,
        moveBefore: Boolean
    ): List<M16CategorieProduit> {
        if (selectedIds.isEmpty() || selectedIds.contains(targetId)) return categories

        val selected = categories.filter { selectedIds.contains(it.id) }
        val remaining = categories.filter { !selectedIds.contains(it.id) }
        val targetIndex = remaining.indexOfFirst { it.id == targetId }

        if (targetIndex == -1) return categories

        val insertIndex = if (moveBefore) targetIndex else targetIndex + 1
        val newList = remaining.toMutableList()

        selected.forEachIndexed { i, cat ->
            newList.add(insertIndex + i, cat)
        }

        return newList.mapIndexed { i, cat -> cat.copy(position = i + 1) }
    }

    fun getSelectedCategoryIds(): Set<Long> {
        return categoriesCompoRepository.datasValue
            .filter { it.cSelectionePourDeplace }
            .map { it.id }
            .toSet()
    }

    fun addOrUpdateCategorie(categorie: M16CategorieProduit) {
        categoriesCompoRepository.addOrUpdateData(categorie)
    }

    fun addOrUpdateCategories(categories: List<M16CategorieProduit>) {
        if (categories.size > 10) {
            categoriesCompoRepository.reorderCategories(categories)
        } else {
            categoriesCompoRepository.addOrUpdateDatas(categories, avec_BatchFireBase = true)
        }
    }

    fun deleteAddMultiCategories(newDatas: List<M16CategorieProduit>) {
        categoriesCompoRepository.deleteAddMultiDatas(newDatas)
    }

    fun deleteAddMultiProduits(list_M1Produit: List<M01Produit>) {
        setter.deleteAddMultiDatas(list_M1Produit)
    }

    fun addOrUpdateProduit(data: M01Produit) {
        produitRepository.upsert(data)
    }

    fun addOrUpdateProduits(datas: List<M01Produit>) {
        datas.forEach { addOrUpdateProduit(it) }
    }

    fun deleteArticlesBasesStatsTable(data: M01Produit) {
        masterRepositorys.repoA_ProduitInfos.deleteData(data)
    }

    fun deleteAddMultiClients() {
        setter.deleteAddMultiClients()
    }
}
