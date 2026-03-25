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

object DropboxImageSyncer {

    // Lazy client — created once on first use, reused forever.
    // No coroutine scope held here; the ViewModel's scope drives every call.
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

    private val dropboxRootFolder = "/images"

    suspend fun syncAll(
        dao_M03CouleurProduitInfos: Dao_M03CouleurProduitInfos,
        onUpdate_M3: (M3CouleurProduitInfos) -> Unit,
        onProgress: (Float) -> Unit,
        set_dropBox_key: Boolean,
    ) {
        onProgress(0.1f)
        val index = buildIndex()

        if (index.isEmpty()) {
            Log.e(TAG, "Index vide — token invalide ou dossier '$dropboxRootFolder' introuvable")
            onProgress(1f)
            return
        }
        Log.d(TAG, "Index construit: ${index.size} fichiers")

        val colors = dao_M03CouleurProduitInfos.getAll()
        val toSync  = colors.count { it.dropBox_key == "Non Dispo" && it.nomImageFichieSansEtansion != "Non Dispo" }
        val skipped = colors.count { it.dropBox_key != "Non Dispo" }
        Log.d(TAG, "syncAll — set_dropBox_key=$set_dropBox_key | total=${colors.size} | à sync=$toSync | déjà indexées (skip)=$skipped")

        val total = colors.size.coerceAtLeast(1)
        colors.forEachIndexed { i, color ->
            syncImage(color, index, set_dropBox_key, onUpdate_M3)
            onProgress(0.2f + 0.8f * ((i + 1).toFloat() / total))
        }
        onProgress(1f)
        Log.d(TAG, "syncAll terminé")
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
        onUpdate_M3: (M3CouleurProduitInfos) -> Unit,
    ) {
        val localImagesBaseDir = File(M00CentralParametresOfAllApps.images_central_Local_storageLink)
        val filename = color.nomImageFichieSansEtansion
        val id = color.keyID.takeLast(4).uppercase()

        if (filename == "Non Dispo" || filename.isBlank()) {
            Log.v(TAG, "[$id] skip — image Non Dispo"); return
        }
        if (!set_dropBox_key || color.dropBox_key != "Non Dispo") {
            Log.v(TAG, "[$id] skip sync — set_dropBox_key=$set_dropBox_key | dropBox_key='${color.dropBox_key.take(20)}'"); return
        }

        val localFile = File(localImagesBaseDir, "$filename.${color.extensionDisponible}")
        if (localFile.exists()) {
            Log.v(TAG, "[$id] fichier local déjà présent: $filename"); return
        }

        val dropboxPath = index[filename]
        if (dropboxPath == null) {
            Log.w(TAG, "[$id] Introuvable dans index: '$filename' (parent=${color.parentBProduitInfosKeyID.takeLast(4)})")
            Log.w(TAG, "[$id]   → Clés similaires: ${index.keys.filter { it.startsWith(filename.take(4)) }.take(3)}")
            return
        }

        Log.d(TAG, "[$id] téléchargement '$filename' ← $dropboxPath")
        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use { out ->
                    client.files().download(dropboxPath).download(out)
                }
            }
            onUpdate_M3(color.copy(dropBox_key = dropboxPath))
            Log.d(TAG, "[$id] OK '$filename' (${localFile.length()} bytes) | dropBox_key → $dropboxPath")
        } catch (e: Exception) {
            localFile.delete()
            Log.e(TAG, "[$id] téléchargement échoué: '$filename' [${e::class.simpleName}]: ${e.message}")
        }
    }
}
