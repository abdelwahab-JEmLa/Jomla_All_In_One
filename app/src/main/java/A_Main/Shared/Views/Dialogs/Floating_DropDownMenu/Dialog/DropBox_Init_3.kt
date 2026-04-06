package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.DropBox_Init_3.buildImages2Index
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.util.Log
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
                    val dropboxPath  = meta.pathLower
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
     * Syncs local images from DropBox Images_2, applying two optimisations before any download:
     *
     * 1. **Date filter in DropBox listing**: [buildImages2Index] only keeps entries whose
     *    `serverModified ≥ sinceMs`, so the index is small from the start — no full-folder scan
     *    followed by per-item date checks.
     *
     * 2. **Intersection**: only colours whose image name appears in that small index are processed.
     *    Colours not on DropBox or already up-to-date are never iterated.
     *
     * @param list_m3          Colours to consider (already filtered by catalogue before calling).
     * @param sinceMs          Epoch-ms cutoff; only DropBox files modified on or after this date
     *                         enter the index.  Pass `0L` to disable the date filter.
     * @param produitKeyToName Map `M01Produit.keyID → produit.nom` used to label progress updates.
     * @param onProgress       `(fraction 0..1, label)` called before and after each download.
     * @return [SyncReport] listing added / overwritten file names.
     */
    suspend fun syncFromImages2(
        list_m3:          List<M3CouleurProduitInfos>?,
        sinceMs:          Long = 0L,
        produitKeyToName: Map<String, String> = emptyMap(),
        onProgress:       (fraction: Float, label: String) -> Unit = { _, _ -> },
    ): SyncReport = withContext(Dispatchers.IO) {

        val TAG = "DropBox_Sync"
        onProgress(0f, "")

        // ── 1. Build a small DropBox index filtered by date ───────────────────
        val index = buildImages2Index(sinceMs)
        Log.d(TAG, "Index DropBox: ${index.size} fichiers (sinceMs=$sinceMs)")

        // ── 2. Intersect: only colours that have a recent file on DropBox ─────
        val validM3 = list_m3?.filter { it.hasValidImage() }
        Log.d(TAG, "list_m3 total=${list_m3?.size}, valides=${validM3?.size}")

        validM3?.forEach { color ->
            val inIndex = index.containsKey(color.nomImageFichieSansEtansion)
            if (!inIndex) {
                Log.d(TAG, "EXCLU de l'index DropBox: '${color.nomImageFichieSansEtansion}' " +
                        "(absent ou trop ancien par rapport à sinceMs)")
            }
        }

        val toSync = validM3?.filter { index.containsKey(it.nomImageFichieSansEtansion) }

        if (toSync.isNullOrEmpty()) {
            Log.d(TAG, "toSync vide → rien à faire")
            onProgress(1f, "")
            return@withContext SyncReport(emptyList(), emptyList())
        }
        Log.d(TAG, "toSync: ${toSync.size} couleurs à traiter")

        val total            = toSync.size.toFloat()
        var done             = 0
        val addedFiles       = mutableListOf<String>()
        val overwrittenFiles = mutableListOf<String>()

        // ── 3. Download when DropBox is newer ─────────────────────────────────
        toSync.forEachIndexed { idx, color ->
            val fullName  = "${color.nomImageFichieSansEtansion}.${color.extensionDisponible}"
            val localFile = File(
                M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
                fullName
            )
            val meta         = index[color.nomImageFichieSansEtansion]!!
            val dropBoxModMs = meta.clientModified?.time ?: meta.serverModified?.time ?: 0L
            val localModMs   = if (localFile.exists()) localFile.lastModified() else 0L

            val productName  = produitKeyToName[color.parentBProduitInfosKeyID]
                ?: color.nomImageFichieSansEtansion
            val label        = "$productName  (${idx + 1} / ${toSync.size})"

            Log.d(TAG, "[$fullName] " +
                    "localExists=${localFile.exists()} | " +
                    "localMod=${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(localModMs))} | " +
                    "dropBoxMod=${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(dropBoxModMs))} | " +
                    "dropBox>local=${dropBoxModMs > localModMs}"
            )

            onProgress(done / total, label)

            if (dropBoxModMs > localModMs) {
                val wasNew = !localFile.exists()
                Log.d(TAG, "[$fullName] → TÉLÉCHARGEMENT (wasNew=$wasNew)")
                try {
                    localFile.parentFile?.mkdirs()
                    FileOutputStream(localFile).use { out ->
                        client.files().download(meta.pathLower).download(out)
                    }
                    if (dropBoxModMs > 0L) localFile.setLastModified(dropBoxModMs)
                    Log.d(TAG, "[$fullName] ✅ téléchargé, lastModified mis à $dropBoxModMs")

                    if (wasNew) addedFiles.add(fullName) else overwrittenFiles.add(fullName)

                } catch (e: Exception) {
                    Log.e(TAG, "[$fullName] ❌ ERREUR téléchargement: ${e.message}", e)
                    localFile.delete()
                }
            } else {
                Log.d(TAG, "[$fullName] → IGNORÉ (local déjà à jour ou plus récent)")
            }

            done++
            onProgress(done / total, label)
        }

        onProgress(1f, "")
        SyncReport(added = addedFiles, overwritten = overwrittenFiles)
    }

    /**
     * Lists Images_2 on DropBox (recursive) and returns a filename-stem → metadata map.
     * When [sinceMs] > 0, entries with `serverModified < sinceMs` are discarded during the
     * listing itself, so the resulting map is small before it ever reaches the caller.
     */
    private suspend fun buildImages2Index(sinceMs: Long = 0L): Map<String, FileMetadata> =
        withContext(Dispatchers.IO) {
            val index = mutableMapOf<String, FileMetadata>()
            try {
                var result = client.files()
                    .listFolderBuilder(M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox)
                    .withRecursive(true).start()
                while (true) {
                    result.entries
                        .filterIsInstance<FileMetadata>()
                        .filter { sinceMs == 0L || (it.serverModified?.time ?: 0L) >= sinceMs }
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
        try { client.files().createFolderV2(path) } catch (_: Exception) { }
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
     * Uploads [imageBytes] to [M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox]/[fileName],
     * overwriting any existing file with the same name.  Called after a successful camera capture.
     * Returns the DropBox path on success, or null if the upload failed.
     */
    /**
     * Uploads [imageBytes] to DropBox Images_2/[fileName], overwriting any existing file.
     *
     * [clientModifiedMs] is stamped on the DropBox entry as `clientModified`.
     * It defaults to **now − 40 seconds** so that the local file's `lastModified` (≈ now)
     * is always newer than the DropBox `clientModified`; syncFromImages2 therefore
     * treats the local copy as up-to-date and skips re-downloading it.
     */
    suspend fun uploadToImages2(
        fileName: String,
        imageBytes: ByteArray,
        clientModifiedMs: Long = System.currentTimeMillis() - 40L * 1_000,
    ): String? = withContext(Dispatchers.IO) {
        val targetPath = "${M3CouleurProduitInfos.Companion.rootFolder_Images_2_DropBox}/$fileName"
        return@withContext try {
            client.files()
                .uploadBuilder(targetPath)
                .withMode(WriteMode.OVERWRITE)
                .withClientModified(java.util.Date(clientModifiedMs))
                .uploadAndFinish(imageBytes.inputStream())
            targetPath
        } catch (_: Exception) {
            null
        }
    }

    private fun M3CouleurProduitInfos.hasValidImage() =
        nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"
}

private enum class FilterState {
    AUCUN,
    PREMIER_CHECK,
    NON_PREMIER_CHECK
}
