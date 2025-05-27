package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MonPrixAchat(
    produit: A_ProduitInfosTest,
    onPrixUpdate: (Double) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PriceEditor(
        currentPrice = produit.monPrixAchat,
        label = "Achat",
        onPriceUpdate = onPrixUpdate,
        modifier = modifier,
        showOnlyWhenPositive = true,
        additionalInfo = {
            val benefice = produit.prixVent - produit.monPrixAchat
            Text(
                text = "Bénéfice: $benefice DA",
                style = MaterialTheme.typography.bodySmall,
                color = if (benefice > 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    )
}
