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
    filterProduiID: Int,
    filterBonID: Long,
) {
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: BonAchatT2()
    }

    val filteredProduit = remember(produitInfosList, filterProduiID) {
        produitInfosList.find { it.vid.toInt() == filterProduiID } ?: _2_1_ProduitsDataBase()
    }

    val filteredTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter { tariff ->
            tariff.idProduit == filteredProduit.vid &&
                    tariff.idParentBonAchat == filteredBonAchat.vid
        }
    }

    Column(modifier = modifier) {
        MainList(
            tariffs = filteredTariffs,
            showLabels = showLabels
        )
    }
}
