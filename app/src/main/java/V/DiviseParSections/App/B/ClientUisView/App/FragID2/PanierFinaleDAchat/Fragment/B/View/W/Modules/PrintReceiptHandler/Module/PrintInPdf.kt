package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
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
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
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

class PrintInPdf_itextpdf_Handler {
    // FIXED: Now using a unified table approach as suggested in the PDF comments
    val storageRef = Firebase.storage.reference.child("bonVents_pdf")
    private val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/bonVents_pdf"

    /**
     * Generate a PDF receipt directly from data objects
     */
    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        transactionId: String = ""
    ): Result<String> {
        return try {
            // Create local directory if it doesn't exist
            val localDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
            if (!localDir.exists()) {
                localDir.mkdirs()
            }

            // Generate filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val clientName = client?.nom?.takeIf { it.isNotBlank() } ?: "Client"
            val fileName = "receipt_${clientName}_${timestamp}_${transactionId}.pdf"
            val localFile = File(localDir, fileName)

            // Generate PDF
            val pdfPath = generateVentPdfFromData(
                localFile.absolutePath,
                client,
                relative_ListM10OperationVentCouleur,
                repo13TarificationInfos,
                repoM1Produit
            )

            // Upload to Firebase Storage
            val downloadUrl = uploadToFirebaseStorage(localFile, fileName)

            Result.success("PDF saved locally: $pdfPath\nFirebase URL: $downloadUrl")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate credit receipt PDF directly from data objects
     */
    suspend fun generateCreditReceiptPdf(
        context: Context,
        creditData: CreditReceiptData
    ): Result<String> {
        return try {
            // Create local directory if it doesn't exist
            val localDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
            if (!localDir.exists()) {
                localDir.mkdirs()
            }

            // Generate filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val clientName = creditData.client?.nom?.takeIf { it.isNotBlank() } ?: "Client"
            val fileName = "credit_${clientName}_${timestamp}_${creditData.transactionId}.pdf"
            val localFile = File(localDir, fileName)

            // Generate PDF
            val pdfPath = generateCreditPdfFromData(localFile.absolutePath, creditData)

            // Upload to Firebase Storage
            val downloadUrl = uploadToFirebaseStorage(localFile, fileName)

            Result.success("PDF saved locally: $pdfPath\nFirebase URL: $downloadUrl")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * FIXED: Generate PDF receipt with unified table structure like in the image
     * Uses a single table for all products instead of separate tables per row
     */
    private fun generateVentPdfFromData(
        outputPath: String,
        client: M2Client?,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit
    ): String {
        val writer = PdfWriter(outputPath)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A5)

        // Set up fonts
        val regularFont: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        // Date and client info
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val clientName = client?.nom?.takeIf { it.isNotBlank() } ?: "Client"

        // Header - Store info
        addCenteredText(document, "Abdelwahab", boldFont, 18f)
        addCenteredText(document, "JeMla.Com", boldFont, 16f)
        addCenteredText(document, "0553885037", regularFont, 12f)
        addCenteredText(document, "Facture", regularFont, 12f)

        // Add space
        document.add(Paragraph("\n").setFontSize(8f))

        // Client and date info
        val clientDateParagraph = Paragraph()
        clientDateParagraph.add(Text(clientName).setFont(regularFont).setFontSize(12f))
        clientDateParagraph.add(Text("                        $dateString").setFont(regularFont).setFontSize(12f))
        clientDateParagraph.setTextAlignment(TextAlignment.LEFT)
        document.add(clientDateParagraph)

        // Add space
        document.add(Paragraph("\n").setFontSize(8f))

        // FIXED: Create unified table structure like in the receipt image
        createUnifiedProductTable(
            document,
            relative_ListM10OperationVentCouleur,
            repo13TarificationInfos,
            repoM1Produit,
            regularFont,
            boldFont
        )

        // Credit info if applicable
        val ancienCredits = client?.currentCreditBalance ?: 0.0
        if (ancienCredits < 0) {
            addCenteredText(document, "Credit Du Compte actuel", regularFont, 12f)
            addCenteredText(document, "${round(ancienCredits)}Da", regularFont, 14f)
        }

        document.close()
        return outputPath
    }

    /**
     * FIXED: Create unified table structure for products (like in the receipt image)
     * This replaces the previous approach of multiple separate tables
     */
    private fun createUnifiedProductTable(
        document: Document,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont
    ) {
        // Create single table for all products with borders
        val mainTable = Table(UnitValue.createPercentArray(floatArrayOf(40f, 15f, 20f, 25f)))
        mainTable.setWidth(UnitValue.createPercentValue(100f))

        // Add header row with borders
        mainTable.addCell(createBorderedCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
        mainTable.addCell(createBorderedCell("Qté", boldFont, 11f, TextAlignment.CENTER))
        mainTable.addCell(createBorderedCell("P.U", boldFont, 11f, TextAlignment.CENTER))
        mainTable.addCell(createBorderedCell("Montant", boldFont, 11f, TextAlignment.RIGHT))

        // Process and add all products to the single table
        val groupe_Produit = relative_ListM10OperationVentCouleur.groupBy { it.parent_M1Produit_KeyId }.toList()
        var totaleBon = 0.0

        groupe_Produit.forEach { produit_vent ->
            val datas_repo13TarificationInfos = repo13TarificationInfos.datasValue
            val standart_Vent = produit_vent.second.first()
            val relative_Tariffication = datas_repo13TarificationInfos.find { it.keyID == standart_Vent.parentM13TarificationKeyID }
            val relative_M1Produit = repoM1Produit.datasValue.find { it.keyID == produit_vent.first }
            val quantite_Boit_Par_Carton = relative_M1Produit?.quantite_Boit_Par_Carton ?: 1
            val vent_quantity = produit_vent.second.sumOf { it.quantity }
            val quantityDisplay = formatQuantityDisplay(vent_quantity, quantite_Boit_Par_Carton)
            val vent_prix = relative_Tariffication?.prixCurrency ?: 0.0
            val subtotal = vent_prix * vent_quantity

            if (subtotal != 0.0) {
                // Add product row to the unified table
                mainTable.addCell(createBorderedCell(relative_M1Produit?.nom ?: "Produit", regularFont, 10f, TextAlignment.LEFT))
                mainTable.addCell(createBorderedCell(quantityDisplay, regularFont, 10f, TextAlignment.CENTER))
                mainTable.addCell(createBorderedCell("${round(vent_prix)}", regularFont, 10f, TextAlignment.CENTER))
                mainTable.addCell(createBorderedCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))

                totaleBon += subtotal
            }
        }

        // Add the complete table to document
        document.add(mainTable)

        // Add space after table
        document.add(Paragraph("\n").setFontSize(10f))

        // Total section
        addCenteredText(document, "Total", boldFont, 14f)
        addCenteredText(document, "${round(totaleBon)}Da", boldFont, 16f)
    }

    /**
     * Generate credit receipt PDF directly from data objects
     * Already using A5 format - no change needed
     */
    private fun generateCreditPdfFromData(
        outputPath: String,
        creditData: CreditReceiptData
    ): String {
        val writer = PdfWriter(outputPath)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A5)

        // Set up fonts
        val regularFont: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        // Date and client info
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val clientName = creditData.client?.nom?.takeIf { it.isNotBlank() } ?: "Client"
        val totalAmount = creditData.totalAmount
        val currentPayment = creditData.currentPayment
        val totalPaid = if (creditData.showPaymentHistory) {
            creditData.previousPayments.sum() + currentPayment
        } else {
            currentPayment
        }
        val remainingAmount = totalAmount - totalPaid

        // Header - Store info
        addCenteredText(document, "Abdelwahab", boldFont, 18f)
        addCenteredText(document, "JeMla.Com", boldFont, 16f)
        addCenteredText(document, "0553885037", regularFont, 12f)

        val receiptType = if (creditData.showPaymentHistory) "Credit Payment Prix_Détaillé" else "Credit Payment"
        addCenteredText(document, receiptType, regularFont, 12f)

        // Add space
        document.add(Paragraph("\n").setFontSize(8f))

        // Client and date info
        val clientDateParagraph = Paragraph()
        clientDateParagraph.add(Text(clientName).setFont(regularFont).setFontSize(12f))
        clientDateParagraph.add(Text("                        $dateString").setFont(regularFont).setFontSize(12f))
        clientDateParagraph.setTextAlignment(TextAlignment.LEFT)
        document.add(clientDateParagraph)

        // Add space
        document.add(Paragraph("\n").setFontSize(8f))

        // Separator
        addSeparatorLine(document, regularFont, "=====================")

        // Transaction ID if showing payment history
        if (creditData.showPaymentHistory) {
            addLeftAlignedText(document, "Transaction: #${creditData.transactionId}", regularFont, 10f)
            document.add(Paragraph("\n").setFontSize(8f))
        }

        // Total amount
        val totalLabel = if (creditData.showPaymentHistory) "Montant Total" else "Total à Payer"
        addLeftAlignedText(document, "$totalLabel :", boldFont, 12f)
        addCenteredText(document, "${round(totalAmount)}Da", boldFont, 14f)
        document.add(Paragraph("\n").setFontSize(8f))

        // Previous payments if showing history
        if (creditData.showPaymentHistory && creditData.previousPayments.isNotEmpty()) {
            addLeftAlignedText(document, "Paiements Précédents:", regularFont, 10f)
            creditData.previousPayments.forEachIndexed { index, payment ->
                addLeftAlignedText(document, "  ${index + 1}. ${round(payment)}Da", regularFont, 10f)
            }
            addLeftAlignedText(document, "Sous-total: ${round(creditData.previousPayments.sum())}Da", regularFont, 10f)
            document.add(Paragraph("\n").setFontSize(8f))
            addLeftAlignedText(document, "Paiement Actuel:", boldFont, 12f)
        } else {
            addLeftAlignedText(document, "Versement Effectué:", boldFont, 12f)
        }

        addCenteredText(document, "${round(currentPayment)}Da", boldFont, 14f)
        document.add(Paragraph("\n").setFontSize(8f))

        // Total paid if showing history
        if (creditData.showPaymentHistory) {
            addLeftAlignedText(document, "Total Payé: ${round(totalPaid)}Da", regularFont, 10f)
            document.add(Paragraph("\n").setFontSize(8f))
        }

        // Separator
        addSeparatorLine(document, regularFont, "---------------------")

        // Remaining amount or status
        when {
            remainingAmount > 0 -> {
                val remainingLabel = if (creditData.showPaymentHistory) "Reste à Payer" else "Crédit Restant"
                addLeftAlignedText(document, "$remainingLabel :", boldFont, 12f)
                addRightAlignedText(document, "${round(remainingAmount)}Da", boldFont, 16f)
            }
            remainingAmount < 0 -> {
                val overpayLabel = if (creditData.showPaymentHistory) "Trop Payé" else "Surplus Payé"
                addLeftAlignedText(document, "$overpayLabel :", boldFont, 12f)
                addRightAlignedText(document, "${round(-remainingAmount)}Da", boldFont, 16f)
            }
            else -> {
                if (creditData.showPaymentHistory) {
                    addCenteredText(document, "✓ PAYÉ COMPLÈTEMENT ✓", boldFont, 14f)
                    addCenteredText(document, "Merci pour votre confiance", regularFont, 12f)
                } else {
                    addCenteredText(document, "PAYÉ INTÉGRALEMENT", boldFont, 14f)
                    addCenteredText(document, "✓ SOLDÉ", boldFont, 12f)
                }
            }
        }

        // Transaction ID if not showing payment history
        if (!creditData.showPaymentHistory) {
            document.add(Paragraph("\n").setFontSize(8f))
            addCenteredText(document, "Transaction: #${creditData.transactionId}", regularFont, 10f)
        }

        document.close()
        return outputPath
    }

    /**
     * FIXED: Create a bordered table cell (for unified table structure)
     */
    private fun createBorderedCell(content: String, font: PdfFont, fontSize: Float, alignment: TextAlignment): Cell {
        val cell = Cell()
        cell.add(Paragraph(content).setFont(font).setFontSize(fontSize).setTextAlignment(alignment))
        cell.setBorder(SolidBorder(1f)) // Add solid border like in receipt image
        cell.setPadding(4f) // Slightly more padding for better readability
        return cell
    }

    /**
     * Create a table cell with specified formatting (kept for backward compatibility)
     */
    private fun createCell(content: String, font: PdfFont, fontSize: Float, alignment: TextAlignment): Cell {
        val cell = Cell()
        cell.add(Paragraph(content).setFont(font).setFontSize(fontSize).setTextAlignment(alignment))
        cell.setBorder(Border.NO_BORDER)
        cell.setPadding(2f)
        return cell
    }

    /**
     * Add centered text to document
     */
    private fun addCenteredText(document: Document, text: String, font: PdfFont, fontSize: Float) {
        val paragraph = Paragraph(text)
            .setFont(font)
            .setFontSize(fontSize)
            .setTextAlignment(TextAlignment.CENTER)
        document.add(paragraph)
    }

    /**
     * Add left-aligned text to document
     */
    private fun addLeftAlignedText(document: Document, text: String, font: PdfFont, fontSize: Float) {
        val paragraph = Paragraph(text)
            .setFont(font)
            .setFontSize(fontSize)
            .setTextAlignment(TextAlignment.LEFT)
        document.add(paragraph)
    }

    /**
     * Add right-aligned text to document
     */
    private fun addRightAlignedText(document: Document, text: String, font: PdfFont, fontSize: Float) {
        val paragraph = Paragraph(text)
            .setFont(font)
            .setFontSize(fontSize)
            .setTextAlignment(TextAlignment.RIGHT)
        document.add(paragraph)
    }

    /**
     * Add separator line
     */
    private fun addSeparatorLine(document: Document, font: PdfFont, separator: String) {
        val paragraph = Paragraph(separator)
            .setFont(font)
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.CENTER)
        document.add(paragraph)
    }

    /**
     * Format quantity display
     */
    private fun formatQuantityDisplay(quantity: Int, quantiteBoitParCarton: Int): String {
        return if (quantiteBoitParCarton in 2..quantity && quantity % quantiteBoitParCarton == 0) {
            val cartons = quantity / quantiteBoitParCarton
            "${cartons}x${quantiteBoitParCarton}(${quantity})"
        } else {
            quantity.toString()
        }
    }

    /**
     * Round value to 1 decimal place
     */
    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0

    /**
     * Upload PDF file to Firebase Storage
     */
    private suspend fun uploadToFirebaseStorage(localFile: File, fileName: String): String {
        return try {
            val fileRef = storageRef.child(fileName)
            val uploadTask = fileRef.putFile(android.net.Uri.fromFile(localFile))

            uploadTask.await()
            val downloadUrl = fileRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload to Firebase: ${e.message}")
        }
    }
}
