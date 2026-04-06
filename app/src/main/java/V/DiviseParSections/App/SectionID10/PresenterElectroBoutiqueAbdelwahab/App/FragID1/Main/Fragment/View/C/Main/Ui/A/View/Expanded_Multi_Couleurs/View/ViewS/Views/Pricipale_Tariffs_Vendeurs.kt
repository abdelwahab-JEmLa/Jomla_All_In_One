package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS.Views

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs(
    relative_M1produit: M01Produit,
    tariffsList: List<M13TarificationInfos>
) {
    // Define the tariff types to display in order
    val displayTariffs = listOf(
        M13TarificationInfos.TypeChoisi.LeMaxPrixArrive,
        M13TarificationInfos.TypeChoisi.Historique,
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
        M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
        M13TarificationInfos.TypeChoisi.Prix_Detaille
    )

    // Filter and prepare tariffs to display
    val tariffsToDisplay = displayTariffs.mapNotNull { tariffType ->
        // Find matching tariff for this product
        val matchingTariff = tariffsList.firstOrNull {
            it.typeChoisi == tariffType &&
                    it.parent_M1Produit_KeyId == relative_M1produit.keyID
        }

        // Get price
        val prix = matchingTariff?.prixCurrency ?: getDefaultPrice(tariffType, relative_M1produit)

        // Only include if price is not 0.0 and either tariff exists or should show default
        if (prix != 0.0 && (matchingTariff != null || shouldShowDefaultTariff(tariffType, relative_M1produit))) {
            tariffType to prix
        } else {
            null
        }
    }

    // Use FlowRow to wrap items when space is not available
    FlowRow(
        modifier = Modifier.padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tariffsToDisplay.forEach { (tariffType, prix) ->
            TariffItem(
                tariffType = tariffType,
                prix = prix
            )
        }
    }
}

@Composable
private fun TariffItem(
    tariffType: M13TarificationInfos.TypeChoisi,
    prix: Double
) {
    Row(
        modifier = Modifier
            .background(
                color = tariffType.couleur.copy(alpha = 0.9f),
                shape = CircleShape
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    .size(16.dp)
                    .clip(CircleShape)
            )
        }

        // Short name
        Text(
            text = tariffType.abrgNom,
            color = tariffType.couleur_Text,
            fontSize = 10.sp
        )

        // Price
        Text(
            text = String.format("%.0f", prix),
            color = tariffType.couleur_Text,
            fontSize = 10.sp
        )
    }
}

private fun shouldShowDefaultTariff(
    tariffType: M13TarificationInfos.TypeChoisi,
    produit: M01Produit
): Boolean {
    return when (tariffType) {
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
        M13TarificationInfos.TypeChoisi.Prix_Detaille -> true
        else -> false
    }
}

private fun getDefaultPrice(
    tariffType: M13TarificationInfos.TypeChoisi,
    produit: M01Produit
): Double {
    return when (tariffType) {
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> produit.prixVent
        M13TarificationInfos.TypeChoisi.Prix_Detaille -> produit.prixVent
        else -> 0.0
    }
}
