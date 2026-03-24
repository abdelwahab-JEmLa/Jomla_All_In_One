package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.FilterState_Facad_Boutique_FragId5
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.FilterTunnel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.GroupTunnel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Construction
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
    onClickImageToShowControles: () -> Unit,
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val filterState_Facad_Boutique_FragId5 = activeCentralValues.filterState_Facad_Boutique_FragId5 ?: FilterState_Facad_Boutique_FragId5()
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
                            filterState_Facad_Boutique_FragId5 = filterState_Facad_Boutique_FragId5.copy(
                                affiche_dialog_editeur = true
                            )
                        )
                    )
                    onDismissDropdown()
                }
            )

            Fab_Stigns(onClickImageToShowControles, onDismissDropdown)

            // Button: upload filtered products, their colors, and their categories to Firestore
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
                        uploadFilteredDataToFirestore(
                            groupe_Par_Catalogue = groupe_Par_Catalogue,
                            filterState = filterState_Facad_Boutique_FragId5,
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

@Composable
fun Fab_Stigns(
    onClickImageToShowControles: () -> Unit,
    onDismissDropdown: () -> Unit ,
    focusedValuesGetter: FocusedValuesGetter =koinInject()
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = "onClickImageToShowControles",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = {
            onClickImageToShowControles()
            onDismissDropdown()
            focusedValuesGetter.update_oneMutableStateLesseRessources(true)
        }
    )
}

/**
 * Deletes all documents in Firestore for products, their related colors, and their related
 * categories, then re-uploads only the data visible after the current filters are applied.
 *
 * Each entity type is handled independently with batched writes (max 500 ops per batch).
 *
 * Collections synced:
 *  - ArticlesBasesStatsTable.refFirestore  (products)
 *  - M3CouleurProduitInfos.refFirestore    (colors whose parent product passes the filter)
 *  - M16CategorieProduit.refFirestore      (categories that contain at least one filtered product)
 */
private suspend fun uploadFilteredDataToFirestore(
    groupe_Par_Catalogue: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    filterState: FilterState_Facad_Boutique_FragId5,
    catalogueFilter: String?,
) {
    val firestore = Firebase.firestore

    // ── Step 1: Apply the same filter as the UI ────────────────────────────────
    val filteredCatalogues = FilterTunnel(
        groupe_Par_Catalogue = groupe_Par_Catalogue,
        catalogueFilter = catalogueFilter,
        filterState = filterState
    )

    // ── Step 2: Collect the three entity sets from the filtered hierarchy ──────
    val filteredProducts  = mutableListOf<M01Produit>()
    val filteredColors    = mutableListOf<M3CouleurProduitInfos>()
    val filteredCategories = mutableListOf<M16CategorieProduit>()

    filteredCatalogues.forEach { (_, categories) ->
        categories.forEach { (category, productColorPairs) ->
            // Each category that survives the filter is included once
            if (filteredCategories.none { it.id == category.id }) {
                filteredCategories += category
            }
            productColorPairs.forEach { (product, colors) ->
                filteredProducts  += product
                filteredColors    += colors   // only colors belonging to a passing product
            }
        }
    }

    // ── Step 3: Delete → re-upload helper ─────────────────────────────────────
    suspend fun <T> syncCollection(
        collectionRef: com.google.firebase.firestore.CollectionReference,
        items: List<T>,
        keyOf: (T) -> String,
        mapOf: (T) -> Map<String, Any?>,
    ) {
        // Delete all existing docs
        collectionRef.get().await().documents
            .chunked(500)
            .forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { batch.delete(it.reference) }
                batch.commit().await()
            }

        // Upload filtered items in batches of 500
        items.chunked(500).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { item ->
                batch.set(collectionRef.document(keyOf(item)), mapOf(item))
            }
            batch.commit().await()
        }
    }

    // ── Step 4: Sync each collection ──────────────────────────────────────────

    // Products
    syncCollection(
        collectionRef = M01Produit.refFirestore,
        items = filteredProducts,
        keyOf = { product -> product.keyFireBase.ifEmpty { product.keyID } },
        mapOf = { product -> product.toFirebaseMap() }
    )

    // Colors  (only those whose parent product is in the filtered set)
    syncCollection(
        collectionRef = M3CouleurProduitInfos.refFirestore,
        items = filteredColors,
        keyOf = { color -> color.keyID },
        mapOf = { color -> color.toFirebaseMap() }
    )

    // Categories  (only those that contain at least one filtered product)
    syncCollection(
        collectionRef = M16CategorieProduit.refFirestore,
        items = filteredCategories,
        keyOf = { category -> category.keyID },
        mapOf = { category -> category.toFirebaseMap() }
    )
}
