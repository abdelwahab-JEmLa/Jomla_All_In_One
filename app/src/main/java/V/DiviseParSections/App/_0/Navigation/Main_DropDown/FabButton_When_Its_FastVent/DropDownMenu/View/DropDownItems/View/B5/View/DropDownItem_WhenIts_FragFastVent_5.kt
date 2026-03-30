package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.UploadHandler
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M10OperationVentCouleur
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B5.View.AndroidNativeTaxInvoiceGenerator
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B5.View.PdfFormatterUtils_2
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
fun DropDownItem_WindowsShare_Facture_Impots(
    nomFun: String = "Partager Facture Impôts",
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

    // Function to open PDF file
    fun openPdfFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    "Aucune application pour ouvrir le PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Erreur lors de l'ouverture du PDF: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    fun shareWithWindows() {
        if (activeVents.isEmpty()) {
            Toast.makeText(
                context,
                "Aucun article à partager",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    focusedValuesGetter.activeOnVent_M8BonVent?.copy(
                        affiche_le_verssement_au_prochen_print = false
                    )
                )

                delay(500)

                val formatter = PdfFormatterUtils_2()
                val uploadHandler = UploadHandler()
                val taxInvoiceGenerator = AndroidNativeTaxInvoiceGenerator(
                    formatter = formatter,
                    uploadHandler = uploadHandler
                )

                // Generate the tax invoice PDF with all required data
                val result = taxInvoiceGenerator.generateTaxInvoicePdf(
                    context = context,
                    client = focusedValuesGetter.activeOnVentM2ClientInfos,
                    operations = focusedValuesGetter
                        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                        .filter { vent ->
                            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                                    vent.quantity > 0
                        },
                    tarificationRepo = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    produitRepo = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    bonVent = focusedValuesGetter.activeOnVent_M8BonVent,
                    companyLogoResId = null // Add your company logo: R.drawable.company_logo
                )

                result.onSuccess { message ->
                    val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                    val pdfFile = File(filePath)

                    if (pdfFile.exists()) {
                        // Share with Windows apps
                        printHandler.sharePdfWithWindowsApps(context, pdfFile)

                        // Open the PDF file automatically
                        delay(300) // Small delay to ensure sharing dialog appears first
                        openPdfFile(pdfFile)

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Facture impôts partagée avec succès",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                result.onFailure { error ->
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "Erreur: ${error.message}",
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
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    text = if (isLoading) "Génération en cours..." else nomFun,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    shareWithWindows()
                }
            },
            enabled = !isLoading
        )
    }
}
