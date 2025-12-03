package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.File

class UploadHandler {
    private val storageRef = Firebase.storage.reference.child("bonVents_pdf")

    companion object {
        private const val TAG = "UploadHandler"
    }

    /**
     * Create local file for PDF generation
     * FIXED: Now uses PdfFileNamingUtils for consistent naming across the application
     */
    fun createLocalFile(context: Context, clientName: String, type: String, id: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bonVents_pdf")
        if (!dir.exists()) dir.mkdirs()

        // Use the naming utility for consistency (FIXED: TODO(1))
        val fileName = PdfFileNamingUtils.generateInternalPdfFileName(clientName, type, id)

        return File(dir, fileName)
    }

    suspend fun uploadToFirebaseStorage(file: File, fileName: String): String {
        val fileRef = storageRef.child(fileName)
        val uploadTask = fileRef.putFile(Uri.fromFile(file))
        uploadTask.await()
        return fileRef.downloadUrl.await().toString()
    }

    /**
     * Share the PDF document with external applications (Drive, Email, etc.)
     * Call this method after PDF generation to allow users to share the receipt
     */
    fun shareDocument(context: Context, file: File) {
        try {
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: ${file.absolutePath}")
                return
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Intent to share specifically with Google Drive if available
            val driveIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
                setPackage("com.google.android.apps.docs") // Google Drive package
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Check if Google Drive is installed
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
            // Fallback in case of error
            shareWithChooser(context, getUriSafely(context, file), file)
        }
    }

    /**
     * Share with Android application chooser
     */
    private fun shareWithChooser(context: Context, uri: Uri?, file: File) {
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

    /**
     * Get URI safely
     */
    private fun getUriSafely(context: Context, file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating URI", e)
            null
        }
    }
}
