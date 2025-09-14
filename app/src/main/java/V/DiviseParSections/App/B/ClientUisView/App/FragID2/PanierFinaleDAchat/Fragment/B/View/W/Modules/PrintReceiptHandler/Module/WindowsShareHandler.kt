package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * Handles sharing PDF files specifically with Windows-compatible applications
 * This addresses TODO(1) by providing direct sharing functionality with Windows apps
 */
class WindowsShareHandler {
    
    companion object {
        private const val TAG = "WindowsShareHandler"
        
        // Known Windows-compatible apps that can handle PDFs
        private val WINDOWS_COMPATIBLE_APPS = mapOf(
            "com.microsoft.office.outlook" to "Microsoft Outlook",
            "com.microsoft.teams" to "Microsoft Teams", 
            "com.microsoft.skydrive" to "Microsoft OneDrive",
            "com.dropbox.android" to "Dropbox",
            "com.google.android.apps.docs" to "Google Drive",
            "com.adobe.reader" to "Adobe Acrobat Reader",
            "com.microsoft.office.word" to "Microsoft Word",
            "com.google.android.gm" to "Gmail",
            "com.whatsapp" to "WhatsApp",
            "com.telegram.messenger" to "Telegram"
        )
    }

    /**
     * Share PDF file with Windows-compatible applications
     */
    fun shareWithWindowsApps(context: Context, pdfFile: File) {
        try {
            if (!pdfFile.exists()) {
                Log.e(TAG, "PDF file does not exist: ${pdfFile.absolutePath}")
                return
            }

            val uri = createFileUri(context, pdfFile)
            if (uri == null) {
                Log.e(TAG, "Failed to create URI for file: ${pdfFile.absolutePath}")
                return
            }

            // Try to find the best available Windows-compatible app
            val availableApp = findBestWindowsApp(context)
            
            if (availableApp != null) {
                shareWithSpecificApp(context, uri, pdfFile, availableApp)
            } else {
                // Fallback to general sharing with preference for Office/productivity apps
                shareWithPreferredApps(context, uri, pdfFile)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing with Windows apps", e)
            // Ultimate fallback - use system sharing
            shareWithSystemChooser(context, pdfFile)
        }
    }

    /**
     * Create file URI safely
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
     * Find the best available Windows-compatible app
     */
    private fun findBestWindowsApp(context: Context): String? {
        val packageManager = context.packageManager
        
        // Priority order: Office apps first, then cloud storage, then communication
        val priorityOrder = listOf(
            "com.microsoft.office.outlook",
            "com.microsoft.teams",
            "com.microsoft.skydrive",
            "com.microsoft.office.word",
            "com.google.android.apps.docs",
            "com.dropbox.android",
            "com.adobe.reader",
            "com.google.android.gm"
        )
        
        for (packageName in priorityOrder) {
            if (isAppInstalled(packageManager, packageName)) {
                Log.i(TAG, "Found Windows-compatible app: ${WINDOWS_COMPATIBLE_APPS[packageName]}")
                return packageName
            }
        }
        
        return null
    }

    /**
     * Check if an app is installed
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
     * Share with a specific app
     */
    private fun shareWithSpecificApp(context: Context, uri: Uri, file: File, packageName: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
            putExtra(Intent.EXTRA_TEXT, "Veuillez trouver ci-joint le bon de vente en PDF.")
            setPackage(packageName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
            Log.i(TAG, "Successfully shared with ${WINDOWS_COMPATIBLE_APPS[packageName]}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share with $packageName", e)
            // Fallback to system chooser
            shareWithSystemChooser(context, file)
        }
    }

    /**
     * Share with preferred apps using chooser
     */
    private fun shareWithPreferredApps(context: Context, uri: Uri, file: File) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
            putExtra(Intent.EXTRA_TEXT, "Bon de vente généré automatiquement.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Create list of preferred apps that are installed
        val preferredIntents = mutableListOf<Intent>()
        val packageManager = context.packageManager

        WINDOWS_COMPATIBLE_APPS.keys.forEach { packageName ->
            if (isAppInstalled(packageManager, packageName)) {
                val targetIntent = Intent(shareIntent).apply {
                    setPackage(packageName)
                }
                preferredIntents.add(targetIntent)
            }
        }

        if (preferredIntents.isNotEmpty()) {
            val chooser = Intent.createChooser(
                preferredIntents.removeAt(0),
                "Partager avec application Windows"
            )
            
            if (preferredIntents.isNotEmpty()) {
                chooser.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    preferredIntents.toTypedArray()
                )
            }
            
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            try {
                context.startActivity(chooser)
                Log.i(TAG, "Launched Windows-compatible app chooser")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to launch preferred chooser", e)
                shareWithSystemChooser(context, file)
            }
        } else {
            shareWithSystemChooser(context, file)
        }
    }

    /**
     * Final fallback - use system sharing chooser
     */
    private fun shareWithSystemChooser(context: Context, file: File) {
        try {
            val uri = createFileUri(context, file)
            if (uri == null) return

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Bon de Vente - ${file.nameWithoutExtension}")
                putExtra(Intent.EXTRA_TEXT, "Bon de vente en pièce jointe.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Partager le bon de vente")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooser)
            Log.i(TAG, "Launched system sharing chooser")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch system chooser", e)
        }
    }

    /**
     * Get list of installed Windows-compatible apps for informational purposes
     */
    fun getInstalledWindowsApps(context: Context): List<String> {
        val packageManager = context.packageManager
        return WINDOWS_COMPATIBLE_APPS.filter { (packageName, _) ->
            isAppInstalled(packageManager, packageName)
        }.values.toList()
    }

    /**
     * Check if any Windows-compatible apps are installed
     */
    fun hasWindowsAppsInstalled(context: Context): Boolean {
        return getInstalledWindowsApps(context).isNotEmpty()
    }
}
