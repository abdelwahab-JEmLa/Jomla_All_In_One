package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SessionDate
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.getMonthDisplayName
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.getSessionDatesForMonth
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionsEducationDialog(
    selectedMonth: Calendar,
    etudiant: M19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion,
    onDismiss: () -> Unit
) {
    val sessionDates = remember(selectedMonth) {
        getSessionDatesForMonth(selectedMonth)
    }
    
    val monthDisplayName = remember(selectedMonth) {
        getMonthDisplayName(selectedMonth)
    }

    // Get all observations for this student in this month
    val studentObservations = remember(repo20Observation.datasValue, selectedMonth) {
        repo20Observation.datasValue.filter { obs ->
            obs.etudiant_keyID == etudiant.keyID &&
            isSameDateInMonth(obs.creationTimestamps, selectedMonth)
        }
    }

    var showAddObservationDialog by remember { mutableStateOf<SessionDate?>(null) }
    var observationToDelete by remember { mutableStateOf<Pair<SessionDate, M20ObsarvationEtudion>?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "سجلات الحضور والتقييم",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${etudiant.nom} ${etudiant.prenom}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = monthDisplayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessionDates) { sessionDate ->
                    SessionDateCard(
                        sessionDate = sessionDate,
                        observations = studentObservations.filter { obs ->
                            isSameDay(obs.creationTimestamps, sessionDate.timestamp)
                        },
                        onAddObservation = { showAddObservationDialog = sessionDate },
                        onDeleteObservation = { obs ->
                            observationToDelete = Pair(sessionDate, obs)
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )

    // Add observation dialog
    showAddObservationDialog?.let { sessionDate ->
        AddObservationDialog(
            sessionDate = sessionDate,
            etudiant = etudiant,
            repo20Observation = repo20Observation,
            onDismiss = { showAddObservationDialog = null }
        )
    }

    // Delete confirmation dialog
    observationToDelete?.let { (sessionDate, observation) ->
        AlertDialog(
            onDismissRequest = { observationToDelete = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل تريد حذف هذا السجل؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        repo20Observation.delete(observation)
                        observationToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { observationToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SessionDateCard(
    sessionDate: SessionDate,
    observations: List<M20ObsarvationEtudion>,
    onAddObservation: () -> Unit,
    onDeleteObservation: (M20ObsarvationEtudion) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - EEEE", Locale("ar"))
    val dateString = dateFormat.format(sessionDate.date)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (observations.isEmpty()) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onAddObservation,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة سجل",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (observations.isEmpty()) {
                Text(
                    text = "لا توجد سجلات",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                observations.forEach { observation ->
                    ObservationItem(
                        observation = observation,
                        onDelete = { onDeleteObservation(observation) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ObservationItem(
    observation: M20ObsarvationEtudion,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (observation.type) {
                    M20ObsarvationEtudion.Type.Raeeb -> "غائب"
                    M20ObsarvationEtudion.Type.Tama_Hifdoha -> "تم حفظها"
                    M20ObsarvationEtudion.Type.Moukarrar_Itmamouhou -> "مكرر إتمامه"
                    M20ObsarvationEtudion.Type.Ousstad_kama_Bil_moundat -> "أستاذ قام بالمناداة"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (observation.type == M20ObsarvationEtudion.Type.Raeeb) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            
            if (observation.type != M20ObsarvationEtudion.Type.Raeeb) {
                Text(
                    text = "التقييم: ${observation.takyim.arabicName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (observation.tabrire_riyab.isNotBlank()) {
                Text(
                    text = "المبرر: ${observation.tabrire_riyab}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "حذف",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AddObservationDialog(
    sessionDate: SessionDate,
    etudiant: M19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(M20ObsarvationEtudion.Type.Tama_Hifdoha) }
    var tabrireRiyab by remember { mutableStateOf("") }
    var showTabrireInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("إضافة سجل جديد")
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(sessionDate.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("نوع السجل:", fontWeight = FontWeight.Bold)
                
                M20ObsarvationEtudion.Type.values().forEach { type ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                        Text(
                            text = when (type) {
                                M20ObsarvationEtudion.Type.Raeeb -> "غائب"
                                M20ObsarvationEtudion.Type.Tama_Hifdoha -> "تم حفظها"
                                M20ObsarvationEtudion.Type.Moukarrar_Itmamouhou -> "مكرر إتمامه"
                                M20ObsarvationEtudion.Type.Ousstad_kama_Bil_moundat -> "أستاذ قام بالمناداة"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (selectedType == M20ObsarvationEtudion.Type.Raeeb) {
                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("إضافة مبرر للغياب:")
                        Switch(
                            checked = showTabrireInput,
                            onCheckedChange = { showTabrireInput = it }
                        )
                    }

                    if (showTabrireInput) {
                        String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings(
                            start_text = tabrireRiyab,
                            placeholder = "أدخل المبرر",
                            icon = Icons.Default.Note,
                            modifier = Modifier.fillMaxWidth(),
                            on_DonneClick_Data_Update = { tabrireRiyab = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newObservation = M20ObsarvationEtudion(
                        type = selectedType,
                        tabrire_riyab = if (selectedType == M20ObsarvationEtudion.Type.Raeeb) tabrireRiyab else "",
                        etudiant_keyID = etudiant.keyID,
                        parent_ousstad_key = etudiant.parent_ousstad_key,
                        creationTimestamps = sessionDate.timestamp
                    )
                    repo20Observation.add(newObservation)
                    onDismiss()
                }
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

private fun isSameDateInMonth(timestamp: Long, calendar: Calendar): Boolean {
    val obsDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    return obsDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
            obsDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
}

private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
