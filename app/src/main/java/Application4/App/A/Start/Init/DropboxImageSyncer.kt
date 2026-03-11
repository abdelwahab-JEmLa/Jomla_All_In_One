package Application4.App.A.Start.Init

import EntreApps.Shared.Models.Home.CentraleMainGetter_NewProtoPattern
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.M3CouleurProduitInfosDao
import android.util.Log
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.example.clientjetpack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TAG = "DropboxImageSyncer"

class DropboxImageSyncer(
    private val dao_M3CouleurProduitInfos: M3CouleurProduitInfosDao,
    private val onProgress: (Float) -> Unit,
) {
    private val localImagesBaseDir = File(CentraleMainGetter_NewProtoPattern.images_central_Local_storageLink)
    private val dropboxRootFolder = "/images"

    private val client: DbxClientV2 by lazy {
        val config = DbxRequestConfig.newBuilder("jeMla-app/1.0").build()
        val credential = DbxCredential(
            "",
            -1L,
            BuildConfig.DROPBOX_REFRESH_TOKEN,
            BuildConfig.DROPBOX_APP_KEY,
            BuildConfig.DROPBOX_APP_SECRET,
        )
        DbxClientV2(config, credential)
    }

    suspend fun syncAll() {
        onProgress(0.1f)
        val index = buildIndex()

        if (index.isEmpty()) {
            Log.e(TAG, "Index vide — token invalide ou dossier '$dropboxRootFolder' introuvable")
            return
        }
        Log.d(TAG, "Index construit: ${index.size} fichiers")

        val colors = dao_M3CouleurProduitInfos.getAll()
        val total = colors.size.coerceAtLeast(1)
        colors.forEachIndexed { i, color ->
            onProgress(0.2f + 0.8f * (i.toFloat() / total))
            syncImage(color, index)
        }
    }

    private suspend fun buildIndex(): Map<String, String> = withContext(Dispatchers.IO) {
        val index = mutableMapOf<String, String>()
        try {
            Log.d(TAG, "Listage '$dropboxRootFolder' récursif…")
            var result = client.files()
                .listFolderBuilder(dropboxRootFolder)
                .withRecursive(true)
                .start()
            var page = 0
            while (true) {
                page++
                result.entries.filterIsInstance<FileMetadata>().forEach { entry ->
                    val path = entry.pathLower ?: return@forEach
                    index[entry.name.substringBeforeLast(".")] = path
                }
                Log.d(TAG, "Page $page — ${result.entries.size} entrées, index: ${index.size}")
                if (!result.hasMore) break
                result = client.files().listFolderContinue(result.cursor)
            }
            Log.d(TAG, "Listage terminé: ${index.size} fichiers en $page page(s)")
        } catch (e: Exception) {
            Log.e(TAG, "buildIndex échoué [${e::class.simpleName}]: ${e.message}")
        }
        index
    }

    private suspend fun syncImage(color: M3CouleurProduitInfos, index: Map<String, String>) {
        val filename = color.nomImageFichieSansEtansion
        if (filename == "Non Dispo" || filename.isBlank()) return

        val localFile = File(localImagesBaseDir, "$filename.${color.extensionDisponible}")
        if (localFile.exists()) return

        val dropboxPath = index[filename]
        if (dropboxPath == null) {
            Log.w(TAG, "Introuvable: '$filename' (parent=${color.parentBProduitInfosKeyID.takeLast(4)})")
            Log.w(TAG, "  → Clés similaires: ${index.keys.filter { it.startsWith(filename.take(4)) }.take(3)}")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use { out ->
                    client.files().download(dropboxPath).download(out)
                }
            }
            Log.d(TAG, "OK '$filename' ← $dropboxPath (${localFile.length()} bytes)")
        } catch (e: Exception) {
            localFile.delete()
            Log.e(TAG, "Téléchargement échoué: '$filename' [${e::class.simpleName}]: ${e.message}")
        }
    }
}
