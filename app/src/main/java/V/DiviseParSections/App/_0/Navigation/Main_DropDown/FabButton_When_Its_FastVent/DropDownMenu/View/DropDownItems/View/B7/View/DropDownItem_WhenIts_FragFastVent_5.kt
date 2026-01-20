package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B7.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    fun shareViaWhatsApp() {
        val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos

        if (activeClient == null) {
            Toast.makeText(
                context,
                "Aucun client actif trouvé",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (activeVents.isEmpty()) {
            Toast.makeText(
                context,
                "Aucun article à partager",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validate phone number
        val phoneNumber = activeClient.numTelephone.trim()
        if (phoneNumber.isEmpty()) {
            Toast.makeText(
                context,
                "Le client n'a pas de numéro de téléphone",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                // Update bon vent to hide versement in print
                aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    focusedValuesGetter.activeOnVent_M8BonVent?.copy(
                        affiche_le_verssement_au_prochen_print = false
                    )
                )

                delay(500)

                // Generate PDF
                val result = printHandler.printPdfOnly(
                    context = context,
                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                    scope = scope,
                    relative_ListM10OperationVentCouleur = activeVents,
                    relative_bonVent = focusedValuesGetter.activeOnVent_M8BonVent,
                    client = activeClient
                )

                result.onSuccess { message ->
                    val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                    val pdfFile = File(filePath)

                    if (pdfFile.exists()) {
                        // Format phone number for WhatsApp (remove spaces, add country code if needed)
                        val formattedPhone = formatPhoneNumberForWhatsApp(phoneNumber)

                        // Create content URI for the PDF file
                        val pdfUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            pdfFile
                        )

                        // Create WhatsApp share intent
                        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            setPackage("com.whatsapp")
                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                            putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
                            putExtra("jid", "$formattedPhone@s.whatsapp.net")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        try {
                            context.startActivity(whatsappIntent)

                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Ouverture de WhatsApp pour ${activeClient.nom}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            // Fallback to general share if WhatsApp is not installed
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
                            Toast.makeText(
                                context,
                                "Fichier PDF introuvable",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                result.onFailure { error ->
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
                isLoading = false
                onDismissDropdown()
            }
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
                    shareViaWhatsApp()
                }
            },
            enabled = !isLoading
        )
    }
}

/**
 * Formats phone number for WhatsApp
 * Removes spaces and special characters, adds country code if needed
 */
private fun formatPhoneNumberForWhatsApp(phoneNumber: String): String {
    // Remove all non-digit characters
    var cleaned = phoneNumber.replace(Regex("[^0-9]"), "")

    // Add Algeria country code (+213) if not present
    if (!cleaned.startsWith("213")) {
        // Remove leading zero if present
        if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1)
        }
        cleaned = "213$cleaned"
    }

    return cleaned
}
