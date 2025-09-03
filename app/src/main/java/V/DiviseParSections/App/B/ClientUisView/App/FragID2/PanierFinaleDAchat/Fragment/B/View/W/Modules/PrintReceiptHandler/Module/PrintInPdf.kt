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

class PrintInPdfHandler {

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
     * Parse the HTML-like formatted text and convert to PDF elements
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
            var currentLine = line

            // Process formatting tags
            when {
                currentLine.contains("<BIG><CENTER>") -> {
                    val text = extractTextBetweenTags(currentLine, "<BIG><CENTER>", "")
                    paragraph.add(Text(text).setFont(boldFont).setFontSize(16f))
                    paragraph.setTextAlignment(TextAlignment.CENTER)
                }
                currentLine.contains("<SMALL><CENTER>") -> {
                    val text = extractTextBetweenTags(currentLine, "<SMALL><CENTER>", "")
                    paragraph.add(Text(text).setFont(regularFont).setFontSize(10f))
                    paragraph.setTextAlignment(TextAlignment.CENTER)
                }
                currentLine.contains("<MEDIUM1><CENTER>") -> {
                    val text = extractTextBetweenTags(currentLine, "<MEDIUM1><CENTER>", "")
                    paragraph.add(Text(text).setFont(regularFont).setFontSize(12f))
                    paragraph.setTextAlignment(TextAlignment.CENTER)
                }
                currentLine.contains("<MEDIUM2><CENTER>") -> {
                    val text = extractTextBetweenTags(currentLine, "<MEDIUM2><CENTER>", "")
                    paragraph.add(Text(text).setFont(boldFont).setFontSize(14f))
                    paragraph.setTextAlignment(TextAlignment.CENTER)
                }
                currentLine.contains("<MEDIUM3><CENTER>") || currentLine.contains("<MEDIUM3><RIGHT>") -> {
                    val text = extractTextBetweenTags(currentLine, "<MEDIUM3>", "")
                        .replace("<BOLD>", "").replace("<CENTER>", "").replace("<RIGHT>", "")
                    paragraph.add(Text(text).setFont(boldFont).setFontSize(14f))
                    if (currentLine.contains("<RIGHT>")) {
                        paragraph.setTextAlignment(TextAlignment.RIGHT)
                    } else {
                        paragraph.setTextAlignment(TextAlignment.CENTER)
                    }
                }
                currentLine.contains("<MEDIUM1><LEFT>") -> {
                    val text = extractTextBetweenTags(currentLine, "<MEDIUM1><LEFT>", "")
                    paragraph.add(Text(text).setFont(regularFont).setFontSize(12f))
                    paragraph.setTextAlignment(TextAlignment.LEFT)
                }
                currentLine.contains("<SMALL><LEFT>") -> {
                    val text = extractTextBetweenTags(currentLine, "<SMALL><LEFT>", "")
                    paragraph.add(Text(text).setFont(regularFont).setFontSize(10f))
                    paragraph.setTextAlignment(TextAlignment.LEFT)
                }
                currentLine.contains("<SMALL><BOLD>") -> {
                    val text = extractTextBetweenTags(currentLine, "<SMALL><BOLD>", "<NORMAL>")
                    paragraph.add(Text(text).setFont(boldFont).setFontSize(10f))
                    paragraph.setTextAlignment(TextAlignment.LEFT)
                }
                currentLine.contains("=====") || currentLine.contains("-----") -> {
                    // Separator lines
                    paragraph.add(Text(currentLine.replace("<LEFT><NORMAL><MEDIUM1>", "")).setFont(regularFont).setFontSize(12f))
                    paragraph.setTextAlignment(TextAlignment.CENTER)
                }
                else -> {
                    // Default formatting
                    val cleanText = currentLine
                        .replace("<LEFT><NORMAL><MEDIUM1>", "")
                        .replace("<NORMAL>", "")
                        .replace("<MEDIUM1>", "")
                        .replace("<LEFT>", "")
                        .trim()

                    if (cleanText.isNotEmpty()) {
                        paragraph.add(Text(cleanText).setFont(regularFont).setFontSize(11f))
                        paragraph.setTextAlignment(TextAlignment.LEFT)
                    }
                }
            }

            document.add(paragraph)
        }
    }

    /**
     * Extract text between formatting tags
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

    /**
     * Simple method to create a basic PDF receipt
     */
    suspend fun createSimpleReceipt(
        context: Context,
        title: String,
        items: List<String>,
        total: String,
        clientName: String = "Client"
    ): Result<String> {
        return try {
            val receiptText = buildString {
                append("<BIG><CENTER>$title<BR>")
                append("<SMALL><CENTER>Abdelwahab JeMla.Com<BR>")
                append("<SMALL><CENTER>0553885037<BR>")
                append("<BR>")
                append("<MEDIUM1><LEFT>Client: $clientName<BR>")
                append("<SMALL><LEFT>Date: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}<BR>")
                append("<BR>")
                append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")

                items.forEach { item ->
                    append("<SMALL><LEFT>$item<BR>")
                }

                append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
                append("<MEDIUM2><CENTER>Total: $total<BR>")
                append("<BR><BR><BR>>")
            }

            generateAndStorePdfReceipt(context, receiptText, clientName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
