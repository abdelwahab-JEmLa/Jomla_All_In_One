package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.ButtonID_5.Action

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Button_5_Imgs_Send_whatsappBuisness_Stored_Bon(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    list_M13TarificationInfos: List<M13TarificationInfos>,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter {
            it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0
        }

    val zeroOrNullPriceProducts = remember(activeVents) {
        activeVents.mapNotNull { vent ->
            val tariff =
                list_M13TarificationInfos.find { it.keyID == vent.parentM13TarificationKeyID }
            val produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos
                .datasValue.find { it.keyID == vent.parent_M1Produit_KeyId }
            if (tariff?.prixCurrency == null || tariff.prixCurrency == 0.0) produit?.nom
                ?: "Produit inconnu"
            else null
        }
    }
    val hasZeroPriceProducts = zeroOrNullPriceProducts.isNotEmpty()
    var showZeroPriceWarning by remember { mutableStateOf(false) }

    val baseName = remember(activeBonVent?.keyID, activeClient?.nom, activeVents.size) {
        if (activeBonVent == null || activeClient == null) ""
        else activeBonVent.keyID.takeLast(6) +
                "_${activeClient.nom.replace(Regex("[^A-Za-z0-9_\\-]"), "_").take(20)}" +
                "_${activeVents.size}"
    }

    val jpgUris by produceState<List<Uri>>(initialValue = emptyList(), key1 = baseName) {
        while (true) {
            value = if (baseName.isNotEmpty()) findBonJpgsFromMediaStore(
                context,
                baseName
            ) else emptyList()
            delay(1_000L)
        }
    }

    val imagesExist = jpgUris.isNotEmpty()
    var showPhoneDialog by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    if (showZeroPriceWarning) {
        Dialog(onDismissRequest = { showZeroPriceWarning = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "⚠️ خطر خسارة المال!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFF9800)
                    )
                    Text(
                        "${zeroOrNullPriceProducts.size} منتج بدون سعر محدد:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    zeroOrNullPriceProducts.forEach {
                        Text(
                            "• $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        "يرجى تحديد الأسعار قبل الإرسال لتجنب الخسائر.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { showZeroPriceWarning = false },
                            modifier = Modifier.weight(1f)
                        ) { Text("إلغاء") }
                        Button(
                            onClick = {
                                showZeroPriceWarning = false
                                val phone = activeClient?.numTelephone?.trim() ?: ""
                                if (phone.isEmpty()) {
                                    showPhoneDialog = true
                                } else {
                                    isSending = true
                                    scope.launch {
                                        sendImgsViaWhatsAppBusiness(
                                            context,
                                            phone,
                                            jpgUris,
                                            activeClient?.nom ?: ""
                                        ) { isSending = false }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            modifier = Modifier.weight(1f)
                        ) { Text("أرسل على أي حال", color = Color.White) }
                    }
                }
            }
        }
    }

    if (showPhoneDialog) {
        PhoneEntryDialog(
            clientName = activeClient?.nom ?: "Client",
            onDismiss = { showPhoneDialog = false },
            onPhoneConfirmed = { enteredPhone ->
                showPhoneDialog = false
                activeClient?.let {
                    aCentralFacade.repositorysMainSetter.upsert_M2Client(
                        it.copy(
                            numTelephone = enteredPhone
                        )
                    )
                }
                isSending = true
                scope.launch {
                    delay(200)
                    sendImgsViaWhatsAppBusiness(
                        context,
                        enteredPhone,
                        jpgUris,
                        activeClient?.nom ?: ""
                    ) { isSending = false }
                }
            }
        )
    }

    val btnColor = when {
        isSending -> MaterialTheme.colorScheme.surfaceVariant
        !imagesExist -> Color(0xFF9E9E9E)
        hasZeroPriceProducts -> Color(0xFFFF9800)
        else -> Color(0xFF1E88E5)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                if (isSending) return@FloatingActionButton
                if (activeBonVent == null) {
                    Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT)
                        .show(); return@FloatingActionButton
                }
                if (!imagesExist) {
                    Toast.makeText(
                        context,
                        "Générez d'abord le PDF — les images apparaîtront ensuite",
                        Toast.LENGTH_SHORT
                    ).show(); return@FloatingActionButton
                }
                if (hasZeroPriceProducts) {
                    showZeroPriceWarning = true; return@FloatingActionButton
                }
                val phone = activeClient?.numTelephone?.trim() ?: ""
                if (phone.isEmpty()) showPhoneDialog = true
                else {
                    isSending = true; scope.launch {
                        sendImgsViaWhatsAppBusiness(
                            context,
                            phone,
                            jpgUris,
                            activeClient?.nom ?: ""
                        ) { isSending = false }
                    }
                }
            },
            containerColor = btnColor
        ) {
            Icon(
                imageVector = when {
                    isSending -> Icons.Default.Phone
                    hasZeroPriceProducts -> Icons.Default.Warning
                    else -> Icons.Default.Image
                },
                contentDescription = null,
                tint = Color.White
            )
        }

        if (showLabels) {
            val phone = activeClient?.numTelephone?.trim() ?: ""
            val labelText = when {
                isSending -> "Envoi images…"
                !imagesExist -> "Images non prêtes"
                hasZeroPriceProducts -> "⚠️ ${zeroOrNullPriceProducts.size} prix manquant${if (zeroOrNullPriceProducts.size > 1) "s" else ""}"
                else -> buildString {
                    if (phone.isNotEmpty()) append("📱 $phone  ")
                    append("🖼️ ${jpgUris.size} img.")
                }
            }
            Text(
                text = labelText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier
                    .background(color = btnColor, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

fun createAndSaveWelcomeImage(context: Context): Uri? {
    val fileName = "welcome_marhaba.jpg"
    val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/BonsWhatsApp/"
    return try {
        val bmp = buildWelcomeBitmap()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            resolver.delete(
                collection,
                "${MediaStore.Downloads.RELATIVE_PATH} = ? AND ${MediaStore.Downloads.DISPLAY_NAME} = ?",
                arrayOf(relativePath, fileName)
            )
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "image/jpeg")
                put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(collection, values) ?: return null
            resolver.openOutputStream(uri)?.use { bmp.compress(Bitmap.CompressFormat.JPEG, 95, it) }
            values.clear(); values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            bmp.recycle(); uri
        } else {
            @Suppress("DEPRECATION")
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "BonsWhatsApp"
            ).also { it.mkdirs() }
            val outFile = File(dir, fileName)
            FileOutputStream(outFile).use { bmp.compress(Bitmap.CompressFormat.JPEG, 95, it) }
            bmp.recycle()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outFile)
        }
    } catch (e: Exception) {
        null
    }
}

 fun buildWelcomeBitmap(): Bitmap {
    val W = 900;
    val H = 400
    val bmp = createBitmap(W, H)
    val canvas = Canvas(bmp)

    canvas.drawRect(
        0f,
        0f,
        W.toFloat(),
        H.toFloat(),
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = "#1B5E20".toColorInt() })
    canvas.drawRect(
        16f,
        16f,
        W - 16f,
        H - 16f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = "#FFD700".toColorInt(); style = Paint.Style.STROKE; strokeWidth = 12f
        })
    canvas.drawRect(
        30f,
        30f,
        W - 30f,
        H - 30f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#A5D6A7"); style =
            Paint.Style.STROKE; strokeWidth = 3f
        })

    val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#FFD700".toColorInt(); textSize = 140f; textAlign =
        Paint.Align.CENTER; isFakeBoldText = true
    }
    val mainY = H / 2f - (mainPaint.fontMetrics.ascent + mainPaint.fontMetrics.descent) / 2f - 30f
    canvas.drawText("مرحبا بك", W / 2f, mainY, mainPaint)

    val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#C8E6C9".toColorInt(); textSize = 44f; textAlign = Paint.Align.CENTER
    }
    val subY =
        mainY + mainPaint.fontMetrics.descent - mainPaint.fontMetrics.ascent + 10f - (subPaint.fontMetrics.ascent + subPaint.fontMetrics.descent) / 2f
    canvas.drawText("✦  شكراً لثقتكم  ✦", W / 2f, subY, subPaint)

    return bmp
}

 fun formatPhoneForWhatsApp(raw: String): String {
    var cleaned = raw.replace(Regex("[^0-9]"), "")
    if (!cleaned.startsWith("213")) {
        if (cleaned.startsWith("0")) cleaned = cleaned.drop(1); cleaned = "213$cleaned"
    }
    return cleaned
}

@Composable
 fun PhoneEntryDialog(
    clientName: String,
    onDismiss: () -> Unit,
    onPhoneConfirmed: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) { focusRequester.requestFocus(); keyboard?.show() }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Numéro de $clientName", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it; showError = false },
                    label = { Text("Numéro de téléphone") },
                    placeholder = { Text("0XXXXXXXXX") },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Numéro invalide", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (phoneNumber.trim()
                                .isNotEmpty()
                        ) onPhoneConfirmed(phoneNumber.trim()) else showError = true
                    }),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Annuler") }
                    Button(
                        onClick = {
                            if (phoneNumber.trim().isNotEmpty()) onPhoneConfirmed(
                                phoneNumber.trim()
                            ) else showError = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) { Text("Envoyer", color = Color.White) }
                }
            }
        }
    }
}
