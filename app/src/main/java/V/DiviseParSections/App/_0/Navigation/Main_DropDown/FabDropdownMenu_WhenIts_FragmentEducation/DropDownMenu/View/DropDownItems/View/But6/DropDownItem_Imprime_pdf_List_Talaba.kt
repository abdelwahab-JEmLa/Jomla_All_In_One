package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4.ParentCommunicationCardData
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4.PdfSaverUtility_Tahfid
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

/**
 * OPTION 1: Enhanced Dropdown Item (Current Style with Improvements)
 * Use this if you want to keep it as a dropdown menu item
 */
@Composable
fun DropDownItem_ID6(
    nomFun: String = "قائمة الطلاب (PDF)",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Get active students count (excluding those marked for exclusion)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        onLoadingChange = { isLoading = it },
                        onStatusChange = { generationStatus = it }
                    )
                }
            },
            enabled = !isLoading && activeStudentsCount > 0
        )
    }
}

/**
 * OPTION 2: Standalone Button (Addresses TODO #1)
 * Use this as a standalone button outside of dropdown menus
 */
@Composable
fun AttendanceReportButton(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }

    val activeStudentsCount = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue.count { !it.exclue_de_l_affiche_au_classe }
    }

    ElevatedButton(
        onClick = {
            if (!isLoading && activeStudentsCount > 0) {
                createAndOpenPdfDocument(
                    context = context,
                    repo19Etudiant = repo19Etudiant,
                    onLoadingChange = { isLoading = it },
                    onStatusChange = { generationStatus = it }
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        enabled = !isLoading && activeStudentsCount > 0,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (activeStudentsCount > 0) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (isLoading && generationStatus.isNotEmpty()) {
                        generationStatus
                    } else if (isLoading) {
                        "جاري الإنشاء..."
                    } else {
                        "إنشاء تقرير الحضور"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                if (!isLoading && activeStudentsCount > 0) {
                    Text(
                        text = "($activeStudentsCount طالب نشط)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * OPTION 3: Compact Button for Toolbars
 * Smaller button suitable for app bars or toolbars
 */
@Composable
fun AttendanceReportCompactButton(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }

    val activeStudentsCount = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue.count { !it.exclue_de_l_affiche_au_classe }
    }

    IconButton(
        onClick = {
            if (!isLoading && activeStudentsCount > 0) {
                createAndOpenPdfDocument(
                    context = context,
                    repo19Etudiant = repo19Etudiant,
                    onLoadingChange = { isLoading = it },
                    onStatusChange = { generationStatus = it }
                )
            }
        },
        modifier = modifier,
        enabled = !isLoading && activeStudentsCount > 0
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            BadgedBox(
                badge = {
                    if (activeStudentsCount > 0) {
                        Badge {
                            Text(text = activeStudentsCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "تقرير الحضور PDF",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Extracted PDF generation logic - reusable across all button types
 */
private fun createAndOpenPdfDocument(
    context: Context,
    repo19Etudiant: Repo19Etudiant,
    onLoadingChange: (Boolean) -> Unit,
    onStatusChange: (String) -> Unit
) {
    onLoadingChange(true)
    onStatusChange("جاري التحضير...")

    kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
        try {
            // Step 1: Fetch and filter active students
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

            // Step 2: Structure the data
            val cardsData = activeEtudiants.map { etudiant ->
                ParentCommunicationCardData.fromEtudiant(etudiant)
            }

            onStatusChange("جاري إنشاء الجدول...")

            // Step 3: Generate PDF document
            val pdfFile = withContext(Dispatchers.IO) {
                generatePdfDocument_6(context, cardsData, activeEtudiants)
            }

            if (pdfFile == null || !pdfFile.exists()) {
                throw Exception("فشل إنشاء ملف PDF")
            }

            onStatusChange("جاري الحفظ...")

            // Step 4: Save the file
            val fileName = "قائمة_الطلاب_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val saveResult = withContext(Dispatchers.IO) {
                PdfSaverUtility_Tahfid.savePdf(
                    context = context,
                    sourceFile = pdfFile,
                    fileName = fileName,
                    subFolder = "Tahfide_Quran"
                )
            }

            // Step 5: Handle result
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
                Toast.makeText(
                    context,
                    "❌ خطأ: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
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
        Toast.makeText(
            context,
            "❌ خطأ في فتح الملف: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}
