// Fixed PrintReceiptHandler_Juil.kt with proper property declarations
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

/**
 * Refactored PrintReceiptHandler using composition pattern
 * Addresses TODO(2): Split class into smaller partitioned sub-classes
 */
class PrintReceiptHandler_Juil(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler,
) {
    // FIX: Declare the partitioned handlers as class properties
    private val bluetoothPrintHandler = BluetoothPrintHandler()
    private val pdfPrintHandler = PdfPrintHandler(printInPdfHandler)
    private val fileOperationsHandler = FileOperationsHandler()

    /**
     * Print receipt with both Bluetooth and PDF (legacy method for backward compatibility)
     */
    fun printVentReceiptWithDirectPdf(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        generatePdf: Boolean = false,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = true,
        versement: Double = 0.0
    ) {
        val shouldShowCreditSection = showCreditSection && bonVent != null

        // Try Bluetooth printing first
        val bluetoothSuccess = bluetoothPrintHandler.printBluetoothReceipt(
            context,
            client,
            relative_ListM10OperationVentCouleur,
            repo13TarificationInfos,
            repoM1Produit,
            bonVent,
            shouldShowCreditSection,
            versement
        )

        // Generate PDF if requested or if Bluetooth failed
        if (generatePdf || !bluetoothSuccess) {
            scope?.launch {
                try {
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
                }
            }
        }
    }

    /**
     * Print via Bluetooth only - addresses TODO(1)
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
     * Generate PDF only - addresses TODO(1)
     */
    fun printPdfOnly(
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
            }
        }
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
     * Print credit via Bluetooth only
     */
    fun printCreditBluetoothOnly(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ) {
        bluetoothPrintHandler.printCreditBluetoothReceipt(
            context,
            client,
            bonVent,
            previousPayments,
            showPaymentHistory
        )
    }

    /**
     * Generate credit PDF only
     */
    fun printCreditPdfOnly(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ) {
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
