package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Ousstad_Tahfid
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.MonthSelectionDialog
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf.PdfSaverUtility_But6
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf_Generateur.ParentCommunicationCardData_But6
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf_Generateur.generatePdfDocument_6
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun DropDownItem_ID6(
    nomFun: String = "قائمة متابعة الغيابات (PDF)",
    selectedMonth: Calendar? = null,
    selectedTeacher: Ousstad_Tahfid? = null,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }
    var showMonthDialog by remember { mutableStateOf(false) }
    var showTeacherDialog by remember { mutableStateOf(false) }
    var chosenMonth by remember { mutableStateOf(selectedMonth) }
    var chosenTeacher by remember { mutableStateOf(selectedTeacher) }
    val scope = rememberCoroutineScope()

    // FIXED: Get the actual current teacher from focused values
    val currentUtilisateur = remember(focusedValuesGetter.active_Central_Values) {
        focusedValuesGetter.active_Central_Values.active_filter_du_utilisateur
            ?: Utilisateur.Admin // Fallback to Admin if no teacher is selected
    }

    val activeStudentsCount = remember(repo19Etudiant.datasValue, chosenTeacher) {
        val students = repo19Etudiant.datasValue.filter { !it.exclue_de_l_affiche_au_classe }

        if (chosenTeacher != null && chosenTeacher != Ousstad_Tahfid.Non_Defini_Actuellemen) {
            // FIXED: Use the actual key property from the enum
            students.count { student ->
                student.parent_ousstad_key == chosenTeacher!!.key
            }
        } else {
            students.size
        }
    }

    // Month selection dialog
    if (showMonthDialog) {
        MonthSelectionDialog(
            onDismiss = { showMonthDialog = false },
            onMonthSelected = { month ->
                chosenMonth = month
                showMonthDialog = false
            }
        )
    }

    // Teacher selection dialog
    if (showTeacherDialog) {
        TeacherSelectionDialog(
            onDismiss = { showTeacherDialog = false },
            onTeacherSelected = { teacher ->
                chosenTeacher = teacher
                showTeacherDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.secondaryContainer
                activeStudentsCount > 0 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLoading) 8.dp else 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "جدول الحضور",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "ملف PDF",
                        tint = if (activeStudentsCount > 0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            },
            text = {
                val monthText = if (chosenMonth != null) {
                    SimpleDateFormat("MMMM yyyy", Locale("ar")).format(chosenMonth!!.time)
                } else {
                    "الشهر الحالي"
                }

                val teacherText = when (chosenTeacher) {
                    null -> "جميع الأساتذة"
                    Ousstad_Tahfid.Non_Defini_Actuellemen -> "جميع الأساتذة"
                    else -> chosenTeacher!!.nom_arab
                }

                Text(           //<--
                //TODO(1): creee log Spesifie pk mem si pour moi nov 25 quand je schoi moi au dialof select moi 
                    // il ya raeeb obsrv mais au pdf ce nai pas affiche au tableau
                    text = when {
                        isLoading && generationStatus.isNotEmpty() -> generationStatus
                        isLoading -> "جاري الإنشاء..."
                        activeStudentsCount > 0 -> "$nomFun\n$monthText - $teacherText\n($activeStudentsCount طالب)"
                        else -> nomFun
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                if (!isLoading && activeStudentsCount > 0) {
                    createAndOpenPdfDocument(
                        context = context,
                        repo19Etudiant = repo19Etudiant,
                        repo20Observation = repo20Observation,
                        selectedMonth = chosenMonth,
                        selectedTeacher = chosenTeacher,
                        onLoadingChange = { isLoading = it },
                        onStatusChange = { generationStatus = it }
                    )
                }
            },
            enabled = !isLoading && activeStudentsCount > 0,
            trailingIcon = {
                Row {
                    // Month selector button
                    OutlinedButton(
                        onClick = { showMonthDialog = true },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "اختر الشهر",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Teacher selector button
                    OutlinedButton(
                        onClick = { showTeacherDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "اختر الأستاذ",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun TeacherSelectionDialog(
    onDismiss: () -> Unit,
    onTeacherSelected: (Ousstad_Tahfid?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر الأستاذ") },
        text = {
            androidx.compose.foundation.lazy.LazyColumn {
                // All teachers option
                item {
                    OutlinedButton(
                        onClick = { onTeacherSelected(null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("جميع الأساتذة")
                    }
                }

                // Individual teachers
                items(Ousstad_Tahfid.values().size) { index ->
                    val teacher = Ousstad_Tahfid.values()[index]
                    if (teacher != Ousstad_Tahfid.Non_Defini_Actuellemen) {
                        OutlinedButton(
                            onClick = { onTeacherSelected(teacher) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(teacher.nom_arab)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

fun createAndOpenPdfDocument(
    context: Context,
    repo19Etudiant: Repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion,
    selectedMonth: Calendar?,
    selectedTeacher: Ousstad_Tahfid?,
    onLoadingChange: (Boolean) -> Unit,
    onStatusChange: (String) -> Unit
) {
    onLoadingChange(true)
    onStatusChange("جاري التحضير...")

    kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
        try {
            // FIXED: Filter students by teacher using the correct key property
            val activeEtudiants = repo19Etudiant.datasValue
                .filter { !it.exclue_de_l_affiche_au_classe }
                .filter { student ->
                    if (selectedTeacher != null && selectedTeacher != Ousstad_Tahfid.Non_Defini_Actuellemen) {
                        student.parent_ousstad_key == selectedTeacher.key
                    } else {
                        true
                    }
                }
                .sortedWith(
                    compareBy<M19Etudiant> { it.positon_don_classe }
                        .thenBy { it.creationTimestamps }
                )

            if (activeEtudiants.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "لا يوجد طلاب نشطون في القائمة", Toast.LENGTH_LONG).show()
                }
                onLoadingChange(false)
                onStatusChange("")
                return@launch
            }

            // Filter observations by month if specified
            val observations = if (selectedMonth != null) {
                val monthStart = Calendar.getInstance().apply {
                    timeInMillis = selectedMonth.timeInMillis
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                val monthEnd = Calendar.getInstance().apply {
                    timeInMillis = selectedMonth.timeInMillis
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }

                repo20Observation.datasValue.filter { obs ->
                    obs.creationTimestamps >= monthStart.timeInMillis &&
                            obs.creationTimestamps <= monthEnd.timeInMillis
                }
            } else {
                repo20Observation.datasValue
            }

            onStatusChange("جاري معالجة ${activeEtudiants.size} طالب...")

            val cardsData = activeEtudiants.map { etudiant ->
                ParentCommunicationCardData_But6.fromEtudiant(
                    etudiant = etudiant,
                    observations = observations
                )
            }

            onStatusChange("جاري إنشاء الجدول...")

            val pdfFile = withContext(Dispatchers.IO) {
                generatePdfDocument_6(
                    context = context,
                    cardsData = cardsData,
                    etudiants = activeEtudiants,
                    observations = observations,
                    selectedTeacher = selectedTeacher,
                    selectedMonth = selectedMonth
                )
            }

            if (pdfFile == null || !pdfFile.exists()) {
                throw Exception("فشل إنشاء ملف PDF")
            }

            onStatusChange("جاري الحفظ...")

            val teacherName = selectedTeacher?.nom_arab
            val monthSuffix = if (selectedMonth != null) {
                "_${SimpleDateFormat("yyyy_MM", Locale.getDefault()).format(selectedMonth.time)}"
            } else {
                ""
            }
            val fileName = "قائمة_الطلاب_${teacherName}${monthSuffix}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

            val saveResult = withContext(Dispatchers.IO) {
                PdfSaverUtility_But6.savePdf(
                    context = context,
                    sourceFile = pdfFile,
                    fileName = fileName,
                    subFolder = "Tahfide_Quran"
                )
            }

            withContext(Dispatchers.Main) {
                saveResult.fold(
                    onSuccess = { savedPath ->
                        openPdfWithViewer(context, pdfFile)
                        Toast.makeText(
                            context,
                            "✅ تم إنشاء وحفظ قائمة ${activeEtudiants.size} طالب\n$savedPath",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            context,
                            "❌ خطأ في الحفظ: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("AttendanceReport", "❌ خطأ: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ خطأ: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } finally {
            onLoadingChange(false)
            onStatusChange("")
        }
    }
}

private fun openPdfWithViewer(context: Context, pdfFile: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "⚠️ لا يوجد تطبيق PDF مثبت\nتم حفظ الملف في التنزيلات",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Log.e("AttendanceReport", "❌ خطأ في فتح PDF", e)
        Toast.makeText(context, "❌ خطأ في فتح الملف: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
