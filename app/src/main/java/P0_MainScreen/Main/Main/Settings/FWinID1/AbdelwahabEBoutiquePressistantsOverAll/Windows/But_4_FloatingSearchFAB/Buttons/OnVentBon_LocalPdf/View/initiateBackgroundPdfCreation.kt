package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfSaverUtility_Proto2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "BgPdfCreation"

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

    Log.d(TAG, "── initiateBackgroundPdfCreation_NewP ──────────────────")
    Log.d(TAG, "  activeClient   = ${activeClient?.nom ?: "NULL"}")
    Log.d(TAG, "  activeBonVent  = ${activeBonVent?.keyID ?: "NULL"}")
    Log.d(TAG, "  activeVents    = ${activeVents.size} items")

    when {
        activeClient == null -> {
            Log.w(TAG, "  ❌ Guard: activeClient is null → aborting")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show()
            }; return
        }

        activeBonVent == null -> {
            Log.w(TAG, "  ❌ Guard: activeBonVent is null → aborting")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
            }; return
        }

        activeVents.isEmpty() -> {
            Log.w(TAG, "  ❌ Guard: activeVents is empty → aborting")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Aucun article à traiter", Toast.LENGTH_SHORT).show()
            }; return
        }
    }

    try {
        // ── 1. Register bonVent in the "print with image" list ──────────────────
        val cv = focusedValuesGetter.active_Central_Values
        val bons = cv.bons_a_imprime_avec_image_produit.toMutableList()
        val alreadyRegistered = bons.any { it.keyID == activeBonVent!!.keyID }
        Log.d(TAG, "  bons_a_imprime list size = ${bons.size}, alreadyRegistered = $alreadyRegistered")

        if (!alreadyRegistered) {
            bons.add(activeBonVent!!)
            focusedValuesGetter.update_activeCentralValues(cv.copy(bons_a_imprime_avec_image_produit = bons))
            Log.d(TAG, "  ✅ bonVent added to bons_a_imprime list")
        }

        delay(300)

        // ── 2. Generate the PDF via PrintReceiptHandler ──────────────────────────
        Log.d(TAG, "  ⏳ Calling printPdfOnly (timeout 30s)…")

        val rawResult = withTimeout(30_000L) {
            aCentralFacade.modulesCentral.printReceiptHandler
                .printPdfOnly(
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
                    shouldOpenFile = false
                )
        }

        Log.d(TAG, "  printPdfOnly raw result → isSuccess=${rawResult?.isSuccess}, value=${rawResult?.getOrNull()?.take(120)}")

        val pdfFilePath = rawResult
            ?.getOrNull()
            ?.substringAfter("PDF saved: ")
            ?.substringBefore("\n")

        Log.d(TAG, "  extracted pdfFilePath = $pdfFilePath")

        // ── 3. Validate the temp file ────────────────────────────────────────────
        val tempFile = pdfFilePath?.let { File(it) }

        when {
            pdfFilePath == null -> Log.e(TAG, "  ❌ pdfFilePath is null – printPdfOnly returned no path")
            tempFile == null    -> Log.e(TAG, "  ❌ tempFile is null")
            !tempFile.exists()  -> Log.e(TAG, "  ❌ tempFile does not exist at: ${tempFile.absolutePath}")
            tempFile.length() == 0L -> Log.e(TAG, "  ❌ tempFile exists but is empty (0 bytes): ${tempFile.absolutePath}")
            else -> Log.d(TAG, "  ✅ tempFile OK – size=${tempFile.length()} bytes, path=${tempFile.absolutePath}")
        }

        if (tempFile == null || !tempFile.exists() || tempFile.length() == 0L) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ Génération échouée", Toast.LENGTH_LONG).show()
            }
            return
        }

        // ── 4. Build final file name ─────────────────────────────────────────────
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM_dd_HH:mm"))
        val clientPart = activeClient!!.nom.replace(Regex("[^A-Za-z0-9_\\-]"), "_").take(20)
        val fileName = "${clientPart}_${activeBonVent!!.keyID.takeLast(6)}_${timestamp}.pdf"
        Log.d(TAG, "  fileName = $fileName")

        // ── 5. Save to BonsWhatsApp folder ───────────────────────────────────────
        Log.d(TAG, "  ⏳ Calling PdfSaverUtility_Proto2.savePdf…")

        val externalDownloads = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
        Log.d(TAG, "  externalDownloads dir = $externalDownloads  exists=${externalDownloads?.exists()}  canWrite=${externalDownloads?.canWrite()}")

        PdfSaverUtility_Proto2.savePdf(context, tempFile, fileName, "BonsWhatsApp")
            .onSuccess { savedRelativePath ->
                val savedDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                val finalPath = File(savedDir, "BonsWhatsApp/$fileName").absolutePath
                Log.d(TAG, "  ✅ savePdf succeeded → savedRelativePath=$savedRelativePath  finalPath=$finalPath")

                // Notify caller with the saved path (e.g. so the FAB can update M8BonVent)
                onPdfSaved?.invoke(finalPath)

                // Fallback: if no callback provided, persist directly from here
                if (onPdfSaved == null) {
                    Log.d(TAG, "  onPdfSaved is null → persisting path directly via repo8BonVent")
                    aCentralFacade.repositorysMainSetter.repo8BonVent.upsert(
                        activeBonVent.copy(
                            path_pdf_bon_file = finalPath,
                            nombre_produits_don_dernier_pdf_stoked = activeVents.size
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "✅ PDF terminé!\n$fileName\nTéléchargements/BonsWhatsApp",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .onFailure { error ->
                Log.e(TAG, "  ❌ savePdf failed → ${error::class.simpleName}: ${error.message}", error)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Erreur: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }

        // ── 6. Clean up temp file ────────────────────────────────────────────────
        val deleted = tempFile.delete()
        Log.d(TAG, "  tempFile.delete() = $deleted")

    } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "  ❌ Timeout after 30s", e)
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "❌ Timeout (>30s)", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e(TAG, "  ❌ Unexpected exception: ${e::class.simpleName}: ${e.message}", e)
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "❌ Erreur: ${e.message}", Toast.LENGTH_LONG).show()
        }
    } finally {
        // ── 7. Remove bonVent from "print with image" list ───────────────────────
        val fv = focusedValuesGetter.active_Central_Values
        val filteredBons = fv.bons_a_imprime_avec_image_produit.filter { it.keyID != activeBonVent?.keyID }
        Log.d(TAG, "  finally: removing bonVent from bons_a_imprime list (${fv.bons_a_imprime_avec_image_produit.size} → ${filteredBons.size})")
        focusedValuesGetter.update_activeCentralValues(
            fv.copy(bons_a_imprime_avec_image_produit = filteredBons)
        )
        delay(500)
        Log.d(TAG, "── END initiateBackgroundPdfCreation_NewP ──────────────────")
    }
}
