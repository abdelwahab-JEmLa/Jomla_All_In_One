
// PdfPrintHandler.kt - Handles only PDF generation and management
package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.CreditReceiptData
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class PdfPrintHandler(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler
) {
    suspend fun generateAndOpenPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = false,
        versement: Double = 0.0
    ): Result<String> {
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to print"))
        }

        return try {
            val transactionId = "vent_${System.currentTimeMillis().toString().takeLast(4)}"
            
            val result = if (showCreditSection && bonVent != null) {
                printInPdfHandler.generateVentReceiptWithCreditPdf(
                    context,
                    client,
                    operations,
                    repo13TarificationInfos,
                    repoM1Produit,
                    transactionId,
                    bonVent,
                    versement
                )
            } else {
                printInPdfHandler.generateVentReceiptPdf(
                    context,
                    client,
                    operations,
                    repo13TarificationInfos,
                    repoM1Produit,
                    transactionId
                )
            }

            result.onSuccess { message ->
                val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                val pdfFile = File(filePath)

                if (pdfFile.exists()) {
                    savePdfToDownloads(context, pdfFile)
                    openPdfFile(context, pdfFile)
                }
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateCreditPdf(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ): Result<String> {
        return try {
            val transactionId = bonVent.keyID.takeLast(4)
            
            val creditData = CreditReceiptData(
                client = client,
                totalAmount = bonVent.sum_De_Totale_Vents,
                currentPayment = bonVent.versement,
                previousPayments = previousPayments,
                transactionId = transactionId,
                showPaymentHistory = showPaymentHistory,
                oldBalance = client?.currentCreditBalance ?: 0.0,
                currentBill = bonVent.sum_De_Totale_Vents
            )

            val result = printInPdfHandler.generateCreditReceiptPdf(context, creditData)

            result.onSuccess { message ->
                val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                val pdfFile = File(filePath)

                if (pdfFile.exists()) {
                    savePdfToDownloads(context, pdfFile)
                    openPdfFile(context, pdfFile)
                }
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun openPdfFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val fileIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(file), "*/*")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fileIntent)
        }
    }

    private fun savePdfToDownloads(context: Context, sourceFile: File): File? {
        return try {
            val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                ?: return null

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val destFile = File(downloadsDir, sourceFile.name)
            sourceFile.copyTo(destFile, overwrite = true)
            destFile
        } catch (e: Exception) {
            null
        }
    }
}
