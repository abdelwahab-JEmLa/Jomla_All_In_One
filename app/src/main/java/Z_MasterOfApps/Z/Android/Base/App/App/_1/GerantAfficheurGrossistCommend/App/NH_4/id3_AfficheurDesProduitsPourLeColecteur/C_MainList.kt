package Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_F3(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    // Split products into regular and carton products
    val (regularProducts, cartonProducts) = visibleProducts
        .filter { product ->
            product.bonCommendDeCetteCota
                ?.mutableBasesStates
                ?.cPositionCheyCeGrossit == true
        }
        .partition { !it.statuesBase.seTrouveAuDernieDuCamionCarCCarton }

    // Group regular products by grossist
    val groupedRegularProducts = regularProducts
        .groupBy { product ->
            product.bonCommendDeCetteCota
                ?.grossistInformations
        }
        .filterKeys { it != null }
        .toSortedMap(compareBy { it?.positionInGrossistsList })

    // Sort carton products by grossist position then product position
    val sortedCartonProducts = cartonProducts
        .sortedWith(
            compareBy<_ModelAppsFather.ProduitModel> {
                it.bonCommendDeCetteCota?.grossistInformations?.positionInGrossistsList
            }.thenBy {
                it.bonCommendDeCetteCota?.mutableBasesStates?.positionProduitDonGrossistChoisiPourAcheterCeProduit
            }
        )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Regular products sections
        groupedRegularProducts.forEach { (grossist, products) ->
            stickyHeader {
                val backgroundColor =
                    Color(android.graphics.Color.parseColor(grossist?.couleur ?: "#FFFFFF"))
                val textColor =
                    if (grossist?.couleur?.equals("#FFFFFF", ignoreCase = true) == true) {
                        Color.Black
                    } else {
                        Color.White
                    }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = grossist?.nom ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            items(
                items = products.sortedBy { product ->
                    product.bonCommendDeCetteCota
                        ?.mutableBasesStates
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: Int.MAX_VALUE
                },
            ) { product ->
                MainItem_F3(
                    mainItem = product,
                    viewModelProduits = viewModelProduits,
                    onCLickOnMain = {
                        product.bonCommendDeCetteCota
                            ?.mutableBasesStates
                            ?.cPositionCheyCeGrossit = false
                        updateProduit(product, viewModelProduits)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .animateItem(fadeInSpec = null, fadeOutSpec = null),
                )
            }
        }

        // Carton products section
        if (sortedCartonProducts.isNotEmpty()) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Produits Type: Carton",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            items(
                items = sortedCartonProducts,
            ) { product ->
                MainItem_F3(
                    mainItem = product,
                    viewModelProduits = viewModelProduits,
                    onCLickOnMain = {
                        product.bonCommendDeCetteCota
                            ?.mutableBasesStates
                            ?.cPositionCheyCeGrossit = false
                        updateProduit(product, viewModelProduits)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .animateItem(fadeInSpec = null, fadeOutSpec = null),
                )
            }
        }
    }
}
