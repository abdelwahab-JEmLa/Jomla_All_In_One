package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B8

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFileNamingUtils
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
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
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null) {
            Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show()
            return
        }

        if (activeBonVent == null) {
            Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
            return
        }

        if (activeVents.isEmpty()) {
            Toast.makeText(context, "Aucun article à traiter", Toast.LENGTH_SHORT).show()
            return
        }

        // Close dropdown immediately
        onDismissDropdown()

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
    try {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null || activeBonVent == null) {
            onLoadingChange(false)
            return@withContext
        }

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
        }

        delay(300)

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

        result?.onSuccess { message ->
            // Extract the temporary PDF file path
            val tempFilePath = message.substringAfter("PDF saved: ").substringBefore("\n")
            val tempPdfFile = File(tempFilePath)

            if (tempPdfFile.exists()) {
                // Create the bons directory in downloads
                val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                val bonsDir = File(downloadsDir, "bons")

                if (!bonsDir.exists()) {
                    bonsDir.mkdirs()
                }

                // Generate filename using bon vent keyID
                val clientName = activeClient.nom.ifEmpty { "Client" }
                val productLineCount = activeVents.size
                val fileName = PdfFileNamingUtils.generatePdfFileName(clientName, productLineCount)

                // Alternative: Use keyID as filename
                val finalFileName = "${activeBonVent.keyID}.pdf"
                val finalPdfFile = File(bonsDir, finalFileName)

                // Copy and replace if exists
                tempPdfFile.copyTo(finalPdfFile, overwrite = true)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "PDF créé: ${finalPdfFile.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Delete temp file if it's different from final location
                if (tempPdfFile.absolutePath != finalPdfFile.absolutePath) {
                    tempPdfFile.delete()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur: Fichier PDF temporaire introuvable",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        result?.onFailure { error ->
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur lors de la génération du PDF: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "Erreur lors de la création du PDF: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
        e.printStackTrace()
    } finally {
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
        }

        withContext(Dispatchers.Main) {
            onLoadingChange(false)
        }
    }
}
