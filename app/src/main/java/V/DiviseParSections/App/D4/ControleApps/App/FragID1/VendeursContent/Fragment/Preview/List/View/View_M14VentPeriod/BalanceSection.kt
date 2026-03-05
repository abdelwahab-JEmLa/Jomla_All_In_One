package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import EntreApps.Shared.Models.M14VentPeriode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BalanceSection(
    balance: Double,
    totalVentes: Double,
    totalAchats: Double,
    totalProduitsDepot: Double,
    sum_Bon_Vents: Double,
    calculatedAchatTotal: Double,
    isLoadingCalculatedAchat: Boolean,
    relative_M14VentPeriode: M14VentPeriode,
    onSyncManualBalanceToSaved: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Manual Balance Card with Sync Button
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = when {
                    balance > 0 -> MaterialTheme.colorScheme.primaryContainer
                    balance < 0 -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚖️ BALANCE",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "(Manual)",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("%.2f", balance),
                            fontSize = 22.sp,
                            style = MaterialTheme.typography.headlineSmall,
                            color = when {
                                balance > 0 -> MaterialTheme.colorScheme.primary
                                balance < 0 -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        // Sync button to copy manual balance to saved_balance
                        IconButton(
                            onClick = onSyncManualBalanceToSaved
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Synchroniser vers saved balance",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Text(
                    text = "Ventes ($totalVentes) - Achats ($totalAchats) - Dépôt ($totalProduitsDepot)",
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Calculated Balance Card
        /*ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = when {
                    !isLoadingCalculatedAchat -> {
                        val calculatedBalance = sum_Bon_Vents - calculatedAchatTotal + totalProduitsDepot + relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period
                        when {
                            calculatedBalance > 0 -> MaterialTheme.colorScheme.tertiaryContainer
                            calculatedBalance < 0 -> MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.8f
                            )
                            else -> MaterialTheme.colorScheme.surfaceContainer
                        }
                    }
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📊 BALANCE ",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "(Calculated)",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    if (isLoadingCalculatedAchat) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "Calcul...",
                                fontSize = 10.sp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        val calculatedBalance = sum_Bon_Vents - calculatedAchatTotal + totalProduitsDepot + relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period
                        Text(
                            text = String.format("%.2f", calculatedBalance),
                            fontSize = 22.sp,
                            style = MaterialTheme.typography.headlineSmall,
                            color = when {
                                calculatedBalance > 0 -> MaterialTheme.colorScheme.tertiary
                                calculatedBalance < 0 -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }

                if (!isLoadingCalculatedAchat) {
                    Text(
                        text = "Ventes calc. (${
                            String.format(
                                "%.2f",
                                sum_Bon_Vents
                            )
                        }) - Achats calc. (${
                            String.format(
                                "%.2f",
                                calculatedAchatTotal
                            )
                        }) + Dépôt stagne ($totalProduitsDepot) + Ancien période (${relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period})",
                        fontSize = 10.sp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }       */

        // Saved Balance Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💾 SAVED BALANCE",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "(Sauvegardé)",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = String.format("%.2f", relative_M14VentPeriode.saved_balance),
                        fontSize = 22.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cliquez sur l'icône de synchronisation dans Balance Manual pour mettre à jour",
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
