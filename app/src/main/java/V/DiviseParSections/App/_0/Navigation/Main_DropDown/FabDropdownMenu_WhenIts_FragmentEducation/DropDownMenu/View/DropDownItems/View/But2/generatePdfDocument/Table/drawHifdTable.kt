package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint

/**
 * Draws the main Hifd progress table (الحفظ القديم | المقرر لتحضيره)
 */
fun drawHifdTable(
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
    val cellHeight = 95f
    val cellWidth = contentWidth / 2f

    // Draw borders
    canvas.drawRect(marginLeft, currentY, marginLeft + cellWidth, currentY + cellHeight, paintBorder)
    canvas.drawRect(marginLeft + cellWidth, currentY, pageWidth - marginRight, currentY + cellHeight, paintBorder)

    // RIGHT cell: الحفظ القديم
    drawRTLText(canvas, "الحفظ القديم",
        marginLeft + cellWidth + 5f, currentY + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
        Layout.Alignment.ALIGN_NORMAL)

    val hifdText = """${cardData.hifdProgress.currentSoura}
قبل الآية ${cardData.hifdProgress.currentAya}

التقييم: ${cardData.evaluation.dabteLevel}"""

    drawRTLText(canvas, hifdText,
        marginLeft + cellWidth + 5f, currentY + 28f, (cellWidth - 10f).toInt(), paintArabic,
        Layout.Alignment.ALIGN_NORMAL)

    // LEFT cell: المقرر لتحضيره
    drawRTLText(canvas, "المقرر لتحضيره",
        marginLeft + 5f, currentY + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
        Layout.Alignment.ALIGN_NORMAL)

    drawRTLText(canvas, cardData.hifdProgress.mokarrarDetails,
        marginLeft + 5f, currentY + 28f, (cellWidth - 10f).toInt(), paintArabic,
        Layout.Alignment.ALIGN_NORMAL)

    currentY += cellHeight + 10f
    return currentY
}
