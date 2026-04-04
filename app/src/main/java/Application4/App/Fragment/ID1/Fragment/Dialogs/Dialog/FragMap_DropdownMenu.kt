package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_OrganiserLocaleParCatalogue
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_OrganiserParCatalogue
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_SyncDepuisImages2
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.Ui.AvertissementDialog
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.Ui.SyncReportDialog
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

enum class PendingAction { DropBox, Local, SyncFromImages2 }

@Composable
fun FragMap_DropdownMenu(
    expanded:  Boolean,
    onDismiss: () -> Unit,
    list_m16:  List<M16CategorieProduit>?,
    list_m1:   List<M01Produit>?,
    list_m3:   List<M3CouleurProduitInfos>?,
    modifier:  Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    var organizeDropBoxProgress by remember { mutableStateOf<Float?>(null) }
    var organizeLocalProgress   by remember { mutableStateOf<Float?>(null) }
    var syncImages2Progress     by remember { mutableStateOf<Float?>(null) }

    // Which action is waiting for the user's confirmation (null = no dialog open)
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    // Result of the last syncFromImages2 run — shown in a summary dialog
    var syncReport by remember { mutableStateOf<SyncReport?>(null) }

    // ── Sync result summary dialog ────────────────────────────────────────────

    syncReport?.let { report ->
        SyncReportDialog(
            report = report,
            onDismiss = { syncReport = null }
        )
    }

    // ── Confirmation dialogs ──────────────────────────────────────────────────

    pendingAction?.let { action ->
        when (action) {
            PendingAction.DropBox -> AvertissementDialog(
                title = "Organiser sur DropBox",
                message = "Cette action va déplacer toutes les images vers leurs dossiers " +
                        "catalogues sur DropBox. Les fichiers seront déplacés de façon " +
                        "permanente. Continuer ?",
                confirmLabel = "Déplacer",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        organizeDropBoxProgress = 0f
                        val groups = buildCatalogueGroups(list_m16, list_m1, list_m3)
                        DropBox_Init_3.organizeByCategories(
                            catalogueGroups = groups,
                            onProgress = { p -> organizeDropBoxProgress = p }
                        )
                        organizeDropBoxProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )

            PendingAction.Local -> AvertissementDialog(
                title = "Organiser en local",
                message = "Cette action va déplacer toutes les images depuis le dossier " +
                        "central local vers leurs dossiers catalogues dans le dossier " +
                        "de sauvegarde. Les fichiers sources seront supprimés. Continuer ?",
                confirmLabel = "Déplacer",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        organizeLocalProgress = 0f
                        val groups = buildCatalogueGroups(list_m16, list_m1, list_m3)
                        Local_Organizer.organizeByCategories(
                            catalogueGroups = groups,
                            onProgress = { p -> organizeLocalProgress = p }
                        )
                        organizeLocalProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )

            PendingAction.SyncFromImages2 -> AvertissementDialog(
                title = "Sync local ← DropBox Images_2",
                message = "Seules les images des catalogues t1/t2 modifiées sur DropBox " +
                        "dans les 20 derniers jours seront téléchargées. " +
                        "Les fichiers locaux plus anciens seront écrasés. Continuer ?",
                confirmLabel = "Synchroniser",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        syncImages2Progress = 0f

                        // ── Filters ──────────────────────────────────────────
                        val cutoffMs = System.currentTimeMillis() - 20L * 24 * 3600 * 1000
                        val filteredM3 = filterM3ByCatalogueKeys(
                            catalogueKeys = setOf("t1", "t4"),
                            list_m16 = list_m16,
                            list_m1 = list_m1,
                            list_m3 = list_m3,
                        )
                        // ─────────────────────────────────────────────────────

                        val report = DropBox_Init_3.syncFromImages2(
                            list_m3 = filteredM3,
                            sinceMs = cutoffMs,
                            onProgress = { p -> syncImages2Progress = p }
                        )
                        syncImages2Progress = null
                        onDismiss()
                        syncReport = report
                    }
                },
                onDismiss = { pendingAction = null }
            )
        }
    }

    DropdownMenu(
        expanded         = expanded,
        onDismissRequest = onDismiss,
        modifier         = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        DropDownItemWBaseDonne_OrganiserParCatalogue(
            progress = organizeDropBoxProgress,
            enabled  = organizeDropBoxProgress == null && pendingAction == null,
            onClick  = { pendingAction = PendingAction.DropBox }
        )

        DropDownItemWBaseDonne_OrganiserLocaleParCatalogue(
            progress = organizeLocalProgress,
            enabled  = organizeLocalProgress == null && pendingAction == null,
            onClick  = { pendingAction = PendingAction.Local }
        )

        DropDownItemWBaseDonne_SyncDepuisImages2(
            progress = syncImages2Progress,
            enabled  = syncImages2Progress == null && pendingAction == null,
            onClick  = { pendingAction = PendingAction.SyncFromImages2 }
        )
    }
}



