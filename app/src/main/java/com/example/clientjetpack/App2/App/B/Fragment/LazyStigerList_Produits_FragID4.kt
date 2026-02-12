package com.example.clientjetpack.App2.App.B.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Z.Components.Modules.HandlePresenterClientScroll
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Z.Components.ScrolleAdBanner
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiConexiontLuncher
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.B.Fragment.Z.Components.CategoryStickyHeader
import com.example.clientjetpack.App2.App.B.Fragment.Z.Components.Modules.HandlePresenterScrollBroadcast_app2
import com.example.clientjetpack.App2.App.View.Item_Produit_AppEcranPresntoireJemlaCom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * UPDATED: Now displays Catalogue headers followed by Category headers
 */
@Composable
fun Etager_LazyColumn_App2(
    modifier: Modifier = Modifier.Companion,
    cataloguesWithCategoriesAndProducts: List<Pair<CataloguesCaegorie, List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>,
    viewModelHeadViewModel_App2: WifiConexiontLuncher,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (ArticlesBasesStatsTable) -> Unit,
    justMovedProductKeyID: String?,
    RepositorysMainGetter_app2: RepositorysMainGetter_app2,
    FocusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    isWifiClientConnected_1: Boolean
) {
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModelHeadViewModel_App2.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isHostPhone = uiState.productDisplayController.isHostPhone
    val isConnected = uiState.productDisplayController.isConnected
    val currentScrollPosition = uiState.productDisplayController.mainGridScrollPosition

    val tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    val isScrollEnabled = isHostPhone || !isConnected

    val expanded_M3CouleurProduitInfos = FocusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos

    // Handle expanded item scroll to position WITHOUT lock
    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            // Only scroll if we're the host phone
            if (!isHostPhone) return@LaunchedEffect

            var currentIndex = 0
            var foundIndex = -1

            // Account for banner at the top (index 0)
            currentIndex = 1

            for ((catalogue, categoriesWithProducts) in cataloguesWithCategoriesAndProducts) {
                // Catalogue header takes one slot
                currentIndex++

                for ((category, productColorPairs) in categoriesWithProducts) {
                    // Category header takes one slot (if displayed)
                    if (category.displayedHeader) {
                        currentIndex++
                    }

                    val productIndex = productColorPairs.indexOfFirst { (product, _) ->
                        product.id == expandedColor.parentBProduitOldID
                    }

                    if (productIndex != -1) {
                        foundIndex = currentIndex + productIndex
                        break
                    }

                    currentIndex += productColorPairs.size
                }

                if (foundIndex != -1) break
            }

            if (foundIndex != -1) {
                delay(100)
                coroutineScope.launch {
                    gridState.animateScrollToItem(foundIndex)
                }
            }
        }
    }

    HandlePresenterScrollBroadcast_app2(
        isHostPhone = isHostPhone,
        isConnected = isConnected,
        gridState = gridState,
        viewModel = viewModelHeadViewModel_App2
    )

    HandlePresenterClientScroll(
        isHostPhone = isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
        tag = tag
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
        userScrollEnabled = isScrollEnabled
    ) {
        // Add banner at the top
        item(
            key = "ad_banner_header",
            span = StaggeredGridItemSpan.Companion.FullLine
        ) {
            ScrolleAdBanner(
                onBannerClick = { bannerIndex ->
                    // Handle banner click if needed
                },
                onClickImageToShowControles = onClickImageToShowControles
            )
        }

        cataloguesWithCategoriesAndProducts.forEach { (catalogue, categoriesWithProducts) ->
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
                                RepositorysMainGetter_app2.repoM16CategorieProduit.addOrUpdateData(
                                    updatedCategory
                                )
                            }
                        )
                    }
                }

                productColorPairs.forEach { (product, colors) ->
                    val isExpanded = FocusedValuesGetter_app2.active_Central_Values
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
                        LazyStigerList_Produits_App2(
                            isWifiClientConnected_1=isWifiClientConnected_1,
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
    catalogue: CataloguesCaegorie,
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
fun LazyStigerList_Produits_App2(
    modifier: Modifier = Modifier.Companion,
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    FocusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false,
    isWifiClientConnected_1: Boolean
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
        Item_Produit_AppEcranPresntoireJemlaCom(
            isWifiClientConnected_1=isWifiClientConnected_1,
            relative_M1produit = product,
            on_pour_send_data = on_pour_send_data,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
