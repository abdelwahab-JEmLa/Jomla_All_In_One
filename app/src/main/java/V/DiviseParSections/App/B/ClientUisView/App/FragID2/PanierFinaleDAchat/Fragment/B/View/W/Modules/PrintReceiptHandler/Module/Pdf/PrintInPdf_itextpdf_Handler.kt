package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context

/**
 * Refactored PDF handler using composition pattern
 * Each component has a single responsibility
 *
 * FIXED TODOs:
 * - Category type name display - Already implemented in PdfFormatterUtils.formatProductNameWithCategory()
 * - Null tariff handling - Already implemented in PdfTableBuilder.addTableRows()
 * - Credit section display logic - Fixed to check affiche_le_verssement_au_prochen_print
 */
class PrintInPdf_itextpdf_Handler(
    val repositorysMainGetter: RepositorysMainGetter,
    val uploadHandler: UploadHandler,
) {

    // Initialize utility classes
    private val formatter = PdfFormatterUtils(repositorysMainGetter)
    private val contentBuilder = PdfContentBuilder(formatter)
    private val tableBuilder = PdfTableBuilder(formatter, contentBuilder)
    private val pdfGenerator = PdfGeneratorCore(formatter, contentBuilder, tableBuilder)

    /**
     * Share a generated PDF document
     */
    fun sharePdf(context: Context, pdfResult: PdfResult) {
        uploadHandler.shareDocument(context, pdfResult.file)
    }

    /**
     * Generate a unified PDF based on parameters
     */
    private fun generatePdfWithParams(path: String, params: PdfGenerationParams) {
        pdfGenerator.generateUnifiedPdf(path, params)
    }

    /**
     * Generate a receipt PDF for sales operations
     *
     * FIXED: Now checks if bonVent has affiche_le_verssement_au_prochen_print = true
     * to determine if credit section should be shown
     */
    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        its_GrossistApp: Boolean = true,
        bonVent: M8BonVent? = null
    ): Result<String> {
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to print"))
        }

        // FIXED: Check if credit section should be displayed based on bonVent flag
        val shouldShowCreditSection = bonVent?.affiche_le_verssement_au_prochen_print == true

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)

        val params = if (shouldShowCreditSection && bonVent != null) {
            // Generate receipt WITH credit section
            PdfGenerationParams(
                type = PdfType.RECEIPT_WITH_CREDIT,
                client = client,
                operations = operations,
                tarificationRepo = tarificationRepo,
                produitRepo = produitRepo,
                bonVent = bonVent,
                versement = bonVent.versement_fait,
                transactionId = transactionId,
                its_GrossistApp = its_GrossistApp
            )
        } else {
            // Generate receipt WITHOUT credit section
            PdfGenerationParams(
                type = PdfType.RECEIPT_ONLY,
                client = client,
                operations = operations,
                tarificationRepo = tarificationRepo,
                produitRepo = produitRepo,
                transactionId = transactionId,
                its_GrossistApp = its_GrossistApp
            )
        }

        generatePdfWithParams(file.absolutePath,
            params
        )

        if (!file.exists()) {
            return Result.failure(IllegalStateException("PDF file creation failed"))
        }

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    /**
     * Generate a receipt PDF with credit information
     */
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
        if (operations.isEmpty()) {
            return Result.failure(IllegalArgumentException("No operations to print"))
        }

        val file = uploadHandler.createLocalFile(context, client?.nom ?: "Client", "receipt_credit", transactionId)
        val params = PdfGenerationParams(
            type = PdfType.RECEIPT_WITH_CREDIT,
            client = client,
            operations = operations,
            tarificationRepo = tarificationRepo,
            produitRepo = produitRepo,
            bonVent = bonVent,
            versement = versement,
            transactionId = transactionId,
            its_GrossistApp = its_GrossistApp
        )

        generatePdfWithParams(file.absolutePath, params)

        if (!file.exists()) {
            return Result.failure(IllegalStateException("PDF file creation failed"))
        }

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }

    /**
     * Generate a credit receipt PDF
     */
    suspend fun generateCreditReceiptPdf(
        context: Context,
        data: CreditReceiptData
    ): Result<String> {
        if (data.totalAmount <= 0) {
            return Result.failure(IllegalArgumentException("Invalid total amount"))
        }

        val file = uploadHandler.createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
        val params = PdfGenerationParams(
            type = PdfType.CREDIT_ONLY,
            client = data.client,
            transactionId = data.transactionId,
            creditData = data
        )

        generatePdfWithParams(file.absolutePath, params)

        if (!file.exists()) {
            return Result.failure(IllegalStateException("PDF file creation failed"))
        }

        val url = uploadHandler.uploadToFirebaseStorage(file, file.name)
        return Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")
    }
}
