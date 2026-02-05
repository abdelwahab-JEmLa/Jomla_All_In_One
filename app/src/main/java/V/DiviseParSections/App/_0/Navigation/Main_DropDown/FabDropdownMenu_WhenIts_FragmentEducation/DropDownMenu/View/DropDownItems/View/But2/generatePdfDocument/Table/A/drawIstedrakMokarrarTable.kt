package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Draws the old istedrak assignment table (المقرر لاستدراك القديم)
 * Single row table showing what the student needs to review from old memorization
 * Returns the updated Y position
 */
fun drawIstedrakMokarrarTable(
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
    // Only draw if istedrak data exists
    if (cardData.istedrakProgress == null) {
        return yPosition
    }

    var currentY = yPosition
    
    // Compact single-row table height
    val tableHeight = 55f

    // Draw table border
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + tableHeight, paintBorder)

    // Title with colored background
    val titlePaint = TextPaint(paintArabicMediumBold).apply {
        textSize = paintArabicMediumBold.textSize + 1f  // 16f
        isFakeBoldText = true
        color = Color(0xFF6A1B9A).toArgb()  // Purple color for istedrak
    }

    drawRTLText(
        canvas = canvas,
        text = "المقرر لاستدراك القديم",
        x = marginLeft + 5f,
        y = currentY + 5f,
        width = contentWidth - 10,
        paint = titlePaint,
        alignment = Layout.Alignment.ALIGN_CENTER
    )
    currentY += 22f

    // Build the assignment text based on whether mokarrare and soura are the same
    val istedrakText = if (cardData.istedrakProgress.mokarrare == cardData.istedrakProgress.soura) {
        // Same soura - just show the soura name
        cardData.istedrakProgress.mokarrare
    } else {
        // Different souras - show range
        "${cardData.istedrakProgress.mokarrare} إلى ${cardData.istedrakProgress.soura}"
    }

    val contentPaint = TextPaint(paintArabic).apply {
        textSize = paintArabic.textSize + 1f  // 14f
        color = Color(0xFF4A148C).toArgb()  // Dark purple
        isFakeBoldText = true
    }

    drawRTLText(
        canvas = canvas,
        text = istedrakText,
        x = marginLeft + 5f,
        y = currentY,
        width = contentWidth - 10,
        paint = contentPaint,
        alignment = Layout.Alignment.ALIGN_CENTER
    )

    currentY += tableHeight - 22f + 8f  // Add small spacing after table
    return currentY
}
