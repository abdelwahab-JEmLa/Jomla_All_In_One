package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_CentralDatasHandlerProtoJuin9
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.addOrUpdateDatasList
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update.deleteData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val a_ProduitInfosList: List<ArticlesBasesStatsTable> = emptyList(),
    val c_CategorieProduitInfosList: List<CategoriesTabelle> = emptyList(),
    val mainLoadingProgressPJuin3: Float = 0f,
)

class EditeBaseDonneMainScreenIdS9ViewModel(
    val a_CentralDatasHandlerProtoJuin9: A_CentralDatasHandlerProtoJuin9,
    private val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    val b3CategoriesCompoRepository = a_CentralDatasHandlerProtoJuin9
        .b3CategoriesCompoRepository
    val b3Categories= b3CategoriesCompoRepository.datasValue


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        collecteMasterRepositorysDatasAuUiState()
        collecteCategoriesDataAuUiState()
    }

    private fun collecteMasterRepositorysDatasAuUiState() {
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

    private fun collecteCategoriesDataAuUiState() {
        viewModelScope.launch {
            a_CentralDatasHandlerProtoJuin9.b3CategoriesCompoRepository.datasState.let { categoriesState ->
                _uiState.value = _uiState.value.copy(
                    c_CategorieProduitInfosList = categoriesState.value
                )
            }
        }
    }

    /**
     * Moves selected categories to a specific catalogue and clears their selection state
     * @param targetCatalogueId ID of the target catalogue (0L for "Autres")
     */
    fun moveCategoriesAuCatalogue(targetCatalogueId: Long) {
        val updatedCategories = b3Categories
            .filter { it.cSelectionePourDeplace }
            .map { categorie ->
                categorie.copy(
                    catalogueParentId = targetCatalogueId,
                    cSelectionePourDeplace = false
                )
            }
        b3CategoriesCompoRepository.logCategoriesSelectionForDisplacementIfNeeded(updatedCategories,true)

        // Update all modified categories in batch
        if (updatedCategories.isNotEmpty()) {
            b3CategoriesCompoRepository.addOrUpdateDatas(updatedCategories)
        }
    }

    fun updateCate_cSelectionePourDeplace(categorie: CategoriesTabelle, newValeur: Boolean) {
        val newData = categorie.copy(cSelectionePourDeplace = newValeur)
        addOrUpdateCategorie(newData)
    }

    fun addOrUpdateCategorie(categorie: CategoriesTabelle) { b3CategoriesCompoRepository.addOrUpdateData(categorie) }
    fun addOrUpdateCategories(categories: List<CategoriesTabelle>) { b3CategoriesCompoRepository.addOrUpdateDatas(categories) }

    fun addOrUpdateProduit(data: ArticlesBasesStatsTable) {
        masterRepositorys.repoA_ProduitInfos.addOrUpdateData(
            data
        )
    }
    fun addOrUpdateProduits(datas: List<ArticlesBasesStatsTable>) { masterRepositorys.repoA_ProduitInfos.addOrUpdateDatasList(datas) }
    fun deleteArticlesBasesStatsTable(data: ArticlesBasesStatsTable) { masterRepositorys.repoA_ProduitInfos.deleteData(data) }
}
