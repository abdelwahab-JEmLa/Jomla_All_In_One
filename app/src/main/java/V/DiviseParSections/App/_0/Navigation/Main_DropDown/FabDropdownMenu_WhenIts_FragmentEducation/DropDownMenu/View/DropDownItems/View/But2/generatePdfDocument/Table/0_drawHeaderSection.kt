package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.core.graphics.toColorInt

/**
 * Draws the header section with introduction text and poetry
 */
fun drawHeaderSection(
    canvas: Canvas,
    marginLeft: Float,
    marginTop: Float,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintHeaderLarge: TextPaint,
    paintSmall: TextPaint,
    paintVerySmall: TextPaint
): Float {
    var yPosition = marginTop

    // Header text
    drawRTLText(canvas, "هذه البطاقة هي أداة تواصل",
        marginLeft, yPosition, contentWidth, paintHeaderLarge, Layout.Alignment.ALIGN_CENTER)
    yPosition += 18f

    drawRTLText(canvas, "لمتابعة سير حفظ ابنكم ليلبسكم الله حلة الكرامة بما أقرأتماه و صبرتما",
        marginLeft, yPosition, contentWidth, paintSmall, Layout.Alignment.ALIGN_CENTER)
    yPosition += 18f

    // Poetry verses
    val poetryText = """وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم
قالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"""

    drawRTLText(canvas, poetryText,
        marginLeft, yPosition, contentWidth, paintVerySmall, Layout.Alignment.ALIGN_CENTER)
    yPosition += 22f

    return yPosition
}

/**
 * Draws the student name and age header
 */
fun drawStudentHeader(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    marginLeft: Float,
    yPosition: Float,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintArabicBold: TextPaint,
    paintBorder: Paint
): Float {
    var currentY = yPosition
    val headerHeight = 32f
    
    val paintHeader = Paint().apply {
        color = "#E8F4FF".toColorInt()
        style = Paint.Style.FILL
    }
    
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight,
        currentY + headerHeight, paintHeader)
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight,
        currentY + headerHeight, paintBorder)

    drawRTLText(canvas, "${cardData.studentInfo.fullName} - ${cardData.studentInfo.age} سنة",
        marginLeft, currentY + 7f, contentWidth, paintArabicBold)
    
    currentY += headerHeight
    return currentY
}
