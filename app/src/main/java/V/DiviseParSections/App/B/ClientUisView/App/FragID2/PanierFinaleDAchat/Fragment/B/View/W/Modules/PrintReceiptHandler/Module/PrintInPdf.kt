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
import com.itextpdf.layout.element.Text
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

class PrintInPdf_itextpdf_Handler {     //<--
//TODO(1): utilise les commentaire don l image pour regle ce code 
    private val storageRef = Firebase.storage.reference.child("bonVents_pdf")
    private val regularFont: PdfFont by lazy { PdfFontFactory.createFont(StandardFonts.HELVETICA) }
    private val boldFont: PdfFont by lazy { PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD) }

    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = ""
    ): Result<String> = try {
        val file = createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)
        generateVentPdf(file.absolutePath, client, operations, tarificationRepo, produitRepo)
        val url = uploadToFirebaseStorage(file, file.name)
        Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun generateCreditReceiptPdf(context: Context, data: CreditReceiptData): Result<String> = try {
        val file = createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
        generateCreditPdf(file.absolutePath, data)
        val url = uploadToFirebaseStorage(file, file.name)
        Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun createLocalFile(context: Context, clientName: String, type: String, id: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
        if (!dir.exists()) dir.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "${type}_${clientName}_${timestamp}_$id.pdf")
    }

    private fun generateVentPdf(
        path: String, client: M2Client?, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit
    ) {
        Document(PdfDocument(PdfWriter(path)), PageSize.A5).use { doc ->
            addHeader(doc, "Facture")
            addClientDate(doc, client?.nom ?: "Client")
            createProductTable(doc, operations, tarificationRepo, produitRepo)
            client?.currentCreditBalance?.takeIf { it < 0 }?.let {
                addText(doc, "Credit Du Compte actuel", regularFont, 12f, TextAlignment.CENTER)
                addText(doc, "${round(it)}Da", regularFont, 14f, TextAlignment.CENTER)
            }
        }
    }

    private fun generateCreditPdf(path: String, data: CreditReceiptData) {
        Document(PdfDocument(PdfWriter(path)), PageSize.A5).use { doc ->
            val receiptType = if (data.showPaymentHistory) "Credit Payment Prix_Détaillé" else "Credit Payment"
            addHeader(doc, receiptType)
            addClientDate(doc, data.client?.nom ?: "Client")

            addSeparator(doc, "=====================")

            if (data.showPaymentHistory) {
                addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.LEFT)
                doc.add(Paragraph("\n").setFontSize(8f))
            }

            val totalPaid = if (data.showPaymentHistory) data.previousPayments.sum() + data.currentPayment else data.currentPayment
            val remaining = data.totalAmount - totalPaid

            val totalLabel = if (data.showPaymentHistory) "Montant Total" else "Total à Payer"
            addText(doc, "$totalLabel :", boldFont, 12f, TextAlignment.LEFT)
            addText(doc, "${round(data.totalAmount)}Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(8f))

            if (data.showPaymentHistory && data.previousPayments.isNotEmpty()) {
                addText(doc, "Paiements Précédents:", regularFont, 10f, TextAlignment.LEFT)
                data.previousPayments.forEachIndexed { i, payment ->
                    addText(doc, "  ${i + 1}. ${round(payment)}Da", regularFont, 10f, TextAlignment.LEFT)
                }
                addText(doc, "Sous-total: ${round(data.previousPayments.sum())}Da", regularFont, 10f, TextAlignment.LEFT)
                doc.add(Paragraph("\n").setFontSize(8f))
                addText(doc, "Paiement Actuel:", boldFont, 12f, TextAlignment.LEFT)
            } else {
                addText(doc, "Versement Effectué:", boldFont, 12f, TextAlignment.LEFT)
            }

            addText(doc, "${round(data.currentPayment)}Da", boldFont, 14f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(8f))

            if (data.showPaymentHistory) {
                addText(doc, "Total Payé: ${round(totalPaid)}Da", regularFont, 10f, TextAlignment.LEFT)
                doc.add(Paragraph("\n").setFontSize(8f))
            }

            addSeparator(doc, "---------------------")

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
                doc.add(Paragraph("\n").setFontSize(8f))
                addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.CENTER)
            }
        }
    }

    private fun addHeader(doc: Document, title: String) {
        addText(doc, "Abdelwahab", boldFont, 18f, TextAlignment.CENTER)
        addText(doc, "JeMla.Com", boldFont, 16f, TextAlignment.CENTER)
        addText(doc, "0553885037", regularFont, 12f, TextAlignment.CENTER)
        addText(doc, title, regularFont, 12f, TextAlignment.CENTER)
        doc.add(Paragraph("\n").setFontSize(8f))
    }

    private fun addClientDate(doc: Document, clientName: String) {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val p = Paragraph()
        p.add(Text(clientName).setFont(regularFont).setFontSize(12f))
        p.add(Text("                        $date").setFont(regularFont).setFontSize(12f))
        p.setTextAlignment(TextAlignment.LEFT)
        doc.add(p)
        doc.add(Paragraph("\n").setFontSize(8f))
    }

    private fun createProductTable(
        doc: Document, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit
    ) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 15f, 20f, 25f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        table.addCell(createCell("Désignation", boldFont, 11f, TextAlignment.LEFT, true))
        table.addCell(createCell("Qté", boldFont, 11f, TextAlignment.CENTER, true))
        table.addCell(createCell("P.U", boldFont, 11f, TextAlignment.CENTER, true))
        table.addCell(createCell("Montant", boldFont, 11f, TextAlignment.RIGHT, true))

        var total = 0.0
        operations.groupBy { it.parent_M1Produit_KeyId }.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find { it.keyID == ops.first().parentM13TarificationKeyID }
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val price = tarification?.prixCurrency ?: 0.0
            val subtotal = price * qty

            if (subtotal != 0.0) {
                val qtyDisplay = formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1)
                table.addCell(createCell(produit?.nom ?: "Produit", regularFont, 10f, TextAlignment.LEFT, true))
                table.addCell(createCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER, true))
                table.addCell(createCell("${round(price)}", regularFont, 10f, TextAlignment.CENTER, true))
                table.addCell(createCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT, true))
                total += subtotal
            }
        }

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(10f))
        addText(doc, "Total", boldFont, 14f, TextAlignment.CENTER)
        addText(doc, "${round(total)}Da", boldFont, 16f, TextAlignment.CENTER)
    }

    private fun createCell(content: String, font: PdfFont, size: Float, align: TextAlignment, bordered: Boolean = false): Cell {
        val cell = Cell().add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
        if (bordered) cell.setBorder(SolidBorder(1f)).setPadding(4f) else cell.setPadding(2f)
        return cell
    }

    private fun addText(doc: Document, text: String, font: PdfFont, size: Float, align: TextAlignment) =
        doc.add(Paragraph(text).setFont(font).setFontSize(size).setTextAlignment(align))

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
