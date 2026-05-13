package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfSaverUtility_Proto2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

private const val TAG = "PdfBonVent"

suspend fun initiateBackgroundPdfCreation_NewP(
    context: Context,
    aCentralFacade: ACentralFacade,
    onPdfSaved: ((savedPath: String) -> Unit)? = null,
    list_M13TarificationInfos: List<M13TarificationInfos>,
    relative_List_M13Vent: List<M10OperationVentCouleur>,
    on_vent_client: M2Client?,
    on_vent_bon: M8BonVent?,
    ) {

    when {
        on_vent_client == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show() }; return }
        on_vent_bon == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun bon de vente actif",  Toast.LENGTH_SHORT).show() }; return }
        relative_List_M13Vent.isEmpty() -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun article à traiter",   Toast.LENGTH_SHORT).show() }; return }
    }

    try {

        delay(300)

        val rawResult = withTimeout(30_000L) {
            aCentralFacade.modulesCentral.printReceiptHandler.printPdfOnly(
                context                              = context,
                repo13TarificationInfos             = list_M13TarificationInfos,
                repoM1Produit                        = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                repo3CouleurProduitInfos             = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                scope                               = CoroutineScope(currentCoroutineContext()),
                relative_ListM10OperationVentCouleur = relative_List_M13Vent,
                relative_bonVent                    = on_vent_bon,
                client                              = on_vent_client,
                showCreditSection                   = false,
                versement                           = 0.0,
                shouldOpenFile                      = false
            )
        }

        val pdfFilePath = rawResult?.getOrNull()?.substringAfter("PDF saved: ")?.substringBefore("\n")
        val tempFile    = pdfFilePath?.let { File(it) }

        if (tempFile == null || !tempFile.exists() || tempFile.length() == 0L) {
            withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Génération échouée", Toast.LENGTH_LONG).show() }
            return
        }

        // baseName has NO extension — used in the Toast and as the JPG file stem.
        val baseName = on_vent_bon!!.keyID.takeLast(6) +
                "_${on_vent_client!!.nom.replace(Regex("[^A-Za-z0-9_\\-]"), "_").take(20)}" +
                "_${relative_List_M13Vent.size}"
        val fileName = "$baseName.pdf"

        PdfSaverUtility_Proto2.savePdf(context, tempFile, fileName, "BonsWhatsApp")
            .onSuccess { savedRelativePath ->

                val finalAbsPath = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "BonsWhatsApp/$fileName"
                ).absolutePath
                val pathToStore = if (File(finalAbsPath).exists() && File(finalAbsPath).length() > 0L)
                    finalAbsPath else savedRelativePath

                onPdfSaved?.invoke(pathToStore)

                if (onPdfSaved == null) {
                    aCentralFacade.repositorysMainSetter.repo8BonVent.upsert(
                        on_vent_bon.copy(
                            path_pdf_bon_file                      = pathToStore,
                            nombre_produits_don_dernier_pdf_stoked = relative_List_M13Vent.size
                        )
                    )
                }

                // Convert all PDF pages to styled JPGs via BonJpgConverter.
                // tempFile is still alive here — Result lambdas are synchronous.
                val savedJpgs = convertAllPdfPagesToJpgs(context, tempFile, baseName)
                Log.i(TAG, "🖼️ ${savedJpgs.count { it != null }}/${savedJpgs.size} JPG(s) saved to Download/BonsWhatsApp")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "✅ PDF terminé!\n$baseName\nTéléchargements/BonsWhatsApp",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .onFailure { error ->
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Erreur: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }

        // Deleted AFTER onSuccess completes (Result lambdas are synchronous).
        tempFile.delete()

    } catch (e: TimeoutCancellationException) {
        withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Timeout (>30s)", Toast.LENGTH_LONG).show() }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Erreur: ${e.message}", Toast.LENGTH_LONG).show() }
    } finally {
        delay(500)
    }
}


