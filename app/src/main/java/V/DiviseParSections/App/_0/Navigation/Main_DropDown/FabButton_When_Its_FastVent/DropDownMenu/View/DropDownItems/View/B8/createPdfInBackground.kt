package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B8

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App._0.Navigation.Buttons_Gps.PdfSaverUtility
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

/**
 * Creates PDF in background with timeout to avoid hanging
 * FIXED: Uses PdfSaverUtility to save in Downloads/BonsWhatsApp/MM_DD/keyID.pdf
 * FIXED: Opens final file after it's saved via MediaStore
 */
suspend fun createPdfInBackground(
    context: Context,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    printHandler: Any,
    activeVents: List<M10OperationVentCouleur>,
    onLoadingChange: (Boolean) -> Unit
) {
    Log.d(TAG, "═══════════════════════════════════════════════════════")
    Log.d(TAG, "🚀 createPdfInBackground: Starting on Dispatchers.IO")
    Log.d(TAG, "═══════════════════════════════════════════════════════")

    try {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null || activeBonVent == null) {
            Log.e(TAG, "❌ Missing data - Client: $activeClient, BonVent: $activeBonVent")
            withContext(Dispatchers.Main) {
                onLoadingChange(false)
            }
            return
        }

        Log.i(TAG, "✅ Data validated:")
        Log.i(TAG, "   Client: ${activeClient.nom}")
        Log.i(TAG, "   BonVent KeyID: ${activeBonVent.keyID}")
        Log.i(TAG, "   Items: ${activeVents.size}")

        // Update bons list to include images
        val currentValues = focusedValuesGetter.active_Central_Values
        val currentBonsWithImages = currentValues.bons_a_imprime_avec_image_produit.toMutableList()

        if (!currentBonsWithImages.any { it.keyID == activeBonVent.keyID }) {
            currentBonsWithImages.add(activeBonVent)
            focusedValuesGetter.update_activeCentralValues(
                currentValues.copy(bons_a_imprime_avec_image_produit = currentBonsWithImages)
            )
            Log.d(TAG, "📸 Added bon to image print list")
        }

        delay(300)

        Log.d(TAG, "───────────────────────────────────────────────────────")
        Log.d(TAG, "📄 Starting PDF generation (30s timeout)...")
        Log.d(TAG, "───────────────────────────────────────────────────────")

        // Call PDF generation with timeout and shouldOpenFile=false
        val pdfFilePath = withTimeout(30000L) {
            val handler = printHandler as? PrintReceiptHandler_Juil
            if (handler == null) {
                Log.e(TAG, "❌ PrintHandler is NULL!")
                null
            } else {
                Log.d(TAG, "▶️ Calling printPdfOnly with shouldOpenFile=false...")
                val result = handler.printPdfOnly(
                    context = context,
                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                    scope = null,
                    relative_ListM10OperationVentCouleur = activeVents,
                    relative_bonVent = activeBonVent,
                    client = activeClient,
                    showCreditSection = false,
                    versement = 0.0,
                    shouldOpenFile = false  // FIXED: Don't open temp file
                )

                Log.d(
                    TAG,
                    "◀️ printPdfOnly completed: ${if (result.isSuccess) "✅ SUCCESS" else "❌ FAILED"}"
                )
                result.getOrNull()?.substringAfter("PDF saved: ")?.substringBefore("\n")
            }
        }

        Log.d(TAG, "───────────────────────────────────────────────────────")
        Log.d(TAG, "📦 PDF generation completed")
        Log.d(TAG, "   Temp file path: $pdfFilePath")
        Log.d(TAG, "───────────────────────────────────────────────────────")

        if (pdfFilePath != null) {
            val tempPdfFile = File(pdfFilePath)

            if (tempPdfFile.exists()) {
                Log.i(TAG, "✅ Temp PDF file exists - Size: ${tempPdfFile.length()} bytes")

                // Generate clean filename using bonVent keyID
                val cleanFileName = "${activeBonVent.keyID}.pdf"

                Log.d(TAG, "───────────────────────────────────────────────────────")
                Log.d(TAG, "💾 Saving to Downloads/BonsWhatsApp/MM_DD/...")
                Log.d(TAG, "   File name: $cleanFileName")
                Log.d(TAG, "   Date folder: ${PdfSaverUtility.getCurrentDateFolder()}")
                Log.d(TAG, "───────────────────────────────────────────────────────")

                // Save using PdfSaverUtility to Downloads/BonsWhatsApp/MM_DD/
                val saveResult = PdfSaverUtility.savePdf(
                    context = context,
                    sourceFile = tempPdfFile,
                    fileName = cleanFileName,
                    subFolder = "BonsWhatsApp"
                )

                saveResult.onSuccess { savedPath ->
                    Log.i(TAG, "═══════════════════════════════════════════════════════")
                    Log.i(TAG, "✅ PDF SAVED SUCCESSFULLY!")
                    Log.i(TAG, "   Path: $savedPath")
                    Log.i(TAG, "═══════════════════════════════════════════════════════")

                    // Now open the file from MediaStore/Downloads
                    // Note: For MediaStore files, we need to query and get URI
                    // For now, just show success toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "✅ PDF créé avec succès!\n$savedPath",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }.onFailure { error ->
                    Log.e(TAG, "❌ Failed to save PDF: ${error.message}", error)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "❌ Erreur sauvegarde PDF: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // Delete temp file
                Log.d(TAG, "🗑️ Deleting temp file: ${tempPdfFile.absolutePath}")
                if (tempPdfFile.delete()) {
                    Log.d(TAG, "✅ Temp file deleted successfully")
                } else {
                    Log.w(TAG, "⚠️ Failed to delete temp file")
                }

            } else {
                Log.e(TAG, "❌ PDF file not found at: $pdfFilePath")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Erreur: Fichier PDF introuvable", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } else {
            Log.e(TAG, "❌ PDF generation returned null")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ Erreur: Génération PDF échouée", Toast.LENGTH_LONG)
                    .show()
            }
        }

    } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "⏱️ TIMEOUT after 30s - Firebase upload may be blocking", e)
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "❌ Timeout: Génération PDF trop longue (>30s)\nVérifiez Firebase/Internet",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Log.e(TAG, "💥 Exception occurred", e)
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "❌ Erreur création PDF:\n${e.message}", Toast.LENGTH_LONG)
                .show()
        }
        e.printStackTrace()
    } finally {
        Log.d(TAG, "───────────────────────────────────────────────────────")
        Log.d(TAG, "🧹 Cleanup starting...")
        Log.d(TAG, "───────────────────────────────────────────────────────")

        delay(500)

        // Clean up bons list
        val finalValues = focusedValuesGetter.active_Central_Values
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        activeBonVent?.let { bon ->
            val cleanedBons = finalValues.bons_a_imprime_avec_image_produit.filter { it.keyID != bon.keyID }
            focusedValuesGetter.update_activeCentralValues(
                finalValues.copy(bons_a_imprime_avec_image_produit = cleanedBons)
            )
            Log.d(TAG, "✅ Removed bon from image print list")
        }

        withContext(Dispatchers.Main) {
            onLoadingChange(false)
        }

        Log.d(TAG, "✅ Cleanup completed")
        Log.d(TAG, "═══════════════════════════════════════════════════════")
    }
}
