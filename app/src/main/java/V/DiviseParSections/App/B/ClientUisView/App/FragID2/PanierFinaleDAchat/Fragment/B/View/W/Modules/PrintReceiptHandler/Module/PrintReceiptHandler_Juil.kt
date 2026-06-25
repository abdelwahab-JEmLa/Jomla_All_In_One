package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * FIXED: Added shouldOpenFile parameter to control when PDFs are opened
 */
class PrintReceiptHandler_Juil(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler,
) {
    private val bluetoothPrintHandler = BluetoothPrintHandler()
    private val pdfPrintHandler = PdfPrintHandler(printInPdfHandler)
    private val windowsShareHandler = WindowsShareHandler()

    /**
     * Print via Bluetooth only
     * FIXED: Now checks demande_Versemet_si_Type_est_regle
     */
    fun printBluetoothOnly(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = true,
        versement: Double = 0.0 ,
        companyHeader: String = "Jomla.com",
        printWithoutProducts: Boolean = false
    ) {
        // FIXED: Use demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
        val shouldShowCreditSection = (showCreditSection && bonVent != null) ||
                (bonVent?.demande_Versemet_si_Type_est_regle == true)

        bluetoothPrintHandler.printBluetoothReceipt(
            context,
            client,
            relative_ListM10OperationVentCouleur,
            repo13TarificationInfos,
            repoM1Produit,
            bonVent,
            shouldShowCreditSection,
            versement,
            companyHeader,
            printWithoutProducts
        )
    }

    /**
     * Generate PDF only - Returns Result for proper error handling
     * FIXED: Now checks demande_Versemet_si_Type_est_regle
     * FIXED: Added shouldOpenFile parameter to control when PDF is opened
     *
     * @param shouldOpenFile If false, PDF will be generated but not opened automatically.
     *                       This is useful when the file will be copied to a different location
     *                       before opening (e.g., in background PDF creation).
     *                       Default is true for backward compatibility.
     */
    suspend fun printPdfOnly(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: List<M13TarificationInfos>,
        relative_bonVent: M8BonVent? = null,
        showCreditSection: Boolean = true,
        versement: Double = 0.0,
        shouldOpenFile: Boolean = true
    ): Result<String> {
        return try {
            // FIXED: The pdfPrintHandler will now automatically check demande_Versemet_si_Type_est_regle
            // No need to override showCreditSection here since the handler checks the bonVent property
            // FIXED: Pass shouldOpenFile to control when PDF is opened
            pdfPrintHandler.generateAndOpenPdf(
                context,
                client,
                relative_ListM10OperationVentCouleur,
                repo13TarificationInfos,
                repoM1Produit,
                relative_bonVent,
                showCreditSection,
                versement,
                shouldOpenFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * NEW: Direct share existing PDF file with Windows apps
     */
    fun sharePdfWithWindowsApps(context: Context, pdfFile: File) {
        windowsShareHandler.shareWithWindowsApps(context, pdfFile)
    }

    /**
     * Print credit receipt (both Bluetooth and PDF)
     * FIXED: Added shouldOpenFile parameter
     */
    fun print_Credit(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        generatePdf: Boolean = false,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false,
        shouldOpenFile: Boolean = true
    ) {
        // Try Bluetooth printing first
        val bluetoothSuccess = bluetoothPrintHandler.printCreditBluetoothReceipt(
            context,
            client,
            bonVent,
            previousPayments,
            showPaymentHistory
        )

        // Generate PDF if requested or if Bluetooth failed
        if (generatePdf || !bluetoothSuccess) {
            scope?.launch {
                try {
                    pdfPrintHandler.generateCreditPdf(
                        context,
                        client,
                        bonVent,
                        previousPayments,
                        showPaymentHistory,
                        shouldOpenFile
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * NEW: Credit receipt with Windows app sharing option
     * FIXED: Added shouldOpenFile parameter
     */
    fun print_CreditWithWindowsShare(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        shareWithWindows: Boolean = false,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false,
        shouldOpenFile: Boolean = true
    ) {
        scope?.launch {
            try {
                val result = pdfPrintHandler.generateCreditPdf(
                    context,
                    client,
                    bonVent,
                    previousPayments,
                    showPaymentHistory,
                    shouldOpenFile
                )

                if (shareWithWindows) {
                    result.onSuccess { message ->
                        val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                        val pdfFile = File(filePath)
                        if (pdfFile.exists()) {
                            windowsShareHandler.shareWithWindowsApps(context, pdfFile)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * NEW: Public accessor to PdfPrintHandler_ProMai for advanced use cases
     * Allows direct access to openPdfFile() method for opening PDFs after custom processing
     */
    fun getPdfPrintHandler(): PdfPrintHandler = pdfPrintHandler
}
