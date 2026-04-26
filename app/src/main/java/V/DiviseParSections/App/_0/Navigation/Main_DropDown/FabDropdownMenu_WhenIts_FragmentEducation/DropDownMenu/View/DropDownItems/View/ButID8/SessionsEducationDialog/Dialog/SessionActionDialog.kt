package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel.Repo19Etudiant
import Application5.App.Repository.SessionDate
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.Repo20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons.DeleteObservationsBeforeDateButton
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons.DropDownID1
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons.DropDownID2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons.DropDownID3
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SessionActionDialog(
    sessionDate: SessionDate,
    repo20Observation: Repo20ObsarvationEtudion,
    sessionObservations: List<M20ObsarvationEtudion>,
    onDismiss: () -> Unit,
    setter: RepositorysMainSetter = koinInject(),
    repo19Etudiant: Repo19Etudiant = koinInject()
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - EEEE", Locale("ar"))
    val dateString = dateFormat.format(sessionDate.date)

    var showJustificationDialog by remember { mutableStateOf(false) }
    var justificationText by remember { mutableStateOf("") }
    var showIndividualInput by remember { mutableStateOf(false) }

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
                        sessionObservations.forEach { obs ->
                            repo20Observation.delete(obs)
                        }

                        val allStudents = repo19Etudiant.datasValue
                        allStudents.forEach { student ->
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
                        onDismiss()
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
                modifier = Modifier.Companion.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "اختر الإجراء المناسب:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Companion.Bold
                )

                Button(
                    onClick = {
                        sessionObservations.forEach { obs ->
                            repo20Observation.delete(obs)
                        }

                        val allStudents = repo19Etudiant.datasValue
                        allStudents.forEach { student ->
                            val absenceObservation = M20ObsarvationEtudion(
                                etudiant_keyID = student.keyID,
                                sessionDateTimestamp = sessionDate.timestamp,
                                type = M20ObsarvationEtudion.Type.Raeeb,
                                tabrire_riyab = "",
                                creationTimestamps = sessionDate.timestamp
                            )
                            setter.upsert_M20ObsarvationEtudion(absenceObservation)
                        }
                        onDismiss()
                    },
                    modifier = Modifier.Companion.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("غياب للجميع (بدون تبرير)")
                }

                OutlinedButton(
                    onClick = {
                        showJustificationDialog = true
                    },
                    modifier = Modifier.Companion.fillMaxWidth()
                ) {
                    Text("غياب للجميع (مع تبرير)")
                }

                DropDownID1(
                    sessionDate = sessionDate,
                    sessionObservations = sessionObservations,
                    repo19Etudiant = repo19Etudiant,
                    setter = setter
                )

                DropDownID2(
                    sessionDate = sessionDate,
                    repo20Observation = repo20Observation,
                    sessionObservations = sessionObservations
                )

                DropDownID3(
                    sessionDate = sessionDate,
                    repo20Observation = repo20Observation,
                    sessionObservations = sessionObservations
                )

                DeleteObservationsBeforeDateButton(
                    sessionDate = sessionDate,
                    repo20Observation = repo20Observation
                )

                if (!showIndividualInput) {
                    OutlinedButton(
                        onClick = {
                            showIndividualInput = true
                        },
                        modifier = Modifier.Companion.fillMaxWidth()
                    ) {
                        Text("إضافة سجلات فردية")
                    }
                } else {
                    Column(
                        modifier = Modifier.Companion.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "إضافة سجلات للطلاب:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Companion.Bold
                        )

                        val allStudents = repo19Etudiant.datasValue
                        LazyColumn(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allStudents) { student ->
                                StudentObservationRow(
                                    student = student,
                                    sessionDate = sessionDate,
                                    setter = setter,
                                    repo20Observation = repo20Observation
                                )
                            }
                        }

                        TextButton(
                            onClick = { showIndividualInput = false },
                            modifier = Modifier.Companion.fillMaxWidth()
                        ) {
                            Text("رجوع")
                        }
                    }
                }

                if (sessionObservations.isNotEmpty()) {
                    Spacer(modifier = Modifier.Companion.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            sessionObservations.forEach { obs ->
                                repo20Observation.delete(obs)
                            }
                            onDismiss()
                        },
                        modifier = Modifier.Companion.fillMaxWidth(),
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
