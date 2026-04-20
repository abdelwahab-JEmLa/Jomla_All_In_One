package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VentesSection(
    relative_M14VentPeriode: M14VentPeriode,
    editingField: String?,
    editingValue: String,
    sum_Bon_Vents: Double,
    totalVentes: Double,
    onStartEditing: (String, Double) -> Unit,
    onEditingValueChange: (String) -> Unit,
    onSaveEditedValue: () -> Unit,
    onCalculatedClick: () -> Unit,
    onSyncCalculatedToManual: () -> Unit = {},
    focusRequester: FocusRequester
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.weight(2f),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰 VENTES (Manual)",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Sync button to copy calculated to manual
                    IconButton(
                        onClick = onSyncCalculatedToManual
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Synchroniser avec calcul",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Credit Ventes
                    Column(modifier = Modifier.weight(1f)) {
                        if (editingField == "credit_vents") {
                            OutlinedTextField(
                                value = editingValue,
                                onValueChange = onEditingValueChange,
                                label = { Text("Crédit") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { onSaveEditedValue() }
                                ),
                                modifier = Modifier.focusRequester(focusRequester)
                            )
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onStartEditing(
                                            "credit_vents",
                                            relative_M14VentPeriode.credit_Vents_Totale
                                        )
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "💳 Crédit",
                                        fontSize = 12.sp,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "${relative_M14VentPeriode.credit_Vents_Totale}",
                                        fontSize = 14.sp,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Cash Ventes
                    Column(modifier = Modifier.weight(1f)) {
                        if (editingField == "cash_vents") {
                            OutlinedTextField(
                                value = editingValue,
                                onValueChange = onEditingValueChange,
                                label = { Text("Cash") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { onSaveEditedValue() }
                                ),
                                modifier = Modifier.focusRequester(focusRequester)
                            )
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onStartEditing(
                                            "cash_vents",
                                            relative_M14VentPeriode.cash_Vents_Totale
                                        )
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "💵 Cash",
                                        fontSize = 12.sp,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "${relative_M14VentPeriode.cash_Vents_Totale}",
                                        fontSize = 14.sp,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Text(
                    text = "Total: $totalVentes",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Calculated Ventes Card
        ElevatedCard(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📊 Calculated",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ventes",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.clickable { onCalculatedClick() },
                    text = "$sum_Bon_Vents",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
