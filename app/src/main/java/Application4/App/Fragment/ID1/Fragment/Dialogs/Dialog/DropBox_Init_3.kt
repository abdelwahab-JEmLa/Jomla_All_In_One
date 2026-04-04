package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.RelocationErrorException
import com.dropbox.core.v2.files.WriteMode
import com.example.clientjetpack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Result of a [DropBox_Init_3.syncFromImages2] operation.
 *
 * @param added       File names that did not exist locally and were downloaded.
 * @param overwritten File names that already existed locally and were replaced by a newer DropBox version.
 */
data class SyncReport(
    val added: List<String>,
    val overwritten: List<String>,
) {
    val isEmpty: Boolean get() = added.isEmpty() && overwritten.isEmpty()
}

object DropBox_Init_3 {
    val rootFolder: String = M3CouleurProduitInfos.Companion.rootFolder_DropBox

    private val client: DbxClientV2 by lazy {
        DbxClientV2(
            DbxRequestConfig.newBuilder("jeMla-app/1.0").build(),
            DbxCredential(
                "", -1L,
                BuildConfig.DROPBOX_REFRESH_TOKEN,
                BuildConfig.DROPBOX_APP_KEY,
                BuildConfig.DROPBOX_APP_SECRET
            )
        )
    }

    suspend fun organizeByCategories(
        catalogueGroups: Map<M21CataloguesCategorie, List<M3CouleurProduitInfos>>?,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        onProgress(0f)
        val allColors = catalogueGroups?.values?.flatten()?.filter { it.hasValidImage() }
        if (allColors.isNullOrEmpty()) {
            onProgress(1f); return@withContext
        }

        val index = buildIndex()
        if (index.isEmpty()) {
            onProgress(1f); return@withContext
        }

        val total = allColors.size.toFloat()
        var done = 0

        catalogueGroups?.forEach { (catalogue, colors) ->
            val folderPath = catalogue.drp_image_folder_catalogue_path
            ensureDropboxFolder(folderPath)

            colors.forEach { color ->
                if (!color.hasValidImage()) {
                    done++; onProgress(done / total); return@forEach
                }

                val filename = color.nomImageFichieSansEtansion
                val fullName = "$filename.${color.extensionDisponible}"
                val meta = index[filename]
                val localFile = File(
                    M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
                    fullName
                )

                if (meta != null) {
                    val dropboxPath = meta.pathLower
                    val dropboxModMs = meta.serverModified?.time ?: 0L

                    if (dropboxPath != null && (!localFile.exists() || dropboxModMs > localFile.lastModified())) {
                        try {
                            localFile.parentFile?.mkdirs()
                            FileOutputStream(localFile).use {
                                client.files().download(dropboxPath).download(it)
                            }
                            if (dropboxModMs > 0L) localFile.setLastModified(dropboxModMs)
                        } catch (_: Exception) {
                            localFile.delete()
                        }
                    }

                    val targetPath = "$folderPath/$fullName"
                    if (dropboxPath != null && !dropboxPath.equals(targetPath, ignoreCase = true))
                        moveDropboxFile(fromPath = dropboxPath, toPath = targetPath)
                }

                done++
                onProgress(done / total)
            }
        }
        onProgress(1f)
    }

    /**
     * For each colour in [list_m3] that has a valid image name, compares the DropBox
     * [M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox] server-modified timestamp against the local file's
     * last-modified timestamp.  If DropBox is newer, downloads and overwrites the local file.
     *
     * @param sinceMs Only consider DropBox files whose [FileMetadata.serverModified] is ≥ this
     *                epoch-millisecond value.  Pass `0L` (default) to skip the date filter.
     * @return [SyncReport] listing which files were newly added and which were overwritten,
     *         so the caller can present a summary dialog to the user.
     */
    suspend fun syncFromImages2(
        list_m3:    List<M3CouleurProduitInfos>?,
        sinceMs:    Long = 0L,
        onProgress: (Float) -> Unit = {},
    ): SyncReport = withContext(Dispatchers.IO) {
        onProgress(0f)
        val validColors = list_m3?.filter { it.hasValidImage() }
        if (validColors.isNullOrEmpty()) {
            onProgress(1f)
            return@withContext SyncReport(emptyList(), emptyList())
        }

        val index = buildImages2Index()
        val total = validColors.size.toFloat()
        var done = 0

        val addedFiles       = mutableListOf<String>()
        val overwrittenFiles = mutableListOf<String>()

        validColors.forEach { color ->
            val fullName  = "${color.nomImageFichieSansEtansion}.${color.extensionDisponible}"
            val localFile = File(
                M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
                fullName
            )
            val meta = index[color.nomImageFichieSansEtansion]

            if (meta != null) {
                val dropBoxModMs = meta.serverModified?.time ?: 0L
                val localModMs   = if (localFile.exists()) localFile.lastModified() else 0L

                // Skip files older than the requested date cutoff
                if (sinceMs > 0L && dropBoxModMs < sinceMs) {
                    done++
                    onProgress(done / total)
                    return@forEach
                }

                if (dropBoxModMs > localModMs) {
                    val wasNew = !localFile.exists()
                    try {
                        localFile.parentFile?.mkdirs()
                        FileOutputStream(localFile).use { out ->
                            client.files().download(meta.pathLower).download(out)
                        }
                        if (dropBoxModMs > 0L) localFile.setLastModified(dropBoxModMs)

                        // Record the outcome for the summary dialog
                        if (wasNew) addedFiles.add(fullName)
                        else        overwrittenFiles.add(fullName)

                    } catch (_: Exception) {
                        localFile.delete()
                    }
                }
            }

            done++
            onProgress(done / total)
        }

        onProgress(1f)
        SyncReport(added = addedFiles, overwritten = overwrittenFiles)
    }

    private suspend fun buildImages2Index(): Map<String, FileMetadata> =
        withContext(Dispatchers.IO) {
            val index = mutableMapOf<String, FileMetadata>()
            try {
                var result = client.files()
                    .listFolderBuilder(M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox)
                    .withRecursive(true).start()
                while (true) {
                    result.entries.filterIsInstance<FileMetadata>()
                        .forEach { index[it.name.substringBeforeLast(".")] = it }
                    if (!result.hasMore) break
                    result = client.files().listFolderContinue(result.cursor)
                }
            } catch (_: Exception) {
            }
            index
        }

    private suspend fun buildIndex(): MutableMap<String, FileMetadata> =
        withContext(Dispatchers.IO) {
            val index = mutableMapOf<String, FileMetadata>()
            try {
                var result =
                    client.files().listFolderBuilder(rootFolder).withRecursive(true).start()
                while (true) {
                    result.entries.filterIsInstance<FileMetadata>()
                        .forEach { index[it.name.substringBeforeLast(".")] = it }
                    if (!result.hasMore) break
                    result = client.files().listFolderContinue(result.cursor)
                }
            } catch (_: Exception) {
            }
            index
        }

    private suspend fun ensureDropboxFolder(path: String) = withContext(Dispatchers.IO) {
        try {
            client.files().createFolderV2(path)
        } catch (_: Exception) {
        }
    }

    private suspend fun moveDropboxFile(fromPath: String, toPath: String) =
        withContext(Dispatchers.IO) {
            try {
                client.files().moveV2(fromPath, toPath)
            } catch (_: RelocationErrorException) {
            } catch (_: Exception) {
            }
        }

    /**
     * Uploads [imageBytes] to [M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox]/[fileName], overwriting any
     * existing file with the same name.  Called after a successful camera capture.
     * Returns the DropBox path on success, or null if the upload failed.
     */
    suspend fun uploadToImages2(
        fileName: String,
        imageBytes: ByteArray,
    ): String? = withContext(Dispatchers.IO) {
        val targetPath = "${M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox}/$fileName"
        return@withContext try {
            client.files()
                .uploadBuilder(targetPath)
                .withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(imageBytes.inputStream())
            targetPath
        } catch (_: Exception) {
            null
        }
    }

    private fun M3CouleurProduitInfos.hasValidImage() =
        nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"
}
