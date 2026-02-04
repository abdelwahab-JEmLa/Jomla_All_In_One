package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Z.Dialogs.CategorySelectionDialog_FragID4
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Jomla_Clients
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.Filter.FilterDropdownMenu_Its_FacadeElectroBoutique
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoire_App_Produits_FragID4(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    viewModelHeadViewModel: HeadViewModel,
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    onClickImageToShowControles: () -> Unit
) {
    // FIXED: Get current app compte and catalogue information
    val currentAppCompt = focusedValuesGetter.currentActive_M9AppCompt

    // Get all available catalogues
    val allCatalogues = remember { B4CatalogueCategoriesRepository() }

    // CRITICAL FIX: Extract catalogue KeyID (String) from filter, NOT the numeric ID
    // The filter uses keyID like "t1", "t2", etc.
    val currentCatalogueKeyID = remember(currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId) {
        currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId?.let { filterStr ->
            // Extract catalogue keyID from filter string (format: "catalogue:t1|hide_depot:true|...")
            filterStr.split("|")
                .firstOrNull { it.startsWith("catalogue:") }
                ?.substringAfter("catalogue:")
                ?.takeIf { it.isNotEmpty() }
        } ?: "t4" // Default to "Sans Catalogue" (keyID = "t4")
    }

    // Get the current catalogue object
    val currentCatalogue = remember(currentCatalogueKeyID, allCatalogues) {
        allCatalogues.find { it.keyID == currentCatalogueKeyID }
    }

    // CRITICAL: Get the numeric ID from the catalogue
    // CategoriesTabelle.catalogueParentId expects a Long (numeric ID)
    val currentCatalogueNumericId = remember(currentCatalogue) {
        currentCatalogue?.id?.toLong() ?: 4L
    }

    Log.d("CatalogueFilter_FragID4", """
        |========================================
        |Current Catalogue Filter:
        |Catalogue KeyID (String): $currentCatalogueKeyID
        |Catalogue Numeric ID (Long): $currentCatalogueNumericId
        |Catalogue Name: ${currentCatalogue?.nom ?: "Unknown"}
        |All Catalogues: ${allCatalogues.map { "${it.keyID} (id=${it.id}, nom=${it.nom})" }}
        |========================================
    """.trimMargin())

    // Initialize filter state from focusedValuesGetter or create default
    val filterState = focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique
        ?: FilterState_Facad_Boutique()

    // Sync filter state with app compte on mount and when app compte changes
    LaunchedEffect(currentAppCompt?.keyID) {
        currentAppCompt?.let { appCompt ->
            if (appCompt.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId.isNotEmpty()) {
                Log.d("FilterSync_FragID4", """
                    |Syncing filter from app compte:
                    |App Compte KeyID: ${appCompt.keyID}
                    |Saved Filter: ${appCompt.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId}
                """.trimMargin())
            }
        }
    }

    // Monitor filter changes and persist to app compte
    LaunchedEffect(
        currentCatalogueKeyID,
        filterState.hide_non_couleurAuDepot,
        filterState.searchText,
        filterState.enableCategoryGrouping
    ) {
        currentAppCompt?.let { appCompt ->
            // CRITICAL: Use catalogue KeyID (String) in filter, not numeric ID
            val filterIdentifier = buildString {
                append("catalogue:$currentCatalogueKeyID")
                append("|hide_depot:${filterState.hide_non_couleurAuDepot}")
                append("|search:${filterState.searchText}")
                append("|grouping:${filterState.enableCategoryGrouping}")
            }

            if (appCompt.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId != filterIdentifier) {
                Log.d("FilterSync_FragID4", """
                    |Saving filter state to app compte:
                    |Filter Identifier: $filterIdentifier
                    |Catalogue KeyID: $currentCatalogueKeyID
                    |Catalogue Numeric ID: $currentCatalogueNumericId
                """.trimMargin())

                val updatedAppCompt = appCompt.copy(
                    presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId = filterIdentifier,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                repositorysMainGetter.repo9AppCompt.update(updatedAppCompt)
            }
        }
    }

    val viewModelToUse = categoryViewModel ?: koinInject<EditeBaseDonneMainScreenIdS9ViewModel>()
    val uiState by viewModelToUse.uiState.collectAsState()

    var selectedProductForCategoryChange by remember { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            delay(1500)
            justMovedProductKeyID = null
        }
    }

    // CRITICAL FIX: Filter categories using NUMERIC ID (catalogueParentId is Long)
    val allCategories = remember(
        repositorysMainGetter.repoM16CategorieProduit.datasValue,
        currentCatalogueNumericId
    ) {
        val allCats = repositorysMainGetter.repoM16CategorieProduit.datasValue

        // Filter categories to only show those belonging to the current catalogue
        // IMPORTANT: catalogueParentId is Long, so we compare with numeric ID
        val filteredCats = allCats.filter { category ->
            category.catalogueParentId == currentCatalogueNumericId
        }

        Log.d("CatalogueFilter_FragID4", """
            |========================================
            |Category Filtering Details:
            |Looking for catalogueParentId = $currentCatalogueNumericId
            |Total categories in repo: ${allCats.size}
            |Categories matching this catalogue: ${filteredCats.size}
            |Sample of all categories: ${allCats.take(5).map { "id=${it.id}, nom='${it.nom}', catalogueParentId=${it.catalogueParentId}" }}
            |Filtered categories: ${filteredCats.map { "id=${it.id}, nom='${it.nom}', catalogueParentId=${it.catalogueParentId}" }}
            |========================================
        """.trimMargin())

        filteredCats
    }

    // Filter products that belong to categories of the current catalogue
    val allProducts = remember(
        repositorysMainGetter.repoM1Produit.datasValue,
        allCategories
    ) {
        val categoryIds = allCategories.map { it.id }.toSet()
        val products = repositorysMainGetter.repoM1Produit.datasValue

        val filteredProducts = products.filter { product ->
            product.idParentCategorie in categoryIds
        }

        Log.d("CatalogueFilter_FragID4", """
            |========================================
            |Product Filtering Details:
            |Category IDs in current catalogue: $categoryIds
            |Total products in repo: ${products.size}
            |Products in categories of catalogue ${currentCatalogue?.nom}: ${filteredProducts.size}
            |Sample products: ${filteredProducts.take(5).map { "nom='${it.nom}', categoryId=${it.idParentCategorie}" }}
            |========================================
        """.trimMargin())

        filteredProducts
    }

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

    // Filter colors: first by catalogue, then by depot stock if enabled
    val list_M3couleur = remember(
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue,
        operationsFromLastBon,
        filterState.hide_non_couleurAuDepot,
        allProducts
    ) {
        val allCouleurs = repositorysMainGetter.repo03CouleurProduitInfos.datasValue

        // First filter: only colors of products in current catalogue
        val allowedProductIds = allProducts.map { it.keyID }.toSet()
        val couleursInCatalogue = allCouleurs.filter { couleur ->
            couleur.parentBProduitInfosKeyID in allowedProductIds
        }

        Log.d("FilterDebug_FragID4", "Colors in catalogue $currentCatalogueNumericId (${currentCatalogue?.nom}): ${couleursInCatalogue.size}")

        // Second filter: depot stock if enabled
        val filteredCouleurs = if (filterState.hide_non_couleurAuDepot) {
            couleursInCatalogue.filter { couleur ->
                couleur.count_Don_Depot > 0
            }
        } else {
            couleursInCatalogue
        }

        Log.d("FilterDebug_FragID4", """
            |========================================
            |Filter State Analysis:
            |Catalogue KeyID: $currentCatalogueKeyID
            |Catalogue Numeric ID: $currentCatalogueNumericId
            |Catalogue Name: ${currentCatalogue?.nom}
            |hide_non_couleurAuDepot = ${filterState.hide_non_couleurAuDepot}
            |Total couleurs in repo: ${allCouleurs.size}
            |Couleurs in catalogue: ${couleursInCatalogue.size}
            |After depot filter: ${filteredCouleurs.size}
            |Couleurs with depot > 0: ${couleursInCatalogue.count { it.count_Don_Depot > 0 }}
            |Couleurs with depot = 0: ${couleursInCatalogue.count { it.count_Don_Depot == 0 }}
            |========================================
        """.trimMargin())

        filteredCouleurs.sortedByDescending { it.creationTimestamp }
    }

    val groupe_Couleur_Par_Produit = remember(
        list_M3couleur,
        allProducts,
        filterState.searchText,
    ) {
        val grouped = list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                allProducts.find { it.keyID == productKeyID }?.let { product ->
                    val matchesSearch = if (filterState.searchText.isNotEmpty()) {
                        product.nom.contains(filterState.searchText, ignoreCase = true)
                    } else {
                        true
                    }

                    if (matchesSearch) product to colors else null
                }
            }
            .filterNotNull()
            .sortedBy { (product, _) -> product.nom }

        Log.d("FilterDebug_FragID4", """
            |Products grouped by color:
            |Total products with colors: ${grouped.size}
            |Search text: "${filterState.searchText}"
        """.trimMargin())

        grouped
    }

    val groupe_Par_Categorie = remember(
        groupe_Couleur_Par_Produit,
        allCategories,
        filterState.enableCategoryGrouping
    ) {
        val result = if (filterState.enableCategoryGrouping) {
            groupe_Couleur_Par_Produit.groupBy { (product, _) -> product.idParentCategorie }
                .mapNotNull { (categoryId, productColorPairs) ->
                    allCategories.find { it.id == categoryId }?.let { category ->
                        category to productColorPairs
                    }
                }
                .sortedBy { (category, _) -> category.positionDouble }
        } else {
            val ungroupedCategory = CategoriesTabelle(
                id = -1,
                nom = "Tous les produits - ${currentCatalogue?.nom ?: "Catalogue"}",
                position = 0,
                positionDouble = 0.0,
                displayedHeader = false,
                catalogueParentId = currentCatalogueNumericId
            )
            listOf(ungroupedCategory to groupe_Couleur_Par_Produit)
        }

        Log.d("FilterDebug_FragID4", """
            |Category grouping:
            |enableCategoryGrouping = ${filterState.enableCategoryGrouping}
            |Total categories: ${result.size}
            |Total products across all categories: ${result.sumOf { it.second.size }}
        """.trimMargin())

        result
    }

    Etager_LazyColumn_FragID4(
        modifier = modifier,
        categoriesWithProducts = groupe_Par_Categorie,
        viewModelHeadViewModel = viewModelHeadViewModel,
        on_pour_send_data = on_pour_send_data,
        onClickImageToShowControles = onClickImageToShowControles,
        onProductCategoryClick = { product ->
            Log.d("CategoryDialog_FragID4", "onProductCategoryClick called for: ${product.nom}")
            selectedProductForCategoryChange = product
        },
        justMovedProductKeyID = justMovedProductKeyID,
        repositorysMainGetter = repositorysMainGetter
    )

    focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique?.affiche_dialog_editeur?.ifTrue {
        FilterDropdownMenu_Its_FacadeElectroBoutique(
            onDismiss = {
                focusedValuesGetter.update_activeCentralValues(
                    focusedValuesGetter.active_Central_Values.copy(
                        filterState_Facad_Boutique = focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique
                            ?.copy(
                                affiche_dialog_editeur = false
                            )
                    )
                )
            }
        )
    }

    selectedProductForCategoryChange?.let { product ->
        Log.d("CategoryDialog_FragID4", "Displaying CategorySelectionDialog for: ${product.nom}")
        CategorySelectionDialog_FragID4(
            product = product,
            allCategories = allCategories,
            allProducts = allProducts,
            isFastMoveMode = uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.FastMove,
            onCategorySelected = { newCategoryId ->
                Log.d("CategoryDialog_FragID4", "Category selected: $newCategoryId")
                val updatedProduct = newCategoryId?.let {
                    product.copy(
                        idParentCategorie = it,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                } ?: product

                repositorysMainGetter.repoM1Produit.update(updatedProduct)
                justMovedProductKeyID = product.keyID

                selectedProductForCategoryChange = null
            },
            onDismiss = {
                Log.d("CategoryDialog_FragID4", "Dialog dismissed")
                selectedProductForCategoryChange = null
            },
            onCreateNewCategory = { categoryName ->
                Log.d("CategoryDialog_FragID4", "Creating new category: $categoryName for catalogue $currentCatalogueKeyID (numeric ID: $currentCatalogueNumericId)")
                viewModelToUse.addOrUpdateCategorie(
                    CategoriesTabelle(
                        nom = categoryName,
                        position = 0,
                        catalogueParentId = currentCatalogueNumericId // Use numeric ID
                    )
                )
            },
            onUpdateCategoryName = { categoryId, newName ->
                Log.d("CategoryDialog_FragID4", "Updating category $categoryId to: $newName")

                allCategories.find { it.id == categoryId }?.let { category ->
                    val updatedCategory = category.copy(nom = newName)
                    repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(updatedCategory)
                }
            }
        )
    }
}
