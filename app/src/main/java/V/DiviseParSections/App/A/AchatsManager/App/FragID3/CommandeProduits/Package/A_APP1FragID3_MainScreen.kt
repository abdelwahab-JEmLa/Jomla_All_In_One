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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject


@Composable
fun A_APP1FragID3_MainScreen(
    modifier: Modifier = Modifier,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    val TAG = "APP2_FragID3_MainScreen"

    // Get the active vendor ID
    val activeIdDe_1_5_Vendeur = _0_0_HeadOfRepositorys_Repository.repositorys_Model.activeIdDe_1_5_Vendeur

    // Get the period filter from the active vendor
    val periodFilter = _0_0_HeadOfRepositorys_Repository
        .repositorys_Model.repository_1_5_Vendeur
        .modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }
        ?.ceComptVendeurStartAffichePeriod

    val models = _0_0_HeadOfRepositorys_Repository.repositorys_Model

    // State to hold filtered products
    var displayableProducts by remember { mutableStateOf<List<_1_2_ProduitAcheteOperation>>(emptyList()) }

    // Debug logging for periodFilter
    LaunchedEffect(periodFilter) {
        Log.d(TAG, "Active period filter: $periodFilter")
    }

    // Add specific logging for product with vid == 127
    LaunchedEffect(models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList) {
        val product127 = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList.find { it.vid == 127L }
        if (product127 != null) {
            Log.d(TAG, "Product 127 found: status=${product127.etateActuellementEst}, produitAcheterID=${product127.produitAcheterID}")

            // Check colors for product 127
            val colorsForProduct127 = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .filter { it.parentProduitAchateOperationVID == 127L }

            Log.d(TAG, "Colors for product 127: ${colorsForProduct127.size}")
            colorsForProduct127.forEach { color ->
                Log.d(TAG, "Color for 127: status=${color.etateActuellementEst}, quantity=${color.totaleQuantity}")
            }

            // Check BonAchat for product 127
            val bonAchatId = product127.parent_1_3_BonAchat
            val bonAchat = models.repository_1_3_TransactionCommercial.modelDatasSnapList.find { it.vid == bonAchatId }
            if (bonAchat != null) {
                Log.d(TAG, "BonAchat for product 127: period=${bonAchat.parentVID_1_4_PeriodeVent}, filter=$periodFilter")
            } else {
                Log.d(TAG, "BonAchat not found for product 127")
            }
        } else {
            Log.d(TAG, "Product with vid=127 not found in modelDatasSnapList")
        }
    }

    // Move heavy computation to LaunchedEffect with Dispatchers.Default
    LaunchedEffect(
        models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList,
        models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList,
        models.repository_1_3_TransactionCommercial.modelDatasSnapList,
        periodFilter
    ) {
        withContext(Dispatchers.Default) {
            // Create lookup maps for faster access
            val colorsByProductVid = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .groupBy { it.parentProduitAchateOperationVID }

            val bonAchatsById = models.repository_1_3_TransactionCommercial.modelDatasSnapList
                .associateBy { it.vid }

            // Debug logging for period filtering
            val bonAchatsPeriods = models.repository_1_3_TransactionCommercial.modelDatasSnapList
                .map { "${it.vid}: ${it.parentVID_1_4_PeriodeVent}" }
            Log.d(TAG, "BonAchats with periods: $bonAchatsPeriods")

            // First pass: find all products that meet the criteria
            val confirmedProducts = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { product ->
                    // Add specific logging for product 127 in the filtering process
                    val isProduct127 = product.vid == 127L
                    if (isProduct127) {
                        Log.d(TAG, "Filtering product 127...")
                    }

                    // 1. Check CONFIRME status
                    val hasConfirmedStatus = product.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                    if (isProduct127) {
                        Log.d(TAG, "Product 127 CONFIRME status check: $hasConfirmedStatus")
                    }

                    if (!hasConfirmedStatus) {
                        return@filter false
                    }

                    // 2. Check for valid colors with quantity > 0
                    val productColors = colorsByProductVid[product.vid] ?: emptyList()
                    val hasValidColors = productColors.any { color ->
                        color.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                && color.totaleQuantity > 0
                    }

                    if (isProduct127) {
                        Log.d(TAG, "Product 127 colors check: found ${productColors.size} colors, hasValidColors=$hasValidColors")
                        productColors.forEach {
                            Log.d(TAG, "Product 127 color: status=${it.etateActuellementEst}, quantity=${it.totaleQuantity}")
                        }
                    }

                    if (!hasValidColors) {
                        return@filter false
                    }

                    // 3. Period filter - only if a period filter is set
                    if (periodFilter != null) {
                        val bonAchat = bonAchatsById[product.parent_1_3_BonAchat]
                        val productPeriod = bonAchat?.parentVID_1_4_PeriodeVent

                        // Debug logging for this specific product's period
                        if (isProduct127) {
                            Log.d(TAG, "Product 127 period check: productPeriod=$productPeriod, filter=$periodFilter")
                        }

                        // Check if ANY associated BonAchats match the period filter (fixed logic)
                        val allBonAchats = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                            .filter { it.produitAcheterID == product.produitAcheterID }
                            .map { it.parent_1_3_BonAchat }
                            .distinct()

                        // Get all periods for these BonAchats
                        val allPeriods = allBonAchats.mapNotNull { bonAchatId ->
                            bonAchatsById[bonAchatId]?.parentVID_1_4_PeriodeVent
                        }.distinct()

                        // FIXED: Changed from allPeriodsMatch to anyPeriodMatches
                        // Only include products where ANY associated periods match the filter
                        val anyPeriodMatches = allPeriods.any { it == periodFilter }

                        if (isProduct127) {
                            Log.d(TAG, "Product 127 (ID: ${product.produitAcheterID}) - allBonAchats: $allBonAchats")
                            Log.d(TAG, "Product 127 - allPeriods: $allPeriods, anyPeriodMatches: $anyPeriodMatches")
                        }

                        return@filter anyPeriodMatches
                    }

                    // If no period filter is set, include the product
                    true
                }
                .distinctBy { it.produitAcheterID }

            // Check if product 127 is in the final list
            val product127InFinalList = confirmedProducts.any { it.vid == 127L }
            Log.d(TAG, "Product 127 in final displayable list: $product127InFinalList")

            displayableProducts = confirmedProducts

            // Log the final filtered products count
            Log.d(TAG, "Final displayable products count: ${confirmedProducts.size}")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn {
            items(displayableProducts) { produit ->
                B_ProduitCommande(models, produit)
            }
        }

    }
}
