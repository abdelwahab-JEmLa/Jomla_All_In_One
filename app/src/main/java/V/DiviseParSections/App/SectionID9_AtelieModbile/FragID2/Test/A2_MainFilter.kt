package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfosT2>,
    bonAchatList: List<BonAchatT2>,
    produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
) {
    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.vid.toInt() == filterProduitID } ?: _2_1_ProduitsDataBase()
    }

    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: BonAchatT2()
    }

    val filteredTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter { tariff ->
            tariff.idProduit == filteredProduit.vid &&
                    tariff.idParentBonAchat == filteredBonAchat.vid
        }
    }
        /*
    Text(
        filteredProduit.nom,
        Modifier.padding(50.dp)
    )          */

    Column(modifier = modifier) {
        MainList(
            tariffs = filteredTariffs,
            showLabels = showLabels
        )
    }
}
