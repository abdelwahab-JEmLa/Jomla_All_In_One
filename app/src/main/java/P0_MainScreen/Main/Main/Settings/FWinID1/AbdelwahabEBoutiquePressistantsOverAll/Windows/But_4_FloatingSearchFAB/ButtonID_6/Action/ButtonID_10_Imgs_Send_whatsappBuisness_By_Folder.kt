package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_6.Action

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
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

interface Send_To_WB_Int {
    fun getFileUris(context: Context, folder: File, extension: String): List<Uri>
    fun send(context: Context, folder: File, extension: String, phoneNumber: String, clientName: String, onResult: () -> Unit)
}

class Send_To_WB_Impl : Send_To_WB_Int {

    override fun getFileUris(context: Context, folder: File, extension: String): List<Uri> {
        if (!folder.exists() || !folder.isDirectory) return emptyList()
        return (folder.listFiles()
            ?.filter { it.extension.equals(extension, ignoreCase = true) }
            ?.sortedBy { it.name }
            ?: emptyList())
            .map { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) }
    }

    override fun send(context: Context, folder: File, extension: String, phoneNumber: String, clientName: String, onResult: () -> Unit) {
        sendImgsViaWhatsAppBusiness(context, phoneNumber, getFileUris(context, folder, extension), clientName, onResult)
    }

    fun sendImgsViaWhatsAppBusiness(context: Context, phoneNumber: String, imageUris: List<Uri>, clientName: String, onResult: () -> Unit) {
        try {
            if (imageUris.isEmpty()) {
                Toast.makeText(context, "Aucune image à envoyer", Toast.LENGTH_SHORT).show()
                onResult(); return
            }
            val jid = "${formatPhoneForWhatsApp(phoneNumber)}@s.whatsapp.net"
            val intent = if (imageUris.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"; setPackage("com.whatsapp.w4b")
                    putExtra(Intent.EXTRA_STREAM, imageUris.first()); putExtra("jid", jid)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/jpeg"; setPackage("com.whatsapp.w4b")
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        kotlin.collections.ArrayList(imageUris)
                    ); putExtra("jid", jid)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            context.startActivity(intent)
            Toast.makeText(context, "Ouverture WhatsApp Business pour $clientName (${imageUris.size} image${if (imageUris.size > 1) "s" else ""})", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp Business non installé ou erreur: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            onResult()
        }
    }

    fun formatPhoneForWhatsApp(raw: String): String {
        var c = raw.replace(Regex("[^0-9]"), "")
        if (!c.startsWith("213")) { if (c.startsWith("0")) c = c.drop(1); c = "213$c" }
        return c
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ButtonID_10_Imgs_Send_whatsappBuisness_By_Folder(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    central_storage: String = "/storage/emulated/0/Download/",
    parent_folder: File = File(central_storage, "Image_Compose_Screen"),
    child: String = "",
    extantion_files_a_find: String = "webp",
    num: String = "+213553885037",
    folder_path: File = File(parent_folder, child),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sender: Send_To_WB_Int = remember { Send_To_WB_Impl() }
    var isSending by remember { mutableStateOf(false) }
    var imagesExist by remember { mutableStateOf(false) }

    LaunchedEffect(folder_path.absolutePath) {
        withContext(Dispatchers.IO) {
            imagesExist = folder_path.exists() &&
                    folder_path.listFiles { f ->
                        f.extension.equals(
                            extantion_files_a_find,
                            ignoreCase = true
                        )
                    }?.isNotEmpty() == true
        }
    }

    val btnColor = if (isSending || !imagesExist) Color(0xFF9E9E9E) else Color(0xFF43A047)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = {
                if (!isSending && imagesExist) {
                    isSending = true
                    scope.launch {
                        sender.send(context, folder_path, extantion_files_a_find, num, folder_path.name) { isSending = false }
                    }
                }
            },
            modifier = modifier.size(56.dp),
            containerColor = btnColor,
        ) { Icon(Icons.Default.Image, null, tint = Color.White) }

        if (showLabels) {
            Text(
                text = when { isSending -> "Envoi…"; !imagesExist -> "Aucune image"; else -> "📁 ${folder_path.name}  📱 $num" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.background(btnColor, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}
