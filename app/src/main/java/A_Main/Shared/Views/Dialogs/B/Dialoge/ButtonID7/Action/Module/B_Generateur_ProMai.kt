package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module

import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Datas
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.PdfContentBuilder_Mai
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.PdfFormatterUtils_Mai
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.PdfGenerationParams_Mai
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.PdfGeneratorCore_Mai
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.PdfTableBuilder_Mai
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.PdfType
import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf.UploadHandler_Mai
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.CreditReceiptData
import android.content.Context

/**
 * Refactored PDF handler using composition pattern
 * Each component has a single responsibility
 *
 * FIXED: Now uses demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
 */
class B_Generateur_ProMai(
    val repositorysMainGetter: Datas,
    val uploadHandler: UploadHandler_Mai,
) {

    // Initialize utility classes
    private val formatter = PdfFormatterUtils_Mai(repositorysMainGetter)
    private val contentBuilder = PdfContentBuilder_Mai(formatter)
    private val tableBuilder = PdfTableBuilder_Mai(formatter, contentBuilder,repositorysMainGetter)
    private val pdfGenerator = PdfGeneratorCore_Mai(formatter, contentBuilder, tableBuilder)

    /**
     * Share a generated PDF document
     */

    /**
     * Generate a unified PDF based on parameters
     */
    private fun generatePdfWithParams(path: String, params: PdfGenerationParams_Mai) {
        pdfGenerator.generateUnifiedPdf(path, params)
    }

    /**
     * Generate a receipt PDF for sales operations
     *
     * FIXED: Now checks if bonVent has demande_Versemet_si_Type_est_regle = true
     * to determine if credit section should be shown
     */
    fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: List<M13TarificationInfos>,
        produitRepo: List<M01Produit>?,
        transactionId: String = "",
        its_GrossistApp: Boolean = true,
        relative_bonVent: M8BonVent? = null
    ): Result<String> {
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to print"))
        }

        // FIXED: Check demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
        val shouldShowCreditSection = relative_bonVent?.demande_Versemet_si_Type_est_regle == true

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)

        val params = if (shouldShowCreditSection && relative_bonVent != null) {
            // Generate receipt WITH credit section
            PdfGenerationParams_Mai(
                type = PdfType.RECEIPT_WITH_CREDIT,
                client = client,
                operations = operations,
                tarificationRepo = tarificationRepo,
                produitRepo = produitRepo,
                bonVent = relative_bonVent,
                versement = relative_bonVent.versement_fait,
                transactionId = transactionId,
                its_GrossistApp = its_GrossistApp,
                relative_bonVent = relative_bonVent
            )
        } else {
            // Generate receipt WITHOUT credit section
            PdfGenerationParams_Mai(
                type = PdfType.RECEIPT_ONLY,
                client = client,
                operations = operations,
                tarificationRepo = tarificationRepo,
                produitRepo = produitRepo,
                transactionId = transactionId,
                its_GrossistApp = its_GrossistApp,
                relative_bonVent = relative_bonVent,
            )
        }

        generatePdfWithParams(file.absolutePath, params)

        if (!file.exists()) {
            return Result.failure(IllegalStateException("PDF file creation failed"))
        }

        return Result.success("PDF saved: ${file.absolutePath}")
    }

    /**
     * Generate a receipt PDF with credit information
     */
    fun generateVentReceiptWithCreditPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: List<M13TarificationInfos>,
        produitRepo: List<M01Produit>?,
        transactionId: String = "",
        relative_bonVent: M8BonVent,
        versement: Double,
        its_GrossistApp: Boolean = true
    ): Result<String> {
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to print"))
        }

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt_credit", transactionId)
        val params = PdfGenerationParams_Mai(
            type = PdfType.RECEIPT_WITH_CREDIT,
            client = client,
            operations = operations,
            tarificationRepo = tarificationRepo,
            produitRepo = produitRepo,
            bonVent = relative_bonVent,
            versement = versement,
            transactionId = transactionId,
            its_GrossistApp = its_GrossistApp,
            relative_bonVent = relative_bonVent
        )

        generatePdfWithParams(file.absolutePath, params)

        if (!file.exists()) {
            return Result.failure(IllegalStateException("PDF file creation failed"))
        }

        return Result.success("PDF saved: ${file.absolutePath}")
    }

    /**
     * Generate a credit receipt PDF
     */
    fun generateCreditReceiptPdf(
        context: Context,
        data: CreditReceiptData
    ): Result<String> {
        if (data.totalAmount <= 0) {
            return Result.failure(IllegalArgumentException("Invalid total amount"))
        }

        val file = uploadHandler.createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)

        if (!file.exists()) {
            return Result.failure(IllegalStateException("PDF file creation failed"))
        }

        return Result.success("PDF saved: ${file.absolutePath}")
    }
}
