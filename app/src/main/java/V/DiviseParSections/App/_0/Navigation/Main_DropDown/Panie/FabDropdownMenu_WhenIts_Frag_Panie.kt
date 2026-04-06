package V.DiviseParSections.App._0.Navigation.Main_DropDown.Panie

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App._0.Navigation.Buttons_Gps.PdfSaverUtility
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B10.EnvoyerPDFviaWhatsAppBusiness.View.DropDownItem_WhenIts_FragFastVent_10
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B6.View.DropDownItem_ThermiquePrint
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B7.View.DropDownItem_WhenIts_FragFastVent_7
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B8.DropDownItem_WhenIts_FragFastVent_8
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B9.View.DropDownItem_WhenIts_FragFastVent_9
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B9.View.initiateBackgroundPdfCreation
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent_3
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent_4
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WindowsShare
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WindowsShare_Facture_Impots
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WindowsShare_WithCredit
import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4.Fab_CleanupM8AndM10
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.Fab_Stigns
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

// ─── Fallback number used when the active client has no phone on file ──────────
private const val FALLBACK_WHATSAPP_NUMBER = "213553885037" // replace with abdelwahab oustad num

/**
 * Launches a WhatsApp Business share intent for [pdfUri].
 *
 * Resolution order:
 *  1. Client phone number (cleaned to digits only, prefixed with country code 213).
 *  2. [FALLBACK_WHATSAPP_NUMBER] when the client has no phone.
 *
 * If WhatsApp Business is not installed the system chooser is shown instead.
 */
private fun shareViaWhatsAppBusiness(
    context: Context,
    phoneNumber: String?,
    pdfUri: Uri,
    clientNom: String,
) {
    // Sanitise number: keep digits only, replace leading 0 with Algeria country code
    val rawPhone = phoneNumber?.filter { it.isDigit() }?.ifBlank { null }
    val intlPhone = when {
        rawPhone == null               -> FALLBACK_WHATSAPP_NUMBER
        rawPhone.startsWith("213")     -> rawPhone
        rawPhone.startsWith("0")       -> "213${rawPhone.drop(1)}"
        else                           -> "213$rawPhone"
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, pdfUri)
        putExtra("jid", "$intlPhone@s.whatsapp.net") // WhatsApp Business deep-link
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Try WhatsApp Business first, fall back to system chooser
    val waBizPackage = "com.whatsapp.w4b"
    val pm = context.packageManager
    val resolved = pm.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY)
    val target = resolved.firstOrNull { it.activityInfo.packageName == waBizPackage }

    if (target != null) {
        sendIntent.setPackage(waBizPackage)
        context.startActivity(sendIntent)
    } else {
        Toast.makeText(
            context,
            "WhatsApp Business non installé – ouverture du sélecteur",
            Toast.LENGTH_SHORT
        ).show()
        context.startActivity(Intent.createChooser(sendIntent, "Partager PDF – $clientNom"))
    }
}

@Composable
fun FabDropdownMenu_WhenIts_Frag_Panie(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    onClick_to_initiateBackgroundPdfCreation: () -> Unit,
    onClickImageToShowControles: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val context = LocalContext.current

    Box(
        modifier = modifier
            .offset(y = (-90).dp)
    ) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {

            Fab_Stigns(onClickImageToShowControles, onDismissDropdown)

            DropDownItem_ThermiquePrint(
                nomFun = "ThermiquePrint ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WindowsShare_WithCredit(
                nomFun = "Partager PDF Avec Versement Credit ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WindowsShare(
                nomFun = "Partager PDF",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenIts_FragFastVent_7(
                nomFun = "Whatsapp test share PDF ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenIts_FragFastVent_9(
                nomFun = "Partager via WhatsApp buisness ",
                onDismissDropdown = onDismissDropdown
            )

            val nom =
                aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client?.nom
            DropDownItem_WhenIts_FragFastVent_8(
                nomFun = "Generation PDF De $nom en arrière-plan",
                onDismissDropdown = onDismissDropdown,
                onClick_to_initiateBackgroundPdfCreation = {
                    GlobalScope.launch(Dispatchers.IO) {
                        initiateBackgroundPdfCreation(context, aCentralFacade, focusedValuesGetter)
                    }
                    onClick_to_initiateBackgroundPdfCreation()
                }
            )

            DropDownItem_WhenIts_FragFastVent_10(
                nomFun = "Envoyer Generaed PDF $nom via WhatsApp Business",
                onDismissDropdown = {
                    onDismissDropdown()
                    GlobalScope.launch(Dispatchers.IO) {
                        val activeClient  = focusedValuesGetter.activeOnVentM2ClientInfos
                        val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

                        if (activeClient == null || activeBonVent == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Client ou bon de vente introuvable",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }

                        // Generate the PDF first, then share it
                        initiateBackgroundPdfCreation(context, aCentralFacade, focusedValuesGetter)

                        // Retrieve the saved PDF URI for this bon-vent
                        val pdfUri = PdfSaverUtility.getLatestPdfUri(context, "BonsWhatsApp")

                        if (pdfUri == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "❌ PDF introuvable pour le partage",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main) {
                            shareViaWhatsAppBusiness(
                                context     = context,
                                phoneNumber = activeClient.numTelephone,
                                pdfUri      = pdfUri,
                                clientNom   = activeClient.nom,
                            )
                        }
                    }
                }
            )

            Fab_CleanupM8AndM10(
                repositorysMainGetter = aCentralFacade.repositorysMainGetter,
                onDismissDropdown = onDismissDropdown,
                on_vent_key = focusedValuesGetter.currentActive_M9AppCompt?.onVentM8BonVentKey ?: ""
            )

            DropDownItem_WindowsShare_Facture_Impots(
                nomFun = "Partager PDF Facture_Impots ",
                onDismissDropdown = onDismissDropdown
            )

            DropDownItem_WhenIts_FragFastVent_4(
                nomFun = "DropDownItem_WhenIts_FragFastVent_4",
                onDismissDropdown = onDismissDropdown
            )

            // Button: show M8 / M10 data sizes and trigger cleanup on click

            DropDownItem_WhenIts_FragFastVent_3(
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenIts_FragFastVent_2(
                nomFun = "givre le neveau classement ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenIts_FragFastVent(
                nomFun = "affiche_CheckList_ChoisiseurActiveFilter",
                onDismissDropdown = onDismissDropdown
            )

        }
    }
}
