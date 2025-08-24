package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dialog to display the list of products for a specific tariff type
 */
@Composable
 fun ProductsListDialog(
    tariffType: M13TarificationInfos.TypeChoisi,
    products: List<ArticlesBasesStatsTable>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tariffType.iconVector?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tariffType.couleur_Text,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${tariffType.nomArabe.ifBlank { tariffType.name }} - ${products.size} ${
                        get_BestNomArabDuPlurieul(
                            products.size
                        )
                    }",
                    style = MaterialTheme.typography.headlineSmall,
                    color = tariffType.couleur_Text
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItemCard(
                        product = product,
                        tariffType = tariffType
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "إغلاق",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Individual product card item for the dialog
 */
@Composable
private fun ProductItemCard(
    product: ArticlesBasesStatsTable,
    tariffType: M13TarificationInfos.TypeChoisi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = tariffType.couleur.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.nom,
                        style = MaterialTheme.typography.bodyMedium,
                        color = tariffType.couleur_Text,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "الكمية: ${product.nombreUniteInt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = tariffType.couleur_Text.copy(alpha = 0.7f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${product.prixVent.toInt()} دج",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tariffType.couleur_Text,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "س.و: ${
                            String.format(
                                "%.2f",
                                product.prixVent / product.nombreUniteInt
                            )
                        } دج",
                        style = MaterialTheme.typography.bodySmall,
                        color = tariffType.couleur_Text.copy(alpha = 0.7f)
                    )
                }
            }

            // Show profit margin if it's wholesale pricing
            if (tariffType == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService && product.prixAchat > 0) {
                val profitMargin =
                    ((product.prixVent - product.prixAchat) / product.prixAchat * 100)
                Text(
                    text = "هامش الربح: ${String.format("%.1f", profitMargin)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (profitMargin < 20) Color.Red else Color.Green,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
