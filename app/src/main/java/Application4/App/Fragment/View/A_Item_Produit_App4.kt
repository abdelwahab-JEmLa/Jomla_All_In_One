package Application4.App.Fragment.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.Components.A_Header.View.A_Compact_Header_App4
import Application4.App.Fragment.View.Components.Big_Principale_FragID3
import Application4.App.Fragment.View.Components.SubColorCard_WithButton
import EntreApps.Shared.Models.Home.find_ListM3CouleurInfos_By_Parent_Produit_KeyID
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
    // Pre-filtered colour list passed in from the grid to avoid re-scanning the full list.
    // Falls back to searching list_M03CouleurProduitInfos when called from outside the grid.
    relative_ListM3Couleurs_override: List<M3CouleurProduitInfos>? = null,
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val wifiState by viewModel.wifiState.collectAsState()
    val centralValues = viewModel.active_Datas

    // Use the pre-filtered override when available (supplied by the staggered grid);
    // fall back to scanning the full live list only when called from other call-sites.
    val allColorsForProduit = relative_ListM3Couleurs_override
        ?: remember(viewModel.active_Datas.list_M03CouleurProduitInfos) {
            find_ListM3CouleurInfos_By_Parent_Produit_KeyID(
                viewModel.active_Datas.list_M03CouleurProduitInfos ?: emptyList(),
                relative_M1produit.keyID
            )
        }

    // FIX(TODO-1): filter the colour list according to the active display mode.
    //   • Échantillons mode  → keep only colours flagged its_in_echantiallants == true
    //   • Normal mode        → keep only colours present in the active ref-keys map
    //                          (falls back to showing everything when the map is empty/null,
    //                           e.g. on first load before Firebase has responded)
    val isEchatillantsMode = centralValues.affiche_produits_Ou_On_TagPrioriter
        ?.contains(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS) == true

    val relative_ListM3Couleurs = remember(
        allColorsForProduit,
        isEchatillantsMode,
        centralValues.map_m3couleur_to_ref_list_Filtred_Keys_M3Couleur_Main_Values
    ) {
        if (isEchatillantsMode) {
            // Show only colours explicitly marked as échantillons
            allColorsForProduit.filter { it.its_in_echantiallants == true }
        } else {
            val activeKeys = centralValues.map_m3couleur_to_ref_list_Filtred_Keys_M3Couleur_Main_Values
            if (activeKeys.isNullOrEmpty()) {
                // Map not yet loaded — show everything so the card is never blank
                allColorsForProduit
            } else {
                // Normal mode: restrict to colours present in the active ref-keys list
                allColorsForProduit.filter { it.keyID in activeKeys }
            }
        }
    }

    val expanded_M1Produit = wifiState.expanded_M1Produit
    Log.d("A_Item_Produit", "wifiState expand — expanded_M1Produit=${expanded_M1Produit?.keyID} | thisProduit=${relative_M1produit.keyID}")
    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos

    val isThisProductExpanded = remember(expanded_M1Produit) {
        expanded_M1Produit?.keyID == relative_M1produit.keyID
    }
    val shouldShowButtons = true // compactMode = !isThisProductExpanded gère le layout

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

    val activeM9compt = centralValues.active_M9Compt
    val isGrossist = activeM9compt?.travailleChezGrossisst3Ali == true

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

    // FIX(TODO-1): removed the outer Card container — the grid's LazyStigerList_Produits_FragID4
    // already wraps each item in a styled Box; a redundant Card here caused double elevation and
    // unnecessary recomposition overhead.
    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = supperGro, key = SemanticsPropertyKey("supperGro"))
                set(value = activeM9compt, key = SemanticsPropertyKey("activeM9compt"))
            }
            .fillMaxWidth()
            .padding(cardPadding)
    ) {
        A_Compact_Header_App4(
            relative_M1produit = relative_M1produit,
            isExpanded = isThisProductExpanded,
            onUpdateTariff = {
                activeM9compt?.let { appCompt ->
                    viewModel.setActiveFocuceTariffPrixDifineur(relative_M1produit, appCompt)
                }
            },
            onUpdateProduit = { viewModel.update_m1Produit(it) },
            affiche_ProduitDataBaseEdites_ComposableViews = centralValues.currentApp_Est_Admin
                    && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true,
            onDelete = { viewModel.delete_m1Produit(it) },
            modifier = modifier,
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
