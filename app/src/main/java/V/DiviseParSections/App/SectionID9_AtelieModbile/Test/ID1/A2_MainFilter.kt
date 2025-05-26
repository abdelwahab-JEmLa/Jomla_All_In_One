package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.TypeTarificationEnumT2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfos>,
    bonAchatList: List<C3_BonAchate>,
    produitAcheteOperationList: List<_1_2_ProduitAcheteOperation>,
    produitInfosList: SnapshotStateList<A_ProduitInfos>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null 
) {
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: C3_BonAchate()
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
            maxPrixArriveDuProduit = maxPrixArriveDuProduit,
            clientDefiniTariffs = clientDefiniTariffs,
            clientLastHistoricalPrice = clientLastHistoricalPrice,
            filteredProduit = filteredProduit,
            showLabels = showLabels,
            onClickPrixButton = onClickPrixButton,
            onClickAnulationButton = onClickAnulationButton // Pass the cancellation callback
        )
    }
}
