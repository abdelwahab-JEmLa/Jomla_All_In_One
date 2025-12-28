package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Draws the "الحفظ القديم" (Old Memorization) cell
 * ENHANCED: Shows current progress with divider and improved formatting
 * Format:
 * - من السورة + آية
 * - [divider]
 * - إلى السورة + آية
 * - التقييم (colored)
 * - ملاحظات للإصلاح
 */
fun drawHifdKadimCell(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    x: Float,
    y: Float,
    cellWidth: Int,
    paintArabicMediumBold: TextPaint,
    paintArabic: TextPaint,
    aCentralFacade: ACentralFacade? = null
) {
    var currentY = y

    // Title: الحفظ القديم
    drawRTLText(
        canvas = canvas,
        text = "الحفظ القديم",
        x = x,
        y = currentY,
        width = cellWidth,
        paint = paintArabicMediumBold,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    currentY += 18f

    // Get latest observation if available
    val latestObservation = if (aCentralFacade != null) {
        val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
        repo20.datasValue
            .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
            .maxByOrNull { it.creationTimestamps }
    } else null

    // ═══════════════════════════════════════════════════
    // Section 1: من (From) - Using latest observation if available
    // ═══════════════════════════════════════════════════
    val fromSoura = latestObservation?.min_soura?.arabicName ?: cardData.hifdProgress.currentSoura
    val fromAya = latestObservation?.min_aya ?: cardData.hifdProgress.currentAya

    val fromText = "من $fromSoura الآية $fromAya"
    drawRTLText(
        canvas = canvas,
        text = fromText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = paintArabic,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    currentY += 16f

    // ═══════════════════════════════════════════════════
    // Divider between من and إلى
    // ═══════════════════════════════════════════════════
    val dividerPaint = Paint().apply {
        color = android.graphics.Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
    }
    canvas.drawLine(x, currentY, x + cellWidth, currentY, dividerPaint)
    currentY += 5f

    // ═══════════════════════════════════════════════════
    // Section 2: إلى (To)
    // ═══════════════════════════════════════════════════
    val toSoura = latestObservation?.ila_soura?.arabicName ?: cardData.hifdProgress.currentSoura
    val toAya = latestObservation?.ila_aya ?: cardData.hifdProgress.currentAya

    val toText = "إلى $toSoura الآية $toAya"
    drawRTLText(
        canvas = canvas,
        text = toText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = paintArabic,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    currentY += 16f

    // ═══════════════════════════════════════════════════
    // Section 3: Enhanced Takyim Display (Colored)
    // ═══════════════════════════════════════════════════
    val takyimValue = latestObservation?.takyim?.arabicName ?: cardData.evaluation.dabteLevel
    val takyimText = "التقييم: $takyimValue"

    val takyimPaint = TextPaint(paintArabic).apply {
        textSize = paintArabic.textSize + 0.5f
        isFakeBoldText = true
        color = when (takyimValue) {
            "ممتاز" -> Color(0xFF4CAF50).toArgb()           // Green
            "جيد جداً", "جيد جدا" -> Color(0xFF2196F3).toArgb()  // Blue
            "فوق الجيد" -> Color(0xFF03A9F4).toArgb()       // Light Blue
            "جيد" -> Color(0xFF9C27B0).toArgb()             // Purple
            "فوق المقبول" -> Color(0xFFFF9800).toArgb()    // Orange
            "مقبول" -> Color(0xFFFF5722).toArgb()          // Deep Orange
            "لم يحفظ" -> Color(0xFFF44336).toArgb()        // Red
            else -> android.graphics.Color.BLACK
        }
    }

    drawRTLText(
        canvas = canvas,
        text = takyimText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = takyimPaint,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    currentY += 16f

    // ═══════════════════════════════════════════════════
    // Section 4: Moulahadat from latest observation (IN BLACK, NOT ORANGE)
    // ═══════════════════════════════════════════════════
    if (latestObservation != null) {
        val moulahadatList = latestObservation.getMoulahadatList()

        if (moulahadatList.isNotEmpty()) {
            val moulahadatTitle = "ملاحظات للإصلاح: ${moulahadatList.size}"

            // Use BLACK text for moulahadat title (not red/orange)
            val moulahadatTitlePaint = TextPaint(paintArabic).apply {
                textSize = paintArabic.textSize - 0.5f
                color = android.graphics.Color.BLACK
            }

            drawRTLText(
                canvas = canvas,
                text = moulahadatTitle,
                x = x,
                y = currentY,
                width = cellWidth,
                paint = moulahadatTitlePaint,
                alignment = Layout.Alignment.ALIGN_NORMAL
            )
            currentY += 14f

            // Display moulahadat as compact list IN BLACK
            val moulahadatPaint = TextPaint(paintArabic).apply {
                textSize = paintArabic.textSize - 1.5f
                color = android.graphics.Color.BLACK  // Black, not orange
            }

            val moulahadaText = moulahadatList.joinToString(" • ")
            drawRTLText(
                canvas = canvas,
                text = moulahadaText,
                x = x + 3f,
                y = currentY,
                width = cellWidth - 6,
                paint = moulahadatPaint,
                alignment = Layout.Alignment.ALIGN_NORMAL
            )
        }
    }
}

/**
 * Extension function to extract moulahadat list from observation
 */
private fun Any.getMoulahadatList(): List<String> {
    return try {
        val field = this::class.java.getDeclaredField("moulahadat_takyim_li_islahiha")
        field.isAccessible = true
        val moulahadatString = field.get(this) as? String

        if (moulahadatString.isNullOrBlank()) {
            emptyList()
        } else {
            moulahadatString.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    } catch (e: Exception) {
        emptyList()
    }
}
