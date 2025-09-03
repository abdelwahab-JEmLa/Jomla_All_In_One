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
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
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
    // Fixed: Reduced spacing to 2dp equivalent (approximately 0.7f points)
    private val SPACING_2DP = 0.7f

    private val storageRef = Firebase.storage.reference.child("bonVents_pdf")
    private val regularFont: PdfFont by lazy { PdfFontFactory.createFont(StandardFonts.HELVETICA) }
    private val boldFont: PdfFont by lazy { PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD) }

    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        its_GrossistApp: Boolean = true
    ): Result<String> {
        if (operations.isEmpty()) return Result.failure(IllegalArgumentException("No operations to print"))
        val file = createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)
        generateVentPdf(file.absolutePath, client, operations, tarificationRepo, produitRepo, its_GrossistApp)
        val url = uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    suspend fun generateCreditReceiptPdf(context: Context, data: CreditReceiptData): Result<String> {
        if (data.totalAmount <= 0) return Result.failure(IllegalArgumentException("Invalid total amount"))
        val file = createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
        generateCreditPdf(file.absolutePath, data)
        val url = uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    private fun createLocalFile(context: Context, clientName: String, type: String, id: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
        if (!dir.exists()) dir.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val sanitizedClientName = clientName.replace("[^a-zA-Z0-9]".toRegex(), "_")
        return File(dir, "${type}_${sanitizedClientName}_${timestamp}_$id.pdf")
    }

    private fun generateVentPdf(
        path: String, client: M2Client?, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, its_GrossistApp: Boolean
    ) {
        PdfWriter(path).use { writer ->
            PdfDocument(writer).use { pdfDoc ->
                Document(pdfDoc, PageSize.A5).use { doc ->
                    // Only add header if its_GrossistApp is false
                    if (!its_GrossistApp) {
                        addHeader(doc, "Facture")
                    }
                    addClientDate(doc, client?.nom ?: "Client")
                    createProductTable(doc, operations, tarificationRepo, produitRepo)
                    client?.currentCreditBalance?.takeIf { it < 0 }?.let {
                        addText(doc, "Credit Du Compte actuel", regularFont, 12f, TextAlignment.CENTER)
                        addText(doc, "${round(it)}Da", regularFont, 14f, TextAlignment.CENTER)
                    }
                }
            }
        }
    }

    private fun generateCreditPdf(path: String, data: CreditReceiptData) {
        PdfWriter(path).use { writer ->
            PdfDocument(writer).use { pdfDoc ->
                Document(pdfDoc, PageSize.A5).use { doc ->
                    val receiptType = if (data.showPaymentHistory) "Credit Payment Prix_Détaillé" else "Credit Payment"
                    addHeader(doc, receiptType)
                    addClientDate(doc, data.client?.nom ?: "Client")

                    if (data.showPaymentHistory) {
                        addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.LEFT)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                    }

                    val totalPaid = if (data.showPaymentHistory) data.previousPayments.sum() + data.currentPayment else data.currentPayment
                    val remaining = data.totalAmount - totalPaid
                    val totalLabel = if (data.showPaymentHistory) "Montant Total" else "Total à Payer"

                    addText(doc, "$totalLabel :", boldFont, 12f, TextAlignment.LEFT)
                    addText(doc, "${round(data.totalAmount)}Da", boldFont, 14f, TextAlignment.CENTER)
                    doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

                    if (data.showPaymentHistory && data.previousPayments.isNotEmpty()) {
                        addText(doc, "Paiements Précédents:", regularFont, 10f, TextAlignment.LEFT)
                        data.previousPayments.forEachIndexed { i, payment ->
                            addText(doc, "  ${i + 1}. ${round(payment)}Da", regularFont, 10f, TextAlignment.LEFT)
                        }
                        addText(doc, "Sous-total: ${round(data.previousPayments.sum())}Da", regularFont, 10f, TextAlignment.LEFT)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        addText(doc, "Paiement Actuel:", boldFont, 12f, TextAlignment.LEFT)
                    } else {
                        addText(doc, "Versement Effectué:", boldFont, 12f, TextAlignment.LEFT)
                    }

                    addText(doc, "${round(data.currentPayment)}Da", boldFont, 14f, TextAlignment.CENTER)
                    doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

                    if (data.showPaymentHistory) {
                        addText(doc, "Total Payé: ${round(totalPaid)}Da", regularFont, 10f, TextAlignment.LEFT)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                    }

                    when {
                        remaining > 0 -> {
                            val label = if (data.showPaymentHistory) "Reste à Payer" else "Crédit Restant"
                            addText(doc, "$label :", boldFont, 12f, TextAlignment.LEFT)
                            addText(doc, "${round(remaining)}Da", boldFont, 16f, TextAlignment.RIGHT)
                        }
                        remaining < 0 -> {
                            val label = if (data.showPaymentHistory) "Trop Payé" else "Surplus Payé"
                            addText(doc, "$label :", boldFont, 12f, TextAlignment.LEFT)
                            addText(doc, "${round(-remaining)}Da", boldFont, 16f, TextAlignment.RIGHT)
                        }
                        else -> {
                            if (data.showPaymentHistory) {
                                addText(doc, "✓ PAYÉ COMPLÈTEMENT ✓", boldFont, 14f, TextAlignment.CENTER)
                                addText(doc, "Merci pour votre confiance", regularFont, 12f, TextAlignment.CENTER)
                            } else {
                                addText(doc, "PAYÉ INTÉGRALEMENT", boldFont, 14f, TextAlignment.CENTER)
                                addText(doc, "✓ SOLDÉ", boldFont, 12f, TextAlignment.CENTER)
                            }
                        }
                    }

                    if (!data.showPaymentHistory) {
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.CENTER)
                    }
                }
            }
        }
    }

    private fun addHeader(doc: Document, title: String) {
        addText(doc, "Abdelwahab", boldFont, 18f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
        addText(doc, "JeMla.Com", boldFont, 16f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
        addText(doc, "0553885037", regularFont, 12f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
        addText(doc, title, regularFont, 12f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
    }

    private fun addClientDate(doc: Document, clientName: String) {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        val clientCell = Cell().add(Paragraph(clientName).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

        val dateCell = Cell().add(Paragraph(date).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

        table.addCell(clientCell)
        table.addCell(dateCell)
        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
    }

    private fun createProductTable(
        doc: Document, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit
    ) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 20f, 45f, 20f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
        table.addCell(createHeaderCell("Montant", boldFont, 11f, TextAlignment.RIGHT))

        var total = 0.0
        operations.groupBy { it.parent_M1Produit_KeyId }.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find { it.keyID == ops.first().parentM13TarificationKeyID }
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val price = tarification?.prixCurrency ?: 0.0
            val subtotal = price * qty

            if (subtotal != 0.0) {
                val qtyDisplay = formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1)

                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell("${round(price)}", regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(produit?.nom ?: "Produit", regularFont, 10f, TextAlignment.LEFT))
                table.addCell(createDataCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))
                total += subtotal
            }
        }

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.1f))
        addText(doc, "Total", boldFont, 14f, TextAlignment.CENTER)
        addText(doc, "${round(total)}Da", boldFont, 16f, TextAlignment.CENTER)
    }

    private fun createHeaderCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell().add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f) // Minimum padding

    private fun createDataCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell().add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.1f)).setPadding(0f) // Minimum padding and border

    private fun addText(doc: Document, text: String, font: PdfFont, size: Float, align: TextAlignment) =
        doc.add(Paragraph(text).setFont(font).setFontSize(size).setTextAlignment(align).setMargin(0f))

    private fun addSeparator(doc: Document, separator: String) =
        addText(doc, separator, regularFont, 10f, TextAlignment.CENTER)

    private fun formatQuantity(qty: Int, cartonSize: Int): String =
        if (cartonSize in 2..qty && qty % cartonSize == 0)
            "${qty / cartonSize}x$cartonSize($qty)" else qty.toString()

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0

    private suspend fun uploadToFirebaseStorage(file: File, fileName: String): String {
        val fileRef = storageRef.child(fileName)
        fileRef.putFile(android.net.Uri.fromFile(file)).await()
        return fileRef.downloadUrl.await().toString()
    }
}
