package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue

/**
 * Handles PDF table creation for products
 * FIXED: Now uses demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
 */
class PdfTableBuilder(
    private val formatter: PdfFormatterUtils,
    private val contentBuilder: PdfContentBuilder
) {
    fun createProductTable(
        doc: Document,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont,
        relativeBonvent: M8BonVent?
    ) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 20f, 45f, 20f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont)
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        if (result.total > 0.0) {
            // FIXED: Check demande_Versemet_si_Type_est_regle instead of affiche_le_verssement_au_prochen_print
            val shouldShowCreditSection = relativeBonvent?.demande_Versemet_si_Type_est_regle == true

            if (shouldShowCreditSection && relativeBonvent != null) {
                // Display credit information (includes total inside)
                addCreditSectionLikeCompose(doc, relativeBonvent, result.total, result.itemCount, regularFont, boldFont)
            } else {
                // Display normal total with item count ONLY when demande_Versemet_si_Type_est_regle is false
                contentBuilder.addTotalWithItemCount(doc, result.total, result.itemCount, boldFont)
            }
        }
    }

    enum class Titres(val text: String) {
        A("Total:"),
        B("Ancien Crédit:"),
        C("Nouveau Crédit:"),
        D("Versement:"),
        E("Nouveau Compt Calculé:")
    }

    private fun addCreditSectionLikeCompose(
        doc: Document,
        bonVent: M8BonVent,
        totalBon: Double,
        itemCount: Int,
        regularFont: PdfFont,
        boldFont: PdfFont
    ) {
        // Total Bon Cette Fois with item count - LEFT aligned like in Compose
        val totalTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        val totalLabelCell = Cell()
            .add(Paragraph(Titres.A.text)
                .setFont(regularFont)
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        val totalValueCell = Cell()
            .add(Paragraph("${formatter.round(totalBon)} Da ($itemCount items)")
                .setFont(boldFont)
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.RIGHT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        totalTable.addCell(totalLabelCell).addCell(totalValueCell)
        doc.add(totalTable)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        // FIXED: Display demande_Versemet_si_Type when demande_Versemet_si_Type_est_regle is true
        // Otherwise display ancien_credit
        val creditValue = if (bonVent.demande_Versemet_si_Type_est_regle) {
            bonVent.demande_Versemet_si_Type
        } else {
            bonVent.ancien_credit
        }

        // Ancien Crédit - LEFT aligned
        val ancienCreditTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        ancienCreditTable.addCell(
            Cell()
                .add(Paragraph(Titres.B.text)
                    .setFont(regularFont)
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        ancienCreditTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(creditValue)} Da")
                    .setFont(regularFont)
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        doc.add(ancienCreditTable)
        doc.add(Paragraph("\n").setFontSize(0.2f))

        // Crédit Après Current Vent - LEFT aligned
        // FIXED: Calculate using the correct credit value (demande_Versemet_si_Type or ancien_credit)
        val creditApresVent = creditValue + totalBon
        val creditApresTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        creditApresTable.addCell(
            Cell()
                .add(Paragraph(Titres.C.text)
                    .setFont(regularFont)
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        creditApresTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(creditApresVent)} Da")
                    .setFont(regularFont)
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        doc.add(creditApresTable)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        // Versement - LEFT aligned and highlighted
        val versementTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        versementTable.addCell(
            Cell()
                .add(Paragraph(Titres.D.text)
                    .setFont(regularFont)
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        versementTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(bonVent.versement_fait)} Da")
                    .setFont(boldFont)
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        doc.add(versementTable)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        // Separator line
        doc.add(Paragraph("────────────────────────")
            .setFont(regularFont)
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT)
            .setMargin(0f))
        doc.add(Paragraph("\n").setFontSize(0.3f))

        // Nouveau Compte Calculé - LEFT aligned and highlighted
        val nouveauCompteTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        nouveauCompteTable.addCell(
            Cell()
                .add(Paragraph(Titres.E.text)
                    .setFont(boldFont)
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        nouveauCompteTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(bonVent.new_credit_apre_tout_fait)} Da")
                    .setFont(boldFont)
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        doc.add(nouveauCompteTable)
    }

    fun createProductTableAndReturnTotal(
        doc: Document,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont
    ): Double {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 14f, 56f, 15f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont)
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        return result.total
    }

    private fun addTableHeaders(table: Table, boldFont: PdfFont) {
        table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
        table.addCell(createHeaderCell("Sous-total", boldFont, 11f, TextAlignment.RIGHT))
    }

    private fun addTableRows(
        table: Table,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont
    ): TableResult {
        var total = 0.0
        var itemCount = 0
        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }

        groupedOps.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find {
                it.keyID == ops.first().parentM13TarificationKeyID
            }

            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }

            val rawPrice = tarification?.prixCurrency ?: 0.0
            val subtotal = rawPrice * qty
            val shouldDisplayPriceAndSubtotal = rawPrice > 0.0

            val qtyDisplay =
                formatter.formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)

            val productNameWithCategory = formatter.formatProductNameWithCategory(produit)

            if (shouldDisplayPriceAndSubtotal) {
                val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                    val nombreUniteInt = produit.nombreUniteInt
                    if (nombreUniteInt > 0) rawPrice / nombreUniteInt else rawPrice
                } else rawPrice

                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(
                    createDataCell(
                        "${formatter.round(unitPrice)}",
                        regularFont,
                        10f,
                        TextAlignment.CENTER
                    )
                )
                table.addCell(
                    createDataCell(
                        productNameWithCategory,
                        regularFont,
                        10f,
                        TextAlignment.LEFT
                    )
                )
                table.addCell(
                    createDataCell(
                        "${formatter.round(subtotal)}",
                        regularFont,
                        10f,
                        TextAlignment.RIGHT
                    )
                )

                total += subtotal
                itemCount++
            } else {
                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(
                    createDataCell(
                        "",
                        regularFont,
                        10f,
                        TextAlignment.CENTER
                    )
                )
                table.addCell(
                    createDataCell(
                        productNameWithCategory,
                        regularFont,
                        10f,
                        TextAlignment.LEFT
                    )
                )
                table.addCell(
                    createDataCell(
                        "",
                        regularFont,
                        10f,
                        TextAlignment.RIGHT
                    )
                )

                itemCount++
            }
        }

        return TableResult(total, itemCount)
    }

    private fun createHeaderCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell {
        val paragraph = Paragraph(content)
            .setFont(font)
            .setFontSize(size)
            .setTextAlignment(align)
            .setMargin(0f)

        return Cell()
            .add(paragraph)
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(4f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(align)
    }

    private fun createDataCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell()
            .add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.1f))
            .setPadding(4f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)

    private data class TableResult(val total: Double, val itemCount: Int)
}
