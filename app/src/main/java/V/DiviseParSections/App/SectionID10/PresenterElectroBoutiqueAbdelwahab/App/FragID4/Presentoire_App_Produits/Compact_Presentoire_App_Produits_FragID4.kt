package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Modules.HandlePresenterClientScroll
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Modules.HandlePresenterScrollBroadcast
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.A_Item_Produit_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Autres.ScrolleAdBanner
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Jomla_Clients
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoire_App_Produits_FragID4(
    modifier: Modifier = Modifier,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    viewModelHeadViewModel: HeadViewModel,
    on_pour_send_data: (String, String) -> Unit = { _, _ -> } ,
    onClickImageToShowControles: () -> Unit
) {
    val lastBonVentAbdelwahab = remember(
        repositorysMainGetter.repo8BonVent.datasValue,
        repositorysMainGetter.repo2Client.datasValue
    ) {
        repositorysMainGetter.getLastBonVentForClient(
            clientKeyID = Jomla_Clients.ECHATILLANTS_KEY_ID,
            etateFilter = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        )
    }

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

    val groupe_Couleur_Par_Produit = remember(list_M3couleur) {
        list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                repositorysMainGetter.repoM1Produit.datasValue.find {
                    it.keyID == productKeyID
                }?.let { product -> product to colors }
            }
            .sortedBy { (product, _) -> product.nom }
    }

    val groupe_Par_Categorie = remember(groupe_Couleur_Par_Produit) {
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
        onClickImageToShowControles = onClickImageToShowControles
    )
}

@Composable
fun Etager_LazyColumn_FragID4(
    modifier: Modifier = Modifier,
    categoriesWithProducts: List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    viewModelHeadViewModel: HeadViewModel,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit
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
            item(
                key = "header_${category.id}",
                span = StaggeredGridItemSpan.FullLine
            ) {
                CategoryStickyHeader(category = category)
            }

            productColorPairs.forEach { (product, colors) ->
                val isExpanded = focusedValuesGetter.active_Central_Values
                    .expanded_M1Produit?.keyID == product.keyID

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
                        on_pour_send_data = on_pour_send_data
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStickyHeader(
    category: CategoriesTabelle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = category.nom,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LazyStigerList_Produits_FragID4(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit
) {
    A_Item_Produit_FragID4(
        relative_M1produit = product,
        on_pour_send_data = on_pour_send_data,
        modifier = modifier
    )
}
