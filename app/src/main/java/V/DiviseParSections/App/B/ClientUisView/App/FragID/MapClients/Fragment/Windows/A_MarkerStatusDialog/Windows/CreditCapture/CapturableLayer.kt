package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.CreditCapture

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class DrawnHolder { var value: Boolean = false }

class CapturableLayerController internal constructor(
    private val graphicsLayer: GraphicsLayer,
    private val drawnHolder: DrawnHolder
) {
    val modifier: Modifier = Modifier.drawWithContent {
        drawContent()
        graphicsLayer.record {
            this@drawWithContent.drawContent()
        }
        drawnHolder.value = true
    }

    val hasBeenDrawn: Boolean get() = drawnHolder.value

    suspend fun capture(): ImageBitmap {
        return graphicsLayer.toImageBitmap()
    }
}

@Composable
fun rememberCapturableLayer(): CapturableLayerController {
    val graphicsLayer = rememberGraphicsLayer()
    val drawnHolder = remember { DrawnHolder() }
    return remember(graphicsLayer) { CapturableLayerController(graphicsLayer, drawnHolder) }
}

class MultiCaptureController {
    private class Entry(
        val index: Int,
        val hasBeenDrawn: () -> Boolean,
        val capture: suspend () -> ImageBitmap
    )

    private val entries = mutableMapOf<String, Entry>()

    fun register(key: String, index: Int, hasBeenDrawn: () -> Boolean, capture: suspend () -> ImageBitmap) {
        entries[key] = Entry(index, hasBeenDrawn, capture)
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
    Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Started with ${bitmaps.size} bitmap(s) for clientKeyID='$clientKeyID'")
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

    Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Cleaning existing MediaStore entries for '%credit_trxs/$safeKey/$mmDd%'...")
    try {
        val proj = arrayOf(MediaStore.Images.Media._ID)
        val sel = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selArgs = arrayOf("%credit_trxs/$safeKey/$mmDd%")
        var deletedCount = 0
        resolver.query(collection, proj, sel, selArgs, null)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(collection, id)
                try {
                    val rows = resolver.delete(uri, null, null)
                    deletedCount += rows
                } catch (e: Exception) {
                    Log.w("CREDIT_CAPTURE", "[saveAllToMediaStore] Failed deleting URI $uri: ${e.message}")
                }
            }
        }
        Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Deleted $deletedCount MediaStore entries.")
    } catch (e: Exception) {
        Log.w("CREDIT_CAPTURE", "[saveAllToMediaStore] MediaStore cleanup error: ${e.message}")
    }

    try {
        val cleanSubPath = "credit_trxs/$safeKey/$mmDd"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dir = File(downloadsDir, cleanSubPath)
        Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Physical dir check: '${dir.absolutePath}' exists=${dir.exists()}")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            var deletedFileCount = 0
            files?.forEach { file ->
                if (file.isFile && file.delete()) {
                    deletedFileCount++
                }
            }
            Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Deleted $deletedFileCount physical files from ${dir.absolutePath}")
        }
    } catch (e: Exception) {
        Log.w("CREDIT_CAPTURE", "[saveAllToMediaStore] Physical file cleanup error: ${e.message}")
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
        Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Saved image: $uri (display='image_${lbl}.webp')")
    }

    Log.d("CREDIT_CAPTURE", "[saveAllToMediaStore] Finished. Total saved URIs: ${savedUris.size}")
    return savedUris
}
