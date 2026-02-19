package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.FilterState_Facad_Boutique
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.FilterTunnel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.GroupTunnel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu_WhenIts_FacadeBoutiqueElectro(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val currentFilterState = activeCentralValues.filterState_Facad_Boutique ?: FilterState_Facad_Boutique()
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    // Generate groupe_Par_Catalogue exactly like Compact_Presentoire_App_Produits_FragID4 does
    val allCategories = repositorysMainGetter.repoM16CategorieProduit.datasValue
    val allProducts = repositorysMainGetter.repoM1Produit.datasValue
    val allColors = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
        .sortedByDescending { it.creationTimestamp }

    val groupe_Par_Catalogue = GroupTunnel(
        allColors = allColors,
        allProducts = allProducts,
        allCategories = allCategories
    )

    Box(modifier = modifier) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
        ) {

            // Button: open filter/sort dialog
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Text(
                        text = "Filtres et tri",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            filterState_Facad_Boutique = currentFilterState.copy(
                                affiche_dialog_editeur = true
                            )
                        )
                    )
                    onDismissDropdown()
                }
            )

            // Button: upload filtered products to Firestore (replaces Model01Produit/Datas)
            DropdownMenuItem(
                leadingIcon = {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                text = {
                    Text(
                        text = if (isUploading) "Envoi en cours..." else "Envoyer produits filtrés → Firestore",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                enabled = !isUploading,
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isUploading = true
                        uploadFilteredProduitsToFirestore(
                            groupe_Par_Catalogue = groupe_Par_Catalogue,
                            filterState = currentFilterState,
                            catalogueFilter = focusedValuesGetter.currentActive_M9AppCompt
                                ?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId
                        )
                        isUploading = false
                        onDismissDropdown()
                    }
                }
            )
        }
    }
}

/**
 * Deletes all documents in Firestore Model01Produit/Datas,
 * then uploads only the currently filtered products — using batched writes (lignes).
 * Firestore batches are limited to 500 operations each, so we chunk automatically.
 */
private suspend fun uploadFilteredProduitsToFirestore(
    groupe_Par_Catalogue: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>,
    filterState: FilterState_Facad_Boutique,
    catalogueFilter: String?,
) {
    val firestore = Firebase.firestore
    val targetCollection = ArticlesBasesStatsTable.refFirestore

    // Step 1: Delete all existing documents in batches of 500
    val existingDocs = targetCollection.get().await()
    existingDocs.documents.chunked(500).forEach { chunk ->
        val deleteBatch = firestore.batch()
        chunk.forEach { doc -> deleteBatch.delete(doc.reference) }
        deleteBatch.commit().await()
    }

    // Step 2: Filter products using the same FilterTunnel as the UI
    val filteredCatalogues = FilterTunnel(
        groupe_Par_Catalogue = groupe_Par_Catalogue,
        catalogueFilter = catalogueFilter,
        filterState = filterState
    )

    // Step 3: Collect all filtered products
    val allFilteredProducts = filteredCatalogues.flatMap { (_, categories) ->
        categories.flatMap { (_, productColorPairs) ->
            productColorPairs.map { (product, _) -> product }
        }
    }

    // Step 4: Upload in batches of 500
    allFilteredProducts.chunked(500).forEach { chunk ->
        val writeBatch = firestore.batch()
        chunk.forEach { product ->
            val key = product.keyFireBase.ifEmpty { product.keyID }
            writeBatch.set(targetCollection.document(key), product.toFirebaseMap())
        }
        writeBatch.commit().await()
    }
}
