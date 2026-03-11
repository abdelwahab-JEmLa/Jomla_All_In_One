package Application2.App.Init

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.toArticle
import EntreApps.Shared.Modules.Base.SQL.ArticlesBasesStatsModelDao
import EntreApps.Shared.Modules.Base.SQL.M16CategorieProduitDao
import EntreApps.Shared.Modules.Base.SQL.M3CouleurProduitInfosDao
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
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TAG = "Dropbox"

class Initializer_Funcs_app2(
    val context: Context,
    val on_Progress_Datas: (Float) -> Unit,
    val dao_M1Produit: ArticlesBasesStatsModelDao,
    val dao_16CategorieProduit: M16CategorieProduitDao,
    val dao_M3CouleurProduitInfos: M3CouleurProduitInfosDao,
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
        listOf(
            repoScope.launch { seedRepo(Repo.M1Produit, isOnline) { seedProducts() } },
            repoScope.launch { seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() } },
            repoScope.launch { seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors(isOnline) } },
        ).joinAll()
        updateMainInitDataBaseProgressEtate(1f)
    }

    private suspend fun seedProducts() {
        if (dao_M1Produit.getAll().isNotEmpty()) return
        val items = fetchWithRetry(M01Produit.refFirestore)
            ?.documents?.mapNotNull { it.toArticle() } ?: return
        if (items.isNotEmpty()) dao_M1Produit.upsertAllDatas(items)
    }

    private suspend fun seedCategories() {
        if (dao_16CategorieProduit.getAll().isNotEmpty()) return
        val items = fetchWithRetry(M16CategorieProduit.refFirestore)
            ?.documents?.mapNotNull { it.toObject(M16CategorieProduit::class.java) } ?: return
        if (items.isNotEmpty()) dao_16CategorieProduit.upsertAllDatas(items)
    }

    private suspend fun seedColors(isOnline: Boolean) {
        if (dao_M3CouleurProduitInfos.getAll().isEmpty()) {
            val items = fetchWithRetry(M3CouleurProduitInfos.refFirestore)
                ?.documents?.mapNotNull { it.toObject(M3CouleurProduitInfos::class.java) } ?: return
            if (items.isNotEmpty()) dao_M3CouleurProduitInfos.upsertAllDatas(items)
        }
        if (!isOnline) return

        setProgress(Repo.M3CouleurProduitInfos.name, 0.1f)
        dropboxIndex = buildDropboxIndex()

        if (dropboxIndex.isEmpty()) {
            Log.e(TAG, "Index vide — token invalide ou dossier '$dropboxRootFolder' introuvable")
            return
        }
        Log.d(TAG, "Index construit: ${dropboxIndex.size} fichiers")

        val colors = dao_M3CouleurProduitInfos.getAll()
        val total = colors.size.coerceAtLeast(1)
        colors.forEachIndexed { index, color ->
            setProgress(Repo.M3CouleurProduitInfos.name, 0.2f + 0.8f * (index.toFloat() / total))
            syncColorImageFromDropbox(color)
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
