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

        // If no tariff exists and it's Prix_Progressive_Editable, calculate it
        val tariff = matchingTariff ?: if (tariffType == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
            calculateProgressiveTariff(tariffsList, relative_M1produit)
        } else null

        // Get price
        val prix = tariff?.prixCurrency ?: 0.0

        if (prix != 0.0 && tariff != null) {
            tariff to prix
        } else {
            null
        }
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
            val isSelected = selectedTariff.typeChoisi == tariff.typeChoisi

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

/**
 * Calculate progressive tariff as average of Prix_Detaille and Prix_SupperGro
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

    if (prixDetaille != null && prixSupperGro != null) {
        val avgPrice = (prixDetaille.prixCurrency + prixSupperGro.prixCurrency) / 2.0

        return prixDetaille.copy(
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
            prixCurrency = avgPrice
        )
    }

    return null
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
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(
                width = borderWidth,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
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
