package V.DiviseParSections.App.D4.ControleApps.App.FragID2.D.Fragment

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun View_M9AppCompt(
    relative_M9AppCompt: Z_AppCompt,
    viewModel: ViewModel_M9AppCompt,
) {
    var showDialog by remember { mutableStateOf(false) }

    val focusedActiveValuesFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
    val currentActiveFocuced_M14VentPeriode = focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
    val active = (currentActiveFocuced_M14VentPeriode?.keyID ?: "") == relative_M9AppCompt.keyID

    val backgroundColor = when {
        active -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val heurDebutInString = "Now Test HH:mm"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (active) {
            Text(
                text = "Selected Periode",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "m14VentPeriode: ${relative_M9AppCompt.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Heure de début: $heurDebutInString",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Settings button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Settings")
        }
    }

    // Settings Dialog
    if (showDialog) {
        SettingsDialog(
            viewModel=viewModel,
            currentAppCompt = relative_M9AppCompt,
            onDismiss = { showDialog = false },
            onUpdateAppCompt = { updatedAppCompt ->
                viewModel.aCentralFacade.repositorysMainSetter.update_M9AppCompt(updatedAppCompt)
                showDialog = false
            }
        )
    }
}

@Composable
private fun SettingsDialog(
    currentAppCompt: Z_AppCompt,
    onDismiss: () -> Unit,
    onUpdateAppCompt: (Z_AppCompt) -> Unit,
    viewModel: ViewModel_M9AppCompt ,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
) {
    var hideAppScreen by remember { mutableStateOf(currentAppCompt.hideAppScreen) }
    var travailleChezGrossisst3Ali by remember { mutableStateOf(currentAppCompt.travailleChezGrossisst3Ali) }
    var itsAdmin by remember { mutableStateOf(currentAppCompt.its_Admin) }
   val datas_Vent = aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue
    AlertDialog(
        //<--
        //TODO(1): ajout un button au click affceh dialoge contien datas_Vent au clcik update compt  var current_OnVent_M14VentPeriode_KeyID: String = "",
        //    var current_OnVent_M14VentPeriode_DebugInfos: String = "", ==get debug
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hide App Screen Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hide App Screen:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { hideAppScreen = !hideAppScreen },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hideAppScreen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(if (hideAppScreen) "ON" else "OFF")
                    }
                }

                // Travaille Chez Grossist3Ali Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Travaille Chez Grossist3Ali:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { travailleChezGrossisst3Ali = !travailleChezGrossisst3Ali },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (travailleChezGrossisst3Ali) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(if (travailleChezGrossisst3Ali) "ON" else "OFF")
                    }
                }

                // Admin Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Admin:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { itsAdmin = !itsAdmin },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (itsAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(if (itsAdmin) "ON" else "OFF")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedAppCompt = currentAppCompt.copy(
                        hideAppScreen = hideAppScreen,
                        travailleChezGrossisst3Ali = travailleChezGrossisst3Ali,
                        its_Admin = itsAdmin
                    )
                    onUpdateAppCompt(updatedAppCompt)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
