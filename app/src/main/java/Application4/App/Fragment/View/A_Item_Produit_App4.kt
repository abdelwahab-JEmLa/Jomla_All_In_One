package Application4.App.Fragment.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.Components.A_Header.View.A_Compact_Header_App4
import Application4.App.Fragment.View.Components.Big_Principale_FragID3
import Application4.App.Fragment.View.Components.SubColorCard_WithButton
import EntreApps.Shared.Models.Home.find_ListM3CouleurInfos_By_Parent_Produit_KeyID
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun A_Item_Produit_App4(
    relative_M1produit: M01Produit,
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    onCategoryClick: (() -> Unit)? = null,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val centralValues = uiState.active_Central_Values

    val allCategories = remember(uiState.list_M16CategorieProduit) {
        uiState.list_M16CategorieProduit
    }
    val categoryMap = remember(allCategories) {
        allCategories.associateBy { it.id }
    }
    val currentCategory = remember(relative_M1produit.idParentCategorie, allCategories) {
        relative_M1produit.idParentCategorie?.let { categoryMap[it] }
    }
    val catalogues = remember { get_ListM21CataloguesCategorie() }
    val currentCatalogue = remember(currentCategory, catalogues) {
        currentCategory?.catalogueParentId?.let { catalogueId ->
            catalogues.find { it.id.toLong() == catalogueId }
        }
    }

    val relative_ListM3Couleurs = remember(uiState.list_M3CouleurProduit) {
        find_ListM3CouleurInfos_By_Parent_Produit_KeyID(
            uiState.list_M3CouleurProduit,
            relative_M1produit.keyID
        )
    }

    val onVentList = centralValues.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        ?: emptyList()

    val expanded_M1Produit = centralValues.expanded_M1Produit
    val expanded_M3CouleurProduitInfos = centralValues.expanded_M3CouleurProduitInfos

    val isThisProductExpanded = remember(expanded_M1Produit) {
        expanded_M1Produit?.keyID == relative_M1produit.keyID
    }
    val shouldShowButtons = !isThisProductExpanded

    val initialColorIndex = remember(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )
                if (matchingIndex != -1) matchingIndex else 0
            } else 0
        } ?: 0
    }

    var big_presenter_couleur_produit by remember(initialColorIndex) {
        mutableStateOf(initialColorIndex)
    }

    LaunchedEffect(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )
                if (matchingIndex != -1 && matchingIndex != big_presenter_couleur_produit) {
                    big_presenter_couleur_produit = matchingIndex
                }
            }
        }
    }

    val datasValue_distinct_type = remember(uiState.list_M13TarificationInfos) {
        uiState.list_M13TarificationInfos
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) -> tariffs.maxByOrNull { it.creationTimestamps } }
            .values
            .filterNotNull()
    }

    val supperGro = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                it.prixCurrency != 0.0
    }
    val detaille = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                it.prixCurrency != 0.0
    }

    val synthetic = M13TarificationInfos.remembered_calculated_progressive_changement_tariff(
        relative_Prix_Detaille = detaille?.prixCurrency,
        relative_Prix_SupperGro_Et_PresentationService = supperGro?.prixCurrency,
        relative_produit = relative_M1produit
    )

    LaunchedEffect(
        centralValues.activeOnVent_M8BonVent?.keyID,
        centralValues.activeOnVent_M8BonVent?.creationTimestamps
    ) {
        viewModel.maybeCreateEditedPourClientTariff(
            produit = relative_M1produit,
            synthetic = synthetic,
            datasValue_distinct_type = datasValue_distinct_type,
        )
    }

    val isGrossist = centralValues.activeCompt?.travailleChezGrossisst3Ali == true

    val datasValue_with_synthetic = if (!isGrossist &&
        datasValue_distinct_type.none { it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client } &&
        synthetic != null
    ) {
        datasValue_distinct_type + synthetic
    } else {
        datasValue_distinct_type
    }

    val fallbackTariff = if (!isGrossist) {
        val retailTariff = datasValue_with_synthetic.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                    tariff.prixCurrency != 0.0
        }
        val superGroTariff = datasValue_with_synthetic.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                    tariff.prixCurrency != 0.0
        }
        retailTariff ?: superGroTariff ?: M13TarificationInfos(
            prixCurrency = relative_M1produit.prixVent,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Detaille
        )
    } else {
        val superGroTariff = datasValue_with_synthetic.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                    tariff.prixCurrency != 0.0
        }
        superGroTariff ?: M13TarificationInfos(
            prixCurrency = relative_M1produit.prixAchat,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
        )
    }

    val finale_Tariff = remember(datasValue_with_synthetic, fallbackTariff) {
        datasValue_with_synthetic
            .filter { it.prixCurrency != 0.0 }
            .maxByOrNull { it.typeChoisi.profitabilityScore }
            ?: fallbackTariff
    }

    var selectedTariff by remember(
        relative_M1produit.keyID,
        finale_Tariff.keyID,
        datasValue_with_synthetic.size
    ) {
        mutableStateOf(finale_Tariff)
    }


    if (relative_ListM3Couleurs.isEmpty()) return

    val safeIndex = big_presenter_couleur_produit.coerceIn(0, relative_ListM3Couleurs.lastIndex)
    if (safeIndex != big_presenter_couleur_produit) big_presenter_couleur_produit = safeIndex
    val selectedCouleur = relative_ListM3Couleurs[safeIndex]


    val cardPadding = if (isThisProductExpanded) 8.dp else 4.dp
    val innerPadding = if (isThisProductExpanded) 8.dp else 4.dp

    val isAdmin = centralValues.currentApp_Est_Admin
            && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true
    val categoryClickForHeader: (() -> Unit)? = if (isAdmin) onCategoryClick else null

    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = supperGro, key = SemanticsPropertyKey("supperGro"))
            }
            .fillMaxWidth()
            .padding(cardPadding)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isThisProductExpanded) 8.dp else 4.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                A_Compact_Header_App4(
                    relative_M1produit = relative_M1produit,
                    isExpanded = isThisProductExpanded,
                    onUpdateTariff = {
                        centralValues.activeCompt?.let { appCompt ->
                            viewModel.setActiveFocuceTariffPrixDifineur(relative_M1produit, appCompt)
                        }
                    },
                    onUpdateProduit = { viewModel.update_m1Produit(it) },
                    affiche_ProduitDataBaseEdites_ComposableViews = centralValues.currentApp_Est_Admin
                            && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true,
                    onDelete = { viewModel.delete_m1Produit(it) },
                    modifier = modifier,
                    catalogueName = currentCatalogue?.nom,
                    categoryName = currentCategory?.nom,
                    onCategoryClick = categoryClickForHeader,
                    section_ToggleButton_TagPreiorities__start_Collapsed = viewModel.active_Datas.section_ToggleButton_TagPrioriter__start_Collapsed == true
                    )

                val filteredAndSortedTariffs = datasValue_with_synthetic
                    .filter { tariff ->
                        tariff.prixCurrency != 0.0 ||
                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client ||
                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable
                    }
                    .sortedByDescending { it.typeChoisi.profitabilityScore }

                Big_Principale_FragID3(
                    uiState_NewProtoPatterns_viewModel,
                    relative_M1produit = relative_M1produit,
                    selectedCouleur = selectedCouleur,
                    selectedTariff = selectedTariff,
                    onTariffSelected = { newTariff ->
                        selectedTariff = newTariff
                        viewModel.updateTariffForProductOperations(
                            relative_M1produit.keyID,
                            newTariff
                        )
                    },
                    tariffsList = filteredAndSortedTariffs,
                    isThisProductExpanded = isThisProductExpanded,
                    shouldShowButtons = shouldShowButtons,
                    on_pour_send_data = on_pour_send_data
                )

                if (relative_ListM3Couleurs.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isThisProductExpanded) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            maxItemsInEachRow = 4
                        ) {
                            relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                                if (index != big_presenter_couleur_produit) {
                                    SubColorCard_WithButton(
                                        uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        on_pour_send_data = on_pour_send_data,
                                        isExpanded = true,
                                        modifier = Modifier.weight(1f, fill = false),
                                        shouldShowButtons = shouldShowButtons,
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                                if (index != big_presenter_couleur_produit) {
                                    SubColorCard_WithButton(
                                        uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        on_pour_send_data = on_pour_send_data,
                                        shouldShowButtons = shouldShowButtons,
                                        isExpanded = false,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
