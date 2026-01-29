package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Icon_Outlined
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt.Companion.getPushFireBase
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
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
    val NORMAL_MAIN_TEXT = 20.sp
    val NORMAL_SECONDARY_TEXT = 15.sp
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
        modifier = Modifier.padding(containerPadding)
            .semantics(mergeDescendants = true) {
                set(value = tariffsList.filter {
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille
                }, key = SemanticsPropertyKey(""))
            },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tariffsToDisplay.forEach { (tariff, prix) ->
            // Use key() to ensure proper recomposition and state tracking
            key(tariff.typeChoisi, relative_M1produit.keyID) {
                // FIXED: Compare by typeChoisi AND parent product, not by keyID
                // This fixes the issue where progressive tariff has synthetic keyID
                val isSelected = isTariffSelected(
                    displayedTariff = tariff,
                    selectedTariff = selectedTariff,
                    productKeyId = relative_M1produit.keyID
                )

                // FIXED: Log tariff information for debugging
                logTariffSelection(
                    displayedTariff = tariff,
                    selectedTariff = selectedTariff,
                    isSelected = isSelected
                )

                // Special handling for Prix_Progressive_Editable
                if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
                    EditableProgressiveTariffItem(
                        tariff = tariff,
                        prix = prix,
                        nombreUnite = relative_M1produit.nombreUniteInt,
                        isSelected = isSelected,
                        compactMode = compactMode,
                        onClick = {
                            // FIXED: When clicking progressive tariff, ensure we use/create real tariff
                            val realTariff = ensureRealTariff(tariff, tariffsList, aCentralFacade)
                            onTariffSelected(realTariff)
                        },
                        onPriceUpdated = { newPrice ->
                            // FIXED: Ensure we update a real tariff in the database
                            val realTariff = ensureRealTariff(tariff, tariffsList, aCentralFacade)
                            val updatedTariff = realTariff.copy(
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

// ============================================================================
// FIXED: Helper functions to properly handle tariff selection and logging
// ============================================================================

/**
 * FIXED: Properly compare tariffs by type and product, not by keyID
 * This fixes the issue where progressive tariffs have synthetic keyIDs
 *
 * IMPORTANT: When multiple real tariffs of the same type exist for a product,
 * we must also compare by keyID to distinguish between them.
 */
private fun isTariffSelected(
    displayedTariff: M13TarificationInfos,
    selectedTariff: M13TarificationInfos,
    productKeyId: String
): Boolean {
    // First check: must be same type and same product
    val sameTypeAndProduct = displayedTariff.typeChoisi == selectedTariff.typeChoisi &&
            displayedTariff.parent_M1Produit_KeyId == productKeyId &&
            selectedTariff.parent_M1Produit_KeyId == productKeyId

    if (!sameTypeAndProduct) {
        return false
    }

    // Second check: If both have real keyIDs (not calculated), compare by keyID
    // This handles the case where multiple tariffs of the same type exist for one product
    val displayedIsCalculated = displayedTariff.keyID.startsWith("progressive_")
    val selectedIsCalculated = selectedTariff.keyID.startsWith("progressive_")

    return if (!displayedIsCalculated && !selectedIsCalculated) {
        // Both are real tariffs - must match by keyID to distinguish them
        displayedTariff.keyID == selectedTariff.keyID
    } else {
        // At least one is calculated - type and product match is enough
        true
    }
}

/**
 * FIXED: Log tariff information for debugging
 * This addresses TODO(2.C): "cree log por le consernon por regle le problemme"
 */
private fun logTariffSelection(
    displayedTariff: M13TarificationInfos,
    selectedTariff: M13TarificationInfos,
    isSelected: Boolean
) {
    if (isSelected) {
        Log.d(
            "TariffSelection",
            """
            ============ TARIFF SELECTED ============
            Type: ${displayedTariff.typeChoisi}
            Displayed Tariff KeyID: ${displayedTariff.keyID}
            Selected Tariff KeyID: ${selectedTariff.keyID}
            Product KeyID: ${displayedTariff.parent_M1Produit_KeyId}
            Price: ${displayedTariff.prixCurrency}
            Is Calculated: ${displayedTariff.keyID.startsWith("progressive_")}
            =========================================
            """.trimIndent()
        )
    }
}

/**
 * FIXED: Ensure we have a real tariff with proper keyID from database
 * Converts calculated progressive tariff to real database tariff
 */
private fun ensureRealTariff(
    tariff: M13TarificationInfos,
    tariffsList: List<M13TarificationInfos>,
    aCentralFacade: ACentralFacade
): M13TarificationInfos {
    // If this is a calculated progressive tariff (synthetic keyID)
    if (tariff.keyID.startsWith("progressive_")) {
        // Try to find existing progressive tariff in database
        val existingRealTariff = tariffsList.firstOrNull {
            it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable &&
                    it.parent_M1Produit_KeyId == tariff.parent_M1Produit_KeyId &&
                    !it.keyID.startsWith("progressive_")
        }

        if (existingRealTariff != null) {
            // Use existing real tariff, update price if different
            if (existingRealTariff.prixCurrency != tariff.prixCurrency) {
                val updatedTariff = existingRealTariff.copy(
                    prixCurrency = tariff.prixCurrency,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
                return updatedTariff
            }
            return existingRealTariff
        } else {
            // Create new real tariff in database
            val newRealTariff = tariff.copy(
                keyID = getPushFireBase(M13TarificationInfos.ref),
                creationTimestamps = System.currentTimeMillis(),
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(newRealTariff)

            Log.d(
                "TariffCreation",
                """
                ============ NEW TARIFF CREATED ============
                Old (calculated) KeyID: ${tariff.keyID}
                New (real) KeyID: ${newRealTariff.keyID}
                Type: ${newRealTariff.typeChoisi}
                Product: ${newRealTariff.parent_M1Produit_KeyId}
                Price: ${newRealTariff.prixCurrency}
                ============================================
                """.trimIndent()
            )

            return newRealTariff
        }
    }

    // Already a real tariff
    return tariff
}

// ============================================================================
// Editable Progressive Tariff Item Component
// ============================================================================

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
                Icon(
                    imageVector = icon,
                    contentDescription = tariff.typeChoisi.nomArabe,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                )
            }

            // Editable price field
            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                value = prix,
                standard_count = 1.0,
                Icon_Outlined_p0 = Icon_Outlined(
                    icon = tariff.typeChoisi.iconVector!!,
                    size = iconSize,
                    color = tariff.typeChoisi.couleur_Text
                ),
                isAvailable = true,
                compact_taille = true,
                textSize = fontSize,
                showDecimals = !(prix % 1.0 == 0.0),
                decimalPlaces = 2,
                onValueChanged = { newPrice ->
                    onPriceUpdated(newPrice)
                }
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

            // Editable price field
            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                value = prix,
                standard_count = 1.0,
                Icon_Outlined_p0 = Icon_Outlined(
                    icon = tariff.typeChoisi.iconVector!!,
                    size = iconSize,
                    color = tariff.typeChoisi.couleur_Text
                ),
                isAvailable = true,
                compact_taille = false,
                textSize = fontSize,
                showDecimals = !(prix % 1.0 == 0.0),
                decimalPlaces = 2,
                onValueChanged = { newPrice ->
                    onPriceUpdated(newPrice)
                }
            )

            // Always show both prices if nombreUnite > 1
            if (nombreUnite > 1) {
                Text(
                    text = "DA/p.u",
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

// ============================================================================
// Regular Tariff Item Component
// ============================================================================

@Composable
private fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
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
            // Icon
            tariff.typeChoisi.iconVector?.let { icon ->

            }

            // Always show total price
            Text(
                text = formatPrice(prix),
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

            // Always show both prices if nombreUnite > 1
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

// ============================================================================
// Progressive Tariff Calculation
// ============================================================================

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
