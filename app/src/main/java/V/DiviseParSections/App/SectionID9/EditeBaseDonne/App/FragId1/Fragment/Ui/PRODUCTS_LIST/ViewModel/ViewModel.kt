package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class Sec9FragId1ViewId2ViewModel(
    a_CentralCompoRepositoryProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val appComptComposeRepositoryProtoJuin17 = a_CentralCompoRepositoryProtoJuin9.appComptComposeRepositoryProtoJuin17
    val a_ProduitDataBaseComposeRepositoryPJ17 = a_CentralCompoRepositoryProtoJuin9.a_ProduitDataBaseComposeRepositoryPJ17

    // FIXED: Safe initialization with null check
    val currentAppCompt_bsonObjectId = appComptComposeRepositoryProtoJuin17.currentAppCompt?.bsonObjectId ?: ""

    data class UiState(
        var showDetailsExpanded: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun get_showDetailsExpanded(produit: ArticlesBasesStatsTable) =
        currentAppCompt_bsonObjectId.isNotEmpty() &&
                produit.afficheCesDetailPourComptBsonId.contains(currentAppCompt_bsonObjectId)

    fun get_updatedDetailIds(produit: ArticlesBasesStatsTable): String {
        if (currentAppCompt_bsonObjectId.isEmpty()) return produit.afficheCesDetailPourComptBsonId

        val currentDetailIds = produit.afficheCesDetailPourComptBsonId
        val updatedDetailIds = if (currentDetailIds.contains(currentAppCompt_bsonObjectId)) {
            currentDetailIds.replace(currentAppCompt_bsonObjectId, "").replace(",,", ",").trim(',')
        } else {
            if (currentDetailIds.isEmpty()) {
                currentAppCompt_bsonObjectId
            } else {
                "$currentDetailIds,$currentAppCompt_bsonObjectId"
            }
        }
        return updatedDetailIds
    }

    fun update_afficheCesDetailPourComptBsonId(produit: ArticlesBasesStatsTable) {
        if (currentAppCompt_bsonObjectId.isEmpty()) return // Guard against empty ID

        val updatedDetailIds = get_updatedDetailIds(produit)
        val updatedProduct = produit.copy(
            afficheCesDetailPourComptBsonId = updatedDetailIds
        )
        aProduitdatabasecomposerepositorypj17_addOrUpdateData(updatedProduct)
    }

    fun update_showDetailsExpanded() {
        _uiState.value =
            _uiState.value.copy(showDetailsExpanded = !uiState.value.showDetailsExpanded)
    }

    fun aProduitdatabasecomposerepositorypj17_addOrUpdateData(updatedProduct: ArticlesBasesStatsTable) {
        a_ProduitDataBaseComposeRepositoryPJ17.addOrUpdateData(updatedProduct)
    }
}
