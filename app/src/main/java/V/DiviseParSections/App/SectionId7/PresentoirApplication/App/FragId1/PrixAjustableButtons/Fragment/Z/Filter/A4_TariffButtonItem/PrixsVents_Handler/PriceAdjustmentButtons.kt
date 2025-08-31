package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.A4_TariffButtonItem.PrixsVents_Handler

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriceAdjustmentButtons(
    currentApp_Est_Admin: Boolean,
    relative_Tariff: M13TarificationInfos,
    couleurButton: Color,
    textColor: Color,
    relative_Produit: ArticlesBasesStatsTable,
    onPriceChange: (Double) -> Unit
) {            //<--
//TODO(1): fait que ca soit comme BenificeAdjustmentButtons au click pri
    val decrease_Value = when {
        relative_Tariff.prixCurrency < 50.0 -> 1.0
        relative_Tariff.prixCurrency < 200.0 -> 5.0
        relative_Tariff.prixCurrency < 1000.0 -> 10.0
        else -> 25.0
    }

    // Decrease button
    if (currentApp_Est_Admin) {
        IconButton(
            onClick = {
                val newPrice = (relative_Tariff.prixCurrency - decrease_Value).coerceAtLeast(0.0)
                onPriceChange(newPrice)
            },
            modifier = Modifier.size(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Diminuer le prix de vente",
                tint = Color.Black
            )
        }
    }

    // Price display and increase button
    ElevatedCard(
        onClick = {
            if (currentApp_Est_Admin) {
                val newPrice = relative_Tariff.prixCurrency + decrease_Value
                onPriceChange(newPrice)
            }
        }
    ) {
        val pls = if (currentApp_Est_Admin) " +" else ""

        Column {
            Text(
                "${relative_Tariff.prixCurrency}$pls",
                modifier = Modifier
                    .background(couleurButton)
                    .padding(4.dp),
                color = textColor
            )

            val unitPrice = relative_Tariff.prixCurrency / relative_Produit.nombreUniteInt
            Text(
                "س.و: ${String.format("%.2f", unitPrice)}",
                modifier = Modifier
                    .background(couleurButton.copy(alpha = 0.6f))
                    .padding(2.dp),
                color = textColor,
                fontSize = 10.sp
            )
        }
    }
}
