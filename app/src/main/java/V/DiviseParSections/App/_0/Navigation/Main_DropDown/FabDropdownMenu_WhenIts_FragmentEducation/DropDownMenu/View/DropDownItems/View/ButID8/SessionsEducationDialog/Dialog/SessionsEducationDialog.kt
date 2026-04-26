package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.SessionDate
import Application5.App.Repository.getMonthDisplayName
import Application5.App.Repository.getSessionDatesForMonth
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.Repo20ObsarvationEtudion
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedTextField
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
import java.util.Calendar
import java.util.Locale

// Replace the SessionActionDialog composable with this updated version

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
fun SessionDateCard(
    sessionDate: SessionDate,
    observations: List<M20ObsarvationEtudion>,
    onSessionClick: () -> Unit
) {
    // Calculate week number and session number within the month
    val calendar = Calendar.getInstance().apply {
        timeInMillis = sessionDate.timestamp
    }

    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("ar")) ?: ""

    // Calculate which week of the month (1-5)
    val weekOfMonth = ((dayOfMonth - 1) / 7) + 1

    // Calculate which session of the week (assuming 2 sessions per week: Thursday and Monday)
    // Adjust this logic based on your actual session schedule
    val dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK)
    val sessionOfWeek = when (dayOfWeekNum) {
        Calendar.MONDAY -> 1
        Calendar.THURSDAY -> 2
        else -> 1 // Default
    }

    // Format: "الأسبوع 1 - الحصة 1 - الاثنين 5"
    val displayText = "الأسبوع $weekOfMonth - الحصة $sessionOfWeek - $dayOfWeek $dayOfMonth"

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
                text = displayText,
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
 fun isSameDateInMonth(timestamp: Long, calendar: Calendar): Boolean {
    val obsDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    return obsDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
            obsDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
}

@Composable
 fun StudentObservationRow(
    student: M19Etudiant,
    sessionDate: SessionDate,
    setter: RepositorysMainSetter,
    repo20Observation: Repo20ObsarvationEtudion
) {
    // Get existing observation for this student on this date
    val existingObservation = remember(repo20Observation.datasValue, sessionDate, student) {
        repo20Observation.datasValue.find { obs ->
            obs.etudiant_keyID == student.keyID &&
                    isSameDay(obs.sessionDateTimestamp, sessionDate.timestamp)
        }
    }

    var observationType by remember(existingObservation) {
        mutableStateOf(existingObservation?.type)
    }
    var justificationText by remember(existingObservation) {
        mutableStateOf(existingObservation?.tabrire_riyab ?: "")
    }
    var showJustificationField by remember(existingObservation) {
        mutableStateOf(
            existingObservation?.type == M20ObsarvationEtudion.Type.Raeeb &&
                    existingObservation.tabrire_riyab.isNotBlank()
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (observationType) {
                M20ObsarvationEtudion.Type.Raeeb -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                M20ObsarvationEtudion.Type.Tama_Hifdoha,
                M20ObsarvationEtudion.Type.Moukarrar_Itmamouhou,
                M20ObsarvationEtudion.Type.Ousstad_kama_Bil_moundat ->
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                null -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Student name
            Text(
                text = student.nom,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            // Observation type buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Present button
                Button(
                    onClick = {
                        // Delete any existing Raeeb observation
                        existingObservation?.let { obs ->
                            if (obs.type == M20ObsarvationEtudion.Type.Raeeb) {
                                repo20Observation.delete(obs)
                            }
                        }

                        observationType = null
                        showJustificationField = false
                        justificationText = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (observationType != M20ObsarvationEtudion.Type.Raeeb)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("حاضر")
                }

                // Absent button (without justification)
                Button(
                    onClick = {
                        observationType = M20ObsarvationEtudion.Type.Raeeb
                        showJustificationField = false
                        justificationText = ""

                        // Save or update observation
                        val observation = M20ObsarvationEtudion(
                            keyID = existingObservation?.keyID ?: M20ObsarvationEtudion.generePushKey(),
                            etudiant_keyID = student.keyID,
                            sessionDateTimestamp = sessionDate.timestamp,
                            type = M20ObsarvationEtudion.Type.Raeeb,
                            tabrire_riyab = "",
                            creationTimestamps = existingObservation?.creationTimestamps ?: sessionDate.timestamp
                        )
                        setter.upsert_M20ObsarvationEtudion(observation)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (observationType == M20ObsarvationEtudion.Type.Raeeb && justificationText.isBlank())
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("غائب")
                }

                // Absent with justification button
                Button(
                    onClick = {
                        observationType = M20ObsarvationEtudion.Type.Raeeb
                        showJustificationField = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (observationType == M20ObsarvationEtudion.Type.Raeeb && justificationText.isNotBlank())
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("غائب مع تبرير")
                }
            }

            // Justification field (shown when absent with justification is selected)
            if (showJustificationField) {
                OutlinedTextField(
                    value = justificationText,
                    onValueChange = {
                        justificationText = it

                        // Save or update observation with justification
                        val observation = M20ObsarvationEtudion(
                            keyID = existingObservation?.keyID ?: M20ObsarvationEtudion.generePushKey(),
                            etudiant_keyID = student.keyID,
                            sessionDateTimestamp = sessionDate.timestamp,
                            type = M20ObsarvationEtudion.Type.Raeeb,
                            tabrire_riyab = it.trim(),
                            creationTimestamps = existingObservation?.creationTimestamps ?: sessionDate.timestamp
                        )
                        setter.upsert_M20ObsarvationEtudion(observation)
                    },
                    label = { Text("التبرير") },
                    placeholder = { Text("مثال: مرض، ظرف عائلي...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    singleLine = false
                )
            }

            // Show current observation status
            existingObservation?.let { obs ->
                Text(
                    text = when (obs.type) {
                        M20ObsarvationEtudion.Type.Raeeb -> {
                            if (obs.tabrire_riyab.isBlank()) "الحالة: غائب (بدون تبرير)"
                            else "الحالة: غائب - ${obs.tabrire_riyab}"
                        }
                        else -> "الحالة: له سجل آخر"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Helper function
 fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
