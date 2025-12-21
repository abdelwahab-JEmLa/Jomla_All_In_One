package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint

/**
 * Draws the Istedrak (review) table if applicable
 * Returns the updated Y position
 */
fun drawIstedrakTable(
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
    
    if (cardData.istedrakProgress != null) {
        val istedrakHeight = 80f
        val cellWidth = contentWidth / 2f

        canvas.drawRect(marginLeft, currentY, marginLeft + cellWidth, currentY + istedrakHeight, paintBorder)
        canvas.drawRect(marginLeft + cellWidth, currentY, pageWidth - marginRight, currentY + istedrakHeight, paintBorder)

        // RIGHT cell: الحفظ القديم (استدراك)
        drawRTLText(canvas, "برناج المراجعة -ما وصل اليه",
            marginLeft + cellWidth + 5f, currentY + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
            Layout.Alignment.ALIGN_NORMAL)

        val istedrakHifdText = """${cardData.istedrakProgress.soura}

التقييم: ${cardData.istedrakProgress.takyim}"""

        drawRTLText(canvas, istedrakHifdText,
            marginLeft + cellWidth + 5f, currentY + 28f, (cellWidth - 10f).toInt(), paintArabic,
            Layout.Alignment.ALIGN_NORMAL)

        // LEFT cell: المكررة (استدراك)
        drawRTLText(canvas, "برناج المراجعة - المقرر",
            marginLeft + 5f, currentY + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
            Layout.Alignment.ALIGN_NORMAL)

        drawRTLText(canvas, cardData.istedrakProgress.mokarrare,
            marginLeft + 5f, currentY + 32f, (cellWidth - 10f).toInt(), paintArabic,
            Layout.Alignment.ALIGN_NORMAL)

        currentY += istedrakHeight + 10f
    }
    
    return currentY
}
