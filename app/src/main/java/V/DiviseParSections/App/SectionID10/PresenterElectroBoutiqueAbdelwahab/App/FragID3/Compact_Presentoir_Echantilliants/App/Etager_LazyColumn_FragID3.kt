package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.App

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.ViewModel.UiState
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

fun get_isWifiClientConnected_by_head_vm(uiState: UiState): Boolean =
    !uiState.productDisplayController.isHostPhone &&  uiState.productDisplayController.isConnected

@Composable
fun Etager_LazyColumn_FragID3(
    modifier: Modifier = Modifier.Companion,
    categoriesWithProducts: List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
    catalogues: List<M21CataloguesCategorie>,
    categoryMap: Map<Long, M16CategorieProduit>,
    onProductCategoryClick: (M01Produit) -> Unit,

    isWifiClientConnected_1: Boolean
) {
    val gridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(4),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        categoriesWithProducts.forEach { (category, productColorPairs) ->
            productColorPairs.forEach { (product, colors) ->
                val isExpanded = focusedValuesGetter.active_Central_Values
                    .expanded_M1Produit?.keyID == product.keyID

                item(
                    key = "product_${product.keyID}",
                    span = if (isExpanded) {
                        StaggeredGridItemSpan.Companion.FullLine
                    } else {
                        StaggeredGridItemSpan.Companion.SingleLane
                    }
                ) {
                    ProductItemWithCategory(
                        isWifiClientConnected_1 = isWifiClientConnected_1,
                        product = product,
                        colors = colors,
                        categoryMap = categoryMap,
                        catalogues = catalogues,
                        onProductCategoryClick = onProductCategoryClick,
                    )
                }
            }
        }

        item(
            key = "navigation_button",
            span = StaggeredGridItemSpan.Companion.FullLine
        ) {
            Box(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Companion.Center
            ) {
                Button(
                    onClick = {
                        fragmentNavigationHandler.navigateTo(
                            Screen.Compact_Presentoire_App_Produits_FragID5,
                            FragmentNavigationHandler.Companion.DEFAULT_CONFIG
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.Companion
                        .fillMaxWidth(0.8f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "View All Products",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItemWithCategory(
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    categoryMap: Map<Long, M16CategorieProduit>,
    catalogues: List<M21CataloguesCategorie>,
    onProductCategoryClick: (M01Produit) -> Unit,

    isWifiClientConnected_1: Boolean
) {
    val currentCategory = remember(product.idParentCategorie, categoryMap) {
        product.idParentCategorie?.let { categoryMap[it] }
    }

    val currentCatalogue = remember(currentCategory, catalogues) {
        currentCategory?.catalogueParentId?.let { catalogueId ->
            catalogues.find { it.id.toLong() == catalogueId }
        }
    }

    LazyStigerList_Produits_FragID3(
        isWifiClientConnected_1=isWifiClientConnected_1,
        product = product,
        colors = colors,

        onCategoryClick = {
            onProductCategoryClick(product)
        }
    )
}

@Composable
fun LazyStigerList_Produits_FragID3(
    modifier: Modifier = Modifier.Companion,
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),

    onCategoryClick: (() -> Unit)? = null,
    isWifiClientConnected_1: Boolean
) {
    val isExpanded = focusedValuesGetter.active_Central_Values
        .expanded_M1Produit?.keyID == product.keyID

   /* Item_Produit_FragID3(
        relative_M1produit = product,

        modifier = modifier,
        onCategoryClick = onCategoryClick,
        isWifiClientConnected_1=isWifiClientConnected_1,
        ,
    )       */
}
