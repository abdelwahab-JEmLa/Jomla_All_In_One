package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Item_Produit_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Dialogs.CategorySelectionDialog_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Modules.HandlePresenterClientScroll
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Modules.HandlePresenterScrollBroadcast
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Autres.ScrolleAdBanner
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Jomla_Clients
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoire_App_Produits_FragID4(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    viewModelHeadViewModel: HeadViewModel,
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    onClickImageToShowControles: () -> Unit
) {
    // FIXED: Get ViewModel for category operations
    val viewModelToUse = categoryViewModel ?: koinInject<EditeBaseDonneMainScreenIdS9ViewModel>()
    val uiState by viewModelToUse.uiState.collectAsState()

    var selectedProductForCategoryChange by remember { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            Log.d("CategoryAnimation_FragID4", "Product moved, will animate: $it")
            delay(1500)
            justMovedProductKeyID = null
        }
    }

    // FIXED: Prepare all data for the dialog
    val allCategories = remember(repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        repositorysMainGetter.repoM16CategorieProduit.datasValue
    }

    val allProducts = remember(repositorysMainGetter.repoM1Produit.datasValue) {
        repositorysMainGetter.repoM1Produit.datasValue
    }

    // FIXED: Get the last bon vent for filtering products
    val lastBonVentAbdelwahab = remember(
        repositorysMainGetter.repo8BonVent.datasValue,
        repositorysMainGetter.repo2Client.datasValue
    ) {
        repositorysMainGetter.getLastBonVentForClient(
            clientKeyID = Jomla_Clients.ECHATILLANTS_KEY_ID,
            etateFilter = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        )
    }

    // FIXED: Get operations from the last bon vent
    val operationsFromLastBon = remember(
        lastBonVentAbdelwahab,
        repositorysMainGetter.repo10OperationVentCouleur.datasValue
    ) {
        lastBonVentAbdelwahab?.let { bonVent ->
            repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { operation ->
                operation.parent_M8BonVent_KeyId == bonVent.keyID
            }
        } ?: emptyList()
    }

    // FIXED: Filter colors that have stock and are in operations
    val list_M3couleur = remember(
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue,
        operationsFromLastBon,
    ) {
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue.filter { couleur ->
            val hasStock = couleur.count_Don_Depot > 0

            operationsFromLastBon.any { operation ->
                operation.parent_M3CouleurProduit_KeyID == couleur.keyID
            }

            hasStock
        }.sortedByDescending { couleur ->
            operationsFromLastBon.find { operation ->
                operation.parent_M3CouleurProduit_KeyID == couleur.keyID
            }?.creationTimestamps ?: 0L
        }
    }

    // FIXED: Group colors by product
    val groupe_Couleur_Par_Produit = remember(
        list_M3couleur,
        repositorysMainGetter.repoM1Produit.datasValue
    ) {
        list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                repositorysMainGetter.repoM1Produit.datasValue.find {
                    it.keyID == productKeyID
                }?.let { product -> product to colors }
            }
            .sortedBy { (product, _) -> product.nom }
    }

    // FIXED: Group products by category - this was the missing variable!
    val groupe_Par_Categorie = remember(
        groupe_Couleur_Par_Produit,
        repositorysMainGetter.repoM16CategorieProduit.datasValue
    ) {
        groupe_Couleur_Par_Produit.groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, productColorPairs) ->
                repositorysMainGetter.repoM16CategorieProduit.datasValue.find {
                    it.id == categoryId
                }?.let { category -> category to productColorPairs }
            }
            .sortedBy { (category, _) -> category.positionDouble }
    }

    Etager_LazyColumn_FragID4(
        modifier = modifier,
        categoriesWithProducts = groupe_Par_Categorie,
        viewModelHeadViewModel = viewModelHeadViewModel,
        on_pour_send_data = on_pour_send_data,
        onClickImageToShowControles = onClickImageToShowControles,
        onProductCategoryClick = { product ->
            Log.d("CategoryDialog_FragID4", "onProductCategoryClick called for: ${product.nom}")
            selectedProductForCategoryChange = product
        },
        justMovedProductKeyID = justMovedProductKeyID,
        repositorysMainGetter = repositorysMainGetter
    )

    focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    // FIXED: Use the refactored dialog with all required parameters
    selectedProductForCategoryChange?.let { product ->
        Log.d("CategoryDialog_FragID4", "Displaying CategorySelectionDialog for: ${product.nom}")
        CategorySelectionDialog_FragID4(
            product = product,
            allCategories = allCategories,
            allProducts = allProducts, // ADDED: For calculating products per category
            isFastMoveMode = uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.FastMove,
            onCategorySelected = { newCategoryId ->
                Log.d("CategoryDialog_FragID4", "Category selected: $newCategoryId")
                val updatedProduct = newCategoryId?.let {
                    product.copy(
                        idParentCategorie = it,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                } ?: product

                repositorysMainGetter.repoM1Produit.update(updatedProduct)
                justMovedProductKeyID = product.keyID

                selectedProductForCategoryChange = null
            },
            onDismiss = {
                Log.d("CategoryDialog_FragID4", "Dialog dismissed")
                selectedProductForCategoryChange = null
            },
            onCreateNewCategory = { categoryName ->
                Log.d("CategoryDialog_FragID4", "Creating new category: $categoryName")
                viewModelToUse.addOrUpdateCategorie(
                    CategoriesTabelle(
                        nom = categoryName,
                        position = 0,
                        catalogueParentId = 4 // Default catalogue
                    )
                )
            },
            onUpdateCategoryName = { categoryId, newName ->
                Log.d("CategoryDialog_FragID4", "Updating category $categoryId to: $newName")

                allCategories.find { it.id == categoryId }?.let { category ->
                    val updatedCategory = category.copy(nom = newName)
                    repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(updatedCategory)
                }
            }
        )
    }
}

@Composable
fun Etager_LazyColumn_FragID4(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter,
    viewModelHeadViewModel: HeadViewModel,
    categoriesWithProducts: List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (ArticlesBasesStatsTable) -> Unit,
    justMovedProductKeyID: String?
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

    // Handle expanded item scroll to position WITHOUT lock
    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            // Only scroll if we're the host phone
            if (!isHostPhone) return@LaunchedEffect

            var currentIndex = 0
            var foundIndex = -1

            // Account for banner at the top (index 0)
            currentIndex = 1

            for ((category, productColorPairs) in categoriesWithProducts) {
                // Category header takes one slot
                currentIndex++

                val productIndex = productColorPairs.indexOfFirst { (product, _) ->
                    product.id == expandedColor.parentBProduitOldID
                }

                if (productIndex != -1) {
                    foundIndex = currentIndex + productIndex
                    break
                }

                currentIndex += productColorPairs.size
            }

            if (foundIndex != -1) {
                delay(100)
                coroutineScope.launch {
                    gridState.animateScrollToItem(foundIndex)
                }
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
        item(
            key = "ad_banner_header",
            span = StaggeredGridItemSpan.FullLine
        ) {
            ScrolleAdBanner(
                onBannerClick = { bannerIndex ->
                    // Handle banner click if needed
                },
                onClickImageToShowControles = onClickImageToShowControles
            )
        }

        categoriesWithProducts.forEach { (category, productColorPairs) ->
            // Only show header if displayedHeader is true
            if (category.displayedHeader) {
                item(
                    key = "header_${category.id}",
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    CategoryStickyHeader(
                        category = category,
                        onToggleHeaderVisibility = { updatedCategory ->
                            repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(updatedCategory)
                        }
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
                        StaggeredGridItemSpan.FullLine
                    } else {
                        StaggeredGridItemSpan.SingleLane
                    }
                ) {
                    LazyStigerList_Produits_FragID4(
                        product = product,
                        colors = colors,
                        on_pour_send_data = on_pour_send_data,
                        onCategoryClick = {
                            Log.d("CategoryDialog_FragID4", "Category click from product: ${product.nom}")
                            onProductCategoryClick(product)
                        },
                        justMoved = justMoved
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStickyHeader(
    category: CategoriesTabelle,
    modifier: Modifier = Modifier,
    onToggleHeaderVisibility: (CategoriesTabelle) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    val updatedCategory = category.copy(
                        displayedHeader = !category.displayedHeader
                    )
                    onToggleHeaderVisibility(updatedCategory)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (category.displayedHeader) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = if (category.displayedHeader) {
                        "Masquer l'en-tête"
                    } else {
                        "Afficher l'en-tête"
                    },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun LazyStigerList_Produits_FragID4(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false
) {
    Log.d("CategoryDialog_FragID4", "LazyStigerList_Produits_FragID4 - onCategoryClick null: ${onCategoryClick == null}")

    // Animation state for moved products
    val backgroundColor by animateColorAsState(
        targetValue = if (justMoved) {
            Color(0xFF4CAF50).copy(alpha = 0.3f) // Green highlight
        } else {
            Color.Transparent
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
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(backgroundColor, RoundedCornerShape(12.dp))
    ) {
        Item_Produit_FragID3(
            relative_M1produit = product,
            on_pour_send_data = on_pour_send_data,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
