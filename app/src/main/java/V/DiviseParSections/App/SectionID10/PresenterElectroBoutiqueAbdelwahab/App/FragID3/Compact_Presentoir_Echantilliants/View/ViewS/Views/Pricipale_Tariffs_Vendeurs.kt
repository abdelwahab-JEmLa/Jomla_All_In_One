package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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

/**
 * Displays tariff information for a product with selection capability
 *
 * @param relative_M1produit The product to display tariffs for
 * @param tariffsList List of all available tariffs
 * @param selectedTariff The currently selected tariff for this product
 * @param onTariffSelected Callback when a tariff is selected
 * @param compactMode Whether to use compact display (removes names and reduces size)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false
) {
    // Define the tariff types to display in order
    val displayTariffs = listOf(
        M13TarificationInfos.TypeChoisi.Prix_Detaille,
        M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
        M13TarificationInfos.TypeChoisi.Historique,
        M13TarificationInfos.TypeChoisi.LeMaxPrixArrive,
    )

    // Filter and prepare tariffs to display
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

    // Adjust padding based on compact mode
    val containerPadding = if (compactMode) 2.dp else 4.dp

    // Use FlowRow to wrap items when space is not available
    FlowRow(
        modifier = Modifier.padding(containerPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tariffsToDisplay.forEach { (tariff, prix) ->
            // FIXED: Use key() to ensure proper recomposition and state tracking
            key(tariff.typeChoisi, relative_M1produit.keyID) {
                // FIXED: Improved selection logic - compare by type AND product
                // This ensures the selection is visible from initial render
                val isSelected = selectedTariff.typeChoisi == tariff.typeChoisi &&
                        selectedTariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID

                TariffItem(
                    tariff = tariff,
                    prix = prix,
                    isSelected = isSelected,
                    compactMode = compactMode,
                    onClick = { onTariffSelected(tariff) }
                )
            }
        }
    }
}

/**
 * Calculate progressive tariff with flexible logic:
 * - If both Prix_Detaille and Prix_SupperGro available: use average
 * - If only one available: use that one
 * - If neither available: return tariff with 0.0
 */
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

@Composable
private fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit
) {
    // Adjust sizes based on compact mode
    val horizontalPadding = if (compactMode) 6.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp
    val iconSize = if (compactMode) 14.dp else 16.dp
    val fontSize = if (compactMode) 9.sp else 10.sp

    // FIXED: Use stable border width calculation to prevent flickering
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Row(
        modifier = Modifier
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

        // In compact mode, only show the abbreviated name (no full name)
        if (!compactMode) {
            Text(
                text = tariff.typeChoisi.abrgNom,
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )
        }

        // Price (always shown)
        // Format: "prix DA / p.u" or just "prix DA" if compact
        Text(
            text = if (compactMode) {
                String.format("%.0f", prix)
            } else {
                String.format("%.0f DA/p.u", prix)
            },
            color = tariff.typeChoisi.couleur_Text,
            fontSize = fontSize
        )
    }
}
