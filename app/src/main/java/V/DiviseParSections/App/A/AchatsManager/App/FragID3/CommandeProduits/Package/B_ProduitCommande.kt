package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun B_ProduitCommande(
    models: _0_0_HeadOfRepositorys_Model,
    Produit: _1_2_ProduitAcheteOperation,
) {
    // Verify that this product has CONFIRME status before proceeding
    if (Produit.etateActuellementEst != _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME) {
        return
    }

    val productFromDB = models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
        .find { it.vid == Produit.produitAcheterID }

    // Get only colors with QUANTITY_CHOISI status for this specific product
    val colorsForProduct =
        models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
            .filter {
                // Find the parent product vid that this color belongs to
                val parentProduct =
                    models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                        .firstOrNull { prod -> prod.vid == it.parentProduitAchateOperationVID }

                // Check if this color belongs to a product with the same ID as our current product
                // AND ensure the color has QUANTITY_CHOISI status
                parentProduct?.produitAcheterID == Produit.produitAcheterID &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }

    // Only proceed if there are available colors with quantity > 0
    val filteredColors = colorsForProduct
        .filter { it.totaleQuantity > 0 }
        .distinctBy { it.couleurIndex_ParentVID }

    if (filteredColors.isEmpty()) {
        return
    }

    val buyerIds = remember {
        // Find all BonAchat IDs associated with this product
        val bonAchatIds =
            models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter {
                    // Only include products with CONFIRME status and matching produitAcheterID
                    it.produitAcheterID == Produit.produitAcheterID &&
                            it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                }
                .map { it.parent_1_3_BonAchat }
                .distinct()

        // Get all client IDs from those BonAchat entries
        models._1_3_BonAchat_Repository.modelDatasSnapList
            .filter { it.vid in bonAchatIds }
            .map { it.clientAcheteurID }
            .distinct()
    }

    Card() {
        Column {
            Text(
                models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
                    .find { it.vid == Produit.produitAcheterID }?.nom
                    ?: "Produit inconnu", Modifier.padding(4.dp)
            )

            Couleurs(Produit, colorsForProduct, buyerIds, models)
        }
    }
}
