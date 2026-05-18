package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler_Mai
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import kotlin.math.abs
data class Datas(     //<--
//TODO(1): ou il ya aCentralFacade o ces chilied 
//          comme 
// et utili datas passed focusedValuesGetter ou repo1 ou RepositorysMainGetter
    val  relative_produits: List<M01Produit>?,
    val  relative_couleurs: List<M3CouleurProduitInfos>,
    val relative_list_tariff: List<M13TarificationInfos>,
    val  on_vent_couleurs: List<M10OperationVentCouleur>,
    val on_vent_bon: M8BonVent?,
)
@Composable
fun But7_Cree_Images_Bons(
    showLabels: Boolean = true,
    modifier: Modifier = Modifier,
    onPdfSaved: ((savedPath: String, count: Int) -> Unit)? = null,
    aCentralFacade: ACentralFacade = koinInject(),          //<--
    //TODO(1): enlev ca
    appDatabase: AppDatabase = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),      //<--
    //TODO(2.C Relative Au Todo(1):
            //... et ca
    context: Context = LocalContext.current,
    relative_list_tariff: List<M13TarificationInfos>,
    on_vent_bon: M8BonVent?,
    on_vent_couleurs: List<M10OperationVentCouleur>,
    relative_produits: List<M01Produit>?,
    relative_couleurs: List<M3CouleurProduitInfos>?
) {
    
    val datas = Datas(      //<--
    //TODO(1): regle 
    )
    val printInPdf_itextpdf_Handler = remember {
        PrintInPdf_itextpdf_Handler_Mai(
            datas=datas,
            uploadHandler = TODO(),
            focusedValuesGetter = TODO(),
        )
    }
    val context = LocalContext.current
    val focusedValuesGetter: FocusedValuesGetter =    //<--
    //TODO(1): enleve et utilise  datas
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    val activeVents = on_vent_couleurs
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }

    // Compute the live total value of the current bon so we can detect price-change staleness.
    val activeTotal = remember(activeVents) {
        activeVents.sumOf { vent ->
            val tariff = relative_list_tariff.find { it.keyID == vent.parentM13TarificationKeyID }
            (tariff?.prixCurrency ?: 0.0) * vent.quantity
        }
    }

    val defaultPathSuffix = "/Pdf/"
    val activeCount = activeVents.size

    var localSavedPath by remember(on_vent_bon?.keyID) {
        mutableStateOf(
            on_vent_bon?.path_pdf_bon_file ?: ""
        )
    }
    var localSavedCount by remember(on_vent_bon?.keyID) {
        mutableStateOf(
            on_vent_bon?.nombre_produits_don_dernier_pdf_stoked ?: 0
        )
    }
    // Tracks the total value at the time the last PDF was generated so price changes invalidate it.
    var localSavedTotal by remember(on_vent_bon?.keyID) {
        mutableStateOf(
            on_vent_bon?.last_sort_pdf_locale_totale_a_paye ?: 0.0
        )
    }

    val storedPath = localSavedPath.takeIf { it.isNotBlank() && !it.endsWith(defaultPathSuffix) }
        ?: (on_vent_bon?.path_pdf_bon_file ?: "")
    val storedCount =
        localSavedCount.takeIf { it > 0 } ?: (on_vent_bon?.nombre_produits_don_dernier_pdf_stoked
            ?: 0)
    val storedTotal =
        localSavedTotal.takeIf { it > 0.0 } ?: (on_vent_bon?.last_sort_pdf_locale_totale_a_paye
            ?: 0.0)

    val fileExistsOnDisk = if (storedPath.startsWith("/")) {
        val f = File(storedPath)
        f.exists() && f.length() > 0L
    } else {
        storedPath.isNotBlank() && !storedPath.endsWith(defaultPathSuffix)
    }

    // isPdfUpToDate: file exists, count matches, AND total value matches (catches price-change staleness).
    val isPdfUpToDate = storedPath.isNotBlank()
            && !storedPath.endsWith(defaultPathSuffix)
            && fileExistsOnDisk
            && storedCount == activeCount
            && activeCount > 0
            && (storedTotal - activeTotal).let { abs(it) < 0.01 }

    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val relativeListM13vent = on_vent_couleurs
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier
                .size(40.dp)
                .semantics(mergeDescendants = true) {
                    set(
                        value = relativeListM13vent
                            .filter { it.parent_M1Produit_DebugInfos.contains("Lino")
                        },
                        key = SemanticsPropertyKey("filter")
                    )
                    set(
                        value = relativeListM13vent.map {
                            (it.parent_M1Produit_DebugInfos to  it.keyID.takeLast(3))
                        },
                        key = SemanticsPropertyKey("relativeListM13vent")
                    )
                },
            onClick = {
                if (isGenerating) return@FloatingActionButton
                isGenerating = true
                scope.launch {
                    try {
                        initiateBackgroundPdfCreation_ProMai(
                            printInPdf_itextpdf_Handler=printInPdf_itextpdf_Handler,
                            context = context,
                            aCentralFacade = aCentralFacade,
                            onPdfSaved = { savedPath ->
                                localSavedPath = savedPath
                                localSavedCount = activeCount
                                localSavedTotal = activeTotal
                                onPdfSaved?.invoke(savedPath, activeCount)
                                on_vent_bon?.let { bon ->
                                    aCentralFacade.repositorysMainSetter.repo8BonVent.upsert(
                                        bon.copy(
                                            path_pdf_bon_file = savedPath,
                                            nombre_produits_don_dernier_pdf_stoked = activeCount,
                                            last_sort_pdf_locale_totale_a_paye = activeTotal
                                        )
                                    )
                                }
                            },
                            list_M13TarificationInfos = relative_list_tariff,
                            relative_List_M13Vent = relativeListM13vent,
                            on_vent_client = focusedValuesGetter.activeOnVentM2ClientInfos,
                            on_vent_bon = focusedValuesGetter.activeOnVent_M8BonVent
                        )
                    } finally {
                        isGenerating = false
                    }
                }
            },
            containerColor = when {
                isGenerating -> MaterialTheme.colorScheme.surfaceVariant
                isPdfUpToDate -> Color(0xFF4CAF50)
                else -> Color(0xFFFF9800)
            },
        ) {
            when {
                isGenerating -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                isPdfUpToDate -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "PDF à jour",
                    tint = Color.White
                )

                else -> Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "Créer PDF",
                    tint = Color.White
                )
            }
        }

        if (showLabels) {
            Text(
                text = when {
                    isGenerating -> "Génération…"
                    isPdfUpToDate -> "PDF ✓ ($storedCount art.)"
                    else -> "Créer PDF ($activeCount art.)"
                },
                modifier = Modifier
                    .background(
                        color = when {
                            isGenerating -> MaterialTheme.colorScheme.surfaceVariant
                            isPdfUpToDate -> Color(0xFF4CAF50)
                            else -> Color(0xFFFF9800)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (isGenerating) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
