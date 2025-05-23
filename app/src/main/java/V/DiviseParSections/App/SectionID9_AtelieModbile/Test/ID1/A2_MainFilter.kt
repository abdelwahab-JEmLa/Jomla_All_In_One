package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfos>,
    bonAchatList: List<C3_BonAchate>,
    produitAcheteOperationList: List<_1_2_ProduitAcheteOperation>,
    produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
) {
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: C3_BonAchate()
    }

    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.vid.toInt() == filterProduitID } ?: _2_1_ProduitsDataBase()
    }

    val idClientFiltruer = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID }?.clientAcheteurID ?: 0L
    }

    val maxPrixArriveDuProduit = remember(tarificationList, filteredProduit) {
        produitAcheteOperationList
            .filter { it.produitAcheterID == filteredProduit.vid }
            .maxOfOrNull { it.provisoireMonPrix }
    }

    val clientDefiniTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter { it.parentIdClient == idClientFiltruer }
    }

    Card {
        Text("produitAcheteOperationList$produitAcheteOperationList")
    }

    val lastOrNull_produitAcheteOperationList = produitAcheteOperationList.lastOrNull { operation ->
        operation.produitAcheterID == filteredProduit.vid &&
                operation.parentIdClient == idClientFiltruer
    }

    val clientLastHistoricalPrice = remember(produitAcheteOperationList, filteredProduit, idClientFiltruer) {
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
        )
    }
}
