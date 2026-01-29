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
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.size
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

    // FIXED TODO(1): Get distinct tariffs by type, keeping the most recent one for each type
    val datasValue_distinct_type = remember(repositorysMainGetter.repo13TarificationInfos.datasValue) {
        repositorysMainGetter.repo13TarificationInfos.datasValue
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) ->
                // For each type, get the one with the latest creation timestamp
                tariffs.maxByOrNull { it.creationTimestamps }
            }
            .values
            .filterNotNull()
    }

    fun algoritme_choisiser_tariff(): M13TarificationInfos {
        // First priority: Active operation tariff
        relative_list_M10operation_Vent.value?.let { operation ->
            val operationTariff = datasValue_distinct_type.find { tariff ->
                tariff.keyID == operation.parentM13TarificationKeyID &&
                        tariff.prixCurrency != 0.0
            }
            if (operationTariff != null && operationTariff.prixCurrency != 0.0) {
                return operationTariff
            }
        }

        // Second priority: Historical tariff
        val historicalTariff = datasValue_distinct_type.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Historique &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                    tariff.prixCurrency != 0.0
        }

        if (historicalTariff != null && historicalTariff.prixCurrency != 0.0) {
            return historicalTariff
        }

        // Third priority: Based on app type
        val fallbackTariff = if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
            // For retail app: try retail price, then super gro, then achat
            val retailTariff = datasValue_distinct_type.find { tariff ->
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.prixCurrency != 0.0
            }

            val superGroTariff = datasValue_distinct_type.find { tariff ->
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.prixCurrency != 0.0
            }

            retailTariff ?: superGroTariff
        } else {
            // For grossist app: try super gro
            datasValue_distinct_type.find { tariff ->
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.prixCurrency != 0.0
            }
        }

        // If we found a tariff, return it
        if (fallbackTariff != null) {
            return fallbackTariff
        }

        // FIXED TODO(2): Create default tariff if none found
        // Last resort: Create a default tariff with achat price
        val achatTariff = datasValue_distinct_type.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_Achat_Depuit_Grossisst &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID
        }

        val startPrice = achatTariff?.prixCurrency ?: 0.0

        // Create and save the default tariff
        val defaultTariff = M13TarificationInfos.get_default_P0(
            relative_M1produit,
            start_Prix_Depuit_Ancient = startPrice
        ).first

        // Save the default tariff to the repository
        aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(defaultTariff)

        return defaultTariff
    }

    // Use the algorithm to find or create the best tariff for this product
    val finale_Tariff = remember(relative_M1produit.keyID, datasValue_distinct_type.size) {
        algoritme_choisiser_tariff()
    }

    // Track the selected tariff for this product
    // When datasValue_distinct_type changes (new tariff created), finale_Tariff will update
    // and this will trigger a recomposition with the new tariff
    var selectedTariff by remember(
        relative_M1produit.keyID,
        finale_Tariff.keyID,
        datasValue_distinct_type.size
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
                    datasValue = datasValue_distinct_type,
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
