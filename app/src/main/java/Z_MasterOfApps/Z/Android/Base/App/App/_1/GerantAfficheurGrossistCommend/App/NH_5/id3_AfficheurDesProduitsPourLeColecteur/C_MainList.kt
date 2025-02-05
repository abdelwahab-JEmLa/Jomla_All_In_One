package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id2_TravaillieurListProduitAchercheChezLeGrossist.D_MainItem.ExpandedMainItem_F2
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.D_MainItem.MainItem_F3
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.LogUtils.logProductFilter
import androidx.compose.animation.AnimatedVisibility
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
    viewModel: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val frag_3A1_ExtVM = viewModel.frag_3A1_ExtVM
    val visibleProducts = frag_3A1_ExtVM.clientFocused?.second

    visibleProducts?.forEach { product ->
        frag_3A1_ExtVM.iDClientAuFilter?.let { clientId ->
            logProductFilter(
                product,
                clientId,
                viewModel._modelAppsFather.grossistsDataBase
            )
        }
    }
    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE3C85858).copy(alpha = 0.1f)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        visibleProducts?.let { products ->
            // Group products by carton packaging and grossist
            val etagerProducts = products.filter {
                !it.statuesBase.characterProduit.emballageCartone
            }
            val cartonsProducts = products.filter {
                it.statuesBase.characterProduit.emballageCartone
            }

            // Etager Section
            if (etagerProducts.isNotEmpty()) {
                val groupedEtagerProducts = viewModel._modelAppsFather.groupedProductsParGrossist
                    .filter { (_, groupProducts) ->
                        groupProducts.any { product ->
                            etagerProducts.contains(product)
                        }
                    }

                item {
                    Text(
                        text = "Etager Section",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                groupedEtagerProducts.forEach { (grossist, grossistProducts) ->
                    val filteredProducts = grossistProducts.filter {
                        etagerProducts.contains(it)
                    }

                    if (filteredProducts.isNotEmpty()) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(android.graphics.Color.parseColor(grossist.statueDeBase.couleur)))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = grossist.nom,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (grossist.statueDeBase.couleur == "#FFFFFF")
                                        Color.Black else Color.White
                                )
                            }
                        }

                        items(filteredProducts) { product ->
                            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                MainItem_F3(
                                    viewModelProduits = viewModel,
                                    mainItem = product,
                                    modifier = Modifier.fillMaxWidth(),
                                    onCLickOnMain = {
                                        expandedItemId = if (expandedItemId == product.id) null
                                        else product.id
                                    }
                                )

                                AnimatedVisibility(
                                    visible = expandedItemId == product.id,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    ExpandedMainItem_F2(
                                        viewModelInitApp = viewModel,
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

            // Cartons Section
            if (cartonsProducts.isNotEmpty()) {
                val groupedCartonsProducts = viewModel._modelAppsFather.groupedProductsParGrossist
                    .filter { (_, groupProducts) ->
                        groupProducts.any { product ->
                            cartonsProducts.contains(product)
                        }
                    }

                item {
                    Text(
                        text = "Cartons Section",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                groupedCartonsProducts.forEach { (grossist, grossistProducts) ->
                    val filteredProducts = grossistProducts.filter {
                        cartonsProducts.contains(it)
                    }

                    if (filteredProducts.isNotEmpty()) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(android.graphics.Color.parseColor(grossist.statueDeBase.couleur)))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = grossist.nom,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (grossist.statueDeBase.couleur == "#FFFFFF")
                                        Color.Black else Color.White
                                )
                            }
                        }

                        items(filteredProducts) { product ->
                            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                MainItem_F3(
                                    viewModelProduits = viewModel,
                                    mainItem = product,
                                    modifier = Modifier.fillMaxWidth(),
                                    onCLickOnMain = {
                                        expandedItemId = if (expandedItemId == product.id) null
                                        else product.id
                                    }
                                )

                                AnimatedVisibility(
                                    visible = expandedItemId == product.id,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    ExpandedMainItem_F2(
                                        viewModelInitApp = viewModel,
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
        }
    }
}
