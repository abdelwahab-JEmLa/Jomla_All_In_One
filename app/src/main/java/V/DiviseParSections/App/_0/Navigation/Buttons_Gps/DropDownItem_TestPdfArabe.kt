package V.DiviseParSections.App._0.Navigation.Buttons_Gps

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
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
import java.io.FileOutputStream

@Composable
fun DropDownItem_TestPdfArabe(
    nomFun: String = "Test PDF Arabe",
    aCentralFacade: ACentralFacade = koinInject(),
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun createAndOpenArabicPdf() {
        isLoading = true
        scope.launch {
            try {
                // Create PDF in background thread
                val pdfFile = withContext(Dispatchers.IO) {
                    generateArabicPdfNative(context)
                }

                // Open PDF on main thread
                withContext(Dispatchers.Main) {
                    if (pdfFile != null && pdfFile.exists()) {
                        // Save to Downloads first
                        savePdfToDownloads(context, pdfFile)
                        // Then open
                        openPdfWithViewer(context, pdfFile)
                        Toast.makeText(
                            context,
                            "✅ PDF créé et ouvert avec succès",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "❌ Erreur lors de la création du PDF",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("TestPdfArabe", "❌ Erreur: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "❌ Erreur: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
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
                MaterialTheme.colorScheme.tertiaryContainer
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
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) "Génération en cours..." else nomFun,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    createAndOpenArabicPdf()
                }
            },
            enabled = !isLoading
        )
    }
}

/**
 * Generate PDF with Arabic text using native Android PdfDocument
 * ✅ Supporte l'arabe nativement sans police externe
 */
private fun generateArabicPdfNative(context: Context): File? {
    return try {
        // Create output file
        val outputDir = context.getExternalFilesDir(null) ?: context.filesDir
        val pdfFile = File(outputDir, "test_arabic_${System.currentTimeMillis()}.pdf")

        // Create PDF document (A4 size: 595 x 842 points)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Setup Paint for Arabic text
        val paintTitle = Paint().apply {
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }

        val paintArabic = Paint().apply {
            textSize = 20f
            typeface = Typeface.DEFAULT // Android gère l'arabe automatiquement
            color = android.graphics.Color.rgb(0, 100, 200)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT // Alignement à droite pour l'arabe
        }

        val paintInfo = Paint().apply {
            textSize = 14f
            typeface = Typeface.DEFAULT
            color = android.graphics.Color.DKGRAY
            isAntiAlias = true
        }

        // Arabic texts (multiple examples)
        val arabicTexts = listOf(
            "مرحبا بكم في تطبيق إنشاء ملفات PDF",
            "هذا اختبار للكتابة العربية",
            "التاريخ والوقت الحالي",
            "نظام أندرويد يدعم العربية",
            "محاولة طباعة بي دي إف بالعربية"
        )

        // Draw content
        var yPosition = 100f

        // Title (French)
        canvas.drawText(
            "Test PDF en Arabe - Android Native",
            50f,
            yPosition,
            paintTitle
        )
        yPosition += 60f

        // Separator line
        canvas.drawLine(50f, yPosition, 545f, yPosition, Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 2f
        })
        yPosition += 40f

        // Arabic texts (RTL - Right to Left)
        arabicTexts.forEach { arabicText ->
            canvas.drawText(
                arabicText,
                545f, // X position à droite pour RTL
                yPosition,
                paintArabic
            )
            yPosition += 50f
        }

        yPosition += 20f

        // Separator line
        canvas.drawLine(50f, yPosition, 545f, yPosition, Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 2f
        })
        yPosition += 40f

        // Info (Date and time)
        val dateTime = java.text.SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            java.util.Locale.FRENCH
        ).format(java.util.Date())

        canvas.drawText(
            "Généré le: $dateTime",
            50f,
            yPosition,
            paintInfo
        )
        yPosition += 30f

        canvas.drawText(
            "Appareil: ${android.os.Build.MODEL}",
            50f,
            yPosition,
            paintInfo
        )
        yPosition += 30f

        canvas.drawText(
            "Android: ${android.os.Build.VERSION.RELEASE}",
            50f,
            yPosition,
            paintInfo
        )

        // Finish page
        pdfDocument.finishPage(page)

        // Write to file
        FileOutputStream(pdfFile).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }

        // Close document
        pdfDocument.close()

        Log.i("TestPdfArabe", "✅ PDF créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("TestPdfArabe", "❌ Erreur lors de la création du PDF", e)
        null
    }
}

/**
 * Open PDF file with a PDF viewer app
 */
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
                "⚠️ Aucun lecteur PDF installé\nPDF sauvegardé: ${pdfFile.name}",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Log.e("TestPdfArabe", "❌ Erreur lors de l'ouverture du PDF", e)
        Toast.makeText(
            context,
            "PDF créé à: ${pdfFile.absolutePath}",
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Save PDF to Downloads folder
 */
private fun savePdfToDownloads(context: Context, sourceFile: File): File? {
    return try {
        val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
            ?: return null

        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val destFile = File(downloadsDir, "test_arabic_${System.currentTimeMillis()}.pdf")
        sourceFile.copyTo(destFile, overwrite = true)

        Log.i("TestPdfArabe", "✅ PDF sauvegardé: ${destFile.absolutePath}")
        destFile
    } catch (e: Exception) {
        Log.e("TestPdfArabe", "❌ Erreur sauvegarde Downloads", e)
        null
    }
}
