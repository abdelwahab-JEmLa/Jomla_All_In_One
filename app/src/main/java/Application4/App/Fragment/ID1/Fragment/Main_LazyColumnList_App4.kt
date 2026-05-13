package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Modules.Wi.Module.HandlePresenterClientScroll
import Application4.App.Modules.Wi.Module.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Main_LazyColumnList_App4(
    modifier: Modifier = Modifier,
    relative_list_m3: List<M3CouleurProduitInfos>?,
    relative_list_m10_vents: List<M10OperationVentCouleur>,
    outlined_search_Query: String,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
) {
    val gridState = rememberLazyStaggeredGridState()
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val activeDatas = viewModel.active_Datas
    val wifiState by viewModel.wifiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    val set_couleursKey_echantilliants_achat by remember {
        derivedStateOf {
            activeDatas.list_M10OperationVentCouleur
                ?.sortedByDescending { it.creationTimestamps }
                ?.map { it.parent_M3CouleurProduit_KeyID }
                ?: emptyList()
        }
    }

    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition
    val isScrollEnabled = isHostPhone || !isConnected


    val finale_filtred_list by remember {
        derivedStateOf {
            ProductListFilterLogic.compute(
                rawColors                  = activeDatas.list_M03CouleurProduitInfos,
                productMap                 = activeDatas.list_M1Produit?.associateBy { it.keyID } ?: emptyMap(),
                query                      = activeDatas.filter_echatilaten.trim().lowercase(),
                mode                       = activeDatas.filterAffichageMode_Proto,
                ventCouleurs               = activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
                categories                 = activeDatas.list_M16CategorieProduit ?: emptyList(),
                catalogues                 = get_ListM21CataloguesCategorie(),
                echantillantsPurchaseOrder = set_couleursKey_echantilliants_achat,
                classement                 = activeDatas.parentProduit_Classement,
                sort_Order = activeDatas.filterAffichageMode_Proto.mais_sort_order,
            )
        }
    }

    val gridColumns by remember {
        derivedStateOf {
            when (activeDatas.filterAffichageMode_Proto) {
                Filter_Affichage_Mode_Proto.Echants_Seulement -> 4
                else -> 2
            }
        }
    }

    LaunchedEffect(finale_filtred_list) {
        activeDatas.parentProduit_Classement = finale_filtred_list
            .mapIndexed { index, (product, _) -> product.keyID to index }
            .toMap()
    }

    val expanded_M1Produit = wifiState.expanded_M1Produit

    LaunchedEffect(expanded_M1Produit) {
        expanded_M1Produit ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect
        val targetKeyID = expanded_M1Produit.keyID
        if (targetKeyID.isBlank()) return@LaunchedEffect
        val foundIndex = finale_filtred_list.indexOfFirst { (product, _) -> product.keyID == targetKeyID }
        if (foundIndex < 0) return@LaunchedEffect
        coroutineScope.launch { gridState.scrollToItem(foundIndex) }
        delay(300)
        coroutineScope.launch { gridState.animateScrollToItem(foundIndex, scrollOffset = 0) }
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
        finale_filtred_list.forEach { (product, colors) ->
            val isExpanded = wifiState.expanded_M1Produit?.keyID == product.keyID
            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
            ) {
                LazyStigerList_Produits_FragID4(
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    product = product,
                    colors = colors,
                    onCategoryClick = { onProductCategoryClick(product) },
                    justMoved = product.keyID == justMovedProductKeyID
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
