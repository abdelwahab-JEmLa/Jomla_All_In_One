// B_ProduitCommande.kt
package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun B_ProduitCommande(
    models: GroupeRepositorysProtoAvJuin3Model,
    Produit: _1_2_ProduitAcheteOperation,
) {
    if (Produit.etateActuellementEst != _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME) {
        return
    }

    val activeIdDe_1_5_Vendeur = models.activeIdDeA5Vendeur

    val periodFilter = remember(activeIdDe_1_5_Vendeur) {
        models.repository_1_5_Vendeur
            .modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurStartAffichePeriod
    }

    val bonAchatPeriods = remember {
        models.c3TransactionCommercialRepository.modelDatasSnapList
            .associate { it.vid to it.parentPeriodeVentOldID }
    }

    val allProductInstances = remember(Produit.produitAcheterID) {
        models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
            .filter {
                it.produitAcheterID == Produit.produitAcheterID &&
                        it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            }
    }

    val relevantProductInstances = remember(allProductInstances, periodFilter) {
        if (periodFilter != null) {
            allProductInstances.filter { product ->
                val bonAchatPeriod = bonAchatPeriods[product.parent_1_3_TransactionCommercial]
                bonAchatPeriod == periodFilter
            }
        } else {
            allProductInstances
        }
    }

    val filteredProductVids = relevantProductInstances.map { it.vid }

    val colorsForProduct = remember(filteredProductVids) {
        models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
            .filter {
                it.parentProduitAchateOperationVID in filteredProductVids &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }
    }

    val filteredColors = colorsForProduct
        .filter { it.totaleQuantity > 0 }
        .distinctBy { it.couleurIndex_ParentVID }

    if (filteredColors.isEmpty()) {
        return
    }

    val buyerIds = remember {
        val bonAchatIds = relevantProductInstances.map { it.parent_1_3_TransactionCommercial }.distinct()

        models.c3TransactionCommercialRepository.modelDatasSnapList
            .filter { it.vid in bonAchatIds }
            .map { it.parentHClientOldID }
            .distinct()
    }

    Card() {
        HorizontalDivider(Modifier.height(20.dp), thickness = 5.dp,color= Color.Red)

        Column {
            Text(
                models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
                    .find { it.vid == Produit.produitAcheterID }?.nom
                    ?: "_015_Produits inconnu", Modifier.padding(4.dp)
            )

            LazyListCouleurVendu(
                Produit = Produit,
                colorsForProduct = colorsForProduct,
                models = models,
                periodFilter = periodFilter
            )
        }
    }
}
