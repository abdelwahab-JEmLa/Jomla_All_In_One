package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Components.Big_Principale_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Components.Compact_Header_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Components.SubColorCard_WithButton
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Components.updateTariffForProductOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item_Produit_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesSetter: FocusedValuesSetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    wifiTransferDatas: WifiTransferDatas = koinInject()
) {

    val relative_ListM3Couleurs = remember(relative_M1produit.keyID) {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
    }

    val relative_list_M10operation_Vent = remember(
        relative_M1produit.keyID,
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.size,

        ) {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
        }
    }

    val expanded_M1Produit = focusedValuesGetter.active_Central_Values.expanded_M1Produit
    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    val isThisProductExpanded = remember(expanded_M1Produit) {
        expanded_M1Produit?.keyID == relative_M1produit.keyID
    }

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

    val datasValue_distinct_type = remember(repositorysMainGetter.repo13TarificationInfos.datasValue) {
        repositorysMainGetter.repo13TarificationInfos.datasValue
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) ->
                tariffs.maxByOrNull { it.creationTimestamps }
            }
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

    // 1. A NEW bon vent is added for the same client (not when returning to old bon vent)
    // 2. The product is currently displayed
    // 3. The tariff doesn't already exist (prevents recreation on app restart)
    LaunchedEffect(
        focusedValuesGetter.activeOnVent_M8BonVent?.keyID,
        focusedValuesGetter.activeOnVent_M8BonVent?.creationTimestamps
    ) {
        val currentBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        // Only proceed if we have a valid bon vent and we're not in grossist mode
        if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst &&
            currentBonVent != null &&
            synthetic != null) {

            val currentClient = focusedValuesGetter.activeOnVent_M2Client

            // Check if Edited_Pour_Client already exists for this product and bon vent
            val existingEditedTariff = datasValue_distinct_type.find {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == relative_M1produit.keyID
            }

            // If tariff already exists, don't recreate it (prevents recreation on app restart)
            if (existingEditedTariff != null) {
                return@LaunchedEffect
            }

            // Find all bon vents for this client in the current period
            val clientBonVents = focusedValuesGetter.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .filter { it.parent_M2Client_KeyID == currentClient?.keyID }
                .sortedByDescending { it.creationTimestamps }

            // Check if current bon vent is the NEWEST one for this client
            val isNewestBonVent = clientBonVents.firstOrNull()?.keyID == currentBonVent.keyID

            // Only create tariff if:
            // 1. This is the newest bon vent for the client
            // 2. Tariff doesn't exist yet (already checked above)
            // 3. Bon vent was created recently (within last 5 minutes) to avoid creating for old bon vents
            val bonVentAge = System.currentTimeMillis() - currentBonVent.creationTimestamps
            val isRecentlyCreated = bonVentAge < (5 * 60 * 1000) // 5 minutes

            if (isNewestBonVent && isRecentlyCreated) {
                // Create new Edited_Pour_Client tariff for this NEW bon vent
                val newTariff = synthetic.copy(
                    parent_M8BonVent_KeyId = currentBonVent.keyID,
                    parent_M8BonVent_DebugInfos = currentBonVent.get_DebugInfos(),
                    parent_M2Client_KeyId = currentClient?.keyID ?: "null",
                    parent_M2Client_DebugInfos = currentClient?.nom ?: "null",
                    creationTimestamps = System.currentTimeMillis(),
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )

                // Save the new tariff
                aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(newTariff)
            }
        }
    }

    // Append the synthetic Edited_Pour_Client whenever it exists and none was persisted.
    // synthetic is already null when both base prices are missing — no extra gate needed.
    val datasValue_with_synthetic = if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst &&
        datasValue_distinct_type.none { it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client } &&
        synthetic != null
    ) {
        datasValue_distinct_type + synthetic
    } else {
        datasValue_distinct_type
    }

    val fallbackTariff = if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
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

        retailTariff ?: superGroTariff
    } else {
        datasValue_with_synthetic.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                    tariff.prixCurrency != 0.0
        }
    }

    fun algoritme_choisiser_tariff(): M13TarificationInfos {
        // Find the most recent operation for this product
        val lastOperation = focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .maxByOrNull { it.creationTimestamps }

        // Try to use the tariff from the last operation
        lastOperation?.parentM13TarificationKeyID?.let { tariffKeyID ->
            datasValue_with_synthetic.find { it.keyID == tariffKeyID }?.let { operationTariff ->
                // Verify the tariff still has a valid price
                if (operationTariff.prixCurrency != 0.0 ||
                    operationTariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) {
                    return operationTariff
                }
            }
        }

        // If no valid operation tariff, use the default logic:
        // First try the highest price tariff with non-zero price
        val highestPriceTariff = datasValue_with_synthetic
            .filter { it.prixCurrency != 0.0 }
            .maxByOrNull { it.prixCurrency }

        return highestPriceTariff ?: fallbackTariff
        ?: datasValue_with_synthetic.firstOrNull()
        ?: M13TarificationInfos.get_default()
    }

    val finale_Tariff = remember(
        datasValue_with_synthetic,
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.size,
        fallbackTariff
    ) {
        algoritme_choisiser_tariff()
    }

    var selectedTariff by remember(
        relative_M1produit.keyID,
        finale_Tariff.keyID,
        datasValue_with_synthetic.size
    ) {
        mutableStateOf(finale_Tariff)
    }

    val developement_affiche = true

    val isHostPhone = wifiTransferDatas.connectionUiState.value.isHostPhone
            && wifiTransferDatas.connectionUiState.value.isConnected || developement_affiche

    val selectedCouleur = relative_ListM3Couleurs[big_presenter_couleur_produit]

    val relative_M10OperationVentCouleur by remember(
        selectedCouleur.keyID,
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.size
    ) {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }
        }
    }

    val cardPadding = if (isThisProductExpanded) 8.dp else 4.dp
    val innerPadding = if (isThisProductExpanded) 8.dp else 4.dp

    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = supperGro, key = SemanticsPropertyKey("supperGro"))
            }
            .semantics(mergeDescendants = true) {
                set(value = synthetic, key = SemanticsPropertyKey("synthetic"))
            }
            .semantics(mergeDescendants = true) {
                set(value = fallbackTariff, key = SemanticsPropertyKey("fallbackTariff"))
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = algoritme_choisiser_tariff(),
                    key = SemanticsPropertyKey(" algoritme_choisiser_tariff()")
                )
            }
            .semantics(mergeDescendants = true) {
                set(value = selectedTariff, key = SemanticsPropertyKey("selectedTariff"))
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
                Compact_Header_FragID3(
                    relative_M1produit = relative_M1produit,
                    isExpanded = isThisProductExpanded,
                    shouldShowButtons = isHostPhone,
                    onUpdateTariffContext = if (isHostPhone) {
                        {
                            focusedValuesGetter.currentActive_M9AppCompt?.let { appCompt ->
                                aCentralFacade.repositorysMainSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                                    relative_M1produit,
                                    appCompt
                                )
                            }
                        }
                    } else null,
                    modifier = modifier
                )

                val filteredAndSortedTariffs = datasValue_with_synthetic
                    .filter { tariff ->
                        // Always show Edited_Pour_Client even if price is 0
                        tariff.prixCurrency != 0.0 ||
                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client ||
                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable
                    }
                    .sortedByDescending { it.typeChoisi.profitabilityScore }  // Sort by profitability score from enum

                Big_Principale_FragID3(
                    relative_M1produit = relative_M1produit,
                    selectedCouleur = selectedCouleur,
                    relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
                    selectedTariff = selectedTariff,
                    onTariffSelected = { newTariff ->
                        selectedTariff = newTariff
                        // Update all product operations with new tariff
                        aCentralFacade.updateTariffForProductOperations(
                            relative_M1produit.keyID,
                            newTariff
                        )
                    },
                    tariffsList = filteredAndSortedTariffs,  // FIXED: Now filtered and sorted
                    isThisProductExpanded = isThisProductExpanded,
                    shouldShowButtons = isHostPhone,
                    on_pour_send_data = on_pour_send_data
                )

                if (relative_ListM3Couleurs.size > 1 && isHostPhone) {
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
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        focusedValuesGetter = focusedValuesGetter,
                                        on_pour_send_data = on_pour_send_data,
                                        isExpanded = true,
                                        modifier = Modifier
                                            .weight(1f, fill = false)
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
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        focusedValuesGetter = focusedValuesGetter,
                                        on_pour_send_data = on_pour_send_data,
                                        isExpanded = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
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
