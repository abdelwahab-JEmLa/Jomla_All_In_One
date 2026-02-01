package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.Locale

private object TariffTextSizes {
    // Compact mode sizes
    val COMPACT_MAIN_TEXT = 15.sp
    val COMPACT_SECONDARY_TEXT = 6.sp
    val COMPACT_HORIZONTAL_PADDING = 8.dp
    val COMPACT_VERTICAL_PADDING = 4.dp
    val COMPACT_ICON_SIZE = 16.dp
    val COMPACT_ICON_SIZE_TARIFF_ITEM = 14.dp

    // Normal mode sizes
    val NORMAL_MAIN_TEXT = 18.sp
    val NORMAL_SECONDARY_TEXT = 14.sp
    val NORMAL_HORIZONTAL_PADDING = 12.dp
    val NORMAL_VERTICAL_PADDING = 8.dp
    val NORMAL_ICON_SIZE = 20.dp

    // FIXED: Border widths for selected state
    val SELECTED_BORDER_WIDTH = 3.dp  // Thicker border for selected items
    val UNSELECTED_BORDER_WIDTH = 0.dp
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    aCentralFacade: ACentralFacade = koinInject(),
    modifier: Modifier = Modifier
) {
    val isGrossistMode = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    val filteredTariffs = tariffsList.filter { tariff ->
        tariff.typeChoisi.its_gro_app == isGrossistMode
                && !tariff.typeChoisi.ignore_affiche
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        filteredTariffs.forEach { tariff ->
            key(tariff.keyID) {
                TariffItemSelector(
                    tariff = tariff,
                    relative_M1produit = relative_M1produit,
                    isSelected = tariff.keyID == selectedTariff.keyID,
                    compactMode = compactMode,
                    onClick = { onTariffSelected(tariff) },
                    aCentralFacade = aCentralFacade,
                    tariffsList = tariffsList
                )
            }
        }
    }
}

@Composable
private fun TariffItemSelector(
    tariff: M13TarificationInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    isSelected: Boolean,
    compactMode: Boolean,
    onClick: () -> Unit,
    aCentralFacade: ACentralFacade,
    tariffsList: List<M13TarificationInfos>
) {
    val prix = tariff.prixCurrency
    val nombreUnite = relative_M1produit.quantite_Boit_Par_Carton

    // FIXED: Added Edited_Pour_Client to the editable tariff types
    when {
        tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive ||
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client -> {
            EditableProgressiveTariffItem(
                tariff = tariff,
                prix = prix,
                nombreUnite = nombreUnite,
                isSelected = isSelected,
                compactMode = compactMode,
                onClick = onClick,
                onPriceUpdated = { newPrice ->
                    handleProgressivePriceUpdate(
                        tariff = tariff,
                        newPrice = newPrice,
                        aCentralFacade = aCentralFacade
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
    aCentralFacade: ACentralFacade
) {
    Log.d(
        "PricipaleTariffsVendeurs",
        "handleProgressivePriceUpdate - Tariff: ${tariff.keyID}, New Price: $newPrice"
    )

    // FIXED: Added Edited_Pour_Client to the validation check
    if (tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Edited_Pour_Client) {
        Log.w(
            "PricipaleTariffsVendeurs",
            "Attempted to edit non-progressive tariff: ${tariff.typeChoisi}"
        )
        return
    }

    if (newPrice <= 0) {
        Log.w(
            "PricipaleTariffsVendeurs",
            "Invalid price value: $newPrice. Must be greater than 0."
        )
        return
    }

    val updatedTariff = tariff.copy(
        prixCurrency = newPrice,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )

    Log.d(
        "PricipaleTariffsVendeurs",
        "Saving updated tariff: ${updatedTariff.keyID} with price: ${updatedTariff.prixCurrency}"
    )

    aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
}

private fun formatPrice(price: Double): String {
    return String.format(Locale.getDefault(), "%.0f", price)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditableProgressiveTariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    onPriceUpdated: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    // Using constants for sizes
    val horizontalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_HORIZONTAL_PADDING
    } else {
        TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    }
    val verticalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_VERTICAL_PADDING
    } else {
        TariffTextSizes.NORMAL_VERTICAL_PADDING
    }
    val iconSize = if (compactMode) {
        TariffTextSizes.COMPACT_ICON_SIZE
    } else {
        TariffTextSizes.NORMAL_ICON_SIZE
    }
    val fontSize = if (compactMode) {
        TariffTextSizes.COMPACT_MAIN_TEXT
    } else {
        TariffTextSizes.NORMAL_MAIN_TEXT
    }

    val borderWidth = if (isSelected) {
        TariffTextSizes.SELECTED_BORDER_WIDTH
    } else {
        TariffTextSizes.UNSELECTED_BORDER_WIDTH
    }
    val borderColor = if (isSelected) Color.Red else Color.Transparent

    val backgroundColor = tariff.typeChoisi.couleur.copy(alpha = if (isSelected) 1f else 0.9f)

    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    FlowRow(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = CircleShape
            )
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = Int.MAX_VALUE // Allow unlimited items per row
    ) {
        Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
            value = prix,
            onValueChanged = { newValue ->
                onPriceUpdated(newValue)
            },
            compact_taille = compactMode,
            textSize = fontSize,
            containerColor = backgroundColor,
            textColor = tariff.typeChoisi.couleur_Text,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        // Show unit price if nombreUnite > 1
        if (nombreUnite > 1) {
            Text(
                text = "(${formatPrice(prixUnitaire)}/u)",
                color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                fontSize = fontSize,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
//<--
// FIXED: Edited_Pour_Client tariff now uses EditableProgressiveTariffItem (see line 115)
// This allows the click outlined text edit field to display when editing client tariff
@Composable
private fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    modifier: Modifier = Modifier
) {
    val effectivePrix = if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) {
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

        val recalculated = M13TarificationInfos.remembered_calculated_progressive_changement_tariff(
            relative_Prix_Detaille = detailleTariff?.prixCurrency,
            relative_Prix_SupperGro_Et_PresentationService = supperGroTariff?.prixCurrency,
            relative_produit = relative_M1produit
        )
        // Fall back to the stored prix only if SupperGro is completely missing
        recalculated?.prixCurrency ?: prix
    } else {
        prix
    }

    // Nothing to display: the synthetic Edited_Pour_Client tariff has prixCurrency == 0
    // and the live recalculation also returned null (both base prices are missing).
    // Skip rendering so no empty pill appears.
    if (effectivePrix == 0.0) return

    // Using constants for sizes
    val horizontalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_HORIZONTAL_PADDING
    } else {
        TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    }
    val verticalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_VERTICAL_PADDING
    } else {
        TariffTextSizes.NORMAL_VERTICAL_PADDING
    }
    val iconSize = if (compactMode) {
        TariffTextSizes.COMPACT_ICON_SIZE_TARIFF_ITEM
    } else {
        TariffTextSizes.NORMAL_ICON_SIZE
    }
    val fontSize = if (compactMode) {
        TariffTextSizes.COMPACT_MAIN_TEXT
    } else {
        TariffTextSizes.NORMAL_MAIN_TEXT
    }
    val secondaryFontSize = if (compactMode) {
        TariffTextSizes.COMPACT_SECONDARY_TEXT
    } else {
        TariffTextSizes.NORMAL_SECONDARY_TEXT
    }

    // FIXED: Thicker border and red color for selected items
    val borderWidth = if (isSelected) {
        TariffTextSizes.SELECTED_BORDER_WIDTH
    } else {
        TariffTextSizes.UNSELECTED_BORDER_WIDTH
    }
    val borderColor = if (isSelected) Color.Red else Color.Transparent

    // FIXED: Override background color for Prix_SupperGro_Et_PresentationService
    val backgroundColor = if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService) {
        Color.Black.copy(alpha = if (isSelected) 1f else 0.9f)
    } else {
        tariff.typeChoisi.couleur.copy(alpha = if (isSelected) 1f else 0.9f)
    }

    // Calculate unit price if nombreUnite > 1
    val prixUnitaire = if (nombreUnite > 1) effectivePrix / nombreUnite else effectivePrix

    // FIXED: Always show both prices when nombreUnite > 1
    // Use Column in compact mode for line break, Row otherwise
    if (compactMode) {
        Column(
            modifier = modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            tariff.typeChoisi.iconVector?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = tariff.typeChoisi.nomArabe,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                )
            }

            // Always show total price
            Text(
                text = formatPrice(effectivePrix),
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            // Always show unit price if nombreUnite > 1
            if (nombreUnite > 1) {
                Text(
                    text = "(${formatPrice(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                    fontSize = secondaryFontSize
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            tariff.typeChoisi.iconVector?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = tariff.typeChoisi.nomArabe,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                )
            }

            // Abbreviated name
            Text(
                text = tariff.typeChoisi.abrgNom,
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            // Always show both prices if nombreUnite > 1
            if (nombreUnite > 1) {
                Text(
                    text = "${formatPrice(effectivePrix)} DA/p.u (${formatPrice(prixUnitaire)}/u)",
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
