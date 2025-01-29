package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.Modules.Dialogs.MoveProductsDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainList_F4(
    visibleProducts: List<_ModelAppsFather.ProduitModel>,
    viewModelProduits: ViewModelInitApp,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    var selectedProducts by remember { mutableStateOf<List<_ModelAppsFather.ProduitModel>>(emptyList()) }
    var currentGrossist by remember { mutableStateOf<_ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?>(null) }
    var showMoveDialog by remember { mutableStateOf(false) }

    val groupedProducts = visibleProducts
        .groupBy { it.bonCommendDeCetteCota?.grossistInformations }
        .filterKeys { it != null }
        .toSortedMap(compareBy { it?.positionInGrossistsList })

    if (showMoveDialog && currentGrossist != null) {
        MoveProductsDialog(
            selectedProducts = selectedProducts,
            currentGrossist = currentGrossist,
            viewModelProduits = viewModelProduits,
            onDismiss = { showMoveDialog = false },
            onProductsMoved = { selectedProducts = emptyList() }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.background(
            Color(0xE3C85858).copy(alpha = 0.1f),
            RoundedCornerShape(8.dp)
        ),
        contentPadding = paddingValues,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedProducts.forEach { (grossist, products) ->
            item(span = { GridItemSpan(3) }) {
                GrossistHeader(
                    grossist = grossist,
                    selectedProductsCount = selectedProducts.size,
                    onMoveClick = {
                        currentGrossist = grossist
                        showMoveDialog = true
                    }
                )
            }

            items(
                items = products.sortedBy {
                    it.bonCommendDeCetteCota?.mutableBasesStates
                        ?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: Int.MAX_VALUE
                }
            ) { product ->
                Box(
                    modifier = Modifier
                        .animateItem()
                        .padding(4.dp)
                ) {
                    MainItem_F4(
                        mainItem = product,
                        onCLickOnMain = {
                            selectedProducts = if (selectedProducts.contains(product)) {
                                selectedProducts - product
                            } else {
                                selectedProducts + product
                            }
                        },
                        position = selectedProducts.indexOf(product).let {
                            if (it >= 0) it + 1 else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (selectedProducts.contains(product))
                                    Color.Yellow.copy(alpha = 0.3f)
                                else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .animateItem(),
                    )
                }
            }
        }
    }
}


