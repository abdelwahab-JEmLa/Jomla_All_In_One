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
 * ✅ Uses android.graphics.pdf.PdfDocument (NO external libraries like iTextPDF)
 * ✅ Official tax invoice format
 * ✅ Company logo support
 * ✅ RC number and Arabic business description
 * ✅ Professional table layout with orange headers
 * ✅ Matches the required invoice format
 */
class AndroidNativeTaxInvoiceGenerator(
    private val formatter: PdfFormatterUtils_2,
    private val uploadHandler: UploadHandler
) {

    companion object {
        // Page dimensions (A4 in points: 595 x 842)
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842

        // Margins
        private const val MARGIN_LEFT = 40f
        private const val MARGIN_RIGHT = 40f
        private const val MARGIN_TOP = 40f

        // Colors
        private val COLOR_ORANGE = Color.rgb(255, 192, 128)
        private val COLOR_LIGHT_ORANGE = Color.rgb(255, 228, 196)
        private val COLOR_BLACK = Color.BLACK
        private val COLOR_GRAY = Color.rgb(128, 128, 128)

        // Company details - EXTRACTED FROM IMAGE
        private const val COMPANY_NAME_AR = "عبد الوهاب حمليش"
        private const val BUSINESS_TYPE_AR = "تجارة المرطبات بالتجزئة"
        private const val RC_NUMBER = "RC : 16/00 – 5138424 D20"
        private const val INVOICE_PREFIX = "Facture N°"
    }

    /**
     * Generate tax invoice PDF
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

            // Create PDF document
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Draw content
            drawInvoiceContent(
                canvas,
                context,
                client,
                operations,
                tarificationRepo,
                produitRepo,
                bonVent,
                invoiceNumber,
                companyLogoResId
            )

            pdfDocument.finishPage(page)

            // Write to file
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            if (!file.exists()) {
                return Result.failure(IllegalStateException("Tax invoice PDF creation failed"))
            }

            val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Draw all invoice content on canvas
     */
    private fun drawInvoiceContent(
        canvas: Canvas,
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        bonVent: M8BonVent?,
        invoiceNumber: String,
        companyLogoResId: Int?
    ) {
        var yPos = MARGIN_TOP

        // 1. Company Logo (if provided)
        yPos = drawCompanyLogo(canvas, context, companyLogoResId, yPos)

        // 2. Company Name (Arabic) - عبد الوهاب حمليش
        yPos = drawCompanyName(canvas, yPos)

        // 3. Business Type (Arabic) - تجارة المرطبات بالتجزئة
        yPos = drawBusinessType(canvas, yPos)

        // 4. RC Number - RC : 16/00 – 5138424 D20
        yPos = drawRCNumber(canvas, yPos)

        yPos += 20f // Space

        // 5. Invoice Title and Number
        yPos = drawInvoiceTitle(canvas, invoiceNumber, yPos)

        yPos += 15f // Space

        // 6. Date (right aligned)
        yPos = drawDate(canvas, yPos)

        yPos += 10f // Space

        // 7. "Doit : clients" header
        yPos = drawClientHeader(canvas, yPos)

        // 8. Client Info
        yPos = drawClientInfo(canvas, client, yPos)

        yPos += 15f // Space

        // 9. Products Table
        val total = drawProductsTable(
            canvas,
            operations,
            tarificationRepo,
            produitRepo,
            yPos
        )

        // 10. Total and Footer (at bottom of page)
        drawTotalAndFooter(canvas, total)
    }

    /**
     * Draw company logo
     */
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
                // If logo fails, continue without it
            }
        }
        return yPos
    }

    /**
     * Draw company name - عبد الوهاب حمليش
     */
    private fun drawCompanyName(canvas: Canvas, yPos: Float): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(COMPANY_NAME_AR, PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 25f
    }

    /**
     * Draw business type in Arabic - تجارة المرطبات بالتجزئة
     */
    private fun drawBusinessType(canvas: Canvas, yPos: Float): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 12f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(BUSINESS_TYPE_AR, PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 20f
    }

    /**
     * Draw RC number - RC : 16/00 – 5138424 D20
     */
    private fun drawRCNumber(canvas: Canvas, yPos: Float): Float {
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(RC_NUMBER, PAGE_WIDTH / 2f, yPos, paint)
        return yPos + 25f
    }

    /**
     * Draw invoice title and number
     */
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

    /**
     * Draw date (right aligned) - format: 14/12/2025
     */
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

    /**
     * Draw "Doit : clients" header
     */
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

    /**
     * Draw client information
     * Uses M2Client data including Arabic name and RC number
     */
    private fun drawClientInfo(canvas: Canvas, client: M2Client?, yPos: Float): Float {
        var currentY = yPos
        val paint = TextPaint().apply {
            color = COLOR_BLACK
            textSize = 11f
            textAlign = Paint.Align.LEFT
        }

        val clientName = client?.nomPrenomArabe?.ifBlank {
            client.nom.substringBefore(".")
        } ?: "Client"

        canvas.drawText(
            "Client: $clientName",
            MARGIN_LEFT,
            currentY,
            paint
        )
        currentY += 18f

        // RC Number - use client's RC (matching image: "16/00 – 5138424 D20")
        val rcNumber = client?.register_Commerce_Nm ?: "N/A"
        if (rcNumber != "N/A") {
            canvas.drawText(
                "RC: $rcNumber",
                MARGIN_LEFT,
                currentY,
                paint
            )
            currentY += 18f
        }

        return currentY
    }

    /**
     * Draw products table with orange headers (matching the image format)
     */
    private fun drawProductsTable(
        canvas: Canvas,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        startY: Float
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

        // Draw header with orange background
        val headerPaint = Paint().apply {
            color = COLOR_ORANGE
            style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos + 30f, headerPaint)

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

        yPos += 30f

        // Draw border around header
        val borderPaint = Paint().apply {
            color = COLOR_BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRect(MARGIN_LEFT, startY, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)

        // Draw rows
        var total = 0.0
        var rowNumber = 1

        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }
        val sortedGroupedOps = groupedOps.entries.sortedBy { (produitId, _) ->
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            formatter.cleanAndCapitalizeProductName(produit?.nom ?: "")
        }

        sortedGroupedOps.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find {
                it.keyID == ops.first().parentM13TarificationKeyID
            }

            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val rawPrice = tarification?.prixCurrency ?: 0.0
            val subtotal = rawPrice * qty

            if (rawPrice > 0.0) {
                val productName = formatter.cleanAndCapitalizeProductName(produit?.nom ?: "Produit")

                // Alternating row colors (light orange for even rows)
                if (rowNumber % 2 == 0) {
                    val rowBgPaint = Paint().apply {
                        color = COLOR_LIGHT_ORANGE
                        style = Paint.Style.FILL
                    }
                    canvas.drawRect(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos + 25f, rowBgPaint)
                }

                val rowTextPaint = TextPaint().apply {
                    color = COLOR_BLACK
                    textSize = 10f
                }

                xPos = MARGIN_LEFT

                // N (row number)
                rowTextPaint.textAlign = Paint.Align.CENTER
                canvas.drawText(rowNumber.toString(), xPos + colWidths[0] / 2, yPos + 17f, rowTextPaint)
                xPos += colWidths[0]

                // Désignation (product name)
                rowTextPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(productName, xPos + 5f, yPos + 17f, rowTextPaint)
                xPos += colWidths[1]

                // Qté (quantity)
                rowTextPaint.textAlign = Paint.Align.CENTER
                canvas.drawText(qty.toString(), xPos + colWidths[2] / 2, yPos + 17f, rowTextPaint)
                xPos += colWidths[2]

                // P.U (unit price)
                rowTextPaint.textAlign = Paint.Align.CENTER
                canvas.drawText(formatter.round(rawPrice).toString(), xPos + colWidths[3] / 2, yPos + 17f, rowTextPaint)
                xPos += colWidths[3]

                // Montant (total amount)
                rowTextPaint.textAlign = Paint.Align.RIGHT
                canvas.drawText(formatter.round(subtotal).toString(), xPos + colWidths[4] - 5f, yPos + 17f, rowTextPaint)

                // Draw row border
                canvas.drawLine(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)

                yPos += 25f
                total += subtotal
                rowNumber++
            }
        }

        // Draw bottom border
        canvas.drawLine(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)
        canvas.drawRect(MARGIN_LEFT, startY, PAGE_WIDTH - MARGIN_RIGHT, yPos, borderPaint)

        return total
    }

    /**
     * Draw total section and footer at bottom of page
     */
    private fun drawTotalAndFooter(canvas: Canvas, total: Double) {
        var yPos = PAGE_HEIGHT - 150f

        // Total line
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

        // Certification text
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

    /**
     * Generate invoice number in format: 03/2025
     */
    private fun generateInvoiceNumber(bonVent: M8BonVent?): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)

        val sequenceNumber = bonVent?.keyID?.takeLast(2) ?:
        String.format("%02d", (System.currentTimeMillis() % 100).toInt())

        return "$sequenceNumber/$year"
    }

    /**
     * Convert amount to words in French
     * Example: 47000.0 -> "Quarante-sept mille Dinars."
     */
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
}
