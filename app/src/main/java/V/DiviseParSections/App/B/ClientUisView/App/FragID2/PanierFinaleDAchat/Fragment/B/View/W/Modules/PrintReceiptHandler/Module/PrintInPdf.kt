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
import android.os.Environment
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
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
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CreditReceiptData(
    val client: M2Client?,
    val totalAmount: Double,
    val currentPayment: Double,
    val previousPayments: List<Double> = emptyList(),
    val transactionId: String,
    val showPaymentHistory: Boolean = false
)

class PrintInPdf_itextpdf_Handler(
    val repositorysMainGetter: RepositorysMainGetter,
) {
    fun find_Relative_Categorie(rela_produit: ArticlesBasesStatsTable): CategoriesTabelle? {
        return rela_produit.idParentCategorie?.let { parentCategoryId ->
            repositorysMainGetter.find_M16CategorieProduit_By_OldID(parentCategoryId)
        }
    }

    private fun formatProductNameWithCategory(produit: ArticlesBasesStatsTable?): String {
        val productName = capitalizeFirstLetter(produit?.nom ?: "Produit")

        val category = produit?.let { find_Relative_Categorie(it) }

        return if (category != null && category.nom.isNotBlank()) {
            "$productName (${capitalizeFirstLetter(category.nom)})"
        } else {
            productName
        }
    }
    // Ajouter cette méthode dans PrintInPdf_itextpdf_Handler

    suspend fun generateVentReceiptWithCreditPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        bonVent: M8BonVent,
        versement: Double,
        its_GrossistApp: Boolean = true
    ): Result<String> {
        return try {
            if (operations.isEmpty()) {
                return Result.failure(IllegalArgumentException("No operations to print"))
            }

            if (regularFont == null || boldFont == null) {
                return Result.failure(IllegalStateException("PDF fonts initialization failed"))
            }

            val file = createLocalFile(context, client?.nom ?: "Client", "receipt_credit", transactionId)
            generateVentWithCreditPdf(file.absolutePath, client, operations, tarificationRepo, produitRepo, bonVent, versement, its_GrossistApp)

            if (!file.exists()) {
                return Result.failure(IllegalStateException("PDF file creation failed"))
            }

            val url = uploadToFirebaseStorage(file, file.name)
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateVentWithCreditPdf(
        path: String,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        bonVent: M8BonVent,
        versement: Double,
        its_GrossistApp: Boolean
    ) {
        try {
            val (regularFont, boldFont) = createFreshFonts()

            PdfWriter(path).use { writer ->
                PdfDocument(writer).use { pdfDoc ->
                    Document(pdfDoc, PageSize.A5).use { doc ->
                        if (!its_GrossistApp) {
                            addHeader(doc, "Facture", regularFont, boldFont)
                        }

                        addClientDate(doc, client?.nom ?: "Client", regularFont)
                        createProductTable(doc, operations, tarificationRepo, produitRepo, regularFont, boldFont)

                        // Ne pas afficher l'ancien crédit ici car on va l'afficher dans la section crédit

                        // Ajouter un séparateur avant la section crédit
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        addText(doc, "═══════════════════════", regularFont, 10f, TextAlignment.CENTER)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

                        // Ajouter la section crédit
                        addCreditSectionToPdf(doc, client, bonVent, versement, regularFont, boldFont)
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun addCreditSectionToPdf(
        doc: Document,
        client: M2Client?,
        bonVent: M8BonVent,
        versement: Double,
        regularFont: PdfFont,
        boldFont: PdfFont
    ) {
        try {
            val oldBalance = client?.currentCreditBalance ?: 0.0
            val currentBill = bonVent.sum_De_Totale_Vents
            val newBalance = oldBalance + currentBill - versement

            // Titre de la section
            addText(doc, "SECTION CRÉDIT", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

            // Ancien solde
            if (oldBalance != 0.0) {
                addText(doc, "Ancien Soldé :", regularFont, 12f, TextAlignment.LEFT)
                addText(doc, "${round(oldBalance)} Da", boldFont, 14f, TextAlignment.CENTER)
                doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            }

            // Bon actuel
            addText(doc, "Bon actuel :", regularFont, 12f, TextAlignment.LEFT)
            addText(doc, "${round(currentBill)} Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

            // Versement
            addText(doc, "Versement :", boldFont, 12f, TextAlignment.LEFT)
            addText(doc, "${round(versement)} Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

            // Ligne de séparation
            addText(doc, "─────────────────────", regularFont, 10f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

            // Nouveau solde
            addText(doc, "Nouv. Soldé :", boldFont, 12f, TextAlignment.LEFT)

            when {
                newBalance > 0 -> {
                    addText(doc, "${round(newBalance)} Da", boldFont, 16f, TextAlignment.CENTER)
                    addText(doc, "(Reste à payer)", regularFont, 10f, TextAlignment.CENTER)
                }
                newBalance < 0 -> {
                    addText(doc, "${round(newBalance)} Da", boldFont, 16f, TextAlignment.CENTER)
                    addText(doc, "(Crédit client)", regularFont, 10f, TextAlignment.CENTER)
                }
                else -> {
                    addText(doc, "0.00 Da", boldFont, 16f, TextAlignment.CENTER)
                    addText(doc, "✓ SOLDÉ ✓", boldFont, 12f, TextAlignment.CENTER)
                    addText(doc, "Merci pour votre confiance", regularFont, 10f, TextAlignment.CENTER)
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }
    data class CreditReceiptData(
        val client: M2Client?,
        val totalAmount: Double,
        val currentPayment: Double,
        val previousPayments: List<Double> = emptyList(),
        val transactionId: String,
        val showPaymentHistory: Boolean = false,
        // Add these fields to match the receipt format
        val oldBalance: Double = 0.0,  // "Ancien Soldé" - previous balance
        val currentBill: Double = 0.0  // "Bon actuel" - current bill amount
    )

    private fun generateCreditPdf(path: String, data: CreditReceiptData) {
        try {
            val (regularFont, boldFont) = createFreshFonts()

            PdfWriter(path).use { writer ->
                PdfDocument(writer).use { pdfDoc ->
                    Document(pdfDoc, PageSize.A5).use { doc ->
                        val receiptType = if (data.showPaymentHistory) "Credit Payment Prix_Détaillé" else "Reçu de Paiement"
                        addHeader(doc, receiptType, regularFont, boldFont)
                        addClientDate(doc, data.client?.nom ?: "Client", regularFont)

                        if (data.showPaymentHistory) {
                            addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.LEFT)
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        }

                        // Display old balance (Ancien Soldé)
                        if (data.oldBalance != 0.0) {
                            addText(doc, "Ancien Soldé :", regularFont, 12f, TextAlignment.LEFT)
                            addText(doc, "${round(data.oldBalance)} Da", boldFont, 14f, TextAlignment.CENTER)
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        }

                        // Display current bill (Bon actuel)
                        if (data.currentBill > 0) {
                            addText(doc, "Bon actuel :", regularFont, 12f, TextAlignment.LEFT)
                            addText(doc, "${round(data.currentBill)} Da", boldFont, 14f, TextAlignment.CENTER)
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        }

                        // Display payment (Versement)
                        addText(doc, "Versement :", boldFont, 12f, TextAlignment.LEFT)
                        addText(doc, "${round(data.currentPayment)} Da", boldFont, 14f, TextAlignment.CENTER)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

                        // Calculate and display new balance (Nouv. Soldé)
                        val totalPaid = if (data.showPaymentHistory) data.previousPayments.sum() + data.currentPayment else data.currentPayment
                        val newBalance = data.oldBalance + data.currentBill - totalPaid

                        addText(doc, "Nouv. Soldé :", boldFont, 12f, TextAlignment.LEFT)

                        when {
                            newBalance > 0 -> {
                                addText(doc, "${round(newBalance)} Da", boldFont, 16f, TextAlignment.CENTER)
                                addText(doc, "(Reste à payer)", regularFont, 10f, TextAlignment.CENTER)
                            }
                            newBalance < 0 -> {
                                addText(doc, "${round(newBalance)} Da", boldFont, 16f, TextAlignment.CENTER)
                                addText(doc, "(Crédit client)", regularFont, 10f, TextAlignment.CENTER)
                            }
                            else -> {
                                addText(doc, "0.00 Da", boldFont, 16f, TextAlignment.CENTER)
                                addText(doc, "✓ SOLDÉ ✓", boldFont, 12f, TextAlignment.CENTER)
                                addText(doc, "Merci pour votre confiance", regularFont, 10f, TextAlignment.CENTER)
                            }
                        }

                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        addText(doc, "Transaction: #${data.transactionId}", regularFont, 9f, TextAlignment.CENTER)

                        // Add separator line like in the image
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        addText(doc, "————————————————————————", regularFont, 8f, TextAlignment.CENTER)
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    companion object {
        private const val SPACING_2DP = 0.7f
    }

    private val storageRef = Firebase.storage.reference.child("bonVents_pdf")

    private fun createFreshFonts(): Pair<PdfFont, PdfFont> {
        return try {
            val regular = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            Pair(regular, bold)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to create PDF fonts", e)
        }
    }

    private fun generateVentPdf(
        path: String, client: M2Client?, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, its_GrossistApp: Boolean
    ) {
        try {
            val (regularFont, boldFont) = createFreshFonts()

            PdfWriter(path).use { writer ->
                PdfDocument(writer).use { pdfDoc ->
                    Document(pdfDoc, PageSize.A5).use { doc ->
                        if (!its_GrossistApp) {
                            addHeader(doc, "Facture", regularFont, boldFont)
                        }

                        addClientDate(doc, client?.nom ?: "Client", regularFont)
                        createProductTable(doc, operations, tarificationRepo, produitRepo, regularFont, boldFont)

                        client?.currentCreditBalance?.takeIf { it < 0 }?.let { credit ->
                            addText(doc, "Credit Du Compte actuel", regularFont, 12f, TextAlignment.CENTER)
                            addText(doc, "${round(credit)}Da", regularFont, 14f, TextAlignment.CENTER)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun addHeader(doc: Document, title: String, regularFont: PdfFont, boldFont: PdfFont) {
        try {
            addText(doc, "Abdelwahab", boldFont, 18f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, "JeMla.Com", boldFont, 16f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, "0553885037", regularFont, 12f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, title, regularFont, 12f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
        } catch (e: Exception) {
            throw e
        }
    }

    private fun addClientDate(doc: Document, clientName: String, regularFont: PdfFont) {
        try {
            val date = formatDateWithAmPm(Date())
            val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            table.setWidth(UnitValue.createPercentValue(100f))

            val clientCell = Cell().add(Paragraph("Client : ${capitalizeFirstLetter(clientName)}").setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

            val dateCell = Cell().add(Paragraph(date).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

            table.addCell(clientCell)
            table.addCell(dateCell)
            doc.add(table)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
        } catch (e: Exception) {
            throw e
        }
    }

    private val regularFont: PdfFont? by lazy {
        try {
            PdfFontFactory.createFont(StandardFonts.HELVETICA)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDateWithAmPm(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val frenchDays = arrayOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
        val dayOfWeek = frenchDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]

        val frenchMonths = arrayOf(
            "Jan", "Fév", "Mar", "Avr", "Mai", "Jun",
            "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"
        )
        val month = frenchMonths[calendar.get(Calendar.MONTH)]

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return "$dayOfWeek $dayOfMonth/$month/$year ${String.format("%02d:%02d", hour, minute)}"
    }

    private val boldFont: PdfFont? by lazy {
        try {
            PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        its_GrossistApp: Boolean = true
    ): Result<String> {
        return try {
            if (operations.isEmpty()) {
                return Result.failure(IllegalArgumentException("No operations to print"))
            }

            if (regularFont == null || boldFont == null) {
                return Result.failure(IllegalStateException("PDF fonts initialization failed"))
            }

            val file = createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)
            generateVentPdf(file.absolutePath, client, operations, tarificationRepo, produitRepo, its_GrossistApp)

            if (!file.exists()) {
                return Result.failure(IllegalStateException("PDF file creation failed"))
            }

            val url = uploadToFirebaseStorage(file, file.name)
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateCreditReceiptPdf(context: Context, data: CreditReceiptData): Result<String> {
        return try {
            if (data.totalAmount <= 0) {
                return Result.failure(IllegalArgumentException("Invalid total amount"))
            }

            if (regularFont == null || boldFont == null) {
                return Result.failure(IllegalStateException("PDF fonts initialization failed"))
            }

            val file = createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
            generateCreditPdf(file.absolutePath, data)

            if (!file.exists()) {
                return Result.failure(IllegalStateException("PDF file creation failed"))
            }

            val url = uploadToFirebaseStorage(file, file.name)
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatQuantity(qty: Int, cartonSize: Int, produit: ArticlesBasesStatsTable?): String {
        val shouldShowUnits = produit?.afficheUniteAuPrint == true
        val nombreUniteInt = produit?.nombreUniteInt ?: 1

        return when {
            shouldShowUnits && cartonSize in 2..qty && qty % cartonSize == 0 -> {
                val cartons = qty / cartonSize
                "$cartons X $cartonSize X $nombreUniteInt"
            }
            shouldShowUnits -> {
                "$qty X $nombreUniteInt"
            }
            cartonSize in 2..qty && qty % cartonSize == 0 -> {
                val cartons = qty / cartonSize
                "$cartons X $cartonSize"
            }
            else -> qty.toString()
        }
    }

    private fun createProductTable(
        doc: Document, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit,
        regularFont: PdfFont, boldFont: PdfFont
    ) {
        try {
            val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 15f, 20f, 35f, 20f)))
            table.setWidth(UnitValue.createPercentValue(100f))

            table.addCell(createHeaderCell("N°", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
            table.addCell(createHeaderCell("Sous-total", boldFont, 11f, TextAlignment.RIGHT))

            var total = 0.0
            var rowNumber = 1

            val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }

            groupedOps.forEach { (produitId, ops) ->
                try {
                    val tarification = tarificationRepo.datasValue.find { it.keyID == ops.first().parentM13TarificationKeyID }
                    val produit = produitRepo.datasValue.find { it.keyID == produitId }
                    val qty = ops.sumOf { it.quantity }
                    val price = tarification?.prixCurrency ?: 0.0
                    val subtotal = price * qty

                    if (subtotal != 0.0) {
                        val qtyDisplay = formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)

                        // Use the new method to format product name with category
                        val productNameWithCategory = formatProductNameWithCategory(produit)

                        val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                            val nombreUniteInt = produit.nombreUniteInt
                            if (nombreUniteInt > 0) price / nombreUniteInt else price
                        } else {
                            price
                        }

                        table.addCell(createDataCell(rowNumber.toString(), regularFont, 10f, TextAlignment.CENTER))
                        table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                        table.addCell(createDataCell("${round(unitPrice)}", regularFont, 10f, TextAlignment.CENTER))
                        table.addCell(createDataCell(productNameWithCategory, regularFont, 10f, TextAlignment.LEFT))
                        table.addCell(createDataCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))

                        total += subtotal
                        rowNumber++
                    }
                } catch (e: Exception) {
                    // Continue avec le prochain produit
                }
            }

            doc.add(table)
            doc.add(Paragraph("\n").setFontSize(0.1f))

            addText(doc, "Total", boldFont, 14f, TextAlignment.CENTER)
            addText(doc, "${round(total)}Da", boldFont, 16f, TextAlignment.CENTER)

        } catch (e: Exception) {
            throw e
        }
    }

    private fun createLocalFile(context: Context, clientName: String, type: String, id: String): File {
        return try {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")

            if (!dir.exists()) {
                dir.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val sanitizedClientName = clientName.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val fileName = "${type}_${sanitizedClientName}_${timestamp}_$id.pdf"

            File(dir, fileName)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun createHeaderCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell {
        val paragraph = Paragraph(content)
            .setFont(font)
            .setFontSize(size)
            .setTextAlignment(align)
            .setMargin(0f)

        return Cell()
            .add(paragraph)
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(4f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(align)
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
        else text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    private suspend fun uploadToFirebaseStorage(file: File, fileName: String): String {
        return try {
            val fileRef = storageRef.child(fileName)
            val uploadTask = fileRef.putFile(android.net.Uri.fromFile(file))
            uploadTask.await()
            fileRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }
}
