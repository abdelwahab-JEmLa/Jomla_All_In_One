package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID8.Action

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun clipboard_copie(
    context: Context,
    message: String = "",          // ← NEW: bon caption (total, count, date)
) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("bon_message", message))
    val clipHint = if (message.isNotEmpty()) " — légende copiée ✓" else ""
    Toast.makeText(
        context, clipHint, Toast.LENGTH_SHORT
    ).show()
}

fun sendImgsViaWhatsAppBusiness(
    context: Context,
    phoneNumber: String,
    imageUris: List<Uri>,
    clientName: String,
    message: String = "",          // ← NEW: bon caption (total, count, date)
    onResult: () -> Unit
) {
    try {
        if (imageUris.isEmpty()) {
            Toast.makeText(context, "Aucune image à envoyer", Toast.LENGTH_SHORT)
                .show(); onResult(); return
        }

        // ── Copy message to clipboard so the user can paste it in WhatsApp ──
        if (message.isNotEmpty()) {
            clipboard_copie(context,message)
        }

        val allUris = createAndSaveWelcomeImage(context)?.let { imageUris + it } ?: imageUris
        val jid = "${formatPhoneForWhatsApp(phoneNumber)}@s.whatsapp.net"

        val intent = if (allUris.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                setPackage("com.whatsapp.w4b")
                putExtra(Intent.EXTRA_STREAM, allUris.first())
                putExtra("jid", jid)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/jpeg"
                setPackage("com.whatsapp.w4b")
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(allUris))
                putExtra("jid", jid)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        context.startActivity(intent)

        val clipHint = if (message.isNotEmpty()) " — légende copiée ✓" else ""
        Toast.makeText(
            context,
            "Ouverture WhatsApp Business pour $clientName " + "(${allUris.size} image${if (allUris.size > 1) "s" else ""})$clipHint",
            Toast.LENGTH_SHORT
        ).show()

    } catch (e: Exception) {
        Toast.makeText(
            context, "WhatsApp Business non installé ou erreur: ${e.message}", Toast.LENGTH_LONG
        ).show()
    } finally {
        onResult()
    }
}

private fun buildBonMessage(
    vents: List<M10OperationVentCouleur>,
    tariffs: List<M13TarificationInfos>,
): String {      //<--
    val activeVents = vents.filter {
        it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0
    }
    val total = activeVents.sumOf { vent ->
        val price =
            tariffs.find { it.keyID == vent.parentM13TarificationKeyID }?.prixCurrency ?: 0.0
        price * vent.quantity
    }
    val formattedTotal = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(total)
    val dateStr = SimpleDateFormat("dd MMM hh:mm a", Locale.FRANCE).format(Date())
    val count = activeVents.size
    return "Totale = $formattedTotal DA\n$count Produit${if (count > 1) "s" else ""}\nBon $dateStr"
}
