package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ViewModel.TariffsButtonsViewModelSec7ID2
import Z_CodePartageEntreApps.Model.A_ProduitInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.C3_TransactionCommercial
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfos>,
    bonAchatList: List<C3_TransactionCommercial>,
    produitAcheteOperationList: List<_1_2_ProduitAcheteOperation>,
    produitInfosList: SnapshotStateList<A_ProduitInfos>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null,
    viewModel: TariffsButtonsViewModelSec7ID2
) {
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: C3_TransactionCommercial()
    }

    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: A_ProduitInfos()
    }

    val idClientFiltruer = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID }?.clientAcheteurID ?: 0L
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

    /*  Card {
          Text("produitAcheteOperationList$produitAcheteOperationList")
      }       */

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
            viewModel=viewModel,
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
