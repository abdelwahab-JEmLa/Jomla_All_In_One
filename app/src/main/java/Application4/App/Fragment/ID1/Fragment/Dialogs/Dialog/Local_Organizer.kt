package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Local_Organizer {

    /**
     * Mirrors the DropBox organizer but works entirely on-device:
     * - Target root : [EntreApps.Shared.Models.M3CouleurProduitInfos.Companion.backup_Images_storageLink]
     * - Per-catalogue sub-folder : catalogue.drp_image_folder_catalogue_path
     * - Source images : [EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink]
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

        if (allColors.isNullOrEmpty()) {
            onProgress(1f); return@withContext
        }

        val total = allColors.size.toFloat()
        var done = 0

        catalogueGroups?.forEach { (catalogue, colors) ->

            // backup_Images_storageLink / drp_image_folder_catalogue_path
            val targetDir = File(
                M3CouleurProduitInfos.Companion.backup_Images_storageLink,
                catalogue.drp_image_folder_catalogue_path
            )
            targetDir.mkdirs()

            colors.forEach { color ->
                if (!color.hasValidImage()) {
                    done++
                    onProgress(done / total)
                    return@forEach
                }

                val fullName = "${color.nomImageFichieSansEtansion}.${color.extensionDisponible}"
                val sourceFile = File(
                    M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
                    fullName
                )
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
