package Application2.App.Fragment

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Item_Produit_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Etager_LazyColumn_App2(
    modifier: Modifier = Modifier,
    productWithColors: List<Pair<M01Produit, List<M3CouleurProduitInfos>>>,
    viewModel: ViewModel_MainFragment
) {
    val gridState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    val wifiState by viewModel.wifiState.collectAsState()
    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition
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

        val foundIndex = productWithColors.indexOfFirst { (product, _) ->
            product.keyID == targetProductKeyID
        }
        if (foundIndex >= 0) {
            delay(100)
            coroutineScope.launch { gridState.animateScrollToItem(foundIndex) }
        }
    }

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
                set(value = expanded_M3CouleurProduitInfos, key = SemanticsPropertyKey("expanded_M3CouleurProduitInfos"))
                set(value = expanded_M1Produit, key = SemanticsPropertyKey("expanded_M1Produit"))
                set(value = currentScrollPosition, key = SemanticsPropertyKey("currentScrollPosition"))
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled,
    ) {
        productWithColors.forEach { (product, colors) ->
            val isExpanded = activeCentralValues.expanded_M1Produit?.keyID == product.keyID
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

@Composable
fun LazyStigerList_Produits_App2(
    productColorPairs: Pair<M01Produit, List<M3CouleurProduitInfos>>,
) {
    Box { Item_Produit_AppEcranPresntoireJemlaCom(productColorPairs) }
}
