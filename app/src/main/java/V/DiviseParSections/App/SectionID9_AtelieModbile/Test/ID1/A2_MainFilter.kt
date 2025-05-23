package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
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
    onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit,
    produitAcheteOperationList: List<_1_2_ProduitAcheteOperation>,
) {
    // Filtrage du produit sélectionné
    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.vid.toInt() == filterProduitID } ?: _2_1_ProduitsDataBase()
    }

    // Filtrage du bon d'achat sélectionné
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: C3_BonAchate()
    }

    // Tarifications pour le produit filtré
    val produitTariffs = remember(tarificationList, filteredProduit) {
        tarificationList.filter { it.idParentProduit == filteredProduit.vid }
    }

    // Tarifications pour le client et produit spécifiques
    val clientProduitTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        produitTariffs.filter { it.parentIdClient == filteredBonAchat.clientAcheteurID }
    }

    // Dernier prix historique du client pour ce produit
    val clientLastHistoricalPrice = remember(produitAcheteOperationList, filteredProduit, filteredBonAchat) {
        produitAcheteOperationList
            .filter { operation ->
                operation.produitAcheterID == filteredProduit.vid &&
                        bonAchatList.any { bon ->
                            bon.vid == operation.parent_1_3_TransactionCommercial &&
                                    bon.clientAcheteurID == filteredBonAchat.clientAcheteurID
                        }
            }
            .maxOfOrNull { it.provisoireMonPrix } ?: 0.0
    }

    Column(modifier = modifier) {
        MainList(
            clientLastHistoricalPrice = clientLastHistoricalPrice,
            clientProduitTariffs = clientProduitTariffs,
            produitTariffs = produitTariffs,
            filteredProduit = filteredProduit,
            showLabels = showLabels,
            onClickPrixButton = onClickPrixButton,
            produitHistoriquesTariffs = tarificationList
                .filter { it.typeTarificationEnumT2Correspond == TypeTarificationEnumT2.Historique }
        )
    }
}
