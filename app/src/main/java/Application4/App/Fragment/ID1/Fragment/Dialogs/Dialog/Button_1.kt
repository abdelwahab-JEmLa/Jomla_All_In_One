package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.SyncProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// ─── Button 1 : Organiser par Catalogue (DropBox) ────────────────────────────

@Composable
fun DropDownItemWBaseDonne_OrganiserParCatalogue(
    progress: Float?,
    enabled:  Boolean,
    onClick:  () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = when {
                        progress == null -> "Organiser images par Catalogue (DropBox)"
                        progress < 1f    -> "Déplacement… ${(progress * 100).toInt()} %"
                        else             -> "Terminé ✓"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            enabled = enabled,
            onClick  = onClick
        )
        if (progress != null) SyncProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

// ─── Button 2 : Organiser par Catalogue (Local) ──────────────────────────────

@Composable
fun DropDownItemWBaseDonne_OrganiserLocaleParCatalogue(
    progress: Float?,
    enabled:  Boolean,
    onClick:  () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = when {
                        progress == null -> "Organiser images par Catalogue (Local)"
                        progress < 1f    -> "Déplacement local… ${(progress * 100).toInt()} %"
                        else             -> "Terminé ✓"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            enabled = enabled,
            onClick  = onClick
        )
        if (progress != null) SyncProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

// ─── Local_Organizer ─────────────────────────────────────────────────────────

object Local_Organizer {

    /**
     * Mirrors the DropBox organizer but works entirely on-device:
     * - Target root : [M3CouleurProduitInfos.backup_Images_storageLink]
     * - Per-catalogue sub-folder : catalogue.drp_image_folder_catalogue_path
     * - Source images : [M00CentralParametresOfAllApps.images_central_Local_storageLink]
     *
     * Each image is *moved* (copy + delete) from the central folder into its
     * catalogue sub-folder inside the backup root.
     */
    suspend fun organizeByCategories(
        catalogueGroups: Map<M21CataloguesCategorie, List<M3CouleurProduitInfos>>?,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        onProgress(0f)

        val allColors = catalogueGroups?.values
            ?.flatten()
            ?.filter { it.hasValidImage() }

        if (allColors.isNullOrEmpty()) { onProgress(1f); return@withContext }

        val total = allColors.size.toFloat()
        var done  = 0

        catalogueGroups?.forEach { (catalogue, colors) ->

            // backup_Images_storageLink / drp_image_folder_catalogue_path
            val targetDir = File(
                M3CouleurProduitInfos.backup_Images_storageLink,
                catalogue.drp_image_folder_catalogue_path
            )
            targetDir.mkdirs()

            colors.forEach { color ->
                if (!color.hasValidImage()) {
                    done++
                    onProgress(done / total)
                    return@forEach
                }

                val fullName   = "${color.nomImageFichieSansEtansion}.${color.extensionDisponible}"
                val sourceFile = File(M00CentralParametresOfAllApps.images_central_Local_storageLink, fullName)
                val targetFile = File(targetDir, fullName)

                if (sourceFile.exists()) {
                    try {
                        sourceFile.copyTo(targetFile, overwrite = true)
                        sourceFile.delete()           // move = copy + delete
                    } catch (_: Exception) {
                        targetFile.delete()           // roll back partial copy on failure
                    }
                }

                done++
                onProgress(done / total)
            }
        }

        onProgress(1f)
    }

    private fun M3CouleurProduitInfos.hasValidImage() =
        nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"
}
