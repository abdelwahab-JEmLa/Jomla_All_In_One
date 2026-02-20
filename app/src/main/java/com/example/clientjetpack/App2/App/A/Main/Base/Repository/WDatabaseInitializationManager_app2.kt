package com.example.clientjetpack.App2.App.A.Main.Base.Repository

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.toArticle
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.M3CouleurProduitInfosDao
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.M16CategorieProduitDao
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.H.Dao.ArticlesBasesStatsModelDao
// FocusedValuesGetter_app2 removed — progress is now forwarded via on_Progress_Datas callback
import android.content.Context
import android.net.ConnectivityManager
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

    enum class Repo {
        M1Produit,
        M16CategorieProduit,
        M3CouleurProduitInfos,
    }

    // ── Seeding guard — one-shot ───────────────────────────────────────────────
    private var isSeedingFromFirebase = false


    // ── Entry point ───────────────────────────────────────────────────────────
    suspend fun initializeAllRepositories() {
        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }

        val isOnline = isInternetAvailable(context)

        listOf(
            repoScope.launch { seedRepo(Repo.M1Produit, isOnline) { seedProducts() } },
            repoScope.launch { seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() } },
            repoScope.launch { seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() } },
        ).joinAll()

        updateMainInitDataBaseProgressEtate(1f)
    }

    // ── Per-table seeding ─────────────────────────────────────────────────────

    private suspend fun seedProducts() {
        if (dao_M1Produit.getAll().isNotEmpty()) return
        val items = fetchWithRetry(ArticlesBasesStatsTable.refFirestore)
            ?.documents?.mapNotNull { it.toArticle() } ?: return
        if (items.isNotEmpty()) dao_M1Produit.upsertAllDatas(items)
    }

    private suspend fun seedCategories() {
        if (dao_16CategorieProduit.getAll().isNotEmpty()) return
        val items = fetchWithRetry(M16CategorieProduit.refFirestore)
            ?.documents?.mapNotNull { it.toObject(M16CategorieProduit::class.java) } ?: return
        if (items.isNotEmpty()) dao_16CategorieProduit.upsertAllDatas(items)
    }

    private suspend fun seedColors() {
        if (dao_M3CouleurProduitInfos.getAll().isNotEmpty()) return
        val items = fetchWithRetry(M3CouleurProduitInfos.refFirestore)
            ?.documents?.mapNotNull { it.toObject(M3CouleurProduitInfos::class.java) } ?: return
        if (items.isNotEmpty()) dao_M3CouleurProduitInfos.upsertAllDatas(items)
    }

    // ── Generic wrapper with progress + error guard ───────────────────────────

    private suspend fun seedRepo(
        repo: Repo,
        isOnline: Boolean,
        block: suspend () -> Unit,
    ) {
        if (!isOnline) {
            markComplete(repo.name); return
        }
        if (isSeedingFromFirebase) return
        try {
            isSeedingFromFirebase = true
            setProgress(repo.name, 0.2f)
            block()
            markComplete(repo.name)
        } catch (e: Exception) {
            isSeedingFromFirebase = false   // allow retry on next launch
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
            try {
                return ref.get(Source.SERVER).await()
            } catch (e: FirebaseFirestoreException) {
                delay(retryDelayMs)
            }
        }
        return null
    }

    // ── Progress helpers ──────────────────────────────────────────────────────

    private suspend fun setProgress(name: String, value: Float) {
        mutex.withLock {
            progress[name] = value
            emitAggregatedProgress()
        }
    }

    private suspend fun markComplete(name: String) {
        mutex.withLock {
            progress[name] = 1f
            emitAggregatedProgress()
        }
    }

    private fun emitAggregatedProgress() {
        val avg = if (progress.isEmpty()) 0f else progress.values.average().toFloat()
        updateMainInitDataBaseProgressEtate(avg)
    }

    // ── Progress sink ─────────────────────────────────────────────────────────

    fun updateMainInitDataBaseProgressEtate(loadingProgress: Float) {
        on_Progress_Datas(loadingProgress)
    }

    // ── Network check ─────────────────────────────────────────────────────────

    private fun isInternetAvailable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.activeNetworkInfo?.isConnected == true
        } catch (e: Exception) {
            false
        }
    }
}
