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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Draws a table showing the last 3 observations for the student
 * Displays: Date, From→To, Takyim, Moulahadat
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
    
    // Get last 3 observations
    val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
    val last3Observations = repo20.datasValue
        .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
        .sortedByDescending { it.creationTimestamps }
        .take(3)
    
    if (last3Observations.isEmpty()) {
        return currentY
    }
    
    // ═══════════════════════════════════════════════════
    // Table Title
    // ═══════════════════════════════════════════════════
    val titleHeight = 28f
    val titlePaint = Paint().apply {
        color = Color(0xFFE3F2FD).toArgb() // Light blue background
        style = Paint.Style.FILL
    }
    
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + titleHeight, titlePaint)
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + titleHeight, paintBorder)
    
    drawRTLText(
        canvas = canvas,
        text = "آخر 3 سجلات للحفظ",
        x = marginLeft + 5f,
        y = currentY + 7f,
        width = contentWidth - 10,
        paint = paintArabicMediumBold,
        alignment = Layout.Alignment.ALIGN_CENTER
    )
    currentY += titleHeight

    // ═══════════════════════════════════════════════════
    // Draw each observation
    // ═══════════════════════════════════════════════════
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    
    last3Observations.forEach { observation ->
        val rowHeight = 70f
        
        // Background color for each row (alternating)
        val rowIndex = last3Observations.indexOf(observation)
        if (rowIndex % 2 == 0) {
            val rowBgPaint = Paint().apply {
                color = Color(0xFFF5F5F5).toArgb() // Light gray
                style = Paint.Style.FILL
            }
            canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + rowHeight, rowBgPaint)
        }
        
        // Border
        canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + rowHeight, paintBorder)
        
        var rowY = currentY + 5f
        
        // ─────────────────────────────────────────────────
        // Line 1: Date
        // ─────────────────────────────────────────────────
        val dateText = "التاريخ: ${dateFormat.format(Date(observation.creationTimestamps))}"
        drawRTLText(
            canvas = canvas,
            text = dateText,
            x = marginLeft + 5f,
            y = rowY,
            width = contentWidth - 10,
            paint = paintSmall,
            alignment = Layout.Alignment.ALIGN_NORMAL
        )
        rowY += 12f
        
        // ─────────────────────────────────────────────────
        // Line 2: From → To
        // ─────────────────────────────────────────────────
        val minAyaDisplay = observation.min_soura.formatAyaDisplay(observation.min_aya)
        val ilaAyaDisplay = observation.ila_soura.formatAyaDisplay(observation.ila_aya)
        
        val rangeText = "${observation.min_soura.arabicName} ($minAyaDisplay) ← ${observation.ila_soura.arabicName} ($ilaAyaDisplay)"
        drawRTLText(
            canvas = canvas,
            text = rangeText,
            x = marginLeft + 5f,
            y = rowY,
            width = contentWidth - 10,
            paint = paintArabic,
            alignment = Layout.Alignment.ALIGN_NORMAL
        )
        rowY += 14f
        
        // ─────────────────────────────────────────────────
        // Line 3: Takyim (Colored)
        // ─────────────────────────────────────────────────
        val takyimValue = observation.takyim.arabicName
        val takyimText = "التقييم: $takyimValue"
        
        val takyimPaint = TextPaint(paintArabic).apply {
            textSize = paintArabic.textSize
            isFakeBoldText = true
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
            text = takyimText,
            x = marginLeft + 5f,
            y = rowY,
            width = contentWidth - 10,
            paint = takyimPaint,
            alignment = Layout.Alignment.ALIGN_NORMAL
        )
        rowY += 14f
        
        // ─────────────────────────────────────────────────
        // Line 4: Moulahadat (if any)
        // ─────────────────────────────────────────────────
        val moulahadatList = observation.getMoulahadatList()
        if (moulahadatList.isNotEmpty()) {
            val moulahadatText = "ملاحظات: ${moulahadatList.joinToString(" • ")}"
            
            val moulahadatPaint = TextPaint(paintSmall).apply {
                color = Color(0xFFE53935).toArgb() // Red
            }
            
            drawRTLText(
                canvas = canvas,
                text = moulahadatText,
                x = marginLeft + 5f,
                y = rowY,
                width = contentWidth - 10,
                paint = moulahadatPaint,
                alignment = Layout.Alignment.ALIGN_NORMAL
            )
        }
        
        currentY += rowHeight
    }
    
    currentY += 10f // Space after table
    return currentY
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
