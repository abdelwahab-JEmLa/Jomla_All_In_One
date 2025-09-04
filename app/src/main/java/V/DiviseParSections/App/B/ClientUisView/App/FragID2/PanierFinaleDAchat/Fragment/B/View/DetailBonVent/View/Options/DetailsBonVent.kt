package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.CartSummarySection
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.ClientDetailsSection
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Details.UI.B.UI.GBonVentInfosHeader
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.ErrorCard
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.PeriodDetailsSection
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsbPrintHelper {

    fun printHelloWorld(context: Context) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Hello World Test - ${System.currentTimeMillis()}"

        val printAdapter = object : PrintDocumentAdapter() {

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder("hello_world.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback.onLayoutFinished(info, true)
            }

            // CORRIGÉ: Signature correcte pour onWrite
            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    return
                }

                try {
                    // Créer un PDF simple avec "Hello World"
                    val pdfDocument = createHelloWorldPdf()

                    // Écrire le PDF dans le fichier de destination
                    FileOutputStream(destination.fileDescriptor).use { output ->
                        pdfDocument.writeTo(output)
                    }

                    pdfDocument.close()
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

                } catch (e: Exception) {
                    callback.onWriteFailed("Erreur: ${e.message}")
                }
            }
        }

        // Lancer l'impression
        printManager.print(jobName, printAdapter, null)
    }

    private fun createHelloWorldPdf(): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint().apply {
            textSize = 48f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        // Dessiner "Hello World" au centre de la page
        val centerX = pageInfo.pageWidth / 2f
        val centerY = pageInfo.pageHeight / 2f

        canvas.drawText("Hello World!", centerX, centerY - 100, paint)

        // Ajouter la date et l'heure
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT
        val dateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        canvas.drawText("Imprimé le: $dateTime", centerX, centerY + 50, paint)

        // Ajouter des informations sur l'imprimante
        paint.textSize = 18f
        canvas.drawText("Test d'impression USB", centerX, centerY + 100, paint)
        canvas.drawText("Samsung ML via Android Print Framework", centerX, centerY + 130, paint)

        pdfDocument.finishPage(page)
        return pdfDocument
    }
}


@Preview
@Composable
fun DetailsBonVentPrev() {
    DetailsBonVent()
}

val petitePaddine = 4.dp //rename

data class ActionButtonData(
    val key: String,
    val content: @Composable () -> Unit
)

@Composable
fun DetailsBonVent(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    viewModel: ZViewModel_Sec1Frag3 = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isMinimized = uiState.isMinimized
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val zAppComptRepositoryComposable = viewModel.uiStateCentralRepositorys.repo9AppCompt
    val comptAppActuelle = zAppComptRepositoryComposable.currentAppCompt
    val ouvertPeriodKeyId = comptAppActuelle?.current_OnVent_M14VentPeriode_KeyID ?: ""

    // Create list of action buttons for LazyColumn
// In your DetailsBonVent.kt file, update the actionButtons list:

// Create list of action buttons for LazyColumn
    val actionButtons = remember(uiState, isMinimized) {
        listOf(
            ActionButtonData("filter") {
                FilterButton(
                    uiState = uiState,
                    showLabel = !isMinimized,
                    onToggleFilter = { viewModel.toggelePanierFilterNonTrouve() }
                )
            },
            ActionButtonData("print") {
                PrintButton(
                    showLabel = !isMinimized,
                    onPrint = {
                        printHandler.printVentReceiptWithDirectPdf(
                            context = context,
                            repo13TarificationInfos = viewModel.aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                            repoM1Produit = viewModel.uiStateCentralRepositorys.repo1ProduitInfos,
                            repo3CouleurProduitInfos = viewModel.uiStateCentralRepositorys.repo03CouleurProduitInfos,
                            client = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos,
                            scope = scope,
                            relative_ListM10OperationVentCouleur = it,
                            bonVent = focusedValuesGetter.activeOnVent_M8BonVent
                        )
                    }
                )
            },
            ActionButtonData("reports") {
                PrintReportsButton(
                    showLabel = !isMinimized
                )
            },
            ActionButtonData("confirmation") {
                ConfirmationButton(
                    viewModel = viewModel,
                    showLabel = !isMinimized,
                )
            },
            ActionButtonData("minimize") {
                MinimizeButton(
                    isMinimized = isMinimized,
                    showLabel = !isMinimized,
                    onToggleMinimized = { viewModel.toggleMinimizedState() }
                )
            }
        )
    }

    if (comptAppActuelle != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = if (isMinimized) 180.dp else 400.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(petitePaddine),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(petitePaddine),
                    verticalArrangement = Arrangement.spacedBy(if (isMinimized) 6.dp else 12.dp)
                ) {
                    if (!isMinimized) {
                        GBonVentInfosHeader(viewModel)
                    }
                    if (!isMinimized) {
                        PeriodDetailsSection(
                            viewModel = viewModel,
                            ouvertPeriodKeyId = ouvertPeriodKeyId,
                        )
                    }

                    ClientDetailsSection(
                        modifier = Modifier,
                        viewModel = viewModel,
                    )

                    if (!isMinimized) {
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }

                    CartSummarySection(viewModel)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(petitePaddine)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = actionButtons,
                    key = { it.key }
                ) { buttonData ->
                    buttonData.content()
                }
            }
        }
    } else {
        ErrorCard(modifier = modifier)
    }
}

@Composable
fun FilterButton(
    uiState: ZViewModel_Sec1Frag3.UiState_Sec1Frag3,
    showLabel: Boolean,
    onToggleFilter: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onToggleFilter,
            containerColor = if (uiState.filterNonTrouve) {
                Color(0xFFFF5722) // Orange when filter is active
            } else {
                MaterialTheme.colorScheme.tertiary
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = if (uiState.filterNonTrouve)
                    "Désactiver filtre Non trouvé"
                else
                    "Activer filtre Non trouvé",
                modifier = Modifier.size(20.dp),
                tint = if (uiState.filterNonTrouve) Color.White else MaterialTheme.colorScheme.onTertiary
            )
        }

        if (showLabel) {
            Text(
                text = if (uiState.filterNonTrouve) "Filtre actif" else "Filtre inactif",
                modifier = Modifier
                    .background(
                        color = if (uiState.filterNonTrouve) {
                            Color(0xFFFF5722)
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (uiState.filterNonTrouve) Color.White else MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PrintButton(
    aCentralFacade: ACentralFacade = koinInject(),
    showLabel: Boolean,
    onPrint: (List<M10OperationVentCouleur>) -> Unit
) {
    val activeVents = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            modifier = Modifier
                .getSemanticsTag(activeVents.map {
                    it.getDebugInfos()
                }, "activeVents")
                .size(48.dp),
            onClick = { onPrint(activeVents) },
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "Imprimer le reçu",
                modifier = Modifier.size(20.dp)
            )
        }

        if (showLabel) {
            Text(
                text = "Imprimer",
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MinimizeButton(
    isMinimized: Boolean,
    showLabel: Boolean,
    onToggleMinimized: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onToggleMinimized,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (isMinimized) "Afficher détails" else "Masquer détails",
                modifier = Modifier.size(20.dp)
            )
        }

        if (showLabel) {
            Text(
                text = if (isMinimized) "Afficher" else "Masquer",
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
