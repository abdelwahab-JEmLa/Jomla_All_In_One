package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.App

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.Item_Produit_FragID3
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.Components.Jomla_Clients
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import android.view.WindowManager
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import V.DiviseParSections.App.Shared.ViewModel.UiState
import org.koin.compose.koinInject

fun get_isWifiClientConnected_by_head_vm(uiState: UiState): Boolean =
    !uiState.productDisplayController.isHostPhone &&  uiState.productDisplayController.isConnected

@Composable
fun Compact_Presentoir_Echantilliants_FragID3(
    modifier: Modifier = Modifier,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    FragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    wifiTransferDatas: WifiTransferDatas = koinInject(),
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    headViewModel: HeadViewModel = koinInject(),
    isWifiClientConnected_1: Boolean
) {
    val uiState by headViewModel.uiState.collectAsState()
    val context = LocalContext.current

    DisposableEffect(isWifiClientConnected_1) {
        val window = (context as? ComponentActivity)?.window

        if (isWifiClientConnected_1 && window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val viewModelToUse = categoryViewModel ?: koinInject<EditeBaseDonneMainScreenIdS9ViewModel>()
    var selectedProductForCategoryChange by remember { mutableStateOf<M01Produit?>(null) }

    val allCategories = remember(repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        repositorysMainGetter.repoM16CategorieProduit.datasValue
    }

    val categoryMap = remember(allCategories) {
        allCategories.associateBy { it.id }
    }

    val catalogues = remember { get_ListM21CataloguesCategorie() }

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
        operationsFromLastBon
    ) {
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue.filter { couleur ->
            val hasStock = couleur.count_Don_Depot > 0

            val produit = repositorysMainGetter.repoM1Produit.datasValue.find {
                it.keyID == couleur.parentBProduitInfosKeyID
            }

            val isInOperations = operationsFromLastBon.any { operation ->
                operation.parent_M3CouleurProduit_KeyID == couleur.keyID
            }

            val isAvailable = produit?.disponibilityEtates == DisponibilityEtates.DISPO

            hasStock && isInOperations && isAvailable
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

    Etager_LazyColumn_FragID3(
        isWifiClientConnected_1=isWifiClientConnected_1,
        modifier = modifier,
        categoriesWithProducts = groupe_Par_Categorie,
        fragmentNavigationHandler = FragmentNavigationHandler,
        catalogues = catalogues,
        categoryMap = categoryMap,
        onProductCategoryClick = { product ->
            selectedProductForCategoryChange = product
        },
        on_pour_send_data = on_pour_send_data,
    )

    focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    selectedProductForCategoryChange?.let { product ->
        CategorySelectionDialog(
            viewModel = viewModelToUse,
            product = product,
            onCategorySelected = { newCategoryId ->
                val updatedProduct = newCategoryId?.let {
                    product.copy(idParentCategorie = it)
                }
                updatedProduct?.let {
                    repositorysMainGetter.repo1ProduitInfos.upsert(it)
                }
                selectedProductForCategoryChange = null
            },
            onDismiss = {
                selectedProductForCategoryChange = null
            },
            onUpdateCategory = { categoryId, newName ->
                val categoryToUpdate = categoryMap[categoryId]
                categoryToUpdate?.let {
                    val updated = it.copy(nom = newName)
                    viewModelToUse.addOrUpdateCategorie(updated)
                }
            },
            categoriesMap = categoryMap,
            availableCategories = allCategories.map { it.id }
        )
    }
}

@Composable
fun Etager_LazyColumn_FragID3(
    modifier: Modifier = Modifier,
    categoriesWithProducts: List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
    catalogues: List<M21CataloguesCategorie>,
    categoryMap: Map<Long, M16CategorieProduit>,
    onProductCategoryClick: (M01Produit) -> Unit,
    on_pour_send_data: (String, String) -> Unit,
    isWifiClientConnected_1: Boolean
) {
    val gridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(4),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        categoriesWithProducts.forEach { (category, productColorPairs) ->
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
                    ProductItemWithCategory(
                        isWifiClientConnected_1=isWifiClientConnected_1,
                        product = product,
                        colors = colors,
                        categoryMap = categoryMap,
                        catalogues = catalogues,
                        onProductCategoryClick = onProductCategoryClick,
                        on_pour_send_data = on_pour_send_data
                    )
                }
            }
        }

        item(
            key = "navigation_button",
            span = StaggeredGridItemSpan.FullLine
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        fragmentNavigationHandler.navigateTo(
                            Screen.Compact_Presentoire_App_Produits_FragID4,
                            FragmentNavigationHandler.DEFAULT_CONFIG
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "View All Products",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItemWithCategory(
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    categoryMap: Map<Long, M16CategorieProduit>,
    catalogues: List<M21CataloguesCategorie>,
    onProductCategoryClick: (M01Produit) -> Unit,
    on_pour_send_data: (String, String) -> Unit,
    isWifiClientConnected_1: Boolean
) {
    val currentCategory = remember(product.idParentCategorie, categoryMap) {
        product.idParentCategorie?.let { categoryMap[it] }
    }

    val currentCatalogue = remember(currentCategory, catalogues) {
        currentCategory?.catalogueParentId?.let { catalogueId ->
            catalogues.find { it.id.toLong() == catalogueId }
        }
    }

    LazyStigerList_Produits_FragID3(
        isWifiClientConnected_1=isWifiClientConnected_1,
        product = product,
        colors = colors,
        on_pour_send_data = on_pour_send_data,
        onCategoryClick = {
            onProductCategoryClick(product)
        }
    )
}

@Composable
fun LazyStigerList_Produits_FragID3(
    modifier: Modifier = Modifier,
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null,
    isWifiClientConnected_1: Boolean
) {
    val isExpanded = focusedValuesGetter.active_Central_Values
        .expanded_M1Produit?.keyID == product.keyID

    Item_Produit_FragID3(
        isWifiClientConnected_1=isWifiClientConnected_1,
        relative_M1produit = product,
        on_pour_send_data = on_pour_send_data,
        modifier = modifier,
        onCategoryClick = onCategoryClick,
    )
}
