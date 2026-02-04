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
    val currentAppCompt = focusedValuesGetter.currentActive_M9AppCompt

    // Initialize filter state from focusedValuesGetter or create default
    val filterState = focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique
        ?: FilterState_Facad_Boutique()

    // Sync filter state with app compte on mount and when app compte changes
    LaunchedEffect(currentAppCompt?.keyID) {
        currentAppCompt?.let { appCompt ->
            // If app compte has a saved filter ID, ensure it's reflected in the filter state
            if (appCompt.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId.isNotEmpty()) {
                Log.d("FilterSync_FragID4", """
                    |Syncing filter from app compte:
                    |App Compte KeyID: ${appCompt.keyID}
                    |Saved Filter ID: ${appCompt.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId}
                """.trimMargin())

                // You can add logic here to restore filter state from the saved ID
                // For example, if the filter ID represents a specific category or configuration
            }
        }
    }

    // Monitor filter changes and persist to app compte
    LaunchedEffect(
        filterState.hide_non_couleurAuDepot,
        filterState.searchText,
        filterState.enableCategoryGrouping
    ) {
        currentAppCompt?.let { appCompt ->
            // Create a unique filter identifier based on current filter state
            val filterIdentifier = buildString {
                append("hide_depot:${filterState.hide_non_couleurAuDepot}")
                append("|search:${filterState.searchText}")
                append("|grouping:${filterState.enableCategoryGrouping}")
            }

            // Only update if the filter has actually changed
            if (appCompt.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId != filterIdentifier) {
                Log.d("FilterSync_FragID4", """
                    |Saving filter state to app compte:
                    |Filter Identifier: $filterIdentifier
                    |hide_non_couleurAuDepot: ${filterState.hide_non_couleurAuDepot}
                    |searchText: ${filterState.searchText}
                    |enableCategoryGrouping: ${filterState.enableCategoryGrouping}
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

    val allCategories = remember(repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        repositorysMainGetter.repoM16CategorieProduit.datasValue
    }

    val allProducts = remember(repositorysMainGetter.repoM1Produit.datasValue) {
        repositorysMainGetter.repoM1Produit.datasValue
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

    // SIMPLIFIED LOGIC:
    // hide_non_couleurAuDepot = false → Show ALL products (1882 colors)
    // hide_non_couleurAuDepot = true  → Show only products with depot stock
    val list_M3couleur = remember(
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue,
        operationsFromLastBon,
        filterState.hide_non_couleurAuDepot
    ) {
        val allCouleurs = repositorysMainGetter.repo03CouleurProduitInfos.datasValue

        Log.d("FilterDebug_FragID4", "Total colors in repo: ${allCouleurs.size}")

        // Apply filter based on hide_non_couleurAuDepot
        val filteredCouleurs = if (filterState.hide_non_couleurAuDepot) {
            // When TRUE: Filter by depot stock (hide items without stock)
            allCouleurs.filter { couleur ->
                couleur.count_Don_Depot > 0
            }
        } else {
            // When FALSE: Show ALL products from repo
            allCouleurs
        }

        Log.d("FilterDebug_FragID4", """
            |========================================
            |Filter State Analysis:
            |hide_non_couleurAuDepot = ${filterState.hide_non_couleurAuDepot}
            |Total couleurs in repo: ${allCouleurs.size}
            |After depot filter: ${filteredCouleurs.size}
            |Couleurs with depot > 0: ${allCouleurs.count { it.count_Don_Depot > 0 }}
            |Couleurs with depot = 0: ${allCouleurs.count { it.count_Don_Depot == 0 }}
            |In operations: ${allCouleurs.count { c -> operationsFromLastBon.any { it.parent_M3CouleurProduit_KeyID == c.keyID } }}
            |Not in operations: ${allCouleurs.count { c -> operationsFromLastBon.none { it.parent_M3CouleurProduit_KeyID == c.keyID } }}
            |========================================
        """.trimMargin())

        // Sort by creation timestamp
        filteredCouleurs.sortedByDescending { it.creationTimestamp }
    }

    val groupe_Couleur_Par_Produit = remember(
        list_M3couleur,
        repositorysMainGetter.repoM1Produit.datasValue,
        filterState.searchText,
    ) {
        val grouped = list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                repositorysMainGetter.repoM1Produit.datasValue.find {
                    it.keyID == productKeyID
                }?.let { product ->
                    // Apply search filter (empty search shows all)
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

    // Category grouping logic
    val groupe_Par_Categorie = remember(
        groupe_Couleur_Par_Produit,
        repositorysMainGetter.repoM16CategorieProduit.datasValue,
        filterState.enableCategoryGrouping
    ) {
        val result = if (filterState.enableCategoryGrouping) {
            groupe_Couleur_Par_Produit.groupBy { (product, _) -> product.idParentCategorie }
                .mapNotNull { (categoryId, productColorPairs) ->
                    repositorysMainGetter.repoM16CategorieProduit.datasValue.find {
                        it.id == categoryId
                    }?.let { category -> category to productColorPairs }
                }
                .sortedBy { (category, _) -> category.positionDouble }
        } else {
            val ungroupedCategory = CategoriesTabelle(
                id = -1,
                nom = "Tous les produits",
                position = 0,
                positionDouble = 0.0,
                displayedHeader = false
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
                Log.d("CategoryDialog_FragID4", "Creating new category: $categoryName")
                viewModelToUse.addOrUpdateCategorie(
                    CategoriesTabelle(
                        nom = categoryName,
                        position = 0,
                        catalogueParentId = 4
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
