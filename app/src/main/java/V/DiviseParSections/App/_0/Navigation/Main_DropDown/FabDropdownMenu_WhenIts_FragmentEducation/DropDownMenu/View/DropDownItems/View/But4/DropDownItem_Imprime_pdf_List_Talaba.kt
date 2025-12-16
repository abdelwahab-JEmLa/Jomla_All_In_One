package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
fun DropDownItem_Imprime_pdf_List_Talaba(
    nomFun: String = "قائمة الطلبة (PDF)",  // Fixed: Changed to "قائمة الطلبة"
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Fixed: Now shows ALL students, not just today's updated ones
    val allStudentsCount = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue.size
    }

    fun createAndOpenPdfDocument() {
        isLoading = true
        generationStatus = "جاري التحضير..."
        scope.launch {
            try {
                // Step 1: Fetch and sort ALL students (not just today's)
                val allEtudiants = repo19Etudiant.datasValue.sortedWith(
                    compareBy<M19Etudiant> { it.positon_don_classe }
                        .thenBy { it.creationTimestamps }
                )

                // Fixed: Remove the filter to include ALL students
                if (allEtudiants.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "لا يوجد طلاب في القائمة", Toast.LENGTH_LONG).show()
                    }
                    isLoading = false
                    generationStatus = ""
                    return@launch
                }

                generationStatus = "جاري معالجة ${allEtudiants.size} طالب..."

                // Step 2: Structure the data for ALL students
                val cardsData = allEtudiants.map { etudiant ->
                    ParentCommunicationCardData.fromEtudiant(etudiant)
                }

                generationStatus = "جاري إنشاء الجدول..."

                // Step 3: Generate PDF document with table format
                val pdfFile = withContext(Dispatchers.IO) {
                    generatePdfDocument_list_talaba(context, cardsData, allEtudiants)
                }

                if (pdfFile == null || !pdfFile.exists()) {
                    throw Exception("فشل إنشاء ملف PDF")
                }

                generationStatus = "جاري الحفظ..."

                // Step 4: Save the file to appropriate location
                val fileName = "قائمة_الطلاب_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                val saveResult = withContext(Dispatchers.IO) {
                    PdfSaverUtility_Tahfid.savePdf(
                        context = context,
                        sourceFile = pdfFile,
                        fileName = fileName,
                        subFolder = "Tahfide_Quran"
                    )
                }

                // Step 5: Handle result and open document
                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = { savedPath ->
                            openPdfWithViewer(context, pdfFile)
                            Toast.makeText(
                                context,
                                "✅ تم إنشاء وحفظ قائمة ${allEtudiants.size} طالب\n$savedPath",
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
                Log.e("ParentCommPdf", "❌ خطأ: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "❌ خطأ: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                isLoading = false
                generationStatus = ""
            }
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.secondaryContainer
                allStudentsCount > 0 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLoading) 8.dp else 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(4.dp)
                                .width(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = if (allStudentsCount > 0) {
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
                        allStudentsCount > 0 -> "$nomFun ($allStudentsCount طالب)"
                        else -> nomFun
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
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
        Toast.makeText(
            context,
            "❌ خطأ في فتح الملف: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}
