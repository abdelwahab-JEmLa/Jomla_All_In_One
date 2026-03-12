package V.DiviseParSections.App._0.Navigation.Buttons_Gps

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
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
            if (!sourceFile.exists()) {
                throw IllegalStateException("Source file does not exist: ${sourceFile.absolutePath}")
            }

            Log.d(TAG, "📁 Saving PDF: $fileName to $subFolder")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savePdfViaMediaStore(context, sourceFile, fileName, subFolder)
            } else {
                savePdfToAppDirectory(context, sourceFile, fileName, subFolder)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error saving PDF: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Returns a share-ready content URI for the most recently saved PDF in [subFolder].
     *
     * On Android 10+ queries MediaStore (Downloads collection) ordered by DATE_ADDED DESC.
     * On older versions scans the app-specific external Documents directory for the newest file.
     *
     * Returns null when no PDF is found.
     */
    fun getLatestPdfUri(context: Context, subFolder: String = "BonsDeVente"): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getLatestPdfUri_MediaStore(context, subFolder)
        } else {
            getLatestPdfUri_AppDirectory(context, subFolder)
        }
    }

    // ── Android 10+ ─────────────────────────────────────────────────────────────

    /**
     * Query MediaStore Downloads for the newest PDF whose RELATIVE_PATH starts with
     * "Download/<subFolder>/".  Returns a content:// URI directly usable for sharing.
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun getLatestPdfUri_MediaStore(context: Context, subFolder: String): Uri? {
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // RELATIVE_PATH is stored as "Download/BonsWhatsApp/MM_DD/" so we use LIKE
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? " +
                "AND ${MediaStore.MediaColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf(
            "${Environment.DIRECTORY_DOWNLOADS}/$subFolder/%",
            "application/pdf"
        )
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        return try {
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID),
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    Uri.withAppendedPath(collection, id.toString())
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ getLatestPdfUri_MediaStore failed: ${e.message}", e)
            null
        }
    }

    // ── Android < 10 ────────────────────────────────────────────────────────────

    /**
     * Scan the app-specific Documents/$subFolder directory for the most recently
     * modified PDF and return a FileProvider content:// URI so other apps can read it.
     *
     * Requires a FileProvider entry in AndroidManifest pointing to
     * getExternalFilesDir(DIRECTORY_DOCUMENTS).
     */
    private fun getLatestPdfUri_AppDirectory(context: Context, subFolder: String): Uri? {
        return try {
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: return null
            val subDir = File(documentsDir, subFolder)
            if (!subDir.exists()) return null

            // Walk all date sub-folders and collect PDF files
            val latestFile = subDir
                .walkTopDown()
                .filter { it.isFile && it.extension.equals("pdf", ignoreCase = true) }
                .maxByOrNull { it.lastModified() }
                ?: return null

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                latestFile
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ getLatestPdfUri_AppDirectory failed: ${e.message}", e)
            null
        }
    }

    // ── Internal save helpers (unchanged) ───────────────────────────────────────

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

            val currentDate = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
            val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/$subFolder/$currentDate"

            Log.d(TAG, "📂 MediaStore path: $relativePath/$fileName")

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }

            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            resolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?",
                arrayOf(fileName, "$relativePath/"),
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val deleteUri = Uri.withAppendedPath(collection, id.toString())
                    resolver.delete(deleteUri, null, null)
                    Log.d(TAG, "🗑️ Deleted existing file from MediaStore")
                }
            }

            val uri = resolver.insert(collection, contentValues)
                ?: throw IllegalStateException("Failed to create MediaStore entry")

            Log.d(TAG, "📝 MediaStore URI created: $uri")

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
            savePdfToAppDirectory(context, sourceFile, fileName, subFolder)
        }
    }

    private fun savePdfToAppDirectory(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String
    ): Result<String> {
        return try {
            val currentDate = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())

            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: throw IllegalStateException("Cannot access external files directory")

            val targetDir = File(documentsDir, "$subFolder/$currentDate")

            Log.d(TAG, "📂 App directory path: ${targetDir.absolutePath}")

            if (!targetDir.exists()) {
                val created = targetDir.mkdirs()
                if (!created && !targetDir.exists()) {
                    throw IllegalStateException("Failed to create directory: ${targetDir.absolutePath}")
                }
                Log.d(TAG, "✅ Directory created: ${targetDir.absolutePath}")
            }

            if (!targetDir.canWrite()) {
                throw IllegalStateException("Directory is not writable: ${targetDir.absolutePath}")
            }

            val destinationFile = File(targetDir, fileName)

            if (destinationFile.exists()) {
                destinationFile.delete()
                Log.d(TAG, "🗑️ Deleted existing file: ${destinationFile.name}")
            }

            var bytesCopied = 0L
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    bytesCopied = input.copyTo(output)
                }
            }

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

    /** User-friendly description of where PDFs are saved */
    fun getSaveLocationDescription(context: Context, subFolder: String = "BonsDeVente"): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "PDFs sauvegardés dans Téléchargements/$subFolder/MM_DD/ (accessible via Gestionnaire de fichiers)"
        } else {
            "PDFs sauvegardés dans les documents de l'application/$subFolder/MM_DD/"
        }
    }

    /** Current date folder name in MM_DD format */
    fun getCurrentDateFolder(): String {
        return SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
    }
}
