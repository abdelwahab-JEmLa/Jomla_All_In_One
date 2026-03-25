package Application2.App.Init

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.example.clientjetpack.BuildConfig
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TAG = "Dropbox"

@Suppress("DEPRECATION")
class Initializer_Funcs_app2(
    val context: Context,
    val on_Progress_Datas: (Float) -> Unit,
    val dao_M1Produit: Dao_M1Produit,
    val dao_16CategorieProduit: Dao_M16CategorieProduit,
    val dao_M03CouleurProduitInfos: Dao_M03CouleurProduitInfos,
) {
    private val mutex = Mutex()
    private val progress = mutableMapOf<String, Float>()
    val repoScope = CoroutineScope(Dispatchers.IO)

    private val localImagesBaseDir = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne")
    private val dropboxRootFolder = "/images"

    private val dropboxClient: DbxClientV2 by lazy {
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

    private var dropboxIndex: Map<String, String> = emptyMap()

    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos }

    suspend fun initializeAllRepositories() {
        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)
        // M3 must be seeded first — M1 and M16 filters depend on its Room content
        repoScope.launch { seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors(isOnline) } }.join()
        // M1Produit must be seeded before M16CategorieProduit — categories filter by available product IDs
        repoScope.launch { seedRepo(Repo.M1Produit, isOnline) { seedProducts() } }.join()
        repoScope.launch { seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() } }.join()
        updateMainInitDataBaseProgressEtate(1f)
    }

    private suspend fun seedProducts() {
        if (dao_M1Produit.getAll().isNotEmpty()) return

        // Only keep products that have at least one M3 colour in Room
        val m3ParentKeys: Set<String> = dao_M03CouleurProduitInfos.getAll()
            .map { it.parentBProduitInfosKeyID }
            .toSet()

        val items = M01Produit.ref.get().await()
            .children.mapNotNull { it.getValue(M01Produit::class.java) }
            .filter { it.keyID in m3ParentKeys }
        if (items.isNotEmpty()) dao_M1Produit.insertAll(items)
    }

    private suspend fun seedCategories() {
        if (dao_16CategorieProduit.getAll().isNotEmpty()) return

        // Only keep categories that have at least one M1Produit in Room
        val m1CategoryIds: Set<Long> = dao_M1Produit.getAll()
            .map { it.idParentCategorie }
            .toSet()

        val items = M16CategorieProduit.ref.get().await()
            .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
            .filter { it.id in m1CategoryIds }
        if (items.isNotEmpty()) dao_16CategorieProduit.insertAll(items)
    }

    private suspend fun seedColors(isOnline: Boolean) {
        // Always work from the filtered list — seed it if Room is empty, otherwise read it back
        val filteredColors: List<M3CouleurProduitInfos> = if (dao_M03CouleurProduitInfos.getAll().isEmpty()) {
            val allowedKeys: Set<String> = M3CouleurProduitInfos.ref_listKeys_M3CouleurProduitInfos
                .get().await()
                .children.mapNotNull { it.key }
                .toSet()

            val items = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { it.getValue(M3CouleurProduitInfos::class.java) }
                .filter { it.keyID in allowedKeys }
            if (items.isNotEmpty()) dao_M03CouleurProduitInfos.insertAll(items)
            items
        } else {
            dao_M03CouleurProduitInfos.getAll()
        }

        if (!isOnline) return

        setProgress(Repo.M3CouleurProduitInfos.name, 0.1f)
        dropboxIndex = buildDropboxIndex()

        if (dropboxIndex.isEmpty()) {
            Log.e(TAG, "Index vide — token invalide ou dossier '$dropboxRootFolder' introuvable")
            return
        }
        Log.d(TAG, "Index construit: ${dropboxIndex.size} fichiers")

        val total = filteredColors.size.coerceAtLeast(1)
        filteredColors.forEachIndexed { index, color ->
            setProgress(Repo.M3CouleurProduitInfos.name, 0.2f + 0.8f * ((index + 1).toFloat() / total))
            // Sync Dropbox seulement si dropBox_key pas encore assignée
            if (color.dropBox_key == "Non Dispo") {
                syncColorImageFromDropbox(color)
            }
        }
    }

    private suspend fun buildDropboxIndex(): Map<String, String> = withContext(Dispatchers.IO) {
        val index = mutableMapOf<String, String>()
        try {
            Log.d(TAG, "Listage '$dropboxRootFolder' récursif…")
            var result = dropboxClient.files()
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
                result = dropboxClient.files().listFolderContinue(result.cursor)
            }
            Log.d(TAG, "Listage terminé: ${index.size} fichiers en $page page(s)")
        } catch (e: Exception) {
            Log.e(TAG, "buildDropboxIndex échoué [${e::class.simpleName}]: ${e.message}")
            Log.e(TAG, "  → Token valide? Dossier '$dropboxRootFolder' existe?")
        }
        index
    }

    private suspend fun syncColorImageFromDropbox(color: M3CouleurProduitInfos) {
        val filename = color.nomImageFichieSansEtansion
        if (filename == "Non Dispo" || filename.isBlank()) return

        val localFile = File(localImagesBaseDir, "$filename.${color.extensionDisponible}")
        if (localFile.exists()) return

        val dropboxPath = dropboxIndex[filename]
        if (dropboxPath == null) {
            Log.w(TAG, "Introuvable dans index: '$filename' (parent=${color.parentBProduitInfosKeyID.takeLast(4)})")
            Log.w(TAG, "  → Clés similaires: ${dropboxIndex.keys.filter { it.startsWith(filename.take(4)) }.take(3)}")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use { out ->
                    dropboxClient.files().download(dropboxPath).download(out)
                }
            }
            Log.d(TAG, "OK '$filename' ← $dropboxPath (${localFile.length()} bytes)")
        } catch (e: Exception) {
            localFile.delete()
            Log.e(TAG, "Téléchargement échoué: '$filename' [${e::class.simpleName}]: ${e.message}")
        }
    }

    private suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
        if (!isOnline) { markComplete(repo.name); return }
        try {
            setProgress(repo.name, 0.2f)
            block()
            markComplete(repo.name)
        } catch (e: Exception) {
            markComplete(repo.name)
        }
    }

    private suspend fun fetchWithRetry(
        ref: CollectionReference,
        maxAttempts: Int = 5,
        retryDelayMs: Long = 1500L,
    ): QuerySnapshot? {
        repeat(maxAttempts) {
            try { return ref.get(Source.SERVER).await() }
            catch (e: FirebaseFirestoreException) { delay(retryDelayMs) }
        }
        return null
    }

    private suspend fun setProgress(name: String, value: Float) {
        mutex.withLock { progress[name] = value; emitAggregatedProgress() }
    }

    private suspend fun markComplete(name: String) {
        mutex.withLock { progress[name] = 1f; emitAggregatedProgress() }
    }

    private fun emitAggregatedProgress() {
        val avg = if (progress.isEmpty()) 0f else progress.values.average().toFloat()
        updateMainInitDataBaseProgressEtate(avg)
    }

    fun updateMainInitDataBaseProgressEtate(loadingProgress: Float) {
        on_Progress_Datas(loadingProgress)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.activeNetworkInfo?.isConnected == true
        } catch (e: Exception) { false }
    }
}
