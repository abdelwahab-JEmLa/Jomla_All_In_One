package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.TextPaint
import androidx.core.graphics.toColorInt

/**
 * Draws the header section with school logo, introduction text and poetry.
 * TODO(1) FIXED: ecole_logo1 is drawn full-width at the very top of the page
 * (from marginLeft to pageWidth-marginRight). Text starts below the logo.
 */
fun drawHeaderSection(
    canvas: Canvas,
    context: Context,
    marginLeft: Float,
    marginTop: Float,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintHeaderLarge: TextPaint,
    paintSmall: TextPaint,
    paintVerySmall: TextPaint,
    compactMode: Boolean = true
): Float {
    var yPosition = marginTop

    // ── Logo: full content-width, aspect-ratio preserved ─────────────────────
    val logoBitmap: Bitmap? = runCatching {
        val resId = context.resources.getIdentifier(
            "ecole_logo1", "drawable", context.packageName
        )
        if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
    }.getOrNull()

    if (logoBitmap != null) {
        // 65% of content width, centered horizontally
        val our = 0.50f
        val logoWidth  = contentWidth * our
        val logoHeight = logoWidth * logoBitmap.height / logoBitmap.width
        val logoLeft   = marginLeft + (contentWidth - logoWidth) / 2f   // center
        val logoRect   = RectF(logoLeft, yPosition, logoLeft + logoWidth, yPosition + logoHeight)
        canvas.drawBitmap(logoBitmap, null, logoRect, null)
        yPosition += logoHeight + 4f
    }

    // ── Title + optional poetry (always full content width) ───────────────────
    if (compactMode) {
        drawRTLText(
            canvas, "هذه البطاقة هي أداة تواصل",
            marginLeft, yPosition, contentWidth,
            paintHeaderLarge, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 22f
    } else {
        drawRTLText(
            canvas, "هذه البطاقة هي أداة تواصل",
            marginLeft, yPosition, contentWidth,
            paintHeaderLarge, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 18f

        drawRTLText(
            canvas,
            "لمتابعة سير حفظ ابنكم ليلبسكم الله حلة الكرامة بما أقرأتماه و صبرتما",
            marginLeft, yPosition, contentWidth,
            paintSmall, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 18f

        val poetryText = """وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم
قالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"""

        drawRTLText(
            canvas, poetryText,
            marginLeft, yPosition, contentWidth,
            paintVerySmall, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 22f
    }

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
