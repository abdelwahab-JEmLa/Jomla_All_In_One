package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Finds the existing "Prix Détaillé" (retail price tariff) for a given product,
 * by looking up all tarification records associated with the product's ID and
 * returning the first one whose [M13TarificationInfos.TypeChoisi] corresponds to
 * a retail / detail pricing type.
 *
 * @return The matching [M13TarificationInfos] entry, or `null` if none is found.
 */

/**
 * Dialog to display the list of products with their associated tariff information
 */
@Composable
fun ProductsListDialog(
    tariffType: M13TarificationInfos.TypeChoisi,
    productTariffPairs: List<Pair<M01Produit, M13TarificationInfos>>,
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
                    text = "${tariffType.nomArabe.ifBlank { tariffType.name }} - ${productTariffPairs.size} ${
                        get_BestNomArabDuPlurieul(productTariffPairs.size)
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
                items(productTariffPairs) { (product, tariffInfo) ->
                    ProductItemCard(
                        product = product,
                        tariffInfo = tariffInfo,
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
 * Individual product card item for the dialog showing both product and tariff information
 */
@SuppressLint("DefaultLocale")
@Composable
private fun ProductItemCard(
    aCentralFacade: ACentralFacade= koinInject(),
    product: M01Produit,
    tariffInfo: M13TarificationInfos,
    tariffType: M13TarificationInfos.TypeChoisi
) {
    val existing_Prix_Detaille_Du_Produit=  find_existing_Prix_Detaille_Du_Produit(aCentralFacade,product)
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
            // Product header with name and quantity
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
                        text = "س.ت: ${existing_Prix_Detaille_Du_Produit?.prixCurrency?.toInt()?.toString() ?: "-"} دج",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tariffType.couleur_Text,
                        fontWeight = FontWeight.Bold
                    )

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

            // Tariff information section
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = tariffType.couleur.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Tariff details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "معلومات التسعيرة:",
                        style = MaterialTheme.typography.labelMedium,
                        color = tariffType.couleur_Text,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "نوع السعر: ${tariffInfo.typeChoisi.nomArabe}",
                        style = MaterialTheme.typography.bodySmall,
                        color = tariffType.couleur_Text.copy(alpha = 0.8f)
                    )

                    if (tariffInfo.prixCurrency > 0) {
                        Text(
                            text = "السعر المسجل: ${tariffInfo.prixCurrency.toInt()} دج",
                            style = MaterialTheme.typography.bodySmall,
                            color = tariffType.couleur_Text.copy(alpha = 0.8f)
                        )
                    }

                    // Creation date
                    if (tariffInfo.creationTimestamps > 0) {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val creationDate = Date(tariffInfo.creationTimestamps)
                        Text(
                            text = "تاريخ الإنشاء: ${dateFormat.format(creationDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = tariffType.couleur_Text.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Profitability indicator
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (tariffInfo.typeChoisi.isTopProfitable())
                                    Color.Green.copy(alpha = 0.2f)
                                else
                                    Color.Red.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (tariffInfo.typeChoisi.isTopProfitable()) "مربح" else "يحتاج تحسين",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (tariffInfo.typeChoisi.isTopProfitable()) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "نقاط الربح: ${tariffInfo.typeChoisi.profitabilityScore}",
                        style = MaterialTheme.typography.bodySmall,
                        color = tariffType.couleur_Text.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Profit margin calculation for wholesale pricing
            if (tariffType == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService && product.prixAchat > 0) {
                val profitMargin = ((product.prixVent - product.prixAchat) / product.prixAchat * 100)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "هامش الربح:",
                        style = MaterialTheme.typography.bodySmall,
                        color = tariffType.couleur_Text.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.1f", profitMargin)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (profitMargin < 20) Color.Red else Color.Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
