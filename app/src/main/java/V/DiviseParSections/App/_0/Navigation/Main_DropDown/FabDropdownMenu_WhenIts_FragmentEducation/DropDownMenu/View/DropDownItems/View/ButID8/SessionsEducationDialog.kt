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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun SessionsEducationDialog(
    selectedMonth: Calendar,
    repo20Observation: Repo20ObsarvationEtudion,
    onDismiss: () -> Unit
) {
    val sessionDates = remember(selectedMonth) {
        getSessionDatesForMonth(selectedMonth)
    }

    val monthDisplayName = remember(selectedMonth) {
        getMonthDisplayName(selectedMonth)
    }

    // Get all observations for this month (all students)
    val monthObservations = remember(repo20Observation.datasValue, selectedMonth) {
        repo20Observation.datasValue.filter { obs ->
            isSameDateInMonth(obs.sessionDateTimestamp, selectedMonth)
        }
    }

    var selectedSessionDate by remember { mutableStateOf<SessionDate?>(null) }

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
                        observations = monthObservations.filter { obs ->
                            isSameDay(obs.sessionDateTimestamp, sessionDate.timestamp)
                        },
                        onSessionClick = { selectedSessionDate = sessionDate }
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

    // Session action dialog
    selectedSessionDate?.let { sessionDate ->
        SessionActionDialog(
            sessionDate = sessionDate,
            repo20Observation = repo20Observation,
            sessionObservations = monthObservations.filter { obs ->
                isSameDay(obs.sessionDateTimestamp, sessionDate.timestamp)
            },
            onDismiss = { selectedSessionDate = null }
        )
    }
}

@Composable
private fun SessionDateCard(
    sessionDate: SessionDate,
    observations: List<M20ObsarvationEtudion>,
    onSessionClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - EEEE", Locale("ar"))
    val dateString = dateFormat.format(sessionDate.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSessionClick() },
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
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            if (observations.isEmpty()) {
                Text(
                    text = "لا توجد سجلات - اضغط للإضافة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "عدد السجلات: ${observations.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
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
private fun SessionActionDialog(
    sessionDate: SessionDate,
    repo20Observation: Repo20ObsarvationEtudion,
    sessionObservations: List<M20ObsarvationEtudion>,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - EEEE", Locale("ar"))
    val dateString = dateFormat.format(sessionDate.date)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("إجراءات الجلسة")
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (sessionObservations.isNotEmpty()) {
                    Text(
                        text = "عدد السجلات الحالية: ${sessionObservations.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "اختر الإجراء المناسب:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                if (sessionObservations.isNotEmpty()) {
                    Text(
                        text = "سيتم حذف جميع السجلات الموجودة قبل إضافة الغياب",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Delete all observations for this session, then add Raeeb for all students
                    sessionObservations.forEach { obs ->
                        repo20Observation.delete(obs)
                    }
                    // Note: You'll need to implement logic to add Raeeb for all students
                    // This requires access to the list of all students
                    onDismiss()
                }
            ) {
                Text("إضافة غياب للجميع")
            }
        },
        dismissButton = {
            Column {
                if (sessionObservations.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            // Delete all observations for this session
                            sessionObservations.forEach { obs ->
                                repo20Observation.delete(obs)
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("حذف جميع السجلات")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
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
