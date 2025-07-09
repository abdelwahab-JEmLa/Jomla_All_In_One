package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Ui

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun VendeurEditDialog(
    vendeur: Z_AppCompt,
    onDismiss: () -> Unit = {},
    onConfirm: (Z_AppCompt) -> Unit = {}
) {
    val context = LocalContext.current

    // State for form fields
    val nom by remember { mutableStateOf(vendeur.nom) }
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

                RowcConnectAuDevelopingDataBase(
                    mainData,
                    onClick = {
                        mainData = it
                    }
                )

                MigreSonDataBaseAuStart1(
                    migreSonDataBaseAuStart = migreSonDataBaseAuStart,
                    onToggle = { migreSonDataBaseAuStart = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate and create updated vendeur
                    val updatedVendeur = vendeur.copy(
                        nom = nom.trim(),
                        hideAppScreen = hideAppScreen,
                        migreSonDataBaseAuStart = migreSonDataBaseAuStart,
                        cConnectAuDevelopingDataBaseAuRelodApp = mainData.cConnectAuDevelopingDataBaseAuRelodApp
                    )

                    // Show Toast with new database information
                    Toast.makeText(
                        context,
                        "Nouvelle base de données:\n" +
                                "Nom: ${updatedVendeur.nom}\n" +
                                "ID: ${updatedVendeur.vid}\n" +
                                "Écran masqué: ${if (updatedVendeur.hideAppScreen) "Oui" else "Non"}\n" +
                                "Migration au start: ${if (updatedVendeur.migreSonDataBaseAuStart) "Oui" else "Non"}\n" +
                                "Connect Dev DB: ${if (updatedVendeur.cConnectAuDevelopingDataBaseAuRelodApp) "Oui" else "Non"}",
                        Toast.LENGTH_LONG
                    ).show()

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

@Composable
private fun RowcConnectAuDevelopingDataBase(
    mainData: Z_AppCompt,
    onClick: (Z_AppCompt) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "cConnectAuDevelopingDataBase:",
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(
            onClick = {
                // Create addNew new copy with the toggled value instead of mutating
                val updatedData = mainData.copy(
                    cConnectAuDevelopingDataBaseAuRelodApp = !mainData.cConnectAuDevelopingDataBaseAuRelodApp
                )
                onClick(updatedData)
            }
        ) {
            val icon = if (mainData.cConnectAuDevelopingDataBaseAuRelodApp) {
                Icons.Default.NoAccounts
            } else {
                Icons.Default.DeviceUnknown
            }

            val tint = if (mainData.cConnectAuDevelopingDataBaseAuRelodApp) {
                Color.Gray
            } else {
                Color.Blue
            }

            Icon(
                imageVector = icon,
                contentDescription = if (mainData.cConnectAuDevelopingDataBaseAuRelodApp) "oui" else "non",
                tint = tint
            )
        }
    }
}

@Composable
private fun MigreSonDataBaseAuStart1(
    migreSonDataBaseAuStart: Boolean,
    onToggle: (Boolean) -> Unit = {}
) {
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
            onClick = { onToggle(!migreSonDataBaseAuStart) }
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
