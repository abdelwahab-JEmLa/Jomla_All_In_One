package Application4.App.Fragment.ID1.Fragment

import A_Main.Shared.Views.Dialogs.B.Dialoge.PressistatntMainActivityButtons_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.M16Categorie.Dialog.CategorySelectionDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun A_Compact_Presentoire_App_Produits_App4(
    modifier: Modifier = Modifier,
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
) {
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
            }
        ) {
            val relative_list_m3 = viewModelNewProtoPatterns.active_Datas.list_M03CouleurProduitInfos
            val activeDatas = viewModelNewProtoPatterns.active_Datas
            val outlined_search_Query = activeDatas.filter_echatilaten.trim().lowercase()
            val relative_list_m10_vents =
                activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state

            val mode = activeDatas.filterAffichageMode_Proto

            Main_LazyColumnList_App4(
                modifier,
                relative_list_m3,
                relative_list_m10_vents,
                mode,
                outlined_search_Query,
                Pair(uiState, viewModelNewProtoPatterns),
                { product -> selectedProductForCategoryChange = product },
                justMovedProductKeyID,
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
}
