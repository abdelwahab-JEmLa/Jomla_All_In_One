package Application4.App.Fragment.ID1.Fragment

import A_Main.Shared.Views.Dialogs.B.Dialoge.PressistatntMainActivityButtons_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.M16Categorie.Dialog.CategorySelectionDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay

@Composable
fun A_Compact_Presentoire_App_Produits_App4(
    modifier: Modifier = Modifier,
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    appDatabase: AppDatabase,
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
) {
    val context = LocalContext.current
    val viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns =
        viewModel(
            factory = viewModelFactory {
                initializer {
                    A_ViewModel_NewProtoPatterns(
                        wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                        context = context,
                        appDatabase = appDatabase,
                        fragmentNavigationHandler = fragmentNavigationHandler,
                    )
                }
            }
        )

    // FIX TODO(1): On every entry to this screen (including navigating back to it),
    // reset the progress state and reload all data so the ViewModel is always fresh.
    // Combined with `remember` (not rememberSaveable) for initDone in AppNavHost_App4,
    // this guarantees a full reload cycle on each navigation.
    LaunchedEffect(Unit) {
        viewModelNewProtoPatterns.retryLoadingData()
    }

    var showFabDropdown_Compact_Presentoire_App_Produits_FragID4 by remember { mutableStateOf(false) }

    val active_Datas = viewModelNewProtoPatterns.active_Datas

    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    val allCategories: List<M16CategorieProduit>? by remember {
        derivedStateOf {
            active_Datas.list_M16CategorieProduit
                ?.takeIf { it.isNotEmpty() }
        }
    }

    val allProducts: List<M01Produit>? by remember {
        derivedStateOf { active_Datas.list_M1Produit }
    }

    var selectedProductForCategoryChange by remember { mutableStateOf<M01Produit?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }
    var hasRetriedLoading by remember { mutableStateOf(false) }

    // Retry loading data if initialization is done but no items are displayed
    LaunchedEffect(isInitDone, allProducts) {
        if (isInitDone && !hasRetriedLoading && allProducts.isNullOrEmpty()) {
            delay(6000)
            if (allProducts.isNullOrEmpty()) {
                hasRetriedLoading = true
                viewModelNewProtoPatterns.retryLoadingData()
            }
        }
    }

    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            delay(1500)
            justMovedProductKeyID = null
        }
    }

    if (!isInitDone) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.initDatasProgressEtate },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Box(
            modifier = Modifier.semantics(mergeDescendants = true) {
                set(value = active_Datas.list_M03CouleurProduitInfos?.find {
                    it.keyID == "-OWDMGKsnReAaqOHO5iH"
                }, key = SemanticsPropertyKey(""))

                set(value = active_Datas.list_M03CouleurProduitInfos?.size, key = SemanticsPropertyKey("size"))

                set(
                    value = active_Datas.active_M9Compt?.onVentM8BonVentDebugInfos,
                    key = SemanticsPropertyKey("onVentM8BonVentDebugInfos")
                )
                set(
                    value = active_Datas.active_M9Compt?.onVentM8BonVentKey,
                    key = SemanticsPropertyKey("onVentM8BonVentKey")
                )
            }
        ) {
            Main_LazyColumnList_App4(
                modifier = modifier,
                uiState_NewProtoPatterns_viewModel = Pair(uiState, viewModelNewProtoPatterns),
                onProductCategoryClick = { product -> selectedProductForCategoryChange = product },
                justMovedProductKeyID = justMovedProductKeyID,
                on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
                mode = active_Datas.filterAffichageMode_Proto,
                ventCouleurs = active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
                relative_m3_Couleurs = active_Datas.list_M03CouleurProduitInfos,
            )

            PressistatntMainActivityButtons_App4(viewModelNewProtoPatterns)

            FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(
                appDatabase = viewModelNewProtoPatterns.appDatabase
            )
        }
    }

    selectedProductForCategoryChange?.let { product ->
        CategorySelectionDialog(
            product = product,
            allCategories = allCategories,
            allProducts = allProducts,
            isFastMoveMode = true,
            onCategorySelected = { newCategoryId ->
                val updatedProduct = newCategoryId?.let {
                    product.copy(
                        idParentCategorie = it,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                } ?: product
                viewModelNewProtoPatterns.update_m1Produit(updatedProduct)
                justMovedProductKeyID = product.keyID
                selectedProductForCategoryChange = null
            },
            onDismiss = {
                selectedProductForCategoryChange = null
            },
            onCreateNewCategory = { categoryName ->
                val newId = System.currentTimeMillis()
                val data = M16CategorieProduit(
                    id = newId,
                    nom = categoryName,
                    position = 0,
                    catalogueParentId = 4
                )
                viewModelNewProtoPatterns.insert_M16CategorieProduit(data)
                val updatedProduct = product.copy(
                    idParentCategorie = newId,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                viewModelNewProtoPatterns.update_m1Produit(updatedProduct)
                justMovedProductKeyID = product.keyID
                selectedProductForCategoryChange = null
            },
            onUpdateCategoryName = { categoryId, newName ->
                allCategories?.find { it.id == categoryId }?.let { category ->
                    viewModelNewProtoPatterns.update_m16CategorieProduit(category.copy(nom = newName))
                }
            }
        )
    }
    if (showFabDropdown_Compact_Presentoire_App_Produits_FragID4) {
        FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4(
            viewModelNewProtoPatterns = viewModelNewProtoPatterns,
            onDismissDropdown = {
                showFabDropdown_Compact_Presentoire_App_Produits_FragID4 = false
            },
        )
    }
}
