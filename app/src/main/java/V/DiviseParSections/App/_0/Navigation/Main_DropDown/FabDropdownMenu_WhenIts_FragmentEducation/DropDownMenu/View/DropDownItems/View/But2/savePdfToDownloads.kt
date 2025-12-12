package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFileNamingUtils
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Save PDF using the utility class
 * Automatically chooses the best method based on Android version
 */
fun savePdfToDownloads(context: Context, pdfFile: File, clientName: String, productLineCount: Int) {
    try {
        // Generate filename
        val fileName = PdfFileNamingUtils.generatePdfFileName(clientName, productLineCount)

        // Use utility to save (handles all the complexity)
        val result = PdfSaverUtility.savePdf(
            context = context,
            sourceFile = pdfFile,
            fileName = fileName,
            subFolder = "BonsDeVente"
        )

        result.onSuccess { savedPath ->
            Log.d("SavePDF", "✅ PDF saved successfully: $savedPath")
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "PDF sauvegardé: $fileName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        result.onFailure { error ->
            throw error
        }

    } catch (e: Exception) {
        Log.e("SavePDF", "❌ Error saving PDF: ${e.message}", e)
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "Erreur sauvegarde: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
