package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfSaverUtility_Proto2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler_Mai
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Proto_Mai
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
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

suspend fun initiateBackgroundPdfCreation_ProMai(
    context: Context,
    aCentralFacade: ACentralFacade,
    onPdfSaved: ((savedPath: String) -> Unit)? = null,
    list_M13TarificationInfos: List<M13TarificationInfos>,
    relative_List_M13Vent: List<M10OperationVentCouleur>,
    on_vent_client: M2Client?,
    on_vent_bon: M8BonVent?,
    printInPdf_itextpdf_Handler: PrintInPdf_itextpdf_Handler_Mai,
    ) {

    when {
        on_vent_client == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show() }; return }
        on_vent_bon == null -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun bon de vente actif",  Toast.LENGTH_SHORT).show() }; return }
        relative_List_M13Vent.isEmpty() -> { withContext(Dispatchers.Main) { Toast.makeText(context, "Aucun article à traiter",   Toast.LENGTH_SHORT).show() }; return }
    }

    try {

        delay(300)

        val rawResult = withTimeout(30_000L) {
            PrintReceiptHandler_Proto_Mai(printInPdf_itextpdf_Handler).printPdf(
                context                              = context,
                repo13TarificationInfos             = list_M13TarificationInfos,
                repoM1Produit                        = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,//<--
                //TODO(1): enleve ou il ya repo1ProduitInfos  et repo03CouleurProduitInfos et utilise datas
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



private const val RENDER_SCALE = 3

/**
 * White mat (padding) in pixels added on every side of the page content.
 * At 3× scale this equals ~16 dp — enough breathing room without wasted space.
 */
private const val PAGE_PAD_PX = 48

/**
 * Maximum drop-shadow displacement in pixels.
 * The shadow is built from [SHADOW_LAYERS] semi-transparent gray layers
 * at increasing offsets to simulate Gaussian blur.
 */
private const val SHADOW_OFFSET_MAX = 18

/** Number of translucent layers used to simulate a soft shadow blur. */
private const val SHADOW_LAYERS = 6

/** Rounded-corner radius (px) applied to the white page rectangle. */
private const val CORNER_RADIUS = 12f

/**
 * JPEG encoding quality (0–100).
 * 95 keeps text razor-sharp while keeping file sizes reasonable for WhatsApp.
 */
private const val JPEG_QUALITY = 95

/** Canvas background colour — a clean off-white so the white page pops. */
private val BG_COLOR = Color.parseColor("#EBEBEB")

/**
 * Renders every page of [pdfFile] to a high-quality JPEG and saves each one
 * via MediaStore to the public `Download/BonsWhatsApp/MM_dd/` folder.
 *
 * Each image is composited as follows:
 * ```
 * ┌─ canvas (EBEBEB background) ──────────────────────────────────┐
 * │          ·· soft drop shadow ··                               │
 * │   ┌──────────────────────────────┐                            │
 * │   │  white mat  (PAGE_PAD_PX)    │                            │
 * │   │   ┌────────────────────────┐ │                            │
 * │   │   │  PDF content @ 3× DPI  │ │                            │
 * │   │   └────────────────────────┘ │                            │
 * │   │  white mat                   │                            │
 * │   └──────────────────────────────┘                            │
 * └───────────────────────────────────────────────────────────────┘
 * ```
 *
 * Naming:
 *  - Single page  → `{baseName}.jpg`
 *  - Multiple pages → `{baseName}_p1.jpg`, `{baseName}_p2.jpg`, …
 *
 * [pdfFile] must be a real absolute filesystem path (not a MediaStore path).
 * Returns one URI per page; null entries mean that page failed to render.
 */
fun convertAllPdfPagesToJpgs(
    context: Context,
    pdfFile: File,
    baseName: String,
): List<Uri?> {
    if (!pdfFile.exists()) {
        Log.e(TAG, "❌ PDF not found: ${pdfFile.absolutePath}")
        return emptyList()
    }

    var fd: ParcelFileDescriptor? = null
    var renderer: PdfRenderer?    = null

    return try {
        fd       = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(fd)
        val pageCount = renderer.pageCount
        Log.d(TAG, "📄 $pageCount page(s) → converting at ${RENDER_SCALE}× DPI")

        List(pageCount) { i ->
            var page: PdfRenderer.Page? = null
            try {
                page = renderer.openPage(i)

                val composed = renderPageToComposedBitmap(page)
                val jpgName  = if (pageCount == 1) "$baseName.jpg" else "${baseName}_p${i + 1}.jpg"
                val uri      = saveBonJpgToMediaStore(context, composed, jpgName)
                composed.recycle()

                Log.d(TAG, if (uri != null) "✅ p${i + 1}/$pageCount → $jpgName" else "❌ p${i + 1}/$pageCount failed")
                uri
            } catch (e: Exception) {
                Log.e(TAG, "❌ Render page ${i + 1}: ${e.message}", e)
                null
            } finally {
                page?.close()
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "❌ PdfRenderer init: ${e.message}", e)
        emptyList()
    } finally {
        renderer?.close()
        fd?.close()
    }
}

/**
 * Renders a single [PdfRenderer.Page] into a composed output bitmap that
 * includes the light-gray background, soft drop shadow, white mat, and
 * the page content at [RENDER_SCALE]× resolution.
 *
 * Two-pass approach:
 *  1. Render the raw PDF content into an intermediate bitmap (no allocations
 *     on the final canvas during render, avoids overdraw artifacts).
 *  2. Composite that bitmap onto the styled frame.
 */
private fun renderPageToComposedBitmap(page: PdfRenderer.Page): Bitmap {
    val pageW = page.width  * RENDER_SCALE
    val pageH = page.height * RENDER_SCALE

    // ── Pass 1: raw PDF content ───────────────────────────────────────────────
    val pageBitmap = Bitmap.createBitmap(pageW, pageH, Bitmap.Config.ARGB_8888)
    pageBitmap.eraseColor(Color.WHITE)
    page.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)

    // ── Pass 2: compose onto styled canvas ───────────────────────────────────
    val totalW = pageW + PAGE_PAD_PX * 2 + SHADOW_OFFSET_MAX
    val totalH = pageH + PAGE_PAD_PX * 2 + SHADOW_OFFSET_MAX
    val output = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    // Background
    canvas.drawColor(BG_COLOR)

    // Soft drop shadow — multiple semi-transparent layers at staggered offsets
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    repeat(SHADOW_LAYERS) { layer ->
        val fraction  = (layer + 1).toFloat() / SHADOW_LAYERS   // 0.17 … 1.0
        val offset    = SHADOW_OFFSET_MAX * fraction
        val alpha     = (55 * (1f - fraction * 0.6f)).toInt().coerceIn(8, 55)
        shadowPaint.color = Color.argb(alpha, 30, 30, 30)
        canvas.drawRoundRect(
            RectF(
                PAGE_PAD_PX + offset,
                PAGE_PAD_PX + offset,
                PAGE_PAD_PX + pageW + offset,
                PAGE_PAD_PX + pageH + offset
            ),
            CORNER_RADIUS, CORNER_RADIUS,
            shadowPaint
        )
    }

    // White page rectangle (rounded)
    val pagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    val pageRect = RectF(
        PAGE_PAD_PX.toFloat(),
        PAGE_PAD_PX.toFloat(),
        (PAGE_PAD_PX + pageW).toFloat(),
        (PAGE_PAD_PX + pageH).toFloat()
    )
    canvas.drawRoundRect(pageRect, CORNER_RADIUS, CORNER_RADIUS, pagePaint)

    // Clip to page rect so content never bleeds outside the rounded corners
    canvas.save()
    canvas.clipRect(pageRect)   // clipRoundRect requires API 26; clipRect is safe & virtually identical here
    canvas.drawBitmap(pageBitmap, PAGE_PAD_PX.toFloat(), PAGE_PAD_PX.toFloat(), null)
    canvas.restore()

    // Hair-line border — separates white page from the gray canvas on low-contrast displays
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color       = Color.argb(40, 0, 0, 0)
        style       = Paint.Style.STROKE
        strokeWidth = 1.5f
    }
    canvas.drawRoundRect(pageRect, CORNER_RADIUS, CORNER_RADIUS, borderPaint)

    pageBitmap.recycle()
    return output
}

/**
 * Writes [bitmap] as a JPEG to `Downloads/BonsWhatsApp/MM_dd/[fileName]` via
 * MediaStore (API 29+) or direct file I/O (API < 29).
 *
 * Any stale file with the same name is replaced atomically using IS_PENDING.
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

        // Remove stale entry so the new file always lands fresh
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
            resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
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
        @Suppress("DEPRECATION")
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "BonsWhatsApp/$todayFolder"
        ).also { it.mkdirs() }
        return try {
            val outFile = File(dir, fileName)
            FileOutputStream(outFile).use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
            FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", outFile
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Legacy write failed: $fileName", e); null
        }
    }
}
