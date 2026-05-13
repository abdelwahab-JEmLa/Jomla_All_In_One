package A_Main.Shared.Init

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.example.clientjetpack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object DropBox_Init {
    const val rootFolder: String = "/images"
    val localImagesBaseDir: File = File(M00CentralParametresOfAllApps.images_central_Local_storageLink)

    private val client: DbxClientV2 by lazy {
        DbxClientV2(
            DbxRequestConfig.newBuilder("jeMla-app/1.0").build(),
            DbxCredential("", -1L, BuildConfig.DROPBOX_REFRESH_TOKEN, BuildConfig.DROPBOX_APP_KEY, BuildConfig.DROPBOX_APP_SECRET)
        )
    }

    suspend fun syncAll(colors: List<M3CouleurProduitInfos>, onProgress: (Float) -> Unit = {}) =
        syncInternal(colors, onProgress)

    suspend fun syncAll(dao: Dao_M03CouleurProduitInfos, onProgress: (Float) -> Unit = {}) =
        syncInternal(dao.getAll(), onProgress)

    private suspend fun syncInternal(colors: List<M3CouleurProduitInfos>, onProgress: (Float) -> Unit) {
        onProgress(0.1f)

        val downloadable = colors.filter { color ->
            color.nomImageFichieSansEtansion.isNotBlank() &&
                    color.nomImageFichieSansEtansion != "Non Dispo"
        }
        if (downloadable.isEmpty()) { onProgress(1f); return }

        val index = buildIndex().takeIf { it.isNotEmpty() } ?: run { onProgress(1f); return }

        val total = downloadable.size
        downloadable.forEachIndexed { i, color ->
            syncImage(color, index)
            onProgress(0.2f + 0.8f * ((i + 1).toFloat() / total))
        }
        onProgress(1f)
    }

    private suspend fun buildIndex(): Map<String, String> = withContext(Dispatchers.IO) {
        val index = mutableMapOf<String, String>()
        try {
            var result = client.files().listFolderBuilder(rootFolder).withRecursive(true).start()
            while (true) {
                result.entries.filterIsInstance<FileMetadata>().forEach { entry ->
                    val path = entry.pathLower ?: return@forEach
                    index[entry.name.substringBeforeLast(".")] = path
                }
                if (!result.hasMore) break
                result = client.files().listFolderContinue(result.cursor)
            }
        } catch (_: Exception) {}
        index
    }

    private suspend fun syncImage(color: M3CouleurProduitInfos, index: Map<String, String>) {
        val filename = color.nomImageFichieSansEtansion
        if (filename == "Non Dispo" || filename.isBlank()) return
        val localFile = File(M00CentralParametresOfAllApps.images_central_Local_storageLink, "$filename.${color.extensionDisponible}")
        if (localFile.exists()) return
        val dropboxPath = index[filename] ?: return
        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use { client.files().download(dropboxPath).download(it) }
            }
        } catch (_: Exception) { localFile.delete() }
    }
}
