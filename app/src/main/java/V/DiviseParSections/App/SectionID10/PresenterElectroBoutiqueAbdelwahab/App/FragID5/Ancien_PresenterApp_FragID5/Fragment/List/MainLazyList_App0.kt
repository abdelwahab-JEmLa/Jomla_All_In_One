package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.Jomla_Clients
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.CategoryStickyHeader
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List.View.Reset_its_pour_affiche_au_presenter
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.List.View.Updated_list_m3couleurs_Affichable_Au_Presenters
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.Item_Produit_FragID5
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Z.Components.Modules.HandlePresenterClientScroll
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Z.Components.Modules.HandlePresenterScrollBroadcast
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Etager_LazyColumn_App0(
    modifier: Modifier = Modifier.Companion,
    cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    viewModelHeadViewModel: HeadViewModel,

    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    repositorysMainGetter: RepositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    isWifiClientConnected_1: Boolean,
) {
    val allColors = cataloguesWithCategoriesAndProducts
        .flatMap { (_, cats) ->
            cats.flatMap { (_, products) ->
                products.flatMap { (_, colors) ->
                    colors
                }
            }
        }

    val parentProduit_Classement: Map<String, Int> =
        cataloguesWithCategoriesAndProducts
            .flatMap { (_, cats) -> cats }
            .flatMap { (_, productColorPairs) -> productColorPairs }
            .sortedBy { (_, colors) -> if (colors.any { it.its_in_echantiallants == true }) 0 else 1 }
            .mapIndexed { index, (product, _) -> product.keyID to index }
            .toMap()
            .also { map ->
                Log.d(
                    "parentProduit_Classement",
                    "✅ ${map.size} products indexed by display position | " +
                            "sample: ${
                                map.entries.take(3)
                                    .joinToString { "(${it.key.takeLast(6)} → ${it.value})" }
                            }"
                )
            }

    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModelHeadViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isHostPhone = uiState.productDisplayController.isHostPhone
    val isConnected = uiState.productDisplayController.isConnected
    val currentScrollPosition = uiState.productDisplayController.mainGridScrollPosition

    val tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    val isScrollEnabled = isHostPhone || !isConnected

    val expanded_M3CouleurProduitInfos =
        focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    var showUploadDropdown by remember { mutableStateOf(false) }

    // Colours that are currently on a Jomla-ECHATILLANTS bon de vente
    val jomlaEchatillantsCouleurKeyIDs: Set<String> = remember(
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.size,
        focusedValuesGetter.activeOnVent_M8BonVent?.keyID,
        allColors.size
    ) {
        // FIXED: Added debug log to verify this block executes
        Log.d("ECHATILLANTS_DEBUG", "🔄 Computing jomlaEchatillantsCouleurKeyIDs...")

        val isJomlaClientBon = focusedValuesGetter.activeOnVent_M8BonVent
            ?.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID

        // FIXED: Added log to show bon type
        Log.d(
            "ECHATILLANTS_DEBUG",
            "Bon type: ${if (isJomlaClientBon) "JOMLA_ECHATILLANTS" else "autre client"} " +
                    "(keyID=${
                        focusedValuesGetter.activeOnVent_M8BonVent?.parent_M2Client_KeyID?.takeLast(
                            6
                        )
                    })"
        )

        if (!isJomlaClientBon) {
            Log.d(
                "ECHATILLANTS_DEBUG",
                "⏭️ Skipping ECHATILLANTS check (not a JOMLA_ECHATILLANTS bon)"
            )
            emptySet()
        } else {
            val ventCouleurKeyIDs = focusedValuesGetter
                .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .map { it.parent_M3CouleurProduit_KeyID }
                .toSet()

            val allColorKeyIDs = allColors.map { it.keyID }.toSet()
            val missingFromRefActive = ventCouleurKeyIDs.filter { it !in allColorKeyIDs }

            // FIXED: Always log the comparison result, not just when there are missing items
            if (missingFromRefActive.isNotEmpty()) {
                Log.w(
                    "ECHATILLANTS_DEBUG",
                    "⚠️ ${missingFromRefActive.size} couleur(s) présente(s) dans " +
                            "onVent_M10VentCouleur ECHATILLANTS mais absentes du ref_active_m3:\n" +
                            missingFromRefActive.joinToString("\n") { keyID ->
                                val ventOp = focusedValuesGetter
                                    .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                                    .find { it.parent_M3CouleurProduit_KeyID == keyID }
                                "  - couleurKey=...${keyID.takeLast(6).uppercase()}" +
                                        " produit=${ventOp?.parent_M1Produit_DebugInfos ?: "?"}"
                            }
                )
            } else {
                Log.d(
                    "ECHATILLANTS_DEBUG",
                    "✅ Toutes les couleurs ECHATILLANTS sont présentes dans ref_active_m3" +
                            " (${ventCouleurKeyIDs.size} couleur(s))"
                )
            }

            // FIXED: Added summary log to verify the final result
            Log.d(
                "ECHATILLANTS_DEBUG",
                "📊 Résumé: ventCouleurs=${ventCouleurKeyIDs.size}, " +
                        "allColors=${allColorKeyIDs.size}, " +
                        "missing=${missingFromRefActive.size}, " +
                        "returning ${ventCouleurKeyIDs.size} keyIDs"
            )

            ventCouleurKeyIDs
        }
    }

    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect

        val targetKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        var currentIndex = 0
        var foundIndex = -1

        outer@ for ((_, categoriesWithProducts) in cataloguesWithCategoriesAndProducts) {
            currentIndex++

            for ((category, productColorPairs) in categoriesWithProducts) {
                if (category.displayedHeader) currentIndex++

                val productIndex = productColorPairs.indexOfFirst { (product, _) ->
                    product.keyID == targetKeyID
                }
                if (productIndex != -1) {
                    foundIndex = currentIndex + productIndex
                    break@outer
                }
                currentIndex += productColorPairs.size
            }
        }

        if (foundIndex >= 0) {
            delay(300)
            coroutineScope.launch {
                gridState.animateScrollToItem(foundIndex)
            }
        }
    }

    HandlePresenterScrollBroadcast(
        isHostPhone = isHostPhone,
        isConnected = isConnected,
        gridState = gridState,
        viewModel = viewModelHeadViewModel
    )

    HandlePresenterClientScroll(
        isHostPhone = isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
        tag = tag
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled
    ) {
        item(
            key = "upload_dropdown_header",
            span = StaggeredGridItemSpan.FullLine
        ) {
            Box(modifier = modifier) {
                Button(
                    onClick = { showUploadDropdown = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "⚙️ Options couleurs")
                }


                DropdownMenu(
                    expanded = showUploadDropdown,
                    onDismissRequest = { showUploadDropdown = false },
                ) {
                    fun repo3CouleurProduit_datasValue(): List<M3CouleurProduitInfos> =
                        repositorysMainGetter.repo3CouleurProduit.datasValue

                    fun get_Updated_list_m3couleurs_Affichable_Au_Presenters() =
                        repo3CouleurProduit_datasValue()
                            .map {
                                it.copy(
                                    its_pour_affiche_au_presenter =
                                        it.count_Don_Depot > 0 || it.its_in_echantiallants,
                                    parentProduit_Classement = parentProduit_Classement[it.parentBProduitInfosKeyID]   //<--
                                )
                            }
                            .also { updatedList ->
                                // ── DEBUG: why is the targeted M3 not at classement 0? ──────────────
                                // parentProduit_Classement sorts echatillants products first (bucket 0)
                                // then all others (bucket 1), preserving relative order within each bucket.
                                // A product floats to classement ~0 only when ANY of its sibling colors
                                // has its_in_echantiallants == true.
                                val targeted = updatedList.find { it.keyID == "-OWDMIC_UdVXmSNw-Dz0" }
                                if (targeted != null) {
                                    val siblingColors = updatedList.filter {
                                        it.parentBProduitInfosKeyID == "-OV3rmZ-9sy3P5rnINL3"
                                    }
                                    val siblingHasEchatillants = siblingColors.any { it.its_in_echantiallants }
                                    val reason = when {
                                        siblingHasEchatillants ->
                                            "✅ a sibling has its_in_echantiallants=true → product is in bucket-0, classement should be low"
                                        targeted.its_in_echantiallants ->
                                            "✅ this color itself has its_in_echantiallants=true → bucket-0"
                                        else ->
                                            "⚠️ NO sibling (nor self) has its_in_echantiallants=true → product stays in bucket-1 → classement=${targeted.parentProduit_Classement}"
                                    }
                                    Log.d(
                                        "TargetedM3_Classement",
                                        "[Targeted M3] keyID=...${"-OWDMIC_UdVXmSNw-Dz0".takeLast(6)}" +
                                                " | parentProduit_Classement=${targeted.parentProduit_Classement}" +
                                                " | its_in_echantiallants=${targeted.its_in_echantiallants}" +
                                                " | count_Don_Depot=${targeted.count_Don_Depot}" +
                                                " | its_pour_affiche_au_presenter=${targeted.its_pour_affiche_au_presenter}" +
                                                " | siblingColors=${siblingColors.size}" +
                                                " | siblingHasEchatillants=$siblingHasEchatillants" +
                                                " | → $reason"
                                    )
                                }
                            }


                    Updated_list_m3couleurs_Affichable_Au_Presenters(
                        updated_list_m3couleurs_Affichable_Au_Presenters = get_Updated_list_m3couleurs_Affichable_Au_Presenters(),
                        updated_list_m3couleurs_Affichable_Au_Presenters_filtred = get_Updated_list_m3couleurs_Affichable_Au_Presenters()
                            .filter {
                                it.its_pour_affiche_au_presenter
                            }.map {
                                it.parentId1ProduitInfosDebugName to it
                            },
                        onDismissDropdown = { showUploadDropdown = false }
                    )  {
                        viewModelHeadViewModel.upsertAll_Room(
                            it
                        )
                    }

                    Reset_its_pour_affiche_au_presenter(
                        allColors = repo3CouleurProduit_datasValue(),
                        onDismissDropdown = { showUploadDropdown = false }
                    ) {
                        viewModelHeadViewModel.upsertAll_Room(
                            it
                        )
                    }

                }
            }
        }

        cataloguesWithCategoriesAndProducts.forEach { (catalogue, categoriesWithProducts) ->
            item(
                key = "catalogue_header_${catalogue.id}",
                span = StaggeredGridItemSpan.Companion.FullLine
            ) {
                CatalogueHeader(catalogue = catalogue)
            }

            categoriesWithProducts.forEach { (category, productColorPairs) ->
                if (category.displayedHeader) {
                    item(
                        key = "category_header_${category.id}",
                        span = StaggeredGridItemSpan.Companion.FullLine
                    ) {
                        CategoryStickyHeader(
                            category = category,
                            onToggleHeaderVisibility = { updatedCategory ->
                                repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(
                                    updatedCategory
                                )
                            }
                        )
                    }
                }

                productColorPairs.forEach { (product, colors) ->
                    val isExpanded = focusedValuesGetter.active_Central_Values
                        .expanded_M1Produit?.keyID == product.keyID

                    val justMoved = product.keyID == justMovedProductKeyID

                    item(
                        key = "product_${product.keyID}",
                        span = if (isExpanded) {
                            StaggeredGridItemSpan.Companion.FullLine
                        } else {
                            StaggeredGridItemSpan.Companion.SingleLane
                        }
                    ) {
                        LazyStigerList_Produits_FragID4(
                            isWifiClientConnected_1 = isWifiClientConnected_1,
                            product = product,
                            colors = colors,

                            onCategoryClick = {
                                Log.d(
                                    "CategoryDialog_FragID4",
                                    "Category click from product: ${product.nom}"
                                )
                                onProductCategoryClick(product)
                            },
                            justMoved = justMoved
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogueHeader(
    catalogue: M21CataloguesCategorie,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(catalogue.couleur.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "📚 ${catalogue.nom}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = catalogue.couleur
        )
    }
}

@Composable
fun LazyStigerList_Produits_FragID4(
    modifier: Modifier = Modifier.Companion,
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),

    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false,
    isWifiClientConnected_1: Boolean
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (justMoved) {
            Color(0xFF4CAF50).copy(alpha = 0.3f)
        } else {
            Color.Companion.Transparent
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "backgroundColorAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (justMoved) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )

    Box(
        modifier = Modifier.Companion
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(backgroundColor, RoundedCornerShape(12.dp))
    ) {
        Item_Produit_FragID5(
            relative_M1produit = product,

            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
