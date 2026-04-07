package V.DiviseParSections.App._0.Navigation.Buttons_Gps.DropDownItem_3.View

import EntreApps.Shared.Models.M8BonVent.EtateActuellementEst
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.koinInject

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
   /*
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
                // Generate all PDFs in parallel using async
                val results = bonVents_OnCommande_ou_Leurclients_avec_confirmed.map { bonVent ->
                    async(Dispatchers.IO) {
                        val relative_ListM10OperationVentCouleur =
                            repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { vent ->
                                vent.parent_M8BonVent_KeyId == bonVent.keyID &&
                                        vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                                        vent.quantity > 0
                            }

                        if (relative_ListM10OperationVentCouleur.isEmpty()) {
                            return@async Result.success<String?>(null)
                        }

                        val productLineCount = relative_ListM10OperationVentCouleur
                            .groupBy { it.parent_M1Produit_KeyId }
                            .size

                        val client = repositorysMainGetter.repo2Client.datasValue.find {
                            it.keyID == bonVent.parent_M2Client_KeyID
                        }

//                        // Generate PDF
//                        val result = printHandler.printPdfOnly(
//                            context = context,
//                            repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
//                            repoM1Produit = repositorysMainGetter.repo1ProduitInfos,
//                            repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos,
//                            scope = this,
//                            relative_ListM10OperationVentCouleur = relative_ListM10OperationVentCouleur,
//                            relative_bonVent = bonVent,
//                            client = client
//                        )

// Generate all PDFs in parallel using async
//                        val results = bonVents_OnCommande_ou_Leurclients_avec_confirmed.map { bonVent ->
//                            async(Dispatchers.IO) {
//                                val relative_ListM10OperationVentCouleur =
//                                    repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { vent ->
//                                        vent.parent_M8BonVent_KeyId == bonVent.keyID &&
//                                                vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
//                                                vent.quantity > 0
//                                    }
//
//                                if (relative_ListM10OperationVentCouleur.isEmpty()) {
//                                    return@async Result.success<Pair<String?, File?>>(null to null)
//                                }
//
//                                val productLineCount = relative_ListM10OperationVentCouleur
//                                    .groupBy { it.parent_M1Produit_KeyId }
//                                    .size
//
//                                val client = repositorysMainGetter.repo2Client.datasValue.find {
//                                    it.keyID == bonVent.parent_M2Client_KeyID
//                                }
//
//                                // Generate main receipt PDF
//                                val receiptResult = printHandler.printPdfOnly(
//                                    context = context,
//                                    repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
//                                    repoM1Produit = repositorysMainGetter.repo1ProduitInfos,
//                                    repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos,
//                                    scope = this,
//                                    relative_ListM10OperationVentCouleur = relative_ListM10OperationVentCouleur,
//                                    relative_bonVent = bonVent,
//                                    client = client
//                                )
//
//                                val labelPdf = genere_Carton_Client_Affiche_PDF_RTL(
//                                    context = context,
//                                    clientName = client?.nom ?: "Client_${bonVent.keyID.takeLast(4)}",
//                                    numberOfItems = productLineCount,
//                                    bonVentId = bonVent.keyID
//                                )
//
//                                receiptResult.fold(
//                                    onSuccess = { message ->
//                                        val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
//                                        val pdfFile = File(filePath)
//
//                                        if (pdfFile.exists()) {
//                                            // Save the main receipt
//                                            savePdfToDownloads(
//                                                context,
//                                                pdfFile,
//                                                client?.nom
//                                                    ?: "Client_${bonVent.keyID.takeLast(4)}",
//                                                productLineCount
//                                            )
//
//                                            // Save the label PDF if it was generated successfully
//                                            if (labelPdf != null && labelPdf.exists()) {
//                                                savePdfToDownloads(
//                                                    context,
//                                                    labelPdf,
//                                                    "${client?.nom ?: "Client"}_LABEL",
//                                                    productLineCount
//                                                )
//                                            }
//
//                                            Result.success(client?.nom to labelPdf)
//                                        } else {
//                                            Log.e("SavePDF", "❌ PDF file not found: $filePath")
//                                            Result.failure(Exception("PDF file not found"))
//                                        }
//                                    },
//                                    onFailure = { error ->
//                                        Log.e("SavePDF", "❌ Error generating PDF for ${client?.nom}: ${error.message}")
//                                        withContext(Dispatchers.Main) {
//                                            Toast.makeText(
//                                                context,
//                                                "Erreur pour ${client?.nom ?: "client"}: ${error.message}",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                        Result.failure(error)
//                                    }
//                                )
//                            }
//                        }.awaitAll()
//
//// Count successes and failures
//                        val successCount = results.count { it.isSuccess && it.getOrNull()?.first != null }
//                        val labelCount = results.count { it.isSuccess && it.getOrNull()?.second != null }
//                        val errorCount = results.count { it.isFailure || it.getOrNull()?.first == null }
//
//// Final summary message
//                        withContext(Dispatchers.Main) {
//                            val locationDesc = PdfSaverUtility.getSaveLocationDescription(context)
//                            val message = when {
//                                successCount > 0 && errorCount == 0 ->
//                                    "✅ $successCount reçu(s) + $labelCount étiquette(s) sauvegardé(s)\n$locationDesc"
//                                successCount > 0 && errorCount > 0 ->
//                                    "⚠️ $successCount réussis, $errorCount erreurs"
//                                else ->
//                                    "❌ Erreur: Aucun PDF sauvegardé"
//                            }
//
//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//                        }
//
//                        result.fold(
//                            onSuccess = { message ->
//                                val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
//                                val pdfFile = File(filePath)
//
//                                if (pdfFile.exists()) {
//                                    savePdfToDownloads(
//                                        context,
//                                        pdfFile,
//                                        client?.nom ?: "Client_${bonVent.keyID.takeLast(4)}",
//                                        productLineCount
//                                    )
//                                    Result.success(client?.nom)
//                                } else {
//                                    Log.e("SavePDF", "❌ PDF file not found: $filePath")
//                                    Result.failure(Exception("PDF file not found"))
//                                }
//                            },
//                            onFailure = { error ->
//                                Log.e("SavePDF", "❌ Error generating PDF for ${client?.nom}: ${error.message}")
//                                withContext(Dispatchers.Main) {
//                                    Toast.makeText(
//                                        context,
//                                        "Erreur pour ${client?.nom ?: "client"}: ${error.message}",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                                Result.failure(error)
//                            }
//                        )
//                    }
//                }.awaitAll()

                // Count successes and failures
                val successCount = results.count { it.isSuccess && it.getOrNull() != null }
                val errorCount = results.count { it.isFailure || it.getOrNull() == null }

                // Final summary message
                withContext(Dispatchers.Main) {
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
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur lors du partage: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                delay(2000)
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
                    CircularProgressIndicator(
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
    }                            */
}
