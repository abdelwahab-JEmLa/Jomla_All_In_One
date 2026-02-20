package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

/**
 * FIXED: Proper resource management for iText PDF objects
 * - All Bitmap objects are explicitly recycled
 * - ByteArrayOutputStream properly closed
 * - Image creation wrapped in try-catch with cleanup
 * - This eliminates "A resource failed to call end" warnings
 */
class PdfTableBuilder(
    private val formatter: PdfFormatterUtils,
    private val contentBuilder: PdfContentBuilder,
    private val focusedValuesGetter: FocusedValuesGetter
) {

    private companion object {
        const val STANDARD_ROW_HEIGHT = 20f
        const val IMAGE_ROW_HEIGHT = 70f
        const val IMAGE_SIZE = 65f
        const val PNG_QUALITY = 70
        const val MAX_IMAGE_DIMENSION = 200
        const val TAG = "PDF_TABLE_BUILDER"
    }

    fun createProductTable(
        doc: Document,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        regularFont: PdfFont,
        boldFont: PdfFont,
        relativeBonvent: M8BonVent?
    ) {
        val bonsWithImages = focusedValuesGetter.active_Central_Values.bons_a_imprime_avec_image_produit
        val shouldIncludeImages = relativeBonvent?.let { bonVent ->
            bonsWithImages.any { it.keyID == bonVent.keyID }
        } ?: false

        val columnWidths = if (shouldIncludeImages) {
            floatArrayOf(25f, 12f, 12f, 38f, 13f)
        } else {
            floatArrayOf(15f, 20f, 45f, 20f)
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

    private fun addTableHeaders(table: Table, boldFont: PdfFont, includeImages: Boolean) {
        if (includeImages) {
            table.addCell(createHeaderCell("Img", boldFont, 10f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
            table.addCell(createHeaderCell("S-tot", boldFont, 10f, TextAlignment.RIGHT))
        } else {
            table.addCell(createHeaderCell("Qté", boldFont, 13f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("P.U", boldFont, 13f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Désignation", boldFont, 13f, TextAlignment.LEFT))
            table.addCell(createHeaderCell("Sous-total", boldFont, 13f, TextAlignment.RIGHT))
        }
    }

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

        sortedGroupedOps.forEachIndexed { index, (produitId, ops) ->
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

            val imageResult = if (includeImages) {
                findProductImage(ops.first())
            } else {
                ImageSearchResult(null, false)
            }

            val rowHeight = if (imageResult.imageFound) IMAGE_ROW_HEIGHT else STANDARD_ROW_HEIGHT

            if (includeImages) {
                val imageCell = createImageCell(imageResult.imageFile, rowHeight)
                    .setKeepTogether(true)
                table.addCell(imageCell)
            }

            if (shouldDisplayPriceAndSubtotal) {
                val unitPrice = if (produit?.afficheUniteAuPrint == true) {
                    val nombreUniteInt = produit.nombreUniteInt
                    if (nombreUniteInt > 0) rawPrice / nombreUniteInt else rawPrice
                } else rawPrice

                val textSize = if (includeImages) 9f else 11f
                val productNameSize = if (includeImages) 10f else 13f

                table.addCell(createDataCell(qtyDisplay, boldFont, textSize, TextAlignment.CENTER, rowHeight).apply {
                    if (includeImages) setKeepTogether(true)
                })
                table.addCell(createDataCell("${formatter.round(unitPrice)}", regularFont, textSize, TextAlignment.CENTER, rowHeight).apply {
                    if (includeImages) setKeepTogether(true)
                })
                table.addCell(createDataCell(productNameWithCategory, boldFont, productNameSize, TextAlignment.LEFT, rowHeight).apply {
                    if (includeImages) setKeepTogether(true)
                })
                table.addCell(createDataCell("${formatter.round(subtotal)}", regularFont, textSize, TextAlignment.RIGHT, rowHeight).apply {
                    if (includeImages) setKeepTogether(true)
                })

                total += subtotal
                itemCount += qty
            } else {
                val emptyContent = ""
                table.addCell(createDataCell(qtyDisplay, boldFont, 11f, TextAlignment.CENTER, rowHeight))
                table.addCell(createDataCell(emptyContent, regularFont, 11f, TextAlignment.CENTER, rowHeight))
                table.addCell(createDataCell(productNameWithCategory, boldFont, 13f, TextAlignment.LEFT, rowHeight))
                table.addCell(createDataCell(emptyContent, regularFont, 11f, TextAlignment.RIGHT, rowHeight))

                itemCount += qty
            }
        }

        return TableResult(total, itemCount)
    }

    /**
     * FIXED: Proper resource management for image creation
     * - Bitmap is explicitly recycled after use
     * - ByteArrayOutputStream is properly closed with .use {}
     * - Try-catch wraps the entire process to prevent resource leaks
     * - This eliminates the "A resource failed to call end" warnings
     */
    private fun createImageCell(imageFile: File?, rowHeight: Float): Cell {
        if (imageFile == null || !imageFile.exists()) {
            return createEmptyImageCell(rowHeight)
        }

        var bitmap: Bitmap? = null
        try {
            // Step 1: Decode with downsampling
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imageFile.absolutePath, options)

            options.inSampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                MAX_IMAGE_DIMENSION
            )
            options.inJustDecodeBounds = false

            bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                ?: return createEmptyImageCell(rowHeight)

            // Step 2: Convert to byte array with proper resource management
            val imageData = ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, stream)
                stream.toByteArray()
            }

            // Step 3: Create iText Image from byte array
            val imgData = ImageDataFactory.create(imageData)
            val image = Image(imgData)
                .setWidth(IMAGE_SIZE)
                .setHeight(IMAGE_SIZE)
                .setAutoScale(true)

            return Cell()
                .add(image)
                .setBorder(SolidBorder(0.5f))
                .setPadding(2f)
                .setHeight(rowHeight)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER)

        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error creating image cell: ${e.message}", e)
            return createEmptyImageCell(rowHeight)
        } finally {
            // CRITICAL: Always recycle bitmap to prevent memory leaks
            bitmap?.recycle()
        }
    }

    private fun findProductImage(operation: M10OperationVentCouleur): ImageSearchResult {
        try {
            val couleurProduit = operation.parent_M3CouleurProduit_KeyID?.let { keyID ->
                focusedValuesGetter.find_M3CouleurInfos_By_KeyID(keyID)
            }

            if (couleurProduit == null) {
                android.util.Log.d(TAG, "No color product found for operation: ${operation.keyID}")
                return ImageSearchResult(null, false)
            }

            val imageFileName = couleurProduit.nomImageFichieSansEtansion
            val extension = couleurProduit.extensionDisponible

            if (imageFileName.isNullOrEmpty() || extension.isNullOrEmpty()) {
                android.util.Log.d(TAG, "No image filename or extension for color product: ${couleurProduit.keyID}")
                return ImageSearchResult(null, false)
            }

            // FIXED: Use the correct image directory path (same as ImageDisplayerGlide_FragFastVent)
            val baseDir = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne")

            if (!baseDir.exists()) {
                android.util.Log.w(TAG, "Image directory does not exist: ${baseDir.absolutePath}")
                return ImageSearchResult(null, false)
            }

            // Use the decrementing function to find the actual image file (same logic as in ImageDisplayerGlide_FragFastVent)
            val actualImageFileName = M3CouleurProduitInfos
                .decrementing_file_name_si_non_trouve(imageFileName, extension)

            val imageFile = actualImageFileName?.let {
                File(baseDir, "$it.$extension")
            }

            return if (imageFile != null && imageFile.exists()) {
                android.util.Log.d(TAG, "✅ Image found: ${imageFile.absolutePath}")
                ImageSearchResult(imageFile, true)
            } else {
                android.util.Log.d(TAG, "❌ Image not found: $imageFileName.$extension in ${baseDir.absolutePath}")
                ImageSearchResult(null, false)
            }

        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error searching for image: ${e.message}", e)
            return ImageSearchResult(null, false)
        }
    }


    private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sampleSize = 1
        val maxOriginalDimension = max(width, height)

        if (maxOriginalDimension > maxDimension) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while ((halfWidth / sampleSize) >= maxDimension &&
                (halfHeight / sampleSize) >= maxDimension) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }

    private fun createEmptyImageCell(rowHeight: Float): Cell {
        return Cell()
            .add(Paragraph("—").setFontSize(8f))
            .setBorder(SolidBorder(0.5f))
            .setPadding(2f)
            .setHeight(rowHeight)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(TextAlignment.CENTER)
    }

    private data class ImageSearchResult(
        val imageFile: File?,
        val imageFound: Boolean
    )

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

        totalTable.addCell(
            Cell().add(Paragraph(Titres.A.text).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
        totalTable.addCell(
            Cell().add(Paragraph("${formatter.round(totalBon)} Da ($itemCount items)").setFont(boldFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)
        )
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
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 14f, 56f, 15f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addTableHeaders(table, boldFont, includeImages = false)
        val result = addTableRows(table, operations, tarificationRepo, produitRepo, regularFont, boldFont, includeImages = false)

        doc.add(table)
        doc.add(Paragraph("\n").setFontSize(0.3f))

        return result.total
    }

    private fun createHeaderCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell {
        return Cell()
            .add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align).setMargin(0f))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(5f)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setTextAlignment(align)
    }

    private fun createDataCell(
        content: String,
        font: PdfFont,
        size: Float,
        align: TextAlignment,
        rowHeight: Float = STANDARD_ROW_HEIGHT
    ): Cell =
        Cell()
            .add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.5f))
            .setPadding(5f)
            .setHeight(rowHeight)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)

    private data class TableResult(val total: Double, val itemCount: Int)
}
