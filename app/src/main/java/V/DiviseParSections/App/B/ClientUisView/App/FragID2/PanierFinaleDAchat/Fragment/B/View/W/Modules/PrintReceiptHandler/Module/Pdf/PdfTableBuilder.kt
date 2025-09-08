package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfContentBuilder
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
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
        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 15f, 20f, 35f, 20f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont)
        val total = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        contentBuilder.addText(doc, "Total", boldFont, 14f, TextAlignment.CENTER)
        contentBuilder.addText(doc, "${formatter.round(total)}Da", boldFont, 16f, TextAlignment.CENTER)
    }

    fun createProductTableAndReturnTotal(
        doc: Document,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont
    ): Double {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(6f, 15f, 14f, 50f, 12f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont)
        val total = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        return total
    }

    private fun addTableHeaders(table: Table, boldFont: PdfFont) {
        table.addCell(createHeaderCell("N°", boldFont, 11f, TextAlignment.CENTER))
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
    ): Double {
        var total = 0.0
        var rowNumber = 1
        val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }

        groupedOps.forEach { (produitId, ops) ->
            val tarification = tarificationRepo.datasValue.find {
                it.keyID == ops.first().parentM13TarificationKeyID
            }
            val produit = produitRepo.datasValue.find { it.keyID == produitId }
            val qty = ops.sumOf { it.quantity }
            val price = tarification?.prixCurrency ?: 0.0
            val subtotal = price * qty

            val shouldDisplayRow = price != 0.0 && subtotal != 0.0

            if (shouldDisplayRow) {
                val qtyDisplay = formatter.formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)
                val productNameWithCategory = formatter.formatProductNameWithCategory(produit)
                val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                    val nombreUniteInt = produit.nombreUniteInt
                    if (nombreUniteInt > 0) price / nombreUniteInt else price
                } else price

                table.addCell(createDataCell(rowNumber.toString(), regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell("${formatter.round(unitPrice)}", regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(productNameWithCategory, regularFont, 10f, TextAlignment.LEFT))
                table.addCell(createDataCell("${formatter.round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))

                total += subtotal
            } else {
                // When price is 0.0 or achat == vent, display row with product and quantity but empty price and subtotal
                val qtyDisplay = formatter.formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)
                val productNameWithCategory = formatter.formatProductNameWithCategory(produit)

                table.addCell(createDataCell(rowNumber.toString(), regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                table.addCell(createDataCell("", regularFont, 10f, TextAlignment.CENTER)) // Empty price
                table.addCell(createDataCell(productNameWithCategory, regularFont, 10f, TextAlignment.LEFT))
                table.addCell(createDataCell("", regularFont, 10f, TextAlignment.RIGHT)) // Empty subtotal
            }

            rowNumber++
        }

        return total
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
}
