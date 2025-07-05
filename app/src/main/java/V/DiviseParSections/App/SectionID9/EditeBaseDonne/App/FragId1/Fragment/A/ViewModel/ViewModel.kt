package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.deleteData
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
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
    val mainLoadingProgressPJuin3: Float = 0f,
    val activeCatalogue: CataloguesCaegorie = B4CatalogueCategoriesRepository().first(),
    var currentMode: EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage = EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage
        .CATEGORIES_LIST,
    val clickItemMode: ClickItemMode = ClickItemMode.FastMove,
) {
    enum class ClickItemMode(val couleur: Color, val icon: ImageVector) {
        Standart(Color.Gray, Icons.Default.Refresh),
        FastMove(Color.Blue, Icons.Default.TouchApp);

        fun toggle(): ClickItemMode {
            return when (this) {
                Standart -> FastMove; FastMove -> Standart
            }
        }
    }
}

class EditeBaseDonneMainScreenIdS9ViewModel(
    aCentral: ACentralFacade,
    private val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    val a_CentralDatasHandlerProtoJuin9 = aCentral.getter
    val setter = aCentral.setter

    val categoriesCompoRepository = a_CentralDatasHandlerProtoJuin9.b3CategoriesCompoRepository
    val a_ProduitDataBaseComposeRepositoryPJ17 =
        a_CentralDatasHandlerProtoJuin9.repoM1ProduitInfos

    private val _uiState = MutableStateFlow(UiStateSec9Frag1())
    val uiState: StateFlow<UiStateSec9Frag1> = _uiState.asStateFlow()

    fun new_currentMode(currentMode: ModeAffichage) {
        _uiState.value = _uiState.value.copy(currentMode = currentMode)
    }

    init {
        collecteMasterRepositorysDatasAuUiState()
    }

    private fun collecteMasterRepositorysDatasAuUiState() {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    val newProduitInfosList =
                        model.repoStateA_ProduitInfos?.modelListFlow ?: emptyList()
                    val newProgress = model.progress

                    _uiState.value = _uiState.value.copy(
                        a_ProduitInfosList = newProduitInfosList,
                        mainLoadingProgressPJuin3 = newProgress
                    )
                }
            }
        }
    }

    fun toggleEntreEntitiesClickItemMode() {
        val currentMode = _uiState.value.clickItemMode
        val newMode = currentMode.toggle()
        _uiState.value = _uiState.value.copy(clickItemMode = newMode)
    }

    fun updateActiveCatalogue(catalogue: CataloguesCaegorie) {
        _uiState.value = _uiState.value.copy(activeCatalogue = catalogue)
    }

    // Function to toggle between catalogues
    fun toggleToCatalogue(catalogueId: Long) {
        val catalogues = B4CatalogueCategoriesRepository()
        val newCatalogue = catalogues.find { it.id == catalogueId } ?: catalogues.first()
        updateActiveCatalogue(newCatalogue)
    }


    enum class MoveOperation {
        TO_CATALOGUE,
        RELATIVE_TO_TARGET,
        REORDER_WITH_CLEAR
    }

    fun moveCategories(
        operation: MoveOperation,
        targetId: Long = 0L,
        moveBefore: Boolean = false,
        newCategoriesList: List<CategoriesTabelle> = emptyList()
    ) {
        when (operation) {
            MoveOperation.TO_CATALOGUE -> {
                val categoriesToMove =
                    categoriesCompoRepository.datasValue.filter { it.cSelectionePourDeplace }
                if (categoriesToMove.isEmpty()) return

                val updatedCategories = categoriesToMove.map { categorie ->
                    categorie.copy(
                        catalogueParentId = targetId,
                        cSelectionePourDeplace = false
                    )
                }
                addOrUpdateCategories(updatedCategories)
            }

            MoveOperation.RELATIVE_TO_TARGET -> {
                val currentCategories = categoriesCompoRepository.datasValue
                val selectedCategories = currentCategories.filter { it.cSelectionePourDeplace }

                if (selectedCategories.isEmpty() || selectedCategories.any { it.id == targetId }) return

                val reorderedCategories = performCategoryReorder(
                    categories = currentCategories,
                    selectedIds = selectedCategories.map { it.id }.toSet(),
                    targetId = targetId,
                    moveBefore = moveBefore
                )

                val finalCategories = reorderedCategories.map { category ->
                    if (selectedCategories.any { it.id == category.id }) {
                        category.copy(cSelectionePourDeplace = false)
                    } else {
                        category
                    }
                }
                addOrUpdateCategories(finalCategories)
            }

            MoveOperation.REORDER_WITH_CLEAR -> {
                val updatedCategories = newCategoriesList.mapIndexed { index, category ->
                    category.copy(
                        position = index + 1,
                        cSelectionePourDeplace = false
                    )
                }
                addOrUpdateCategories(updatedCategories)
            }
        }
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

        selected.forEachIndexed { i, cat -> newList.add(insertIndex + i, cat) }

        return newList.mapIndexed { i, cat -> cat.copy(position = i + 1) }
    }

    fun moveCategoriesAuCatalogue(targetCatalogueId: Long) {
        moveCategories(MoveOperation.TO_CATALOGUE, targetCatalogueId)
    }

    fun moveSelectedCategoriesRelativeToTarget(targetCategoryId: Long, moveBefore: Boolean) {
        moveCategories(MoveOperation.RELATIVE_TO_TARGET, targetCategoryId, moveBefore)
    }

    fun updateCate_cSelectionePourDeplace(categorie: CategoriesTabelle) {
        val newData = categorie.copy(cSelectionePourDeplace = !categorie.cSelectionePourDeplace)
        addOrUpdateCategorie(newData)
    }

    fun getSelectedCategoryIds(): Set<Long> {
        return categoriesCompoRepository.datasValue.filter { it.cSelectionePourDeplace }
            .map { it.id }.toSet()
    }

    fun addOrUpdateCategorie(categorie: CategoriesTabelle) {
        categoriesCompoRepository.addOrUpdateData(categorie)
    }

    fun addOrUpdateCategories(categories: List<CategoriesTabelle>) {
        categoriesCompoRepository.addOrUpdateDatas(categories)
    }

    fun deleteAddMultiCategories(categories: List<CategoriesTabelle>) {
        categoriesCompoRepository.deleteAddMultiDatas(categories)
    }

    fun deleteAddMultiProduits() {
        setter.deleteAddMultiDatas()
    }

    fun addOrUpdateProduit(data: ArticlesBasesStatsTable) {
        a_ProduitDataBaseComposeRepositoryPJ17.upsert(data)
    }

    fun addOrUpdateProduits(datas: List<ArticlesBasesStatsTable>) {
        datas.forEach {
            addOrUpdateProduit(it)
        }
    }

    fun deleteArticlesBasesStatsTable(data: ArticlesBasesStatsTable) {
        masterRepositorys.repoA_ProduitInfos.deleteData(data)
    }

    fun deleteAddMultiClients() {
        setter.deleteAddMultiClients()
    }

    enum class ModeAffichage {
        CATEGORIES_LIST, PRODUCTS_LIST, REORDER_GRID
    }
}
