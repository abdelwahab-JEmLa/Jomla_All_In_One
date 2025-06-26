package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.E_GroupedDataBasesRepositoryNonConnue
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val B_ClientInfosProtoJuin3List: List<B_ClientInfosProtoJuin3> = emptyList(),
    val mainLoadingProgress: Float = 0f
)
class PanierFinaleDAchatViewModel(
    val a_CentralDatasHandlerProtoJuin9 : ACentralCompoRepositoryProtoJuin9,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    private val groupedDataBasesRepository: E_GroupedDataBasesRepositoryNonConnue,
) : ViewModel() {
    val a_ProduitDataBaseComposeRepositoryPJ17 = a_CentralDatasHandlerProtoJuin9.bProduitDataBase_SubClassFunctionality
    val d_AchatOperationComposeRepositoryPJ17 = a_CentralDatasHandlerProtoJuin9.fCouleurAchatOperationRepositoryComposable

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        B_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }

    fun updatePrice(
        priceText: String,
        defaultPrice: Double,
        produitAcheteOperation: _1_2_ProduitAcheteOperation?,
        repositoryModel: GroupeRepositorysProtoAvJuin3Model,
        updateChangePrixDeBase: Boolean = false
    ) {
        val newPrice = priceText.toDoubleOrNull() ?: defaultPrice

        produitAcheteOperation?.let { product ->
            val updatedProduct = product.copy(
                provisoireMonPrix = newPrice
            )
            repositoryModel
                .repositoryC2_ProduitAcheteOperation
                .updateUnSeulData(updatedProduct)
        }

        // Only call updateChangePrixDeBase if the flag is true
        if (updateChangePrixDeBase) {
            updateChangePrixDeBase(newPrice, produitAcheteOperation?.produitAcheterID ?: 0L)
        }
    }

    private fun updateChangePrixDeBase(newPrice: Double, produitAcheterID: Long) {
        val currentData = groupedDataBasesRepository.modelListFlow.value
            .firstOrNull()
            ?.a_ProduitInfos
            ?.find { it.id == produitAcheterID }

        currentData?.let { produitInfo ->
            val updatedProduitInfo = produitInfo.copy(
                prixVent = newPrice,
                needUpdate = true
            )

            // Insert the updated data
            groupedDataBasesRepository.update(
                data = updatedProduitInfo,
            )
        }
    }
}
