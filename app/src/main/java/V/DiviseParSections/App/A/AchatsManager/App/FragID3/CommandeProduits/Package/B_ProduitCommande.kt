// B_ProduitCommande.kt
package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import android.util.Log
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
    val TAG = "B_ProduitCommande"

    // Verify that this product has CONFIRME status before proceeding
    if (Produit.etateActuellementEst != _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME) {
        return
    }

    // Get the current period filter
    val activeIdDe_1_5_Vendeur = models.activeIdDeA5Vendeur

    // Derive the period filter as a remembered value
    val periodFilter = remember(activeIdDe_1_5_Vendeur) {
        models.repository_1_5_Vendeur
            .modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurStartAffichePeriod
    }

    // Create a map of BonAchat IDs to their periods
    val bonAchatPeriods = remember {
        models.c3TransactionCommercialRepository.modelDatasSnapList
            .associate { it.vid to it.parentVID_1_4_PeriodeVent }
    }

    // Find all product instances with the same produitAcheterID (same base product)
    val allProductInstances = remember(Produit.produitAcheterID) {
        models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
            .filter {
                it.produitAcheterID == Produit.produitAcheterID &&
                        it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            }
    }

    // If period filter is active, filter product instances by period
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

    // Log information about product filtering if it's product 127
    if (Produit.vid == 127L) {
        Log.d(TAG, "Product 127: Found ${allProductInstances.size} instances, ${relevantProductInstances.size} match period $periodFilter")
    }

    // Get the product IDs that match our period filter
    val filteredProductVids = relevantProductInstances.map { it.vid }

    // Get colors for the filtered product instances
    val colorsForProduct = remember(filteredProductVids) {
        models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
            .filter {
                // Check if color belongs to a relevant product instance
                it.parentProduitAchateOperationVID in filteredProductVids &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }
    }

    // Only proceed if there are available colors with quantity > 0
    val filteredColors = colorsForProduct
        .filter { it.totaleQuantity > 0 }
        .distinctBy { it.couleurIndex_ParentVID }

    if (filteredColors.isEmpty()) {
        if (Produit.vid == 127L) {
            Log.d(TAG, "Product 127: No valid colors found after filtering")
        }
        return
    }

    if (Produit.vid == 127L) {
        Log.d(TAG, "Product 127: Displaying with ${filteredColors.size} colors")
        filteredColors.forEach {
            Log.d(TAG, "Color index: ${it.couleurIndex_ParentVID}, quantity: ${it.totaleQuantity}")
        }
    }

    val buyerIds = remember {
        // Find all BonAchat IDs associated with filtered product instances
        val bonAchatIds = relevantProductInstances.map { it.parent_1_3_TransactionCommercial }.distinct()

        // Get all client IDs from those BonAchat entries
        models.c3TransactionCommercialRepository.modelDatasSnapList
            .filter { it.vid in bonAchatIds }
            .map { it.clientAcheteurID }
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

            Couleurs(
                Produit = Produit,
                colorsForProduct = colorsForProduct,
                buyerIds = buyerIds,
                models = models,
                periodFilter = periodFilter
            )
        }
    }
}
