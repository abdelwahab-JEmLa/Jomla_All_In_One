// A_MainScreen_APP2_ID_3.kt
package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

private const val TAG = "MainScreenFiltering"

@Composable
fun A_APP1FragID3_MainScreen(
    modifier: Modifier = Modifier,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    var filterProduitsDonBonAchatesOuIlSontDonCettePeriod by remember {
        mutableStateOf(2L)
    }

    val models = _0_0_HeadOfRepositorys_Repository.repositorys_Model

    // Debug: List all BonAchats with their periods
    models._1_3_BonAchat_Repository.modelDatasSnapList.forEach { bonAchat ->
        Log.d(TAG, "BonAchat ID=${bonAchat.vid}, PeriodID=${bonAchat.parentVID_1_4_PeriodeVent}")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn {
            // Get all products with CONFIRME status
            val confirmedProducts = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter {
                    it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                }
                .map { it.produitAcheterID }
                .toSet()

            Log.d(TAG, "Confirmed products count: ${confirmedProducts.size}")

            // Get all products that have colors with QUANTITY_CHOISI status
            val productsWithQuantityChosenColors = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .filter {
                    it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                }
                .mapNotNull { color ->
                    // Find the parent product for this color
                    val parentProduct = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                        .firstOrNull { product -> product.vid == color.parentProduitAchateOperationVID }

                    // Return the product ID if found
                    parentProduct?.produitAcheterID
                }
                .toSet()

            Log.d(TAG, "Products with QUANTITY_CHOISI colors count: ${productsWithQuantityChosenColors.size}")

            // Combine both sets to get all products that should be displayed
            val productsToShow = (confirmedProducts + productsWithQuantityChosenColors).toList()

            // Get the full product objects to display
            val filteredProducts = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { it.produitAcheterID in productsToShow }
                .distinctBy { it.produitAcheterID }

            Log.d(TAG, "Initial filtered products count: ${filteredProducts.size}")

            // DEBUG: List all products with BonAchat whose period ID matches the filter
            val productsWithTargetPeriod = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { product ->
                    // Find the BonAchat for this product
                    val bonAchats = models._1_3_BonAchat_Repository.modelDatasSnapList
                        .filter { it.vid == product.parent_1_3_BonAchat }

                    // Log all found BonAchats for this product
                    bonAchats.forEach { bonAchat ->
                        Log.d(TAG, "Product ${product.produitAcheterID} linked to BonAchat ID=${bonAchat.vid}, " +
                                "with PeriodID=${bonAchat.parentVID_1_4_PeriodeVent}")
                    }

                    // Check if any of the found BonAchats have the target period
                    val hasTargetPeriod = bonAchats.any {
                        it.parentVID_1_4_PeriodeVent == filterProduitsDonBonAchatesOuIlSontDonCettePeriod
                    }

                    if (hasTargetPeriod) {
                        Log.d(TAG, "Found product with target period: ProductID=${product.produitAcheterID}, " +
                                "BonAchatID=${product.parent_1_3_BonAchat}, " +
                                "State=${product.etateActuellementEst}")
                    }

                    hasTargetPeriod
                }

            Log.d(TAG, "Total products with target period: ${productsWithTargetPeriod.size}")

            val displayableProducts = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { product ->
                    // Log starting filter check for this product
                    Log.d(TAG, "Checking product ${product.produitAcheterID} (ProdOp ID: ${product.vid})")

                    // 1. Check if the product has CONFIRME status
                    val hasConfirmedStatus = product.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                    if (!hasConfirmedStatus) {
                        Log.d(TAG, "Product ${product.produitAcheterID} rejected: Not CONFIRME status")
                        return@filter false
                    }

                    // 2. Check if there are colors with QUANTITY_CHOISI status and quantity > 0
                    val colorsForProduct = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                        .filter { color ->
                            // Find all products associated with this color
                            val parentProduct = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                                .firstOrNull { prod -> prod.vid == color.parentProduitAchateOperationVID }

                            // Check color criteria
                            val colorMatches = parentProduct?.produitAcheterID == product.produitAcheterID &&
                                    color.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI &&
                                    color.totaleQuantity > 0

                            colorMatches
                        }

                    val hasValidColors = colorsForProduct.isNotEmpty()
                    if (!hasValidColors) {
                        Log.d(TAG, "Product ${product.produitAcheterID} rejected: No valid colors")
                        return@filter false
                    }

                    // 3. Apply period filter - IMPORTANT: Check ALL BonAchats associated with this product
                    var matchesPeriodFilter = false

                    // This is the key change: Find ALL BonAchats for this product and check if ANY match the filter
                    val bonAchats = models._1_3_BonAchat_Repository.modelDatasSnapList
                        .filter { it.vid == product.parent_1_3_BonAchat }

                    if (bonAchats.isEmpty()) {
                        Log.d(TAG, "Product ${product.produitAcheterID} has no BonAchat")
                    } else {
                        // Log each BonAchat's period
                        bonAchats.forEach { bonAchat ->
                            Log.d(TAG, "Product ${product.produitAcheterID} linked to BonAchat ID=${bonAchat.vid} " +
                                    "with PeriodID=${bonAchat.parentVID_1_4_PeriodeVent}, " +
                                    "Filter=$filterProduitsDonBonAchatesOuIlSontDonCettePeriod")

                            // Check if ANY BonAchat belongs to the specified period
                            if (bonAchat.parentVID_1_4_PeriodeVent == filterProduitsDonBonAchatesOuIlSontDonCettePeriod) {
                                matchesPeriodFilter = true
                            }
                        }
                    }

                    if (!matchesPeriodFilter) {
                        Log.d(TAG, "Product ${product.produitAcheterID} rejected: No BonAchat with matching period")
                        return@filter false
                    }

                    // Product passed all filters
                    Log.d(TAG, "Product ${product.produitAcheterID} WILL be displayed")
                    true
                }
            Log.d(TAG, "Final displayable products count: ${displayableProducts.size}")

            items(displayableProducts) { produit ->
                // Divider is only shown for products that will actually be displayed
                HorizontalDivider(Modifier.padding(10.dp), thickness = 2.dp)
                B_ProduitCommande(models, produit)
            }
        }
    }
}
