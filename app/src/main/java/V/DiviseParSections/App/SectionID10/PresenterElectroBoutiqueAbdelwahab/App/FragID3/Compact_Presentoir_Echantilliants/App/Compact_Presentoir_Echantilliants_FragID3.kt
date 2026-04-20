package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.App

import EntreApps.Shared.Models.Relative_Vents.Models.Jomla_Clients
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoir_Echantilliants_FragID3(
    modifier: Modifier = Modifier,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    FragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    wifiTransferDatas: WifiTransferDatas = koinInject(),
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

