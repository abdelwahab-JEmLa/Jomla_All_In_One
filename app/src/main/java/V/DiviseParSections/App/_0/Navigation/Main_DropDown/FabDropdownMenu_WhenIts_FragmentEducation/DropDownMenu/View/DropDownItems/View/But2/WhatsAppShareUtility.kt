package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG          = "WhatsAppShareUtility"
private const val CARDS_FOLDER = "whatsapp_cards"

// ─────────────────────────────────────────────────────────────────────────────
// Naming helpers
// ─────────────────────────────────────────────────────────────────────────────

/** Today's date folder name, e.g. "04_05" */
fun getTodayFolderName(): String =
    SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())

/** MediaStore RELATIVE_PATH for today's cards */
private fun todayRelativePath() =
    "${Environment.DIRECTORY_PICTURES}/$CARDS_FOLDER/${getTodayFolderName()}"

private fun sanitize(s: String) =
    s.trim().replace(Regex("[/\\\\:*?\"<>|]"), "_").take(40)

/** Canonical filename: "{keyID}_{nom}.jpg" */
fun cardFileName(keyID: String, nom: String) =
    "${sanitize(keyID)}_${sanitize(nom)}.jpg"

// ─────────────────────────────────────────────────────────────────────────────
// Bulk PDF → JPGs  (generate button)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Converts each page of [pdfFile] to a named JPEG saved at:
 *   Pictures/whatsapp_cards/MM_dd/{keyID}_{nom}.jpg
 *
 * Today's folder is wiped first so re-generation is always clean.
 * [students] must be in the same order as the PDF pages.
 */
fun convertPdfPagesToJpgs(
    context: Context,
    pdfFile: File,
    students: List<M19Etudiant>
): List<Uri?> {
    val results = MutableList<Uri?>(students.size) { null }
    if (!pdfFile.exists()) { Log.e(TAG, "❌ PDF not found"); return results }

    deleteTodayCards(context)

    var fd: ParcelFileDescriptor? = null
    var renderer: PdfRenderer?    = null
    try {
        fd       = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(fd)
        val pages = minOf(renderer.pageCount, students.size)

        for (i in 0 until pages) {
            var page: PdfRenderer.Page? = null
            try {
                page = renderer.openPage(i)
                val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                    .also { it.eraseColor(Color.WHITE); page.render(it, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT) }
                val s    = students[i]
                val name = cardFileName(s.keyID, s.nom)
                results[i] = saveJpg(context, bitmap, name)
                bitmap.recycle()
                Log.d(TAG, if (results[i] != null) "✅ Page $i → $name" else "❌ Page $i failed (${s.nom})")
            } catch (e: Exception) { Log.e(TAG, "❌ Render page $i", e) }
            finally { page?.close() }
        }
    } catch (e: Exception) { Log.e(TAG, "❌ PdfRenderer", e) }
    finally { renderer?.close(); fd?.close() }

    Log.i(TAG, "🖼️ ${results.count{it!=null}}/${students.size} JPGs → Pictures/$CARDS_FOLDER/${getTodayFolderName()}/")
    return results
}

// ─────────────────────────────────────────────────────────────────────────────
// Single-student PDF → JPG  (EtudiantCard per-card share)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Converts page 0 of [pdfFile] to a JPEG saved under the same dated folder.
 * Does NOT wipe other cards — only overwrites this student's file if it exists.
 */
fun convertSingleCardToJpg(
    context: Context,
    pdfFile: File,
    etudiant: M19Etudiant
): Uri? {
    if (!pdfFile.exists()) { Log.e(TAG, "❌ PDF not found"); return null }
    var fd: ParcelFileDescriptor? = null
    var renderer: PdfRenderer?    = null
    return try {
        fd       = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(fd)
        if (renderer.pageCount == 0) return null
        val page   = renderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            .also { it.eraseColor(Color.WHITE); page.render(it, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT) }
        page.close()
        val name = cardFileName(etudiant.keyID, etudiant.nom)
        val uri  = saveJpg(context, bitmap, name)
        bitmap.recycle()
        Log.d(TAG, if (uri != null) "✅ Single card → $name" else "❌ Single card failed (${etudiant.nom})")
        uri
    } catch (e: Exception) { Log.e(TAG, "❌ Single card render (${etudiant.nom})", e); null }
    finally { renderer?.close(); fd?.close() }
}

// ─────────────────────────────────────────────────────────────────────────────
// Lookup  (send button + EtudiantCard)
// ─────────────────────────────────────────────────────────────────────────────

/** keyID → Uri map for today's cards.  Missing entry = card not yet generated. */
fun getStoredCardUriMap(context: Context): Map<String, Uri> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        queryMediaStoreCardMap(context)
    else
        queryPublicPicturesCardMap(context)

/** Uri for one student's today card, or null if not generated yet. */
fun getStoredCardUriForStudent(context: Context, keyID: String): Uri? =
    getStoredCardUriMap(context)[keyID]

/** Flat list of today's card URIs (for availability count display). */
fun getStoredCardUris(context: Context): List<Uri> =
    getStoredCardUriMap(context).values.toList()

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun queryMediaStoreCardMap(context: Context): Map<String, Uri> {
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val map = mutableMapOf<String, Uri>()
    context.contentResolver.query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME),
        "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?",
        arrayOf("${todayRelativePath()}/%"),
        null
    )?.use { cursor ->
        val idCol   = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id    = cursor.getLong(idCol)
            val name  = cursor.getString(nameCol)   // "{keyID}_{nom}.jpg"
            val keyID = name.substringBefore("_")
            if (keyID.isNotBlank()) map[keyID] = Uri.withAppendedPath(collection, id.toString())
        }
    }
    return map
}

@Suppress("DEPRECATION")
private fun queryPublicPicturesCardMap(context: Context): Map<String, Uri> {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "$CARDS_FOLDER/${getTodayFolderName()}"
    )
    if (!dir.exists()) return emptyMap()
    val map = mutableMapOf<String, Uri>()
    dir.listFiles { f -> f.extension.lowercase() == "jpg" }?.forEach { f ->
        val keyID = f.nameWithoutExtension.substringBefore("_")
        if (keyID.isNotBlank())
            runCatching { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f) }
                .getOrNull()?.let { map[keyID] = it }
    }
    return map
}

// ─────────────────────────────────────────────────────────────────────────────
// Cleanup
// ─────────────────────────────────────────────────────────────────────────────

private fun deleteTodayCards(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val n = context.contentResolver.delete(
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?",
            arrayOf("${todayRelativePath()}/%")
        )
        Log.d(TAG, "🗑️ Deleted $n today's card(s) from MediaStore")
    } else {
        @Suppress("DEPRECATION")
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "$CARDS_FOLDER/${getTodayFolderName()}"
        )
        Log.d(TAG, "🗑️ Deleted ${dir.listFiles()?.count { it.delete() } ?: 0} today's card(s)")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Internal save dispatcher
// ─────────────────────────────────────────────────────────────────────────────

private fun saveJpg(context: Context, bitmap: Bitmap, fileName: String): Uri? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        saveJpgViaMediaStore(context, bitmap, fileName)
    else
        saveJpgToPublicPictures(context, bitmap, fileName)

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun saveJpgViaMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    val resolver   = context.contentResolver
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    // Remove stale entry with same name
    resolver.delete(collection,
        "${MediaStore.Images.Media.RELATIVE_PATH} = ? AND ${MediaStore.Images.Media.DISPLAY_NAME} = ?",
        arrayOf("${todayRelativePath()}/", fileName))
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME,  fileName)
        put(MediaStore.Images.Media.MIME_TYPE,     "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${todayRelativePath()}/")
        put(MediaStore.Images.Media.IS_PENDING,    1)
    }
    val uri = resolver.insert(collection, values) ?: return null
    return try {
        resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        uri
    } catch (e: Exception) {
        Log.e(TAG, "❌ MediaStore write: $fileName", e)
        resolver.delete(uri, null, null); null
    }
}

@Suppress("DEPRECATION")
private fun saveJpgToPublicPictures(context: Context, bitmap: Bitmap, fileName: String): Uri? {
    return try {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "$CARDS_FOLDER/${getTodayFolderName()}"
        ).also { it.mkdirs() }
        FileOutputStream(File(dir, fileName)).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(dir, fileName))
    } catch (e: Exception) { Log.e(TAG, "❌ Public Pictures write: $fileName", e); null }
}

// ─────────────────────────────────────────────────────────────────────────────
// Phone
// ─────────────────────────────────────────────────────────────────────────────

fun formatAlgerianPhoneNumber(raw: String): String {
    val c = raw.trim().replace(Regex("[ \\-.]"), "")
    return when {
        c.startsWith("+213")  -> c.removePrefix("+")
        c.startsWith("00213") -> c.removePrefix("00")
        c.startsWith("213")   -> c
        c.startsWith("0")     -> "213${c.substring(1)}"
        c.length == 9         -> "213$c"
        else                  -> "213$c"
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Share  (WA Business → WA → generic chooser)
// ─────────────────────────────────────────────────────────────────────────────

fun shareImageToWhatsAppBusiness(context: Context, imageUri: Uri, rawPhone: String) {
    val phone = formatAlgerianPhoneNumber(rawPhone)
    val jid   = "${phone}@s.whatsapp.net"
    fun intent(pkg: String) = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"; setPackage(pkg)
        putExtra(Intent.EXTRA_STREAM, imageUri); putExtra("jid", jid)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val pm = context.packageManager
    when {
        intent("com.whatsapp.w4b").resolveActivity(pm) != null -> { Log.d(TAG,"📲 WA Business → $phone"); context.startActivity(intent("com.whatsapp.w4b")) }
        intent("com.whatsapp").resolveActivity(pm)     != null -> { Log.d(TAG,"📲 WhatsApp → $phone");    context.startActivity(intent("com.whatsapp")) }
        else -> context.startActivity(
            Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"; putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "بطاقة التواصل - $rawPhone")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }, "إرسال البطاقة").apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        )
    }
}

/** Backward-compat File overload. */
fun shareImageToWhatsAppBusiness(context: Context, imageFile: File, rawPhone: String) {
    if (!imageFile.exists()) { Log.w(TAG,"⚠️ File missing: ${imageFile.name}"); return }
    runCatching { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile) }
        .onSuccess { shareImageToWhatsAppBusiness(context, it, rawPhone) }
        .onFailure { Log.e(TAG,"❌ FileProvider failed: ${imageFile.name}", it) }
}
