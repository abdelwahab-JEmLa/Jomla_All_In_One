package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.SortOrder
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_CentralDatasHandlerProtoJuin9
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.DisponibilityEtates
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.addOrUpdateDatasList
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.deleteData
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val a_ProduitInfosList: List<ArticlesBasesStatsTable> = emptyList(),
    val mainLoadingProgressPJuin3: Float = 0f,
)

class EditeBaseDonneMainScreenIdS9ViewModel(
    val a_CentralDatasHandlerProtoJuin9: A_CentralDatasHandlerProtoJuin9,
    private val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    val categoriesCompoRepository = a_CentralDatasHandlerProtoJuin9.b3CategoriesCompoRepository

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        collecteMasterRepositorysDatasAuUiState()
    }

    private fun collecteMasterRepositorysDatasAuUiState() {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    val newProduitInfosList = model.repoStateA_ProduitInfos?.modelListFlow ?: emptyList()
                    val newProgress = model.progress

                    _uiState.value = _uiState.value.copy(
                        a_ProduitInfosList = newProduitInfosList,
                        mainLoadingProgressPJuin3 = newProgress
                    )
                }
            }
        }
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
                val categoriesToMove = categoriesCompoRepository.datasValue.filter { it.cSelectionePourDeplace }
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
        return categoriesCompoRepository.datasValue.filter { it.cSelectionePourDeplace }.map { it.id }.toSet()
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

    fun addOrUpdateProduit(data: ArticlesBasesStatsTable) {
        masterRepositorys.repoA_ProduitInfos.addOrUpdateData(data)
    }

    fun addOrUpdateProduits(datas: List<ArticlesBasesStatsTable>) {
        masterRepositorys.repoA_ProduitInfos.addOrUpdateDatasList(datas)
    }

    fun deleteArticlesBasesStatsTable(data: ArticlesBasesStatsTable) {
        masterRepositorys.repoA_ProduitInfos.deleteData(data)
    }
}
