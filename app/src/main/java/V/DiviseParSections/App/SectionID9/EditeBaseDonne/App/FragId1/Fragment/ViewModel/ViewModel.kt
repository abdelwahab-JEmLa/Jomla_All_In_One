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

    fun moveCategoriesAuCatalogue(targetCatalogueId: Long) {
        val categoriesToMove = categoriesCompoRepository.datasValue.filter { it.cSelectionePourDeplace }

        if (categoriesToMove.isEmpty()) {
            return
        }

        val updatedCategories = categoriesToMove.map { categorie ->
            categorie.copy(
                catalogueParentId = targetCatalogueId,
                cSelectionePourDeplace = false
            )
        }

        addOrUpdateCategories(updatedCategories)
    }

    fun updateCate_cSelectionePourDeplace(categorie: CategoriesTabelle) {
        val newData = categorie.copy(cSelectionePourDeplace = !categorie.cSelectionePourDeplace)
        addOrUpdateCategorie(newData)
    }

    fun addOrUpdateCategorie(categorie: CategoriesTabelle) {
        categoriesCompoRepository.addOrUpdateData(categorie)
    }

    fun addOrUpdateCategories(categories: List<CategoriesTabelle>) {
        categoriesCompoRepository.addOrUpdateDatas(categories)
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
