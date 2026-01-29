package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Icon_Outlined
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
import androidx.compose.material3.MaterialTheme
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

// ============================================================================
// CONSTANTS - Text Sizes
// ============================================================================

/**
 * Text size constants for compact and normal display modes
 */
private object TariffTextSizes {
    // Compact mode sizes
    val COMPACT_MAIN_TEXT = 15.sp
    val COMPACT_SECONDARY_TEXT = 6.sp
    val COMPACT_ICON_SIZE = 4.dp
    val COMPACT_ICON_SIZE_TARIFF_ITEM = 14.dp

    // Normal mode sizes
    val NORMAL_MAIN_TEXT = 12.sp
    val NORMAL_SECONDARY_TEXT = 11.sp
    val NORMAL_ICON_SIZE = 16.dp

    // Padding constants
    val COMPACT_HORIZONTAL_PADDING = 6.dp
    val COMPACT_VERTICAL_PADDING = 2.dp
    val NORMAL_HORIZONTAL_PADDING = 8.dp
    val NORMAL_VERTICAL_PADDING = 4.dp

    val COMPACT_CONTAINER_PADDING = 2.dp
    val NORMAL_CONTAINER_PADDING = 4.dp
}

/**
 * Helper function to format price - shows decimals only if needed
 * FIXED: Doesn't display .00 for whole numbers
 */
private fun formatPrice(prix: Double): String {
    return if (prix % 1.0 == 0.0) {
        // Whole number - no decimals
        prix.toInt().toString()
    } else {
        // Has decimals - show up to 2 decimal places, removing trailing zeros
        String.format("%.2f", prix).trimEnd('0').trimEnd('.')
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    aCentralFacade: ACentralFacade = koinInject()
) {
    val displayTariffs = listOf(
        M13TarificationInfos.TypeChoisi.Prix_Detaille,
        M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
        M13TarificationInfos.TypeChoisi.Historique,
        M13TarificationInfos.TypeChoisi.LeMaxPrixArrive,
    )
    val tariffsToDisplay = displayTariffs.mapNotNull { tariffType ->
        // Find matching tariff for this product
        val matchingTariff = tariffsList.firstOrNull {
            it.typeChoisi == tariffType &&
                    it.parent_M1Produit_KeyId == relative_M1produit.keyID
        }

        // Always calculate progressive tariff
        val tariff = if (tariffType == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
            matchingTariff ?: calculateProgressiveTariff(tariffsList, relative_M1produit)
        } else {
            matchingTariff
        }

        // Get price
        val prix = tariff?.prixCurrency ?: 0.0

        // For progressive tariff, always show it (even with 0.0)
        if (tariffType == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable && tariff != null) {
            tariff to prix
        } else if (prix != 0.0 && tariff != null) {
            tariff to prix
        } else {
            null
        }
    }

    // If no tariffs to display, don't render anything
    if (tariffsToDisplay.isEmpty()) {
        return
    }

    // Adjust padding based on compact mode - using constants
    val containerPadding = if (compactMode) {
        TariffTextSizes.COMPACT_CONTAINER_PADDING
    } else {
        TariffTextSizes.NORMAL_CONTAINER_PADDING
    }

    // Use FlowRow to wrap items when space is not available
    FlowRow(
        modifier = Modifier.padding(containerPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tariffsToDisplay.forEach { (tariff, prix) ->
            // Use key() to ensure proper recomposition and state tracking
            key(tariff.typeChoisi, relative_M1produit.keyID) {
                val isSelected = selectedTariff.typeChoisi == tariff.typeChoisi &&
                        selectedTariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID

                // Special handling for Prix_Progressive_Editable
                if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
                    EditableProgressiveTariffItem(
                        tariff = tariff,
                        prix = prix,
                        nombreUnite = relative_M1produit.nombreUniteInt,
                        isSelected = isSelected,
                        compactMode = compactMode,
                        onClick = {
                            onTariffSelected(tariff)
                        },
                        onPriceUpdated = { newPrice ->
                            // Create updated tariff with new price
                            val updatedTariff = tariff.copy(
                                prixCurrency = newPrice,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )

                            // Save to repository
                            aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)

                            onTariffSelected(updatedTariff)
                        },
                        // FIXED: Added fillMaxWidth modifier in compact mode
                        modifier = if (compactMode) Modifier.fillMaxWidth() else Modifier
                    )
                } else {
                    // FIXED: TODO resolved - Added fillMaxWidth modifier in compact mode
                    TariffItem(
                        tariff = tariff,
                        prix = prix,
                        nombreUnite = relative_M1produit.nombreUniteInt,
                        isSelected = isSelected,
                        compactMode = compactMode,
                        onClick = { onTariffSelected(tariff) },
                        modifier = if (compactMode) Modifier.fillMaxWidth() else Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableProgressiveTariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    onPriceUpdated: (Double) -> Unit,
    modifier: Modifier = Modifier  // FIXED: Added modifier parameter to accept fillMaxWidth
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
    val secondaryFontSize = if (compactMode) {
        TariffTextSizes.COMPACT_SECONDARY_TEXT
    } else {
        TariffTextSizes.NORMAL_SECONDARY_TEXT
    }

    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    if (compactMode) {
        Column(
            // FIXED: Now using the passed modifier which includes fillMaxWidth() when needed
            modifier = modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = tariff.typeChoisi.couleur.copy(
                        alpha = if (isSelected) 1f else 0.9f
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                value = prix,
                standard_count = 1.0,
                Icon_Outlined_p0 = Icon_Outlined(
                    icon = tariff.typeChoisi.iconVector!!,
                    size = iconSize,
                    color = tariff.typeChoisi.couleur_Text
                ),
                isAvailable = true,
                compact_taille = compactMode,
                textSize = fontSize,
                showDecimals = !(prix % 1.0 == 0.0),
                decimalPlaces = 2,
                onValueChanged = { newPrice ->
                    onPriceUpdated(newPrice)
                }
            )

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
            modifier = modifier  // FIXED: Using the passed modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = tariff.typeChoisi.couleur.copy(
                        alpha = if (isSelected) 1f else 0.9f
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Text(
                text = tariff.typeChoisi.abrgNom,
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                value = prix,
                standard_count = 1.0,
                Icon_Outlined_p0 = Icon_Outlined(
                    icon = tariff.typeChoisi.iconVector!!,
                    size = iconSize,
                    color = tariff.typeChoisi.couleur_Text
                ),
                isAvailable = true,
                compact_taille = compactMode,
                textSize = fontSize,
                showDecimals = !(prix % 1.0 == 0.0),
                decimalPlaces = 2,
                onValueChanged = { newPrice ->
                    onPriceUpdated(newPrice)
                }
            )

            if (nombreUnite > 1) {
                Text(
                    text = "(${formatPrice(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            } else {
                Text(
                    text = "DA/p.u",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Composable
private fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier  // FIXED: Added modifier parameter
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

    // Use stable border width calculation to prevent flickering
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Calculate unit price if nombreUnite > 1
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    // FIXED: Always show both prices when nombreUnite > 1
    // Use Column in compact mode for line break, Row otherwise
    if (compactMode) {
        Column(
            modifier = modifier  // FIXED: Using the passed modifier which includes fillMaxWidth() in compact mode
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = tariff.typeChoisi.couleur.copy(
                        alpha = if (isSelected) 1f else 0.9f
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            tariff.typeChoisi.iconVector?.let { icon ->

            }

            // Always show total price - FIXED: Smart decimal formatting
            Text(
                text = formatPrice(prix),
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            // Always show unit price if nombreUnite > 1 - FIXED: Smart decimal formatting
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
            modifier = modifier  // FIXED: Using the passed modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = tariff.typeChoisi.couleur.copy(
                        alpha = if (isSelected) 1f else 0.9f
                    ),
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

            // Always show both prices if nombreUnite > 1 - FIXED: Smart decimal formatting
            if (nombreUnite > 1) {
                Text(
                    text = "${formatPrice(prix)} DA/p.u (${formatPrice(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            } else {
                Text(
                    text = "${formatPrice(prix)} DA/p.u",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            }
        }
    }
}

private fun calculateProgressiveTariff(
    tariffsList: List<M13TarificationInfos>,
    product: ArticlesBasesStatsTable
): M13TarificationInfos? {
    val prixDetaille = tariffsList.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                it.parent_M1Produit_KeyId == product.keyID
    }

    val prixSupperGro = tariffsList.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                it.parent_M1Produit_KeyId == product.keyID
    }

    val detaillePrice = prixDetaille?.prixCurrency ?: 0.0
    val supperGroPrice = prixSupperGro?.prixCurrency ?: 0.0

    // Calculate progressive price based on availability
    val progressivePrice = when {
        // Both available: use average
        detaillePrice > 0.0 && supperGroPrice > 0.0 -> {
            (detaillePrice + supperGroPrice) / 2.0
        }
        // Only Detaille available: use it
        detaillePrice > 0.0 -> detaillePrice
        // Only SupperGro available: use it
        supperGroPrice > 0.0 -> supperGroPrice
        // Neither available: 0.0
        else -> 0.0
    }

    // Use the first available tariff as base, or create a new one
    val baseTariff = prixDetaille ?: prixSupperGro ?: M13TarificationInfos(
        parent_M1Produit_KeyId = product.keyID,
        parent_M1Produit_DebugInfos = product.getDebugInfos()
    )

    return baseTariff.copy(
        keyID = "progressive_${product.keyID}",
        typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
        prixCurrency = progressivePrice,
        parent_M1Produit_KeyId = product.keyID
    )
}
