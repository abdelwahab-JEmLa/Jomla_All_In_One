package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Ui

import Z_CodePartageEntreApps.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DayHeader(
    tempTravaille: K_TempTravaille,
    viewModel: Windows__ViewModel
) {
    // State for dialog visibility
    var showTimeDialog by remember { mutableStateOf(false) }

    // States for time inputs
    var startTimeInput by remember { mutableStateOf("") }
    var endTimeInput by remember { mutableStateOf("") }

    val jour = tempTravaille.infosDeBase.dateInString

    // Parse the date for formatting
    val parsedDate = try {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        inputFormat.parse(jour) ?: Date()
    } catch (e: Exception) {
        Date()
    }

    // Format for full date display
    val formattedDate = try {
        val outputFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        outputFormat.format(parsedDate)
    } catch (e: Exception) {
        jour
    }

    // Get day of week (e.g., "Dimanche", "Lundi")
    val dayOfWeek = try {
        val dayFormat = SimpleDateFormat("EEEE", Locale.FRENCH)
        dayFormat.format(parsedDate).capitalize()
    } catch (e: Exception) {
        "Jour"
    }

    // Calculate total duration for the day
    val totalMinutes = tempTravaille.intervalesDeTravaille.sumOf { intervale ->
        K_TempTravaille.calculateDurationMinutes(intervale.tempDepart, intervale.temparrete)
    }

    val totalHours = totalMinutes / 60
    val remainingMinutes = totalMinutes % 60
    val totalDuration = if (totalMinutes > 0) "${totalHours}h ${remainingMinutes}m" else "Pas de temps enregistré"

    // Time Dialog
    if (showTimeDialog) {
        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Ajouter un temps manuel") },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    // First card for start time
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Temp Départ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            OutlinedTextField(
                                value = startTimeInput,
                                onValueChange = { startTimeInput = it },
                                placeholder = { Text("00.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Second card for end time
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Temp Arrêté",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            OutlinedTextField(
                                value = endTimeInput,
                                onValueChange = { endTimeInput = it },
                                placeholder = { Text("00.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Use updatePareMain to update the times
                        viewModel.updatePareMain(
                            recordId = tempTravaille.vid,
                            startTime = startTimeInput.takeIf { it.isNotEmpty() },
                            endTime = endTimeInput.takeIf { it.isNotEmpty() }
                        )
                        showTimeDialog = false
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showTimeDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Day of week card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Main header with date and duration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                // Edit Button
                IconButton(
                    onClick = {
                        startTimeInput = ""
                        endTimeInput = ""
                        showTimeDialog = true
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier le temps",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Time summary
                ElevatedCard(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Total: $totalDuration",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Extension function to capitalize the first letter of a string
private fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1)
    } else {
        ""
    }
}
