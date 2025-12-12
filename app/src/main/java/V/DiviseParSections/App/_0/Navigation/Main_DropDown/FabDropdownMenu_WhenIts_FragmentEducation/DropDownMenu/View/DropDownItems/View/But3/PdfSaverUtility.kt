package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But3

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
 * Utility class for saving PDFs and Word documents to accessible locations
 * Provides multiple strategies depending on Android version and permissions
 */
object PdfSaverUtility {
    private const val TAG = "FileSaverUtility"

    /**
     * Save PDF or Word document using the best available method
     * Returns the path where the file was saved
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

            // Determine MIME type based on file extension
            val mimeType = when {
                fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                fileName.endsWith(".docx", ignoreCase = true) -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                fileName.endsWith(".doc", ignoreCase = true) -> "application/msword"
                else -> "application/pdf" // default
            }

            // Try MediaStore first (Android 10+) - saves to Downloads and is visible to user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileViaMediaStore(context, sourceFile, fileName, subFolder, mimeType)
            } else {
                // Fallback to app-specific directory
                saveFileToAppDirectory(context, sourceFile, fileName, subFolder)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving file: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save file via MediaStore (Android 10+)
     * Saves to Downloads folder and is immediately visible to user
     * NO PERMISSIONS NEEDED
     */
    private fun saveFileViaMediaStore(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String,
        mimeType: String
    ): Result<String> {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return saveFileToAppDirectory(context, sourceFile, fileName, subFolder)
            }

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/$subFolder/$currentDate"

            // Prepare content values
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
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

            // Write file content
            resolver.openOutputStream(uri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IllegalStateException("Failed to open output stream")

            val savedPath = "Downloads/$subFolder/$currentDate/$fileName"
            Log.d(TAG, "✅ File saved via MediaStore: $savedPath")

            Result.success(savedPath)
        } catch (e: Exception) {
            Log.e(TAG, "❌ MediaStore save failed, falling back to app directory: ${e.message}")
            // Fallback to app directory
            saveFileToAppDirectory(context, sourceFile, fileName, subFolder)
        }
    }

    /**
     * Save file to app-specific directory
     * ALWAYS WORKS - No permissions needed
     * Located in: /Android/data/[package]/files/Documents/[subFolder]/[date]/
     */
    private fun saveFileToAppDirectory(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String
    ): Result<String> {
        return try {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Use app-specific Documents directory
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: throw IllegalStateException("Cannot access external files directory")

            val targetDir = File(documentsDir, "$subFolder/$currentDate")

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

            Log.d(TAG, "✅ File saved to app directory ($bytesCopied bytes): ${destinationFile.absolutePath}")

            Result.success(destinationFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "❌ App directory save failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get a user-friendly description of where files are saved
     */
    fun getSaveLocationDescription(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Fichiers sauvegardés dans le dossier Téléchargements (accessible via Gestionnaire de fichiers)"
        } else {
            "Fichiers sauvegardés dans les documents de l'application"
        }
    }
}
