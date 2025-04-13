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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun A_APP1FragID3_MainScreen(
    modifier: Modifier = Modifier,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    val models = _0_0_HeadOfRepositorys_Repository.repositorys_Model

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

            // Log confirmed products for debugging
            Log.d("ProductDebug", "Confirmed products: $confirmedProducts")

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

            // Log products with quantity chosen colors for debugging
            Log.d("ProductDebug", "Products with quantity chosen colors: $productsWithQuantityChosenColors")

            // Check specifically for product ID 849
            if (849L in confirmedProducts) {
                Log.d("ProductDebug", "Product 849 found in confirmedProducts")
            }

            if (849L in productsWithQuantityChosenColors) {
                Log.d("ProductDebug", "Product 849 found in productsWithQuantityChosenColors")

                // Find which colors are causing product 849 to be included
                val colorsForProduct849 = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                    .filter {
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                    }
                    .filter { color ->
                        val parentProduct = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                            .firstOrNull { product -> product.vid == color.parentProduitAchateOperationVID }

                        parentProduct?.produitAcheterID == 1039L
                    }

                Log.d("ProductDebug", "Colors for product 849: ${colorsForProduct849.map {
                    "colorVID=${it.vid}, parentVID=${it.parentProduitAchateOperationVID}, " +
                            "colorIndex=${it.couleurIndex_ParentVID}, quantity=${it.totaleQuantity}"
                }}")
            }

            // Combine both sets to get all products that should be displayed
            val productsToShow = (confirmedProducts + productsWithQuantityChosenColors).toList()

            // Log final list of products to show
            Log.d("ProductDebug", "Final products to show: $productsToShow")

            // Get the full product objects to display
            val filteredProducts = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { it.produitAcheterID in productsToShow }
                .distinctBy { it.produitAcheterID }

            items(filteredProducts) { Produit ->
                HorizontalDivider(Modifier.padding(10.dp), thickness = 2.dp)
                ProduitCommande(models, Produit)
            }
        }
    }
}
