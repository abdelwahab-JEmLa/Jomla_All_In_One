package V.DiviseParSections.App._0.Navigation.Buttons_Gps

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFileNamingUtils
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent.EtateActuellementEst
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@Composable
fun DropDownItem_3(
    nomFun: String = "Partager PDF",
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter

    // Récupérer les clients avec commandes confirmées
    val clients_avec_confirmed = repositorysMainGetter.repo2Client.datasValue
        .filter { cli ->
            repositorysMainGetter.repo8BonVent.datasValue.any {
                it.parent_M2Client_KeyID == cli.keyID &&
                        it.etateActuellementEst == EtateActuellementEst.A_COMMANDE_CONFIRME &&
                        it.parent_M14VentPeriod_KeyId == focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
            }
        }

    val bonVents_OnCommande_ou_Leurclients_avec_confirmed =
        repositorysMainGetter.repo8BonVent.datasValue.filter { bonVent ->
            bonVent.etateActuellementEst == EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT &&
                    bonVent.parent_M14VentPeriod_KeyId == focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID &&
                    clients_avec_confirmed.any { it.keyID == bonVent.parent_M2Client_KeyID }
        }

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

    fun shareWithWindows() {
        if (bonVents_OnCommande_ou_Leurclients_avec_confirmed.isEmpty()) {
            Toast.makeText(
                context,
                "Aucune commande confirmée à partager",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                var successCount = 0
                var errorCount = 0

                bonVents_OnCommande_ou_Leurclients_avec_confirmed.forEach { bonVent ->

                    val relative_ListM10OperationVentCouleur =
                        repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { vent ->
                            vent.parent_M8BonVent_KeyId == bonVent.keyID &&
                                    vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                                    vent.quantity > 0
                        }

                    if (relative_ListM10OperationVentCouleur.isEmpty()) {
                        return@forEach
                    }

                    // Count number of product lines for suffix
                    val productLineCount = relative_ListM10OperationVentCouleur
                        .groupBy { it.parent_M1Produit_KeyId }
                        .size

                    val client = repositorysMainGetter.repo2Client.datasValue.find {
                        it.keyID == bonVent.parent_M2Client_KeyID
                    }

                    // Generate PDF
                    val result = printHandler.printPdfOnly(
                        context = context,
                        repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
                        repoM1Produit = repositorysMainGetter.repo1ProduitInfos,
                        repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos,
                        scope = scope,
                        relative_ListM10OperationVentCouleur = relative_ListM10OperationVentCouleur,
                        relative_bonVent = bonVent,
                        client = client
                    )

                    result.onSuccess { message ->
                        val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                        val pdfFile = File(filePath)

                        if (pdfFile.exists()) {
                            // Save to Downloads folder
                            savePdfToDownloads(
                                context,
                                pdfFile,
                                client?.nom ?: "Client_${bonVent.keyID.takeLast(4)}",
                                productLineCount
                            )
                            successCount++
                        } else {
                            Log.e("SavePDF", "❌ PDF file not found: $filePath")
                            errorCount++
                        }
                    }

                    result.onFailure { error ->
                        Log.e("SavePDF", "❌ Error generating PDF for ${client?.nom}: ${error.message}")
                        errorCount++
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Erreur pour ${client?.nom ?: "client"}: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // Final summary message
                CoroutineScope(Dispatchers.Main).launch {
                    val locationDesc = PdfSaverUtility.getSaveLocationDescription(context)
                    val message = when {
                        successCount > 0 && errorCount == 0 ->
                            "✅ $successCount PDF(s) sauvegardé(s)\n$locationDesc"
                        successCount > 0 && errorCount > 0 ->
                            "⚠️ $successCount réussis, $errorCount erreurs"
                        else ->
                            "❌ Erreur: Aucun PDF sauvegardé"
                    }

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("SavePDF", "❌ Fatal error during sharing: ${e.message}", e)
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Erreur lors du partage: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                kotlinx.coroutines.delay(2000)
                isLoading = false
            }
        }
    }

    // Calculate total sales for all confirmed orders
    val totalVentes = bonVents_OnCommande_ou_Leurclients_avec_confirmed.sumOf { bonVent ->
        repositorysMainGetter.repo10OperationVentCouleur.datasValue.count { vent ->
            vent.parent_M8BonVent_KeyId == bonVent.keyID &&
                    vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }
    }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = clients_avec_confirmed,
                    key = SemanticsPropertyKey("clients_avec_confirmed")
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = bonVents_OnCommande_ou_Leurclients_avec_confirmed,
                    key = SemanticsPropertyKey("bonVents_confirmed")
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = totalVentes,
                    key = SemanticsPropertyKey("total_ventes")
                )
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) {
                        "Partage en cours..."
                    } else {
                        "$nomFun (${bonVents_OnCommande_ou_Leurclients_avec_confirmed.size})"
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    shareWithWindows()
                }
            },
            enabled = !isLoading && bonVents_OnCommande_ou_Leurclients_avec_confirmed.isNotEmpty()
        )
    }
}
