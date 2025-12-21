package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint

/**
 * Draws the question table with space for parent answer
 * Always displayed with default question if empty
 */
fun drawQuestionTable(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    marginLeft: Float,
    yPosition: Float,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintArabicMediumBold: TextPaint,
    paintArabic: TextPaint,
    paintSmall: TextPaint,
    paintBorder: Paint
): Float {
    var currentY = yPosition
    val questionHeight = 85f

    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + questionHeight, paintBorder)

    drawRTLText(canvas, "سؤال:",
        marginLeft + 5f, currentY + 7f, contentWidth - 10, paintArabicMediumBold,
        Layout.Alignment.ALIGN_NORMAL)

    drawRTLText(canvas, cardData.questionOuiNon,
        marginLeft + 5f, currentY + 25f, contentWidth - 10, paintArabic,
        Layout.Alignment.ALIGN_NORMAL)

    // Espace pour la réponse des parents avec texte indicatif
    drawRTLText(canvas, "الجواب:",
        marginLeft + 5f, currentY + questionHeight - 38f, contentWidth - 10, paintSmall,
        Layout.Alignment.ALIGN_NORMAL)

    // Lignes pour écrire la réponse (3 lignes)
    val linePaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 0.8f
    }

    val startLineY = currentY + questionHeight - 30f
    val lineSpacing = 10f

    // Dessiner 3 lignes horizontales pour écrire
    for (i in 0..2) {
        val lineY = startLineY + (i * lineSpacing)
        canvas.drawLine(
            marginLeft + 5f,
            lineY,
            pageWidth - marginRight - 5f,
            lineY,
            linePaint
        )
    }

    currentY += questionHeight + 10f
    return currentY
}
