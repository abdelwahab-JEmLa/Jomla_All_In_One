package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B5.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.UploadHandler
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Tax Invoice PDF Generator using Android Native PdfDocument API
 * ✅ Multi-page support with proper pagination
 * ✅ Client name displayed at top
 * ✅ Respects maximum page limit
 * ✅ Products that don't fit are excluded with warning
 */
class AndroidNativeTaxInvoiceGenerator(
    private val formatter: PdfFormatterUtils_2,
    private val uploadHandler: UploadHandler,
    val nombre_Page_max: Int = 10
) {

    companion object {
        // Page dimensions (A4 in points: 595 x 842)
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842

        // Margins
        private const val MARGIN_LEFT = 40f
        private const val MARGIN_RIGHT = 40f
        private const val MARGIN_TOP = 40f
        private const val MARGIN_BOTTOM = 150f // Space for total and footer

        // Row height
        private const val ROW_HEIGHT = 25f
        private const val HEADER_HEIGHT = 30f

        // Colors
        private val COLOR_ORANGE = Color.rgb(255, 192, 128)
        private val COLOR_LIGHT_ORANGE = Color.rgb(255, 228, 196)
        private const val COLOR_BLACK = Color.BLACK
        private val COLOR_GRAY = Color.rgb(128, 128, 128)

        // Company details
        private const val BUSINESS_TYPE_AR = "تجارة المرطبات بالتجزئة"
        private const val INVOICE_PREFIX = "Facture N°"
    }

    /**
     * Generate tax invoice PDF with multi-page support
     */
    suspend fun generateTaxInvoicePdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        bonVent: M8BonVent?,
        companyLogoResId: Int? = null
    ): Result<String> {
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to include in invoice"))
        }

        return try {
            val invoiceNumber = generateInvoiceNumber(bonVent)
            val fileName = "Facture_${invoiceNumber.replace("/", "_")}_${client?.nom ?: "Client"}"

            val file = uploadHandler.createLocalFile(
                context,
                fileName,
                "tax_invoice",
                bonVent?.keyID?.takeLast(4) ?: ""
            )

            // Prepare product data
            val productRows = prepareProductData(operations, tarificationRepo, produitRepo)

            // Calculate how many products fit per page
            val headerHeight = calculateHeaderHeight(companyLogoResId != null)
            val availableHeightFirstPage = PAGE_HEIGHT - headerHeight - MARGIN_BOTTOM
            val availableHeightOtherPages = PAGE_HEIGHT - MARGIN_TOP - HEADER_HEIGHT - MARGIN_BOTTOM

            val rowsPerFirstPage = (availableHeightFirstPage / ROW_HEIGHT).toInt()
            val rowsPerOtherPage = (availableHeightOtherPages / ROW_HEIGHT).toInt()

            // Calculate total pages needed
            val totalRowsNeeded = productRows.size
            var pagesNeeded = 1
            var remainingRows = totalRowsNeeded - rowsPerFirstPage

            if (remainingRows > 0) {
                pagesNeeded += (remainingRows + rowsPerOtherPage - 1) / rowsPerOtherPage
            }

            // Check if we exceed max pages
            val actualPages = minOf(pagesNeeded, nombre_Page_max)
            val productsToInclude = if (pagesNeeded > nombre_Page_max) {
                // Calculate how many products we can fit
                val maxRows = rowsPerFirstPage + (rowsPerOtherPage * (nombre_Page_max - 1))
                productRows.take(maxRows)
            } else {
                productRows
            }

            val excludedCount = totalRowsNeeded - productsToInclude.size

            // Create PDF document
            val pdfDocument = PdfDocument()

            var currentPage = 1
            var rowsInCurrentPage = 0
            var currentRowIndex = 0
            var total = 0.0

            // Generate pages
            while (currentPage <= actualPages && currentRowIndex < productsToInclude.size) {
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPage).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val rowsForThisPage = if (currentPage == 1) {
                    minOf(rowsPerFirstPage, productsToInclude.size - currentRowIndex)
                } else {
                    minOf(rowsPerOtherPage, productsToInclude.size - currentRowIndex)
                }

                val rowsToDisplay = productsToInclude.subList(
                    currentRowIndex,
                    currentRowIndex + rowsForThisPage
                )

                if (currentPage == 1) {
                    // First page: full header
                    val pageTotal = drawFirstPage(
                        canvas,
                        context,
                        client,
                        rowsToDisplay,
                        bonVent,
                        invoiceNumber,
                        companyLogoResId,
                        currentPage,
                        actualPages
                    )
                    total += pageTotal
                } else {
                    // Continuation pages: minimal header
                    val pageTotal = drawContinuationPage(
                        canvas,
                        rowsToDisplay,
                        currentRowIndex + 1,
                        currentPage,
                        actualPages
                    )
                    total += pageTotal
                }

                pdfDocument.finishPage(page)

                currentRowIndex += rowsForThisPage
                currentPage++
            }

            // Add total on last page
            if (actualPages > 0) {
                val lastPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, actualPages + 1).create()
                val lastPage = pdfDocument.startPage(lastPageInfo)
                drawTotalAndFooter(lastPage.canvas, total, excludedCount)
                pdfDocument.finishPage(lastPage)
            }

            // Write to file
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            if (!file.exists()) {
                return Result.failure(IllegalStateException("Tax invoice PDF creation failed"))
            }

            val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
            val warningMsg = if (excludedCount > 0) {
                "\n⚠️ ATTENTION: $excludedCount produits non inclus (dépassement limite de $nombre_Page_max pages)"
            } else ""
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url$warningMsg")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Prepare product data sorted and formatted
     */
    private fun prepareProductData(
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit
    ): List<ProductRow> {
        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }
        val sortedGroupedOps = groupedOps.entries.sortedBy { (produitId, _) ->
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            formatter.cleanAndCapitalizeProductName(produit?.nom ?: "")
        }

        return sortedGroupedOps.mapNotNull { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find {
                it.keyID == ops.first().parentM13TarificationKeyID
            }

            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val rawPrice = tarification?.prixCurrency ?: 0.0
            val subtotal = rawPrice * qty

            if (rawPrice > 0.0) {
                ProductRow(
                    name = formatter.cleanAndCapitalizeProductName(produit?.nom ?: "Produit"),
                    quantity = qty,
                    unitPrice = rawPrice,
                    total = subtotal
                )
            } else null
        }
    }

    /**
     * Calculate header height for first page
     */
    private fun calculateHeaderHeight(hasLogo: Boolean): Float {
        var height = MARGIN_TOP
        if (hasLogo) height += 90f // Logo + spacing
        height += 25f // Company name
        height += 20f // Business type
        height += 25f // RC number
        height += 20f // Spacing
        height += 25f // Invoice title
        height += 15f // Spacing
        height += 20f // Date
        height += 10f // Spacing
        height += 20f // Client header
        height += 36f // Client info (2 lines)
        height += 15f // Spacing
        height += HEADER_HEIGHT // Table header
        return height
    }

    /**
     * Draw first page with full header
     */
    private fun drawFirstPage(
        canvas: Canvas,
        context: Context,
        client: M2Client?,
        products: List<ProductRow>,
        bonVent: M8BonVent?,
        invoiceNumber: String,
        companyLogoResId: Int?,
        pageNum: Int,
        totalPages: Int
    ): Double {
        var yPos = MARGIN_TOP

        // 1. Company Logo
        yPos = drawCompanyLogo(canvas, context, companyLogoResId, yPos)

        yPos = drawClientNameAtTop(canvas, client, yPos)

        // 3. Business Type
        yPos = drawBusinessType(canvas, yPos)

        // 4. RC Number
        yPos = drawRCNumber(canvas, yPos, client)
        yPos += 20f

        // 5. Invoice Title
        yPos = drawInvoiceTitle(canvas, invoiceNumber, yPos)
        yPos += 15f

        // 6. Date
        yPos = drawDate(canvas, yPos)
        yPos += 10f

        // 7. "Doit : clients"
        yPos = drawClientHeader(canvas, yPos)

        // 8. Client Info (RC only)
        yPos = drawClientInfo(canvas, client, yPos)
        yPos += 15f

        // 9. Products Table
        val total = drawProductsTable(canvas, products, yPos, 1, pageNum, totalPages)

        return total
    }

    /**
     * Draw continuation page
     */
    private fun drawContinuationPage(
        canvas: Canvas,
        products: List<ProductRow>,
        startRowNum: Int,
        pageNum: Int,
        totalPages: Int
    ): Double {
        var yPos = MARGIN_TOP

        // Page indicator
        val pagePaint = TextPaint().apply {
            color = COLOR_GRAY
            textSize = 10f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Page $pageNum/$totalPages", PAGE_WIDTH - MARGIN_RIGHT, yPos, pagePaint)
        yPos += 20f

        return drawProductsTable(canvas, products, yPos, startRowNum, pageNum, totalPages)
    }

    /**
     * Draw client name at top (replaces company name)
     */
    private fun drawClientNameAtTop(canvas: Canvas, client: M2Client?, yPos: Float): Float {
        val clientName = client?.nomPrenomArabe?.ifBlank {
            client.nom.substringBefore(".")
        } ?: ""

        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(clientName, PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 25f
    }

    private fun drawCompanyLogo(
        canvas: Canvas,
        context: Context,
        logoResId: Int?,
        yPos: Float
    ): Float {
        logoResId?.let { resId ->
            try {
                val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                val logoSize = 80f
                val xPos = (PAGE_WIDTH - logoSize) / 2
                val destRect = RectF(xPos, yPos, xPos + logoSize, yPos + logoSize)
                canvas.drawBitmap(bitmap, null, destRect, null)
                return yPos + logoSize + 10f
            } catch (e: Exception) {
                // Continue without logo
            }
        }
        return yPos
    }

    private fun drawBusinessType(canvas: Canvas, yPos: Float): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 12f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(BUSINESS_TYPE_AR, PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 20f
    }

    private fun drawRCNumber(canvas: Canvas, yPos: Float, client: M2Client?): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(client?.register_Commerce_Nm ?: "", PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 25f
    }

    private fun drawInvoiceTitle(canvas: Canvas, invoiceNumber: String, yPos: Float): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("$INVOICE_PREFIX $invoiceNumber", PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 25f
    }

    private fun drawDate(canvas: Canvas, yPos: Float): Float {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 10f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(currentDate, PAGE_WIDTH - MARGIN_RIGHT, yPos, paint)
        return yPos + 20f
    }

    private fun drawClientHeader(canvas: Canvas, yPos: Float): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Doit : clients", PAGE_WIDTH - MARGIN_RIGHT, yPos, paint)
        return yPos + 20f
    }

    private fun drawClientInfo(canvas: Canvas, client: M2Client?, yPos: Float): Float {
        var currentY = yPos
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 11f
            textAlign = Paint.Align.LEFT
        }

        // Only show RC number (name is already at top)
        val rcNumber = client?.register_Commerce_Nm ?: "N/A"
        canvas.drawText("RC: $rcNumber", MARGIN_LEFT, currentY, paint)
        currentY += 18f

        return currentY
    }

    /**
     * Draw products table
     */
    private fun drawProductsTable(
        canvas: Canvas,
        products: List<ProductRow>,
        startY: Float,
        startRowNum: Int,
        pageNum: Int,
        totalPages: Int
    ): Double {
        val tableWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT
        val colWidths = floatArrayOf(
            tableWidth * 0.08f,  // N
            tableWidth * 0.42f,  // Désignation
            tableWidth * 0.15f,  // Qté
            tableWidth * 0.15f,  // P.U
            tableWidth * 0.20f   // Montant
        )

        var yPos = startY

        // Draw header
        val headerPaint = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos + HEADER_HEIGHT, headerPaint)

        val headerTextPaint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val headers = arrayOf("N", "Désignation", "Qté", "P.U", "Montant")
        var xPos = MARGIN_LEFT

        headers.forEachIndexed { index, header ->
            val align = when (index) {
                0, 2, 3 -> Paint.Align.CENTER
                4 -> Paint.Align.RIGHT
                else -> Paint.Align.LEFT
            }
            headerTextPaint.textAlign = align

            val textX = when (align) {
                Paint.Align.CENTER -> xPos + colWidths[index] / 2
                Paint.Align.RIGHT -> xPos + colWidths[index] - 5f
                else -> xPos + 5f
            }

            canvas.drawText(header, textX, yPos + 20f, headerTextPaint)
            xPos += colWidths[index]
        }

        yPos += HEADER_HEIGHT

        val borderPaint = Paint().apply {
            color = COLOR_BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRect(MARGIN_LEFT, startY, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)

        // Draw rows
        var total = 0.0
        var rowNumber = startRowNum

        products.forEach { product ->
            // Alternating colors
            if (rowNumber % 2 == 0) {
                val rowBgPaint = Paint().apply {
                    color = COLOR_LIGHT_ORANGE
                    style = Paint.Style.FILL
                }
                canvas.drawRect(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos + ROW_HEIGHT, rowBgPaint)
            }

            val rowTextPaint = TextPaint().apply {
                color = COLOR_BLACK
                textSize = 10f
            }

            xPos = MARGIN_LEFT

            // N
            rowTextPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(rowNumber.toString(), xPos + colWidths[0] / 2, yPos + 17f, rowTextPaint)
            xPos += colWidths[0]

            // Désignation
            rowTextPaint.textAlign = Paint.Align.LEFT
            canvas.drawText(product.name, xPos + 5f, yPos + 17f, rowTextPaint)
            xPos += colWidths[1]

            // Qté
            rowTextPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(product.quantity.toString(), xPos + colWidths[2] / 2, yPos + 17f, rowTextPaint)
            xPos += colWidths[2]

            // P.U
            rowTextPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(formatter.round(product.unitPrice).toString(), xPos + colWidths[3] / 2, yPos + 17f, rowTextPaint)
            xPos += colWidths[3]

            // Montant
            rowTextPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(formatter.round(product.total).toString(), xPos + colWidths[4] - 5f, yPos + 17f, rowTextPaint)

            canvas.drawLine(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)

            yPos += ROW_HEIGHT
            total += product.total
            rowNumber++
        }

        canvas.drawLine(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)
        canvas.drawRect(MARGIN_LEFT, startY, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)

        return total
    }

    /**
     * Draw total and footer
     */
    private fun drawTotalAndFooter(canvas: Canvas, total: Double, excludedCount: Int) {
        var yPos = PAGE_HEIGHT - 150f

        // Warning if products excluded
        if (excludedCount > 0) {
            val warningPaint = TextPaint().apply {
                color = Color.RED
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                "⚠️ $excludedCount produits non affichés (limite de pages atteinte)",
                PAGE_WIDTH / 2f,
                yPos - 20f,
                warningPaint
            )
        }

        // Total
        val totalPaint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        totalPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("TOTAL TTC", MARGIN_LEFT, yPos, totalPaint)

        totalPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${formatter.round(total)} Da", PAGE_WIDTH - MARGIN_RIGHT, yPos, totalPaint)

        yPos += 25f

        // Certification
        val certPaint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 9f
        }
        canvas.drawText(
            "Certifié sincères et véritable de présente facture arrêtée à la somme :",
            MARGIN_LEFT,
            yPos,
            certPaint
        )

        yPos += 20f

        // Amount in words
        val amountInWords = convertAmountToWords(total)
        val wordsPaint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(amountInWords, PAGE_WIDTH / 2f, yPos, wordsPaint)

        yPos += 40f

        // Signature
        val sigPaint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 10f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Signature et cachet", PAGE_WIDTH - MARGIN_RIGHT, yPos, sigPaint)
    }

    private fun generateInvoiceNumber(bonVent: M8BonVent?): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val sequenceNumber = bonVent?.keyID?.takeLast(2) ?:
        String.format("%02d", (System.currentTimeMillis() % 100).toInt())
        return "$sequenceNumber/$year"
    }

    private fun convertAmountToWords(amount: Double): String {
        val roundedAmount = formatter.round(amount).toInt()
        val thousands = roundedAmount / 1000
        val remainder = roundedAmount % 1000

        return when {
            roundedAmount >= 1000 -> {
                if (remainder > 0) {
                    "$thousands mille $remainder Dinars."
                } else {
                    "$thousands mille Dinars."
                }
            }
            else -> "$roundedAmount Dinars."
        }
    }

    /**
     * Data class for product row
     */
    private data class ProductRow(
        val name: String,
        val quantity: Int,
        val unitPrice: Double,
        val total: Double
    )
}
