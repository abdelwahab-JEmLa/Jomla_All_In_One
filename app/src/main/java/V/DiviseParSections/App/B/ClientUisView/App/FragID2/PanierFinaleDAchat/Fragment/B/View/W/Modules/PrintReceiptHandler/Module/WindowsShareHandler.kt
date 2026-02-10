package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * FIXED: Proper resource management when sharing PDFs
 * - FileProvider URIs are reused instead of recreated
 * - No resource leaks from ParcelFileDescriptor
 */
class WindowsShareHandler {

    companion object {
        private const val TAG = "WindowsShareHandler_V2"

        private val SHARING_APPS = listOf(
            "com.microsoft.appmanager",
            "com.microsoft.office.outlook",
            "com.google.android.gm",
            "com.whatsapp",
            "com.telegram.messenger",
            "com.microsoft.teams",
            "com.dropbox.android",
            "com.microsoft.skydrive",
            "com.slack",
            "com.discord"
        )
    }

    /**
     * FIXED: Share PDF with focus on actual sharing apps
     * Reuses URI across all sharing attempts to avoid resource leaks
     */
    fun shareWithWindowsApps(context: Context, pdfFile: File) {
        try {
            if (!pdfFile.exists()) {
                Log.e(TAG, "PDF file does not exist: ${pdfFile.absolutePath}")
                return
            }

            // Create URI once and reuse it
            val uri = createFileUri(context, pdfFile)
            if (uri == null) {
                Log.e(TAG, "Failed to create URI for file: ${pdfFile.absolutePath}")
                return
            }

            // Try approaches in order, reusing the same URI
            val approaches = listOf(
                { shareAsAttachment(context, uri, pdfFile) },
                { shareWithGenericType(context, uri, pdfFile) },
                { shareWithTextFocus(context, uri, pdfFile) }
            )

            var success = false
            for ((index, approach) in approaches.withIndex()) {
                try {
                    approach()
                    Log.i(TAG, "Share approach ${index + 1} launched successfully")
                    success = true
                    break
                } catch (e: Exception) {
                    Log.w(TAG, "Share approach ${index + 1} failed: ${e.message}")
                }
            }

            if (!success) {
                Log.e(TAG, "All share approaches failed")
            }

            // Note: We don't need to explicitly release the URI
            // Android handles cleanup when the intent is completed

        } catch (e: Exception) {
            Log.e(TAG, "Error in shareWithWindowsApps", e)
        }
    }

    /**
     * Create file URI safely - call this ONCE per sharing session
     */
    private fun createFileUri(context: Context, file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create file URI", e)
            null
        }
    }

    /**
     * Approach 1: Share as email attachment style
     */
    private fun shareAsAttachment(context: Context, uri: Uri, file: File) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
            putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
            putExtra(Intent.EXTRA_TEXT, "Veuillez trouver ci-joint le bon de vente.\n\nCordialement")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Envoyer le bon de vente")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Approach 2: Share with generic type to avoid PDF viewers
     */
    private fun shareWithGenericType(context: Context, uri: Uri, file: File) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
            putExtra(Intent.EXTRA_TEXT, "📄 Document: Bon de vente\n" +
                    "📧 Prêt à être partagé\n\n" +
                    "Fichier joint: ${file.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Partager via:")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Approach 3: Focus on text sharing with attachment
     */
    private fun shareWithTextFocus(context: Context, uri: Uri, file: File) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"

            val shareText = """
                📋 BON DE VENTE
                ━━━━━━━━━━━━━━━━
                
                Document: ${file.nameWithoutExtension}
                Généré le: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}
                
                📎 Fichier PDF en pièce jointe
                
                ━━━━━━━━━━━━━━━━
                Merci pour votre confiance
            """.trimIndent()

            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Partager le document")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Alternative method: Force sharing apps only
     */
    fun shareOnlyWithSharingApps(context: Context, pdfFile: File) {
        try {
            // Create URI once
            val uri = createFileUri(context, pdfFile) ?: return

            val baseIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${pdfFile.nameWithoutExtension}")
                putExtra(Intent.EXTRA_TEXT, "Bon de vente en pièce jointe.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Create intents only for known sharing apps
            val sharingIntents = mutableListOf<Intent>()
            val pm = context.packageManager

            for (packageName in SHARING_APPS) {
                if (isAppInstalled(pm, packageName)) {
                    val targetIntent = Intent(baseIntent).apply {
                        setPackage(packageName)
                    }
                    sharingIntents.add(targetIntent)
                }
            }

            if (sharingIntents.isNotEmpty()) {
                val chooser = Intent.createChooser(
                    sharingIntents.removeAt(0),
                    "Apps de partage"
                )

                if (sharingIntents.isNotEmpty()) {
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, sharingIntents.toTypedArray())
                }

                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
                Log.i(TAG, "Launched sharing-only chooser")
            } else {
                // No sharing apps found, fall back to approach 1
                shareAsAttachment(context, uri, pdfFile)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in shareOnlyWithSharingApps", e)
        }
    }

    /**
     * Check if app is installed
     */
    private fun isAppInstalled(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Get available sharing apps
     */
    fun getAvailableSharingApps(context: Context): List<String> {
        val pm = context.packageManager
        return SHARING_APPS.filter { packageName ->
            isAppInstalled(pm, packageName)
        }
    }
}
