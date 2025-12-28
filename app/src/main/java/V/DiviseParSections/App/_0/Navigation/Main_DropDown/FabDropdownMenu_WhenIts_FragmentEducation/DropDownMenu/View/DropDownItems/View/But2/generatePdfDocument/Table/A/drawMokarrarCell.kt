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
 * Draws the "المقرر لتحضيره" (Assigned to Prepare) cell
 * ENHANCED: Shows with divider format + moulahadat للإصلاح in color (NO TAKYIM)
 * Format:
 * - من السورة + آية
 * - [divider]
 * - إلى السورة + آية (or "إلى نهاية السورة" if end of soura)
 * - ملاحظات للإصلاح (colored bullets)
 */
fun drawMokarrarCell(
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
    currentY += 18f

    // Parse mokarrarDetails to extract soura and aya information
    val mokarrarText = cardData.hifdProgress.mokarrarDetails

    // Extract from/to information
    val fromMatch = Regex("من الآية (\\d+)").find(mokarrarText)
    val toMatch = Regex("إلى (\\d+)").find(mokarrarText)
    val toEndMatch = mokarrarText.contains("نهاية السورة") || mokarrarText.contains("نهاية")

    // Get soura name (first word before "من")
    val souraName = mokarrarText.substringBefore("من").trim()

    val fromAya = fromMatch?.groupValues?.get(1) ?: "1"

    // Create black paint for all text
    val blackPaint = TextPaint(paintArabic).apply {
        color = android.graphics.Color.BLACK
    }

    // ═══════════════════════════════════════════════════
    // Section 1: من (From)
    // ═══════════════════════════════════════════════════
    val fromText = if (souraName.isNotEmpty()) {
        "من $souraName الآية $fromAya"
    } else {
        mokarrarText
    }

    drawRTLText(
        canvas = canvas,
        text = fromText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = blackPaint,
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
    val toText = if (souraName.isNotEmpty()) {
        when {
            toEndMatch -> "إلى نهاية $souraName"
            toMatch != null -> {
                val toAya = toMatch.groupValues[1]
                "إلى $souraName الآية $toAya"
            }
            else -> "إلى $souraName"
        }
    } else {
        mokarrarText
    }

    drawRTLText(
        canvas = canvas,
        text = toText,
        x = x,
        y = currentY,
        width = cellWidth,
        paint = blackPaint,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    currentY += 16f

    // ═══════════════════════════════════════════════════
    // Section 3: ملاحظات للإصلاح (NO TAKYIM - just moulahadat)
    // ═══════════════════════════════════════════════════
    if (aCentralFacade != null) {
        val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
        val latestObservation = repo20.datasValue
            .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
            .maxByOrNull { it.creationTimestamps }

        if (latestObservation != null) {
            val moulahadatList = latestObservation.getMoulahadatList()

            if (moulahadatList.isNotEmpty()) {
                // Display moulahadat with each one on new line IN COLORED BULLETS
                val moulahadatPaint = TextPaint(paintArabic).apply {
                    textSize = paintArabic.textSize - 1f
                    color = Color(0xFFFF9800).toArgb()  // Orange color for moulahadat
                }

                moulahadatList.forEach { moulahada ->
                    val moulahadaText = "• $moulahada"
                    drawRTLText(
                        canvas = canvas,
                        text = moulahadaText,
                        x = x + 3f,
                        y = currentY,
                        width = cellWidth - 6,
                        paint = moulahadatPaint,
                        alignment = Layout.Alignment.ALIGN_NORMAL
                    )
                    currentY += 12f
                }
            }
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
