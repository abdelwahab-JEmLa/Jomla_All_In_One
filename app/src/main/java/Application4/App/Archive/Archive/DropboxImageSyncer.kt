package Application4.App.Archive.Archive

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M3CouleurProduitInfos
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

object DropboxImageSyncer {
    private val client: DbxClientV2 by lazy {
        val config = DbxRequestConfig.newBuilder("jeMla-app/1.0").build()
        val credential = DbxCredential(
            "",
            -1L,
            BuildConfig.DROPBOX_REFRESH_TOKEN,
            BuildConfig.DROPBOX_APP_KEY,
            BuildConfig.DROPBOX_APP_SECRET
        )
        DbxClientV2(config, credential)
    }

    suspend fun syncAll(dao_M03CouleurProduitInfos: Dao_M03CouleurProduitInfos, onProgress: (Float) -> Unit) {
        onProgress(0.1f)
        val index = buildIndex().takeIf { it.isNotEmpty() } ?: run { onProgress(1f); return }
        val colors = dao_M03CouleurProduitInfos.getAll()
        val total = colors.size.coerceAtLeast(1)
        colors.forEachIndexed { i, color ->
            syncImage(color, index)
            onProgress(0.2f + 0.8f * ((i + 1).toFloat() / total))
        }
        onProgress(1f)
    }

    private suspend fun buildIndex(): Map<String, String> = withContext(Dispatchers.IO) {
        val index = mutableMapOf<String, String>()
        try {
            var result = client.files().listFolderBuilder("/images").withRecursive(true).start()
            while (true) {
                result.entries.filterIsInstance<FileMetadata>().forEach { entry ->
                    val path = entry.pathLower ?: return@forEach
                    index[entry.name.substringBeforeLast(".")] = path
                }
                if (!result.hasMore) break
                result = client.files().listFolderContinue(result.cursor)
            }
        } catch (_: Exception) {
        }
        index
    }

    private suspend fun syncImage(color: M3CouleurProduitInfos, index: Map<String, String>) {
        val filename = color.nomImageFichieSansEtansion
        if (filename == "Non Dispo" || filename.isBlank()) return
        val localFile = File(
            M00CentralParametresOfAllApps.Companion.images_central_Local_storageLink,
            "$filename.${color.extensionDisponible}"
        )
        if (localFile.exists()) return
        val dropboxPath = index[filename] ?: return
        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use {
                    client.files().download(dropboxPath).download(it)
                }
            }
        } catch (_: Exception) { localFile.delete() }
    }
}
