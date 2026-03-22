package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.Components.Big_Principale_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.Components.SubColorCard_WithButton
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.Components.updateTariffForProductOperations
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.Compact_Header_FragID3
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item_Produit_FragID5(
    viewModel: TariffsButtonsViewModelSec7ID2 = koinInject(),
    relative_M1produit: M01Produit,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesSetter: FocusedValuesSetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    headViewModel: HeadViewModel = koinInject(),
    wifiTransferDatas: WifiTransferDatas = koinInject(),
    onCategoryClick: (() -> Unit)? = null,
    isWifiClientConnected_1: Boolean = false,
) {
    val shouldShowButtons = !isWifiClientConnected_1

    val uiState by headViewModel.uiState.collectAsState()

    val allCategories = remember(repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        repositorysMainGetter.repoM16CategorieProduit.datasValue
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
    val expanded_M3CouleurProduitInfos =
        focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

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

    val datasValue_distinct_type =
        remember(repositorysMainGetter.repo13TarificationInfos.datasValue) {
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
            synthetic != null
        ) {

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
            val clientBonVents =
                focusedValuesGetter.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
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
    //<--
    // FIXED: Screen now stays active when connected to a client (see DisposableEffect above)
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

        // Use retail price, fallback to superGro, then to product's base price
        retailTariff ?: superGroTariff ?: M13TarificationInfos(
            prixCurrency = relative_M1produit.prixVent,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Detaille
        )
    } else {
        // For grossist mode, use superGro price as fallback
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

    val algoritme_choisiser_tariff = remember(datasValue_with_synthetic) {
        val algorithm: () -> M13TarificationInfos? = {
            datasValue_with_synthetic
                .filter { it.prixCurrency != 0.0 }
                .maxByOrNull { it.typeChoisi.profitabilityScore }
        }
        algorithm
    }

    val finale_Tariff = remember(algoritme_choisiser_tariff(), fallbackTariff) {
        algoritme_choisiser_tariff() ?: fallbackTariff
    }

    var selectedTariff by remember(
        relative_M1produit.keyID,
        finale_Tariff.keyID,
        datasValue_with_synthetic.size
    ) {
        mutableStateOf(finale_Tariff)
    }

    val developement_affiche = false

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
                if (focusedValuesGetter.currentApp_Est_Admin && (currentCatalogue != null || currentCategory != null)) {
                    Log.d(
                        "CategoryDialog_Item",
                        "Rendering CategoryBadge - isHostPhone: $isHostPhone, onCategoryClick null: ${onCategoryClick == null}"
                    )
                    CategoryBadge(
                        catalogueName = currentCatalogue?.nom,
                        categoryName = currentCategory?.nom,
                        onClick = {
                            Log.d("CategoryDialog_Item", "CategoryBadge onClick triggered")
                            onCategoryClick?.invoke()
                                ?: Log.e("CategoryDialog_Item", "onCategoryClick is NULL!")
                        },
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                } else {
                    Log.d(
                        "CategoryDialog_Item",
                        "CategoryBadge NOT rendered - isHostPhone: $isHostPhone, catalogue: $currentCatalogue, category: $currentCategory"
                    )
                }

                Compact_Header_FragID3(
                    relative_M1produit = relative_M1produit,
                    isExpanded = isThisProductExpanded,
                    shouldShowButtons = true,
                    onUpdateTariffContext = {
                        focusedValuesGetter.currentActive_M9AppCompt?.let { appCompt ->
                            aCentralFacade.repositorysMainSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                                relative_M1produit,
                                appCompt
                            )
                        }
                    },
                    modifier = modifier,
                    onUpdateNombreUnite = { newUnite ->
                        viewModel.update_m1Produit(
                            relative_M1produit.copy(nombreUniteInt = newUnite)
                        )
                    },
                    onUpdateCarton = { newCarton ->
                        viewModel.update_m1Produit(
                            relative_M1produit.copy(quantite_Boit_Par_Carton = newCarton)
                        )
                    }
                )
                val filteredAndSortedTariffs = datasValue_with_synthetic
                    .filter { tariff ->
                        tariff.prixCurrency != 0.0 ||
                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client ||
                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable
                    }
                    .sortedByDescending { it.typeChoisi.profitabilityScore }

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
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        focusedValuesGetter = focusedValuesGetter,
                                        on_pour_send_data = on_pour_send_data,
                                        shouldShowButtons = shouldShowButtons,
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
                                        shouldShowButtons = shouldShowButtons,
                                        isExpanded = false,
                                        modifier = Modifier
                                            .fillMaxWidth(),
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

/**
 * Clickable badge displaying the current category and catalogue
 */
@Composable
private fun CategoryBadge(
    catalogueName: String?,
    categoryName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(
        "CategoryDialog_Item",
        "CategoryBadge composed in Item_Produit - catalogue: $catalogueName, category: $categoryName"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            .clickable {
                Log.d("CategoryDialog_Item", "CategoryBadge CLICKED in Item_Produit!")
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (catalogueName != null) {
                    Text(
                        text = catalogueName,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = categoryName ?: "Sans Catégorie",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Changer catégorie",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
