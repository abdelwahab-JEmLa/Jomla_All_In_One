package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.PdfSaverUtility_Tahfid
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DropDownItem_Imprime_pdf_collecte_numeros_whatsapp(
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

    fun generate() {
        isLoading = true
        scope.launch {
            try {
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val students = repo19Etudiant.datasValue
                    .filter { it.dernierTimeTampsSynchronisationAvecFireBase >= todayStart && !it.absent }
                    .sortedWith(compareBy<M19Etudiant> { it.positon_don_classe }.thenBy { it.creationTimestamps })

                if (students.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "لا يوجد طلاب تم تحديثهم اليوم", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val pdfFile = withContext(Dispatchers.IO) {
                    generateWhatsAppCollectPdf(context, students)
                }

                val saveResult = withContext(Dispatchers.IO) {
                    if (pdfFile != null && pdfFile.exists()) {
                        val fileName = "فيشة_أرقام_واتساب_${
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        }.pdf"
                        PdfSaverUtility_Tahfid.savePdf(context, pdfFile, fileName, "Tahfide_Quran")
                    } else {
                        Result.failure(Exception("فشل إنشاء الملف"))
                    }
                }

                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = {
                            if (pdfFile != null) openPdfWithViewer(context, pdfFile)
                            Toast.makeText(context, "✅ تم إنشاء فيشة الأرقام (${students.size} طالب)", Toast.LENGTH_LONG).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "❌ خطأ في الحفظ: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("CollecteNumeros", "❌ ${e.message}", e)
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
            else MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.ContactPhone, null, tint = MaterialTheme.colorScheme.tertiary)
            },
            text = {
                Text(
                    text = when {
                        isLoading -> "جاري الإنشاء…"
                        todayStudentsCount > 0 -> "فيشة أرقام واتساب ($todayStudentsCount)"
                        else -> "فيشة أرقام واتساب"
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { if (!isLoading) generate() },
            enabled = !isLoading
        )
    }
}

private fun generateWhatsAppCollectPdf(
    context: Context,
    students: List<M19Etudiant>
): File? {
    return try {
        val pdfFile = File(context.cacheDir, "whatsapp_collect_${System.currentTimeMillis()}.pdf")

        // A5 portrait (same as other PDFs)
        val pageWidth = 420
        val pageHeight = 595
        val marginLeft = 30f
        val marginRight = 30f
        val marginTop = 35f
        val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paintBold = TextPaint().apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = android.graphics.Color.BLACK
        }
        val paintMedium = TextPaint().apply {
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = android.graphics.Color.BLACK
        }
        val paintSmall = TextPaint().apply {
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = android.graphics.Color.BLACK
        }
        val paintVerySmall = TextPaint().apply {
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = android.graphics.Color.DKGRAY
        }
        val paintBorder = Paint().apply {
            color = android.graphics.Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        val paintLineDashed = Paint().apply {
            color = android.graphics.Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
        }

        var y = marginTop

        // ── Logo ─────────────────────────────────────────────────────────────
        val logoBitmap: Bitmap? = runCatching {
            val resId = context.resources.getIdentifier("ecole_logo1", "drawable", context.packageName)
            if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
        }.getOrNull()

        if (logoBitmap != null) {
            val logoWidth = contentWidth * 0.45f
            val logoHeight = logoWidth * logoBitmap.height / logoBitmap.width
            val logoLeft = marginLeft + (contentWidth - logoWidth) / 2f
            canvas.drawBitmap(logoBitmap, null, RectF(logoLeft, y, logoLeft + logoWidth, y + logoHeight), null)
            y += logoHeight + 6f
        }

        // ── Section title ─────────────────────────────────────────────────────
        drawRTL(canvas, "قسم عبدالوهاب حمنيش", marginLeft, y, contentWidth, paintBold)
        y += 20f

        // ── Poetry ────────────────────────────────────────────────────────────
        val poetry = "وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم\nقالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"
        drawRTL(canvas, poetry, marginLeft, y, contentWidth, paintVerySmall)
        y += 26f

        // ── Divider ───────────────────────────────────────────────────────────
        canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, paintBorder)
        y += 8f

        // ── QR code + request text ────────────────────────────────────────────
        val qrBitmap: Bitmap? = runCatching {
            val resId = context.resources.getIdentifier("qr_whatsapp", "drawable", context.packageName)
            if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
        }.getOrNull()

        if (qrBitmap != null) {
            val qrW = contentWidth * 0.40f
            val qrH = qrW * qrBitmap.height / qrBitmap.width
            val qrLeft = marginLeft + (contentWidth - qrW) / 2f
            canvas.drawBitmap(qrBitmap, null, RectF(qrLeft, y, qrLeft + qrW, y + qrH), null)
            y += qrH + 4f
            drawRTL(canvas, "إن لم تستطع المسح، اكتب الرقم أدناه", marginLeft, y, contentWidth, paintVerySmall)
            y += 14f
        }

        drawRTL(
            canvas,
            "يرجى كتابة رقم الهاتف الذي يحتوي على حساب واتساب\nلنطلعكم على كل ما يعين تحفيظ ابنكم",
            marginLeft, y, contentWidth, paintMedium
        )
        y += 34f

        canvas.drawLine(marginLeft, y, pageWidth - marginRight, y, paintBorder)
        y += 10f

        // ── Column headers ────────────────────────────────────────────────────
        val colNum   = 30f
        val colPhone = (contentWidth - colNum).toFloat()

        val xNum   = marginLeft
        val xPhone = xNum + colNum

        val headerHeight = 20f
        canvas.drawRect(xNum,   y, xNum + colNum,   y + headerHeight, paintBorder)
        canvas.drawRect(xPhone, y, xPhone + colPhone, y + headerHeight, paintBorder)

        drawRTL(canvas, "#",                     xNum,   y + 3f, colNum.toInt(),   paintMedium, Layout.Alignment.ALIGN_CENTER)
        drawRTL(canvas, "رقم الهاتف (واتساب)", xPhone, y + 3f, colPhone.toInt(), paintMedium, Layout.Alignment.ALIGN_CENTER)

        y += headerHeight

        // ── Student rows ──────────────────────────────────────────────────────
        val rowHeight = 22f
        students.forEachIndexed { idx, student ->
            if (y + rowHeight > pageHeight - 40f) return@forEachIndexed  // safety guard

            val bgPaint = Paint().apply {
                color = if (idx % 2 == 0) 0xFFF5F5F5.toInt() else android.graphics.Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawRect(xNum, y, xPhone + colPhone, y + rowHeight, bgPaint)
            canvas.drawRect(xNum,   y, xNum + colNum,   y + rowHeight, paintBorder)
            canvas.drawRect(xPhone, y, xPhone + colPhone, y + rowHeight, paintBorder)

            drawRTL(canvas, "${idx + 1}", xNum, y + 5f, colNum.toInt(), paintSmall, Layout.Alignment.ALIGN_CENTER)

            // Dotted line inside phone cell for writing
            val lineY = y + rowHeight - 6f
            canvas.drawLine(xPhone + 5f, lineY, xPhone + colPhone - 5f, lineY, paintLineDashed)

            y += rowHeight
        }

        y += 14f

        // ── Footer note ───────────────────────────────────────────────────────
        drawRTL(
            canvas,
            "جزاكم الله خيراً على تعاونكم",
            marginLeft, y, contentWidth, paintVerySmall, Layout.Alignment.ALIGN_CENTER
        )

        pdfDocument.finishPage(page)
        FileOutputStream(pdfFile).use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        pdfFile
    } catch (e: Exception) {
        Log.e("CollecteNumeros", "❌ PDF gen error", e)
        null
    }
}

private fun drawRTL(
    canvas: android.graphics.Canvas,
    text: String,
    x: Float,
    y: Float,
    width: Int,
    paint: TextPaint,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
) {
    val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
        .setAlignment(alignment)
        .setTextDirection(TextDirectionHeuristics.RTL)
        .setIncludePad(false)
        .build()
    canvas.save()
    canvas.translate(x, y)
    layout.draw(canvas)
    canvas.restore()
}
