package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._A.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Produit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.formatTimestamp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TarificationTypeSection(
    typeTarification: Produit.Client.TypeTarification,
    showOnlyLatestPrices: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(typeTarification.timestamp)

    var currentTypeTarification by remember { mutableStateOf(typeTarification) }
    val typeId = currentTypeTarification.id  // Store ID for logging

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Type: ${currentTypeTarification.infos.type.name}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                IconButton(onClick = {
                    // Add logging to track price addition
                    logDebug("Add price button clicked for tarification type $typeId")

                    // Find the maximum price ID to prevent duplicates
                    val maxPriceId = currentTypeTarification.PrixsCurrency.maxOfOrNull { it.id } ?: 0
                    val newPriceId = maxPriceId + 1
                    logDebug("Creating new price with ID: $newPriceId (max was $maxPriceId)")

                    val timestamp = System.currentTimeMillis()
                    val newPrice = Produit.Client.TypeTarification.Prix(
                        id = newPriceId,
                        timestamp = timestamp,
                        valeur = 0.0
                    )

                    logDebug("New price created with timestamp: $timestamp")

                    // Ensure we're not adding duplicate price IDs
                    if (currentTypeTarification.PrixsCurrency.none { it.id == newPriceId }) {
                        logDebug("Adding new price to type $typeId")
                        currentTypeTarification = currentTypeTarification.copy(
                            PrixsCurrency = currentTypeTarification.PrixsCurrency + newPrice
                        )
                    } else {
                        logDebug("Price ID $newPriceId already exists! Not adding.")
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter un prix",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val pricesToShow = if (showOnlyLatestPrices) {
            // Get only the most recent price by timestamp
            logDebug("Showing only latest price for type $typeId")
            currentTypeTarification.PrixsCurrency
                .maxByOrNull { it.timestamp }
                ?.let { listOf(it) } ?: emptyList()
        } else {
            // Show all prices sorted by id and value
            logDebug("Showing all ${currentTypeTarification.PrixsCurrency.size} prices for type $typeId")
            currentTypeTarification.PrixsCurrency
                .sortedWith(compareBy({ it.id }, { it.valeur }))
        }

        pricesToShow.forEach { prix ->
            TarificationItem(prix = prix)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
