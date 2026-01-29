package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B8

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFileNamingUtils
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File

/**
 * FIXED: Background PDF creation without sharing or phone number dialogs
 *
 * Changes implemented:
 * 1. PDF creation happens in background thread
 * 2. Dropdown menu closes immediately on click
 * 3. PDF saved to: downloads/bons/{keyID_bonVent}.pdf
 * 4. Replaces existing file if present
 * 5. Removed WhatsApp sharing logic
 * 6. Removed phone number input dialog
 * 7. Uses proper file naming with PdfFileNamingUtils
 */

private const val TAG = "DropDownItem_FragFastVent_8"

@Composable
fun DropDownItem_WhenIts_FragFastVent_8(
    nomFun: String = "Créer PDF en arrière-plan",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    fun initiateBackgroundPdfCreation() {
        Log.d(TAG, "initiateBackgroundPdfCreation: Starting PDF creation process")

        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null) {
            Log.e(TAG, "initiateBackgroundPdfCreation: No active client found")
            Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show()
            return
        }

        if (activeBonVent == null) {
            Log.e(TAG, "initiateBackgroundPdfCreation: No active bon de vente found")
            Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
            return
        }

        if (activeVents.isEmpty()) {
            Log.e(TAG, "initiateBackgroundPdfCreation: No active vents to process")
            Toast.makeText(context, "Aucun article à traiter", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i(TAG, "initiateBackgroundPdfCreation: Validated - Client: ${activeClient.nom}, BonVent: ${activeBonVent.keyID}, Items: ${activeVents.size}")

        // Close dropdown immediately
        onDismissDropdown()
        Log.d(TAG, "initiateBackgroundPdfCreation: Dropdown dismissed")

        // Start background PDF creation
        isLoading = true
        scope.launch {
            createPdfInBackground(
                context = context,
                scope = scope,
                aCentralFacade = aCentralFacade,
                focusedValuesGetter = focusedValuesGetter,
                printHandler = printHandler,
                activeVents = activeVents,
                onLoadingChange = { isLoading = it }
            )
        }
        Log.d(TAG, "initiateBackgroundPdfCreation: Background task launched")
    }

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
                    initiateBackgroundPdfCreation()
                }
            },
            enabled = !isLoading
        )
    }
}

/**
 * Creates PDF in background and saves to downloads/bons/{keyID}.pdf
 */
private suspend fun createPdfInBackground(
    context: Context,
    scope: CoroutineScope,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    printHandler: Any,
    activeVents: List<M10OperationVentCouleur>,
    onLoadingChange: (Boolean) -> Unit
) = withContext(Dispatchers.IO) {
    Log.d(TAG, "createPdfInBackground: Starting on background thread")

    try {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null || activeBonVent == null) {
            Log.e(TAG, "createPdfInBackground: Missing data - Client: ${activeClient != null}, BonVent: ${activeBonVent != null}")
            onLoadingChange(false)
            return@withContext
        }

        Log.i(TAG, "createPdfInBackground: Processing - Client: ${activeClient.nom}, BonVent: ${activeBonVent.keyID}")

        // Update bons_a_imprime_avec_image_produit to include current bon
        val currentValues = focusedValuesGetter.active_Central_Values
        val currentBonsWithImages = currentValues.bons_a_imprime_avec_image_produit.toMutableList()

        if (!currentBonsWithImages.any { it.keyID == activeBonVent.keyID }) {
            currentBonsWithImages.add(activeBonVent)

            focusedValuesGetter.update_activeCentralValues(
                currentValues.copy(
                    bons_a_imprime_avec_image_produit = currentBonsWithImages
                )
            )
            Log.d(TAG, "createPdfInBackground: Added bon to image print list - KeyID: ${activeBonVent.keyID}")
        } else {
            Log.d(TAG, "createPdfInBackground: Bon already in image print list - KeyID: ${activeBonVent.keyID}")
        }

        delay(300)
        Log.d(TAG, "createPdfInBackground: Starting PDF generation...")

        // Generate PDF with images
        val result = (printHandler as? PrintReceiptHandler_Juil)
            ?.printPdfOnly(
                context = context,
                repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                scope = scope,
                relative_ListM10OperationVentCouleur = activeVents,
                relative_bonVent = activeBonVent,
                client = activeClient,
                showCreditSection = false,
                versement = 0.0
            )

        Log.d(TAG, "createPdfInBackground: PDF generation completed - Result: ${result != null}")

        result?.onSuccess { message ->
            Log.i(TAG, "createPdfInBackground: PDF generation SUCCESS - Message: $message")

            // Extract the temporary PDF file path
            val tempFilePath = message.substringAfter("PDF saved: ").substringBefore("\n")
            val tempPdfFile = File(tempFilePath)

            Log.d(TAG, "createPdfInBackground: Temp file path: $tempFilePath, Exists: ${tempPdfFile.exists()}")

            if (tempPdfFile.exists()) {
                // Create the bons directory in downloads
                val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                val bonsDir = File(downloadsDir, "bons")

                if (!bonsDir.exists()) {
                    val created = bonsDir.mkdirs()
                    Log.d(TAG, "createPdfInBackground: Bons directory created: $created - Path: ${bonsDir.absolutePath}")
                } else {
                    Log.d(TAG, "createPdfInBackground: Bons directory already exists - Path: ${bonsDir.absolutePath}")
                }

                // Generate filename using bon vent keyID
                val clientName = activeClient.nom.ifEmpty { "Client" }
                val productLineCount = activeVents.size
                val fileName = PdfFileNamingUtils.generatePdfFileName(clientName, productLineCount)

                // Alternative: Use keyID as filename
                val finalFileName = "${activeBonVent.keyID}.pdf"
                val finalPdfFile = File(bonsDir, finalFileName)

                Log.i(TAG, "createPdfInBackground: Copying PDF - From: ${tempPdfFile.name} To: ${finalPdfFile.absolutePath}")

                // Copy and replace if exists
                tempPdfFile.copyTo(finalPdfFile, overwrite = true)

                Log.i(TAG, "createPdfInBackground: PDF copied successfully - Size: ${finalPdfFile.length()} bytes")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "✅ PDF créé avec succès!\n${finalPdfFile.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Delete temp file if it's different from final location
                if (tempPdfFile.absolutePath != finalPdfFile.absolutePath) {
                    val deleted = tempPdfFile.delete()
                    Log.d(TAG, "createPdfInBackground: Temp file deleted: $deleted - Path: ${tempPdfFile.absolutePath}")
                }
            } else {
                Log.e(TAG, "createPdfInBackground: Temp PDF file not found - Path: $tempFilePath")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "❌ Erreur: Fichier PDF introuvable",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        result?.onFailure { error ->
            Log.e(TAG, "createPdfInBackground: PDF generation FAILED - Error: ${error.message}", error)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "❌ Erreur génération PDF:\n${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    } catch (e: Exception) {
        Log.e(TAG, "createPdfInBackground: Exception occurred", e)

        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "❌ Erreur création PDF:\n${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
        e.printStackTrace()
    } finally {
        Log.d(TAG, "createPdfInBackground: Cleanup starting...")

        delay(500)

        // Clean up bons_a_imprime_avec_image_produit
        val finalValues = focusedValuesGetter.active_Central_Values
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        activeBonVent?.let { bon ->
            val cleanedBons = finalValues.bons_a_imprime_avec_image_produit
                .filter { it.keyID != bon.keyID }

            focusedValuesGetter.update_activeCentralValues(
                finalValues.copy(
                    bons_a_imprime_avec_image_produit = cleanedBons
                )
            )

            Log.d(TAG, "createPdfInBackground: Removed bon from image print list - KeyID: ${bon.keyID}")
        }

        withContext(Dispatchers.Main) {
            onLoadingChange(false)
        }

        Log.d(TAG, "createPdfInBackground: Cleanup completed, loading state reset")
    }
}
