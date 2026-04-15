package Application4.App.Fragment.ID1.Fragment

import A_Main.Shared.Init.DBG_M3_KEY
import A_Main.Shared.Init.DBG_PROD_KEY
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Modules.Wi.Module.HandlePresenterClientScroll
import Application4.App.Modules.Wi.Module.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Etager_LazyColumn(
    modifier: Modifier = Modifier,
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
    val isScrollEnabled = isHostPhone || !isConnected
    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos

    // when active, only colours that own an M10 vent operation on the current bon are shown.
    val displayList by remember {
        derivedStateOf {
            val allColours = activeDatas.list_M03CouleurProduitInfos ?: emptyList()
            val allProducts = activeDatas.list_M1Produit ?: emptyList()
            val isEchatillantsMode = activeDatas.isEchatillantsMode
            val isPanieMode = activeDatas.its_Panie_Mode

            // ── Search filter (case-insensitive, trims whitespace) ────────────
            val searchQuery = activeDatas.filter_echatilaten.trim().lowercase()

            val echaKeys = allColours
                .filter { it.its_in_echantiallants }
                .map { it.keyID }
                .toSet()

            // Colours that have at least one active-bon-vent operation
            val ventColourKeys: Set<String> = if (isPanieMode) {
                activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                    ?.map { it.parent_M3CouleurProduit_KeyID }
                    ?.toSet()
                    ?: emptySet()
            } else emptySet()

            val productByKey = allProducts.associateBy { it.keyID }

            // ── DEBUG: targeted M3 trace ──────────────────────────────────────
            val targetedColor = allColours.find { it.keyID == DBG_M3_KEY }
            android.util.Log.d(
                "TargetedM3_Lazy",
                "[displayList] allColours=${allColours.size}" +
                        " | targetedM3 in allColours=${targetedColor != null}" +
                        " | visible=${targetedColor?.its_pour_affiche_au_presenter}" +
                        " | isEcha=${targetedColor?.its_in_echantiallants}" +
                        " | parent=${targetedColor?.parentBProduitInfosKeyID}" +
                        " | parentProduct=${productByKey[DBG_PROD_KEY]?.nom}" +
                        " | echaMode=$isEchatillantsMode" +
                        " | panieMode=$isPanieMode" +
                        " | isInEchaKeys=${DBG_M3_KEY in echaKeys}" +
                        " | isInVentKeys=${DBG_M3_KEY in ventColourKeys}"
            )
            // ─────────────────────────────────────────────────────────────────

            allColours
                .groupBy { it.parentBProduitInfosKeyID }
                .mapNotNull { (produitKeyID, colours) ->
                    val product = productByKey[produitKeyID] ?: return@mapNotNull null
                    product to colours
                }
                .mapNotNull { (product, colors) ->
                    val filtered = when {
                        // Panie mode: only colours present in the active bon vent
                        isPanieMode -> colors.filter { it.keyID in ventColourKeys }
                        // Échantillons mode: only échantillon colours
                        isEchatillantsMode -> colors.filter { it.keyID in echaKeys }
                        // Normal mode: everything except échantillons
                        else -> colors.filter { it.keyID !in echaKeys }
                    }

                    // ── DEBUG: trace pourquoi le targeted passe ou non ────────
                    if (product.keyID == DBG_PROD_KEY) {
                        android.util.Log.d(
                            "TargetedM3_Lazy",
                            "[groupBy] targetedProduct found" +
                                    " | colorsInGroup=${colors.size}" +
                                    " | filteredColors=${filtered.size}" +
                                    " | targetedM3InGroup=${colors.any { it.keyID == DBG_M3_KEY }}" +
                                    " | targetedM3InFiltered=${filtered.any { it.keyID == DBG_M3_KEY }}"
                        )
                    }
                    // ─────────────────────────────────────────────────────────

                    if (filtered.isEmpty()) null else product to filtered
                }
                // ── Apply search filter across all modes ──────────────────────
                .filter { (product, _) ->
                   searchQuery.isEmpty() || product.nom.lowercase().contains(searchQuery)
                }
        }
    }

    var lenceVentOperations by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var activeBonVentKey by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val targetComptKeyId =
                M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            activeBonVentKey = viewModel.appDatabase.dao_M9AppCompt().getAll()
                .find { it.keyID == targetComptKeyId }?.onVentM8BonVentKey ?: ""
            lenceVentOperations = (activeDatas.list_M10OperationVentCouleur ?: emptyList())
                .filter { it.parent_M8BonVent_KeyId == activeBonVentKey }
        }
    }

    val gridColumns by remember {
        derivedStateOf {
            when {
                activeDatas.isEchatillantsMode -> 4
                activeDatas.its_Panie_Mode -> 2
                else -> 2
            }
        }
    }

    LaunchedEffect(displayList) {
        activeDatas.parentProduit_Classement = displayList
            .mapIndexed { index, (product, _) -> product.keyID to index }
            .toMap()
    }

    val expanded_M1Produit = wifiState.expanded_M1Produit

    LaunchedEffect(expanded_M1Produit) {
        expanded_M1Produit ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect
        val targetKeyID = expanded_M1Produit.keyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        val foundIndex = displayList.indexOfFirst { (product, _) -> product.keyID == targetKeyID }
        if (foundIndex < 0) return@LaunchedEffect

        coroutineScope.launch { gridState.scrollToItem(foundIndex) }
        delay(300)
        coroutineScope.launch { gridState.animateScrollToItem(foundIndex, scrollOffset = 0) }
    }

    LaunchedEffect(isHostPhone, isConnected) {
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { (_, _) -> }
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
        gridState = gridState
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(gridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled
    ) {
        displayList.forEach { (product, colors) ->
            val isExpanded = wifiState.expanded_M1Produit?.keyID == product.keyID
            val justMoved = product.keyID == justMovedProductKeyID
            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
            ) {
                LazyStigerList_Produits_FragID4(
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    product = product,
                    colors = colors,
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
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
