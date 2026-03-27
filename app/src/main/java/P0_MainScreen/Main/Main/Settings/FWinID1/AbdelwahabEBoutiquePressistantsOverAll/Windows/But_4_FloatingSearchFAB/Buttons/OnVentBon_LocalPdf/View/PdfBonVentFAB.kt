package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * FAB that:
 * - Shows green ✓ when the last stored PDF already covers all current active products
 *   (path_pdf_bon_file is set AND nombre_produits_don_dernier_pdf_stoked == activeVents.size)
 * - Shows a PDF icon otherwise, and generates a new PDF on click
 * - After generation succeeds, persists path + product count back into M8BonVent via the repo
 */
@Composable
fun PdfBonVentFAB(
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val context = LocalContext.current
    val focusedValuesGetter: FocusedValuesGetter =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter {
            it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0
        }

    // Default path suffix that means "nothing saved yet"
    val defaultPathSuffix = "/Pdf/"
    val storedPath  = activeBonVent?.path_pdf_bon_file ?: ""
    val storedCount = activeBonVent?.nombre_produits_don_dernier_pdf_stoked ?: 0
    val activeCount = activeVents.size

    // Green "done" state: path is not the empty default AND count matches current basket
    val isPdfUpToDate = storedPath.isNotBlank()
            && !storedPath.endsWith(defaultPathSuffix)
            && storedCount == activeCount
            && activeCount > 0

    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                if (isGenerating || isPdfUpToDate) return@FloatingActionButton
                isGenerating = true
                scope.launch {
                    try {
                        initiateBackgroundPdfCreation_Np(
                            context            = context,
                            aCentralFacade     = aCentralFacade,
                            focusedValuesGetter = focusedValuesGetter,
                            onPdfSaved = { savedPath: String ->        //->
                                //TODO(FIXME):Fix erreur No parameter with name 'onPdfSaved' found.
                                // Persist path + count back into M8BonVent
                                activeBonVent?.let { bon ->
                                    val updated = bon.copy(
                                        path_pdf_bon_file                  = savedPath,
                                        nombre_produits_don_dernier_pdf_stoked = activeCount
                                    )
                                    aCentralFacade.repositorysMainSetter.repo8BonVent.upsert(updated)
                                }
                            }
                        )
                    } finally {
                        isGenerating = false
                    }
                }
            },
            containerColor = when {
                isGenerating   -> MaterialTheme.colorScheme.surfaceVariant
                isPdfUpToDate  -> Color(0xFF4CAF50)   // green  = up-to-date
                else           -> Color(0xFFFF9800)   // orange = needs (re)generation
            },
        ) {
            when {
                isGenerating  -> CircularProgressIndicator(
                    modifier  = Modifier.size(24.dp),
                    color     = Color.White,
                    strokeWidth = 2.dp
                )
                isPdfUpToDate -> Icon(
                    imageVector      = Icons.Default.Check,
                    contentDescription = "PDF à jour",
                    tint             = Color.White
                )
                else          -> Icon(
                    imageVector      = Icons.Default.PictureAsPdf,
                    contentDescription = "Créer PDF",
                    tint             = Color.White
                )
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
