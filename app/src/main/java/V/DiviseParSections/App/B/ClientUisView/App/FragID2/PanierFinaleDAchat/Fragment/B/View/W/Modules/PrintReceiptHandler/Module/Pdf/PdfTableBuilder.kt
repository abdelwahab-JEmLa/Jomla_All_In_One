package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File

/**
 * FIXED: Added support for product images in PDF receipts
 * Images are displayed in a dedicated column when bon is in bons_a_imprime_avec_image_produit
 */
class PdfTableBuilder(
    private val formatter: PdfFormatterUtils,
    private val contentBuilder: PdfContentBuilder,
    private val focusedValuesGetter: FocusedValuesGetter
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
        // FIXED: Check if this bon should include images
        val shouldIncludeImages = relativeBonvent?.let { bonVent ->
            focusedValuesGetter.active_Central_Values.bons_a_imprime_avec_image_produit
                .any { it.keyID == bonVent.keyID }
        } ?: false

        // FIXED: Create table with image column if needed
        val columnWidths = if (shouldIncludeImages) {
            floatArrayOf(10f, 15f, 15f, 40f, 20f) // Image, Qté, P.U, Désignation, Sous-total
        } else {
            floatArrayOf(15f, 20f, 45f, 20f) // Qté, P.U, Désignation, Sous-total
        }

        val table = Table(UnitValue.createPercentArray(columnWidths))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont, shouldIncludeImages)
        val result = addTableRows(
            table,
            operations,
            tarificationRepo,
            produitRepo,
            regularFont,
            boldFont,
            shouldIncludeImages
        )

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

    /**
     * FIXED: Added image header when images are enabled
     */
    private fun addTableHeaders(table: Table, boldFont: PdfFont, includeImages: Boolean) {
        if (includeImages) {
            table.addCell(createHeaderCell("Img", boldFont, 11f, TextAlignment.CENTER))
        }
        table.addCell(createHeaderCell("Qté", boldFont, 13f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("P.U", boldFont, 13f, TextAlignment.CENTER))
        table.addCell(createHeaderCell("Désignation", boldFont, 13f, TextAlignment.LEFT))
        table.addCell(createHeaderCell("Sous-total", boldFont, 13f, TextAlignment.RIGHT))
    }

    /**
     * FIXED: Added image loading and display in table rows
     */
    private fun addTableRows(
        table: Table,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont,
        includeImages: Boolean
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

            val qtyDisplay = formatter.formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1, produit)
            val productNameWithCategory = formatter.formatProductNameWithCategory(produit)

            // FIXED: Add image cell if images are enabled
            if (includeImages) {
                val imageCell = createImageCell(ops.first())
                table.addCell(imageCell)
            }

            if (shouldDisplayPriceAndSubtotal) {
                val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                    val nombreUniteInt = produit.nombreUniteInt
                    if (nombreUniteInt > 0) rawPrice / nombreUniteInt else rawPrice
                } else rawPrice

                table.addCell(createDataCell(qtyDisplay, boldFont, 11f, TextAlignment.CENTER))
                table.addCell(createDataCell("${formatter.round(unitPrice)}", regularFont, 11f, TextAlignment.CENTER))
                table.addCell(createDataCell(productNameWithCategory, boldFont, 13f, TextAlignment.LEFT))
                table.addCell(createDataCell("${formatter.round(subtotal)}", regularFont, 11f, TextAlignment.RIGHT))

                total += subtotal
                itemCount++
            } else {
                table.addCell(createDataCell(qtyDisplay, boldFont, 11f, TextAlignment.CENTER))
                table.addCell(createDataCell("", regularFont, 11f, TextAlignment.CENTER))
                table.addCell(createDataCell(productNameWithCategory, boldFont, 13f, TextAlignment.LEFT))
                table.addCell(createDataCell("", regularFont, 11f, TextAlignment.RIGHT))
                itemCount++
            }
        }

        return TableResult(total, itemCount)
    }

    /**
     * FIXED: Create image cell for product
     * Images are 20x20 points (approximately 20dp at standard PDF resolution)
     */
    private fun createImageCell(operation: M10OperationVentCouleur): Cell {
        val imageFile = getProductImageFile(operation)

        return if (imageFile != null && imageFile.exists()) {
            try {
                val imageData = ImageDataFactory.create(imageFile.absolutePath)
                val image = Image(imageData)
                    .setWidth(20f)  // 20 points width
                    .setHeight(20f) // 20 points height
                    .setAutoScale(true)

                Cell()
                    .add(image)
                    .setBorder(SolidBorder(0.5f))
                    .setPadding(2f)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
            } catch (e: Exception) {
                // If image loading fails, return empty cell
                createEmptyImageCell()
            }
        } else {
            createEmptyImageCell()
        }
    }

    /**
     * Get the image file for a product operation
     * Searches in the standard image directory
     */
    private fun getProductImageFile(operation: M10OperationVentCouleur): File? {
        return try {
            // Get the main color for this operation
            val couleurRepo = focusedValuesGetter.run {
                focusedValuesGetter.repo3CouleurProduitInfos
            }

            val couleur = couleurRepo.datasValue.find {
                it.keyID == operation.parent_M3CouleurProduit_KeyID
            }

            couleur?.let {
                if (it.nomImageFichieSansEtansion != "Non Dispo") {
                    val fileName = "${it.nomImageFichieSansEtansion}.${it.extensionDisponible}"
                    File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Create empty cell when image is not available
     */
    private fun createEmptyImageCell(): Cell {
        return Cell()
            .add(Paragraph("—").setFontSize(8f))
            .setBorder(SolidBorder(0.5f))
            .setPadding(2f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(TextAlignment.CENTER)
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
        val totalTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        val totalLabelCell = Cell()
            .add(Paragraph(Titres.A.text).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(0f)

        val totalValueCell = Cell()
            .add(Paragraph("${formatter.round(totalBon)} Da ($itemCount items)").setFont(boldFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
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

        val ancienCreditTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        ancienCreditTable.addCell(
            Cell().add(Paragraph(Titres.B.text).setFont(regularFont).setFontSize(11f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        ancienCreditTable.addCell(
            Cell().add(Paragraph("${formatter.round(creditValue)} Da").setFont(regularFont).setFontSize(11f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        doc.add(ancienCreditTable)
        doc.add(Paragraph("\n").setFontSize(0.2f))

        val creditApresVent = creditValue + totalBon
        val creditApresTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        creditApresTable.addCell(
            Cell().add(Paragraph(Titres.C.text).setFont(regularFont).setFontSize(11f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        creditApresTable.addCell(
            Cell().add(Paragraph("${formatter.round(creditApresVent)} Da").setFont(regularFont).setFontSize(11f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        doc.add(creditApresTable)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        val versementTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        versementTable.addCell(
            Cell().add(Paragraph(Titres.D.text).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        versementTable.addCell(
            Cell().add(Paragraph("${formatter.round(bonVent.versement_fait)} Da").setFont(boldFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        doc.add(versementTable)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        doc.add(Paragraph("────────────────────────").setFont(regularFont).setFontSize(10f).setTextAlignment(TextAlignment.LEFT).setMargin(0f))
        doc.add(Paragraph("\n").setFontSize(0.3f))

        val nouveauCompteTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))

        nouveauCompteTable.addCell(
            Cell().add(Paragraph(Titres.E.text).setFont(boldFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        nouveauCompteTable.addCell(
            Cell().add(Paragraph("${formatter.round(bonVent.new_credit_apre_tout_fait)} Da").setFont(boldFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
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
        // Don't include images in this variant
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 14f, 56f, 15f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont, includeImages = false)
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont, boldFont, includeImages = false)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        return result.total
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
            .setPadding(5f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(align)
    }

    private fun createDataCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell()
            .add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.5f))
            .setPadding(5f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)

    private data class TableResult(val total: Double, val itemCount: Int)
}
