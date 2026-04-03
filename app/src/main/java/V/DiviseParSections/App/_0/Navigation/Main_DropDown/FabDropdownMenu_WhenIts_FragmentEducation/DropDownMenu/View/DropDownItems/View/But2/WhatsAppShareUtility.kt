package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

private const val TAG = "WhatsAppShareUtility"

/**
 * Converts each page of a PDF file into a separate JPEG image file.
 *
 * Uses Android's [PdfRenderer] at 2× resolution so the exported image
 * is crisp when viewed on a phone screen.
 *
 * @param context   Application / Activity context (needed for cacheDir).
 * @param pdfFile   The source PDF file (must exist and be readable).
 * @param pageCount How many pages to convert (usually equal to the number of students).
 * @return A list of [File] objects — one per page; `null` entries mean that
 *         a specific page failed to render (the list always has `pageCount` slots).
 */
fun convertPdfPagesToJpgs(
    context: Context,
    pdfFile: File,
    pageCount: Int
): List<File?> {
    val results = MutableList<File?>(pageCount) { null }

    if (!pdfFile.exists()) {
        Log.e(TAG, "❌ PDF file not found: ${pdfFile.absolutePath}")
        return results
    }

    var fileDescriptor: ParcelFileDescriptor? = null
    var pdfRenderer: PdfRenderer? = null

    try {
        fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)

        val pagesToProcess = minOf(pdfRenderer.pageCount, pageCount)

        for (i in 0 until pagesToProcess) {
            var page: PdfRenderer.Page? = null
            try {
                page = pdfRenderer.openPage(i)

                // 2× scale → sharper image on high-DPI screens
                val scale = 2
                val bitmapWidth  = page.width  * scale
                val bitmapHeight = page.height * scale

                val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE) // white background (PDF pages are transparent)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)

                // Unique name so parallel calls never collide
                val jpgFile = File(context.cacheDir, "card_page${i}_${System.currentTimeMillis()}.jpg")

                FileOutputStream(jpgFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                bitmap.recycle()

                results[i] = jpgFile
                Log.d(TAG, "✅ Page $i → ${jpgFile.name} (${jpgFile.length()} bytes)")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to render page $i", e)
            } finally {
                page?.close()
            }
        }

    } catch (e: Exception) {
        Log.e(TAG, "❌ PdfRenderer initialisation failed", e)
    } finally {
        pdfRenderer?.close()
        fileDescriptor?.close()
    }

    return results
}

/**
 * Formats an Algerian phone number into the international format expected
 * by WhatsApp's `jid` field (no `+`, no leading zeros).
 *
 * Examples:
 *  - "0555123456"   → "213555123456"
 *  - "+213555123456"→ "213555123456"
 *  - "00213555123456"→"213555123456"
 *  - "555123456"    → "213555123456"   (9-digit local number)
 *  - "213555123456" → "213555123456"   (already correct)
 */
fun formatAlgerianPhoneNumber(rawPhone: String): String {
    val cleaned = rawPhone
        .trim()
        .replace(" ", "")
        .replace("-", "")
        .replace(".", "")

    return when {
        cleaned.startsWith("+213")  -> cleaned.removePrefix("+")
        cleaned.startsWith("00213") -> cleaned.removePrefix("00")
        cleaned.startsWith("213")   -> cleaned
        cleaned.startsWith("0")     -> "213${cleaned.substring(1)}"
        cleaned.length == 9         -> "213$cleaned"   // e.g. 555XXXXXX
        else                        -> "213$cleaned"
    }
}

/**
 * Shares a JPEG image directly to a specific WhatsApp Business contact.
 *
 * Strategy:
 *  1. Try **WhatsApp Business** (`com.whatsapp.w4b`) with the `jid` extra so
 *     the chat opens pre-selected.
 *  2. Fall back to **WhatsApp** (`com.whatsapp`) with the same `jid`.
 *  3. If neither app is installed, log a warning (no crash, no toast —
 *     the caller decides how to surface this to the user).
 *
 * @param context     Application context.
 * @param imageFile   JPEG file to share (must be accessible via FileProvider).
 * @param rawPhone    Parent phone number in any common Algerian format.
 */
fun shareImageToWhatsAppBusiness(
    context: Context,
    imageFile: File,
    rawPhone: String
) {
    if (!imageFile.exists()) {
        Log.w(TAG, "⚠️ Image file missing, skipping share: ${imageFile.name}")
        return
    }

    val formattedPhone = formatAlgerianPhoneNumber(rawPhone)
    // WhatsApp JID format for personal contacts: "<countryCode><number>@s.whatsapp.net"
    val jid = "${formattedPhone}@s.whatsapp.net"

    val uri: Uri = try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        Log.e(TAG, "❌ FileProvider failed for ${imageFile.name}", e)
        return
    }

    fun buildIntent(pkg: String) = Intent(Intent.ACTION_SEND).apply {
        type  = "image/jpeg"
        setPackage(pkg)
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra("jid", jid)                         // pre-selects the contact inside WA
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val waBusinessIntent = buildIntent("com.whatsapp.w4b")
    val waIntent         = buildIntent("com.whatsapp")
    val pm               = context.packageManager

    when {
        waBusinessIntent.resolveActivity(pm) != null -> {
            Log.d(TAG, "📲 Sharing via WhatsApp Business → $formattedPhone")
            context.startActivity(waBusinessIntent)
        }
        waIntent.resolveActivity(pm) != null -> {
            Log.d(TAG, "📲 Sharing via WhatsApp → $formattedPhone (Business not installed)")
            context.startActivity(waIntent)
        }
        else -> {
            Log.w(TAG, "⚠️ WhatsApp / WhatsApp Business not installed — skipping $formattedPhone")
        }
    }
}
