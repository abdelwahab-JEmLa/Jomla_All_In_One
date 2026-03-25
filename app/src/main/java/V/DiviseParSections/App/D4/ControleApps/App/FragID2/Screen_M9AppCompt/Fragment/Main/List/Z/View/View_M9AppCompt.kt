package V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.Main.List.Z.View

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ScreenM14VentPeriod
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.ViewModel_M9AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Repo18CentralParametresOfAllApps
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject

@Composable
fun View_M9AppCompt(
    relative_M9AppCompt: Z_AppCompt,
    viewModel: ViewModel_M9AppCompt,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps = aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps,
) {
    val au_Lence_Set_Compt_Ac_KeyId =
        repo18CentralParametresOfAllApps
            .dataValue
            ?.au_Lence_Set_Compt_Ac_KeyId

    val currentActive_AppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val relative_Compt_its_The_Active_One =
        relative_M9AppCompt.keyID == (currentActive_AppCompt?.keyID ?: "")

    var showDialog by remember { mutableStateOf(false) }

    val backgroundColor = when {
        relative_Compt_its_The_Active_One -> Color.Red
        else -> MaterialTheme.colorScheme.surface
    }

    val heurDebutInString = "Now Test HH:mm"

    Column(
        modifier = Modifier
            .getSemanticsTag(au_Lence_Set_Compt_Ac_KeyId, "au_Lence_Set_Compt_Ac_KeyId")
            .getSemanticsTag(relative_M9AppCompt, "")
            .fillMaxWidth()
            .clickable {
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (relative_Compt_its_The_Active_One) {
            Text(
                text = "Selected Data",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "data: ${relative_M9AppCompt.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = if (relative_Compt_its_The_Active_One) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Heure de début: $heurDebutInString",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = if (relative_Compt_its_The_Active_One) Color.White else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            viewModel = viewModel,
            relative_M9AppCompt = relative_M9AppCompt,
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
    relative_M9AppCompt: Z_AppCompt,
    onDismiss: () -> Unit,
    onUpdateAppCompt: (Z_AppCompt) -> Unit,
    viewModel: ViewModel_M9AppCompt,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
) {
    var hideAppScreen by remember { mutableStateOf(relative_M9AppCompt.hideAppScreen) }
    var travailleChezGrossisst3Ali by remember { mutableStateOf(relative_M9AppCompt.travailleChezGrossisst3Ali) }
    var itsAdmin by remember { mutableStateOf(relative_M9AppCompt.its_Admin) }
    var showPeriodSelector by remember { mutableStateOf(false) }

    // State for credit_fait
    var creditFait by remember { mutableStateOf(relative_M9AppCompt.credit_fait.toString()) }

    var showWarningMessage by remember {
        mutableStateOf(relative_M9AppCompt.text_Message_Warning.isNotEmpty())
    }

    AlertDialog(
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
                // Credit Fait TextField
                Text(
                    text = "💳 الائتمان المقدم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = creditFait,
                    onValueChange = { newValue ->
                        // Allow only numeric input with optional decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            creditFait = newValue
                        }
                    },
                    label = { Text("الرصيد (دج)") },
                    placeholder = { Text("0.0") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = creditFait.toDoubleOrNull() == null && creditFait.isNotEmpty()
                )

                if (creditFait.isNotEmpty() && creditFait.toDoubleOrNull() == null) {
                    Text(
                        text = "يرجى إدخال رقم صحيح",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Warning Message:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (showWarningMessage) {
                            Text(
                                text = "لا تنسى اغلاق الوقت",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            showWarningMessage = !showWarningMessage
                            val updatedAppCompt = relative_M9AppCompt.copy(
                                text_Message_Warning = if (showWarningMessage) "لا تنسى اغلاق الوقت" else ""
                            )
                            viewModel.aCentralFacade.repositorysMainSetter.update_M9AppCompt(updatedAppCompt)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showWarningMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(if (showWarningMessage) "ON" else "OFF")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Select Period:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Button(
                        onClick = { showPeriodSelector = true }
                    ) {
                        Text("Select Period")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val creditFaitValue = creditFait.toDoubleOrNull() ?: relative_M9AppCompt.credit_fait
                    val updatedAppCompt = relative_M9AppCompt.copy(
                        hideAppScreen = hideAppScreen,
                        travailleChezGrossisst3Ali = travailleChezGrossisst3Ali,
                        its_Admin = itsAdmin,
                        text_Message_Warning = if (showWarningMessage) "لا تنسى اغلاق الوقت" else "",
                        credit_fait = creditFaitValue
                    )
                    onUpdateAppCompt(updatedAppCompt)
                },
                enabled = creditFait.isEmpty() || creditFait.toDoubleOrNull() != null
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

    // Period Selection Dialog
    if (showPeriodSelector) {
        Dialog(
            onDismissRequest = { showPeriodSelector = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Period",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Use the existing ScreenM14VentPeriod
                    ScreenM14VentPeriod(
                        modifier = Modifier.weight(1f),
                        viewModel = koinInject<ViewModel_M14VentPeriod>(),
                        relative_M9AppCompt = relative_M9AppCompt,
                    )
                }
            }
        }
    }
}
