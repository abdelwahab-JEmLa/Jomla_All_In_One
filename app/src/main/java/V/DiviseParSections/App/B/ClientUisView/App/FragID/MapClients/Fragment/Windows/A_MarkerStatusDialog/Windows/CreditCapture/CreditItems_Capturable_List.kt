package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.CreditCapture

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Z.Component.Situation_Card.View.Situation_Card_ItemView
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.content.ClipData
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CREDIT_STATES = setOf(
    M8BonVent.EtateActuellementEst.Credit,
    M8BonVent.EtateActuellementEst.Versemment,
    M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
    M8BonVent.EtateActuellementEst.Demande_Versemet,
    M8BonVent.EtateActuellementEst.New_Situation_Credit,
)

fun getTodayCreditImages(context: Context, clientKeyID: String?): List<Uri> {
    if (clientKeyID.isNullOrEmpty()) return emptyList()
    val safeKey = clientKeyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
    val mmDd = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
    val folderPath = "credit_trxs/$safeKey/$mmDd"
    val uris = mutableListOf<Uri>()

    try {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%$folderPath%")

        context.contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                uris.add(ContentUris.withAppendedId(collection, id))
            }
        }
    } catch (_: Exception) {}

    if (uris.isEmpty()) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dir = File(downloadsDir, folderPath)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles { _, name ->
                    val lower = name.lowercase()
                    lower.endsWith(".webp") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                }?.sortedBy { it.name }?.forEach { file ->
                    uris.add(
                        androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    )
                }
            }
        } catch (_: Exception) {}
    }
    return uris
}

fun sendImagesToWhatsApp(context: Context, phone: String, uris: List<Uri>) {
    if (uris.isEmpty()) {
        Toast.makeText(context, "لا توجد صور لإرسالها", Toast.LENGTH_SHORT).show()
        return
    }

    var formattedPhone = phone.replace(Regex("[^0-9]"), "")
    if (formattedPhone.startsWith("0")) {
        formattedPhone = "213" + formattedPhone.substring(1)
    } else if (!formattedPhone.startsWith("213") && formattedPhone.length == 9) {
        formattedPhone = "213$formattedPhone"
    }
    val jid = "$formattedPhone@s.whatsapp.net"

    val packageName = "com.whatsapp.w4b"
    val altPackage = "com.whatsapp"
    val intentAction = if (uris.size == 1) Intent.ACTION_SEND else Intent.ACTION_SEND_MULTIPLE

    @Suppress("DEPRECATION")
    val allCandidates = context.packageManager.queryIntentActivities(
        Intent(intentAction).apply { type = "image/*" }, 0
    )
    val resolvedInfo = allCandidates.firstOrNull { it.activityInfo.packageName == packageName }
        ?: allCandidates.firstOrNull { it.activityInfo.packageName == altPackage }
    val targetPackage = resolvedInfo?.activityInfo?.packageName ?: packageName

    fun baseIntent(): Intent =
        if (uris.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uris.first())
                putExtra("jid", jid)
                clipData = ClipData.newRawUri("CreditImage", uris.first())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                putExtra("jid", jid)
                val clip = ClipData.newRawUri("CreditImages", uris.first())
                uris.drop(1).forEach { clip.addItem(ClipData.Item(it)) }
                clipData = clip
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    listOf(packageName, altPackage).forEach { pkg ->
        uris.forEach { uri ->
            try {
                context.grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
        }
    }

    val directIntent = baseIntent().apply {
        if (resolvedInfo?.activityInfo != null) {
            component = android.content.ComponentName(resolvedInfo.activityInfo.packageName, resolvedInfo.activityInfo.name)
        } else {
            setPackage(targetPackage)
        }
    }

    try {
        context.startActivity(directIntent)
    } catch (_: Exception) {
        try {
            val chooserIntent = Intent.createChooser(baseIntent(), "مشاركة صور الدين").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (_: Exception) {
            try {
                val directChatIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$formattedPhone")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(directChatIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "WhatsApp non installé", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CreditItems_Capturable_List(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client?,
    triggerCaptureVersion: Int = 0,
) {
    val context: Context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isExpanded by remember { mutableStateOf(false) }
    var todayImages by remember(relative_M2Client?.keyID, triggerCaptureVersion) {
        mutableStateOf(getTodayCreditImages(context, relative_M2Client?.keyID))
    }

    val allBons: List<M8BonVent> = remember(
        relative_M2Client?.keyID,
        aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
    ) {
        aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
            .filter {
                it.parent_M2Client_KeyID == relative_M2Client?.keyID &&
                it.etateActuellementEst in CREDIT_STATES
            }
            .sortedByDescending { it.creationTimestamps }
    }

    var captureCountInput by remember { mutableStateOf("10") }
    var captureCount by remember { mutableStateOf(10) }
    var showCountDialog by remember { mutableStateOf(false) }
    val countFocusRequester = remember { FocusRequester() }

    val itemsToDisplay = remember(allBons, captureCount) { allBons.take(captureCount) }

    val ctrl = rememberMultiCaptureController()

    var captured by remember { mutableStateOf<List<Pair<ImageBitmap, String>>>(emptyList()) }
    var showPreview by remember { mutableStateOf(false) }
    var whatsappSendRequest by remember { mutableStateOf<String?>(null) }
    val isCapturing = remember { mutableStateOf(false) }

    val sdf = remember { SimpleDateFormat("mm-ss-SSS", Locale.FRANCE) }

    fun buildImageName(idx: Int, key: String): String {
        val pts = key.split("|")
        val ts = pts.getOrNull(0)?.toLongOrNull() ?: System.currentTimeMillis()
        val etat = pts.getOrNull(2) ?: key
        return "${idx}_${sdf.format(Date(ts))}_${etat}"
    }

    fun mapRawToNamed(raw: List<Pair<String, ImageBitmap>>) =
        raw.mapIndexed { idx, (k, bmp) -> bmp to buildImageName(idx, k) }

    LaunchedEffect(triggerCaptureVersion) {
        if (triggerCaptureVersion == 0) return@LaunchedEffect
        val phone = relative_M2Client?.numTelephone
        if (phone.isNullOrEmpty()) return@LaunchedEffect
        if (itemsToDisplay.isEmpty()) return@LaunchedEffect
        whatsappSendRequest = phone
    }

    LaunchedEffect(whatsappSendRequest) {
        val phone = whatsappSendRequest ?: return@LaunchedEffect
        if (isCapturing.value) {
            Log.w("CREDIT_CAPTURE", "Capture already in progress, skipping duplicate request")
            return@LaunchedEffect
        }
        isCapturing.value = true
        Log.d("CREDIT_CAPTURE", "Starting capture flow for phone: $phone. isExpanded: $isExpanded, itemsToDisplay: ${itemsToDisplay.size}")

        if (!isExpanded) {
            Log.d("CREDIT_CAPTURE", "List is collapsed — setting isExpanded = true for capture")
            isExpanded = true
            delay(300)
        }

        var retry = 0
        while (ctrl.size() < itemsToDisplay.size && retry < 5) {
            Log.d("CREDIT_CAPTURE", "Waiting for items composition: ctrl.size()=${ctrl.size()} expected=${itemsToDisplay.size} (attempt $retry)")
            delay(200)
            retry++
        }

        Log.d("CREDIT_CAPTURE", "Executing ctrl.captureAll(). Registered items: ${ctrl.size()}")
        val raw = ctrl.captureAll()
        Log.d("CREDIT_CAPTURE", "ctrl.captureAll() returned ${raw.size} bitmap(s)")
        val namedImages = mapRawToNamed(raw)

        if (namedImages.isEmpty()) {
            Log.w("CREDIT_CAPTURE", "namedImages is empty after capture! Aborting.")
            whatsappSendRequest = null
            isCapturing.value = false
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "لم يتم التقاط أي عنصر", Toast.LENGTH_SHORT).show()
            }
            return@LaunchedEffect
        }

        val mmDd = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
        val safeKey = relative_M2Client?.keyID?.replace(Regex("[^a-zA-Z0-9_\\-]"), "_") ?: ""
        val todayFolderPath = "Download/credit_trxs/$safeKey/$mmDd"

        Log.d("CREDIT_CAPTURE", "Saving ${namedImages.size} images to MediaStore path '$todayFolderPath'...")
        val savedUris: List<Uri> = withContext(Dispatchers.IO) {
            relative_M2Client?.let {
                saveAllToMediaStore(
                    bitmaps = namedImages.map { (img, lbl) -> img.asAndroidBitmap() to lbl },
                    context = context,
                    clientKeyID = it.keyID,
                    customFolderPath = todayFolderPath
                )
            } ?: emptyList()
        }

        Log.d("CREDIT_CAPTURE", "saveAllToMediaStore returned ${savedUris.size} URIs")

        if (savedUris.isNotEmpty()) {
            val updated = getTodayCreditImages(context, relative_M2Client?.keyID)
            todayImages = updated
            Log.d("CREDIT_CAPTURE", "Updated todayImages count: ${updated.size}")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "تم التقاط وحفظ ${savedUris.size} صورة", Toast.LENGTH_SHORT).show()
                Log.d("CREDIT_CAPTURE", "Displayed Toast: تم التقاط وحفظ ${savedUris.size} صورة")
            }
            sendImagesToWhatsApp(context, phone, updated)
        } else {
            Log.e("CREDIT_CAPTURE", "savedUris is empty after saving!")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "فشل في حفظ الصور", Toast.LENGTH_SHORT).show()
            }
        }

        whatsappSendRequest = null
        isCapturing.value = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row with Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Cacher" else "Afficher"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "معاملات الدين (${allBons.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (allBons.isNotEmpty()) {
                        Button(
                            onClick = { showCountDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("التقاط $captureCount الأخيرة", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (todayImages.isNotEmpty()) {
                        Button(
                            onClick = {
                                val updated = getTodayCreditImages(context, relative_M2Client?.keyID)
                                todayImages = updated
                                relative_M2Client?.numTelephone?.let { phone ->
                                    sendImagesToWhatsApp(context, phone, updated)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("صور اليوم (${todayImages.size})", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            if (isExpanded) {
                Spacer(Modifier.height(8.dp))
                if (allBons.isEmpty()) {
                    Text(
                        "لا توجد معاملات دين",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsToDisplay.forEachIndexed { index, b ->
                            val cap = rememberCapturableLayer()
                            val capKey = "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}"

                            DisposableEffect(capKey) {
                                ctrl.register(capKey, index, { cap.hasBeenDrawn }) { cap.capture() }
                                onDispose { ctrl.unregister(capKey) }
                            }

                            Box(modifier = cap.modifier) {
                                Situation_Card_ItemView(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { aCentralFacade.repositorysMainSetter.update_M8BonVent(it) },
                                    onDelete = { aCentralFacade.repositorysMainSetter.delete_M8BonVent(it) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCountDialog) {
        AlertDialog(
            onDismissRequest = { showCountDialog = false },
            title = { Text("عدد العناصر للالتقاط") },
            text = {
                OutlinedTextField(
                    value = captureCountInput,
                    onValueChange = { input -> if (input.all { it.isDigit() }) captureCountInput = input },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val count = captureCountInput.toIntOrNull() ?: 10
                            captureCount = count.coerceAtLeast(1)
                            showCountDialog = false
                            relative_M2Client?.numTelephone?.let { phone ->
                                whatsappSendRequest = phone
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(countFocusRequester)
                )
                LaunchedEffect(Unit) {
                    countFocusRequester.requestFocus()
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = captureCountInput.toIntOrNull() ?: 10
                        captureCount = count.coerceAtLeast(1)
                        showCountDialog = false
                        relative_M2Client?.numTelephone?.let { phone ->
                            whatsappSendRequest = phone
                        }
                    }
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                Button(onClick = { showCountDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // ── Dialog preview ────────────────────────────────────────────────────
    if (showPreview && captured.isNotEmpty()) {
        Afficheur_locale_Image_Captured(
            capturedBitmaps = captured,
            onDismiss = { showPreview = false; captured = emptyList() },
            onSave = { bmpList ->
                relative_M2Client?.let { client ->
                    scope.launch(Dispatchers.IO) {
                        val mmDd = SimpleDateFormat("MM_dd", Locale.getDefault()).format(Date())
                        val safeKey = client.keyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
                        val saved = saveAllToMediaStore(
                            bitmaps = bmpList,
                            context = context,
                            clientKeyID = client.keyID,
                            customFolderPath = "Download/credit_trxs/$safeKey/$mmDd"
                        )
                        val updated = getTodayCreditImages(context, client.keyID)
                        withContext(Dispatchers.Main) {
                            todayImages = updated
                            Toast.makeText(context, "تم حفظ ${saved.size} صورة", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                showPreview = false; captured = emptyList()
            },
        )
    }
}
