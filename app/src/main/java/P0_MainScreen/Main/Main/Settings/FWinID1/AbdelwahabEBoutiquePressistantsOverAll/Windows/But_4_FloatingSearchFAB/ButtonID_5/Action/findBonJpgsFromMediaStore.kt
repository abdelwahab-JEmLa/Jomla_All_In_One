package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_5.Action

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
fun findBonJpgsFromMediaStore(context: Context, baseName: String): List<Uri> {
    if (baseName.isEmpty()) return emptyList()
    return try {
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        context.contentResolver.query(
            collection,
            arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME),
            "${MediaStore.Downloads.DISPLAY_NAME} LIKE ? AND ${MediaStore.Downloads.RELATIVE_PATH} LIKE ?",
            arrayOf("$baseName%.jpg", "%BonsWhatsApp%"),
            "${MediaStore.Downloads.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            buildList {
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                while (cursor.moveToNext()) add(
                    ContentUris.withAppendedId(
                        collection,
                        cursor.getLong(idCol)
                    )
                )
            }
        } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}
