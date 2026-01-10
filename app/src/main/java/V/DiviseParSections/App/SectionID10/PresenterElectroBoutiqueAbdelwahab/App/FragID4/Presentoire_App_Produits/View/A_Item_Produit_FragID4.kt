package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Components.Big_Principale_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Components.SubColorCard_WithButton_FragId4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.Compact_Header_FragID4
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
fun A_Item_Produit_FragID4(
    relative_M1produit: ArticlesBasesStatsTable,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
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
            } else {
                0
            }
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

    val datasValue = repositorysMainGetter.repo13TarificationInfos.datasValue

    fun algoritme_choisiser_tariff(): Double {
        relative_list_M10operation_Vent.value?.let { operation ->
            val operationTariff = datasValue.find { tariff ->
                tariff.keyID == operation.parentM13TarificationKeyID &&
                        tariff.prixCurrency != 0.0
            }
            if (operationTariff != null && operationTariff.prixCurrency != 0.0) {
                return operationTariff.prixCurrency
            }
        }

        val historicalTariff = datasValue.find { tariff ->
            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Historique &&
                    tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                    tariff.prixCurrency != 0.0
        }

        if (historicalTariff != null && historicalTariff.prixCurrency != 0.0) {
            return historicalTariff.prixCurrency
        }

        return if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
            val retailTariff = datasValue.find { tariff ->
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.prixCurrency != 0.0
            }

            val superGroTariff = datasValue.find { tariff ->
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.prixCurrency != 0.0
            }

            retailTariff?.prixCurrency
                ?: superGroTariff?.prixCurrency
                ?: relative_M1produit.prixAchat
        } else {
            val superGroTariff = datasValue.find { tariff ->
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.prixCurrency != 0.0
            }

            superGroTariff?.prixCurrency ?: relative_M1produit.prixAchat
        }
    }

    val start_Prix = remember(
        relative_M1produit.keyID,
        datasValue,
        relative_list_M10operation_Vent.value
    ) {
        algoritme_choisiser_tariff()
    }

    val default_Tariff = remember(relative_M1produit.keyID, start_Prix) {
        M13TarificationInfos.get_default_P0(
            relative_M1produit,
            start_Prix_Depuit_Ancient = start_Prix
        )
    }

    val findTariff = remember(
        relative_M1produit.keyID,
        focusedValuesGetter.currentApp_ItsWorkChezGrossisst,
        datasValue,
        relative_list_M10operation_Vent.value
    ) {
        relative_list_M10operation_Vent.value?.let { operation ->
            datasValue.find { it.keyID == operation.parentM13TarificationKeyID }
        } ?: run {
            val type_A_Cherche = if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst)
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
            else
                M13TarificationInfos.TypeChoisi.Prix_Detaille

            datasValue.find { tariff ->
                tariff.typeChoisi == type_A_Cherche &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID
            }
        }
    }

    val finale_Tariff = remember(findTariff, default_Tariff) {
        findTariff ?: default_Tariff.first
    }

    var selectedTariff by remember(relative_M1produit.keyID, finale_Tariff.keyID) {
        mutableStateOf(finale_Tariff)
    }

    val developement_affiche = true

    val isHostPhone = wifiTransferDatas.connectionUiState.value.isHostPhone
            && wifiTransferDatas.connectionUiState.value.isConnected || developement_affiche

    val shouldShowButtons = isHostPhone

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
                Compact_Header_FragID4(
                    relative_M1produit = relative_M1produit,
                    isExpanded = isThisProductExpanded,
                    modifier = modifier
                )

                Big_Principale_FragID4(
                    relative_M1produit = relative_M1produit,
                    selectedCouleur = selectedCouleur,
                    relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
                    selectedTariff = selectedTariff,
                    onTariffSelected = { newTariff ->
                        selectedTariff = newTariff
                    },
                    datasValue = datasValue,
                    isThisProductExpanded = isThisProductExpanded,
                    shouldShowButtons = shouldShowButtons,
                    on_pour_send_data = on_pour_send_data
                )

                if (relative_ListM3Couleurs.size > 1 && shouldShowButtons) {
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
                                    SubColorCard_WithButton_FragId4(
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        focusedValuesGetter = focusedValuesGetter,
                                        on_pour_send_data = on_pour_send_data,
                                        isExpanded = true,
                                        modifier = Modifier.weight(1f, fill = false)
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
                                    SubColorCard_WithButton_FragId4(
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        selectedTariff = selectedTariff,
                                        focusedValuesGetter = focusedValuesGetter,
                                        on_pour_send_data = on_pour_send_data,
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
