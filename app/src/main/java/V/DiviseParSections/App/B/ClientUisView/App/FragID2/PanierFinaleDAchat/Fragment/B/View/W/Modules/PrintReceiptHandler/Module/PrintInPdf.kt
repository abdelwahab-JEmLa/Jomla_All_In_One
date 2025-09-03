package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CreditReceiptData(
    val client: M2Client?,
    val totalAmount: Double,
    val currentPayment: Double,
    val previousPayments: List<Double> = emptyList(),
    val transactionId: String,
    val showPaymentHistory: Boolean = false
)

class PrintInPdf_itextpdf_Handler {

    companion object {
        private const val TAG = "PrintInPdf_Handler"
        private const val SPACING_2DP = 0.7f
    }

    private val storageRef = Firebase.storage.reference.child("bonVents_pdf")

    // Key fixes for the PDF generation issue:

    // 1. DON'T reuse fonts across documents - create fresh fonts for each document
    private fun createFreshFonts(): Pair<PdfFont, PdfFont> {
        return try {
            val regular = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            Pair(regular, bold)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to create PDF fonts", e)
        }
    }

    // 2. Fixed generateVentPdf method - create fresh fonts each time
    private fun generateVentPdf(
        path: String, client: M2Client?, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit, its_GrossistApp: Boolean
    ) {
        Log.d(TAG, "Starting generateVentPdf - Path: $path")

        try {
            // Create fresh fonts for this document - DON'T reuse class-level fonts
            val (regularFont, boldFont) = createFreshFonts()

            Log.d(TAG, "Creating PdfWriter...")
            PdfWriter(path).use { writer ->
                Log.d(TAG, "PdfWriter created successfully")

                Log.d(TAG, "Creating PdfDocument...")
                PdfDocument(writer).use { pdfDoc ->
                    Log.d(TAG, "PdfDocument created successfully")

                    Log.d(TAG, "Creating Document with A5 size...")
                    Document(pdfDoc, PageSize.A5).use { doc ->
                        Log.d(TAG, "Document created successfully")

                        // Only add header if its_GrossistApp is false
                        if (!its_GrossistApp) {
                            Log.d(TAG, "Adding header...")
                            addHeader(doc, "Facture", regularFont, boldFont)
                        } else {
                            Log.d(TAG, "Skipping header (Grossist App)")
                        }

                        Log.d(TAG, "Adding client and date...")
                        addClientDate(doc, client?.nom ?: "Client", regularFont)

                        Log.d(TAG, "Creating product table...")
                        createProductTable(doc, operations, tarificationRepo, produitRepo, regularFont, boldFont)

                        // Add credit balance if negative
                        client?.currentCreditBalance?.takeIf { it < 0 }?.let { credit ->
                            Log.d(TAG, "Adding credit balance: $credit")
                            addText(doc, "Credit Du Compte actuel", regularFont, 12f, TextAlignment.CENTER)
                            addText(doc, "${round(credit)}Da", regularFont, 14f, TextAlignment.CENTER)
                        }

                        Log.d(TAG, "Document content added successfully")
                    }
                }
            }
            Log.d(TAG, "generateVentPdf completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error in generateVentPdf", e)
            throw e
        }
    }

    // 3. Fixed generateCreditPdf method
    private fun generateCreditPdf(path: String, data: CreditReceiptData) {
        Log.d(TAG, "Starting generateCreditPdf - Path: $path")

        try {
            // Create fresh fonts for this document
            val (regularFont, boldFont) = createFreshFonts()

            Log.d(TAG, "Creating PdfWriter...")
            PdfWriter(path).use { writer ->
                Log.d(TAG, "PdfWriter created successfully")

                Log.d(TAG, "Creating PdfDocument...")
                PdfDocument(writer).use { pdfDoc ->
                    Log.d(TAG, "PdfDocument created successfully")

                    Log.d(TAG, "Creating Document with A5 size...")
                    Document(pdfDoc, PageSize.A5).use { doc ->
                        Log.d(TAG, "Document created successfully")

                        val receiptType = if (data.showPaymentHistory) "Credit Payment Prix_Détaillé" else "Credit Payment"
                        Log.d(TAG, "Adding header with type: $receiptType")
                        addHeader(doc, receiptType, regularFont, boldFont)

                        Log.d(TAG, "Adding client and date...")
                        addClientDate(doc, data.client?.nom ?: "Client", regularFont)

                        if (data.showPaymentHistory) {
                            Log.d(TAG, "Adding transaction ID...")
                            addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.LEFT)
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        }

                        val totalPaid = if (data.showPaymentHistory) data.previousPayments.sum() + data.currentPayment else data.currentPayment
                        val remaining = data.totalAmount - totalPaid
                        val totalLabel = if (data.showPaymentHistory) "Montant Total" else "Total à Payer"

                        Log.d(TAG, "Adding payment details - Total: ${data.totalAmount}, Paid: $totalPaid, Remaining: $remaining")

                        addText(doc, "$totalLabel :", boldFont, 12f, TextAlignment.LEFT)
                        addText(doc, "${round(data.totalAmount)}Da", boldFont, 14f, TextAlignment.CENTER)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

                        if (data.showPaymentHistory && data.previousPayments.isNotEmpty()) {
                            Log.d(TAG, "Adding previous payments history...")
                            addText(doc, "Paiements Précédents:", regularFont, 10f, TextAlignment.LEFT)
                            data.previousPayments.forEachIndexed { i, payment ->
                                addText(doc, "  ${i + 1}. ${round(payment)}Da", regularFont, 10f, TextAlignment.LEFT)
                            }
                            addText(doc, "Sous-total: ${round(data.previousPayments.sum())}Da", regularFont, 10f, TextAlignment.LEFT)
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                            addText(doc, "Paiement Actuel:", boldFont, 12f, TextAlignment.LEFT)
                        } else {
                            addText(doc, "Versement Effectué:", boldFont, 12f, TextAlignment.LEFT)
                        }

                        addText(doc, "${round(data.currentPayment)}Da", boldFont, 14f, TextAlignment.CENTER)
                        doc.add(Paragraph("\n").setFontSize(SPACING_2DP))

                        if (data.showPaymentHistory) {
                            addText(doc, "Total Payé: ${round(totalPaid)}Da", regularFont, 10f, TextAlignment.LEFT)
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                        }

                        Log.d(TAG, "Adding remaining balance section...")
                        when {
                            remaining > 0 -> {
                                val label = if (data.showPaymentHistory) "Reste à Payer" else "Crédit Restant"
                                addText(doc, "$label :", boldFont, 12f, TextAlignment.LEFT)
                                addText(doc, "${round(remaining)}Da", boldFont, 16f, TextAlignment.RIGHT)
                            }
                            remaining < 0 -> {
                                val label = if (data.showPaymentHistory) "Trop Payé" else "Surplus Payé"
                                addText(doc, "$label :", boldFont, 12f, TextAlignment.LEFT)
                                addText(doc, "${round(-remaining)}Da", boldFont, 16f, TextAlignment.RIGHT)
                            }
                            else -> {
                                if (data.showPaymentHistory) {
                                    addText(doc, "✓ PAYÉ COMPLÈTEMENT ✓", boldFont, 14f, TextAlignment.CENTER)
                                    addText(doc, "Merci pour votre confiance", regularFont, 12f, TextAlignment.CENTER)
                                } else {
                                    addText(doc, "PAYÉ INTÉGRALEMENT", boldFont, 14f, TextAlignment.CENTER)
                                    addText(doc, "✓ SOLDÉ", boldFont, 12f, TextAlignment.CENTER)
                                }
                            }
                        }

                        if (!data.showPaymentHistory) {
                            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
                            addText(doc, "Transaction: #${data.transactionId}", regularFont, 10f, TextAlignment.CENTER)
                        }

                        Log.d(TAG, "Credit document content added successfully")
                    }
                }
            }
            Log.d(TAG, "generateCreditPdf completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error in generateCreditPdf", e)
            throw e
        }
    }

    // 4. Update helper methods to accept fonts as parameters
    private fun addHeader(doc: Document, title: String, regularFont: PdfFont, boldFont: PdfFont) {
        try {
            Log.d(TAG, "Adding header with title: $title")
            addText(doc, "Abdelwahab", boldFont, 18f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, "JeMla.Com", boldFont, 16f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, "0553885037", regularFont, 12f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, title, regularFont, 12f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            Log.d(TAG, "Header added successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding header", e)
            throw e
        }
    }

    private fun addClientDate(doc: Document, clientName: String, regularFont: PdfFont) {
        try {
            Log.d(TAG, "Adding client date section for: $clientName")
            val date = formatDateWithAmPm(Date())
            val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            table.setWidth(UnitValue.createPercentValue(100f))

            val clientCell = Cell().add(Paragraph(capitalizeFirstLetter(clientName)).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.LEFT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

            val dateCell = Cell().add(Paragraph(date).setFont(regularFont).setFontSize(12f).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

            table.addCell(clientCell)
            table.addCell(dateCell)
            doc.add(table)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            Log.d(TAG, "Client date section added successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding client date section", e)
            throw e
        }
    }

    private fun createProductTable(
        doc: Document, operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos, produitRepo: RepoM1Produit,
        regularFont: PdfFont, boldFont: PdfFont
    ) {
        try {
            Log.d(TAG, "Creating product table with ${operations.size} operations")

            val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 15f, 20f, 35f, 20f)))
            table.setWidth(UnitValue.createPercentValue(100f))

            // Headers with numbering column
            Log.d(TAG, "Adding table headers...")
            table.addCell(createHeaderCell("N°", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Qté", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("P.U", boldFont, 11f, TextAlignment.CENTER))
            table.addCell(createHeaderCell("Désignation", boldFont, 11f, TextAlignment.LEFT))
            table.addCell(createHeaderCell("Sous-total", boldFont, 11f, TextAlignment.RIGHT))

            var total = 0.0
            var rowNumber = 1

            Log.d(TAG, "Processing grouped operations...")
            val groupedOps = operations.groupBy { it.parent_M1Produit_KeyId }
            Log.d(TAG, "Found ${groupedOps.size} product groups")

            groupedOps.forEach { (produitId, ops) ->
                try {
                    Log.d(TAG, "Processing product ID: $produitId with ${ops.size} operations")

                    val tarification = tarificationRepo.datasValue.find { it.keyID == ops.first().parentM13TarificationKeyID }
                    val produit = produitRepo.datasValue.find { it.keyID == produitId }
                    val qty = ops.sumOf { it.quantity }
                    val price = tarification?.prixCurrency ?: 0.0
                    val subtotal = price * qty

                    Log.d(TAG, "Product: ${produit?.nom}, Qty: $qty, Price: $price, Subtotal: $subtotal")

                    if (subtotal != 0.0) {
                        val qtyDisplay = formatQuantity(qty, produit?.quantite_Boit_Par_Carton ?: 1)
                        val productName = capitalizeFirstLetter(produit?.nom ?: "Produit")

                        table.addCell(createDataCell(rowNumber.toString(), regularFont, 10f, TextAlignment.CENTER))
                        table.addCell(createDataCell(qtyDisplay, regularFont, 10f, TextAlignment.CENTER))
                        table.addCell(createDataCell("${round(price)}", regularFont, 10f, TextAlignment.CENTER))
                        table.addCell(createDataCell(productName, regularFont, 10f, TextAlignment.LEFT))
                        table.addCell(createDataCell("${round(subtotal)}", regularFont, 10f, TextAlignment.RIGHT))

                        total += subtotal
                        rowNumber++
                        Log.d(TAG, "Added row $rowNumber for product: $productName")
                    } else {
                        Log.d(TAG, "Skipping product with zero subtotal")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing product ID: $produitId", e)
                    // Continue with next product instead of failing completely
                }
            }

            Log.d(TAG, "Adding table to document...")
            doc.add(table)
            doc.add(Paragraph("\n").setFontSize(0.1f))

            Log.d(TAG, "Adding total: $total")
            addText(doc, "Total", boldFont, 14f, TextAlignment.CENTER)
            addText(doc, "${round(total)}Da", boldFont, 16f, TextAlignment.CENTER)

            Log.d(TAG, "Product table created successfully with total: $total")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating product table", e)
            throw e
        }
    }

    // Initialize fonts with error handling
    private val regularFont: PdfFont? by lazy {
        try {
            Log.d(TAG, "Initializing regular font")
            PdfFontFactory.createFont(StandardFonts.HELVETICA)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating regular font", e)
            null
        }
    }
    private fun formatDateWithAmPm(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        // French day names abbreviations
        val frenchDays = arrayOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
        val dayOfWeek = frenchDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]

        // French month names abbreviations
        val frenchMonths = arrayOf(
            "Jan", "Fév", "Mar", "Avr", "Mai", "Jun",
            "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"
        )
        val month = frenchMonths[calendar.get(Calendar.MONTH)]

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return "$dayOfWeek $dayOfMonth/$month/$year ${String.format("%02d:%02d", hour, minute)}"
    }

    private val boldFont: PdfFont? by lazy {
        try {
            Log.d(TAG, "Initializing bold font")
            PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating bold font", e)
            null
        }
    }

    suspend fun generateVentReceiptPdf(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = "",
        its_GrossistApp: Boolean = true
    ): Result<String> {
        Log.d(TAG, "=== STARTING VENT RECEIPT PDF GENERATION ===")
        Log.d(TAG, "Client: ${client?.nom ?: "null"}")
        Log.d(TAG, "Operations count: ${operations.size}")
        Log.d(TAG, "Transaction ID: $transactionId")
        Log.d(TAG, "Is Grossist App: $its_GrossistApp")

        return try {
            // Validate input data
            if (operations.isEmpty()) {
                Log.e(TAG, "ERROR: No operations to print")
                return Result.failure(IllegalArgumentException("No operations to print"))
            }

            // Check fonts initialization
            if (regularFont == null || boldFont == null) {
                Log.e(TAG, "ERROR: Fonts not initialized properly")
                return Result.failure(IllegalStateException("PDF fonts initialization failed"))
            }

            // Create file
            Log.d(TAG, "Creating local file...")
            val file = createLocalFile(context, client?.nom ?: "Client", "receipt", transactionId)
            Log.d(TAG, "File created: ${file.absolutePath}")
            Log.d(TAG, "File parent directory exists: ${file.parentFile?.exists()}")
            Log.d(TAG, "File parent directory writable: ${file.parentFile?.canWrite()}")

            // Generate PDF
            Log.d(TAG, "Starting PDF generation...")
            generateVentPdf(file.absolutePath, client, operations, tarificationRepo, produitRepo, its_GrossistApp)
            Log.d(TAG, "PDF generation completed")

            // Check if file was created successfully
            if (!file.exists()) {
                Log.e(TAG, "ERROR: PDF file was not created")
                return Result.failure(IllegalStateException("PDF file creation failed"))
            }

            Log.d(TAG, "File size after generation: ${file.length()} bytes")

            // Upload to Firebase
            Log.d(TAG, "Starting Firebase upload...")
            val url = uploadToFirebaseStorage(file, file.name)
            Log.d(TAG, "Firebase upload completed: $url")

            Log.d(TAG, "=== PDF GENERATION SUCCESSFUL ===")
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")

        } catch (e: Exception) {
            Log.e(TAG, "ERROR in generateVentReceiptPdf", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }

    suspend fun generateCreditReceiptPdf(context: Context, data: CreditReceiptData): Result<String> {
        Log.d(TAG, "=== STARTING CREDIT RECEIPT PDF GENERATION ===")
        Log.d(TAG, "Client: ${data.client?.nom ?: "null"}")
        Log.d(TAG, "Total amount: ${data.totalAmount}")
        Log.d(TAG, "Current payment: ${data.currentPayment}")
        Log.d(TAG, "Transaction ID: ${data.transactionId}")
        Log.d(TAG, "Show payment history: ${data.showPaymentHistory}")

        return try {
            // Validate input data
            if (data.totalAmount <= 0) {
                Log.e(TAG, "ERROR: Invalid total amount: ${data.totalAmount}")
                return Result.failure(IllegalArgumentException("Invalid total amount"))
            }

            // Check fonts initialization
            if (regularFont == null || boldFont == null) {
                Log.e(TAG, "ERROR: Fonts not initialized properly")
                return Result.failure(IllegalStateException("PDF fonts initialization failed"))
            }

            // Create file
            Log.d(TAG, "Creating local file...")
            val file = createLocalFile(context, data.client?.nom ?: "Client", "credit", data.transactionId)
            Log.d(TAG, "File created: ${file.absolutePath}")

            // Generate PDF
            Log.d(TAG, "Starting PDF generation...")
            generateCreditPdf(file.absolutePath, data)
            Log.d(TAG, "PDF generation completed")

            // Check if file was created successfully
            if (!file.exists()) {
                Log.e(TAG, "ERROR: PDF file was not created")
                return Result.failure(IllegalStateException("PDF file creation failed"))
            }

            Log.d(TAG, "File size after generation: ${file.length()} bytes")

            // Upload to Firebase
            Log.d(TAG, "Starting Firebase upload...")
            val url = uploadToFirebaseStorage(file, file.name)
            Log.d(TAG, "Firebase upload completed: $url")

            Log.d(TAG, "=== CREDIT PDF GENERATION SUCCESSFUL ===")
            Result.success("PDF saved: ${file.absolutePath}\nFirebase: $url")

        } catch (e: Exception) {
            Log.e(TAG, "ERROR in generateCreditReceiptPdf", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }

    private fun createLocalFile(context: Context, clientName: String, type: String, id: String): File {
        Log.d(TAG, "Creating local file - Client: $clientName, Type: $type, ID: $id")

        return try {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
            Log.d(TAG, "Target directory: ${dir.absolutePath}")
            Log.d(TAG, "Directory exists before creation: ${dir.exists()}")

            if (!dir.exists()) {
                val created = dir.mkdirs()
                Log.d(TAG, "Directory creation result: $created")
                Log.d(TAG, "Directory exists after creation: ${dir.exists()}")
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val sanitizedClientName = clientName.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val fileName = "${type}_${sanitizedClientName}_${timestamp}_$id.pdf"

            Log.d(TAG, "Generated filename: $fileName")

            val file = File(dir, fileName)
            Log.d(TAG, "Final file path: ${file.absolutePath}")
            Log.d(TAG, "Parent directory writable: ${file.parentFile?.canWrite()}")

            file
        } catch (e: Exception) {
            Log.e(TAG, "Error creating local file", e)
            throw e
        }
    }

    private fun addHeader(doc: Document, title: String) {
        try {
            Log.d(TAG, "Adding header with title: $title")
            addText(doc, "Abdelwahab", boldFont!!, 18f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, "JeMla.Com", boldFont!!, 16f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, "0553885037", regularFont!!, 12f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            addText(doc, title, regularFont!!, 12f, TextAlignment.CENTER)
            doc.add(Paragraph("\n").setFontSize(SPACING_2DP))
            Log.d(TAG, "Header added successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding header", e)
            throw e
        }
    }

    private fun createHeaderCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell().add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(0f)

    private fun createDataCell(content: String, font: PdfFont, size: Float, align: TextAlignment): Cell =
        Cell().add(Paragraph(content).setFont(font).setFontSize(size).setTextAlignment(align))
            .setBorder(SolidBorder(0.1f)).setPadding(0f)

    private fun addText(doc: Document, text: String, font: PdfFont, size: Float, align: TextAlignment) =
        doc.add(Paragraph(text).setFont(font).setFontSize(size).setTextAlignment(align).setMargin(0f))

    private fun formatQuantity(qty: Int, cartonSize: Int): String =
        if (cartonSize in 2..qty && qty % cartonSize == 0)
            "${qty / cartonSize} X $cartonSize" else qty.toString()

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0

    private fun capitalizeFirstLetter(text: String): String {
        return if (text.isBlank()) text
        else text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    private suspend fun uploadToFirebaseStorage(file: File, fileName: String): String {
        return try {
            Log.d(TAG, "Starting Firebase upload for file: $fileName")
            Log.d(TAG, "File exists: ${file.exists()}, File size: ${file.length()} bytes")

            val fileRef = storageRef.child(fileName)
            Log.d(TAG, "Firebase reference created: ${fileRef.path}")

            val uploadTask = fileRef.putFile(android.net.Uri.fromFile(file))
            Log.d(TAG, "Upload task created, starting upload...")

            uploadTask.await()
            Log.d(TAG, "File uploaded successfully")

            val downloadUrl = fileRef.downloadUrl.await().toString()
            Log.d(TAG, "Download URL obtained: $downloadUrl")

            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading to Firebase Storage", e)
            Log.e(TAG, "Firebase upload error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Firebase upload error message: ${e.message}")
            throw e
        }
    }
}
