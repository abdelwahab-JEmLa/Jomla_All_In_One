package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import java.util.Date
import java.util.Locale

@Composable
fun DropDownItem_ID6(
    nomFun: String = "قائمة متابعة الغيابات (PDF)",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    focusedValuesGetter: FocusedValuesGetter=koinInject() ,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Get current teacher/user from focused values
    val currentUtilisateur = remember(focusedValuesGetter.active_Central_Values) {
        focusedValuesGetter.active_Central_Values.active_filter_du_utilisateur
            ?: Utilisateur.Admin
    }

    // Get active students count
    val activeStudentsCount = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue.count { !it.exclue_de_l_affiche_au_classe }
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
                            modifier = Modifier.padding(4.dp).size(20.dp),
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
                Text(
                    text = when {
                        isLoading && generationStatus.isNotEmpty() -> generationStatus
                        isLoading -> "جاري الإنشاء..."
                        activeStudentsCount > 0 -> "$nomFun ($activeStudentsCount طالب)"
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
                        currentUtilisateur = currentUtilisateur,
                        onLoadingChange = { isLoading = it },
                        onStatusChange = { generationStatus = it }
                    )
                }
            },
            enabled = !isLoading && activeStudentsCount > 0
        )
    }
}

private fun createAndOpenPdfDocument(
    context: Context,
    repo19Etudiant: Repo19Etudiant,
    currentUtilisateur: Utilisateur,
    onLoadingChange: (Boolean) -> Unit,
    onStatusChange: (String) -> Unit
) {
    onLoadingChange(true)
    onStatusChange("جاري التحضير...")

    kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
        try {
            // Fetch and filter active students
            val activeEtudiants = repo19Etudiant.datasValue
                .filter { !it.exclue_de_l_affiche_au_classe }
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

            onStatusChange("جاري معالجة ${activeEtudiants.size} طالب...")

            // Structure the data
            val cardsData = activeEtudiants.map { etudiant ->
                ParentCommunicationCardData_But6.fromEtudiant(etudiant)
            }

            onStatusChange("جاري إنشاء الجدول...")

            // Generate PDF with teacher info
            val pdfFile = withContext(Dispatchers.IO) {
                generatePdfDocument_6(
                    context = context,
                    cardsData = cardsData,
                    etudiants = activeEtudiants,
                    currentUtilisateur = currentUtilisateur
                )
            }

            if (pdfFile == null || !pdfFile.exists()) {
                throw Exception("فشل إنشاء ملف PDF")
            }

            onStatusChange("جاري الحفظ...")

            // Save the file
            val teacherName = currentUtilisateur.nom_arab
            val fileName = "قائمة_الطلاب_${teacherName}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val saveResult = withContext(Dispatchers.IO) {
                PdfSaverUtility_But6.savePdf(
                    context = context,
                    sourceFile = pdfFile,
                    fileName = fileName,
                    subFolder = "Tahfide_Quran"
                )
            }

            // Handle result
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
