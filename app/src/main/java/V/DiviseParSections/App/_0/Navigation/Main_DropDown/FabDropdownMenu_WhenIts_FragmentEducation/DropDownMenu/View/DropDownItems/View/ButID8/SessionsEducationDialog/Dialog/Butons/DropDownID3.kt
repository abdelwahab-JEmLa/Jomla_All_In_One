package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.Butons

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel.Repo19Etudiant
import Application5.App.Repository.SessionDate
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.Repo20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.createAndOpenPdfDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DropDownID3(
    sessionDate: SessionDate,
    repo20Observation: Repo20ObsarvationEtudion,
    sessionObservations: List<M20ObsarvationEtudion>,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val context = LocalContext.current
    var showTeacherDialog by remember { mutableStateOf(false) }
    var isGeneratingPdf by remember { mutableStateOf(false) }

    val raeebObservations = sessionObservations.filter {
        it.type == M20ObsarvationEtudion.Type.Raeeb
    }

    // Get the month from sessionDate
    val selectedMonth = remember(sessionDate) {
        Calendar.getInstance().apply {
            timeInMillis = sessionDate.timestamp
        }
    }

    // FIXED: Get the actual current teacher from focused values
    val selectedTeacher = remember(focusedValuesGetter.active_Central_Values) {
        focusedValuesGetter.active_Central_Values.active_Ousstad_Tahfid
            ?: Ousstad_Tahfid.Abdelwahab_Osstad // Fallback to Admin if no teacher is selected
    }

    if (raeebObservations.isEmpty()) {
        return
    }

    // Teacher selection dialog for printing monthly report
    if (showTeacherDialog) {
        AlertDialog(
            onDismissRequest = { showTeacherDialog = false },
            title = { Text("اختر الأستاذ لطباعة التقرير الشهري") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option pour tous les oustads
                    item {
                        Button(
                            onClick = {
                                showTeacherDialog = false
                                isGeneratingPdf = true

                                // Launch PDF generation for ALL teachers
                                createAndOpenPdfDocument(
                                    context = context,
                                    repo19Etudiant = repo19Etudiant,
                                    repo20Observation = repo20Observation,
                                    selectedMonth = selectedMonth,
                                    selectedTeacher = selectedTeacher, // All teachers
                                    onLoadingChange = { isGeneratingPdf = it },
                                    onStatusChange = { }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("جميع الأساتذة")
                        }
                    }

                    // Individual teachers
                    items(Ousstad_Tahfid.entries) { ousstad ->
                        OutlinedButton(
                            onClick = {
                                showTeacherDialog = false
                                isGeneratingPdf = true

                                // Launch PDF generation for selected teacher
                                createAndOpenPdfDocument(
                                    context = context,
                                    repo19Etudiant = repo19Etudiant,
                                    repo20Observation = repo20Observation,
                                    selectedMonth = selectedMonth,
                                    selectedTeacher = ousstad,
                                    onLoadingChange = { isGeneratingPdf = it },
                                    onStatusChange = { }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isGeneratingPdf
                        ) {
                            Text(ousstad.nom_arab)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTeacherDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Original delete button
        OutlinedButton(
            onClick = {
                raeebObservations.forEach { obs ->
                    repo20Observation.delete(obs)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("حذف سجلات الغياب فقط (${raeebObservations.size})")
        }

        Button(
            onClick = { showTeacherDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !isGeneratingPdf
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "PDF",
                modifier = Modifier.padding(end = 8.dp)
            )
            val monthName = SimpleDateFormat("MMMM yyyy", Locale("ar")).format(selectedMonth.time)
            Text(
                if (isGeneratingPdf) {
                    "جاري إنشاء PDF..."
                } else {
                    "طباعة تقرير شهر $monthName"
                }
            )
        }
    }
}
