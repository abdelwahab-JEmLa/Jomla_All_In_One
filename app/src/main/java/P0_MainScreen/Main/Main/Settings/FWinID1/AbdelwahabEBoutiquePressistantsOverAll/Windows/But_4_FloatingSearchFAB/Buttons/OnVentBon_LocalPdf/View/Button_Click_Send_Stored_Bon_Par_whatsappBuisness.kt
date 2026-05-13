package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun Button_Click_Send_Stored_Bon_Par_whatsappBuisness(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    overridePath: String = "",
    overrideCount: Int = 0,
    list_M13TarificationInfos: List<M13TarificationInfos>,
    activeClient: M2Client?,
    on_vent_couleurs: List<M10OperationVentCouleur>,
    produits: List<M01Produit>,
    on_vent_m8: M8BonVent?,
    on_upsert_M2Client: (M2Client) -> Unit,
    ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val defaultPathSuffix = "/Pdf/"

    val activeVents = on_vent_couleurs
        .filter {
            it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve
                    && it.quantity > 0
        }

    val zeroOrNullPriceProducts = remember(activeVents) {
        activeVents.mapNotNull { vent ->
            val tariff = list_M13TarificationInfos
                .find {
                    it.keyID == vent.parentM13TarificationKeyID
                }
            val produit = produits.find { it.keyID == vent.parent_M1Produit_KeyId }
            if (tariff?.prixCurrency == null || tariff.prixCurrency == 0.0) {
                produit?.nom ?: "Produit inconnu"
            } else null
        }
    }
    var showZeroPriceWarning by remember { mutableStateOf(false) }
    val hasZeroPriceProducts = zeroOrNullPriceProducts.isNotEmpty()

    val livePdfPath by produceState(
        initialValue = on_vent_m8?.path_pdf_bon_file ?: "",
        key1 = on_vent_m8?.keyID
    ) {
        while (true) {
            val current = on_vent_m8?.path_pdf_bon_file ?: ""
            if (current != value) value = current
            kotlinx.coroutines.delay(500L)
        }
    }

    val storedPdfPath = overridePath
        .takeIf { it.isNotBlank() && !it.endsWith(defaultPathSuffix) }
        ?: livePdfPath
    val storedPdfFile = if (storedPdfPath.startsWith("/")) File(storedPdfPath) else null
    val activeCount = activeVents.size
    val liveCount by produceState(
        initialValue = on_vent_m8?.nombre_produits_don_dernier_pdf_stoked ?: 0,
        key1 = on_vent_m8?.keyID
    ) {
        while (true) {
            val current = on_vent_m8
                ?.nombre_produits_don_dernier_pdf_stoked ?: 0
            if (current != value) value = current
            kotlinx.coroutines.delay(500L)
        }
    }
    val isRealPath = storedPdfPath.isNotBlank() && !storedPdfPath.endsWith(defaultPathSuffix)
    val isMediaStorePath = isRealPath && !storedPdfPath.startsWith("/")
    val fileOnDisk =
        isMediaStorePath || (storedPdfFile?.exists() == true && storedPdfFile.length() > 0L)
    val effectiveLiveCount = if (overrideCount > 0) overrideCount else liveCount
    val pdfExists = isRealPath && fileOnDisk && effectiveLiveCount == activeCount && activeCount > 0

    var showPhoneDialog by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    // ⚠️ Zero-price warning dialog — shown before sending when any product has no price set
    if (showZeroPriceWarning) {
        Dialog(onDismissRequest = { showZeroPriceWarning = false }) {
            androidx.compose.material3.Card(
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
                        text = "⚠️ خطر خسارة المال!",
                        style = MaterialTheme.typography.titleLarge,
                        color = androidx.compose.ui.graphics.Color(0xFFFF9800)
                    )
                    Text(
                        text = "${zeroOrNullPriceProducts.size} منتج بدون سعر محدد:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    zeroOrNullPriceProducts.forEach { productName ->
                        Text(
                            text = "• $productName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "يرجى تحديد الأسعار قبل الإرسال لتجنب الخسائر.",
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
                                // Proceed despite zero prices — user accepted the risk
                                val phone = activeClient?.numTelephone?.trim() ?: ""
                                if (phone.isEmpty()) {
                                    showPhoneDialog = true
                                } else {
                                    isSending = true
                                    scope.launch {
                                        sendPdfViaWhatsAppBusiness(
                                            context = context,
                                            phoneNumber = phone,
                                            pdfFile = storedPdfFile,
                                            pdfMediaStorePath = if (isMediaStorePath) storedPdfPath else null,
                                            clientName = activeClient?.nom ?: "",
                                            packageName = context.packageName,
                                            onResult = { isSending = false }
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFFFF9800)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "أرسل على أي حال",
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
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
                activeClient?.let { client ->
                    on_upsert_M2Client(
                        client.copy(numTelephone = enteredPhone)
                    )
                }
                isSending = true
                scope.launch {
                    delay(200)
                    sendPdfViaWhatsAppBusiness(
                        context = context,
                        phoneNumber = enteredPhone,
                        pdfFile = storedPdfFile,
                        pdfMediaStorePath = if (isMediaStorePath) storedPdfPath else null,
                        clientName = activeClient?.nom ?: "",
                        packageName = context.packageName,
                        onResult = { isSending = false }
                    )
                }
            }
        )
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
                if (on_vent_m8 == null) {
                    Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
                    return@FloatingActionButton
                }
                if (!pdfExists) {
                    Toast.makeText(
                        context,
                        "Générez d'abord le PDF (bouton orange)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@FloatingActionButton
                }
                // Guard: warn before sending if any product has a zero price
                if (zeroOrNullPriceProducts.isNotEmpty()) {
                    showZeroPriceWarning = true
                    return@FloatingActionButton
                }
                val phone = activeClient?.numTelephone?.trim() ?: ""
                if (phone.isEmpty()) {
                    showPhoneDialog = true
                } else {
                    isSending = true
                    scope.launch {
                        sendPdfViaWhatsAppBusiness(
                            context = context,
                            phoneNumber = phone,
                            pdfFile = storedPdfFile,
                            pdfMediaStorePath = if (isMediaStorePath) storedPdfPath else null,
                            clientName = activeClient?.nom ?: "",
                            packageName = context.packageName,
                            onResult = { isSending = false }
                        )
                    }
                }
            },
            containerColor = when {
                isSending -> MaterialTheme.colorScheme.surfaceVariant
                !pdfExists -> Color(0xFF9E9E9E)
                hasZeroPriceProducts -> Color(0xFFFF9800)   // orange — prix manquants
                else -> Color(0xFF25D366)   // vert — tout est bon
            }
        ) {
            Icon(
                imageVector = when {
                    isSending -> Icons.Default.Phone
                    hasZeroPriceProducts -> Icons.Default.Warning
                    else -> Icons.Default.Send
                },
                contentDescription = "Envoyer via WhatsApp Business",
                tint = Color.White
            )
        }

        if (showLabels) {
            val creationDate = storedPdfPath
                .split("/")
                .firstOrNull { it.matches(Regex("\\d{2}_\\d{2}")) }
                ?.replace("_", "/")
                ?: ""
            val phone = activeClient?.numTelephone?.trim() ?: ""

            val labelText = when {
                isSending -> "Envoi…"
                !pdfExists -> "PDF non prêt"
                hasZeroPriceProducts ->
                    "⚠️ ${zeroOrNullPriceProducts.size} prix manquant${if (zeroOrNullPriceProducts.size > 1) "s" else ""}"

                else -> buildString {
                    if (phone.isNotEmpty()) append("📱 $phone")
                    if (creationDate.isNotEmpty()) {
                        if (isNotEmpty()) append("  ")
                        append("📅 $creationDate")
                    }
                    if (isNotEmpty()) append("  ")
                    append("📦 $effectiveLiveCount art.")
                }
            }

            Text(
                text = labelText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier

                    .background(
                        color = when {
                            isSending -> MaterialTheme.colorScheme.surfaceVariant
                            !pdfExists -> Color(0xFF9E9E9E)
                            hasZeroPriceProducts -> Color(0xFFFF9800)
                            else -> Color(0xFF25D366)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

private fun sendPdfViaWhatsAppBusiness(
    context: Context,
    phoneNumber: String,
    pdfFile: File?,
    pdfMediaStorePath: String?,
    clientName: String,
    packageName: String,
    onResult: () -> Unit,
) {
    try {
        val pdfUri = when {
            pdfFile != null && pdfFile.exists() ->
                androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "$packageName.fileprovider",
                    pdfFile
                )

            !pdfMediaStorePath.isNullOrBlank() ->
                queryMediaStoreUri(context, pdfMediaStorePath)

            else -> null
        }
        if (pdfUri == null) {
            Toast.makeText(context, "Fichier PDF introuvable", Toast.LENGTH_LONG).show()
            onResult(); return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage("com.whatsapp.w4b")
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
            putExtra("jid", "${formatPhoneForWhatsApp(phoneNumber)}@s.whatsapp.net")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
        Toast.makeText(context, "Ouverture WhatsApp Business pour $clientName", Toast.LENGTH_SHORT)
            .show()
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

private fun queryMediaStoreUri(context: Context, relativePath: String): android.net.Uri? {
    return try {
        val fileName = relativePath.substringAfterLast("/")
        val folder = relativePath.removePrefix("Downloads/").substringBeforeLast("/")
        val collection = android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(android.provider.MediaStore.Downloads._ID)
        val selection =
            "${android.provider.MediaStore.Downloads.DISPLAY_NAME} = ? AND ${android.provider.MediaStore.Downloads.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf(fileName, "%$folder%")
        context.contentResolver.query(collection, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(android.provider.MediaStore.Downloads._ID))
                    android.content.ContentUris.withAppendedId(collection, id)
                } else null
            }
    } catch (e: Exception) {
        null
    }
}

private fun formatPhoneForWhatsApp(raw: String): String {
    var cleaned = raw.replace(Regex("[^0-9]"), "")
    if (!cleaned.startsWith("213")) {
        if (cleaned.startsWith("0")) cleaned = cleaned.drop(1)
        cleaned = "213$cleaned"
    }
    return cleaned
}

@Composable
private fun PhoneEntryDialog(
    clientName: String,
    onDismiss: () -> Unit,
    onPhoneConfirmed: (String) -> Unit,
) {
    var phoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Numéro de $clientName",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        showError = false
                    },
                    label = { Text("Numéro de téléphone") },
                    placeholder = { Text("0XXXXXXXXX") },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Numéro invalide", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val cleaned = phoneNumber.trim()
                            if (cleaned.isNotEmpty()) onPhoneConfirmed(cleaned)
                            else showError = true
                        }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
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
                            val cleaned = phoneNumber.trim()
                            if (cleaned.isNotEmpty()) onPhoneConfirmed(cleaned)
                            else showError = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366)
                        )
                    ) {
                        Text("Envoyer", color = Color.White)
                    }
                }
            }
        }
    }
}
