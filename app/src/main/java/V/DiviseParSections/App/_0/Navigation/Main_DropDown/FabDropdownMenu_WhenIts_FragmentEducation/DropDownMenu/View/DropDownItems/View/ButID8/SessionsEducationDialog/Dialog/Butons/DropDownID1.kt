package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SessionDate
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

/**
 * Button that adds absence observations for students who don't have any observation
 * for the selected session date
 */
@Composable
fun DropDownID1(
    sessionDate: SessionDate,
    sessionObservations: List<M20ObsarvationEtudion>,
    repo19Etudiant: Repo19Etudiant,
    setter: RepositorysMainSetter
) {
    var showJustificationDialog by remember { mutableStateOf(false) }
    var justificationText by remember { mutableStateOf("") }

    // Calculate missing students (those without observations for this session)
    val allStudents = repo19Etudiant.datasValue
    val studentsWithObservations = sessionObservations.map { it.etudiant_keyID }.toSet()
    val missingStudents = allStudents.filter { student ->
        student.keyID !in studentsWithObservations
    }

    if (missingStudents.isEmpty()) {
        // No missing students, show disabled button
        OutlinedButton(
            onClick = { },
            modifier = Modifier.Companion.fillMaxWidth(),
            enabled = false
        ) {
            Text("جميع الطلاب لديهم سجلات")
        }
        return
    }

    // Justification dialog
    if (showJustificationDialog) {
        AlertDialog(
            onDismissRequest = { showJustificationDialog = false },
            title = { Text("أدخل تبرير الغياب") },
            text = {
                OutlinedTextField(
                    value = justificationText,
                    onValueChange = { justificationText = it },
                    label = { Text("التبرير (اختياري)") },
                    placeholder = { Text("مثال: مرض، ظرف عائلي...") },
                    modifier = Modifier.Companion.fillMaxWidth(),
                    maxLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Add Raeeb observation for missing students with justification
                        missingStudents.forEach { student ->
                            val absenceObservation = M20ObsarvationEtudion(
                                etudiant_keyID = student.keyID,
                                sessionDateTimestamp = sessionDate.timestamp,
                                type = M20ObsarvationEtudion.Type.Raeeb,
                                tabrire_riyab = justificationText.trim(),
                                creationTimestamps = sessionDate.timestamp
                            )
                            setter.upsert_M20ObsarvationEtudion(absenceObservation)
                        }

                        showJustificationDialog = false
                        justificationText = ""
                    }
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showJustificationDialog = false
                    justificationText = ""
                }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Main button with two options
    OutlinedButton(
        onClick = {
            // Add absence without justification for missing students
            missingStudents.forEach { student ->
                val absenceObservation = M20ObsarvationEtudion(
                    etudiant_keyID = student.keyID,
                    sessionDateTimestamp = sessionDate.timestamp,
                    type = M20ObsarvationEtudion.Type.Raeeb,
                    tabrire_riyab = "",
                    creationTimestamps = sessionDate.timestamp
                )
                setter.upsert_M20ObsarvationEtudion(absenceObservation)
            }
        },
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text("غياب للطلاب المتبقين (${missingStudents.size}) - بدون تبرير")
    }

    // Button with justification
    OutlinedButton(
        onClick = {
            showJustificationDialog = true
        },
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text("غياب للطلاب المتبقين (${missingStudents.size}) - مع تبرير")
    }
}
