package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
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
    val a_ProduitInfosList: List<ArticlesBasesStatsTable> = emptyList(),
    val selectionePourDeplacement_Categorie: CategoriesTabelle? = null,
    val mainLoadingProgressPJuin3: Float = 0f,
    val activeCatalogue: CataloguesCaegorie = B4CatalogueCategoriesRepository().first(),
    var currentMode: EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage =
        EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage.PRODUCTS_LIST,
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

    fun updateCate_cSelectionePourDeplace(categorie: CategoriesTabelle? = null) {
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

    fun updateActiveCatalogue(catalogue: CataloguesCaegorie) {
        _uiState.value = _uiState.value.copy(activeCatalogue = catalogue)
    }

    fun toggleToCatalogue(catalogueId: Long) {
        val catalogues = B4CatalogueCategoriesRepository()
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
        categories: List<CategoriesTabelle>,
        selectedIds: Set<Long>,
        targetId: Long,
        moveBefore: Boolean
    ): List<CategoriesTabelle> {
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

    fun addOrUpdateCategorie(categorie: CategoriesTabelle) {
        categoriesCompoRepository.addOrUpdateData(categorie)
    }

    fun addOrUpdateCategories(categories: List<CategoriesTabelle>) {
        if (categories.size > 10) {
            categoriesCompoRepository.reorderCategories(categories)
        } else {
            categoriesCompoRepository.addOrUpdateDatas(categories, avec_BatchFireBase = true)
        }
    }

    fun deleteAddMultiCategories(newDatas: List<CategoriesTabelle>) {
        categoriesCompoRepository.deleteAddMultiDatas(newDatas)
    }

    fun deleteAddMultiProduits(list_M1Produit: List<ArticlesBasesStatsTable>) {
        setter.deleteAddMultiDatas(list_M1Produit)
    }

    fun addOrUpdateProduit(data: ArticlesBasesStatsTable) {
        produitRepository.upsert(data)
    }

    fun addOrUpdateProduits(datas: List<ArticlesBasesStatsTable>) {
        datas.forEach { addOrUpdateProduit(it) }
    }

    fun deleteArticlesBasesStatsTable(data: ArticlesBasesStatsTable) {
        masterRepositorys.repoA_ProduitInfos.deleteData(data)
    }

    fun deleteAddMultiClients() {
        setter.deleteAddMultiClients()
    }
}
