package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_5.Action

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun sendImgsViaWhatsAppBusiness(
    context: Context,
    phoneNumber: String,
    imageUris: List<Uri>,
    clientName: String,
    onResult: () -> Unit
) {
    try {
        if (imageUris.isEmpty()) {
            Toast.makeText(context, "Aucune image à envoyer", Toast.LENGTH_SHORT)
                .show(); onResult(); return
        }
        val allUris = createAndSaveWelcomeImage(context)?.let { imageUris + it } ?: imageUris
        val jid = "${formatPhoneForWhatsApp(phoneNumber)}@s.whatsapp.net"
        val intent = if (allUris.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"; setPackage("com.whatsapp.w4b"); putExtra(
                Intent.EXTRA_STREAM,
                allUris.first()
            ); putExtra("jid", jid); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/jpeg"; setPackage("com.whatsapp.w4b"); putParcelableArrayListExtra(
                Intent.EXTRA_STREAM,
                ArrayList(allUris)
            ); putExtra("jid", jid); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        context.startActivity(intent)
        Toast.makeText(
            context,
            "Ouverture WhatsApp Business pour $clientName (${allUris.size} image${if (allUris.size > 1) "s" else ""})",
            Toast.LENGTH_SHORT
        ).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "WhatsApp Business non installé ou erreur: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    } finally {
        onResult()
    }
}
