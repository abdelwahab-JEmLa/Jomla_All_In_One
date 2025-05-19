package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    tarificationList: List<D_TarificationInfosT2>,
    bonAchatList: List<BonAchatT2>,
    produitInfosList: List<ArticlesBasesStatsTable>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduiID: Int,
    filterBonID: Long,
) {

    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: BonAchatT2()
    }

    val filteredProduit = remember(produitInfosList, filterProduiID) {
        produitInfosList.find { it.idArticle == filterProduiID } ?: ArticlesBasesStatsTable()
    }

    val filteredTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter { tariff ->
            tariff.idProduit.toInt() == filteredProduit.idArticle &&
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
