package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MinorCrash
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun VP() {
    VendeurEditDialog(_1_5_Vendeur(

    ))
}

@Composable
fun VendeurEditDialog(
    vendeur: _1_5_Vendeur,
    onDismiss: () -> Unit = {},
    onConfirm: (_1_5_Vendeur) -> Unit = {}
) {
    // State for form fields
    val nom by remember { mutableStateOf(vendeur.nom) }
    val vid by remember { mutableStateOf(vendeur.vid.toString()) }
    var hideAppScreen by remember { mutableStateOf(vendeur.hideAppScreen) }
    var migreSonDataBaseAuStart by remember { mutableStateOf(vendeur.migreSonDataBaseAuStart) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Modifier Vendeur Options",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Masquer l'écran de l'app:",
                        style = MaterialTheme.typography.bodyMedium
                    )
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "migreOldDataBaseAuStart:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(
                        onClick = { migreSonDataBaseAuStart = !migreSonDataBaseAuStart }
                    ) {
                        val icon = if (migreSonDataBaseAuStart) {
                            Icons.Default.NoAccounts
                        } else {
                            Icons.Default.MinorCrash
                        }

                        val tint = if (migreSonDataBaseAuStart) {
                            Color.Gray
                        } else {
                            Color.Blue
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = if (migreSonDataBaseAuStart) "oui" else "non",
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
                        hideAppScreen = hideAppScreen,
                        migreSonDataBaseAuStart = migreSonDataBaseAuStart
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
