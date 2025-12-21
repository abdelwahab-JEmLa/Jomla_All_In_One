package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint

/**
 * Draws the justification table if applicable
 * Conditional based on absence count and shouldPrintJustification flag
 */
fun drawJustificationTable(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    marginLeft: Float,
    yPosition: Float,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintArabicMediumBold: TextPaint,
    paintArabic: TextPaint,
    paintBorder: Paint
): Float {
    var currentY = yPosition
    val cellWidth = contentWidth / 2f

    if (cardData.footer.shouldPrintJustification && cardData.footer.absenceCount > 0) {
        val justificationHeight = 85f

        canvas.drawRect(marginLeft, currentY, marginLeft + cellWidth, currentY + justificationHeight, paintBorder)
        canvas.drawRect(marginLeft + cellWidth, currentY, pageWidth - marginRight, currentY + justificationHeight, paintBorder)

        val absenceText = """نعلمكم بغياب ابنكم
لـ ${cardData.footer.absenceCount} حصة

يرجى توضيح السبب"""

        drawRTLText(canvas, absenceText,
            marginLeft + cellWidth + 5f, currentY + 8f, (cellWidth - 10f).toInt(), paintArabic,
            Layout.Alignment.ALIGN_NORMAL)

        drawRTLText(canvas, "المبرر:",
            marginLeft + 5f, currentY + 5f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
            Layout.Alignment.ALIGN_NORMAL)

        drawWritableSpace(
            canvas,
            marginLeft + 5f,
            currentY + 22f,
            cellWidth - 10f,
            justificationHeight - 25f,
            lines = 3
        )

        currentY += justificationHeight + 10f
        
    } else if (cardData.footer.absenceCount > 0) {
        val infoTableHeight = 65f

        canvas.drawRect(marginLeft, currentY, marginLeft + cellWidth, currentY + infoTableHeight, paintBorder)
        canvas.drawRect(marginLeft + cellWidth, currentY, pageWidth - marginRight, currentY + infoTableHeight, paintBorder)

        val absenceText = if (cardData.footer.attendanceStatus.contains("غائب")) {
            "نعلمكم بغياب ابنكم لـ ${cardData.footer.absenceCount} حصة"
        } else {
            "مجموع الغيابات: ${cardData.footer.absenceCount} حصة"
        }

        drawRTLText(canvas, absenceText,
            marginLeft + cellWidth + 5f, currentY + 20f, (cellWidth - 10f).toInt(), paintArabic,
            Layout.Alignment.ALIGN_NORMAL)

        drawRTLText(canvas, "ملاحظة:",
            marginLeft + 5f, currentY + 5f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
            Layout.Alignment.ALIGN_NORMAL)

        drawWritableSpace(
            canvas,
            marginLeft + 5f,
            currentY + 22f,
            cellWidth - 10f,
            infoTableHeight - 25f,
            lines = 2
        )

        currentY += infoTableHeight + 10f
    }
    
    return currentY
}
