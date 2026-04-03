package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class PendingAction { DropBox, Local }

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

    // Which action is waiting for the user's confirmation (null = no dialog open)
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    // ── Confirmation dialogs ──────────────────────────────────────────────────

    pendingAction?.let { action ->
        when (action) {
            PendingAction.DropBox -> AvertissementDialog(
                title   = "Organiser sur DropBox",
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
                            onProgress      = { p -> organizeDropBoxProgress = p }
                        )
                        organizeDropBoxProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )

            PendingAction.Local -> AvertissementDialog(
                title   = "Organiser en local",
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
                            onProgress      = { p -> organizeLocalProgress = p }
                        )
                        organizeLocalProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )
        }
    }

    // ── Dropdown items ────────────────────────────────────────────────────────

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
    }
}

// ─── Shared helper ────────────────────────────────────────────────────────────

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
