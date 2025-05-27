package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment

import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun VendeurEditDialog(
    vendeur: _1_5_Vendeur,
    onDismiss: () -> Unit,
    onConfirm: (_1_5_Vendeur) -> Unit
) {
    // State for form fields
    var nom by remember { mutableStateOf(vendeur.nom) }
    var vid by remember { mutableStateOf(vendeur.vid.toString()) }
    var hideAppScreen by remember { mutableStateOf(vendeur.hideAppScreen) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Modifier le Vendeur",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = vid,
                    onValueChange = { vid = it },
                    label = { Text("ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Masquer l'écran de l'app:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { hideAppScreen = !hideAppScreen }
                    ) {
                        val icon = if (hideAppScreen) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        }

                        val tint = if (hideAppScreen) {
                            Color.Gray
                        } else {
                            MaterialTheme.colorScheme.primary
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = if (hideAppScreen) "Masqué" else "Visible",
                            tint = tint
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate and create updated vendeur
                    val updatedVid = vid.toLongOrNull() ?: vendeur.vid
                    val updatedVendeur = vendeur.copy(
                        vid = updatedVid,
                        nom = nom.trim(),
                        hideAppScreen = hideAppScreen
                    )
                    onConfirm(updatedVendeur)
                }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
