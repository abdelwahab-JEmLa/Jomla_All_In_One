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
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont, boldFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        if (result.total > 0.0) {
            val shouldShowCreditSection = relativeBonvent?.demande_Versemet_si_Type_est_regle == true

            if (shouldShowCreditSection && relativeBonvent != null) {
                addCreditSectionLikeCompose(doc, relativeBonvent, result.total, result.itemCount, regularFont, boldFont)
            } else {
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
        // Total Bon Cette Fois with item count - LEFT aligned
        val totalTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        val totalLabelCell = Cell()
            .add(Paragraph(Titres.A.text)
                .setFont(regularFont)
                .setFontSize(12f)  // Increased from 11f
                .setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        val totalValueCell = Cell()
            .add(Paragraph("${formatter.round(totalBon)} Da ($itemCount items)")
                .setFont(boldFont)
                .setFontSize(12f)  // Increased from 11f
                .setTextAlignment(TextAlignment.RIGHT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        totalTable.addCell(totalLabelCell).addCell(totalValueCell)
        doc.add(totalTable)
        doc.add(Paragraph("\n").setFontSize(0.3f))

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
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        ancienCreditTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(creditValue)} Da")
                    .setFont(regularFont)
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        doc.add(ancienCreditTable)
        doc.add(Paragraph("\n").setFontSize(0.2f))

        // Crédit Après Current Vent - LEFT aligned
        val creditApresVent = creditValue + totalBon
        val creditApresTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        creditApresTable.addCell(
            Cell()
                .add(Paragraph(Titres.C.text)
                    .setFont(regularFont)
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        creditApresTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(creditApresVent)} Da")
                    .setFont(regularFont)
                    .setFontSize(11f)
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
                    .setFontSize(12f)  // Increased from 11f
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        versementTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(bonVent.versement_fait)} Da")
                    .setFont(boldFont)
                    .setFontSize(12f)  // Increased from 11f
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
                    .setFontSize(12f)  // Increased from 11f
                    .setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(0f)
        )
        nouveauCompteTable.addCell(
            Cell()
                .add(Paragraph("${formatter.round(bonVent.new_credit_apre_tout_fait)} Da")
                    .setFont(boldFont)
                    .setFontSize(12f)  // Increased from 11f
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
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont, boldFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        return result.total
    }

    private fun addTableHeaders(table: Table, boldFont: PdfFont) {
        // Increased header font size from 11f to 13f
        table.addCell(createHeaderCell("Qté", boldFont, 13f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("P.U", boldFont, 13f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Désignation", boldFont, 13f, TextAlignment.LEFT))
        table.addCell(createHeaderCell("Sous-total", boldFont, 13f, TextAlignment.RIGHT))
    }

    private fun addTableRows(
        table: Table,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont
    ): TableResult {
        var total = 0.0
        var itemCount = 0
        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }

        val sortedGroupedOps = groupedOps.entries.sortedBy { (produitId, _) ->
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            formatter.cleanAndCapitalizeProductName(produit?.nom ?: "")
        }

        sortedGroupedOps.forEach { (produitId, ops) ->
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

                // BOLD font for quantity
                table.addCell(createDataCell(qtyDisplay, boldFont, 11f, TextAlignment.CENTER))

                // Regular font for unit price - NOT bold
                table.addCell(
                    createDataCell(
                        "${formatter.round(unitPrice)}",
                        regularFont,  // Keep regularFont
                        11f,          // Keep 11f
                        TextAlignment.CENTER
                    )
                )

                // FIXED: Designation size 13f and bold
                table.addCell(
                    createDataCell(
                        productNameWithCategory,
                        boldFont,
                        13f,  // Changed to 13f
                        TextAlignment.LEFT
                    )
                )

                // Regular font for subtotal - NOT bold
                table.addCell(
                    createDataCell(
                        "${formatter.round(subtotal)}",
                        regularFont,  // Keep regularFont
                        11f,          // Keep 11f
                        TextAlignment.RIGHT
                    )
                )

                total += subtotal
                itemCount++
            } else {
                table.addCell(createDataCell(qtyDisplay, boldFont, 11f, TextAlignment.CENTER))
                table.addCell(
                    createDataCell(
                        "",
                        regularFont,
                        11f,
                        TextAlignment.CENTER
                    )
                )
                table.addCell(
                    createDataCell(
                        productNameWithCategory,
                        boldFont,
                        13f,  // Changed to 13f
                        TextAlignment.LEFT
                    )
                )
                table.addCell(
                    createDataCell(
                        "",
                        regularFont,
                        11f,
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
            .setPadding(5f)  // Increased from 4f to 5f for more spacing
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(align)
    }

    private fun createDataCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell()
            .add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.5f))  // Increased from 0.1f to 0.5f for thicker lines
            .setPadding(5f)  // Increased from 4f to 5f for more spacing
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)

    private data class TableResult(val total: Double, val itemCount: Int)
}
