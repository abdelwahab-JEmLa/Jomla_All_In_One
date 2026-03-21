package Application2.App.Fragment

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.Fragment.Z.Components.CategoryStickyHeader
import Application2.App.View.Pro0.Proto.Item_Produit_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun Etager_LazyColumn_App2(
    modifier: Modifier = Modifier,
    cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    viewModel: ViewModel_MainFragment = koinViewModel(),
) {
    val gridState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    // Read connection state from the single source of truth
    val wifiState by viewModel.wifiState.collectAsState()
    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition

    // Scroll is only user-driven on the host, or when not connected at all
    val isScrollEnabled = isHostPhone || !isConnected

    val uiState by viewModel.uiState.collectAsState()
    val activeCentralValues = uiState.active_Central_Values

    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos
    val expanded_M1Produit = wifiState.expanded_M1Produit

    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect

        val targetProductKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetProductKeyID.isBlank()) return@LaunchedEffect

        var currentIndex = 0
        var foundIndex = -1

        outer@ for ((catalogue, categoriesWithProducts) in cataloguesWithCategoriesAndProducts) {
            currentIndex++ // catalogue_header_{catalogue.id}

            for ((category, productColorPairs) in categoriesWithProducts) {
                if (category.displayedHeader) currentIndex++ // category_header_{category.id}

                val productIndex = productColorPairs.indexOfFirst { (product, _) ->
                    product.keyID == targetProductKeyID
                }
                if (productIndex != -1) {
                    foundIndex = currentIndex + productIndex
                    break@outer
                }
                currentIndex += productColorPairs.size
            }
        }

        if (foundIndex >= 0) {
            delay(100)
            coroutineScope.launch { gridState.animateScrollToItem(foundIndex) }
        }
    }

    // HOST → broadcasts scroll position to the connected client
    HandlePresenterScrollBroadcast_app2(
        isHostPhone = isHostPhone,
        isConnected = isConnected,
        gridState = gridState,
        viewModel = viewModel,
    )

    // CLIENT → receives scroll position from host and applies it
    HandlePresenterClientScroll_app2(
        isHostPhone = isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = expanded_M3CouleurProduitInfos,
                    key = SemanticsPropertyKey("expanded_M3CouleurProduitInfos")
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = expanded_M1Produit, key = SemanticsPropertyKey(
                        "expanded_M1Produit"
                    )
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = currentScrollPosition,
                    key = SemanticsPropertyKey("currentScrollPosition")
                )
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled,
    ) {
        cataloguesWithCategoriesAndProducts.forEach { (catalogue, categoriesWithProducts) ->
            item(key = "catalogue_header_${catalogue.id}", span = StaggeredGridItemSpan.FullLine) {
                CatalogueHeader(catalogue = catalogue)
            }

            categoriesWithProducts.forEach { (category, productColorPairs) ->
                if (category.displayedHeader) {
                    item(key = "category_header_${category.id}", span = StaggeredGridItemSpan.FullLine) {
                        CategoryStickyHeader(
                            category = category,
                            onToggleHeaderVisibility = { /* update repo if needed */ }
                        )
                    }
                }

                productColorPairs.forEach { (product, colors) ->
                    val isExpanded = activeCentralValues
                        .expanded_M1Produit?.keyID == product.keyID

                    item(
                        key = "product_${product.keyID}",
                        span = if (isExpanded) StaggeredGridItemSpan.FullLine
                        else StaggeredGridItemSpan.SingleLane,
                    ) {
                        LazyStigerList_Produits_App2(product to colors)
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogueHeader(catalogue: M21CataloguesCategorie, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(catalogue.couleur.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "📚 ${catalogue.nom}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = catalogue.couleur
        )
    }
}

@Composable
fun LazyStigerList_Produits_App2(
    productColorPairs: Pair<M01Produit, List<M3CouleurProduitInfos>>,
) {
    Box { Item_Produit_AppEcranPresntoireJemlaCom(productColorPairs) }
}
