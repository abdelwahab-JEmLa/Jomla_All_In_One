package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Fragment.Z.Components.Modules.HandlePresenterClientScroll
import Application4.App.Fragment.Z.Components.Modules.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "LazyGrid_Debug"

@Composable
fun Etager_LazyColumn(
    modifier: Modifier = Modifier,
    on_pour_send_data: (String, String) -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {
    val gridState = rememberLazyStaggeredGridState()
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val activeDatas = viewModel.active_Datas
    val wifiState by viewModel.wifiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition
    val tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    val isScrollEnabled = isHostPhone || !isConnected

    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos

    // ── LOG 1 : raw state of the two source lists ──────────────────────────
    val rawM1   = activeDatas.list_M1Produit
    val rawM3   = activeDatas.list_M03CouleurProduitInfos
    val filter  = activeDatas.affiche_produits_Ou_On_TagPrioriter
    Log.d(TAG, "=== Etager_LazyColumn recomposed ===")
    Log.d(TAG, "  list_M1Produit        : ${rawM1?.size ?: "NULL"}")
    Log.d(TAG, "  list_M03CouleurProduit: ${rawM3?.size ?: "NULL"}")
    Log.d(TAG, "  activeFilter          : $filter")

    val productWithColorsList by remember {
        derivedStateOf {
            val allColours  = activeDatas.list_M03CouleurProduitInfos ?: emptyList()
            val allProducts = activeDatas.list_M1Produit              ?: emptyList()
            val activeFilter = activeDatas.affiche_produits_Ou_On_TagPrioriter

            // ── LOG 2 : inputs at derivedStateOf evaluation time ──────────
            Log.d(TAG, "  [derivedState] allColours.size =${allColours.size}")
            Log.d(TAG, "  [derivedState] allProducts.size=${allProducts.size}")
            Log.d(TAG, "  [derivedState] activeFilter    =$activeFilter")

            val productByKey = allProducts.associateBy { it.keyID }

            // ── LOG 3 : grouping M3 → M1 ──────────────────────────────────
            val grouped = allColours.groupBy { it.parentBProduitInfosKeyID }
            Log.d(TAG, "  [derivedState] grouped M3 parentKeys count=${grouped.size}")
            if (grouped.isNotEmpty()) {
                Log.d(TAG, "  [derivedState] sample parentBProduitInfosKeyID=${grouped.keys.take(3)}")
            }
            Log.d(TAG, "  [derivedState] productByKey keys count=${productByKey.size}")
            if (productByKey.isNotEmpty()) {
                Log.d(TAG, "  [derivedState] sample product keyIDs=${productByKey.keys.take(3)}")
            }

            var droppedNoProduct   = 0
            var droppedFilterMiss  = 0

            val result = allColours
                .groupBy { it.parentBProduitInfosKeyID }
                .mapNotNull { (produitKeyID, colours) ->
                    val product = productByKey[produitKeyID]
                    if (product == null) {
                        droppedNoProduct++
                        // Log first few misses so we can spot the key mismatch
                        if (droppedNoProduct <= 5)
                            Log.w(TAG, "  [derivedState] ⚠️ no M1 found for parentKey=$produitKeyID")
                        return@mapNotNull null
                    }
                    if (!product.matchesPrioriteFilter(activeFilter)) {
                        droppedFilterMiss++
                        return@mapNotNull null
                    }
                    product to colours
                }
                .sortedBy { (product, _) -> product.classement_By_FilterKeys_M3 }

            // ── LOG 4 : final result ───────────────────────────────────────
            Log.d(TAG, "  [derivedState] RESULT size=${result.size}  " +
                    "droppedNoProduct=$droppedNoProduct  droppedFilterMiss=$droppedFilterMiss")
            if (result.isEmpty()) {
                Log.w(TAG, "  [derivedState] ⚠️ productWithColorsList is EMPTY — grid will show nothing")
                if (droppedNoProduct > 0)
                    Log.w(TAG, "    → $droppedNoProduct colours had no matching M1 product (keyID mismatch?)")
                if (droppedFilterMiss > 0)
                    Log.w(TAG, "    → $droppedFilterMiss products were filtered out by matchesPrioriteFilter")
                if (allColours.isEmpty())
                    Log.w(TAG, "    → list_M03CouleurProduitInfos is empty (seeding not done yet?)")
                if (allProducts.isEmpty())
                    Log.w(TAG, "    → list_M1Produit is empty (seeding not done yet?)")
            }

            result
        }
    }

    // ── LOG 5 : each time the final list changes ───────────────────────────
    LaunchedEffect(productWithColorsList.size) {
        Log.d(TAG, "  [LaunchedEffect] productWithColorsList.size changed → ${productWithColorsList.size}")
    }

    // FIX(TODO-2.C): Keep activeDatas.parentProduit_Classement in sync with the actual
    // display order computed by this grid. The map (productKeyID → display index) is what
    // Upload_Filtered_Au_Ref_Active_Keys_M03Couleurs_Button uses as classement when writing
    // filter-keys back to Firebase, so it must reflect the real on-screen positions rather
    // than the stale zeros stored in Firebase.
    LaunchedEffect(productWithColorsList) {
        activeDatas.parentProduit_Classement = productWithColorsList
            .mapIndexed { index, (product, _) -> product.keyID to index }
            .toMap()
    }

    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect
        val targetKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        val foundIndex =
            productWithColorsList.indexOfFirst { (product, _) -> product.keyID == targetKeyID }
        if (foundIndex >= 0) {
            delay(300)
            coroutineScope.launch { gridState.animateScrollToItem(foundIndex) }
        }
    }

    HandlePresenterScrollBroadcast(
        isHostPhone = isHostPhone,
        isConnected = isConnected,
        gridState = gridState,
        viewModel = viewModel
    )
    HandlePresenterClientScroll(
        isHostPhone = isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
        tag = tag
    )

    var lenceVentOperations by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var all by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var activeBonVentKey by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val targetComptKeyId =
                M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            activeBonVentKey = viewModel.appDatabase.dao_M9AppCompt().getAll()
                .find { it.keyID == targetComptKeyId }?.onVentM8BonVentKey ?: ""
            val allOps = viewModel.appDatabase.dao_M10OperationVentCouleur().getAll()
            lenceVentOperations = allOps.filter { it.parent_M8BonVent_KeyId == activeBonVentKey }
            all = allOps
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = lenceVentOperations, key = SemanticsPropertyKey("lenceVentOperations"))
            }
            .semantics(mergeDescendants = true) {
                set(value = activeBonVentKey, key = SemanticsPropertyKey("activeBonVentKey"))
            }
            .semantics(mergeDescendants = true) {
                set(value = all, key = SemanticsPropertyKey("all"))
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled
    ) {
        // ── LOG 6 : inside the grid lambda ────────────────────────────────
        Log.d(TAG, "  [GridLambda] productWithColorsList.size=${productWithColorsList.size}")

        productWithColorsList.forEach { (product, colors) ->
            val isExpanded = wifiState.expanded_M1Produit?.keyID == product.keyID
            val justMoved = product.keyID == justMovedProductKeyID

            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine
                else StaggeredGridItemSpan.SingleLane
            ) {
                LazyStigerList_Produits_FragID4(
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    product = product,
                    colors = colors,
                    on_pour_send_data = on_pour_send_data,
                    onCategoryClick = { onProductCategoryClick(product) },
                    justMoved = justMoved
                )
            }
        }
    }
}

@Composable
fun LazyStigerList_Produits_FragID4(
    modifier: Modifier = Modifier,
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (justMoved) Color(0xFF4CAF50).copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "backgroundColorAnimation"
    )
    val scale by animateFloatAsState(
        targetValue = if (justMoved) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )
    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(backgroundColor, RoundedCornerShape(12.dp))
    ) {
        A_Item_Produit_App4(
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_M1produit = product,
            relative_ListM3Couleurs_override = colors,
            on_pour_send_data = on_pour_send_data,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
