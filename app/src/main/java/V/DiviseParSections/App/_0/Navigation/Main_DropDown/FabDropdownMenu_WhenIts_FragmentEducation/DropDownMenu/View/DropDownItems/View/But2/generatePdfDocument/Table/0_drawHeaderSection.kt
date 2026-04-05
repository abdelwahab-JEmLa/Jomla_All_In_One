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
 * TODO(1) FIXED: Draws ecole_logo1.png drawable in the header.
 *
 * The logo is placed on the trailing side (right in LTR coordinates, which is
 * the visually leading side for RTL Arabic readers) and the title is centred
 * in the remaining space.
 */
fun drawHeaderSection(
    canvas: Canvas,
    context: Context,           // ← NEW: needed to load the drawable
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

    // ── Logo (ecole_logo1) ────────────────────────────────────────────────────
    val logoSize = 40f   // square logo, in PDF points
    val logoBitmap: Bitmap? = runCatching {
        val resId = context.resources.getIdentifier(
            "ecole_logo1", "drawable", context.packageName
        )
        if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
    }.getOrNull()

    if (logoBitmap != null) {
        // Place logo at the right edge of the content area (visually first for RTL)
        val logoLeft  = pageWidth - marginRight - logoSize
        val logoTop   = marginTop
        val logoRect  = RectF(logoLeft, logoTop, logoLeft + logoSize, logoTop + logoSize)
        canvas.drawBitmap(logoBitmap, null, logoRect, null)
    }

    // Reserve space on the right so the title doesn't overlap the logo
    val titleContentWidth = if (logoBitmap != null) (contentWidth - logoSize - 4f).toInt()
    else contentWidth

    // ── Title + optional poetry ───────────────────────────────────────────────
    if (compactMode) {
        // COMPACT MODE: only the main title
        drawRTLText(
            canvas, "هذه البطاقة هي أداة تواصل",
            marginLeft, yPosition, titleContentWidth,
            paintHeaderLarge, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 22f
    } else {
        // ORIGINAL MODE: full header with subtitle and poetry
        drawRTLText(
            canvas, "هذه البطاقة هي أداة تواصل",
            marginLeft, yPosition, titleContentWidth,
            paintHeaderLarge, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 18f

        drawRTLText(
            canvas,
            "لمتابعة سير حفظ ابنكم ليلبسكم الله حلة الكرامة بما أقرأتماه و صبرتما",
            marginLeft, yPosition, titleContentWidth,
            paintSmall, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 18f

        val poetryText = """وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم
قالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"""

        drawRTLText(
            canvas, poetryText,
            marginLeft, yPosition, titleContentWidth,
            paintVerySmall, Layout.Alignment.ALIGN_CENTER
        )
        yPosition += 22f
    }

    // Make sure we clear the logo height before the next section starts
    if (logoBitmap != null) {
        val logoBottom = marginTop + logoSize + 4f
        if (yPosition < logoBottom) yPosition = logoBottom
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
