package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

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
    val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos

    // The stored PDF path (set by PdfBonVentFAB after generation)
    val storedPdfPath = activeBonVent?.path_pdf_bon_file ?: ""
    val storedPdfFile = if (storedPdfPath.isNotBlank()) File(storedPdfPath) else null
    val pdfExists = storedPdfFile?.exists() == true && storedPdfFile.length() > 0L

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

                // Guard: need an active bon and a generated PDF
                if (activeBonVent == null) {
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

                val phone = activeClient?.numTelephone?.trim() ?: ""
                if (phone.isEmpty()) {
                    // No phone → show outlined dialog + keyboard
                    showPhoneDialog = true
                } else {
                    // Phone exists → send directly
                    isSending = true
                    scope.launch {
                        sendPdfViaWhatsAppBusiness(
                            context = context,
                            phoneNumber = phone,
                            pdfFile = storedPdfFile,
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
    pdfFile: File?,
    clientName: String,
    packageName: String,
    onResult: () -> Unit,
) {
    try {
        if (pdfFile == null || !pdfFile.exists()) {
            Toast.makeText(context, "Fichier PDF introuvable", Toast.LENGTH_LONG).show()
            onResult()
            return
        }

        val formattedPhone = formatPhoneForWhatsApp(phoneNumber)
        val pdfUri = FileProvider.getUriForFile(context, "$packageName.fileprovider", pdfFile)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage("com.whatsapp.w4b")                    // WhatsApp Business
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
            putExtra("jid", "$formattedPhone@s.whatsapp.net")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
        Toast.makeText(
            context,
            "Ouverture WhatsApp Business pour $clientName",
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
