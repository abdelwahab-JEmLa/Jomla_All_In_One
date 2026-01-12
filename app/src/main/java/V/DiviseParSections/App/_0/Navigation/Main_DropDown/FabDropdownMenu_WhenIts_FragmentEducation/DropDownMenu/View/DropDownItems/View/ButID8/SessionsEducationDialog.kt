package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SessionDate
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.getMonthDisplayName
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.getSessionDatesForMonth
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
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
private fun SessionActionDialog(
    sessionDate: SessionDate,
    repo20Observation: Repo20ObsarvationEtudion,
    sessionObservations: List<M20ObsarvationEtudion>,
    onDismiss: () -> Unit,
    setter: RepositorysMainSetter = koinInject(),
    repo19Etudiant: Repo19Etudiant = koinInject()
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - EEEE", Locale("ar"))
    val dateString = dateFormat.format(sessionDate.date)

    var showActionButtons by remember { mutableStateOf(true) }

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
                if (showActionButtons) {
                    Text(
                        text = "اختر الإجراء المناسب:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Add Absence for All Button
                    Button(
                        onClick = {
                            // Delete existing observations first
                            sessionObservations.forEach { obs ->
                                repo20Observation.delete(obs)
                            }

                            // Add Raeeb (absence) observation for all students
                            val allStudents = repo19Etudiant.datasValue
                            allStudents.forEach { student ->
                                val absenceObservation = M20ObsarvationEtudion(
                                    etudiant_keyID = student.keyID,
                                    sessionDateTimestamp = sessionDate.timestamp,
                                    type = M20ObsarvationEtudion.Type.Raeeb,
                                    creationTimestamps = System.currentTimeMillis()
                                )
                                setter.upsert_M20ObsarvationEtudion(absenceObservation)
                            }
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("إضافة غياب للجميع")
                    }

                    // Add Individual Observations Button
                    OutlinedButton(
                        onClick = {
                            // Navigate to add observations screen
                            // You'll need to implement navigation logic here
                            showActionButtons = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة سجلات فردية")
                    }

                    if (sessionObservations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Delete All Records Button
                        OutlinedButton(
                            onClick = {
                                sessionObservations.forEach { obs ->
                                    repo20Observation.delete(obs)
                                }
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("حذف جميع السجلات")
                        }

                        Text(
                            text = "سيتم حذف جميع السجلات الموجودة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Text(
                        text = "يرجى تنفيذ منطق إضافة السجلات الفردية هنا",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        dismissButton = null
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
