package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    tempTravaille: K_TempTravaille,
    viewModel: RecordingViewModel
) {
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

    var relative_temp_travaille_abdelmoumen by remember {
        mutableStateOf(K_TempTravaille.IntervalesDeTravaille.get_default())
    }
    var relative_temp_travaille_walid by remember {
        mutableStateOf(K_TempTravaille.IntervalesDeTravaille.get_default())
    }

    var startTimeInput by remember { mutableStateOf("") }
    var endTimeInput by remember { mutableStateOf("") }

    var startTimeInput_walid by remember { mutableStateOf("") }
    var endTimeInput_walid by remember { mutableStateOf("") }

    val jour = tempTravaille.infosDeBase.dateInString

    // FIXED: Parse the date from tempTravaille, not current date
    val parsedDate = try {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        inputFormat.parse(jour) ?: Date()
    } catch (e: Exception) {
        // If parsing fails, try with underscore format (yyyy_MM_dd)
        try {
            val inputFormat = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
            inputFormat.parse(jour) ?: Date()
        } catch (e2: Exception) {
            Date()
        }
    }

    val arabicMonths = mapOf(
        "January" to "جانفي",
        "February" to "فيفري",
        "March" to "مارس",
        "April" to "أفريل",
        "May" to "ماي",
        "June" to "جوان",
        "July" to "جويلية",
        "August" to "أوت",
        "September" to "سبتمبر",
        "October" to "أكتوبر",
        "November" to "نوفمبر",
        "December" to "ديسمبر"
    )

    // Format for full date display with Arabic month - using parsedDate from tempTravaille
    val formattedDate = try {
        val englishFormat = SimpleDateFormat("d MMMM", Locale.ENGLISH)
        val dateWithEnglishMonth = englishFormat.format(parsedDate)
        val parts = dateWithEnglishMonth.split(" ")

        if (parts.size >= 2) {
            val day = parts[0]
            val englishMonth = parts[1].trim()
            val arabicMonth = arabicMonths[englishMonth] ?: englishMonth
            "$day $arabicMonth"
        } else {
            dateWithEnglishMonth
        }
    } catch (e: Exception) {
        jour
    }

    // Get day of week in Arabic from parsedDate (not current date)
    val dayOfWeek = try {
        val dayFormat = SimpleDateFormat("EEEE", Locale.FRENCH)
        val frenchDayName = dayFormat.format(parsedDate).capitalize()
        arabicDays[frenchDayName] ?: frenchDayName
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

    val editingInterval by viewModel.editingInterval.collectAsState()

    var showTimeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(editingInterval) {
        if (editingInterval != null) {
            startTimeInput = editingInterval?.tempDepart?.replace(":", ".") ?: ""
            endTimeInput = editingInterval?.temparrete?.replace(":", ".") ?: ""
            showTimeDialog = true
        }
    }
    var selectedType by remember { mutableStateOf(K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN) }

    val updated_relative_temp_travaille_abdelmoumen = relative_temp_travaille_abdelmoumen.apply {
        tempDepart =
            startTimeInput.replace(".", ":").takeIf { it.isNotEmpty() }
                ?: "09:30"
        temparrete =
            endTimeInput.replace(".", ":").takeIf { it.isNotEmpty() }
                ?: "10:30"
        typeTemp = selectedType
    }

    val updated_relative_temp_travaille_walid = relative_temp_travaille_walid.apply {
        tempDepart =
            startTimeInput_walid.replace(".", ":").takeIf { it.isNotEmpty() }
                ?: "08:30"
        temparrete =
            endTimeInput_walid.replace(".", ":").takeIf { it.isNotEmpty() }
                ?: "10:30"
        typeTemp = selectedType
    }

    if (showTimeDialog) {
        var showTypeSelector by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("إضافة وقت يدوي") },
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
                                text = "وقت البدء",  // "Start Time"
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = startTimeInput,
                                    onValueChange = { startTimeInput = it },
                                    placeholder = { Text("00.00") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    label = { Text("عبدالمؤمن") }
                                )

                                OutlinedTextField(
                                    value = startTimeInput_walid,
                                    onValueChange = { startTimeInput_walid = it },
                                    placeholder = { Text("00.00") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    label = { Text("وليد") }
                                )
                            }
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
                                text = "وقت الانتهاء",  // "End Time"
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = endTimeInput,
                                    onValueChange = { endTimeInput = it },
                                    placeholder = { Text("00.00") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    label = { Text("عبدالمؤمن") }
                                )

                                OutlinedTextField(
                                    value = endTimeInput_walid,
                                    onValueChange = { endTimeInput_walid = it },
                                    placeholder = { Text("00.00") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    label = { Text("وليد") }
                                )
                            }
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
                            Text("النوع: ${selectedType.nomArabe}")
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
                                    K_TempTravaille.IntervalesDeTravaille.TypeTemp.entries
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
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(
                                key = SemanticsPropertyKey("relative_temp_travaille_abdelmoumen"),
                                value = updated_relative_temp_travaille_abdelmoumen
                            )
                        },
                    onClick = {
                        val list_itervaless = buildList {
                            add(updated_relative_temp_travaille_abdelmoumen)
                            add(updated_relative_temp_travaille_walid)
                        }
                        viewModel.repository.addNewIntervals_au_TempTravaille(
                            tempTravaille,
                            list_itervaless
                        )

                        showTimeDialog = false
                    }
                ) {
                    Text(if (editingInterval != null) "تحديث" else "تأكيد")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showTimeDialog = false
                        viewModel.clearEditingInterval()
                    }
                ) {
                    Text("إلغاء")
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
                        text = "المجموع: $totalDuration",  // "Total:" in Arabic
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
