package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ActiveDatasFragNewProto
import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Modules.Wi.Module.HandlePresenterClientScroll
import Application4.App.Modules.Wi.Module.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M3CouleurProduitInfos
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
    val isScrollEnabled = isHostPhone || !isConnected
    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos

    val productWithColorsList by remember {
        derivedStateOf {
            val allColours = activeDatas.list_M03CouleurProduitInfos ?: emptyList()
            val allProducts = activeDatas.list_M1Produit ?: emptyList()
            val activeFilter = activeDatas.affiche_produits_Ou_On_TagPrioriter

            // In échantillons mode the priority-tag filter is skipped here;
            // displayList handles visibility via the M3 ref-keys map instead.
            val isEchatillantsMode = activeFilter
                ?.contains(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS) == true

            val productByKey = allProducts.associateBy { it.keyID }
            allColours
                .groupBy { it.parentBProduitInfosKeyID }
                .mapNotNull { (produitKeyID, colours) ->
                    val product = productByKey[produitKeyID] ?: return@mapNotNull null
                    if (!isEchatillantsMode && !product.matchesPrioriteFilter(activeFilter)) return@mapNotNull null
                    product to colours
                }
                .sortedBy { (product, _) -> product.classement_By_FilterKeys_M3 }
        }
    }

    var lenceVentOperations by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var activeBonVentKey by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val targetComptKeyId = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            activeBonVentKey = viewModel.appDatabase.dao_M9AppCompt().getAll()
                .find { it.keyID == targetComptKeyId }?.onVentM8BonVentKey ?: ""
            lenceVentOperations = (activeDatas.list_M10OperationVentCouleur ?: emptyList())
                .filter { it.parent_M8BonVent_KeyId == activeBonVentKey }
        }
    }

    val displayList by remember {
        derivedStateOf {
            val isEchatillants = activeDatas.affiche_produits_Ou_On_TagPrioriter
                ?.contains(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS) == true
            if (!isEchatillants) {
                productWithColorsList
            } else {
                val echaKeys =get_echatilliantKeyM3(activeDatas)
                productWithColorsList.mapNotNull { (product, colors) ->
                    val echaColors = colors.filter { it.keyID in echaKeys }
                    if (echaColors.isEmpty()) null else product to echaColors
                }
            }
        }
    }

    val gridColumns by remember {
        derivedStateOf {
            if (activeDatas.affiche_produits_Ou_On_TagPrioriter
                    ?.contains(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS) == true) 4 else 2
        }
    }

    LaunchedEffect(displayList) {
        activeDatas.parentProduit_Classement = displayList
            .mapIndexed { index, (product, _) -> product.keyID to index }
            .toMap()
    }

    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect
        val targetKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetKeyID.isBlank()) return@LaunchedEffect
        val foundIndex = displayList.indexOfFirst { (product, _) -> product.keyID == targetKeyID }
        if (foundIndex >= 0) {
            delay(400)
            coroutineScope.launch {
                gridState.animateScrollToItem((foundIndex - 1).coerceAtLeast(0))
            }
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
        tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(gridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = run {
                    val echaKeys = get_echatilliantKeyM3(activeDatas)
                    val echaProductKeyIDs = activeDatas.list_M03CouleurProduitInfos
                        ?.filter { it.keyID in echaKeys }
                        ?.map { it.parentBProduitInfosKeyID }
                        ?.toSet() ?: emptySet()
                    activeDatas.list_M1Produit?.filter { it.keyID in echaProductKeyIDs }?.map { it.nom to it }
                }, key = SemanticsPropertyKey("its_in_echantiallants"))
                set(value = lenceVentOperations, key = SemanticsPropertyKey("lenceVentOperations"))
                set(value = activeBonVentKey, key = SemanticsPropertyKey("activeBonVentKey"))
                set(value = activeDatas.list_M10OperationVentCouleur ?: emptyList<M10OperationVentCouleur>(), key = SemanticsPropertyKey("all"))
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled
    ) {
        displayList.forEach { (product, colors) ->
            val isExpanded = wifiState.expanded_M1Produit?.keyID == product.keyID
            Log.d("FragID4Grid", "isExpanded=$isExpanded | product=${product.keyID} | expanded=${wifiState.expanded_M1Produit?.keyID}")
            val justMoved = product.keyID == justMovedProductKeyID
            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
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
private fun get_echatilliantKeyM3(activeDatas: ActiveDatasFragNewProto): Collection<String> =
    (activeDatas.list_M03CouleurProduitInfos?.filter { it.its_in_echantiallants == true }
        ?.map { it.keyID }
        ?: emptySet())

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
