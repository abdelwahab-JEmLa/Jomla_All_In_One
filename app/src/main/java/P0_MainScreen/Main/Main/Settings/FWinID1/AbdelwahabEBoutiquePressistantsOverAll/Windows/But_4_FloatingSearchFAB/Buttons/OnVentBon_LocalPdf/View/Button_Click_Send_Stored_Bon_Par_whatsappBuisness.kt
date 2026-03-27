package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import org.koin.compose.koinInject
import java.io.File

private const val TAG_WA = "WA_SendButton"

@Composable
fun Button_Click_Send_Stored_Bon_Par_whatsappBuisness(
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeClient  = focusedValuesGetter.activeOnVentM2ClientInfos

    val defaultPathSuffix = "/Pdf/"

    // ── Reactive PDF path ────────────────────────────────────────────────────
    // focusedValuesGetter is a plain getter (not a StateFlow), so Compose never
    // recomposes when the underlying M8BonVent changes after upsert().
    // produceState polls the getter every 500ms so the WA button catches the
    // update within half a second — no manual callback wiring needed.
    val livePdfPath by produceState(
        initialValue = activeBonVent?.path_pdf_bon_file ?: "",
        key1 = activeBonVent?.keyID
    ) {
        while (true) {
            val current = focusedValuesGetter.activeOnVent_M8BonVent?.path_pdf_bon_file ?: ""
            if (current != value) value = current
            kotlinx.coroutines.delay(500L)
        }
    }

    val storedPdfPath = livePdfPath
    // Handle both absolute path and MediaStore relative path
    val storedPdfFile = if (storedPdfPath.startsWith("/")) File(storedPdfPath) else null
    val isRealPath = storedPdfPath.isNotBlank() && !storedPdfPath.endsWith(defaultPathSuffix)
    val isMediaStorePath = isRealPath && !storedPdfPath.startsWith("/")
    val pdfExists = isRealPath && (isMediaStorePath || (storedPdfFile?.exists() == true && storedPdfFile.length() > 0L))

    Log.d(TAG_WA, "── WA_SendButton recompose ──")
    Log.d(TAG_WA, "  activeBonVent   = ${activeBonVent?.keyID ?: "NULL"}")
    Log.d(TAG_WA, "  storedPdfPath   = $storedPdfPath")
    Log.d(TAG_WA, "  isMediaStorePath= $isMediaStorePath")
    Log.d(TAG_WA, "  storedPdfFile   = ${storedPdfFile?.absolutePath}  exists=${storedPdfFile?.exists()}  size=${storedPdfFile?.length()}")
    Log.d(TAG_WA, "  pdfExists       = $pdfExists")
    Log.d(TAG_WA, "  clientPhone     = ${activeClient?.numTelephone ?: "NULL"}")

    // UI state
    var showPhoneDialog by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    // ── Phone-entry dialog (shown when client has no phone number) ───────────
    if (showPhoneDialog) {
        PhoneEntryDialog(
            clientName = activeClient?.nom ?: "Client",
            onDismiss = { showPhoneDialog = false },
            onPhoneConfirmed = { enteredPhone ->
                showPhoneDialog = false
                // Persist the phone number on the client record
                activeClient?.let { client ->
                    aCentralFacade.repositorysMainSetter.upsert_M2Client(
                        client.copy(numTelephone = enteredPhone)
                    )
                }
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

    // ── Button ────────────────────────────────────────────────────────────────
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                if (isSending) return@FloatingActionButton

                Log.d(TAG_WA, "── onClick ──")
                Log.d(TAG_WA, "  activeBonVent=${ activeBonVent?.keyID }  pdfExists=$pdfExists  storedPdfPath=$storedPdfPath")

                // Guard: need an active bon and a generated PDF
                if (activeBonVent == null) {
                    Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
                    Log.w(TAG_WA, "  ❌ activeBonVent null → abort")
                    return@FloatingActionButton
                }
                if (!pdfExists) {
                    Toast.makeText(
                        context,
                        "Générez d'abord le PDF (bouton orange)",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w(TAG_WA, "  ❌ pdfExists=false → abort  storedPdfPath=$storedPdfPath")
                    return@FloatingActionButton
                }

                val phone = activeClient?.numTelephone?.trim() ?: ""
                Log.d(TAG_WA, "  clientPhone=$phone")
                if (phone.isEmpty()) {
                    Log.d(TAG_WA, "  → no phone, showing dialog")
                    // No phone → show outlined dialog + keyboard
                    showPhoneDialog = true
                } else {
                    Log.d(TAG_WA, "  → phone exists, sending directly")
                    // Phone exists → send directly
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
                !pdfExists -> Color(0xFF9E9E9E)          // grey  = no PDF yet
                else -> Color(0xFF25D366)                 // green = ready to send
            }
        ) {
            Icon(
                imageVector = if (isSending) Icons.Default.Phone else Icons.Default.Send,
                contentDescription = "Envoyer via WhatsApp Business",
                tint = Color.White
            )
        }

        if (showLabels) {
            Text(
                text = when {
                    isSending -> "Envoi…"
                    !pdfExists -> "PDF non prêt"
                    else -> "Envoyer WhatsApp"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier
                    .let { m ->
                        androidx.compose.foundation.layout.Box(
                            modifier = m
                                .then(
                                    Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                        ) {}
                        m
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ── Internal: send the stored local PDF to WhatsApp Business ─────────────────
private fun sendPdfViaWhatsAppBusiness(
    context: Context,
    phoneNumber: String,
    pdfFile: File?,               // absolute File path (may be null if MediaStore path)
    pdfMediaStorePath: String?,   // MediaStore relative path e.g. "Downloads/BonsWhatsApp/..."
    clientName: String,
    packageName: String,
    onResult: () -> Unit,
) {
    Log.d(TAG_WA, "── sendPdfViaWhatsAppBusiness ──")
    Log.d(TAG_WA, "  phoneNumber      = $phoneNumber")
    Log.d(TAG_WA, "  pdfFile          = ${pdfFile?.absolutePath}  exists=${pdfFile?.exists()}")
    Log.d(TAG_WA, "  pdfMediaStorePath= $pdfMediaStorePath")

    try {
        // Resolve the URI: prefer absolute file, fall back to MediaStore
        val pdfUri = when {
            pdfFile != null && pdfFile.exists() -> {
                Log.d(TAG_WA, "  → using absolute file path for URI")
                androidx.core.content.FileProvider.getUriForFile(
                    context, "$packageName.fileprovider", pdfFile
                )
            }
            !pdfMediaStorePath.isNullOrBlank() -> {
                // MediaStore saved to public Downloads — query MediaStore for the URI
                Log.d(TAG_WA, "  → querying MediaStore for relative path: $pdfMediaStorePath")
                queryMediaStoreUri(context, pdfMediaStorePath)
            }
            else -> null
        }

        Log.d(TAG_WA, "  resolved pdfUri  = $pdfUri")

        if (pdfUri == null) {
            Log.e(TAG_WA, "  ❌ pdfUri is null — cannot send")
            Toast.makeText(context, "Fichier PDF introuvable", Toast.LENGTH_LONG).show()
            onResult()
            return
        }

        val formattedPhone = formatPhoneForWhatsApp(phoneNumber)
        Log.d(TAG_WA, "  formattedPhone   = $formattedPhone")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage("com.whatsapp.w4b")
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
            putExtra("jid", "$formattedPhone@s.whatsapp.net")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
        Log.d(TAG_WA, "  ✅ WhatsApp Business intent launched")
        Toast.makeText(
            context,
            "Ouverture WhatsApp Business pour $clientName",
            Toast.LENGTH_SHORT
        ).show()

    } catch (e: Exception) {
        Log.e(TAG_WA, "  ❌ Exception: ${e::class.simpleName}: ${e.message}", e)
        Toast.makeText(
            context,
            "WhatsApp Business non installé ou erreur: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    } finally {
        onResult()
    }
}

// Query MediaStore Downloads for a relative path like "Downloads/BonsWhatsApp/03_27/file.pdf"
private fun queryMediaStoreUri(context: Context, relativePath: String): android.net.Uri? {
    return try {
        val fileName = relativePath.substringAfterLast("/")
        val folder = relativePath
            .removePrefix("Downloads/")
            .substringBeforeLast("/")

        Log.d(TAG_WA, "  [queryMediaStore] fileName=$fileName  folder=$folder")

        val collection = android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(android.provider.MediaStore.Downloads._ID)
        val selection = "${android.provider.MediaStore.Downloads.DISPLAY_NAME} = ? AND ${android.provider.MediaStore.Downloads.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf(fileName, "%$folder%")

        context.contentResolver.query(collection, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(android.provider.MediaStore.Downloads._ID))
                    val uri = android.content.ContentUris.withAppendedId(collection, id)
                    Log.d(TAG_WA, "  [queryMediaStore] ✅ found URI=$uri")
                    uri
                } else {
                    Log.w(TAG_WA, "  [queryMediaStore] ❌ no row found for fileName=$fileName  folder=$folder")
                    null
                }
            }
    } catch (e: Exception) {
        Log.e(TAG_WA, "  [queryMediaStore] exception: ${e.message}", e)
        null
    }
}

// Normalise to Algerian international format (213XXXXXXXXX)
private fun formatPhoneForWhatsApp(raw: String): String {
    var cleaned = raw.replace(Regex("[^0-9]"), "")
    if (!cleaned.startsWith("213")) {
        if (cleaned.startsWith("0")) cleaned = cleaned.drop(1)
        cleaned = "213$cleaned"
    }
    return cleaned
}

// ── Phone-entry dialog ────────────────────────────────────────────────────────
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
