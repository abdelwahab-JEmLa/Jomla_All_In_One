package com.example.clientjetpack.App2.App.B.Fragment

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.App.ViewModel.ViewModel_MainFragment
import com.example.clientjetpack.App2.App.B.Fragment.Z.Components.CategoryStickyHeader
import com.example.clientjetpack.App2.App.View.Pro0.Proto.Item_Produit_AppEcranPresntoireJemlaCom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun Etager_LazyColumn_App2(
    modifier: Modifier = Modifier,
    cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>,
    viewModel: ViewModel_MainFragment = koinViewModel(),
    focusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
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

    val expanded_M3CouleurProduitInfos =
        focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos

    // When an item is expanded on the HOST, auto-scroll to it
    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect

        var currentIndex = 1 // account for ad_banner_header at index 0
        var foundIndex = -1

        outer@ for ((_, categoriesWithProducts) in cataloguesWithCategoriesAndProducts) {
            currentIndex++ // catalogue header

            for ((category, productColorPairs) in categoriesWithProducts) {
                if (category.displayedHeader) currentIndex++ // category header

                val productIndex = productColorPairs.indexOfFirst { (product, _) ->
                    product.id == expanded_M3CouleurProduitInfos.parentBProduitOldID
                }
                if (productIndex != -1) {
                    foundIndex = currentIndex + productIndex
                    break@outer
                }
                currentIndex += productColorPairs.size
            }
        }

        if (foundIndex != -1) {
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
                    val isExpanded = focusedValuesGetter_app2.active_Central_Values
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
    productColorPairs: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>,
) {
    Box { Item_Produit_AppEcranPresntoireJemlaCom(productColorPairs) }
}
