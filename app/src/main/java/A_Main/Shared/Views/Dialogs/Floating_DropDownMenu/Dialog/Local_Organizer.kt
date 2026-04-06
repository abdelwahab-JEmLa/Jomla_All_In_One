package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Local_Organizer {

    /**
     * Mirrors the DropBox organizer but works entirely on-device:
     * - Target root : [M3CouleurProduitInfos.Companion.backup_Images_storageLink]
     * - Per-catalogue sub-folder : catalogue.drp_image_folder_catalogue_path
     * - Source images : [M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink]
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

                val fullName   = "${color.nomImageFichieSansEtansion}.${color.extensionDisponible}"
                val sourceFile = File(
                    M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
                    fullName
                )
                val targetFile = File(targetDir, fullName)

                if (sourceFile.exists()) {
                    try {
                        sourceFile.copyTo(targetFile, overwrite = true)
                        sourceFile.delete()
                    } catch (_: Exception) {
                        targetFile.delete()
                    }
                }

                done++
                onProgress(done / total)
            }
        }

        onProgress(1f)
    }

    /**
     * Sets the `lastModified` timestamp of every local image file referenced by [list_m3]
     * to the current time ([System.currentTimeMillis]).
     *
     * Use this to "reset" the local modification dates so that a subsequent
     * [DropBox_Init_3.syncFromImages2] with a [sinceMs] cutoff treats all local files as older
     * than any incoming DropBox version, forcing a full re-download on the next sync.
     *
     * @param list_m3    Colours whose local image files should be touched.
     * @param onProgress `fraction 0..1` called after each file.
     */
    suspend fun updateLocalTimestampsToNow(
        list_m3:    List<M3CouleurProduitInfos>?,
        onProgress: (Float) -> Unit = {},
    ) = withContext(Dispatchers.IO) {
        onProgress(0f)

        val validColors = list_m3?.filter { it.hasValidImage() }
        if (validColors.isNullOrEmpty()) {
            onProgress(1f); return@withContext
        }

        val nowMs = System.currentTimeMillis()
        val total = validColors.size.toFloat()
        var done  = 0

        validColors.forEach { color ->
            val fullName = "${color.nomImageFichieSansEtansion}.${color.extensionDisponible}"
            val file     = File(
                M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
                fullName
            )
            if (file.exists()) file.setLastModified(nowMs)

            done++
            onProgress(done / total)
        }

        onProgress(1f)
    }

    private fun M3CouleurProduitInfos.hasValidImage() =
        nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"
}
