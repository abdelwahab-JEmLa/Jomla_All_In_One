// Updated PrintReceiptHandler_Juil.kt with Windows app sharing functionality
package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class PrintReceiptHandler_Juil(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler,
) {
    // Declare the partitioned handlers as class properties
    private val bluetoothPrintHandler = BluetoothPrintHandler()
    private val pdfPrintHandler = PdfPrintHandler(printInPdfHandler)
    private val windowsShareHandler = WindowsShareHandler()

    /**
     * Print via Bluetooth only
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
        versement: Double = 0.0
    ) {
        val shouldShowCreditSection = showCreditSection && bonVent != null

        bluetoothPrintHandler.printBluetoothReceipt(
            context,
            client,
            relative_ListM10OperationVentCouleur,
            repo13TarificationInfos,
            repoM1Produit,
            bonVent,
            shouldShowCreditSection,
            versement
        )
    }

    /**
     * Generate PDF only - Returns Result for proper error handling
     */
    suspend fun printPdfOnly(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = true,
        versement: Double = 0.0
    ): Result<String> {
        return try {
            val shouldShowCreditSection = showCreditSection && bonVent != null

            pdfPrintHandler.generateAndOpenPdf(
                context,
                client,
                relative_ListM10OperationVentCouleur,
                repo13TarificationInfos,
                repoM1Produit,
                bonVent,
                shouldShowCreditSection,
                versement
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * NEW: Share PDF with Windows applications - Addresses TODO(1)
     * Creates PDF and directly shares it with Windows-compatible applications
     */
    fun sharePdfWithWindowsApps(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = true,
        versement: Double = 0.0
    ) {
        scope?.launch {
            try {
                val shouldShowCreditSection = showCreditSection && bonVent != null

                val result = pdfPrintHandler.generateAndOpenPdf(
                    context,
                    client,
                    relative_ListM10OperationVentCouleur,
                    repo13TarificationInfos,
                    repoM1Produit,
                    bonVent,
                    shouldShowCreditSection,
                    versement
                )

                result.onSuccess { message ->
                    val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                    val pdfFile = File(filePath)
                    if (pdfFile.exists()) {
                        windowsShareHandler.shareWithWindowsApps(context, pdfFile)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
     */
    fun print_Credit(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        generatePdf: Boolean = false,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
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
                        showPaymentHistory
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * NEW: Credit receipt with Windows app sharing option
     */
    fun print_CreditWithWindowsShare(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        shareWithWindows: Boolean = false,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ) {
        scope?.launch {
            try {
                val result = pdfPrintHandler.generateCreditPdf(
                    context,
                    client,
                    bonVent,
                    previousPayments,
                    showPaymentHistory
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
}
