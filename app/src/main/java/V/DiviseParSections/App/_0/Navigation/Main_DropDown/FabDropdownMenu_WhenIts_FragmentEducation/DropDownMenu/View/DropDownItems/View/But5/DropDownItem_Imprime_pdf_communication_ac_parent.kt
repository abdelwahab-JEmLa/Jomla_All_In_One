package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But5

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.PdfSaverUtility_Tahfid
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But5.generatePdfDocument.generateCheckboxGridPdf
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
import java.util.Date
import java.util.Locale

@Composable
fun DropDownItem_Imprime_pdf_Case_A_Cochet(
    nomFun: String = "بطاقة التواصل مع الولي (PDF) - شبكة",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun createAndOpenCheckboxGridPdf() {
        isLoading = true
        scope.launch {
            try {
                // Generate PDF with 20 pages of checkbox grids (15x15 = 225 checkboxes per page)
                val pdfFile = withContext(Dispatchers.IO) {
                    generateCheckboxGridPdf(context, numberOfPages = 20)
                }

                // Save the file to appropriate location
                val saveResult = withContext(Dispatchers.IO) {
                    if (pdfFile != null && pdfFile.exists()) {
                        val fileName = "شبكة_الحضور_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                        PdfSaverUtility_Tahfid.savePdf(
                            context = context,
                            sourceFile = pdfFile,
                            fileName = fileName,
                            subFolder = "Tahfide_Quran"
                        )
                    } else {
                        Result.failure(Exception("فشل إنشاء ملف PDF"))
                    }
                }

                // Handle result and open document
                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = { savedPath ->
                            if (pdfFile != null) {
                                openPdfWithViewer(context, pdfFile)
                            }
                            Toast.makeText(context, "✅ تم إنشاء وحفظ شبكة 20 صفحة: $savedPath", Toast.LENGTH_LONG).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, "❌ خطأ في الحفظ: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("CheckboxGridPdf", "❌ خطأ: ${e.message}", e)
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
                MaterialTheme.colorScheme.primaryContainer
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) {
                        "جاري الإنشاء..."
                    } else {
                        "$nomFun (20 صفحة)"
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    createAndOpenCheckboxGridPdf()
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
        Log.e("CheckboxGridPdf", "❌ خطأ في فتح PDF", e)
    }
}
