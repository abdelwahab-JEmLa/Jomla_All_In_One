package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.CreditReceiptData
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * FIXED: Always uses FileProvider URIs to avoid "file exposed beyond app" errors
 * FIXED: Added shouldOpenFile parameter to control when PDF is opened
 */
class PdfPrintHandler(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler
) {
    companion object {
        private const val TAG = "PdfPrintHandler"
    }

    /**
     * FIXED: Now uses demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
     * FIXED: Added shouldOpenFile parameter to prevent opening temp files before they're copied
     *
     * @param shouldOpenFile If false, PDF will be generated but not opened.
     *                       This is useful when the file needs to be copied to a different location first.
     *                       Default is true for backward compatibility.
     */
    suspend fun generateAndOpenPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        relative_bonVent: M8BonVent? = null,
        showCreditSection: Boolean = false,
        versement: Double = 0.0,
        shouldOpenFile: Boolean = true
    ): Result<String> {
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to print"))
        }

        return try {
            val transactionId = "vent_${System.currentTimeMillis().toString().takeLast(4)}"

            // FIXED: Use demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
            val shouldShowCredit = (showCreditSection && relative_bonVent != null) ||
                    (relative_bonVent?.demande_Versemet_si_Type_est_regle == true)

            val result = if (shouldShowCredit && relative_bonVent != null) {
                printInPdfHandler.generateVentReceiptWithCreditPdf(
                    context,
                    client,
                    operations,
                    repo13TarificationInfos,
                    repoM1Produit,
                    transactionId,
                    relative_bonVent,
                    versement
                )

            } else {
                printInPdfHandler.generateVentReceiptPdf(
                    context,
                    client,
                    operations,
                    repo13TarificationInfos,
                    repoM1Produit,
                    transactionId,
                    relative_bonVent = relative_bonVent
                )
            }

            result.onSuccess { message ->
                val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                val pdfFile = File(filePath)

                if (pdfFile.exists()) {
                    savePdfToDownloads(context, pdfFile)

                    // FIXED: Only open file if requested
                    if (shouldOpenFile) {
                        openPdfFile(context, pdfFile)
                        Log.d(TAG, "PDF opened from temp location: ${pdfFile.absolutePath}")
                    } else {
                        Log.d(TAG, "PDF generation complete, skipping auto-open: ${pdfFile.absolutePath}")
                    }
                }
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * FIXED: Added shouldOpenFile parameter
     */
    suspend fun generateCreditPdf(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false,
        shouldOpenFile: Boolean = true
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

                    // FIXED: Only open file if requested
                    if (shouldOpenFile) {
                        openPdfFile(context, pdfFile)
                        Log.d(TAG, "Credit PDF opened: ${pdfFile.absolutePath}")
                    } else {
                        Log.d(TAG, "Credit PDF generation complete, skipping auto-open: ${pdfFile.absolutePath}")
                    }
                }
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * FIXED: Always uses FileProvider URI, never exposes file:// URIs
     * This prevents "file exposed beyond app through Intent.getData()" errors
     *
     * PUBLIC: Made public so it can be called from createPdfInBackground after copying to final location
     */
    fun openPdfFile(context: Context, file: File) {
        try {
            Log.d(TAG, "Opening PDF file: ${file.absolutePath}")

            if (!file.exists()) {
                Log.e(TAG, "Cannot open PDF - file does not exist: ${file.absolutePath}")
                return
            }

            // Create FileProvider URI
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Log.d(TAG, "FileProvider URI created: $uri")

            // Try to open with PDF viewer
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if there's an app that can handle PDFs
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Log.i(TAG, "PDF opened successfully")
            } else {
                // No PDF viewer found - try with generic viewer
                // IMPORTANT: Still use FileProvider URI, not Uri.fromFile()
                val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "*/*")  // ✅ Still using content:// URI
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }

                try {
                    context.startActivity(genericIntent)
                    Log.i(TAG, "PDF opened with generic viewer")
                } catch (e: Exception) {
                    Log.e(TAG, "No app available to open PDF", e)
                    // Could show a Toast here to inform the user
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error opening PDF file: ${file.absolutePath}", e)
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
            Log.i(TAG, "PDF saved to downloads: ${destFile.absolutePath}")
            destFile
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to downloads", e)
            null
        }
    }
}
