package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

object TariffTextSizes {
    val COMPACT_MAIN_TEXT = 15.sp
    val COMPACT_SECONDARY_TEXT = 6.sp
    val COMPACT_HORIZONTAL_PADDING = 6.dp
    val COMPACT_VERTICAL_PADDING = 4.dp
    val COMPACT_ICON_SIZE = 16.dp
    val COMPACT_ICON_SIZE_TARIFF_ITEM = 14.dp

    val NORMAL_MAIN_TEXT = 18.sp
    val NORMAL_SECONDARY_TEXT = 14.sp
    val NORMAL_HORIZONTAL_PADDING = 12.dp
    val NORMAL_VERTICAL_PADDING = 8.dp
    val NORMAL_ICON_SIZE = 20.dp

    val SELECTED_BORDER_WIDTH = 3.dp
    val UNSELECTED_BORDER_WIDTH = 0.dp
}

@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: M01Produit,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff_Par_AncienProto: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    une_des_selectedCouleur: M3CouleurProduitInfos,
) {
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val listM10OperationVentCouleur_FilteredBy_activeM8BonVent =
        viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state

    val relative_M10OperationVentCouleur by remember(
        listM10OperationVentCouleur_FilteredBy_activeM8BonVent
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent?.find {
                it.parent_M3CouleurProduit_KeyID == une_des_selectedCouleur.keyID
            }
        }
    }

    val tariff_Stocked_Au_OperationVent =
        tariffsList.find {
            it.keyID == relative_M10OperationVentCouleur?.parentM13TarificationKeyID
        }

    Log.d(
        "TariffDebug",
        "[Pricipale_Tariffs] produit=${relative_M1produit.keyID} " +
                "couleur=${une_des_selectedCouleur.keyID} " +
                "m10Found=${relative_M10OperationVentCouleur?.keyID} " +
                "m10TariffKey=${relative_M10OperationVentCouleur?.parentM13TarificationKeyID} " +
                "tariff_Stocked=${tariff_Stocked_Au_OperationVent?.keyID} " +
                "tariff_StockedType=${tariff_Stocked_Au_OperationVent?.typeChoisi}"
    )

    val isGrossistMode = false
    val filteredTariffs = tariffsList.filter { tariff ->
        tariff.typeChoisi.its_gro_app == isGrossistMode && !tariff.typeChoisi.ignore_affiche
    }

    val tariffsWithEditableProgressive = if (filteredTariffs.none {
            it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client
        }) {
        filteredTariffs + M13TarificationInfos(
            prixCurrency = 0.0,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
            creationTimestamps = System.currentTimeMillis()
        )
    } else {
        filteredTariffs
    }

    val sortedTariffs = tariffsWithEditableProgressive.sortedBy { tariff ->
        when (tariff.typeChoisi) {
            M13TarificationInfos.TypeChoisi.Prix_Detaille -> 0
            M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
            M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable -> 1

            M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> 2
            else -> tariff.typeChoisi.profitabilityScore
        }
    }

    Tariffs_MainList(
        modifier = modifier,
        sortedTariffs = sortedTariffs,
        uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
        relative_M1produit = relative_M1produit,
        selectedTariff_Par_AncienProto = selectedTariff_Par_AncienProto,
        compactMode = compactMode,
        onTariffSelected = onTariffSelected,
        tariffsList = tariffsList,
        tariff_Stocked_Au_OperationVent = tariff_Stocked_Au_OperationVent,
        relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
    )
}

@Composable
fun TariffItemSelector(
    tariff: M13TarificationInfos,
    relative_M1produit: M01Produit,
    isSelected: Boolean,
    compactMode: Boolean,
    onClick: () -> Unit,
    tariffsList: List<M13TarificationInfos>,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {       //<--
    Log.d(
        "TariffDebug",
        "[TariffItemSelector] tariff=${tariff.keyID} type=${tariff.typeChoisi} " +
                "prix=${tariff.prixCurrency} isSelected=$isSelected produit=${relative_M1produit.keyID}"
    )
    val prix = tariff.prixCurrency
    val nombreUnite = relative_M1produit.nombreUniteInt

    when {
        tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive ||
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client -> {
            EditableProgressiveTariffItem(
                tariff = tariff,
                prix = prix,
                nombreUnite = nombreUnite,
                relative_M1produit = relative_M1produit,
                isSelected = isSelected,
                compactMode = compactMode,
                onClick = onClick,
                onPriceUpdated = { newPrice ->
                    handleProgressivePriceUpdate(
                        uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                        tariff = tariff,
                        newPrice = newPrice,
                    )
                }
            )
        }

        else -> {
            TariffItem(
                tariff = tariff,
                prix = prix,
                nombreUnite = nombreUnite,
                isSelected = isSelected,
                compactMode = compactMode,
                onClick = onClick,
                relative_M1produit = relative_M1produit,
                tariffsList = tariffsList
            )
        }
    }
}

fun handleProgressivePriceUpdate(
    tariff: M13TarificationInfos,
    newPrice: Double,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
) {
    if (tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Edited_Pour_Client
    ) return

    if (newPrice <= 0) {
        Log.d(
            "TariffFix",
            "[handleProgressivePriceUpdate] blocked — newPrice=$newPrice (<=0) tariff=${tariff.keyID} type=${tariff.typeChoisi}"
        )
        return
    }

    Log.d(
        "TariffFix",
        "[handleProgressivePriceUpdate] updating tariff=${tariff.keyID} type=${tariff.typeChoisi} newPrice=$newPrice"
    )

    uiState_NewProtoPatterns_viewModel.second.update_M13TarificationInfos(
        tariff.copy(
            prixCurrency = newPrice,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    )
}

fun formatPrice(price: Double): String =
    String.format(Locale.getDefault(), "%.0f", price)

fun formatPriceWithDecimals(price: Double): String =
    String.format(Locale.getDefault(), "%.1f", price)

@Composable
fun EditableProgressiveTariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    relative_M1produit: M01Produit,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    onPriceUpdated: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding =
        if (compactMode) TariffTextSizes.COMPACT_HORIZONTAL_PADDING else TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    val verticalPadding =
        if (compactMode) TariffTextSizes.COMPACT_VERTICAL_PADDING else TariffTextSizes.NORMAL_VERTICAL_PADDING
    val fontSize =
        if (compactMode) TariffTextSizes.COMPACT_MAIN_TEXT else TariffTextSizes.NORMAL_MAIN_TEXT
    val borderWidth =
        if (isSelected) TariffTextSizes.SELECTED_BORDER_WIDTH else TariffTextSizes.UNSELECTED_BORDER_WIDTH
    val borderColor = if (isSelected) Color.Red else Color.Transparent
    val backgroundColor = tariff.typeChoisi.couleur.copy(alpha = if (isSelected) 1f else 0.9f)
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix
    val clientPrixVentUnite = relative_M1produit.clientPrixVentUnite
    val beneficeClient = clientPrixVentUnite * nombreUnite - prix

    Column(
        modifier = modifier
            .border(width = borderWidth, color = borderColor, shape = CircleShape)
            .background(color = backgroundColor, shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
            value = prix,
            onValueChanged = { newValue ->
                // Select this item first whenever the user edits the price field while it
                // is not yet selected — covers all three editable tariff variants so that
                // typing in the field always triggers selection before the price update.
                if (!isSelected && (
                            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client ||
                                    tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                                    tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive
                            )
                ) {
                    Log.d(
                        "TariffFix",
                        "[EditableProgressiveTariffItem] auto-selecting tariff=${tariff.keyID} type=${tariff.typeChoisi} before price update"
                    )
                    onClick()
                }
                onPriceUpdated(newValue)
            },
            compact_taille = compactMode,
            textSize = fontSize,
            showDecimals = false,
            containerColor = backgroundColor,
            textColor = tariff.typeChoisi.couleur_Text,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (nombreUnite > 1) {
            Text(
                text = "(${formatPriceWithDecimals(prixUnitaire)}/u)",
                color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                fontSize = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                lineHeight = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (clientPrixVentUnite > 0) {
            Text(
                text = "bén: ${formatPrice(beneficeClient)} DA",
                color = if (beneficeClient >= 0) tariff.typeChoisi.couleur_Text.copy(alpha = 0.75f) else Color.Red.copy(
                    alpha = 0.85f
                ),
                fontSize = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                lineHeight = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}             //<--
