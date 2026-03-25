package Application4.App.A.Start.Init

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
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
    private val dao_M03CouleurProduitInfos: Dao_M03CouleurProduitInfos,
    private val onUpdate_M3: (M3CouleurProduitInfos) -> Unit,
    private val onProgress: (Float) -> Unit,
) {
    private val localImagesBaseDir = File(M00CentralParametresOfAllApps.images_central_Local_storageLink)
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

    suspend fun syncAll(set_dropBox_key: Boolean) {
        onProgress(0.1f)
        val index = buildIndex()

        if (index.isEmpty()) {
            Log.e(TAG, "Index vide — token invalide ou dossier '$dropboxRootFolder' introuvable")
            onProgress(1f)
            return
        }
        Log.d(TAG, "Index construit: ${index.size} fichiers")

        val colors = dao_M03CouleurProduitInfos.getAll()
        val total = colors.size.coerceAtLeast(1)
        colors.forEachIndexed { i, color ->
            syncImage(color, index, set_dropBox_key)
            onProgress(0.2f + 0.8f * ((i + 1).toFloat() / total))
        }
        onProgress(1f)
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

    private suspend fun syncImage(
        color: M3CouleurProduitInfos,
        index: Map<String, String>,
        set_dropBox_key: Boolean,
    ) {
        val filename = color.nomImageFichieSansEtansion

        // Skip si image non dispo
        if (filename == "Non Dispo" || filename.isBlank()) return

        // Skip la recherche Dropbox si trigger désactivé OU si dropBox_key déjà assignée
        if (!set_dropBox_key || color.dropBox_key != "Non Dispo") return

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
            onUpdate_M3(color.copy(dropBox_key = dropboxPath))
            Log.d(TAG, "OK '$filename' ← $dropboxPath (${localFile.length()} bytes) | dropBox_key mis à jour")
        } catch (e: Exception) {
            localFile.delete()
            Log.e(TAG, "Téléchargement échoué: '$filename' [${e::class.simpleName}]: ${e.message}")
        }
    }
}
