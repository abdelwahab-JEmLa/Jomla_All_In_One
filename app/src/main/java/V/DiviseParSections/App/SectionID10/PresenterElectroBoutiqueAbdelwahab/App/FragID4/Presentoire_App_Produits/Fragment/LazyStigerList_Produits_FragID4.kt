package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.ViewModel_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Z.Components.Modules.HandlePresenterClientScroll
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Z.Components.Modules.HandlePresenterScrollBroadcast
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.Item_Produit_FragID3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * UPDATED: Now displays Catalogue headers followed by Category headers
 */
@Composable
fun Etager_LazyColumn_FragID4(
    modifier: Modifier = Modifier.Companion,
    cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    viewModelHeadViewModel: HeadViewModel,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    repositorysMainGetter: RepositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    isWifiClientConnected_1: Boolean,
    viewModel: ViewModel_FragID4
) {
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModelHeadViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isHostPhone = uiState.productDisplayController.isHostPhone
    val isConnected = uiState.productDisplayController.isConnected
    val currentScrollPosition = uiState.productDisplayController.mainGridScrollPosition

    val tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    val isScrollEnabled = isHostPhone || !isConnected

    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    // When an item is expanded, auto-scroll so it is fully visible.
    // KEY FIXES:
    //  1. No phantom banner offset — the ad_banner_header item is commented out,
    //     so currentIndex starts at 0 to match the actual grid item positions.
    //  2. Match by product.keyID == expandedColor.parentBProduitInfosKeyID (String),
    //     NOT product.id == parentBProduitOldID (Int/Long) which is often 0 and
    //     always picks the first item in the list by mistake.
    //  3. Wait 300 ms for the FullLine span recomposition to settle before scrolling,
    //     otherwise the grid scrolls to a stale layout position and lands below the item.
    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect

        val targetKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        var currentIndex = 0   // no banner — grid starts directly with catalogue_header
        var foundIndex = -1

        outer@ for ((_, categoriesWithProducts) in cataloguesWithCategoriesAndProducts) {
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
        viewModel = viewModelHeadViewModel
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
        /*  item(
              key = "ad_banner_header",
              span = StaggeredGridItemSpan.Companion.FullLine
          ) {
              ScrolleAdBanner(
                  onBannerClick = { bannerIndex ->
                      // Handle banner click if needed
                  },
                  onClickImageToShowControles = onClickImageToShowControles
              )
          }        */

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
                                repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(
                                    updatedCategory
                                )
                            },viewModel=viewModel
                        )
                    }
                }

                productColorPairs.forEach { (product, colors) ->
                    val isExpanded = focusedValuesGetter.active_Central_Values
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
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
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
        Item_Produit_FragID3(
            isWifiClientConnected_1=isWifiClientConnected_1,
            relative_M1produit = product,
            on_pour_send_data = on_pour_send_data,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
