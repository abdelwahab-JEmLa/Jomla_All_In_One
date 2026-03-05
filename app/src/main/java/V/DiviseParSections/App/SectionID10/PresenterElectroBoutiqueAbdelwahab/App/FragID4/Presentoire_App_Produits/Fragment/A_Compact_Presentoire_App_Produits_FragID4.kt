package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.ViewModel_NewProtoPatterns
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.FilterSortGroupe_Tunnels
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.GroupTunnel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Z.Components.Dialogs.CategorySelectionDialog_FragID4

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun Compact_Presentoire_App_Produits_FragID4(
    modifier: Modifier = Modifier,
    viewModelNewProtoPatterns: ViewModel_NewProtoPatterns = koinViewModel(),
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    onClickImageToShowControles: () -> Unit,
    isWifiClientConnected_1: Boolean,
) {

    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    var selectedProductForCategoryChange by remember { mutableStateOf<M01Produit?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            delay(1500)
            justMovedProductKeyID = null
        }
    }

    val allCategories = remember(uiState.list_Datas?.m16CategorieProduit) {
        uiState.list_Datas?.m16CategorieProduit
    }

    val allProducts = remember(uiState.list_Datas?.m1Produit) {
        uiState.list_Datas?.m1Produit
    }

    val allColors = remember(uiState.list_Datas?.m3CouleurProduit) {
        uiState.list_Datas?.m3CouleurProduit
            ?.sortedByDescending { it.creationTimestamp }
    }

    val groupe_Par_Catalogue = GroupTunnel(
        allColors = allColors,
        allProducts = allProducts,
        allCategories = allCategories
    )

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
        FilterSortGroupe_Tunnels(
            uiState = uiState,
            viewModel = viewModelNewProtoPatterns,
            isWifiClientConnected_1 = isWifiClientConnected_1,
            modifier = modifier,
            groupe_Par_Catalogue = groupe_Par_Catalogue,
            on_pour_send_data = on_pour_send_data,
            onClickImageToShowControles = onClickImageToShowControles,
            onProductCategoryClick = { product ->
                selectedProductForCategoryChange = product
            },
            justMovedProductKeyID = justMovedProductKeyID
        )
    }

    selectedProductForCategoryChange?.let { product ->
        CategorySelectionDialog_FragID4(
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

                // Use ViewModel method so local state stays in sync too
                viewModelNewProtoPatterns.update_m1Produit(updatedProduct)
                justMovedProductKeyID = product.keyID

                selectedProductForCategoryChange = null
            },
            onDismiss = {
                selectedProductForCategoryChange = null
            },
            onCreateNewCategory = { categoryName ->
                viewModelNewProtoPatterns.repositorysMainSetter_NewProtoPatterns.insert_M16CategorieProduit(
                    M16CategorieProduit(
                        nom = categoryName,
                        position = 0,
                        catalogueParentId = 4
                    )
                )
            },
            onUpdateCategoryName = { categoryId, newName ->
                allCategories?.find { it.id == categoryId }?.let { category ->
                    val updatedCategory = category.copy(nom = newName)
                    viewModelNewProtoPatterns.update_m16CategorieProduit(updatedCategory)
                }
            }
        )
    }
}
