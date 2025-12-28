package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint

/**
 * Draws the main Hifd progress table (الحفظ القديم | المقرر لتحضيره)
 * REFACTORED: Each section extracted to separate functions
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
    paintBorder: Paint ,
    aCentralFacade: ACentralFacade

): Float {
    var currentY = yPosition
    val cellHeight = 95f
    val cellWidth = contentWidth / 2f

    // Draw borders for both cells
    canvas.drawRect(marginLeft, currentY, marginLeft + cellWidth, currentY + cellHeight, paintBorder)
    canvas.drawRect(marginLeft + cellWidth, currentY, pageWidth - marginRight, currentY + cellHeight, paintBorder)

    // Draw RIGHT cell: الحفظ القديم (Old Memorization)
    drawHifdKadimCell(
        canvas = canvas,
        cardData = cardData,
        x = marginLeft + cellWidth + 5f,
        y = currentY + 7f,
        cellWidth = (cellWidth - 10f).toInt(),
        paintArabicMediumBold = paintArabicMediumBold,
        paintArabic = paintArabic ,aCentralFacade
    )

    // Draw LEFT cell: المقرر لتحضيره (Assigned to Prepare)
    drawMokarrarCell(
        canvas = canvas,
        cardData = cardData,
        x = marginLeft + 5f,
        y = currentY + 7f,
        cellWidth = (cellWidth - 10f).toInt(),
        paintArabicMediumBold = paintArabicMediumBold,
        paintArabic = paintArabic
    )

    currentY += cellHeight + 10f
    return currentY
}
