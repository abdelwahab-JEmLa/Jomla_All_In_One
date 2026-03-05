package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import EntreApps.Shared.Models.M14VentPeriode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun Produits_Ancien_Period(
    relative_M14VentPeriode: M14VentPeriode,
    editingField: String?,
    editingValue: String,
    onStartEditing: (String, Double) -> Unit,
    onEditingValueChange: (String) -> Unit,
    onSaveEditedValue: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier.Companion
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.Companion.padding(12.dp)
        ) {
            Text(
                text = "📦 Produits Ancien Période",
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                text = "Produits restants des périodes précédentes",
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.Companion.height(8.dp))

            // Use the correct field for ancient products
            if (editingField == "ancien_produits") {
                OutlinedTextField(
                    value = editingValue,
                    onValueChange = onEditingValueChange,
                    label = { Text("Valeur des produits anciens") },
                    placeholder = { Text("0.0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Companion.Decimal,
                        imeAction = ImeAction.Companion.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSaveEditedValue() }
                    ),
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    suffix = { Text("DA") }
                )
            } else {
                Card(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .clickable {
                            onStartEditing(
                                "ancien_produits",
                                relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period // Use the correct field
                            )
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "💼 Valeur totale",
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Cliquez pour modifier",
                                fontSize = 11.sp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "${relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period} DA", // Use the correct field
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.Companion.height(4.dp))

            Text(
                text = "Cette valeur sera ajoutée aux calculs automatiquement",
                fontSize = 10.sp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
            )
        }
    }
}
