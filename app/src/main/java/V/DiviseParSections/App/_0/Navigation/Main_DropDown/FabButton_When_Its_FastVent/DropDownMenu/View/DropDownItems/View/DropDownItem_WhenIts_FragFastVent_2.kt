package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenIts_FragFastVent_2(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    // Security double count state to prevent multiple concurrent operations
    var isProcessing by remember { mutableStateOf(false) }

    fun updateProductsByPosition() {
        // Security double count check - prevent multiple concurrent operations
        if (isProcessing) {
            Toast.makeText(
                context,
                "Operation already in progress, please wait...",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        isProcessing = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentData = repositorysMainGetter.repo1ProduitInfos.datasValue

                // Get the confectionery catalogue ID (id = 1 based on the repository)
                val confectioneryCatalogueId = B4CatalogueCategoriesRepository()
                    .find { it.nom == "Confiserie" }?.premierCategorieId ?: 1755942577975

                val filteredData = currentData.filter { product ->
                    val parentCategoryBelongsToConfectionery = repositorysMainGetter.repoM16CategorieProduit.datasValue
                        .find { it.id == product.idParentCategorie }
                        ?.catalogueParentId == confectioneryCatalogueId

                    parentCategoryBelongsToConfectionery
                }

                val sortedData = filteredData.sortedWith(
                    compareBy<ArticlesBasesStatsTable> { it.position_store_3jamale }
                        .thenByDescending { it.dernier_timeTamps_position_store_3jamale }
                        .thenBy { it.its_Carton }
                        .thenBy { it.cartonState }
                        .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
                        .thenBy { it.idParentCategorie }
                        .thenBy { it.nom }
                )

                sortedData.forEachIndexed { index, product ->
                    val updatedProduct = product.copy(
                        position_store_3jamale = index + 1,
                        dernier_timeTamps_position_store_3jamale = System.currentTimeMillis(),
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )

                    // Update the product in the repository
                    repositorysMainSetter.upsert_M1Produit(updatedProduct)
                }

                // Show success message
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Successfully updated ${sortedData.size} confectionery products",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Error updating products: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                // Reset security flag regardless of success/failure
                isProcessing = false
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isProcessing) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            enabled = !isProcessing, // Disable when processing
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = if (isProcessing) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            text = {
                Text(
                    text = if (isProcessing) "Processing..." else nomFun,
                    color = if (isProcessing) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                // Execute the product position update functionality
                updateProductsByPosition()

                // Only dismiss dropdown if not processing
                if (!isProcessing) {
                    onDismissDropdown()
                }
            }
        )
    }
}
