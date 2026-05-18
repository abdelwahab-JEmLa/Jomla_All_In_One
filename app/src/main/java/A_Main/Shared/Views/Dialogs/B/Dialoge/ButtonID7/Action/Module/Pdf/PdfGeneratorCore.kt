package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf

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
 * FIXED: Enhanced logging to debug image issues
 */
class PdfGeneratorCore(
    private val formatter: PdfFormatterUtils,
    private val contentBuilder: PdfContentBuilder,
    private val tableBuilder: PdfTableBuilder
) {

    fun generateUnifiedPdf(path: String, params: PdfGenerationParams) {
        val logTag = "PDF_GENERATOR_CORE"

        android.util.Log.d(logTag, "════════════════════════════════════════")
        android.util.Log.d(logTag, "📄 Generating unified PDF")
        android.util.Log.d(logTag, "   Path: $path")
        android.util.Log.d(logTag, "   Type: ${params.type}")
        android.util.Log.d(logTag, "   Client: ${params.client?.nom ?: "null"}")
        android.util.Log.d(logTag, "   Operations: ${params.operations.size}")
        android.util.Log.d(logTag, "   BonVent KeyID: ${params.bonVent?.keyID}")
        android.util.Log.d(logTag, "   relative_bonVent KeyID: ${params.relative_bonVent?.keyID}")
        android.util.Log.d(logTag, "════════════════════════════════════════")

        val regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        PdfWriter(path).use { writer ->
            android.util.Log.d(logTag, "✅ PdfWriter created")

            PdfDocument(writer).use { pdfDoc ->
                android.util.Log.d(logTag, "✅ PdfDocument created")

                Document(pdfDoc, PageSize.A5).use { doc ->
                    android.util.Log.d(logTag, "✅ Document created with A5 size")

                    var currentReceiptTotal = 0.0

                    // Add header based on PDF type
                    addHeaderBasedOnType(doc, params, regularFont, boldFont)

                    // Add client and date
                    contentBuilder.addClientDate(doc, params.client?.nom ?: "Client", regularFont)

                    // Add transaction ID for credit with payment history
                    if (params.type == PdfType.CREDIT_ONLY && params.creditData?.showPaymentHistory == true) {
                        contentBuilder.addText(
                            doc,
                            "Transaction: #${params.transactionId}",
                            regularFont,
                            10f,
                            TextAlignment.LEFT
                        )
                        doc.add(Paragraph("\n").setFontSize(0.3f))
                    }

                    // Add product table if needed
                    if (params.type != PdfType.CREDIT_ONLY) {
                        android.util.Log.d(logTag, "➡️ Adding product table...")
                        android.util.Log.d(logTag, "   Passing relative_bonVent: ${params.relative_bonVent?.keyID}")

                        currentReceiptTotal = addProductTableIfNeeded(
                            doc,
                            params,
                            regularFont,
                            boldFont
                        )

                        android.util.Log.d(logTag, "✅ Product table added, total: $currentReceiptTotal")
                    }

                    addCreditSectionsIfNeeded(
                        doc,
                        params,
                        regularFont,
                        boldFont,
                        currentReceiptTotal
                    )

                    android.util.Log.d(logTag, "════════════════════════════════════════")
                    android.util.Log.d(logTag, "✅ PDF generation complete")
                    android.util.Log.d(logTag, "════════════════════════════════════════")
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
                contentBuilder.addHeader(
                    doc,
                    receiptType,
                    regularFont,
                    boldFont,
                    params.bonVent?.keyID
                )
            }
        }
    }

    /**
     * FIXED: Now checks demande_Versemet_si_Type_est_regle to determine if total should be shown
     * ADDED: Enhanced logging
     */
    private fun addProductTableIfNeeded(
        doc: Document,
        params: PdfGenerationParams,
        regularFont: com.itextpdf.kernel.font.PdfFont,
        boldFont: com.itextpdf.kernel.font.PdfFont
    ): Double {
        val logTag = "PDF_PRODUCT_TABLE"
        var currentReceiptTotal = 0.0

        params.tarificationRepo?.let { tarificationRepo ->
            params.produitRepo?.let { produitRepo ->

                android.util.Log.d(logTag, "🏗️ Building product table...")
                android.util.Log.d(logTag, "   relative_bonVent: ${params.relative_bonVent?.keyID}")

                // CORE FIX: Use demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
                val shouldShowCreditSection = params.bonVent?.demande_Versemet_si_Type_est_regle == true
                val hasVersement = params.versement > 0.0
                val hasActualCredit = shouldShowCreditSection || hasVersement

                val shouldShowTotalSection = params.type == PdfType.RECEIPT_ONLY ||
                        (params.type == PdfType.RECEIPT_WITH_CREDIT && !hasActualCredit)

                android.util.Log.d(logTag, "   shouldShowCreditSection: $shouldShowCreditSection")
                android.util.Log.d(logTag, "   hasVersement: $hasVersement")
                android.util.Log.d(logTag, "   shouldShowTotalSection: $shouldShowTotalSection")

                if (shouldShowTotalSection) {
                    // Show table WITH "Total" section centered
                    android.util.Log.d(logTag, "   ➡️ Creating table WITH total section")
                    tableBuilder.createProductTable(
                        doc,
                        params.operations,
                        tarificationRepo,
                        produitRepo,
                        regularFont,
                        boldFont,
                        params.relative_bonVent
                    )
                } else {
                    // Show table WITHOUT total section, return total for credit calculations
                    android.util.Log.d(logTag, "   ➡️ Creating table WITHOUT total section")
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
            contentBuilder.addText(
                doc,
                "Credit Du Compte actuel",
                regularFont,
                12f,
                TextAlignment.CENTER
            )
            contentBuilder.addText(
                doc,
                "${formatter.round(credit)}Da",
                regularFont,
                14f,
                TextAlignment.CENTER
            )
        }
    }

    /**
     * FIXED: Uses demande_Versemet_si_Type_est_regle to show credit section
     */
    private fun addCreditSectionsIfNeeded(
        doc: Document,
        params: PdfGenerationParams,
        regularFont: com.itextpdf.kernel.font.PdfFont,
        boldFont: com.itextpdf.kernel.font.PdfFont,
        currentReceiptTotal: Double
    ) {
        if (params.type == PdfType.RECEIPT_WITH_CREDIT || params.type == PdfType.CREDIT_ONLY) {

            // CORE FIX: Check demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
            val shouldShowCreditSection = params.bonVent?.demande_Versemet_si_Type_est_regle == true
            val hasVersement = params.versement > 0.0
            val hasActualCredit = shouldShowCreditSection || hasVersement

            // Only show credit section if demande_Versemet_si_Type_est_regle is true or there's payment
            if (hasActualCredit || params.type == PdfType.CREDIT_ONLY) {

                if (params.type == PdfType.RECEIPT_WITH_CREDIT) {
                    // Add separator before credit section
                    doc.add(Paragraph("\n").setFontSize(0.3f))
                    contentBuilder.addText(
                        doc,
                        "────────────────────────",
                        regularFont,
                        10f,
                        TextAlignment.CENTER
                    )
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
            // If demande_Versemet_si_Type_est_regle is false, the total was already shown in the product table
        }
    }

    /**
     * Helper method to determine if credit section should be shown
     * FIXED: Uses demande_Versemet_si_Type_est_regle
     */
    private fun shouldShowCreditSection(params: PdfGenerationParams): Boolean {
        if (params.type == PdfType.CREDIT_ONLY) return true
        if (params.type != PdfType.RECEIPT_WITH_CREDIT) return false

        val shouldShowCreditSection = params.bonVent?.demande_Versemet_si_Type_est_regle == true
        val hasVersement = params.versement > 0.0

        return shouldShowCreditSection || hasVersement
    }
}
