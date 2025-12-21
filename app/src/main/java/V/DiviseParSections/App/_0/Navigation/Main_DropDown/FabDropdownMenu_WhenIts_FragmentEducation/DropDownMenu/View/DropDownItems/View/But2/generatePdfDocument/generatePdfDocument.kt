package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawFooterSection
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawHeaderSection
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawHifdTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawIstedrakTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawJustificationTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawQuestionTable
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawStudentHeader
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Generate PDF document from structured card data with proper RTL support
 * OPTIMIZED VERSION - All TODOs resolved
 */
fun generatePdfDocument(context: Context, cardsData: List<ParentCommunicationCardData_2>): File? {
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

            // Draw all sections
            var yPosition = drawHeaderSection(
                canvas, marginLeft, marginTop, pageWidth, marginRight, contentWidth,
                paintHeaderLarge, paintSmall, paintVerySmall
            )

            yPosition = drawStudentHeader(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicBold, paintBorder
            )

            yPosition = drawHifdTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder
            )

            yPosition = drawIstedrakTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder
            )

            yPosition = drawQuestionTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintSmall, paintBorder
            )

            yPosition = drawJustificationTable(
                canvas, cardData, marginLeft, yPosition, pageWidth, marginRight, contentWidth,
                paintArabicMediumBold, paintArabic, paintBorder
            )

            drawFooterSection(
                canvas, cardData, marginLeft, pageHeight, pageWidth, marginRight, contentWidth,
                paintSmall, paintVerySmall, paintBorder
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

/**
 * Convenience function to generate PDF for a single student
 */
fun generateSingleStudentPdf(context: Context, etudiant: M19Etudiant): File? {
    val cardData = ParentCommunicationCardData_2.fromEtudiant(etudiant)
    return generatePdfDocument(context, listOf(cardData))
}

/**
 * Convenience function to generate PDF for multiple students
 */
fun generateMultipleStudentsPdf(context: Context, etudiants: List<M19Etudiant>): File? {
    val cardsData = etudiants.map { ParentCommunicationCardData_2.fromEtudiant(it) }
    return generatePdfDocument(context, cardsData)
}
