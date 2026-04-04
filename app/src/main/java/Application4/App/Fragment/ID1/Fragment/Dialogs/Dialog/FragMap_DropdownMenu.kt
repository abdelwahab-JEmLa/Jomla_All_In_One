package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_OrganiserLocaleParCatalogue
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_OrganiserParCatalogue
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_SyncDepuisImages2
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.DropDownItemWBaseDonne_UpdateLocalTimestamps
import Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.Ui.AvertissementDialog
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class PendingAction { DropBox, Local, SyncFromImages2, UpdateLocalTimestamps }

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
    val context = LocalContext.current

    var organizeDropBoxProgress    by remember { mutableStateOf<Float?>(null) }
    var organizeLocalProgress      by remember { mutableStateOf<Float?>(null) }
    var syncImages2Progress        by remember { mutableStateOf<Float?>(null) }
    var syncImages2Label           by remember { mutableStateOf("") }
    var updateTimestampsProgress   by remember { mutableStateOf<Float?>(null) }

    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }
    var syncReport    by remember { mutableStateOf<SyncReport?>(null) }

    // ── Sync result summary dialog ────────────────────────────────────────────

    syncReport?.let { report ->
        SyncReportDialog(report = report, onDismiss = { syncReport = null })
    }

    // ── Confirmation dialogs ──────────────────────────────────────────────────

    pendingAction?.let { action ->
        when (action) {

            PendingAction.DropBox -> AvertissementDialog(
                title        = "Organiser sur DropBox",
                message      = "Cette action va déplacer toutes les images vers leurs dossiers " +
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
                            onProgress      = { p -> organizeDropBoxProgress = p }
                        )
                        organizeDropBoxProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )

            PendingAction.Local -> AvertissementDialog(
                title        = "Organiser en local",
                message      = "Cette action va déplacer toutes les images depuis le dossier " +
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
                            onProgress      = { p -> organizeLocalProgress = p }
                        )
                        organizeLocalProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )

            PendingAction.SyncFromImages2 -> AvertissementDialog(      //<--
            //TODO(1): ajot des de colors au burrons et icons animable pour distencte les clicks on utili lottif . 
                title        = "Sync local ← DropBox Images_2",
                message      = "Seules les images des catalogues t1 et t4 modifiées sur DropBox " +
                        "dans les 20 derniers jours seront téléchargées. " +
                        "Les fichiers locaux plus anciens seront écrasés. Continuer ?",
                confirmLabel = "Synchroniser",
                onConfirm = {      //<--
                    //TODO(1): exctract pour l utilise au init vec le progress edites
                    pendingAction = null
                    coroutineScope.launch {
                        syncImages2Progress = 0f
                        syncImages2Label    = ""

                        // ── Filters ───────────────────────────────────────────
                        val cutoffMs   = System.currentTimeMillis() - 20L * 24 * 3_600 * 1_000
                        val filteredM3 = filterM3ByCatalogueKeys(
                            catalogueKeys = setOf("t1", "t4"),
                            list_m16      = list_m16,
                            list_m1       = list_m1,
                            list_m3       = list_m3,
                        )

                        val TAG = "DropBox_Sync"
                        Log.d(TAG, "=== SYNC DÉMARRÉ ===")
                        Log.d(TAG, "cutoffMs = $cutoffMs " +
                                "(${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(cutoffMs))})")
                        Log.d(TAG, "list_m3 total=${list_m3?.size} | après filtre catalogue(t1,t4)=${filteredM3?.size}")
                        if (filteredM3.isNullOrEmpty()) {
                            Log.w(TAG, "⚠️ filteredM3 VIDE — l'image cherchée n'est peut-être pas dans catalogue t1 ou t4")
                        }
                        val produitKeyToName = list_m1
                            ?.associate { it.keyID to it.nom }
                            ?: emptyMap()
                        // ─────────────────────────────────────────────────────

                        val report = DropBox_Init_3.syncFromImages2(
                            list_m3          = filteredM3,
                            sinceMs          = cutoffMs,
                            produitKeyToName = produitKeyToName,
                            onProgress       = { p, label ->
                                syncImages2Progress = p
                                syncImages2Label    = label
                            }
                        )
                        syncImages2Progress = null
                        syncImages2Label    = ""

                        // Invalidate Glide cache so every displayer in the app
                        // picks up the newly downloaded files immediately.
                        withContext(Dispatchers.Main) { Glide.get(context).clearMemory() }
                        launch(Dispatchers.IO)        { Glide.get(context).clearDiskCache() }

                        onDismiss()
                        syncReport = report
                    }
                },
                onDismiss = { pendingAction = null }
            )

            PendingAction.UpdateLocalTimestamps -> AvertissementDialog(
                title        = "Mettre à jour dates locales",
                message      = "La date de modification de chaque fichier image local sera " +
                        "remplacée par l'heure actuelle. Cela forcera un re-téléchargement " +
                        "lors de la prochaine synchronisation. Continuer ?",
                confirmLabel = "Mettre à jour",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        updateTimestampsProgress = 0f
                        Local_Organizer.updateLocalTimestampsToNow(
                            list_m3    = list_m3,
                            onProgress = { p -> updateTimestampsProgress = p }
                        )
                        updateTimestampsProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )
        }
    }

    // ── Dropdown menu ─────────────────────────────────────────────────────────

    val anyRunning = organizeDropBoxProgress  != null
            || organizeLocalProgress    != null
            || syncImages2Progress      != null
            || updateTimestampsProgress != null

    DropdownMenu(
        expanded         = expanded,
        onDismissRequest = onDismiss,
        modifier         = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        DropDownItemWBaseDonne_OrganiserParCatalogue(
            progress = organizeDropBoxProgress,
            enabled  = organizeDropBoxProgress == null && pendingAction == null && !anyRunning,
            onClick  = { pendingAction = PendingAction.DropBox }
        )

        DropDownItemWBaseDonne_OrganiserLocaleParCatalogue(
            progress = organizeLocalProgress,
            enabled  = organizeLocalProgress == null && pendingAction == null && !anyRunning,
            onClick  = { pendingAction = PendingAction.Local }
        )

        DropDownItemWBaseDonne_SyncDepuisImages2(
            progress     = syncImages2Progress,
            currentLabel = syncImages2Label,
            enabled      = syncImages2Progress == null && pendingAction == null && !anyRunning,
            onClick      = { pendingAction = PendingAction.SyncFromImages2 }
        )

        DropDownItemWBaseDonne_UpdateLocalTimestamps(
            progress = updateTimestampsProgress,
            enabled  = updateTimestampsProgress == null && pendingAction == null && !anyRunning,
            onClick  = { pendingAction = PendingAction.UpdateLocalTimestamps }
        )
    }
}

// ─── Sync report dialog ───────────────────────────────────────────────────────

@Composable
private fun SyncReportDialog(report: SyncReport, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector        = Icons.Default.CheckCircle,
                contentDescription = null,
                tint               = Color(0xFF4CAF50)
            )
        },
        title = {
            Text(
                text  = "Synchronisation terminée",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                if (report.isEmpty) {
                    Text(
                        text  = "Aucun fichier modifié — tout est déjà à jour.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    if (report.added.isNotEmpty()) {
                        Text(
                            text  = "✅ Ajoutés (${report.added.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        report.added.forEach { name ->
                            Text(
                                text  = "  • $name",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    if (report.added.isNotEmpty() && report.overwritten.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (report.overwritten.isNotEmpty()) {
                        Text(
                            text  = "🔄 Écrasés (${report.overwritten.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        report.overwritten.forEach { name ->
                            Text(
                                text  = "  • $name",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "OK", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

// ─── Shared helpers ───────────────────────────────────────────────────────────

/**
 * Returns only the [M3CouleurProduitInfos] entries whose parent catalogue
 * has a [M21CataloguesCategorie.keyID] in [catalogueKeys].
 * Lookup chain: colour → produit → catégorie → catalogue.
 */
private suspend fun filterM3ByCatalogueKeys(
    catalogueKeys: Set<String>,
    list_m16:      List<M16CategorieProduit>?,
    list_m1:       List<M01Produit>?,
    list_m3:       List<M3CouleurProduitInfos>?,
): List<M3CouleurProduitInfos>? = withContext(Dispatchers.Default) {
    if (list_m3.isNullOrEmpty()) return@withContext list_m3

    val catalogues             = get_ListM21CataloguesCategorie()
    val catalogueById          = catalogues.associateBy { it.id }
    val catalogueByCategorieId = list_m16?.associate { cat ->
        cat.id to catalogueById[cat.catalogueParentId]
    }
    val catalogueByProduitKey  = list_m1?.associate { p ->
        p.keyID to catalogueByCategorieId?.get(p.idParentCategorie)
    }

    list_m3.filter { color ->
        catalogueByProduitKey?.get(color.parentBProduitInfosKeyID)?.keyID in catalogueKeys
    }
}

private suspend fun buildCatalogueGroups(
    list_m16: List<M16CategorieProduit>?,
    list_m1:  List<M01Produit>?,
    list_m3:  List<M3CouleurProduitInfos>?,
): Map<M21CataloguesCategorie, List<M3CouleurProduitInfos>>? =
    withContext(Dispatchers.Default) {
        val catalogues    = get_ListM21CataloguesCategorie()
        val sansCatalogue = catalogues.find { it.nom == "Sans Catalogue" }
            ?: M21CataloguesCategorie(keyID = "t4", id = 4, nom = "Sans Catalogue")

        val catalogueById          = catalogues.associateBy { it.id }
        val catalogueByCategorieId = list_m16?.associate { cat ->
            cat.id to (catalogueById[cat.catalogueParentId] ?: sansCatalogue)
        }
        val catalogueByProduitKey  = list_m1?.associate { p ->
            p.keyID to (catalogueByCategorieId?.get(p.idParentCategorie) ?: sansCatalogue)
        }

        list_m3
            ?.filter { it.nomImageFichieSansEtansion.isNotBlank() && it.nomImageFichieSansEtansion != "Non Dispo" }
            ?.groupBy { catalogueByProduitKey?.get(it.parentBProduitInfosKeyID) ?: sansCatalogue }
    }
