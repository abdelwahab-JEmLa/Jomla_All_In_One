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
 * Draws the footer section with date and instructions
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
    paintBorder: Paint
) {
    val bottomMargin = 25f
    val row2Height = 75f
    val yPosition = pageHeight - bottomMargin - row2Height
    val cellWidth = contentWidth / 2f

    canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + row2Height, paintBorder)
    canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + row2Height, paintBorder)

    // RIGHT cell: Instructions
    val notesText = if (cardData.notes.specialAttention.isNotBlank()) {
        """يرجى الاطلاع على المقرر
و محاولة التعاون على تحقيقه
بالهدايا و التنبيه له

${cardData.notes.specialAttention}"""
    } else {
        """يرجى الاطلاع على المقرر
و محاولة التعاون على تحقيقه
بالهدايا و التنبيه له"""
    }

    drawRTLText(canvas, notesText,
        marginLeft + cellWidth + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintSmall,
        Layout.Alignment.ALIGN_NORMAL)

    // LEFT cell: Date and signature
    val hijriDate = getHijriDate()
    val gregorianDay = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
    val gregorianMonth = SimpleDateFormat("MMMM", Locale("ar")).format(Date())
    val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date())
    val todayDate = "$hijriDate\nموافق ل $gregorianDay $gregorianMonth $gregorianYear م"

    drawRTLText(canvas, todayDate,
        marginLeft + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintVerySmall,
        Layout.Alignment.ALIGN_NORMAL)

    drawRTLText(canvas, "التوقيع:",
        marginLeft + 5f, yPosition + 50f, (cellWidth - 10f).toInt(), paintSmall,
        Layout.Alignment.ALIGN_NORMAL)
}
