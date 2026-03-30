package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.Dialogs.PressistatntMainActivityButtons_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.M16Categorie.Dialog.CategorySelectionDialog
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_Compact_Presentoire_App_Produits_App4(
    modifier: Modifier = Modifier,
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns = koinViewModel(),
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    onClickImageToShowControles: () -> Unit = {},
) {
    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    val active_Datas = viewModelNewProtoPatterns.active_Datas

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
        Box() {
            Etager_LazyColumn(
                modifier = modifier,
                on_pour_send_data = on_pour_send_data,
                onProductCategoryClick = { product -> selectedProductForCategoryChange = product },
                justMovedProductKeyID = justMovedProductKeyID,
                uiState_NewProtoPatterns_viewModel = Pair(uiState, viewModelNewProtoPatterns),
            )
            PressistatntMainActivityButtons_App4(viewModelNewProtoPatterns)
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
