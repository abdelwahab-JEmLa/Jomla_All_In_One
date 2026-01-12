package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SessionDate
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Button component that adds absence (Raeeb) for students who don't have any observation
 * for the selected session date.
 */
@Composable
fun AddAbsenceForMissingStudentsButton(
    sessionDate: SessionDate,
    sessionObservations: List<M20ObsarvationEtudion>,
    repo19Etudiant: Repo19Etudiant = koinInject(),
    setter: RepositorysMainSetter = koinInject()
) {
    var showJustificationDialog by remember { mutableStateOf(false) }
    var justificationText by remember { mutableStateOf("") }
    
    // Get students without observations for this date
    val allStudents = repo19Etudiant.datasValue
    val studentsWithObservations = sessionObservations.map { it.etudiant_keyID }.toSet()
    val missingStudents = allStudents.filter { it.keyID !in studentsWithObservations }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
    val dateString = dateFormat.format(sessionDate.date)

    // Show button only if there are missing students
    if (missingStudents.isNotEmpty()) {
        OutlinedButton(
            onClick = { showJustificationDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Column {
                Text("إضافة غياب للطلاب المتبقين")
                Text(
                    text = "(${missingStudents.size} طالب بدون سجل)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    // Justification Dialog
    if (showJustificationDialog) {
        AddAbsenceJustificationDialog(
            sessionDate = sessionDate,
            missingStudents = missingStudents,
            justificationText = justificationText,
            onJustificationChange = { justificationText = it },
            onConfirm = {
                // Add Raeeb for all missing students
                missingStudents.forEach { student ->
                    val absenceObservation = M20ObsarvationEtudion(
                        etudiant_keyID = student.keyID,
                        sessionDateTimestamp = sessionDate.timestamp,
                        type = M20ObsarvationEtudion.Type.Raeeb,
                        tabrire_riyab = justificationText.trim(),
                        creationTimestamps = System.currentTimeMillis()
                    )
                    setter.upsert_M20ObsarvationEtudion(absenceObservation)
                }
                
                // Reset state
                justificationText = ""
                showJustificationDialog = false
            },
            onDismiss = {
                justificationText = ""
                showJustificationDialog = false
            },
            dateString = dateString
        )
    }
}

@Composable
private fun AddAbsenceJustificationDialog(
    sessionDate: SessionDate,
    missingStudents: List<M19Etudiant>,
    justificationText: String,
    onJustificationChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dateString: String
) {
    var showStudentList by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Column {
                Text("إضافة غياب للطلاب المتبقين")
                Text(
                    text = dateString,
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
                Text(
                    text = "سيتم إضافة غياب لـ ${missingStudents.size} طالب ليس لديهم سجل لهذا اليوم:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Toggle to show/hide student list
                TextButton(
                    onClick = { showStudentList = !showStudentList }
                ) {
                    Text(
                        if (showStudentList) "إخفاء القائمة ▲" else "عرض قائمة الطلاب ▼"
                    )
                }
                
                // Student list (expandable)
                if (showStudentList) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(missingStudents) { student ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "• ${student.nom}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Justification input
                OutlinedTextField(
                    value = justificationText,
                    onValueChange = onJustificationChange,
                    label = { Text("التبرير (اختياري)") },
                    placeholder = { Text("مثال: مرض، ظرف عائلي، عطلة رسمية...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Text(
                    text = "ملاحظة: إذا تركت التبرير فارغاً، سيتم احتساب الغياب كغياب غير مبرر",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("تأكيد الغياب")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

/**
 * Helper function to get students without observations for a specific date
 */
fun getStudentsWithoutObservations(
    allStudents: List<M19Etudiant>,
    sessionObservations: List<M20ObsarvationEtudion>
): List<M19Etudiant> {
    val studentsWithObservations = sessionObservations.map { it.etudiant_keyID }.toSet()
    return allStudents.filter { it.keyID !in studentsWithObservations }
}
