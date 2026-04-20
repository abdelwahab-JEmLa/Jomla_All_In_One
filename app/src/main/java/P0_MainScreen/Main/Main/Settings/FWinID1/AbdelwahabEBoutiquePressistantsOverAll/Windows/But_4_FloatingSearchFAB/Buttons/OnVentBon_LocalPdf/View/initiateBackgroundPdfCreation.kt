package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfSaverUtility_Proto2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
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
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "PdfBonVent"

suspend fun initiateBackgroundPdfCreation_NewP(
    context: Context,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    onPdfSaved: ((savedPath: String) -> Unit)? = null,
    list_M13TarificationInfos: List<M13TarificationInfos>,
) {
    val activeClient  = focusedValuesGetter.activeOnVentM2ClientInfos
    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeVents   = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }

    when {
        activeClient  == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show() }; return }
        activeBonVent == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun bon de vente actif",  Toast.LENGTH_SHORT).show() }; return }
        activeVents.isEmpty() -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun article à traiter",   Toast.LENGTH_SHORT).show() }; return }
    }

    try {
        val cv   = focusedValuesGetter.active_Central_Values
        val bons = cv.bons_a_imprime_avec_image_produit.toMutableList()
        if (bons.none { it.keyID == activeBonVent!!.keyID }) {
            bons.add(activeBonVent!!)
            focusedValuesGetter.update_activeCentralValues(cv.copy(bons_a_imprime_avec_image_produit = bons))
        }

        delay(300)

        val rawResult = withTimeout(30_000L) {
            aCentralFacade.modulesCentral.printReceiptHandler.printPdfOnly(
                context                              = context,
                repo13TarificationInfos             = list_M13TarificationInfos,
                repoM1Produit                        = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                repo3CouleurProduitInfos             = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                scope                               = CoroutineScope(currentCoroutineContext()),
                relative_ListM10OperationVentCouleur = activeVents,
                relative_bonVent                    = activeBonVent,
                client                              = activeClient,
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

        // baseName has NO extension — used in display (TODO 1) and for JPG names (TODO 2).
        val baseName = "${activeBonVent!!.keyID.takeLast(6)}" +
                "_${activeClient!!.nom.replace(Regex("[^A-Za-z0-9_\\-]"), "_").take(20)}" +
                "_${activeVents.size}"
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
                        activeBonVent.copy(
                            path_pdf_bon_file                      = pathToStore,
                            nombre_produits_don_dernier_pdf_stoked = activeVents.size
                        )
                    )
                }

                // ── TODO 2: convert ALL PDF pages to JPGs ─────────────────────
                // Source is tempFile — a real absolute path still alive inside
                // onSuccess (Result lambdas are synchronous; tempFile.delete()
                // below has not run yet).
                // Each page saved to public Download/BonsWhatsApp/MM_dd/ via
                // MediaStore so the images appear next to the PDF in the file
                // manager and are accessible to WhatsApp without FileProvider.
                //
                // Naming:
                //   1 page  → baseName.jpg
                //   N pages → baseName_p1.jpg, baseName_p2.jpg, …
                val savedJpgs = convertAllPdfPagesToJpgs(context, tempFile, baseName)
                Log.i(TAG, "🖼️ ${savedJpgs.count { it != null }}/${savedJpgs.size} JPG(s) saved to Download/BonsWhatsApp")

                // TODO 1: display name WITHOUT extension in Toast
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
        val fv = focusedValuesGetter.active_Central_Values
        focusedValuesGetter.update_activeCentralValues(
            fv.copy(
                bons_a_imprime_avec_image_produit =
                    fv.bons_a_imprime_avec_image_produit.filter { it.keyID != activeBonVent?.keyID }
            )
        )
        delay(500)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Multi-page PDF → JPGs
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Renders every page of [pdfFile] to a JPEG and saves each one via MediaStore
 * to the public `Download/BonsWhatsApp/MM_dd/` folder — the same location used
 * by [PdfSaverUtility_Proto2] for the PDF itself.
 *
 * Naming convention:
 *  - Single page  → `{baseName}.jpg`
 *  - Multiple pages → `{baseName}_p1.jpg`, `{baseName}_p2.jpg`, …
 *
 * Returns a list of URIs (one per page); null entries mean that page failed.
 *
 * [pdfFile] must be a real absolute filesystem path — NOT a MediaStore relative
 * string — so PdfRenderer can open it directly.
 */
private fun convertAllPdfPagesToJpgs(
    context: Context,
    pdfFile: File,
    baseName: String,
): List<Uri?> {
    if (!pdfFile.exists()) {
        Log.e(TAG, "❌ PDF not found for JPG conversion: ${pdfFile.absolutePath}")
        return emptyList()
    }

    var fd: ParcelFileDescriptor? = null
    var renderer: PdfRenderer?    = null

    return try {
        fd       = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(fd)
        val pageCount = renderer.pageCount
        Log.d(TAG, "📄 PDF has $pageCount page(s) — converting all to JPG")

        List(pageCount) { i ->
            var page: PdfRenderer.Page? = null
            try {
                page = renderer.openPage(i)

                // 2× resolution for sharpness (same as WhatsAppShareUtility pattern)
                val bitmap = Bitmap.createBitmap(
                    page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888
                ).also { bmp ->
                    bmp.eraseColor(Color.WHITE)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                }

                // Single page → "baseName.jpg"; multi-page → "baseName_p1.jpg" …
                val jpgName = if (pageCount == 1) "$baseName.jpg" else "${baseName}_p${i + 1}.jpg"

                val uri = saveBonJpgToMediaStore(context, bitmap, jpgName)
                bitmap.recycle()

                Log.d(TAG, if (uri != null) "✅ Page ${i + 1}/$pageCount → $jpgName" else "❌ Page ${i + 1}/$pageCount failed")
                uri
            } catch (e: Exception) {
                Log.e(TAG, "❌ Render page ${i + 1} failed: ${e.message}", e)
                null
            } finally {
                page?.close()
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "❌ PdfRenderer init failed: ${e.message}", e)
        emptyList()
    } finally {
        renderer?.close()
        fd?.close()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MediaStore JPEG writer → public Download/BonsWhatsApp/MM_dd/
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Saves [bitmap] as a JPEG to the PUBLIC Downloads folder under
 * `BonsWhatsApp/MM_dd/` so it is visible in the file manager alongside the PDF.
 */
private fun saveBonJpgToMediaStore(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
): Uri? {
    val todayFolder  = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
    val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/BonsWhatsApp/$todayFolder/"

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver   = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // Delete any stale entry with the same name first.
        resolver.delete(
            collection,
            "${MediaStore.Downloads.RELATIVE_PATH} = ? AND ${MediaStore.Downloads.DISPLAY_NAME} = ?",
            arrayOf(relativePath, fileName)
        )

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME,  fileName)
            put(MediaStore.Downloads.MIME_TYPE,     "image/jpeg")
            put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
            put(MediaStore.Downloads.IS_PENDING,    1)
        }
        val uri = resolver.insert(collection, values) ?: run {
            Log.e(TAG, "❌ MediaStore insert failed: $fileName"); return null
        }
        try {
            resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            uri
        } catch (e: Exception) {
            Log.e(TAG, "❌ MediaStore write failed: $fileName", e)
            resolver.delete(uri, null, null)
            null
        }
    } else {
        // Android 9 and below — direct file write to public storage
        @Suppress("DEPRECATION")
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "BonsWhatsApp/$todayFolder"
        ).also { it.mkdirs() }
        return try {
            val outFile = File(dir, fileName)
            FileOutputStream(outFile).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
            androidx.core.content.FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", outFile
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Legacy write failed: $fileName", e); null
        }
    }
}
