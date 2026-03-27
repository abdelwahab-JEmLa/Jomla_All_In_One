package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.util.Log
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

private const val TAG_FAB = "PdfBonVentFAB"

@Composable
fun PdfBonVentFAB(
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    onPdfSaved: ((savedPath: String, count: Int) -> Unit)? = null,
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

    val defaultPathSuffix = "/Pdf/"
    val activeCount = activeVents.size

    // ── Local reactive state ────────────────────────────────────────────────
    // focusedValuesGetter.activeOnVent_M8BonVent is a plain property, not a
    // StateFlow, so after upsert() the Composable won't recompose automatically.
    // We keep a local snapshot updated immediately inside onPdfSaved so the UI
    // turns green without waiting for the DB Flow to propagate.
    var localSavedPath  by remember(activeBonVent?.keyID) { mutableStateOf(activeBonVent?.path_pdf_bon_file ?: "") }
    var localSavedCount by remember(activeBonVent?.keyID) { mutableStateOf(activeBonVent?.nombre_produits_don_dernier_pdf_stoked ?: 0) }

    // Prefer local state when it already holds a real path, otherwise use DB value
    val storedPath  = localSavedPath.takeIf { it.isNotBlank() && !it.endsWith(defaultPathSuffix) }
        ?: (activeBonVent?.path_pdf_bon_file ?: "")
    val storedCount = localSavedCount.takeIf { it > 0 }
        ?: (activeBonVent?.nombre_produits_don_dernier_pdf_stoked ?: 0)

    // The path stored can be either:
    //  • an absolute File path  → check File.exists()
    //  • a MediaStore relative  → e.g. "Downloads/BonsWhatsApp/..." (no leading '/'), treat as valid if non-blank & non-default
    val isAbsolutePath = storedPath.startsWith("/")
    val fileExistsOnDisk = if (isAbsolutePath) {
        val f = java.io.File(storedPath)
        val exists = f.exists() && f.length() > 0L
        Log.d(TAG_FAB, "  [isPdfUpToDate] absolute path check → exists=$exists  size=${if (f.exists()) f.length() else -1}  path=$storedPath")
        exists
    } else {
        // MediaStore relative path — we trust the saved value is valid
        val valid = storedPath.isNotBlank() && !storedPath.endsWith(defaultPathSuffix)
        Log.d(TAG_FAB, "  [isPdfUpToDate] mediaStore relative path → valid=$valid  path=$storedPath")
        valid
    }

    val isPdfUpToDate = storedPath.isNotBlank()
            && !storedPath.endsWith(defaultPathSuffix)
            && fileExistsOnDisk
            && storedCount == activeCount
            && activeCount > 0

    Log.d(TAG_FAB, "── PdfBonVentFAB recompose ──")
    Log.d(TAG_FAB, "  activeBonVent  = ${activeBonVent?.keyID ?: "NULL"}")
    Log.d(TAG_FAB, "  storedPath     = $storedPath  (local=$localSavedPath)")
    Log.d(TAG_FAB, "  storedCount    = $storedCount  activeCount=$activeCount  (local=$localSavedCount)")
    Log.d(TAG_FAB, "  isAbsolutePath = $isAbsolutePath  fileExistsOnDisk=$fileExistsOnDisk")
    Log.d(TAG_FAB, "  isPdfUpToDate  = $isPdfUpToDate")

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
                        initiateBackgroundPdfCreation_NewP(
                            context = context,
                            aCentralFacade = aCentralFacade,
                            focusedValuesGetter = focusedValuesGetter,
                            onPdfSaved = { savedPath: String ->
                                // ① Update local State immediately → forces recomposition now
                                localSavedPath  = savedPath
                                localSavedCount = activeCount
                                // ② Notify parent so sibling composables (e.g. WA button) update instantly
                                onPdfSaved?.invoke(savedPath, activeCount)
                                // ③ Persist to DB (Flow will eventually sync, local state is the bridge)
                                activeBonVent?.let { bon ->
                                    val updated = bon.copy(
                                        path_pdf_bon_file = savedPath,
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
                isGenerating -> MaterialTheme.colorScheme.surfaceVariant
                isPdfUpToDate -> Color(0xFF4CAF50)   // green  = up-to-date & file exists
                else -> Color(0xFFFF9800)             // orange = needs (re)generation
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
