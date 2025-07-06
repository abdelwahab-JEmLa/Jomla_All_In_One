package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfos>,
    bonAchatList: List<M8BonVent>,
    produitAcheteOperationList: List<_1_2_ProduitAcheteOperation>,
    produitInfosList: List<ArticlesBasesStatsTable>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null,
    viewModel: TariffsButtonsViewModelSec7ID2
) {
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: M8BonVent(
            parentID2ClientKeyByParent = BSetterFacade.getListDesParentKeys("null")[M8BonVent.keyModel] ?: "",
            parentID7VentPeriodeKeyByParent = BSetterFacade.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent] ?: "",
            parentID8C2TypeTransactionKeyByParent = BSetterFacade.getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel] ?: ""
        )
    }

    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: ArticlesBasesStatsTable()
    }

    val idClientFiltruer = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID }?.parentHClientOldID ?: 0L
    }

    val maxPrixArriveDuProduit = remember(tarificationList, filteredProduit) {
        produitAcheteOperationList
            .filter { it.produitAcheterID == filteredProduit.id }
            .maxOfOrNull { it.provisoireMonPrix }
    }

    val clientDefiniTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter {
            it.idParentProduit == filteredProduit.id &&
                    it.parentIdClient == idClientFiltruer
        }
    }

    val lastOrNull_produitAcheteOperationList = produitAcheteOperationList.lastOrNull { operation ->
        operation.produitAcheterID == filteredProduit.id &&
                operation.parentIdClient == idClientFiltruer
    }

    val clientLastHistoricalPrice =
        remember(produitAcheteOperationList, filteredProduit, idClientFiltruer) {
            lastOrNull_produitAcheteOperationList
                ?.provisoireMonPrix ?: 0.0
        }

    Column(modifier = modifier) {
        MainList(
            viewModel =viewModel,
            filteredProduit = filteredProduit,
            showLabels = showLabels,
            clientLastHistoricalPrice = clientLastHistoricalPrice,
            maxPrixArriveDuProduit = maxPrixArriveDuProduit,
            clientDefiniTariffs = clientDefiniTariffs,
            onClickPrixButton = onClickPrixButton,
            onClickAnulationButton = onClickAnulationButton // Pass the cancellation callback
        )
    }
}
