package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import android.content.Context
import android.os.Environment
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrintInPdf_itextpdf_Handler {
    val storageRef = Firebase.storage.reference.child("bonVents_pdf")
    private val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/bonVents_pdf"

    /**
     * Generate a PDF receipt and save it locally and to Firebase Storage
     */
    suspend fun generateAndStorePdfReceipt(
        context: Context,
        receiptText: String,
        clientName: String = "Client",
        transactionId: String = ""
    ): Result<String> {
        return try {
            // Create local directory if it doesn't exist
            val localDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
            if (!localDir.exists()) {
                localDir.mkdirs()
            }

            // Generate filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "receipt_${clientName}_${timestamp}_${transactionId}.pdf"
            val localFile = File(localDir, fileName)

            // Generate PDF
            val pdfPath = generatePdfFromText(receiptText, localFile.absolutePath)

            // Upload to Firebase Storage
            val downloadUrl = uploadToFirebaseStorage(localFile, fileName)

            Result.success("PDF saved locally: $pdfPath\nFirebase URL: $downloadUrl")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate PDF from formatted receipt text
     */
    private fun generatePdfFromText(receiptText: String, outputPath: String): String {
        val writer = PdfWriter(outputPath)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // Set up fonts
        val regularFont: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        // Parse the formatted text and convert to PDF
        parseAndAddFormattedText(document, receiptText, regularFont, boldFont)

        document.close()
        return outputPath
    }

    /**
     * Parse the HTML-like formatted text and convert to PDF elements - FIXED VERSION
     */
    private fun parseAndAddFormattedText(
        document: Document,
        receiptText: String,
        regularFont: PdfFont,
        boldFont: PdfFont
    ) {
        val lines = receiptText.split("<BR>")

        for (line in lines) {
            if (line.trim().isEmpty() || line == ">") continue

            val paragraph = Paragraph()
            var currentLine = line.trim()

            // Skip empty lines after cleaning
            if (currentLine.isEmpty()) continue

            // Handle separator lines first (common pattern)
            if (currentLine.contains("=====") || currentLine.contains("-----")) {
                val cleanText = cleanAllTags(currentLine)
                if (cleanText.isNotEmpty()) {
                    paragraph.add(Text(cleanText).setFont(regularFont).setFontSize(10f))
                    paragraph.setTextAlignment(TextAlignment.CENTER)
                }
                document.add(paragraph)
                continue
            }

            // Extract and apply formatting
            val formattedText = parseFormattedLine(currentLine)

            if (formattedText.text.isNotEmpty()) {
                val textElement = Text(formattedText.text)
                    .setFont(if (formattedText.isBold) boldFont else regularFont)
                    .setFontSize(formattedText.fontSize)

                paragraph.add(textElement)
                paragraph.setTextAlignment(formattedText.alignment)

                document.add(paragraph)
            }
        }
    }

    private data class FormattedText(
        val text: String,
        val fontSize: Float,
        val isBold: Boolean,
        val alignment: TextAlignment
    )

    /**
     * Parse a single line and extract formatting information
     */
    private fun parseFormattedLine(line: String): FormattedText {
        var text = line
        var fontSize = 11f // default
        var isBold = false
        var alignment = TextAlignment.LEFT

        // Determine font size
        when {
            text.contains("<BIG>") -> {
                fontSize = 16f
                text = text.replace("<BIG>", "")
            }
            text.contains("<MEDIUM3>") -> {
                fontSize = 14f
                text = text.replace("<MEDIUM3>", "")
            }
            text.contains("<MEDIUM2>") -> {
                fontSize = 14f
                text = text.replace("<MEDIUM2>", "")
            }
            text.contains("<MEDIUM1>") -> {
                fontSize = 12f
                text = text.replace("<MEDIUM1>", "")
            }
            text.contains("<SMALL>") -> {
                fontSize = 10f
                text = text.replace("<SMALL>", "")
            }
        }

        // Determine alignment
        when {
            text.contains("<CENTER>") -> {
                alignment = TextAlignment.CENTER
                text = text.replace("<CENTER>", "")
            }
            text.contains("<RIGHT>") -> {
                alignment = TextAlignment.RIGHT
                text = text.replace("<RIGHT>", "")
            }
            text.contains("<LEFT>") -> {
                alignment = TextAlignment.LEFT
                text = text.replace("<LEFT>", "")
            }
        }

        // Determine if bold
        if (text.contains("<BOLD>") || text.contains("<MEDIUM2>") || text.contains("<MEDIUM3>")) {
            isBold = true
            text = text.replace("<BOLD>", "")
        }

        // Clean remaining tags
        text = cleanAllTags(text)

        return FormattedText(
            text = text,
            fontSize = fontSize,
            isBold = isBold,
            alignment = alignment
        )
    }

    /**
     * Clean all remaining HTML-like tags from text
     */
    private fun cleanAllTags(text: String): String {
        return text
            .replace("<NORMAL>", "")
            .replace("<UNDERLINE>", "")
            .replace("<LINE>", "")
            .replace("<DLINE>", "")
            .replace("<LINE0>", "")
            .replace("<DLINE0>", "")
            .replace("<CUT>", "")
            .replace("<AWAKE>", "")
            .replace("<LOGO>", "")
            .replace("<LOGO2>", "")
            .replace("<INVERSE>", "")
            .replace("<DRAWER>", "")
            .replace("<COMMAND>", "")
            // Remove any remaining tags with regex pattern
            .replace(Regex("<[^>]*>"), "")
            .trim()
    }

    /**
     * Extract text between formatting tags (keeping old method for compatibility)
     */
    private fun extractTextBetweenTags(text: String, startTag: String, endTag: String): String {
        val startIndex = text.indexOf(startTag)
        if (startIndex == -1) return text

        val textAfterStart = text.substring(startIndex + startTag.length)

        if (endTag.isEmpty()) {
            return textAfterStart.trim()
        }

        val endIndex = textAfterStart.indexOf(endTag)
        return if (endIndex != -1) {
            textAfterStart.substring(0, endIndex).trim()
        } else {
            textAfterStart.trim()
        }
    }

    /**
     * Upload PDF file to Firebase Storage
     */
    private suspend fun uploadToFirebaseStorage(localFile: File, fileName: String): String {
        return try {
            val fileRef = storageRef.child(fileName)
            val uploadTask = fileRef.putFile(android.net.Uri.fromFile(localFile))

            uploadTask.await()
            val downloadUrl = fileRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload to Firebase: ${e.message}")
        }
    }
}
