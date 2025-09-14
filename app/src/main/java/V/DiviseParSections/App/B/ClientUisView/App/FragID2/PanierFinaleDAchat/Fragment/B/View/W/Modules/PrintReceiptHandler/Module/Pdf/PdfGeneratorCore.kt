package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PdfTableBuilder
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment

/**
 * Core PDF generation logic
 * FIXED: Now correctly handles cases where ancien solde = 0.0
 */
class PdfGeneratorCore(
    private val formatter: PdfFormatterUtils,
    private val contentBuilder: PdfContentBuilder,
    private val tableBuilder: PdfTableBuilder
) {

    fun generateUnifiedPdf(path: String, params: PdfGenerationParams) {
        val regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        PdfWriter(path).use { writer ->
            PdfDocument(writer).use { pdfDoc ->
                Document(pdfDoc, PageSize.A5).use { doc ->
                    var currentReceiptTotal = 0.0

                    // Add header based on PDF type
                    addHeaderBasedOnType(doc, params, regularFont, boldFont)

                    // Add client and date
                    contentBuilder.addClientDate(doc, params.client?.nom ?: "Client", regularFont)

                    // Add transaction ID for credit with payment history
                    if (params.type == PdfType.CREDIT_ONLY && params.creditData?.showPaymentHistory == true) {
                        contentBuilder.addText(doc, "Transaction: #${params.transactionId}", regularFont, 10f, TextAlignment.LEFT)
                        doc.add(Paragraph("\n").setFontSize(0.3f))
                    }

                    // Add product table if needed
                    if (params.type != PdfType.CREDIT_ONLY) {
                        currentReceiptTotal = addProductTableIfNeeded(doc, params, regularFont, boldFont)
                    }

                    // Add credit display for receipt only
                    if (params.type == PdfType.RECEIPT_ONLY) {
                        addCreditDisplayForReceiptOnly(doc, params, regularFont)
                    }

                    // Add credit sections only if there's actual credit
                    addCreditSectionsIfNeeded(doc, params, regularFont, boldFont, currentReceiptTotal)
                }
            }
        }
    }

    private fun addHeaderBasedOnType(
        doc: Document,
        params: PdfGenerationParams,
        regularFont: com.itextpdf.kernel.font.PdfFont,
        boldFont: com.itextpdf.kernel.font.PdfFont
    ) {
        when (params.type) {
            PdfType.RECEIPT_ONLY, PdfType.RECEIPT_WITH_CREDIT -> {
                val title = if (!params.its_GrossistApp) "Facture" else ""
                contentBuilder.addHeader(doc, title, regularFont, boldFont, params.bonVent?.keyID)
            }
            PdfType.CREDIT_ONLY -> {
                val receiptType = if (params.creditData?.showPaymentHistory == true) {
                    "Credit Payment Prix_Détaillé"
                } else {
                    "Reçu de Paiement"
                }
                contentBuilder.addHeader(doc, receiptType, regularFont, boldFont, params.bonVent?.keyID)
            }
        }
    }

    /**
     * FIXED: Now checks actual credit balance to determine if total should be shown
     */
    private fun addProductTableIfNeeded(
        doc: Document,
        params: PdfGenerationParams,
        regularFont: com.itextpdf.kernel.font.PdfFont,
        boldFont: com.itextpdf.kernel.font.PdfFont
    ): Double {
        var currentReceiptTotal = 0.0

        params.tarificationRepo?.let { tarificationRepo ->
            params.produitRepo?.let { produitRepo ->

                // CORE FIX: Determine if there's actual credit to display
                val clientCreditBalance = params.client?.currentCreditBalance ?: 0.0
                val hasVersement = params.versement > 0.0
                val hasActualCredit = clientCreditBalance != 0.0 || hasVersement

                // Show total section when:
                // 1. It's RECEIPT_ONLY (no credit expected), OR
                // 2. It's RECEIPT_WITH_CREDIT but there's NO actual credit balance and NO payment
                val shouldShowTotalSection = params.type == PdfType.RECEIPT_ONLY ||
                        (params.type == PdfType.RECEIPT_WITH_CREDIT && !hasActualCredit)

                if (shouldShowTotalSection) {
                    // Show table WITH "Total" section centered
                    tableBuilder.createProductTable(
                        doc, params.operations, tarificationRepo,
                        produitRepo, regularFont, boldFont
                    )
                    // currentReceiptTotal remains 0.0 since total is already displayed
                } else {
                    // Show table WITHOUT total section, return total for credit calculations
                    currentReceiptTotal = tableBuilder.createProductTableAndReturnTotal(
                        doc, params.operations, tarificationRepo,
                        produitRepo, regularFont, boldFont
                    )
                }
            }
        }

        return currentReceiptTotal
    }

    private fun addCreditDisplayForReceiptOnly(
        doc: Document,
        params: PdfGenerationParams,
        regularFont: com.itextpdf.kernel.font.PdfFont
    ) {
        // Only show existing credit if negative (client owes money)
        params.client?.currentCreditBalance?.takeIf { it < 0 }?.let { credit ->
            contentBuilder.addText(doc, "Credit Du Compte actuel", regularFont, 12f, TextAlignment.CENTER)
            contentBuilder.addText(doc, "${formatter.round(credit)}Da", regularFont, 14f, TextAlignment.CENTER)
        }
    }

    /**
     * FIXED: Only shows credit section when there's actual credit to display
     */
    private fun addCreditSectionsIfNeeded(
        doc: Document,
        params: PdfGenerationParams,
        regularFont: com.itextpdf.kernel.font.PdfFont,
        boldFont: com.itextpdf.kernel.font.PdfFont,
        currentReceiptTotal: Double
    ) {
        if (params.type == PdfType.RECEIPT_WITH_CREDIT || params.type == PdfType.CREDIT_ONLY) {

            // CORE FIX: Check if there's actual credit to display
            val clientCreditBalance = params.client?.currentCreditBalance ?: 0.0
            val hasVersement = params.versement > 0.0
            val hasActualCredit = clientCreditBalance != 0.0 || hasVersement

            // Only show credit section if there's actual credit or payment
            if (hasActualCredit || params.type == PdfType.CREDIT_ONLY) {

                if (params.type == PdfType.RECEIPT_WITH_CREDIT) {
                    // Add separator before credit section
                    doc.add(Paragraph("\n").setFontSize(0.3f))
                    contentBuilder.addText(doc, "────────────────────────", regularFont, 10f, TextAlignment.CENTER)
                    doc.add(Paragraph("\n").setFontSize(0.3f))
                }

                when (params.type) {
                    PdfType.RECEIPT_WITH_CREDIT -> {
                        params.bonVent?.let {
                            contentBuilder.addCreditSection(
                                doc, params.client, it, params.versement,
                                regularFont, boldFont, currentReceiptTotal
                            )
                        }
                    }
                    PdfType.CREDIT_ONLY -> {
                        params.creditData?.let {
                            contentBuilder.addCreditOnlySection(doc, it, regularFont, boldFont)
                        }
                    }
                    else -> {}
                }
            }
            // If no actual credit, the total was already shown in the product table
        }
    }

    /**
     * Helper method to determine if credit section should be shown
     */
    private fun shouldShowCreditSection(params: PdfGenerationParams): Boolean {
        if (params.type == PdfType.CREDIT_ONLY) return true
        if (params.type != PdfType.RECEIPT_WITH_CREDIT) return false

        val clientCreditBalance = params.client?.currentCreditBalance ?: 0.0
        val hasVersement = params.versement > 0.0

        return clientCreditBalance != 0.0 || hasVersement
    }
}
