package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.PdfSaverUtility_Tahfid
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.generatePdfDocument
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
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        repo19Etudiant.datasValue.count {
            it.dernierTimeTampsSynchronisationAvecFireBase >= todayStart && !it.absent
        }
    }

    fun createAndSave() {
        isLoading = true
        scope.launch {
            try {
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                // ── Step 1: today's students, sorted by seat position ─────────────
                val targetedEtudiants = repo19Etudiant.datasValue
                    .filter { it.dernierTimeTampsSynchronisationAvecFireBase >= todayStart && !it.absent }
                    .sortedWith(compareBy<M19Etudiant> { it.positon_don_classe }.thenBy { it.creationTimestamps })

                if (targetedEtudiants.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "لا يوجد طلاب تم تحديثهم اليوم", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                // ── Step 2: build card data ───────────────────────────────────────
                val cardsData = targetedEtudiants.map { ParentCommunicationCardData_2.fromEtudiant(it) }

                // ── Step 3: generate PDF ──────────────────────────────────────────
                val pdfFile = withContext(Dispatchers.IO) {
                    generatePdfDocument(context, cardsData, aCentralFacade)
                }

                // ── Step 4: save PDF to Downloads ─────────────────────────────────
                val saveResult = withContext(Dispatchers.IO) {
                    if (pdfFile != null && pdfFile.exists()) {
                        val fileName = "بطاقة_التواصل_${
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        }.pdf"
                        PdfSaverUtility_Tahfid.savePdf(context, pdfFile, fileName, "Tahfide_Quran")
                    } else {
                        Result.failure(Exception("فشل إنشاء ملف PDF"))
                    }
                }

                // ── Step 5: open PDF for review ───────────────────────────────────
                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = { savedPath ->
                            if (pdfFile != null) openPdfWithViewer(context, pdfFile)
                            Toast.makeText(
                                context,
                                "✅ تم إنشاء وحفظ ${targetedEtudiants.size} بطاقة: $savedPath",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, "❌ خطأ في الحفظ: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }

                if (pdfFile == null || !pdfFile.exists()) return@launch

                // ── Step 6: convert to JPGs named {keyID}_{nom}.jpg ───────────────
                // Saved to Pictures/whatsapp_cards/MM_dd/ via MediaStore so they
                // are visible in the gallery and ready for the send button.
                val jpgUris = withContext(Dispatchers.IO) {
                    convertPdfPagesToJpgs(
                        context  = context,
                        pdfFile  = pdfFile,
                        students = targetedEtudiants   // ← named by student, not page index
                    )
                }

                val saved = jpgUris.count { it != null }
                Log.i("ParentCommPdf", "🖼️ $saved/${targetedEtudiants.size} JPGs ready for send button")

                withContext(Dispatchers.Main) {
                    if (saved > 0)
                        Toast.makeText(context, "🖼️ $saved صورة جاهزة للإرسال عبر واتساب", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("ParentCommPdf", "❌ ${e.message}", e)
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
            containerColor = if (isLoading) MaterialTheme.colorScheme.secondaryContainer
            else           MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                else           Icon(Icons.Default.PictureAsPdf, null, tint = MaterialTheme.colorScheme.error)
            },
            text = {
                Text(
                    text = when {
                        isLoading            -> "جاري الإنشاء والتحويل…"
                        todayStudentsCount > 0 -> "$nomFun ($todayStudentsCount)"
                        else                 -> nomFun
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick  = { if (!isLoading) createAndSave() },
            enabled  = !isLoading
        )
    }
}

fun openPdfWithViewer(context: Context, pdfFile: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
        else Toast.makeText(context, "⚠️ لا يوجد تطبيق PDF — تم الحفظ في التنزيلات", Toast.LENGTH_LONG).show()
    } catch (e: Exception) { Log.e("ParentCommPdf", "❌ فتح PDF", e) }
}
