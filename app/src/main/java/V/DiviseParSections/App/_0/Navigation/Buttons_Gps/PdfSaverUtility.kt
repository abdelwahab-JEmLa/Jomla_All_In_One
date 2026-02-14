package V.DiviseParSections.App._0.Navigation.Buttons_Gps

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class for saving PDFs to accessible locations
 * Provides multiple strategies depending on Android version and permissions
 *
 * USAGE for BonsWhatsApp:
 * PdfSaverUtility.savePdf(
 *     context = context,
 *     sourceFile = tempPdfFile,
 *     fileName = "${bonVentKeyId}.pdf",
 *     subFolder = "BonsWhatsApp"
 * )
 *
 * Result: Downloads/BonsWhatsApp/MM_DD/keyID.pdf
 */
object PdfSaverUtility {
    private const val TAG = "PdfSaverUtility"

    /**
     * Save PDF using the best available method
     * Returns the path where the file was saved
     *
     * @param context Application context
     * @param sourceFile The PDF file to save
     * @param fileName Name for the saved file (e.g., "-OlSiYxYxkqGuoBrQ306.pdf")
     * @param subFolder Folder name in Downloads (e.g., "BonsWhatsApp")
     * @return Result with saved file path
     */
    fun savePdf(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String = "BonsDeVente"
    ): Result<String> {
        return try {
            // Validate source file
            if (!sourceFile.exists()) {
                throw IllegalStateException("Source file does not exist: ${sourceFile.absolutePath}")
            }

            Log.d(TAG, "📁 Saving PDF: $fileName to $subFolder")

            // Try MediaStore first (Android 10+) - saves to Downloads and is visible to user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savePdfViaMediaStore(context, sourceFile, fileName, subFolder)
            } else {
                // Fallback to app-specific directory
                savePdfToAppDirectory(context, sourceFile, fileName, subFolder)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error saving PDF: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save PDF via MediaStore (Android 10+)
     * Saves to Downloads/BonsWhatsApp/MM_DD/ folder and is immediately visible to user
     * NO PERMISSIONS NEEDED
     *
     * Structure: Downloads/BonsWhatsApp/02_14/-OlSiYxYxkqGuoBrQ306.pdf
     */
    private fun savePdfViaMediaStore(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String
    ): Result<String> {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return savePdfToAppDirectory(context, sourceFile, fileName, subFolder)
            }

            // Format: MM_DD (e.g., 02_14 for February 14)
            val currentDate = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
            val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/$subFolder/$currentDate"

            Log.d(TAG, "📂 MediaStore path: $relativePath/$fileName")

            // Prepare content values
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }

            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            // Check if file already exists and delete it
            resolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?",
                arrayOf(fileName, "$relativePath/"),
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val deleteUri = android.net.Uri.withAppendedPath(collection, id.toString())
                    resolver.delete(deleteUri, null, null)
                    Log.d(TAG, "🗑️ Deleted existing file from MediaStore")
                }
            }

            // Insert new file
            val uri = resolver.insert(collection, contentValues)
                ?: throw IllegalStateException("Failed to create MediaStore entry")

            Log.d(TAG, "📝 MediaStore URI created: $uri")

            // Write file content
            var bytesCopied = 0L
            resolver.openOutputStream(uri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    bytesCopied = inputStream.copyTo(outputStream)
                }
            } ?: throw IllegalStateException("Failed to open output stream")

            val savedPath = "Downloads/$subFolder/$currentDate/$fileName"
            Log.d(TAG, "✅ PDF saved via MediaStore ($bytesCopied bytes): $savedPath")

            Result.success(savedPath)
        } catch (e: Exception) {
            Log.e(TAG, "❌ MediaStore save failed, falling back to app directory: ${e.message}")
            e.printStackTrace()
            // Fallback to app directory
            savePdfToAppDirectory(context, sourceFile, fileName, subFolder)
        }
    }

    /**
     * Save PDF to app-specific directory
     * ALWAYS WORKS - No permissions needed
     * Located in: /Android/data/[package]/files/Documents/[subFolder]/[date]/
     *
     * Structure: /Android/data/.../files/Documents/BonsWhatsApp/02_14/-OlSiYxYxkqGuoBrQ306.pdf
     */
    private fun savePdfToAppDirectory(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String
    ): Result<String> {
        return try {
            // Format: MM_DD (e.g., 02_14 for February 14)
            val currentDate = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())

            // Use app-specific Documents directory
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: throw IllegalStateException("Cannot access external files directory")

            val targetDir = File(documentsDir, "$subFolder/$currentDate")

            Log.d(TAG, "📂 App directory path: ${targetDir.absolutePath}")

            // Create directory if needed
            if (!targetDir.exists()) {
                val created = targetDir.mkdirs()
                if (!created && !targetDir.exists()) {
                    throw IllegalStateException("Failed to create directory: ${targetDir.absolutePath}")
                }
                Log.d(TAG, "✅ Directory created: ${targetDir.absolutePath}")
            }

            // Verify directory is writable
            if (!targetDir.canWrite()) {
                throw IllegalStateException("Directory is not writable: ${targetDir.absolutePath}")
            }

            val destinationFile = File(targetDir, fileName)

            // Delete existing file if present
            if (destinationFile.exists()) {
                destinationFile.delete()
                Log.d(TAG, "🗑️ Deleted existing file: ${destinationFile.name}")
            }

            // Copy file
            var bytesCopied = 0L
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    bytesCopied = input.copyTo(output)
                }
            }

            // Verify write success
            if (!destinationFile.exists() || destinationFile.length() == 0L) {
                throw IllegalStateException("File was not written successfully")
            }

            Log.d(TAG, "✅ PDF saved to app directory ($bytesCopied bytes): ${destinationFile.absolutePath}")

            Result.success(destinationFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "❌ App directory save failed: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Get a user-friendly description of where PDFs are saved
     */
    fun getSaveLocationDescription(context: Context, subFolder: String = "BonsDeVente"): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "PDFs sauvegardés dans Téléchargements/$subFolder/MM_DD/ (accessible via Gestionnaire de fichiers)"
        } else {
            "PDFs sauvegardés dans les documents de l'application/$subFolder/MM_DD/"
        }
    }

    /**
     * Get current date folder name (MM_DD format)
     */
    fun getCurrentDateFolder(): String {
        return SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
    }
}
