package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B7.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@Composable
fun DropDownItem_WhenIts_FragFastVent_7(
    nomFun: String = "Partager via WhatsApp",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var showPhoneInputDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    if (showPhoneInputDialog) {
        PhoneNumberInputDialog(
            clientName = focusedValuesGetter.activeOnVentM2ClientInfos?.nom ?: "Client",
            onDismiss = {
                showPhoneInputDialog = false
                onDismissDropdown()
            },
            onPhoneEntered = { phoneNumber ->
                showPhoneInputDialog = false

                // Update client with new phone number
                focusedValuesGetter.activeOnVentM2ClientInfos?.let { client ->
                    val updatedClient = client.copy(numTelephone = phoneNumber)
                    aCentralFacade.repositorysMainSetter.upsert_M2Client(updatedClient)

                    // Wait a bit for update then proceed with sharing
                    scope.launch {
                        delay(300)
                        shareViaWhatsApp(
                            context = context,
                            scope = scope,
                            phoneNumber = phoneNumber,
                            aCentralFacade = aCentralFacade,
                            focusedValuesGetter = focusedValuesGetter,
                            printHandler = printHandler,
                            activeVents = activeVents,
                            onLoadingChange = { isLoading = it },
                            onDismiss = onDismissDropdown
                        )
                    }
                }
            }
        )
    }

    fun initiateShare() {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null) {
            Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show()
            return
        }

        if (activeBonVent == null) {
            Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
            return
        }

        if (activeVents.isEmpty()) {
            Toast.makeText(context, "Aucun article à partager", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if client has phone number, if not show input dialog
        val phoneNumber = activeClient.numTelephone.trim()
        if (phoneNumber.isEmpty()) {
            showPhoneInputDialog = true
            return
        }

        // Proceed with sharing
        isLoading = true
        scope.launch {
            shareViaWhatsApp(
                context = context,
                scope = scope,
                phoneNumber = phoneNumber,
                aCentralFacade = aCentralFacade,
                focusedValuesGetter = focusedValuesGetter,
                printHandler = printHandler,
                activeVents = activeVents,
                onLoadingChange = { isLoading = it },
                onDismiss = onDismissDropdown
            )
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) "Partage en cours..." else nomFun,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    initiateShare()
                }
            },
            enabled = !isLoading
        )
    }
}

@Composable
private fun PhoneNumberInputDialog(
    clientName: String,
    onDismiss: () -> Unit,
    onPhoneEntered: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Numéro de téléphone requis",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Client: $clientName",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Ce client n'a pas de numéro de téléphone. Veuillez entrer son numéro pour partager via WhatsApp.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() || it in "+-() " }) {
                            phoneNumber = newValue
                            showError = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = { Text("Numéro de téléphone") },
                    placeholder = { Text("0XXX XX XX XX") },
                    prefix = { Text("+213 ") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val cleanedNumber = phoneNumber.trim()
                            if (cleanedNumber.isNotEmpty()) {
                                onPhoneEntered(cleanedNumber)
                            } else {
                                showError = true
                            }
                        }
                    ),
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Veuillez entrer un numéro valide") }
                    } else null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Annuler")
                    }

                    Button(
                        onClick = {
                            val cleanedNumber = phoneNumber.trim()
                            if (cleanedNumber.isNotEmpty()) {
                                onPhoneEntered(cleanedNumber)
                            } else {
                                showError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Confirmer")
                    }
                }
            }
        }
    }
}

private suspend fun shareViaWhatsApp(
    context: Context,
    scope: CoroutineScope,
    phoneNumber: String,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    printHandler: Any,
    activeVents: List<M10OperationVentCouleur>,
    onLoadingChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    try {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        if (activeClient == null || activeBonVent == null) {
            onLoadingChange(false)
            return
        }

        val currentValues = focusedValuesGetter.active_Central_Values
        val currentBonsWithImages = currentValues.bons_a_imprime_avec_image_produit.toMutableList()

        if (!currentBonsWithImages.any { it.keyID == activeBonVent.keyID }) {
            currentBonsWithImages.add(activeBonVent)

            focusedValuesGetter.update_activeCentralValues(
                currentValues.copy(
                    bons_a_imprime_avec_image_produit = currentBonsWithImages
                )
            )
        }

        aCentralFacade.repositorysMainSetter.update_M8BonVent(
            activeBonVent.copy(
                affiche_le_verssement_au_prochen_print = false
            )
        )

        delay(500)

        val result = (printHandler as? PrintReceiptHandler_Juil)
            ?.printPdfOnly(
                context = context,
                repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                scope = scope,
                relative_ListM10OperationVentCouleur = activeVents,
                relative_bonVent = activeBonVent,
                client = activeClient
            )

        result?.onSuccess { message ->
            val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
            val pdfFile = File(filePath)

            if (pdfFile.exists()) {
                // FIXED: Format phone number properly for WhatsApp
                val formattedPhone = formatPhoneNumberForWhatsApp(phoneNumber)
                val pdfUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    pdfFile
                )

                // FIXED: Launch WhatsApp with specific phone number using URI
                val whatsappUri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone")

                val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = whatsappUri
                    setPackage("com.whatsapp")
                }

                try {
                    // First, open WhatsApp chat with the contact
                    context.startActivity(whatsappIntent)

                    // Then after a short delay, trigger the share intent
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            setPackage("com.whatsapp")
                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                            putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(shareIntent)

                        Toast.makeText(
                            context,
                            "Partage du PDF à ${activeClient.nom}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    // Fallback to general share if WhatsApp fails
                    val generalShareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, pdfUri)
                        putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(
                        Intent.createChooser(generalShareIntent, "Partager le PDF via")
                    )

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "WhatsApp non installé. Veuillez choisir une autre application",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Fichier PDF introuvable", Toast.LENGTH_LONG).show()
                }
            }
        }

        result?.onFailure { error ->
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Erreur lors de la génération du PDF: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    } catch (e: Exception) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                "Erreur lors du partage: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
        e.printStackTrace()
    } finally {
        delay(2000)

        val finalValues = focusedValuesGetter.active_Central_Values
        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

        activeBonVent?.let { bon ->
            val cleanedBons = finalValues.bons_a_imprime_avec_image_produit
                .filter { it.keyID != bon.keyID }

            focusedValuesGetter.update_activeCentralValues(
                finalValues.copy(
                    bons_a_imprime_avec_image_produit = cleanedBons
                )
            )
        }

        onLoadingChange(false)
        onDismiss()
    }
}

/**
 * FIXED: Formats phone number for WhatsApp
 * - Removes all non-digit characters (spaces, +, -, etc.)
 * - Handles numbers starting with 0 by removing the leading 0
 * - Handles numbers that already have country code (213)
 * - Handles numbers with extra 0 after country code (+213 0 XXX) by removing it
 *
 * Examples:
 * - "0553885037" -> "213553885037"
 * - "+213 0 553 88 50 37" -> "213553885037"
 * - "+213 553 88 50 37" -> "213553885037"
 * - "213553885037" -> "213553885037"
 */
private fun formatPhoneNumberForWhatsApp(phoneNumber: String): String {
    // Remove all non-digit characters
    var cleaned = phoneNumber.replace(Regex("[^0-9]"), "")

    // If it already starts with 213 (country code)
    if (cleaned.startsWith("213")) {
        // Remove the country code temporarily to check for extra 0
        val withoutCountryCode = cleaned.substring(3)

        // If there's a leading 0 after the country code, remove it
        val finalNumber = if (withoutCountryCode.startsWith("0")) {
            withoutCountryCode.substring(1)
        } else {
            withoutCountryCode
        }

        // Re-add country code
        cleaned = "213$finalNumber"
    } else {
        // Number doesn't have country code
        // If it starts with 0, remove it
        if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1)
        }
        // Add country code
        cleaned = "213$cleaned"
    }

    return cleaned
}
