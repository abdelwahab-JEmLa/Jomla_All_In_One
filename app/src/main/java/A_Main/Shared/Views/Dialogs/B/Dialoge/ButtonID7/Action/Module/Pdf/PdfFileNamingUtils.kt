package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for consistent PDF file naming across the application
 */
object PdfFileNamingUtils_Mai {

    /**
     * Generate a PDF filename based on client name and product line count
     * Uses Arabic suffixes for page indicators
     * 
     * @param clientName The name of the client
     * @param productLineCount Number of product lines to determine page count
     * @return Formatted filename with page suffix
     */
    fun generatePdfFileName(clientName: String, productLineCount: Int): String {
        // Sanitize client name (remove special characters, replace spaces with underscores)
        val sanitizedClientName = clientName.replace(" ", "_")
        
        // Determine the page suffix based on product line count
        val pageSuffix = when {
            productLineCount > 36 -> "_3صفحات"  // 3 pages
            productLineCount > 18 -> "_2صفحات"  // 2 pages
            else -> "_صفحة"                      // 1 page
        }
        
        return "${sanitizedClientName}${pageSuffix}.pdf"
    }

    /**
     * Generate a PDF filename for internal storage with timestamp
     * Used for temporary files and Firebase uploads
     * 
     * @param clientName The name of the client
     * @param type Type of document (receipt, credit, etc.)
     * @param id Transaction or document ID
     * @return Formatted filename with timestamp
     */
    fun generateInternalPdfFileName(clientName: String, type: String, id: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val sanitizedClientName = clientName.replace("[^a-zA-Z0-9]".toRegex(), "_")
        return "${type}_${sanitizedClientName}_${timestamp}_${id}.pdf"
    }

    /**
     * Generate a date-based folder path for organizing PDFs
     * 
     * @return Folder path in format "yyyy-MM-dd"
     */
    fun generateDateBasedFolderPath(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Sanitize a filename by removing invalid characters
     * 
     * @param fileName Original filename
     * @return Sanitized filename safe for file system
     */
    fun sanitizeFileName(fileName: String): String {
        return fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
    }
}
