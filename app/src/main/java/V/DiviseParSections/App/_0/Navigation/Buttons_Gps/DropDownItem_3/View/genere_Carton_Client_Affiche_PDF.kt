package V.DiviseParSections.App._0.Navigation.Buttons_Gps.DropDownItem_3.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.toColorInt
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates an A5 landscape PDF label for client carton
 * Shows: Client Name and Number of Items
 */
fun genere_Carton_Client_Affiche_PDF(
    context: Context,
    clientName: String,
    numberOfItems: Int,
    bonVentId: String
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "label_${clientName}_${System.currentTimeMillis()}.pdf")

        // A5 Landscape dimensions (595 x 420 points)
        val pageWidth = 595
        val pageHeight = 420

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Margins
        val margin = 40f
        val contentWidth = pageWidth - (2 * margin)

        // Paint configurations
        val paintTitle = TextPaint().apply {
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = Color.BLACK
        }

        val paintSubtitle = TextPaint().apply {
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = "#424242".toColorInt()
        }

        val paintItemCount = TextPaint().apply {
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = "#2196F3".toColorInt()
        }

        val paintLabel = TextPaint().apply {
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = "#757575".toColorInt()
        }

        val paintDate = TextPaint().apply {
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = "#9E9E9E".toColorInt()
        }

        val paintBorder = Paint().apply {
            color = "#2196F3".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        // Draw border
        canvas.drawRect(
            margin / 2, 
            margin / 2, 
            pageWidth - margin / 2, 
            pageHeight - margin / 2, 
            paintBorder
        )

        var yPosition = margin + 40f

        // Draw "Client" label
        drawCenteredText(canvas, "NOM DU CLIENT", margin, yPosition, contentWidth, paintLabel)
        yPosition += 40f

        // Draw client name (main focus)
        drawCenteredText(canvas, clientName, margin, yPosition, contentWidth, paintTitle)
        yPosition += 80f

        // Separator line
        val linePaint = Paint().apply {
            color = "#E0E0E0".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawLine(
            margin + 50f, 
            yPosition, 
            pageWidth - margin - 50f, 
            yPosition, 
            linePaint
        )
        yPosition += 50f

        // Draw "Number of Items" label
        drawCenteredText(canvas, "NOMBRE D'ARTICLES", margin, yPosition, contentWidth, paintLabel)
        yPosition += 50f

        // Draw item count (large and prominent)
        drawCenteredText(canvas, "$numberOfItems", margin, yPosition, contentWidth, paintItemCount)
        yPosition += 80f

        // Draw product lines info
        drawCenteredText(
            canvas, 
            "lignes de produits", 
            margin, 
            yPosition, 
            contentWidth, 
            paintSubtitle
        )

        // Footer with date and ID
        val dateText = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date())
        val footerText = "Date: $dateText | ID: ${bonVentId.takeLast(8)}"
        
        val footerY = pageHeight - margin - 20f
        drawCenteredText(canvas, footerText, margin, footerY, contentWidth, paintDate)

        pdfDocument.finishPage(page)

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("CartonLabel", "✅ Label PDF créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("CartonLabel", "❌ Erreur lors de la création du label PDF", e)
        null
    }
}

/**
 * Helper function to draw centered text
 */
private fun drawCenteredText(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    width: Float,
    paint: TextPaint
) {
    val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width.toInt())
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setIncludePad(false)
        .build()

    canvas.save()
    canvas.translate(x, y)
    layout.draw(canvas)
    canvas.restore()
}

/**
 * Enhanced version with RTL support for Arabic names
 */
fun genere_Carton_Client_Affiche_PDF_RTL(
    context: Context,
    clientName: String,
    numberOfItems: Int,
    bonVentId: String
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "label_${clientName}_${System.currentTimeMillis()}.pdf")

        // A5 Landscape dimensions
        val pageWidth = 595
        val pageHeight = 420

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val margin = 40f
        val contentWidth = pageWidth - (2 * margin)

        // Paint configurations for RTL text
        val paintTitle = TextPaint().apply {
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = Color.BLACK
        }

        val paintLabel = TextPaint().apply {
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = "#757575".toColorInt()
        }

        val paintItemCount = TextPaint().apply {
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            color = "#2196F3".toColorInt()
        }

        val paintDate = TextPaint().apply {
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            color = "#9E9E9E".toColorInt()
        }

        // Draw border
        val paintBorder = Paint().apply {
            color = "#2196F3".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRect(margin / 2, margin / 2, pageWidth - margin / 2, pageHeight - margin / 2, paintBorder)

        var yPosition = margin + 40f

        // Draw label (bilingual)
        drawCenteredText(canvas, "اسم العميل / NOM CLIENT", margin, yPosition, contentWidth, paintLabel)
        yPosition += 40f

        // Draw client name
        drawCenteredText(canvas, clientName, margin, yPosition, contentWidth, paintTitle)
        yPosition += 80f

        // Separator
        val linePaint = Paint().apply {
            color = "#E0E0E0".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawLine(margin + 50f, yPosition, pageWidth - margin - 50f, yPosition, linePaint)
        yPosition += 50f

        // Draw items label
        drawCenteredText(canvas, "عدد الأصناف / ARTICLES", margin, yPosition, contentWidth, paintLabel)
        yPosition += 50f

        // Draw item count
        drawCenteredText(canvas, "$numberOfItems", margin, yPosition, contentWidth, paintItemCount)

        // Footer
        val dateText = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date())
        val footerY = pageHeight - margin - 20f
        drawCenteredText(canvas, "$dateText | ID: ${bonVentId.takeLast(8)}", margin, footerY, contentWidth, paintDate)

        pdfDocument.finishPage(page)

        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("CartonLabel", "✅ Label PDF RTL créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("CartonLabel", "❌ Erreur lors de la création du label PDF RTL", e)
        null
    }
}
