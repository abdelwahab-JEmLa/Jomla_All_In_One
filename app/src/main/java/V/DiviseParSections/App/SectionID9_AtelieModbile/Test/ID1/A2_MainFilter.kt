package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
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
    produitInfosList: SnapshotStateList<_2_1_ProduitsDataBase>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: () -> () -> (TypeTarificationEnumT2, D_TarificationInfos, Context) -> () -> Unit,
) {
    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.vid.toInt() == filterProduitID } ?: _2_1_ProduitsDataBase()
    }

    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: C3_BonAchate()
    }

    val filteredTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter { tariff ->
            tariff.idParentProduit == filteredProduit.vid &&
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
            showLabels = showLabels,
            onClickPrixButton = onClickPrixButton(),
        )
    }
}
