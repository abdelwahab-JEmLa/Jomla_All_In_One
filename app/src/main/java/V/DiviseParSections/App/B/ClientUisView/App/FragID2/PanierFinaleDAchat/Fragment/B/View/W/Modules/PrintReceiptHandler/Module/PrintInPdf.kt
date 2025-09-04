package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrintInPdf_itextpdf_Handler(
    val repositorysMainGetter: RepositorysMainGetter,
    val uploadHandler: UploadHandler,
) {

    enum class PdfType { RECEIPT_ONLY, RECEIPT_WITH_CREDIT, CREDIT_ONLY }

    data class PdfGenerationParams(
        val type: PdfType,
        val client: M2Client?,
        val operations: List<M10OperationVentCouleur> = emptyList(),
        val tarificationRepo: Repo13TarificationInfos? = null,
        val produitRepo: RepoM1Produit? = null,
        val bonVent: M8BonVent? = null,
        val versement: Double = 0.0,
        val transactionId: String = "",
        val its_GrossistApp: Boolean = true,
        val creditData: CreditReceiptData? = null,
        val autoShare: Boolean = false // New parameter to control auto-sharing
    )

    data class CreditReceiptData(
        val client: M2Client?,
        val totalAmount: Double,
        val currentPayment: Double,
        val previousPayments: List<Double> = emptyList(),
        val transactionId: String,
        val showPaymentHistory: Boolean = false,
        val oldBalance: Double = 0.0,
        val currentBill: Double = 0.0
    )

    data class PdfResult(
        val localPath: String,
        val firebaseUrl: String,
        val file: java.io.File
    )

    private fun addCreditOnlySection(doc: Document, creditData: CreditReceiptData, regularFont: PdfFont, boldFont: PdfFont) {
        if (creditData.oldBalance != 0.0) {
            addText(doc, "Ancien Soldé :", regularFont, 12f, TextAlignment.LEFT)
            addText(doc, "${round(creditData.oldBalance)} Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(0.3f))
        }

        if (creditData.currentBill > 0) {
            addText(doc, "Bon actuel :", regularFont, 12f, TextAlignment.LEFT)
            addText(doc, "${round(creditData.currentBill)} Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(0.3f))
        }

        addText(doc, "Versement :", boldFont, 12f, TextAlignment.LEFT)
        addText(doc, "${round(creditData.currentPayment)} Da", boldFont, 14f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        val totalPaid = if (creditData.showPaymentHistory) creditData.previousPayments.sum() + creditData.currentPayment else creditData.currentPayment
        val newBalance = creditData.oldBalance + creditData.currentBill - totalPaid

        addText(doc, "────────────────────", regularFont, 10f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        addText(doc, "Nouv. Sold :", boldFont, 12f, TextAlignment.LEFT)
        addText(doc, "${round(newBalance)} Da", boldFont, 14f, TextAlignment.CENTER)

        doc.add(Paragraph("\n").setFontSize(0.3f))
        addText(doc, "Transaction: #${creditData.transactionId}", regularFont, 9f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(0.3f))
        addText(doc, "────────────────────────", regularFont, 8f, TextAlignment.CENTER)
    }

    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        its_GrossistApp: Boolean = true,
        autoShare: Boolean = false
    ): Result<PdfResult> {
        if (operations.isEmpty()) return Result.failure(IllegalArgumentException("No operations to print"))

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)
        val params = PdfGenerationParams(
            PdfType.RECEIPT_ONLY,
            client,
            operations,
            tarificationRepo,
            produitRepo,
            transactionId = transactionId,
            its_GrossistApp = its_GrossistApp,
            autoShare = autoShare
        )

        generateUnifiedPdf(file.absolutePath, params)
        if (!file.exists()) return Result.failure(IllegalStateException("PDF file creation failed"))

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)

        // Auto-share if requested
        if (autoShare) {
            uploadHandler.shareDocument(context, file)
        }

        return Result.success(PdfResult(file.absolutePath, url, file))
    }

    suspend fun generateVentReceiptWithCreditPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        bonVent: M8BonVent,
        versement: Double,
        its_GrossistApp: Boolean = true,
        autoShare: Boolean = false
    ): Result<PdfResult> {
        if (operations.isEmpty()) return Result.failure(IllegalArgumentException("No operations to print"))

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt_credit", transactionId)
        val params = PdfGenerationParams(
            PdfType.RECEIPT_WITH_CREDIT,
            client,
            operations,
            tarificationRepo,
            produitRepo,
            bonVent,
            versement,
            transactionId,
            its_GrossistApp,
            autoShare = autoShare
        )

        generateUnifiedPdf(file.absolutePath, params)
        if (!file.exists()) return Result.failure(IllegalStateException("PDF file creation failed"))

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)

        // Auto-share if requested
        if (autoShare) {
            uploadHandler.shareDocument(context, file)
        }

        return Result.success(PdfResult(file.absolutePath, url, file))
    }

    suspend fun generateCreditReceiptPdf(
        context: Context,
        data: CreditReceiptData,
        autoShare: Boolean = false
    ): Result<PdfResult> {
        if (data.totalAmount <= 0) return Result.failure(IllegalArgumentException("Invalid total amount"))

        val file = uploadHandler.createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
        val params = PdfGenerationParams(
            PdfType.CREDIT_ONLY,
            data.client,
            transactionId = data.transactionId,
            creditData = data,
            autoShare = autoShare
        )

        generateUnifiedPdf(file.absolutePath, params)
        if (!file.exists()) return Result.failure(IllegalStateException("PDF file creation failed"))

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)

        // Auto-share if requested
        if (autoShare) {
            uploadHandler.shareDocument(context, file)
        }

        return Result.success(PdfResult(file.absolutePath, url, file))
    }

    /**
     * Convenience method to share an already generated PDF
     */
    fun sharePdf(context: Context, pdfResult: PdfResult) {
        uploadHandler.shareDocument(context, pdfResult.file)
    }

    private fun find_Relative_Categorie(rela_produit: ArticlesBasesStatsTable): CategoriesTabelle? =
        rela_produit.idParentCategorie?.let { repositorysMainGetter.find_M16CategorieProduit_By_OldID(it) }

    private fun formatProductNameWithCategory(produit: ArticlesBasesStatsTable?): String {
        val productName = cleanAndCapitalizeProductName(produit?.nom ?: "Produit")

        // Add © after product name if it doesn't already contain it
        val productNameWithCopyright = if (!productName.contains("©")) {
            "$productName ©"
        } else {
            productName
        }

        val category = produit?.let { find_Relative_Categorie(it) }
        return if (category != null && category.nom.isNotBlank()) {
            val cleanCategoryName = cleanAndCapitalizeProductName(category.nom)
            "$productNameWithCopyright ($cleanCategoryName)"
        } else {
            productNameWithCopyright
        }
    }

    private fun cleanAndCapitalizeProductName(name: String): String {
        val nameWithoutHash = name.replace("#", "").trim()

        // If we have a very short result, return as-is
        if (nameWithoutHash.length < 2) {
            return nameWithoutHash
        }

        // Split by spaces and capitalize each word
        return nameWithoutHash.split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
    }

    private fun addCompactLabelValue(doc: Document, label: String, value: String, labelFont: PdfFont, valueFont: PdfFont) {
        val paragraph = Paragraph()
            .add(com.itextpdf.layout.element.Text(label).setFont(labelFont).setFontSize(12f))
            .add(com.itextpdf.layout.element.Text(" $value").setFont(valueFont).setFontSize(12f))
            .setTextAlignment(TextAlignment.LEFT)
            .setMargin(0f)

        doc.add(paragraph)
        doc.add(Paragraph("\n").setFontSize(0.3f))
    }

    private fun addHeader(doc: Document, title: String, regularFont: PdfFont, boldFont: PdfFont, bonVentKeyId: String? = null) {
        bonVentKeyId?.let {
            if (it.length >= 3) {
                val prefix = it.dropLast(3)
                val last3UpperCase = it.takeLast(3).uppercase()

                val paragraph = Paragraph()
                    .add(com.itextpdf.layout.element.Text("Bon Vent : $prefix").setFont(regularFont).setFontSize(11f))
                    .add(com.itextpdf.layout.element.Text(last3UpperCase).setFont(boldFont).setFontSize(11f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMargin(0f)

                doc.add(paragraph)
            } else {
                addText(doc, "Bon Vent : ${it.uppercase()}", boldFont, 11f, TextAlignment.CENTER)
            }
            doc.add(Paragraph("\n").setFontSize(0.3f))
        }
    }

    private fun addClientDate(doc: Document, clientName: String, regularFont: PdfFont) {
        val date = formatDateWithAmPm(Date())
        val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).setWidth(UnitValue.createPercentValue(100f))

        val clientCell = Cell().add(Paragraph("Client : ${capitalizeFirstLetter(clientName)}").setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        val dateCell = Cell().add(Paragraph(date).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

        table.addCell(clientCell).addCell(dateCell)
        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))
    }

    fun formatDateWithAmPm(date: Date): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val frenchDays = arrayOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
        val frenchMonths = arrayOf("Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc")
        val dayOfWeek = frenchDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val month = frenchMonths[calendar.get(Calendar.MONTH)]
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return "$dayOfWeek $dayOfMonth/$month/$year ${String.format("%02d:%02d", hour, minute)}"
    }

    private fun addCreditSection(doc: Document, client: M2Client?, bonVent: M8BonVent, versement: Double, regularFont: PdfFont, boldFont: PdfFont, currentReceiptTotal: Double) {
        val oldBalance = client?.currentCreditBalance ?: 0.0
        val currentBill = currentReceiptTotal
        val newBalance = oldBalance + currentBill - versement

        addCompactLabelValue(doc, "Ancien Solde :", "${round(oldBalance)} Da", regularFont, boldFont)
        addCompactLabelValue(doc, "Bon actuel :", "${round(currentBill)} Da", regularFont, boldFont)
        addCompactLabelValue(doc, "Versement :", "${round(versement)} Da", regularFont, boldFont)
        addCompactLabelValue(doc, "Nouv. Soldé :", "${round(newBalance)} Da", regularFont, boldFont)
    }

    private fun generateUnifiedPdf(path: String, params: PdfGenerationParams) {
        val (regularFont, boldFont) = PdfFontFactory.createFont(StandardFonts.HELVETICA) to PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        PdfWriter(path).use { writer ->
            PdfDocument(writer).use { pdfDoc ->
                Document(pdfDoc, PageSize.A5).use { doc ->
                    var currentReceiptTotal = 0.0

                    when (params.type) {
                        PdfType.RECEIPT_ONLY, PdfType.RECEIPT_WITH_CREDIT -> {
                            val title = if (!params.its_GrossistApp) "Facture" else ""
                            addHeader(doc, title, regularFont, boldFont, params.bonVent?.keyID)
                        }
                        PdfType.CREDIT_ONLY -> {
                            val receiptType = if (params.creditData?.showPaymentHistory == true) "Credit Payment Prix_Détaillé" else "Reçu de Paiement"
                            addHeader(doc, receiptType, regularFont, boldFont, params.bonVent?.keyID)
                        }
                    }

                    addClientDate(doc, params.client?.nom ?: "Client", regularFont)

                    if (params.type == PdfType.CREDIT_ONLY && params.creditData?.showPaymentHistory == true) {
                        addText(doc, "Transaction: #${params.transactionId}", regularFont, 10f, TextAlignment.LEFT)
                        doc.add(Paragraph("\n").setFontSize(0.3f))
                    }

                    if (params.type != PdfType.CREDIT_ONLY) {
                        params.tarificationRepo?.let { tarificationRepo ->
                            params.produitRepo?.let { produitRepo ->
                                val showTotalSection = params.type != PdfType.RECEIPT_WITH_CREDIT
                                if (showTotalSection) {
                                    createProductTable(doc, params.operations, tarificationRepo, produitRepo, regularFont, boldFont)
                                } else {
                                    currentReceiptTotal = createProductTableAndReturnTotal(doc, params.operations, tarificationRepo, produitRepo, regularFont, boldFont)
                                }
                            }
                        }

                        if (params.type == PdfType.RECEIPT_ONLY) {
                            params.client?.currentCreditBalance?.takeIf { it < 0 }?.let { credit ->
                                addText(doc, "Credit Du Compte actuel", regularFont, 12f, TextAlignment.CENTER)
                                addText(doc, "${round(credit)}Da", regularFont, 14f, TextAlignment.CENTER)
                            }
                        }
                    }

                    if (params.type == PdfType.RECEIPT_WITH_CREDIT || params.type == PdfType.CREDIT_ONLY) {
                        if (params.type == PdfType.RECEIPT_WITH_CREDIT) {
                            doc.add(Paragraph("\n").setFontSize(0.3f))
                            addText(doc, "────────────────────────", regularFont, 10f, TextAlignment.CENTER)
                            doc.add(Paragraph("\n").setFontSize(0.3f))
                        }

                        when (params.type) {
                            PdfType.RECEIPT_WITH_CREDIT -> params.bonVent?.let { addCreditSection(doc, params.client, it, params.versement, regularFont, boldFont, currentReceiptTotal) }
                            PdfType.CREDIT_ONLY -> params.creditData?.let { addCreditOnlySection(doc, it, regularFont, boldFont) }
                            else -> {}
                        }
                    }
                }
            }
        }
    }


    suspend fun generateVentReceiptPdf(context: Context, client: M2Client?, operations: List<M10OperationVentCouleur>, tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, transactionId: String = "", its_GrossistApp: Boolean = true): Result<String> {
        if (operations.isEmpty()) return Result.failure(IllegalArgumentException("No operations to print"))

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)
        val params = PdfGenerationParams(PdfType.RECEIPT_ONLY, client, operations, tarificationRepo, produitRepo, transactionId = transactionId, its_GrossistApp = its_GrossistApp)

        generateUnifiedPdf(file.absolutePath, params)
        if (!file.exists()) return Result.failure(IllegalStateException("PDF file creation failed"))

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    suspend fun generateVentReceiptWithCreditPdf(context: Context, client: M2Client?, operations: List<M10OperationVentCouleur>, tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, transactionId: String = "", bonVent: M8BonVent, versement: Double, its_GrossistApp: Boolean = true): Result<String> {
        if (operations.isEmpty()) return Result.failure(IllegalArgumentException("No operations to print"))

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt_credit", transactionId)
        val params = PdfGenerationParams(PdfType.RECEIPT_WITH_CREDIT, client, operations, tarificationRepo, produitRepo, bonVent, versement, transactionId, its_GrossistApp)

        generateUnifiedPdf(file.absolutePath, params)
        if (!file.exists()) return Result.failure(IllegalStateException("PDF file creation failed"))

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    suspend fun generateCreditReceiptPdf(context: Context, data: CreditReceiptData): Result<String> {
        if (data.totalAmount <= 0) return Result.failure(IllegalArgumentException("Invalid total amount"))

        val file = uploadHandler.createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
        val params = PdfGenerationParams(PdfType.CREDIT_ONLY, data.client, transactionId = data.transactionId, creditData = data)

        generateUnifiedPdf(file.absolutePath, params)
        if (!file.exists()) return Result.failure(IllegalStateException("PDF file creation failed"))

        val url =uploadHandler. uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    private fun createProductTableAndReturnTotal(doc: Document, operations: List<M10OperationVentCouleur>, tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, regularFont: PdfFont, boldFont: PdfFont): Double {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(6f, 15f, 14f, 50f, 12f))).setWidth(UnitValue.createPercentValue(100f))

        table.addCell(createHeaderCell("N°", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
        table.addCell(createHeaderCell("Sous-total", boldFont, 11f, TextAlignment.RIGHT))

        var total = 0.0
        var rowNumber = 1
        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }

        groupedOps.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find { it.keyID == ops.first().parentM13TarificationKeyID }
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val price = tarification?.prixCurrency ?: 0.0
            val subtotal = price * qty

            if (subtotal != 0.0) {
                val qtyDisplay = formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)
                val productNameWithCategory = formatProductNameWithCategory(produit)
                val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                    val nombreUniteInt = produit.nombreUniteInt
                    if (nombreUniteInt > 0) price / nombreUniteInt else price
                } else price

                table.addCell(createDataCell(rowNumber.toString(), regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell("${round(unitPrice)}", regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(productNameWithCategory, regularFont, 10f, TextAlignment.LEFT))
                table.addCell(createDataCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))

                total += subtotal
                rowNumber++
            }
        }

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        return total
    }

    private fun formatQuantity(qty: Int, cartonSize: Int, produit: ArticlesBasesStatsTable?): String {
        val shouldShowUnits = produit?.afficheUniteAuPrint == true
        val nombreUniteInt = produit?.nombreUniteInt ?: 1

        return when {
            shouldShowUnits && cartonSize in 2..qty && qty % cartonSize == 0 -> {
                val cartons = qty / cartonSize
                "$cartons X $cartonSize X $nombreUniteInt"
            }
            shouldShowUnits -> "$qty X $nombreUniteInt"
            cartonSize in 2..qty && qty % cartonSize == 0 -> {
                val cartons = qty / cartonSize
                "$cartons X $cartonSize"
            }
            else -> qty.toString()
        }
    }

    private fun createProductTable(doc: Document, operations: List<M10OperationVentCouleur>, tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, regularFont: PdfFont, boldFont: PdfFont) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 15f, 20f, 35f, 20f))).setWidth(UnitValue.createPercentValue(100f))

        table.addCell(createHeaderCell("N°", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
        table.addCell(createHeaderCell("Sous-total", boldFont, 11f, TextAlignment.RIGHT))

        var total = 0.0
        var rowNumber = 1
        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }

        groupedOps.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find { it.keyID == ops.first().parentM13TarificationKeyID }
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val price = tarification?.prixCurrency ?: 0.0
            val subtotal = price * qty

            if (subtotal != 0.0) {
                val qtyDisplay = formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)
                val productNameWithCategory = formatProductNameWithCategory(produit)
                val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                    val nombreUniteInt = produit.nombreUniteInt
                    if (nombreUniteInt > 0) price / nombreUniteInt else price
                } else price

                table.addCell(createDataCell(rowNumber.toString(), regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell("${round(unitPrice)}", regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(productNameWithCategory, regularFont, 10f, TextAlignment.LEFT))
                table.addCell(createDataCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))

                total += subtotal
                rowNumber++
            }
        }

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        addText(doc, "Total", boldFont, 14f, TextAlignment.CENTER)
        addText(doc, "${round(total)}Da", boldFont, 16f, TextAlignment.CENTER)
    }


    private fun createHeaderCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell {
        val paragraph = Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align).setMargin(0f)
        return Cell().add(paragraph).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(4f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE).setTextAlignment(align)
    }

    private fun createDataCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell().add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.1f)).setPadding(4f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)

    private fun addText(doc: Document, text: String, font: PdfFont, size: Float, align: TextAlignment) =
        doc.add(Paragraph(text).setFont(font).setFontSize(size).setTextAlignment(align).setMargin(0f))

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0

    private fun capitalizeFirstLetter(text: String): String {
        return if (text.isBlank()) text
        else text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

}
