package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.graphics.Canvas
import android.text.Layout
import android.text.TextPaint

/**
 * Draws the "المقرر لتحضيره" (Assigned to Prepare) cell
 * Shows: Mokarrar soura and aya range details in BLACK text (not orange)
 */
fun drawMokarrarCell(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    x: Float,
    y: Float,
    cellWidth: Int,
    paintArabicMediumBold: TextPaint,
    paintArabic: TextPaint
) {
    var currentY = y

    // Title: المقرر لتحضيره
    drawRTLText(
        canvas = canvas,
        text = "المقرر لتحضيره",
        x = x,
        y = currentY,
        width = cellWidth,
        paint = paintArabicMediumBold,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    currentY += 21f

    // Content: Mokarrar details (soura and aya range) - IN BLACK
    // Create a clean paint to ensure black color (not orange)
    val blackPaint = TextPaint(paintArabic).apply {
        color = android.graphics.Color.BLACK
    }

    drawRTLText(
        canvas = canvas,
        text = cardData.hifdProgress.mokarrarDetails,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = blackPaint,  // Use black paint explicitly
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
}
