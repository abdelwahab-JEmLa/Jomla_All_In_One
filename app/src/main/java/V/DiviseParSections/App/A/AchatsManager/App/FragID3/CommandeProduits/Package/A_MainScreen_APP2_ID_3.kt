package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
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
    val activeIdDe_1_5_Vendeur = _0_0_HeadOfRepositorys_Repository.repositorys_Model.activeIdDe_1_5_Vendeur
    val periodFilter = _0_0_HeadOfRepositorys_Repository
        . repositorys_Model.repository_1_5_Vendeur
        .modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }
        ?.ceComptVendeurStartAffichePeriod

    val models = _0_0_HeadOfRepositorys_Repository.repositorys_Model

    // State to hold filtered products
    var displayableProducts by remember { mutableStateOf<List<_1_2_ProduitAcheteOperation>>(emptyList()) }

    // Move heavy computation to LaunchedEffect with Dispatchers.Default
    LaunchedEffect(
        models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList,
        models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList,
        models._1_3_BonAchat_Repository.modelDatasSnapList,
        periodFilter
    ) {
        withContext(Dispatchers.Default) {
            // Create lookup maps for faster access
            val productsById = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .groupBy { it.vid }

            val colorsByProductVid = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .groupBy { it.parentProduitAchateOperationVID }

            val bonAchatsById = models._1_3_BonAchat_Repository.modelDatasSnapList
                .associateBy { it.vid }

            // Group product operations by produitAcheterID for better performance
            val productOperationsByProductId = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .groupBy { it.produitAcheterID }

            // First pass: find all products that meet the basic criteria
            val confirmedProducts = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { product ->
                    // 1. Check CONFIRME status
                    if (product.etateActuellementEst != _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME) {
                        return@filter false
                    }

                    // 2. Check for valid colors
                    val productColors = colorsByProductVid[product.vid] ?: emptyList()
                    val hasValidColors = productColors.any { color ->
                        color.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                && color.totaleQuantity > 0
                    }

                    if (!hasValidColors) {
                        return@filter false
                    }

                    // 3. Period filter
                    val allProductOps = productOperationsByProductId[product.produitAcheterID] ?: emptyList()
                    val bonAchatIds = allProductOps.map { it.parent_1_3_BonAchat }.distinct()

                    val matchesPeriod = bonAchatIds.any { bonAchatId ->
                        val bonAchat = bonAchatsById[bonAchatId]
                        bonAchat?.parentVID_1_4_PeriodeVent == periodFilter
                    }

                    matchesPeriod
                }
                .distinctBy { it.produitAcheterID }

            displayableProducts = confirmedProducts
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn {
            items(displayableProducts) { produit ->
                HorizontalDivider(Modifier.padding(10.dp), thickness = 2.dp)
                B_ProduitCommande(models, produit)
            }
        }
        A_OptionsControlsButtons_A1FragID_3()
    }
}
