package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.CreditCapture

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private class DrawnHolder { var value: Boolean = false }

class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
    val hasBeenDrawn: () -> Boolean,
)

@Composable
fun rememberCapturableLayer(@DrawableRes backgroundRes: Int? = null): CapturableLayerState {
    val ctx = LocalContext.current
    val gLayer = rememberGraphicsLayer()
    val drawnHolder = remember { DrawnHolder() }

    val mod = Modifier.drawWithContent {
        gLayer.record { this@drawWithContent.drawContent() }
        drawnHolder.value = true
        drawContent()
    }

    return CapturableLayerState(
        modifier = mod,
        hasBeenDrawn = { drawnHolder.value },
        capture = {
            delay(100)
            val hw = gLayer.toImageBitmap()
            val sw = hw.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
            if (backgroundRes == null) return@CapturableLayerState sw.asImageBitmap()
            val out = Bitmap.createBitmap(sw.width, sw.height, Bitmap.Config.ARGB_8888)
            val cvs = Canvas(out)
            AppCompatResources.getDrawable(ctx, backgroundRes)?.let { drw ->
                drw.setBounds(0, 0, sw.width, sw.height)
                drw.draw(cvs)
            }
            cvs.drawBitmap(sw, 0f, 0f, null)
            out.asImageBitmap()
        },
    )
}

private data class CaptureEntry(
    val index: Int,
    val hasBeenDrawn: () -> Boolean,
    val capture: suspend () -> ImageBitmap,
)

class MultiCaptureController {
    private val entries = linkedMapOf<String, CaptureEntry>()

    fun register(key: String, index: Int, hasBeenDrawn: () -> Boolean, capture: suspend () -> ImageBitmap) {
        entries[key] = CaptureEntry(index, hasBeenDrawn, capture)
    }

    fun unregister(key: String) { entries.remove(key) }

    fun size(): Int = entries.size

    /** Capture tous les items actuellement enregistrés sans scroll (pour Column). */
    suspend fun captureAll(): List<Pair<String, ImageBitmap>> {
        delay(100)
        return entries.entries
            .filter { it.value.hasBeenDrawn() }
            .sortedBy { it.value.index }
            .mapNotNull { (key, entry) ->
                try { key to entry.capture() } catch (_: Exception) { null }
            }
    }

    suspend fun captureAllWithScroll(
        state: LazyListState,
        totalItemCount: Int,
        scrollSettleMs: Long = 300,
        restoreIndex: Int = 0,
        orderedKeys: List<String>? = null,
    ): List<Pair<String, ImageBitmap>> {
        data class R(val key: String, val listIndex: Int, val bmp: ImageBitmap)
        val results = mutableListOf<R>()
        val capturedKeys = mutableSetOf<String>()

        for (index in 0 until totalItemCount) {
            state.scrollToItem(index)
            delay(scrollSettleMs)
            for ((k, e) in entries.entries.toList()) {
                if (k !in capturedKeys && e.hasBeenDrawn()) {
                    results.add(R(k, e.index, e.capture()))
                    capturedKeys.add(k)
                }
            }
        }

        val sorted = if (orderedKeys != null) {
            results.sortedBy { r ->
                val pos = orderedKeys.indexOf(r.key)
                if (pos >= 0) pos else Int.MAX_VALUE
            }
        } else {
            results.sortedBy { it.listIndex }
        }.map { it.key to it.bmp }

        state.scrollToItem(restoreIndex)
        return sorted
    }
}

@Composable
fun rememberMultiCaptureController() = remember { MultiCaptureController() }

fun saveAllToMediaStore(
    bitmaps: List<Pair<Bitmap, String>>,
    context: Context,
    clientKeyID: String,
    customFolderPath: String? = null,
): List<Uri> {
    if (bitmaps.isEmpty()) return emptyList()

    val savedUris = mutableListOf<Uri>()
    val safeKey = clientKeyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
    val mmDd = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
    val folderPath = customFolderPath ?: "Download/credit_trxs/$safeKey/$mmDd"
    val resolver = context.contentResolver
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
            resolver.delete(collection, "${MediaStore.Images.Media.RELATIVE_PATH} = ?", arrayOf("$folderPath/"))
        } catch (_: Exception) {}
    }

    val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSLESS
    else
        @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP

    bitmaps.forEach { (bmp, lbl) ->
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${lbl}.webp")
            put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "$folderPath/")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = resolver.insert(collection, cv) ?: return@forEach
        resolver.openOutputStream(uri)?.use { out -> bmp.compress(fmt, 100, out) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
        }
        savedUris.add(uri)
    }
    return savedUris
}
