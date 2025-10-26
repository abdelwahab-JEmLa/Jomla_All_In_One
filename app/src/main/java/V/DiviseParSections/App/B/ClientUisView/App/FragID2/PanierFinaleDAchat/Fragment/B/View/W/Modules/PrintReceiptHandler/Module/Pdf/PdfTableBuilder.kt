package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
 * FIXED:
 * - Removed N° column, added item count display with total aligned left
 * - Added category type name display in parentheses when available
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
        boldFont: PdfFont
    ) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 20f, 45f, 20f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont)
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        if (result.total > 0.0) {
            contentBuilder.addTotalWithItemCount(doc, result.total, result.itemCount, boldFont)
        }
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

            // FIXED: Use formatProductNameWithCategory which includes category type name
            val productNameWithCategory = formatter.formatProductNameWithCategory(produit)

            if (shouldDisplayPriceAndSubtotal) {
                // Calculate unit price if needed
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
                // When tariff is null or laisse_Au_Gerant is true, show product and quantity but hide price
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
