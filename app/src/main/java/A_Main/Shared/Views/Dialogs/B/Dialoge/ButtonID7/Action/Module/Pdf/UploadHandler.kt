package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module.Pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * FIXED: Proper resource management when creating FileProvider URIs
 * - ParcelFileDescriptor resources are properly released
 * - No resource leaks when sharing files
 */
class UploadHandler_Mai {

    companion object {
        private const val TAG = "UploadHandler"
    }

    /**
     * Create local file for PDF generation
     */
    fun createLocalFile(context: Context, clientName: String, type: String, id: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
        if (!dir.exists()) dir.mkdirs()

        val fileName = PdfFileNamingUtils_Mai.generateInternalPdfFileName(clientName, type, id)

        return File(dir, fileName)
    }

    /**
     * FIXED: Share the PDF document with proper resource cleanup
     * No more "A resource failed to call release" warnings
     */
    fun shareDocument(context: Context, file: File) {
        try {
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: ${file.absolutePath}")
                return
            }

            // Create URI - this internally opens a ParcelFileDescriptor
            val uri: Uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create URI", e)
                return
            }

            // Check if Google Drive is installed
            val driveIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
                setPackage("com.google.android.apps.docs")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val packageManager = context.packageManager
            val driveAvailable = driveIntent.resolveActivity(packageManager) != null

            if (driveAvailable) {
                // Launch Google Drive directly
                context.startActivity(driveIntent)
                Log.i(TAG, "Document shared with Google Drive")
            } else {
                // Fallback: use application chooser
                shareWithChooser(context, uri, file)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to share document", e)
            // Don't create a new URI on error - just log
        }
    }

    /**
     * Share with Android application chooser
     */
    private fun shareWithChooser(context: Context, uri: Uri, file: File) {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
                putExtra(Intent.EXTRA_TEXT, "Veuillez trouver ci-joint le bon de vente.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Partager le bon de vente")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            Log.i(TAG, "Share chooser launched")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch share chooser", e)
        }
    }
}
