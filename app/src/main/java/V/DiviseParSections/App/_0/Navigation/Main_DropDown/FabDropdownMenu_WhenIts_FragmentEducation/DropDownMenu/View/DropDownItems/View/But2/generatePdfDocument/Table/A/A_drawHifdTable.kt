package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.ParentCommunicationCardData_2
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint

fun drawHifdTable(
    canvas: Canvas,
    cardData: ParentCommunicationCardData_2,
    marginLeft: Float,
    yPosition: Float,
    pageWidth: Int,
    marginRight: Float,
    contentWidth: Int,
    paintArabicMediumBold: TextPaint,
    paintArabic: TextPaint,
    paintBorder: Paint,
    aCentralFacade: ACentralFacade
): Float {
    var currentY = yPosition

    // Calculate dynamic height based on moulahadat count
    val moulahadatCount = try {
        val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
        val latestObs = repo20.datasValue
            .filter { it.etudiant_keyID == cardData.studentInfo.keyID }
            .maxByOrNull { it.creationTimestamps }
        latestObs?.getMoulahadatList()?.size ?: 0
    } catch (e: Exception) {
        0
    }

    // Base height + extra space for moulahadat (12px per moulahada)
    val cellHeight = 95f + (moulahadatCount * 12f)

    // Draw single full-width cell border (NO MORE 2 CELLS!)
    canvas.drawRect(marginLeft, currentY, pageWidth - marginRight, currentY + cellHeight, paintBorder)

    // Draw المقرر لتحضيره cell (full width now)
    drawMokarrarCell(
        canvas = canvas,
        cardData = cardData,
        x = marginLeft + 5f,
        y = currentY + 7f,
        cellWidth = contentWidth - 10,
        paintArabicMediumBold = paintArabicMediumBold,
        paintArabic = paintArabic,
        aCentralFacade = aCentralFacade
    )

    currentY += cellHeight + 10f
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
