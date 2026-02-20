package Application2.App.Init

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.toArticle
import EntreApps.Shared.Modules.Dao.SQL.ArticlesBasesStatsModelDao
import EntreApps.Shared.Modules.Dao.SQL.M16CategorieProduitDao
import EntreApps.Shared.Modules.Dao.SQL.M3CouleurProduitInfosDao
import android.content.Context
import android.net.ConnectivityManager
import com.dropbox.core.DbxRequestConfig
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

    /** Local base directory where color images are stored */
    private val localImagesBaseDir = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne")

    /**
     * Root Dropbox folder that contains the dated sub-folders.
     * Structure: /images/1_1/xxx.webp  /images/2_4/yyy.webp  etc.
     */
    private val dropboxRootFolder = "/images"

    private val dropboxClient: DbxClientV2 by lazy {
        val config = DbxRequestConfig.newBuilder("jeMla-app/1.0").build()
        DbxClientV2(config,BuildConfig.DROPBOX_ACCESS_TOKEN)}

    /**
     * In-memory map built once at startup:
     *   filename-without-extension  →  full Dropbox path
     * e.g. "image_5" → "/images/1_21/image_5.webp"
     *
     * Avoids re-listing Dropbox on every color lookup.
     */
    private var dropboxIndex: Map<String, String> = emptyMap()

    enum class Repo {
        M1Produit,
        M16CategorieProduit,
        M3CouleurProduitInfos,
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    suspend fun initializeAllRepositories() {
        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }

        val isOnline = isInternetAvailable(context)

        listOf(
            repoScope.launch { seedRepo(Repo.M1Produit, isOnline) { seedProducts() } },
            repoScope.launch { seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() } },
            repoScope.launch {
                seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors(isOnline) }
            },
        ).joinAll()

        updateMainInitDataBaseProgressEtate(1f)
    }

    // ── Per-table seeding ─────────────────────────────────────────────────────

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

    /**
     * 1) Seeds M3CouleurProduitInfos metadata from Firestore.
     * 2) Builds a full index of every file across all sub-folders in Dropbox /images.
     * 3) For each color missing its local image → looks up the index → downloads.
     */
    private suspend fun seedColors(isOnline: Boolean) {
        // ── 1. Firestore metadata ─────────────────────────────────────────────
        if (dao_M3CouleurProduitInfos.getAll().isEmpty()) {
            val items = fetchWithRetry(M3CouleurProduitInfos.refFirestore)
                ?.documents?.mapNotNull { it.toObject(M3CouleurProduitInfos::class.java) } ?: return
            if (items.isNotEmpty()) dao_M3CouleurProduitInfos.upsertAllDatas(items)
        }

        if (!isOnline) return

        // ── 2. Build Dropbox index (filename → full path) once ─────────────────
        setProgress(Repo.M3CouleurProduitInfos.name, 0.1f)
        dropboxIndex = buildDropboxIndex()

        if (dropboxIndex.isEmpty()) {
            android.util.Log.w("Initializer_Funcs_app2", "Dropbox index is empty — skipping image sync")
            return
        }

        android.util.Log.d("Initializer_Funcs_app2", "Dropbox index built: ${dropboxIndex.size} files found")

        // ── 3. Download missing images ─────────────────────────────────────────
        val colors = dao_M3CouleurProduitInfos.getAll()
        val total  = colors.size.coerceAtLeast(1)

        colors.forEachIndexed { index, color ->
            setProgress(Repo.M3CouleurProduitInfos.name, 0.2f + 0.8f * (index.toFloat() / total))
            syncColorImageFromDropbox(color)
        }
    }

    /**
     * Recursively lists every file under [dropboxRootFolder] and builds a map:
     *   "filename_without_extension" → "/images/sub_folder/filename.ext"
     *
     * Handles Dropbox pagination automatically (cursor-based).
     */
    private suspend fun buildDropboxIndex(): Map<String, String> =
        withContext(Dispatchers.IO) {
            val index = mutableMapOf<String, String>()
            try {
                // list_folder with recursive=true walks all sub-folders in one go
                var result = dropboxClient.files()
                    .listFolderBuilder(dropboxRootFolder)
                    .withRecursive(true)
                    .start()

                while (true) {
                    for (entry in result.entries) {
                        if (entry is FileMetadata) {
                            // key = filename without extension, value = full Dropbox path
                            val nameWithoutExt = entry.name.substringBeforeLast(".")
                            index[nameWithoutExt] = entry.pathLower as String
                        }
                        // FolderMetadata entries are skipped — we only care about files
                    }

                    if (!result.hasMore) break

                    // Fetch next page using the cursor
                    result = dropboxClient.files().listFolderContinue(result.cursor)
                }
            } catch (e: Exception) {
                android.util.Log.e("Initializer_Funcs_app2", "Failed to build Dropbox index: ${e.message}")
            }
            index
        }

    /**
     * Checks if [color]'s image is already on device.
     * If not, looks up [dropboxIndex] for the matching path and downloads it.
     */
    private suspend fun syncColorImageFromDropbox(color: M3CouleurProduitInfos) {
        val filename = color.nomImageFichieSansEtansion
        if (filename == "Non Dispo" || filename.isBlank()) return

        val localFile = File(localImagesBaseDir, "$filename.${color.extensionDisponible}")
        if (localFile.exists()) return  // already on device

        // Look up the pre-built index — O(1), no extra network call
        val dropboxPath = dropboxIndex[filename]
        if (dropboxPath == null) {
            android.util.Log.w("Initializer_Funcs_app2", "Image '$filename' not found in Dropbox index")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                localFile.parentFile?.mkdirs()
                FileOutputStream(localFile).use { out ->
                    dropboxClient.files().download(dropboxPath).download(out)
                }
            }
            android.util.Log.d("Initializer_Funcs_app2", "Downloaded '$filename' from $dropboxPath")
        } catch (e: Exception) {
            localFile.delete() // clean up partial write
            android.util.Log.w("Initializer_Funcs_app2", "Failed to download '$filename': ${e.message}")
        }
    }

    // ── Generic wrapper with progress + error guard ───────────────────────────

    private suspend fun seedRepo(
        repo: Repo,
        isOnline: Boolean,
        block: suspend () -> Unit,
    ) {
        if (!isOnline) {
            android.util.Log.d("Initializer_Funcs_app2", "${repo.name}: offline — skipping Firebase seed")
            markComplete(repo.name)
            return
        }
        try {
            android.util.Log.d("Initializer_Funcs_app2", "${repo.name}: starting seed…")
            setProgress(repo.name, 0.2f)
            block()
            android.util.Log.d("Initializer_Funcs_app2", "${repo.name}: seed complete ✓")
            markComplete(repo.name)
        } catch (e: Exception) {
            android.util.Log.e("Initializer_Funcs_app2", "${repo.name}: seed failed — ${e.message}", e)
            markComplete(repo.name)
        }
    }

    // ── Firestore fetch with retries ──────────────────────────────────────────

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

    // ── Progress helpers ──────────────────────────────────────────────────────

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

    // ── Network check ─────────────────────────────────────────────────────────

    private fun isInternetAvailable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.activeNetworkInfo?.isConnected == true
        } catch (e: Exception) { false }
    }
}
