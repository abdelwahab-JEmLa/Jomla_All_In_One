package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
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
fun DropDownItem_Imprime_pdf_communication_ac_parent(
    nomFun: String = "بطاقة التواصل مع الولي (PDF)",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val todayStudentsCount = remember(repo19Etudiant.datasValue) {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        repo19Etudiant.datasValue.count { etudiant ->
            etudiant.dernierTimeTampsSynchronisationAvecFireBase >= todayStart && !etudiant.absent
        }
    }

    fun createAndOpenPdfDocument() {
        isLoading = true
        scope.launch {
            try {
                // Step 1: Fetch and sort all students
                val allEtudiants = repo19Etudiant.datasValue.sortedWith(
                    compareBy<M19Etudiant> { it.positon_don_classe }
                        .thenBy { it.creationTimestamps }
                )

                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val targetedEtudiants = allEtudiants.filter { etudiant ->
                    etudiant.dernierTimeTampsSynchronisationAvecFireBase >= todayStart && !etudiant.absent
                }

                if (targetedEtudiants.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "لا يوجد طلاب تم تحديثهم اليوم", Toast.LENGTH_LONG).show()
                    }
                    isLoading = false
                    return@launch
                }

                // Step 3: Structure the data for all targeted students
                val cardsData = targetedEtudiants.map { etudiant ->
                    ParentCommunicationCardData.fromEtudiant(etudiant)
                }

                // Step 4: Generate PDF document with structured data (one page per student)
                val pdfFile = withContext(Dispatchers.IO) {
                    generatePdfDocument(context, cardsData)
                }

                // Step 5: Save the file to appropriate location
                val saveResult = withContext(Dispatchers.IO) {
                    if (pdfFile != null && pdfFile.exists()) {
                        val fileName = "بطاقة_التواصل_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                        PdfSaverUtility.savePdf(
                            context = context,
                            sourceFile = pdfFile,
                            fileName = fileName,
                            subFolder = "Tahfide_Quran"
                        )
                    } else {
                        Result.failure(Exception("فشل إنشاء ملف PDF"))
                    }
                }

                // Step 6: Handle result and open document
                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = { savedPath ->
                            if (pdfFile != null) {
                                openPdfWithViewer(context, pdfFile)
                            }
                            Toast.makeText(context, "✅ تم إنشاء وحفظ ${targetedEtudiants.size} بطاقة: $savedPath", Toast.LENGTH_LONG).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, "❌ خطأ في الحفظ: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ParentCommPdf", "❌ خطأ: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ خطأ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) {
                        "جاري الإنشاء..."
                    } else if (todayStudentsCount > 0) {
                        "$nomFun ($todayStudentsCount)"
                    } else {
                        nomFun
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    createAndOpenPdfDocument()
                }
            },
            enabled = !isLoading
        )
    }
}

fun openPdfWithViewer(context: Context, pdfFile: File) {
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
        Log.e("ParentCommPdf", "❌ خطأ في فتح PDF", e)
    }
}
