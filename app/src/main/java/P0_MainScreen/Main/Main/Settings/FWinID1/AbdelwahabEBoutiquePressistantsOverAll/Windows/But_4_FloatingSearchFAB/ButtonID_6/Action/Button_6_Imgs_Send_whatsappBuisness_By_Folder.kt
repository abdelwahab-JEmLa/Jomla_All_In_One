package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_6.Action

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_5.Action.createAndSaveWelcomeImage
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_5.Action.formatPhoneForWhatsApp
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File

interface Send_To_WB_Int {
    fun getFileUris(context: Context, folder: File, extension: String): List<Uri>
    fun send(context: Context, folder: File, extension: String, phoneNumber: String, clientName: String, onResult: () -> Unit)
}

class Send_To_WB_Impl : Send_To_WB_Int {
    override fun getFileUris(context: Context, folder: File, extension: String): List<Uri> =
        if (!folder.exists() || !folder.isDirectory) emptyList()
        else folder.listFiles { f -> f.extension.equals(extension, ignoreCase = true) }
            ?.sortedBy { it.name }
            ?.map { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) }
            ?: emptyList()

    override fun send(context: Context, folder: File, extension: String, phoneNumber: String, clientName: String, onResult: () -> Unit) =
        sendImgsViaWhatsAppBusiness(context, phoneNumber, getFileUris(context, folder, extension), clientName, onResult)


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
            val jid = "${formatPhoneForWhatsApp(phoneNumber)}@s.whatsapp.net"
            val intent = if (imageUris.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"; setPackage("com.whatsapp.w4b"); putExtra(
                    Intent.EXTRA_STREAM,
                    imageUris.first()
                ); putExtra("jid", jid); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/jpeg"; setPackage("com.whatsapp.w4b"); putParcelableArrayListExtra(
                    Intent.EXTRA_STREAM,
                    ArrayList(imageUris)
                ); putExtra("jid", jid); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            context.startActivity(intent)
            Toast.makeText(
                context,
                "Ouverture WhatsApp Business pour $clientName (${imageUris.size} image${if (imageUris.size > 1) "s" else ""})",
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

}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Button_6_Imgs_Send_whatsappBuisness_By_Folder(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    downold: String = "/storage/emulated/0/Download/",
    folder_path: File = File(downold, "BonsWhatsApp/05_08"),
    extention_files_a_find: String = "jpg",
    num: String = "553885037",
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val send_To_WB_Int: Send_To_WB_Int = remember { Send_To_WB_Impl() }
    var isSending by remember { mutableStateOf(false) }
    val imagesExist = remember(folder_path.absolutePath) {
        folder_path.exists() && folder_path.listFiles { f -> f.extension.equals(extention_files_a_find, ignoreCase = true) }?.isNotEmpty() == true
    }
    val btnColor = if (isSending || !imagesExist) Color(0xFF9E9E9E) else Color(0xFF43A047)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(onClick = {
            if (!isSending && imagesExist) { isSending = true
                scope.launch { send_To_WB_Int.send(context, folder_path, extention_files_a_find, num, folder_path.name) { isSending = false } }
            }
        }, modifier = modifier.size(56.dp), containerColor = btnColor) {
            Icon(Icons.Default.Image, null, tint = Color.White)
        }
        if (showLabels) Text(
            text = if (isSending) "Envoi…" else if (!imagesExist) "Aucune image" else "📁 ${folder_path.name}  📱 $num",
            style = MaterialTheme.typography.bodySmall, color = Color.White,
            modifier = Modifier.background(btnColor, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
