package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But5.generatePdfDocument

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Generate PDF with 20 pages of checkbox grids (10x12 = 120 checkboxes per page)
 * Each checkbox represents a student attendance/tracking slot
 */
fun generateCheckboxGridPdf(context: Context, numberOfPages: Int = 20): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "checkbox_grid_${System.currentTimeMillis()}.pdf")

        // A5 Portrait dimensions in points
        val pageWidth = 420
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        // Generate 20 pages
        for (pageIndex in 0 until numberOfPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Page margins - reduced for better space usage
            val marginLeft = 20f
            val marginRight = 20f
            val marginTop = 40f
            val marginBottom = 40f

            // Calculate available space
            val availableWidth = pageWidth - marginLeft - marginRight
            val availableHeight = pageHeight - marginTop - marginBottom

            // Grid configuration: 15 columns x 15 rows = 225 checkboxes
            val columns = 15
            val rows = 20

            // Calculate checkbox size to fill the page better
            val horizontalSpacing = 4f
            val verticalSpacing = 6f

            val checkboxSize = minOf(
                (availableWidth - (horizontalSpacing * (columns - 1))) / columns,
                (availableHeight - (verticalSpacing * (rows - 1))) / rows
            )

            // Center the grid
            val gridWidth = (checkboxSize * columns) + (horizontalSpacing * (columns - 1))
            val gridHeight = (checkboxSize * rows) + (verticalSpacing * (rows - 1))
            val startX = marginLeft + (availableWidth - gridWidth) / 2
            val startY = marginTop + (availableHeight - gridHeight) / 2

            // Paint for checkbox borders
            val paintCheckbox = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }

            // Paint for page number
            val paintPageNumber = TextPaint().apply {
                textSize = 12f
                color = android.graphics.Color.GRAY
                isAntiAlias = true
            }

            // Draw the grid of checkboxes
            for (row in 0 until rows) {
                for (col in 0 until columns) {
                    val x = startX + col * (checkboxSize + horizontalSpacing)
                    val y = startY + row * (checkboxSize + verticalSpacing)

                    // Draw circle checkbox
                    val centerX = x + checkboxSize / 2
                    val centerY = y + checkboxSize / 2
                    val radius = checkboxSize / 2
                    canvas.drawCircle(centerX, centerY, radius, paintCheckbox)
                }
            }

            // Draw page number at bottom
            val pageText = "Page ${pageIndex + 1} / $numberOfPages"
            val textWidth = paintPageNumber.measureText(pageText)
            canvas.drawText(
                pageText,
                (pageWidth - textWidth) / 2,
                pageHeight - 20f,
                paintPageNumber
            )

            pdfDocument.finishPage(page)
        }

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("CheckboxGridPdf", "✅ PDF created with $numberOfPages pages: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("CheckboxGridPdf", "❌ Error creating PDF", e)
        null
    }
}
