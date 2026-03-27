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
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

/**
 * Compact status chip displayed between PdfBonVentFAB and the WhatsApp button.
 * Shows:
 *  • ✓ green  — PDF exists and article count matches active cart
 *  • ⧗ orange — PDF missing or stale (count mismatch)
 *
 * Uses the same produceState polling as Button_Click_Send_Stored_Bon_Par_whatsappBuisness
 * so it reacts within 500 ms of the DB update without any manual wiring.
 */
@Composable
fun BonVentPdfStatusChip(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val focusedValuesGetter: FocusedValuesGetter =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val activeVents   = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve && it.quantity > 0 }

    val activeCount       = activeVents.size
    val defaultPathSuffix = "/Pdf/"

    // Poll the getter every 500 ms — same strategy as the WA button
    val livePath by produceState(
        initialValue = activeBonVent?.path_pdf_bon_file ?: "",
        key1 = activeBonVent?.keyID
    ) {
        while (true) {
            val current = focusedValuesGetter.activeOnVent_M8BonVent?.path_pdf_bon_file ?: ""
            if (current != value) value = current
            kotlinx.coroutines.delay(500L)
        }
    }

    val liveCount by produceState(
        initialValue = activeBonVent?.nombre_produits_don_dernier_pdf_stoked ?: 0,
        key1 = activeBonVent?.keyID
    ) {
        while (true) {
            val current = focusedValuesGetter.activeOnVent_M8BonVent
                ?.nombre_produits_don_dernier_pdf_stoked ?: 0
            if (current != value) value = current
            kotlinx.coroutines.delay(500L)
        }
    }

    val isRealPath = livePath.isNotBlank() && !livePath.endsWith(defaultPathSuffix)
    val fileExistsOnDisk = if (isRealPath && livePath.startsWith("/")) {
        val f = java.io.File(livePath)
        f.exists() && f.length() > 0L
    } else {
        isRealPath   // MediaStore relative path — trust it if non-blank & non-default
    }

    val isPdfUpToDate = isRealPath
            && fileExistsOnDisk
            && liveCount == activeCount
            && activeCount > 0

    val bgColor  = if (isPdfUpToDate) Color(0xFF4CAF50) else Color(0xFFFF9800)
    val label    = if (isPdfUpToDate) "PDF ✓  $liveCount/$activeCount" else "PDF ?  $liveCount/$activeCount"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (isPdfUpToDate) Icons.Default.Check else Icons.Default.HourglassEmpty,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
