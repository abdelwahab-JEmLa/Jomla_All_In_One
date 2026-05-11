package Application2.App.Fragment

import Application2.App.App.ViewModel.Feature.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Item_Produit_AppEcranPresntoireJemlaCom
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
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
import androidx.compose.runtime.DisposableEffect
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

private const val DBG_TAG_GRID  = "TargetedM3_Affichage"
private const val DBG_M3_KEY    = "-OWDMIC_UdVXmSNw-Dz0"
private const val DBG_M3_PARENT = "-OV3rmZ-9sy3P5rnINL3"

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
    val filterDesProduits = uiState.filter_des_produits

    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos
    val expanded_M1Produit = wifiState.expanded_M1Produit

    // ── Sort by parentProduit_Classement so lazyIndex == classement position ──
    val sortedProductWithColors = remember(productWithColors) {
        val rawOrder = productWithColors.withIndex()
            .associate { (i, pair) -> pair.first.keyID to i }

        val sorted = productWithColors.sortedBy { (_, colors) ->
            colors.minOfOrNull { it.parentProduit_Classement ?: Int.MAX_VALUE }
                ?: Int.MAX_VALUE
        }

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

    // ── Apply filter received from the host over WiFi ─────────────────────────
    val visibleProductWithColors = remember(sortedProductWithColors, filterDesProduits) {
        when (filterDesProduits) {
            Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement ->
                sortedProductWithColors.filter { (_, colors) -> colors.none { it.its_in_echantiallants } }
            null,
            Filter_Affichage_Mode_Proto.Tablette_Et_Echants ->
                sortedProductWithColors

            Filter_Affichage_Mode_Proto.Echants_Seulement ->
                sortedProductWithColors.filter { (_, colors) -> colors.any { it.its_in_echantiallants } }
        }
    }

    LaunchedEffect(expanded_M1Produit) {
        expanded_M1Produit ?: return@LaunchedEffect
        val targetKeyID = expanded_M1Produit.keyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        val foundIndex = visibleProductWithColors.indexOfFirst { (product, _) ->
            product.keyID == targetKeyID
        }
        if (foundIndex < 0) return@LaunchedEffect

        coroutineScope.launch { gridState.scrollToItem(foundIndex) }
        delay(300)
        coroutineScope.launch { gridState.animateScrollToItem(foundIndex, scrollOffset = 0) }
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
                set(value = uiState, key = SemanticsPropertyKey("uiState"))
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled,
    ) {
        visibleProductWithColors.forEachIndexed { lazyIndex, (product, colors) ->
            val isExpanded = activeCentralValues.expanded_M1Produit?.keyID == product.keyID

            if (product.keyID == DBG_M3_PARENT) {
                val targeted = colors.find { it.keyID == DBG_M3_KEY }
                Log.d(DBG_TAG_GRID, "[Grid item] productKeyID=${product.keyID}" +
                        " | lazyIndex=$lazyIndex" +
                        " | parentProduit_Classement=${targeted?.parentProduit_Classement}" +
                        " | isExpanded=$isExpanded" +
                        " | colorsCount=${colors.size}" +
                        " | targetedM3 present=${targeted != null}" +
                        " | targetedM3 img=${targeted?.nomImageFichieSansEtansion}" +
                        " | targetedM3 visible=${targeted?.its_pour_affiche_au_presenter}" +
                        " | filterDesProduits=$filterDesProduits")
            }

            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine
                else StaggeredGridItemSpan.SingleLane,
            ) {
                // ── LOG: targeted M3 est-il affiché à l'écran ? ───────────────
                // DisposableEffect se déclenche quand l'item entre en composition
                // (= la grille lazy le rend visible) et onDispose quand il sort.
                if (product.keyID == DBG_M3_PARENT) {
                    val targeted = colors.find { it.keyID == DBG_M3_KEY }
                    DisposableEffect(Unit) {
                        val colorsLog = colors.joinToString(separator = "\n    ") { c ->
                            "keyID=${c.keyID}" +
                                    " | nom=${c.nomCouleurStrSiSonImageDispo}" +
                                    " | img=${c.nomImageFichieSansEtansion}" +
                                    " | visible=${c.its_pour_affiche_au_presenter}" +
                                    " | echant=${c.its_in_echantiallants}" +
                                    " | classement=${c.parentProduit_Classement}" +
                                    if (c.keyID == DBG_M3_KEY) " ← TARGETED" else ""
                        }
                        Log.d(DBG_TAG_GRID,
                            "[AFFICHE ✅] targeted M3 EST affiché à l'écran" +
                                    " | keyID=${targeted?.keyID}" +
                                    " | img=${targeted?.nomImageFichieSansEtansion}" +
                                    " | its_pour_affiche_au_presenter=${targeted?.its_pour_affiche_au_presenter}" +
                                    " | its_in_echantiallants=${targeted?.its_in_echantiallants}" +
                                    " | lazyIndex=$lazyIndex" +
                                    " | filterDesProduits=$filterDesProduits" +
                                    " | totalColorsInItem=${colors.size}" +
                                    "\n    [Couleurs affichées au lazy]\n    $colorsLog")
                        onDispose {
                            Log.d(DBG_TAG_GRID,
                                "[AFFICHE ❌] targeted M3 a quitté l'écran (scroll)" +
                                        " | keyID=${targeted?.keyID}")
                        }
                    }
                }
                // ──────────────────────────────────────────────────────────────

                LazyStigerList_Produits_App2(product to colors, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LazyStigerList_Produits_App2(
    productColorPairs: Pair<M01Produit, List<M3CouleurProduitInfos>>,
    viewModel: ViewModel_MainFragment,
) {
    Box {
        Item_Produit_AppEcranPresntoireJemlaCom(
            productColorPairs,
            viewModel = viewModel
        )
    }
}
