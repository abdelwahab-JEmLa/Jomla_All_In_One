package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravaille
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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

// Map for Arabic month names
private val arabicMonths = mapOf(
    "January" to "يناير",
    "February" to "فبراير",
    "March" to "مارس",
    "April" to "أبريل",
    "May" to "مايو",
    "June" to "يونيو",
    "July" to "يوليو",
    "August" to "أغسطس",
    "September" to "سبتمبر",
    "October" to "أكتوبر",
    "November" to "نوفمبر",
    "December" to "ديسمبر"
)

// Map for Arabic days of the week
private val arabicDays = mapOf(
    "Lundi" to "الاثنين",
    "Mardi" to "الثلاثاء",
    "Mercredi" to "الأربعاء",
    "Jeudi" to "الخميس",
    "Vendredi" to "الجمعة",
    "Samedi" to "السبت",
    "Dimanche" to "الأحد"
)

@Composable
fun DayHeader(
    tempTravaille: K_TempTravaille,
    viewModel: Windows__ViewModel
) {
    // Get admin state
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

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

    // Format for full date display with Arabic month
    val formattedDate = try {
        val englishFormat = SimpleDateFormat("d MMMM ", Locale.ENGLISH)
        val dateWithEnglishMonth = englishFormat.format(parsedDate)
        val parts = dateWithEnglishMonth.split(" ")

        if (parts.size >= 2) {
            val day = parts[0]
            val englishMonth = parts[1].trim()
            val arabicMonth = arabicMonths[englishMonth] ?: englishMonth
            "$day $arabicMonth"  // Arabic month name
        } else {
            dateWithEnglishMonth
        }
    } catch (e: Exception) {
        jour
    }

    // Get day of week in Arabic
    val dayOfWeek = try {
        val dayFormat = SimpleDateFormat("EEEE", Locale.FRENCH)
        val frenchDayName = dayFormat.format(parsedDate).capitalize()
        arabicDays[frenchDayName] ?: frenchDayName  // Use Arabic day name
    } catch (e: Exception) {
        "يوم"  // Default "Day" in Arabic
    }

    // Calculate total duration for the day
    val totalMinutes = tempTravaille.intervalesDeTravaille.sumOf { intervale ->
        K_TempTravaille.calculateDurationMinutes(intervale.tempDepart, intervale.temparrete)
    }

    val totalHours = totalMinutes / 60
    val remainingMinutes = totalMinutes % 60
    val totalDuration =
        if (totalMinutes > 0) "${totalHours}h ${remainingMinutes}m" else "لا وقت مسجل"  // "No time recorded" in Arabic

    // In DayHeader.kt, observe the editingInterval state
    val editingInterval by viewModel.editingInterval.collectAsState()

    // Modify the showTimeDialog initialization
    var showTimeDialog by remember { mutableStateOf(false) }

    // Set up a side effect to show the dialog when editingInterval is not null
    LaunchedEffect(editingInterval) {
        if (editingInterval != null) {
            startTimeInput = editingInterval?.tempDepart?.replace(":", ".") ?: ""
            endTimeInput = editingInterval?.temparrete?.replace(":", ".") ?: ""
            showTimeDialog = true
        }
    }

    // Time Dialog
    if (showTimeDialog) {
        // Add state for TypeTemp selection
        var selectedType by remember { mutableStateOf(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN) }
        var showTypeSelector by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("إضافة وقت يدوي") },  // "Add manual time" in Arabic
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
                                text = "وقت البدء",  // "Start Time" in Arabic
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
                                text = "وقت الانتهاء",  // "End Time" in Arabic
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

                    // Type selection button
                    Button(
                        onClick = { showTypeSelector = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = selectedType.icon,
                                contentDescription = null,
                                tint = selectedType.color,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("النوع: ${selectedType.nomArabe}")  // "Type:" in Arabic
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Show type selector dialog
                    if (showTypeSelector) {
                        AlertDialog(
                            onDismissRequest = { showTypeSelector = false },
                            title = { Text("اختر النوع") },  // "Select type" in Arabic
                            text = {
                                Column {
                                    K_TempTravaille.IntervalesDeTravaille.TypeTemp.values()
                                        .forEach { type ->
                                            Button(
                                                onClick = {
                                                    selectedType = type
                                                    showTypeSelector = false
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(
                                                        imageVector = type.icon,
                                                        contentDescription = null,
                                                        tint = type.color,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    )
                                                    Text(type.nomArabe)
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                }
                            },
                            confirmButton = {
                                Button(onClick = { showTypeSelector = false }) {
                                    Text("إلغاء")  // "Cancel" in Arabic
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingInterval != null) {
                            // Update the existing interval
                            viewModel.repository.updateExistingInterval(
                                recordId = tempTravaille.vid,
                                intervalId = editingInterval?.vid,
                                startTime = startTimeInput.takeIf { it.isNotEmpty() },
                                endTime = endTimeInput.takeIf { it.isNotEmpty() },
                                typeTemp = selectedType
                            )
                            viewModel.clearEditingInterval()
                        } else {
                            // Create a new interval
                            viewModel.updatePareMain(
                                recordId = tempTravaille.vid,
                                startTime = startTimeInput.takeIf { it.isNotEmpty() },
                                endTime = endTimeInput.takeIf { it.isNotEmpty() },
                                typeTemp = selectedType
                            )
                        }
                        showTimeDialog = false
                    }
                ) {
                    Text(if (editingInterval != null) "تحديث" else "تأكيد")  // "Update" or "Confirm" in Arabic
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showTimeDialog = false
                        viewModel.clearEditingInterval()
                    }
                ) {
                    Text("إلغاء")  // "Cancel" in Arabic
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

                // Edit Button - only show if in admin mode
                if (isAbdelwahabLeGerant) {
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
                            contentDescription = "تعديل الوقت",  // "Edit time" in Arabic
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Time summary
                ElevatedCard(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "ٌTotale == $totalDuration",  // "Total:" in Arabic
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
