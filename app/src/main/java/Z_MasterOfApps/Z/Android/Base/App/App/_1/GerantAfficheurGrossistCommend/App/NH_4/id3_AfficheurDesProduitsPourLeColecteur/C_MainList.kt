package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur

import Z_MasterOfApps.Kotlin.Model.Extension.groupedProductsParClients
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.D_MainItem.ExpandedMainItem_F2
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.ExpandedMainItem_F3
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.MainItem_F3
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainList_F3(
    viewModelInitApp: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val extensionvmapp1fragmentid3 = viewModelInitApp.extensionVMApp1FragmentId_3

    val visibleProducts = viewModelInitApp._modelAppsFather
        .groupedProductsParClients.find {
            it.key.id == extensionvmapp1fragmentid3.clientIDAuFilter
        }
        ?.value.orEmpty()

    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    // Split products into regular and carton products
    val (etagersProduits, cartonsSectionProsduits) = visibleProducts
        .filter { product ->
            product.bonCommendDeCetteCota?.mutableBasesStates?.cPositionCheyCeGrossit == true
        }
        .partition { !it.statuesBase.seTrouveAuDernieDuCamionCarCCarton }

    val groupedRegularProducts = etagersProduits
        .groupBy { product ->
            product.bonCommendDeCetteCota
                ?.idGrossistChoisi
        }
        .filterKeys { it != null }
        .toSortedMap(compareBy { grossistId ->
            // Find the grossist in the database and get its position
            viewModelInitApp._modelAppsFather.grossistsDataBase
                .find { it.id == grossistId }
                ?.statueDeBase
                ?.itPositionInParentList
                ?: Int.MAX_VALUE
        })

    // Sort carton products by grossist position then product position
    val sortedCartonProducts = cartonsSectionProsduits
        .sortedWith(
            compareBy<_ModelAppsFather.ProduitModel> { product ->
                product.bonCommendDeCetteCota?.idGrossistChoisi?.let { grossistId ->
                    viewModelInitApp._modelAppsFather.grossistsDataBase
                        .find { it.id == grossistId }
                        ?.statueDeBase
                        ?.itPositionInParentList
                } ?: Int.MAX_VALUE
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
        groupedRegularProducts.forEach { (grossistId, products) ->
            stickyHeader {
                val grossist = viewModelInitApp._modelAppsFather.grossistsDataBase
                    .find { it.id == grossistId }

                val backgroundColor = Color(
                    android.graphics.Color.parseColor(
                        grossist?.statueDeBase?.couleur ?: "#FFFFFF"
                    )
                )
                val textColor = if (grossist?.statueDeBase?.couleur == "#FFFFFF") {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    MainItem_F3(
                        viewModelProduits = viewModelInitApp,
                        mainItem = product,
                        modifier = Modifier.fillMaxWidth(),
                        onCLickOnMain = {
                            expandedItemId = if (expandedItemId == product.id)
                                null else product.id
                        }
                    )

                    AnimatedVisibility(
                        visible = expandedItemId == product.id,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = 0.9f,
                                stiffness = 300f
                            )
                        ),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = 0.9f,
                                stiffness = 300f
                            )
                        )
                    ) {
                        ExpandedMainItem_F2(
                            viewModelInitApp = viewModelInitApp,
                            mainItem = product,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            onCLickOnMain = { expandedItemId = null }
                        )
                    }
                }
            }
        }

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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    MainItem_F3(
                        viewModelProduits = viewModelInitApp,
                        mainItem = product,
                        modifier = Modifier.fillMaxWidth(),
                        onCLickOnMain = {
                            expandedItemId = if (expandedItemId == product.id)
                                null else product.id
                        }
                    )

                    AnimatedVisibility(
                        visible = expandedItemId == product.id,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = 0.9f,
                                stiffness = 300f
                            )
                        ),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = 0.9f,
                                stiffness = 300f
                            )
                        )
                    ) {
                        ExpandedMainItem_F3(
                            viewModelInitApp = viewModelInitApp,
                            mainItem = product,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            onCLickOnMain = { expandedItemId = null }
                        )
                    }
                }
            }
        }
    }
}
