package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B9.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App._0.Navigation.Buttons_Gps.PdfSaverUtility
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

 suspend fun initiateBackgroundPdfCreation(
    context: Context,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
) {
    val activeClient  = focusedValuesGetter.activeOnVentM2ClientInfos
    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeVents   = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }

    when {
        activeClient  == null -> {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Aucun client actif trouvé",
                    Toast.LENGTH_SHORT
                ).show()
            }; return }
        activeBonVent == null -> {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Aucun bon de vente actif",
                    Toast.LENGTH_SHORT
                ).show()
            }; return }
        activeVents.isEmpty() -> {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Aucun article à traiter",
                    Toast.LENGTH_SHORT
                ).show()
            }; return }
    }

    try {
        val cv   = focusedValuesGetter.active_Central_Values
        val bons = cv.bons_a_imprime_avec_image_produit.toMutableList()
        if (!bons.any { it.keyID == activeBonVent!!.keyID }) {
            bons.add(activeBonVent!!)
            focusedValuesGetter.update_activeCentralValues(cv.copy(bons_a_imprime_avec_image_produit = bons))
        }

        delay(300)

        val pdfFilePath = withTimeout(30_000L) {
            (aCentralFacade.modulesCentral.printReceiptHandler as? PrintReceiptHandler_Juil)
                ?.printPdfOnly(
                    context = context,
                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                    scope = null,
                    relative_ListM10OperationVentCouleur = activeVents,
                    relative_bonVent = activeBonVent!!,
                    client = activeClient!!,
                    showCreditSection = false,
                    versement = 0.0,
                    shouldOpenFile = false
                )?.getOrNull()
                ?.substringAfter("PDF saved: ")
                ?.substringBefore("\n")
        }

        val tempFile = pdfFilePath?.let { File(it) }
        if (tempFile == null || !tempFile.exists()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "❌ Génération échouée",
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }

        val timestamp  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM_dd_HH:mm"))
        val clientPart = activeClient!!.nom.replace(Regex("[^A-Za-z0-9_\\-]"), "_").take(20)
        val fileName   = "${clientPart}_${activeBonVent!!.keyID.takeLast(6)}_${timestamp}.pdf"

        PdfSaverUtility.savePdf(context, tempFile, fileName, "BonsWhatsApp")
            .onSuccess {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "✅ PDF terminé!\n$fileName\nTéléchargements/BonsWhatsApp",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "❌ Erreur: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        tempFile.delete()

    } catch (e: TimeoutCancellationException) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "❌ Timeout (>30s)",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "❌ Erreur: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    } finally {
        delay(500)
        val fv = focusedValuesGetter.active_Central_Values
        focusedValuesGetter.update_activeCentralValues(
            fv.copy(bons_a_imprime_avec_image_produit = fv.bons_a_imprime_avec_image_produit.filter { it.keyID != activeBonVent?.keyID })
        )
    }
}
