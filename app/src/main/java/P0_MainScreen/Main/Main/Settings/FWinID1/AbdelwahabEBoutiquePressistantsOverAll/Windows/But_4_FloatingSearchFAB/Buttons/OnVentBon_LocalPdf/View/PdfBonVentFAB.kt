package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Base.AppDatabase
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PdfBonVentFAB(
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onPdfSaved: ((savedPath: String, count: Int) -> Unit)? = null,
    aCentralFacade: ACentralFacade = koinInject(),
    appDatabase: AppDatabase = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    context: Context = LocalContext.current,
    listm13: List<M13TarificationInfos>
) {
    val context = LocalContext.current
    val focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeVents   = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }

    // Compute the live total value of the current bon so we can detect price-change staleness.
    val activeTotal = remember(activeVents) {
        activeVents.sumOf { vent ->
            val tariff = listm13.find { it.keyID == vent.parentM13TarificationKeyID }
            (tariff?.prixCurrency ?: 0.0) * vent.quantity
        }
    }

    val defaultPathSuffix = "/Pdf/"
    val activeCount = activeVents.size

    var localSavedPath  by remember(activeBonVent?.keyID) { mutableStateOf(activeBonVent?.path_pdf_bon_file ?: "") }
    var localSavedCount by remember(activeBonVent?.keyID) { mutableStateOf(activeBonVent?.nombre_produits_don_dernier_pdf_stoked ?: 0) }
    // Tracks the total value at the time the last PDF was generated so price changes invalidate it.
    var localSavedTotal by remember(activeBonVent?.keyID) { mutableStateOf(activeBonVent?.last_sort_pdf_locale_totale_a_paye ?: 0.0) }

    val storedPath  = localSavedPath.takeIf  { it.isNotBlank() && !it.endsWith(defaultPathSuffix) } ?: (activeBonVent?.path_pdf_bon_file ?: "")
    val storedCount = localSavedCount.takeIf { it > 0 }    ?: (activeBonVent?.nombre_produits_don_dernier_pdf_stoked ?: 0)
    val storedTotal = localSavedTotal.takeIf { it > 0.0 }  ?: (activeBonVent?.last_sort_pdf_locale_totale_a_paye ?: 0.0)

    val fileExistsOnDisk = if (storedPath.startsWith("/")) {
        val f = java.io.File(storedPath)
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
            && (storedTotal - activeTotal).let { kotlin.math.abs(it) < 0.01 }

    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Row(
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier
                .size(40.dp)
                .semantics(mergeDescendants = true) {},
            onClick = {
                if (isGenerating) return@FloatingActionButton
                isGenerating = true
                scope.launch {
                    try {
                        initiateBackgroundPdfCreation_NewP(
                            list_M13TarificationInfos = listm13,
                            context                   = context,
                            aCentralFacade            = aCentralFacade,
                            focusedValuesGetter       = focusedValuesGetter,
                            onPdfSaved                = { savedPath ->
                                localSavedPath  = savedPath
                                localSavedCount = activeCount
                                localSavedTotal = activeTotal
                                onPdfSaved?.invoke(savedPath, activeCount)
                                activeBonVent?.let { bon ->
                                    aCentralFacade.repositorysMainSetter.repo8BonVent.upsert(
                                        bon.copy(
                                            path_pdf_bon_file                      = savedPath,
                                            nombre_produits_don_dernier_pdf_stoked = activeCount,
                                            last_sort_pdf_locale_totale_a_paye     = activeTotal
                                        )
                                    )
                                }
                            }
                        )
                    } finally {
                        isGenerating = false
                    }
                }
            },
            containerColor = when {
                isGenerating  -> MaterialTheme.colorScheme.surfaceVariant
                isPdfUpToDate -> Color(0xFF4CAF50)
                else          -> Color(0xFFFF9800)
            },
        ) {
            when {
                isGenerating  -> CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                isPdfUpToDate -> Icon(imageVector = Icons.Default.Check,        contentDescription = "PDF à jour",  tint = Color.White)
                else          -> Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Créer PDF",   tint = Color.White)
            }
        }

        if (showLabels) {
            Text(
                text = when {
                    isGenerating  -> "Génération…"
                    isPdfUpToDate -> "PDF ✓ ($storedCount art.)"
                    else          -> "Créer PDF ($activeCount art.)"
                },
                modifier = Modifier
                    .background(
                        color = when {
                            isGenerating  -> MaterialTheme.colorScheme.surfaceVariant
                            isPdfUpToDate -> Color(0xFF4CAF50)
                            else          -> Color(0xFFFF9800)
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
