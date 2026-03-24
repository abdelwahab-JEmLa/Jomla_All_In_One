package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Fragment.Z.Components.Modules.HandlePresenterClientScroll
import Application4.App.Fragment.Z.Components.Modules.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * UPDATED: Now displays Catalogue headers followed by Category headers
 */
@Composable
fun Etager_LazyColumn(
    modifier: Modifier = Modifier.Companion,
    cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    on_pour_send_data: (String, String) -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {            //<--
//TODO(1): fait que ca n affihe pas le header si il n ya aucun produit la don
    val gridState = rememberLazyStaggeredGridState()
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val wifiState by viewModel.wifiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition

    val tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    val isScrollEnabled = isHostPhone || !isConnected

    val expanded_M3CouleurProduitInfos =
        uiState_NewProtoPatterns_viewModel.first.active_Central_Values.expanded_M3CouleurProduitInfos

    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect

        val targetKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        var currentIndex = 0   // no banner — grid starts directly with catalogue_header
        var foundIndex = -1

        outer@ for ((_, categoriesWithProducts) in cataloguesWithCategoriesAndProducts) {
            // Guard: skip empty catalogues (mirrors the forEach guard in the grid)
            if (categoriesWithProducts.isEmpty()) continue@outer

            currentIndex++     // catalogue_header_{catalogue.id}

            for ((category, productColorPairs) in categoriesWithProducts) {
                if (category.displayedHeader) currentIndex++  // category_header_{category.id}

                val productIndex = productColorPairs.indexOfFirst { (product, _) ->
                    product.keyID == targetKeyID
                }
                if (productIndex != -1) {
                    foundIndex = currentIndex + productIndex
                    break@outer
                }
                currentIndex += productColorPairs.size
            }
        }

        if (foundIndex >= 0) {
            // Wait for the FullLine span change to recompose and lay out before scrolling.
            delay(300)
            coroutineScope.launch {
                gridState.animateScrollToItem(foundIndex)
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
        tag = tag
    )
    var lenceVentOperations by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var all by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var activeBonVentKey by remember { mutableStateOf("") }
    var activeM9AppCompt by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val targetComptKeyId =
                M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            activeBonVentKey = viewModel.appDatabase.dao_M9AppCompt().getAll()
                .find { it.keyID == targetComptKeyId }?.onVentM8BonVentKey ?: ""

            val dao_M10OperationVentCouleurall =
                viewModel.appDatabase.dao_M10OperationVentCouleur().getAll()
            lenceVentOperations =
                dao_M10OperationVentCouleurall.filter { it.parent_M8BonVent_KeyId == activeBonVentKey }
            all =
                dao_M10OperationVentCouleurall
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
        /*  // Add banner at the top
           item(
                key = "ad_banner_header",
                span = StaggeredGridItemSpan.Companion.FullLine
            ) {
                ScrolleAdBanner(
                    onBannerClick = { bannerIndex ->
                        // Handle banner click if needed
                    },
                )
            }      */

        cataloguesWithCategoriesAndProducts.forEach { (catalogue, categoriesWithProducts) ->
            // TODO(1) fix: skip catalogue block entirely if it has no categories with products
            if (categoriesWithProducts.isEmpty()) return@forEach

            // Add Catalogue Header
            item(
                key = "catalogue_header_${catalogue.id}",
                span = StaggeredGridItemSpan.Companion.FullLine
            ) {
                CatalogueHeader(catalogue = catalogue)
            }

            categoriesWithProducts.forEach { (category, productColorPairs) ->
                // Only show category header if displayedHeader is true
                if (category.displayedHeader) {
                    item(
                        key = "category_header_${category.id}",
                        span = StaggeredGridItemSpan.Companion.FullLine
                    ) {
                        CategoryStickyHeader(
                            category = category,
                            onToggleHeaderVisibility = { updatedCategory ->
                                uiState_NewProtoPatterns_viewModel.second.repositorysMainSetter_NewProtoPatterns.update_M16CategorieProduit(
                                    updatedCategory
                                )
                            },
                            viewModel = uiState_NewProtoPatterns_viewModel.second,
                            uiStateNewProtoPatterns = uiState_NewProtoPatterns_viewModel.first
                        )
                    }
                }

                productColorPairs.forEach { (product, colors) ->
                    val isExpanded = uiState_NewProtoPatterns_viewModel.first.active_Central_Values
                        .expanded_M1Produit?.keyID == product.keyID

                    // Check if this product just moved
                    val justMoved = product.keyID == justMovedProductKeyID

                    item(
                        key = "product_${product.keyID}",
                        span = if (isExpanded) {
                            StaggeredGridItemSpan.Companion.FullLine
                        } else {
                            StaggeredGridItemSpan.Companion.SingleLane
                        }
                    ) {
                        LazyStigerList_Produits_FragID4(
                            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                            product = product,
                            colors = colors,
                            on_pour_send_data = on_pour_send_data,
                            onCategoryClick = {
                                Log.d(
                                    "CategoryDialog_FragID4",
                                    "Category click from product: ${product.nom}"
                                )
                                onProductCategoryClick(product)
                            },
                            justMoved = justMoved
                        )
                    }
                }
            }
        }
    }
}

/**
 * Catalogue Header - Displays the catalogue name with color
 */
@Composable
fun CatalogueHeader(
    catalogue: M21CataloguesCategorie,
    modifier: Modifier = Modifier
) {
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
fun LazyStigerList_Produits_FragID4(
    modifier: Modifier = Modifier.Companion,
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {

    // Animation state for moved products
    val backgroundColor by animateColorAsState(
        targetValue = if (justMoved) {
            Color(0xFF4CAF50).copy(alpha = 0.3f) // Green highlight
        } else {
            Color.Companion.Transparent
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
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
        modifier = Modifier.Companion
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(backgroundColor, RoundedCornerShape(12.dp))
    ) {
        A_Item_Produit_App4(
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_M1produit = product,
            on_pour_send_data = on_pour_send_data,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
