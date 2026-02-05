package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Draws the footer section with date and signature
 * FIXED: Compact mode - only date and signature, no instruction table
 */
fun drawFooterSection(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    marginLeft: Float,
    pageHeight: Int,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintSmall: TextPaint,
    paintVerySmall: TextPaint,
    paintBorder: Paint,
    compactMode: Boolean = false  // NEW: Control compact mode
) {
    if (compactMode) {
        // COMPACT MODE: Only date and signature in a small single-row table
        val bottomMargin = 20f
        val compactHeight = 45f  // Much smaller than before (was 65f)
        val yPosition = pageHeight - bottomMargin - compactHeight

        // Single row for date and signature
        canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight, yPosition + compactHeight, paintBorder)

        // Date (left side - 60% width)
        val dateWidth = contentWidth * 0.6f
        val hijriDate = getHijriDate()
        val gregorianDay = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
        val gregorianMonth = SimpleDateFormat("MMMM", Locale("ar")).format(Date())
        val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date())
        val todayDate = "$hijriDate\nموافق ل $gregorianDay $gregorianMonth $gregorianYear م"

        drawRTLText(canvas, todayDate,
            marginLeft + 5f, yPosition + 5f, dateWidth.toInt() - 10, paintVerySmall,
            Layout.Alignment.ALIGN_NORMAL)

        // Vertical divider
        canvas.drawLine(
            marginLeft + dateWidth,
            yPosition,
            marginLeft + dateWidth,
            yPosition + compactHeight,
            paintBorder
        )

        // Signature (right side - 40% width)
        val signatureX = marginLeft + dateWidth
        val signatureWidth = contentWidth * 0.4f

        drawRTLText(canvas, "التوقيع:",
            signatureX + 5f, yPosition + 15f, signatureWidth.toInt() - 10, paintVerySmall,
            Layout.Alignment.ALIGN_NORMAL)

    } else {
        // ORIGINAL MODE: Full table with instructions
        val bottomMargin = 20f
        val row2Height = 65f
        val yPosition = pageHeight - bottomMargin - row2Height
        val cellWidth = contentWidth / 2f

        canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + row2Height, paintBorder)
        canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + row2Height, paintBorder)

        // RIGHT cell: Instructions with return request
        val notesText = if (cardData.notes.specialAttention.isNotBlank()) {
            """يرجى الاطلاع على المقرر
و محاولة التعاون على تحقيقه
بالهدايا و التنبيه له
يرجى إعادة الورقة معه

${cardData.notes.specialAttention}"""
        } else {
            """يرجى الاطلاع على المقرر
و محاولة التعاون على تحقيقه
بالهدايا و التنبيه له
يرجى إعادة الورقة معه"""
        }

        drawRTLText(canvas, notesText,
            marginLeft + cellWidth + 5f, yPosition + 5f, (cellWidth - 10f).toInt(), paintVerySmall,
            Layout.Alignment.ALIGN_NORMAL)

        // LEFT cell: Date and signature
        val hijriDate = getHijriDate()
        val gregorianDay = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
        val gregorianMonth = SimpleDateFormat("MMMM", Locale("ar")).format(Date())
        val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date())
        val todayDate = "$hijriDate\nموافق ل $gregorianDay $gregorianMonth $gregorianYear م"

        drawRTLText(canvas, todayDate,
            marginLeft + 5f, yPosition + 5f, (cellWidth - 10f).toInt(), paintVerySmall,
            Layout.Alignment.ALIGN_NORMAL)

        drawRTLText(canvas, "التوقيع:",
            marginLeft + 5f, yPosition + 45f, (cellWidth - 10f).toInt(), paintVerySmall,
            Layout.Alignment.ALIGN_NORMAL)
    }
}
