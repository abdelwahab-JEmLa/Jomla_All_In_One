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
import java.util.Calendar

/**
 * Draws observation history table in ultra-compact format
 * Shows last 4 observations with minimal height usage
 */
fun drawObservationHistoryTable(
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
    paintBorder: Paint,
    aCentralFacade: ACentralFacade?
): Float {
    if (aCentralFacade == null) return yPosition

    var currentY = yPosition

    // Get last 5 observations (was 3)
    val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
    val last3Observations = repo20.datasValue
        .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
        .sortedByDescending { it.creationTimestamps }
        .take(4)  // ✅ Changed from 3 to 5

    if (last3Observations.isEmpty()) {
        return currentY
    }

    // ═══════════════════════════════════════════════════
    // Table Title - NEW TEXT
    // ═══════════════════════════════════════════════════
    val titleHeight = 24f
    val titlePaint = Paint().apply {
        color = Color(0xFFE3F2FD).toArgb()
        style = Paint.Style.FILL
    }

    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + titleHeight, titlePaint)
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + titleHeight, paintBorder)

    drawRTLText(
        canvas = canvas,
        text = "آخر متابعات تقدم الحفظ",  // ✅ NEW TITLE
        x = marginLeft + 5f,
        y = currentY + 5f,
        width = contentWidth - 10,
        paint = paintArabicMediumBold,
        alignment = Layout.Alignment.ALIGN_CENTER
    )
    currentY += titleHeight

    // ═══════════════════════════════════════════════════
    // Draw each observation - ULTRA COMPACT (32px per row)
    // ═══════════════════════════════════════════════════
    last3Observations.forEach { observation ->
        val rowHeight = 30f // ✅ ULTRA COMPACT - reduced for better fit

        // Alternating background
        val rowIndex = last3Observations.indexOf(observation)
        if (rowIndex % 2 == 0) {
            val rowBgPaint = Paint().apply {
                color = Color(0xFFF5F5F5).toArgb()
                style = Paint.Style.FILL
            }
            canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + rowHeight, rowBgPaint)
        }

        // Border
        canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + rowHeight, paintBorder)

        var rowY = currentY + 3f

        // ─────────────────────────────────────────────────
        // Line 1: Arabic Date with Day Name + Range
        // ─────────────────────────────────────────────────
        val arabicDate = getArabicDate(observation.creationTimestamps)
        val minAyaDisplay = formatAyaCompact(observation.min_soura, observation.min_aya)
        val ilaAyaDisplay = formatAyaCompact(observation.ila_soura, observation.ila_aya)

        val firstLineText = "$arabicDate • $minAyaDisplay ← $ilaAyaDisplay"

        val ultraCompactPaint = TextPaint(paintSmall).apply {
            textSize = paintSmall.textSize - 2f  // Very small (9f)
        }

        drawRTLText(
            canvas = canvas,
            text = firstLineText,
            x = marginLeft + 4f,
            y = rowY,
            width = contentWidth - 8,
            paint = ultraCompactPaint,
            alignment = Layout.Alignment.ALIGN_NORMAL
        )
        rowY += 10f

        // ─────────────────────────────────────────────────
        // Line 2: Takyim + Moulahadat (combined, colored)
        // ─────────────────────────────────────────────────
        val takyimValue = observation.takyim.arabicName
        val moulahadatList = observation.getMoulahadatList()

        val combinedText = if (moulahadatList.isNotEmpty()) {
            "$takyimValue • ${moulahadatList.joinToString(" • ")}"
        } else {
            takyimValue
        }

        val tinyTakyimPaint = TextPaint(paintSmall).apply {
            textSize = paintSmall.textSize - 2.5f  // Tiny (8f)
            isFakeBoldText = false
            color = when (takyimValue) {
                "ممتاز" -> Color(0xFF4CAF50).toArgb()
                "جيد جداً", "جيد جدا" -> Color(0xFF2196F3).toArgb()
                "فوق الجيد" -> Color(0xFF03A9F4).toArgb()
                "جيد" -> Color(0xFF9C27B0).toArgb()
                "فوق المقبول" -> Color(0xFFFF9800).toArgb()
                "مقبول" -> Color(0xFFFF5722).toArgb()
                "لم يحفظ" -> Color(0xFFF44336).toArgb()
                else -> android.graphics.Color.BLACK
            }
        }

        drawRTLText(
            canvas = canvas,
            text = combinedText,
            x = marginLeft + 4f,
            y = rowY,
            width = contentWidth - 8,
            paint = tinyTakyimPaint,
            alignment = Layout.Alignment.ALIGN_NORMAL
        )

        currentY += rowHeight
    }

    currentY += 4f
    return currentY
}

/**
 * Get Arabic formatted date with day name and Arabic month
 * Format: "الأحد 15 جانفي" (without year)
 */
private fun getArabicDate(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    // Arabic day names
    val dayNames = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
    val dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]

    // Arabic month names
    val monthNames = arrayOf(
        "جانفي", "فيفري", "مارس", "أفريل", "ماي", "جوان",
        "جويلية", "أوت", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    val monthName = monthNames[calendar.get(Calendar.MONTH)]

    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return "$dayName $day $monthName"
}

/**
 * Format aya display with end-of-soura handling
 * Uses rakme_akher_aya field from SOUAR enum
 */
private fun formatAyaCompact(soura: Any, aya: Int): String {
    return try {
        val souraNameField = soura::class.java.getDeclaredField("arabicName")
        souraNameField.isAccessible = true
        val souraName = souraNameField.get(soura) as String

        val rakmeAkherAyaField = soura::class.java.getDeclaredField("rakme_akher_aya")
        rakmeAkherAyaField.isAccessible = true
        val rakmeAkherAya = rakmeAkherAyaField.get(soura) as? Int ?: 0

        if (aya >= rakmeAkherAya && rakmeAkherAya > 0) {
            "$souraName نهاية"
        } else {
            "$souraName ($aya)"
        }
    } catch (e: Exception) {
        try {
            val souraNameField = soura::class.java.getDeclaredField("arabicName")
            souraNameField.isAccessible = true
            val souraName = souraNameField.get(soura) as String
            "$souraName ($aya)"
        } catch (e2: Exception) {
            "($aya)"
        }
    }
}

/**
 * Extension function to extract moulahadat list
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
