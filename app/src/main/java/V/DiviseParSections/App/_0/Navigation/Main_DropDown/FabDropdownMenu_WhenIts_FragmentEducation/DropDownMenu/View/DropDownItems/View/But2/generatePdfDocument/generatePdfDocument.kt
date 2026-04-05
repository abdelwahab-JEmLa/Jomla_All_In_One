package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawHifdTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawIstedrakMokarrarTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.A.drawObservationHistoryTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawFooterSection
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawHeaderSection
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawStudentHeader
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import android.util.Log
import java.io.File
import java.io.FileOutputStream


fun generatePdfDocument(
    context: Context,
    cardsData: List<ParentCommunicationCardData_2>,
    aCentralFacade: ACentralFacade,
    compactHeightMode: Boolean = true  // ✅ FIXED: Default to compact mode
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.pdf")

        // A5 Portrait dimensions in points
        val pageWidth = 420
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        cardsData.forEachIndexed { index, cardData ->
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Margins - optimized
            val marginLeft = 30f
            val marginRight = 30f
            val marginTop = 35f
            val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

            // TextPaint configurations - REDUCED SIZES
            val paintArabic = TextPaint().apply {
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicBold = TextPaint().apply {
                textSize = 17f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicMediumBold = TextPaint().apply {
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintHeaderLarge = TextPaint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintVerySmall = TextPaint().apply {
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            // Paint for borders
            val paintBorder = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            var yPosition = drawHeaderSection(
                canvas, marginLeft, marginTop, pageWidth, marginRight, contentWidth,
                paintHeaderLarge, paintSmall, paintVerySmall,
                compactMode = compactHeightMode
            )

            yPosition = drawStudentHeader(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicBold, paintBorder
            )

            // Draw Hifd table with repository access
            yPosition = drawHifdTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder,
                aCentralFacade = aCentralFacade
            )

            // Draw Istedrak Mokarrar table (المقرر لاستدراك القديم)
            yPosition = drawIstedrakMokarrarTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder
            )

            yPosition = drawObservationHistoryTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintSmall, paintBorder,
                aCentralFacade = aCentralFacade
            )

            drawFooterSection(
                canvas, cardData, marginLeft, pageHeight, pageWidth, marginRight, contentWidth,
                paintSmall, paintVerySmall, paintBorder,
                compactMode = compactHeightMode
            )

            pdfDocument.finishPage(page)
        }

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("ParentCommPdf", "✅ PDF créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("ParentCommPdf", "❌ Erreur lors de la création du PDF", e)
        null
    }
}
