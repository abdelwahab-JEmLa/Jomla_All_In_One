package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays tariff information for a product
 *
 * @param relative_M1produit The product to display tariffs for
 * @param tariffsList List of all available tariffs
 * @param compactMode Whether to use compact display (removes names and reduces size)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    compactMode: Boolean = false
) {
    //<--
    //TODO(1): au click donne le tariff soit le choisi pour les orations de vents
     //<--
     //TODO(1): fait que au click un est selected  gere ui pour affiche le selected
    //<--
    //TODO(1): ajout un Prix_Progressive_Editable =Prix_Detaille + Prix_SupperGro_Et_PresentationService / 2 au click c un outlined //<--
    //TODO(1): il enregestre pouqeu ca soit un selected
    //<--
//TODO(1): fait que prix / p.u:
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

        // Get price
        val prix = matchingTariff?.prixCurrency ?: 0.0

        if (prix != 0.0) {
            tariffType to prix
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
        tariffsToDisplay.forEach { (tariffType, prix) ->
            TariffItem(
                tariffType = tariffType,
                prix = prix,
                compactMode = compactMode
            )
        }
    }
}

@Composable
private fun TariffItem(
    tariffType: M13TarificationInfos.TypeChoisi,
    prix: Double,
    compactMode: Boolean = false
) {
    // Adjust sizes based on compact mode
    val horizontalPadding = if (compactMode) 6.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp
    val iconSize = if (compactMode) 14.dp else 16.dp
    val fontSize = if (compactMode) 9.sp else 10.sp

    Row(
        modifier = Modifier
            .background(
                color = tariffType.couleur.copy(alpha = 0.9f),
                shape = CircleShape
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        tariffType.iconVector?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = tariffType.nomArabe,
                tint = tariffType.couleur_Text,
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape)
            )
        }

        // In compact mode, only show the abbreviated name (no full name)
        if (!compactMode) {
            Text(
                text = tariffType.abrgNom,
                color = tariffType.couleur_Text,
                fontSize = fontSize
            )
        }

        // Price (always shown)
        Text(
            text = String.format("%.0f", prix),
            color = tariffType.couleur_Text,
            fontSize = fontSize
        )
    }
}
