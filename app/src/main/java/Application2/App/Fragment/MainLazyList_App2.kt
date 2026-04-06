package Application2.App.Fragment

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Item_Produit_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DBG_TAG_GRID    = "TargetedM3_Affichage"
private const val DBG_M3_KEY      = "-OWDMIC_UdVXmSNw-Dz0"
private const val DBG_M3_PARENT   = "-OV3rmZ-9sy3P5rnINL3"

@Composable
fun MainLazyList_App2(
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

    // ── Sort by parentProduit_Classement so lazyIndex == classement position ──
    // productWithColors arrives from the ViewModel in raw DB order (lazyIndex=37
    // for the targeted product). Sorting by the classement value stored on each
    // M3CouleurProduitInfos aligns lazy positions with the ordering App0 computed
    // and persisted. Products whose colors have no classement fall to the end.
    val sortedProductWithColors = remember(productWithColors) {
        val rawOrder = productWithColors.withIndex()
            .associate { (i, pair) -> pair.first.keyID to i }   // keep for debug

        val sorted = productWithColors.sortedBy { (_, colors) ->
            colors.minOfOrNull { it.parentProduit_Classement ?: Int.MAX_VALUE }
                ?: Int.MAX_VALUE
        }

        // ── DEBUG: log the re-ordering of the targeted product ────────────────
        val beforeIndex = rawOrder[DBG_M3_PARENT]
        val afterIndex  = sorted.indexOfFirst { (p, _) -> p.keyID == DBG_M3_PARENT }
        if (beforeIndex != null && afterIndex >= 0 && beforeIndex != afterIndex) {
            Log.d(DBG_TAG_GRID,
                "[Sort] productKeyID=$DBG_M3_PARENT" +
                        " | rawIndex=$beforeIndex → sortedIndex=$afterIndex" +
                        " | parentProduit_Classement=${
                            sorted[afterIndex].second
                                .minOfOrNull { it.parentProduit_Classement ?: Int.MAX_VALUE }
                        }")
        }

        sorted
    }

    LaunchedEffect(expanded_M1Produit) {
        expanded_M1Produit ?: return@LaunchedEffect
        val targetKeyID = expanded_M1Produit.keyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        val foundIndex = sortedProductWithColors.indexOfFirst { (product, _) ->
            product.keyID == targetKeyID
        }
        if (foundIndex < 0) return@LaunchedEffect

        coroutineScope.launch { gridState.scrollToItem(foundIndex) }

        delay(300)
        coroutineScope.launch {
            gridState.animateScrollToItem(foundIndex, scrollOffset = 0)
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
                set(value = currentScrollPosition, key = SemanticsPropertyKey("currentScrollPosition"))
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled,
    ) {
        sortedProductWithColors.forEachIndexed { lazyIndex, (product, colors) ->    //<--
            val isExpanded = activeCentralValues.expanded_M1Produit?.keyID == product.keyID

            // ── DEBUG: log whenever the targeted product / M3 enters the grid ──
            if (product.keyID == DBG_M3_PARENT) {
                val targeted = colors.find { it.keyID == DBG_M3_KEY }
                Log.d(DBG_TAG_GRID, "[Grid item] productKeyID=${product.keyID}" +
                        " | lazyIndex=$lazyIndex" +
                        " | parentProduit_Classement=${targeted?.parentProduit_Classement}" +
                        " | isExpanded=$isExpanded" +
                        " | colorsCount=${colors.size}" +
                        " | targetedM3 present=${targeted != null}" +
                        " | targetedM3 img=${targeted?.nomImageFichieSansEtansion}" +
                        " | targetedM3 visible=${targeted?.its_pour_affiche_au_presenter}")
            }

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
