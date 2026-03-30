package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfSaverUtility_Proto2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M10OperationVentCouleur
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

suspend fun initiateBackgroundPdfCreation_NewP(
    context: Context,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    onPdfSaved: ((savedPath: String) -> Unit)? = null,
) {
    val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }

    when {
        activeClient == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show() }; return }
        activeBonVent == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show() }; return }
        activeVents.isEmpty() -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun article à traiter", Toast.LENGTH_SHORT).show() }; return }
    }

    try {
        val cv = focusedValuesGetter.active_Central_Values
        val bons = cv.bons_a_imprime_avec_image_produit.toMutableList()
        if (bons.none { it.keyID == activeBonVent!!.keyID }) {
            bons.add(activeBonVent!!)
            focusedValuesGetter.update_activeCentralValues(cv.copy(bons_a_imprime_avec_image_produit = bons))
        }

        delay(300)

        val rawResult = withTimeout(30_000L) {
            aCentralFacade.modulesCentral.printReceiptHandler.printPdfOnly(
                context = context,
                repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                scope = CoroutineScope(currentCoroutineContext()),
                relative_ListM10OperationVentCouleur = activeVents,
                relative_bonVent = activeBonVent,
                client = activeClient,
                showCreditSection = false,
                versement = 0.0,
                shouldOpenFile = false
            )
        }

        val pdfFilePath = rawResult?.getOrNull()?.substringAfter("PDF saved: ")?.substringBefore("\n")
        val tempFile = pdfFilePath?.let { File(it) }

        if (tempFile == null || !tempFile.exists() || tempFile.length() == 0L) {
            withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Génération échouée", Toast.LENGTH_LONG).show() }
            return
        }

        val fileName = "${activeBonVent!!.keyID.takeLast(6)}_${activeClient!!.nom.replace(Regex("[^A-Za-z0-9_\\-]"), "_").take(20)}_${activeVents.size}.pdf"

        PdfSaverUtility_Proto2.savePdf(context, tempFile, fileName, "BonsWhatsApp")
            .onSuccess { savedRelativePath ->
                val finalPath = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS), "BonsWhatsApp/$fileName").absolutePath
                val pathToStore = if (File(finalPath).exists() && File(finalPath).length() > 0L) finalPath else savedRelativePath

                onPdfSaved?.invoke(pathToStore)

                if (onPdfSaved == null) {
                    aCentralFacade.repositorysMainSetter.repo8BonVent.upsert(
                        activeBonVent.copy(path_pdf_bon_file = pathToStore, nombre_produits_don_dernier_pdf_stoked = activeVents.size)
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ PDF terminé!\n$fileName\nTéléchargements/BonsWhatsApp", Toast.LENGTH_LONG).show()
                }
            }
            .onFailure { error ->
                withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Erreur: ${error.message}", Toast.LENGTH_LONG).show() }
            }

        tempFile.delete()

    } catch (e: TimeoutCancellationException) {
        withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Timeout (>30s)", Toast.LENGTH_LONG).show() }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) { Toast.makeText(context, "❌ Erreur: ${e.message}", Toast.LENGTH_LONG).show() }
    } finally {
        val fv = focusedValuesGetter.active_Central_Values
        focusedValuesGetter.update_activeCentralValues(
            fv.copy(bons_a_imprime_avec_image_produit = fv.bons_a_imprime_avec_image_produit.filter { it.keyID != activeBonVent?.keyID })
        )
        delay(500)
    }
}
