package Application4.App.Fragment.View.ViewS.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private object TariffTextSizes {
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
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {
    val isGrossistMode = false
    val filteredTariffs = tariffsList.filter { tariff ->
        tariff.typeChoisi.its_gro_app == isGrossistMode
                && !tariff.typeChoisi.ignore_affiche
    }

    val tariffsWithEditableProgressive = if (filteredTariffs.none {
            it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client
        }) {
        val defaultEditableTariff = M13TarificationInfos(
            prixCurrency = 0.0,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
            creationTimestamps = System.currentTimeMillis()
        )
        filteredTariffs + defaultEditableTariff
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

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(sortedTariffs, key = { it.keyID }) { tariff ->
            TariffItemSelector(
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                tariff = tariff,
                relative_M1produit = relative_M1produit,
                isSelected = tariff.keyID == selectedTariff.keyID,
                compactMode = compactMode,
                onClick = { onTariffSelected(tariff) },
                tariffsList = tariffsList
            )
        }
    }
}

@Composable
private fun TariffItemSelector(
    tariff: M13TarificationInfos,
    relative_M1produit: M01Produit,
    isSelected: Boolean,
    compactMode: Boolean,
    onClick: () -> Unit,
    tariffsList: List<M13TarificationInfos>,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {
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
                // FIX TODO(1)b: pass produit so we can compute bénéfice client
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

private fun handleProgressivePriceUpdate(
    tariff: M13TarificationInfos,
    newPrice: Double,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
) {
    if (tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Edited_Pour_Client
    ) return

    if (newPrice <= 0) return

    val updatedTariff = tariff.copy(
        prixCurrency = newPrice,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )
    uiState_NewProtoPatterns_viewModel.second.update_M13TarificationInfos(updatedTariff)
}

private fun formatPrice(price: Double): String =
    String.format(Locale.getDefault(), "%.0f", price)

private fun formatPriceWithDecimals(price: Double): String =
    String.format(Locale.getDefault(), "%.1f", price)

// ─────────────────────────────────────────────────────────────────────────────
// EditableProgressiveTariffItem
// FIX TODO(1)a : FlowRow → Column so every value stacks below the previous one
// FIX TODO(1)b : added relative_M1produit param + bénéfice-client row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EditableProgressiveTariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    relative_M1produit: M01Produit,           // FIX TODO(1)b
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    onPriceUpdated: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = if (compactMode) TariffTextSizes.COMPACT_HORIZONTAL_PADDING
    else TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    val verticalPadding = if (compactMode) TariffTextSizes.COMPACT_VERTICAL_PADDING
    else TariffTextSizes.NORMAL_VERTICAL_PADDING
    val fontSize = if (compactMode) TariffTextSizes.COMPACT_MAIN_TEXT
    else TariffTextSizes.NORMAL_MAIN_TEXT

    val borderWidth = if (isSelected) TariffTextSizes.SELECTED_BORDER_WIDTH
    else TariffTextSizes.UNSELECTED_BORDER_WIDTH
    val borderColor = if (isSelected) Color.Red else Color.Transparent

    val backgroundColor = tariff.typeChoisi.couleur.copy(alpha = if (isSelected) 1f else 0.9f)
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    // FIX TODO(1)b: bénéfice client = clientPrixVentUnite * nombreUnite - prix achat/tariff
    val clientPrixVentUnite = relative_M1produit.clientPrixVentUnite
    val beneficeClient = clientPrixVentUnite * nombreUnite - prix

    // FIX TODO(1)a: was FlowRow — now Column so items stack vertically
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
                if (!isSelected && tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) {
                    onClick()
                }
                onPriceUpdated(newValue)
            },
            compact_taille = compactMode,
            textSize = fontSize,
            containerColor = backgroundColor,
            textColor = tariff.typeChoisi.couleur_Text,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // prix par unité
        if (nombreUnite > 1) {
            Text(
                text = "(${formatPriceWithDecimals(prixUnitaire)}/u)",
                color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                fontSize = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                lineHeight = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // FIX TODO(1)b: bénéfice client
        if (clientPrixVentUnite > 0) {
            Text(
                text = "bén: ${formatPrice(beneficeClient)} DA",
                color = if (beneficeClient >= 0)
                    tariff.typeChoisi.couleur_Text.copy(alpha = 0.75f)
                else
                    Color.Red.copy(alpha = 0.85f),
                fontSize = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                lineHeight = TariffTextSizes.COMPACT_SECONDARY_TEXT,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TariffItem
// FIX TODO(1)b: added bénéfice-client row inside the compact Column branch
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    relative_M1produit: M01Produit,
    tariffsList: List<M13TarificationInfos>,
    modifier: Modifier = Modifier
) {
    val effectivePrix =
        if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) {
            val detailleTariff = tariffsList.find {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                        it.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        it.prixCurrency != 0.0
            }
            val supperGroTariff = tariffsList.find {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                        it.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        it.prixCurrency != 0.0
            }
            val recalculated =
                M13TarificationInfos.remembered_calculated_progressive_changement_tariff(
                    relative_Prix_Detaille = detailleTariff?.prixCurrency,
                    relative_Prix_SupperGro_Et_PresentationService = supperGroTariff?.prixCurrency,
                    relative_produit = relative_M1produit
                )
            recalculated?.prixCurrency ?: prix
        } else {
            prix
        }

    if (effectivePrix == 0.0) return

    val horizontalPadding = if (compactMode) TariffTextSizes.COMPACT_HORIZONTAL_PADDING
    else TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    val verticalPadding = if (compactMode) TariffTextSizes.COMPACT_VERTICAL_PADDING
    else TariffTextSizes.NORMAL_VERTICAL_PADDING
    val fontSize = if (compactMode) TariffTextSizes.COMPACT_MAIN_TEXT
    else TariffTextSizes.NORMAL_MAIN_TEXT
    val secondaryFontSize = if (compactMode) TariffTextSizes.COMPACT_SECONDARY_TEXT
    else TariffTextSizes.NORMAL_SECONDARY_TEXT

    val borderWidth = if (isSelected) TariffTextSizes.SELECTED_BORDER_WIDTH
    else TariffTextSizes.UNSELECTED_BORDER_WIDTH
    val borderColor = if (isSelected) Color.Red else Color.Transparent

    val backgroundColor =
        if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService) {
            Color.Black.copy(alpha = if (isSelected) 1f else 0.9f)
        } else {
            tariff.typeChoisi.couleur.copy(alpha = if (isSelected) 1f else 0.9f)
        }

    val prixUnitaire = if (nombreUnite > 1) effectivePrix / nombreUnite else effectivePrix

    // FIX TODO(1)b
    val clientPrixVentUnite = relative_M1produit.clientPrixVentUnite
    val beneficeClient = clientPrixVentUnite * nombreUnite - effectivePrix

    if (compactMode) {
        Column(
            modifier = modifier
                .border(width = borderWidth, color = borderColor, shape = CircleShape)
                .background(color = backgroundColor, shape = CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = formatPrice(effectivePrix),
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize,
                lineHeight = fontSize
            )

            if (nombreUnite > 1) {
                Text(
                    text = "(${formatPriceWithDecimals(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize
                )
            }

            // FIX TODO(1)b: bénéfice client
            if (clientPrixVentUnite > 0) {
                Text(
                    text = "bén: ${formatPrice(beneficeClient)} DA",
                    color = if (beneficeClient >= 0)
                        tariff.typeChoisi.couleur_Text.copy(alpha = 0.75f)
                    else
                        Color.Red.copy(alpha = 0.85f),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .border(width = borderWidth, color = borderColor, shape = CircleShape)
                .background(color = backgroundColor, shape = CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tariff.typeChoisi.abrgNom,
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            if (nombreUnite > 1) {
                Text(
                    text = "${formatPrice(effectivePrix)} DA/p.u (${formatPriceWithDecimals(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            } else {
                Text(
                    text = "${formatPrice(effectivePrix)} DA/p.u",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            }
        }
    }
}
