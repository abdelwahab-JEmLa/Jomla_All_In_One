package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.ByteArrayOutputStream
import java.util.Date

/**
 * Handles PDF content creation and layout
 */
class PdfContentBuilder_Mai(
    private val formatter: PdfFormatterUtils_Mai
) {
    private fun getCircularBitmap(srcBitmap: Bitmap): Bitmap {
        val squareSize = minOf(srcBitmap.width, srcBitmap.height)
        val output = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply { isAntiAlias = true }
        val rect = RectF(0f, 0f, squareSize.toFloat(), squareSize.toFloat())
        val srcX = (srcBitmap.width - squareSize) / 2
        val srcY = (srcBitmap.height - squareSize) / 2
        val srcRect = android.graphics.Rect(srcX, srcY, srcX + squareSize, srcY + squareSize)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rect, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(srcBitmap, srcRect, rect, paint)
        return output
    }

    fun addHeader(
        doc: Document,
        title: String,
        regularFont: PdfFont,
        boldFont: PdfFont,
        bonVentKeyId: String? = null,
        context: Context? = null
    ) {
        if (context != null) {
            try {
                val logoBitmap = BitmapFactory.decodeResource(context.resources, com.example.clientjetpack.R.drawable.logo)
                if (logoBitmap != null) {
                    val circularLogo = getCircularBitmap(logoBitmap)
                    val stream = ByteArrayOutputStream()
                    circularLogo.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val imgData = ImageDataFactory.create(stream.toByteArray())
                    val logoImage = Image(imgData)
                        .setWidth(80f)
                        .setHeight(80f)
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                    doc.add(logoImage)
                    doc.add(Paragraph("\n").setFontSize(0.2f))
                    circularLogo.recycle()
                    logoBitmap.recycle()
                }
            } catch (e: Exception) {
                android.util.Log.e("PdfHeader", "Error adding circular logo: ${e.message}")
            }
        }

        bonVentKeyId?.let {
            if (it.length >= 3) {
                val prefix = it.dropLast(3)
                val last3UpperCase = it.takeLast(3).uppercase()

                val paragraph = Paragraph()
                    .add(com.itextpdf.layout.element.Text("Bon Vent : $prefix").setFont(regularFont).setFontSize(11f))
                    .add(com.itextpdf.layout.element.Text(last3UpperCase).setFont(boldFont).setFontSize(11f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMargin(0f)

                doc.add(paragraph)
            } else {
                addText(doc, "Bon Vent : ${it.uppercase()}", boldFont, 11f, TextAlignment.CENTER)
            }
            doc.add(Paragraph("\n").setFontSize(0.3f))
        }
    }

    fun addClientDate(doc: Document, clientName: String, regularFont: PdfFont) {
        val date = formatter.formatDateWithAmPm(Date())
        val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            .setWidth(UnitValue.createPercentValue(100f))

        // FIXED: Remove everything after the first dot in clientName
        val cleanedClientName = clientName.substringBefore(".")

        val clientCell = Cell()
            .add(Paragraph("Client : ${formatter.capitalizeFirstLetter(cleanedClientName)}")
                .setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        val dateCell = Cell()
            .add(Paragraph(date).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        table.addCell(clientCell).addCell(dateCell)
        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))
    }

    fun addCompactLabelValue(
        doc: Document,
        label: String,
        value: String,
        labelFont: PdfFont,
        valueFont: PdfFont
    ) {
        val paragraph = Paragraph()
            .add(com.itextpdf.layout.element.Text(label).setFont(labelFont).setFontSize(12f))
            .add(com.itextpdf.layout.element.Text(" $value").setFont(valueFont).setFontSize(12f))
            .setTextAlignment(TextAlignment.LEFT)
            .setMargin(0f)

        doc.add(paragraph)
        doc.add(Paragraph("\n").setFontSize(0.3f))
    }

    fun addCreditSection(
        doc: Document,
        client: M2Client?,
        bonVent: M8BonVent,
        versement: Double,
        regularFont: PdfFont,
        boldFont: PdfFont,
        currentReceiptTotal: Double
    ) {
        val oldBalance = client?.currentCreditBalance ?: 0.0
        val currentBill = currentReceiptTotal
        val newBalance = oldBalance + currentBill - versement

        addCompactLabelValue(doc, "Ancien Solde :", "${formatter.round(oldBalance)} Da", regularFont, boldFont)
        addCompactLabelValue(doc, "Bon actuel :", "${formatter.round(currentBill)} Da", regularFont, boldFont)
        addCompactLabelValue(doc, "Nouv. Soldé :", "${formatter.round(newBalance)} Da", regularFont, boldFont)
    }

    fun addCreditOnlySection(
        doc: Document,
        creditData: CreditReceiptData_Mai,
        regularFont: PdfFont,
        boldFont: PdfFont
    ) {
        if (creditData.oldBalance != 0.0) {
            addText(doc, "Ancien Soldé :", regularFont, 12f, TextAlignment.LEFT)
            addText(doc, "${formatter.round(creditData.oldBalance)} Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(0.3f))
        }

        if (creditData.currentBill > 0) {
            addText(doc, "Bon actuel :", regularFont, 12f, TextAlignment.LEFT)
            addText(doc, "${formatter.round(creditData.currentBill)} Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(0.3f))
        }
        addText(doc, "${formatter.round(creditData.currentPayment)} Da", boldFont, 14f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        val totalPaid = if (creditData.showPaymentHistory) {
            creditData.previousPayments.sum() + creditData.currentPayment
        } else {
            creditData.currentPayment
        }
        val newBalance = creditData.oldBalance + creditData.currentBill - totalPaid

        addText(doc, "────────────────────────", regularFont, 10f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        addText(doc, "Nouv. Sold :", boldFont, 12f, TextAlignment.LEFT)
        addText(doc, "${formatter.round(newBalance)} Da}", boldFont, 14f, TextAlignment.CENTER)

        doc.add(Paragraph("\n").setFontSize(0.3f))
        addText(doc, "Transaction: #${creditData.transactionId}", regularFont, 9f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(0.3f))
        addText(doc, "────────────────────────────", regularFont, 8f, TextAlignment.CENTER)
    }

    /**
     * FIXED: Add total with item count displayed next to it, total aligned left
     */
    fun addTotalWithItemCount(
        doc: Document,
        total: Double,
        itemCount: Int,
        boldFont: PdfFont
    ) {
        // Create a table with two columns: left for total, right for item count
        val table = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
            .setWidth(UnitValue.createPercentValue(100f))

        val totalCell = Cell()
            .add(Paragraph("Total: ${formatter.round(total)} Da")
                .setFont(boldFont)
                .setFontSize(14f)
                .setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        val itemCountCell = Cell()
            .add(Paragraph("($itemCount items)")
                .setFont(boldFont)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        table.addCell(totalCell).addCell(itemCountCell)
        doc.add(table)
    }

    fun addText(doc: Document, text: String, font: PdfFont, size: Float, align: TextAlignment) =
        doc.add(Paragraph(text).setFont(font).setFontSize(size).setTextAlignment(align).setMargin(0f))
}
