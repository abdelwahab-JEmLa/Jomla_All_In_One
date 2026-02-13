package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B8

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.compose.koinInject
import java.io.File

private const val TAG = "DropDownItem_FragFastVent_8"

@Composable
fun DropDownItem_WhenIts_FragFastVent_8(
    nomFun: String = "Créer PDF en arrière-plan",
    onDismissDropdown: () -> Unit,
    onClick_to_initiateBackgroundPdfCreation: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    var isLoading by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) "Création PDF en cours..." else nomFun,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    onClick_to_initiateBackgroundPdfCreation()
                }
            },
            enabled = !isLoading
        )
    }
}

/**
 * Creates PDF in background with timeout to avoid hanging
 */
suspend fun createPdfInBackground(
    context: Context,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    printHandler: Any,
    activeVents: List<M10OperationVentCouleur>,
    onLoadingChange: (Boolean) -> Unit
) {
    Log.d(TAG, "createPdfInBackground: Starting on background thread (already on Dispatchers.IO)")

    try {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null || activeBonVent == null) {
            Log.e(TAG, "createPdfInBackground: Missing data")
            withContext(Dispatchers.Main) {
                onLoadingChange(false)
            }
            return
        }

        Log.i(TAG, "createPdfInBackground: Processing - Client: ${activeClient.nom}, BonVent: ${activeBonVent.keyID}")

        // Update bons list
        val currentValues = focusedValuesGetter.active_Central_Values
        val currentBonsWithImages = currentValues.bons_a_imprime_avec_image_produit.toMutableList()

        if (!currentBonsWithImages.any { it.keyID == activeBonVent.keyID }) {
            currentBonsWithImages.add(activeBonVent)
            focusedValuesGetter.update_activeCentralValues(
                currentValues.copy(bons_a_imprime_avec_image_produit = currentBonsWithImages)
            )
            Log.d(TAG, "createPdfInBackground: Added bon to image print list")
        }

        delay(300)
        Log.d(TAG, "createPdfInBackground: Starting PDF generation with 30s timeout...")

        // Call PDF generation with timeout to avoid hanging
        val pdfFilePath = withTimeout(30000L) {  // 30 second timeout
            val handler = printHandler as? PrintReceiptHandler_Juil
            if (handler == null) {
                Log.e(TAG, "createPdfInBackground: PrintHandler is NULL!")
                null
            } else {
                Log.d(TAG, "createPdfInBackground: Calling printPdfOnly...")
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
                    versement = 0.0
                )

                Log.d(TAG, "createPdfInBackground: printPdfOnly returned: ${result.isSuccess}")
                result.getOrNull()?.substringAfter("PDF saved: ")?.substringBefore("\n")
            }
        }

        Log.d(TAG, "createPdfInBackground: PDF generation completed - FilePath: $pdfFilePath")

        if (pdfFilePath != null) {
            val tempPdfFile = File(pdfFilePath)

            if (tempPdfFile.exists()) {
                // Create bons directory
                val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                val bonsDir = File(downloadsDir, "bons")

                if (!bonsDir.exists()) {
                    bonsDir.mkdirs()
                    Log.d(TAG, "createPdfInBackground: Bons directory created: ${bonsDir.absolutePath}")
                }

                // Generate filename
                val itemCount = activeVents.size
                val finalFileName = "${activeBonVent.keyID},nombre_item=${itemCount}.pdf"
                val finalPdfFile = File(bonsDir, finalFileName)

                Log.i(TAG, "createPdfInBackground: Copying PDF to: ${finalPdfFile.absolutePath}")

                // Copy file
                tempPdfFile.copyTo(finalPdfFile, overwrite = true)

                Log.i(TAG, "createPdfInBackground: PDF copied successfully - Size: ${finalPdfFile.length()} bytes")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "✅ PDF créé avec succès!\n${finalPdfFile.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Delete temp file if different
                if (tempPdfFile.absolutePath != finalPdfFile.absolutePath) {
                    tempPdfFile.delete()
                    Log.d(TAG, "createPdfInBackground: Temp file deleted")
                }
            } else {
                Log.e(TAG, "createPdfInBackground: PDF file not found at: $pdfFilePath")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Erreur: Fichier PDF introuvable", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Log.e(TAG, "createPdfInBackground: PDF generation returned null")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ Erreur: Génération PDF échouée", Toast.LENGTH_LONG).show()
            }
        }

    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
        Log.e(TAG, "createPdfInBackground: TIMEOUT after 30s - Firebase upload may be blocking", e)
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "❌ Timeout: Génération PDF trop longue (>30s)\nVérifiez Firebase/Internet",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Log.e(TAG, "createPdfInBackground: Exception occurred", e)
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "❌ Erreur création PDF:\n${e.message}", Toast.LENGTH_LONG).show()
        }
        e.printStackTrace()
    } finally {
        Log.d(TAG, "createPdfInBackground: Cleanup starting...")

        delay(500)

        // Clean up bons list
        val finalValues = focusedValuesGetter.active_Central_Values
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        activeBonVent?.let { bon ->
            val cleanedBons = finalValues.bons_a_imprime_avec_image_produit.filter { it.keyID != bon.keyID }
            focusedValuesGetter.update_activeCentralValues(
                finalValues.copy(bons_a_imprime_avec_image_produit = cleanedBons)
            )
            Log.d(TAG, "createPdfInBackground: Removed bon from image print list")
        }

        withContext(Dispatchers.Main) {
            onLoadingChange(false)
        }

        Log.d(TAG, "createPdfInBackground: Cleanup completed")
    }
}
