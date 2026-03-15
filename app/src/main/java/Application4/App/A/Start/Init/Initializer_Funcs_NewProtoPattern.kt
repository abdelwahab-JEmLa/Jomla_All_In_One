package Application4.App.A.Start.Init

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.SQL.ArticlesBasesStatsModelDao
import EntreApps.Shared.Modules.Base.SQL.M16CategorieProduitDao
import EntreApps.Shared.Modules.Base.SQL.M3CouleurProduitInfosDao
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Dao13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.Dao14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.SQL.GBonVentDao
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
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

class Initializer_Funcs_NewProtoPattern(
    val context: Context,
    val on_Progress_Datas: (Float) -> Unit,
    val dao_M1Produit: ArticlesBasesStatsModelDao,
    val dao_16CategorieProduit: M16CategorieProduitDao,
    val dao_M3CouleurProduitInfos: M3CouleurProduitInfosDao,
    val dao_M13TarificationInfos: Dao13TarificationInfos,
    val dao_M14VentPeriode: Dao14VentPeriode,
    val dao_M8BonVent: GBonVentDao,
    val dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
    val dao_M9AppCompt: Dao_M9AppCompt,
) {
    private val mutex = Mutex()
    private val progress = mutableMapOf<String, Float>()
    val repoScope = CoroutineScope(Dispatchers.IO)

    private val dropboxSyncer = DropboxImageSyncer(
        dao_M3CouleurProduitInfos = dao_M3CouleurProduitInfos,
        onProgress = { p -> setProgressBlocking(Repo.M3CouleurProduitInfos.name, p) },
    )

    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos, M13TarificationInfos, M14VentPeriode, M8BonVent, M10OperationVentCouleur, Z_AppCompt }

    suspend fun initializeAllRepositories() {
        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)
        listOf(
            repoScope.launch { seedRepo(Repo.M1Produit, isOnline) { seedProducts() } },
            repoScope.launch { seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() } },
            repoScope.launch { seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors(isOnline) } },
            repoScope.launch { seedRepo(Repo.M13TarificationInfos, isOnline) { seedTarifications() } },
            repoScope.launch { seedRepo(Repo.M14VentPeriode, isOnline) { seedVentPeriodes() } },
            repoScope.launch { seedRepo(Repo.M8BonVent, isOnline) { seedBonVents() } },
            repoScope.launch { seedRepo(Repo.M10OperationVentCouleur, isOnline) { seedOperationVentCouleurs() } },
            repoScope.launch { seedRepo(Repo.Z_AppCompt, isOnline) { seedAppCompts() } },
        ).joinAll()
        updateMainInitDataBaseProgressEtate(1f)
    }

    private suspend fun seedProducts() {
        if (dao_M1Produit.getAll().isNotEmpty()) return
        val items = M01Produit.ref.get().await().children.mapNotNull { it.getValue(M01Produit::class.java) }
        if (items.isNotEmpty()) dao_M1Produit.insertAll(items)
    }

    private suspend fun seedCategories() {
        if (dao_16CategorieProduit.getAll().isNotEmpty()) return
        val items = fetchWithRetry(M16CategorieProduit.refFirestore)
            ?.documents?.mapNotNull { it.toObject(M16CategorieProduit::class.java) } ?: return
        if (items.isNotEmpty()) dao_16CategorieProduit.insertAll(items)
    }

    private suspend fun seedColors(isOnline: Boolean) {
        if (dao_M3CouleurProduitInfos.getAll().isEmpty()) {
            val items = fetchWithRetry(M3CouleurProduitInfos.refFirestore)
                ?.documents?.mapNotNull { it.toObject(M3CouleurProduitInfos::class.java) } ?: return
            if (items.isNotEmpty()) dao_M3CouleurProduitInfos.insertAll(items)
        }
        if (isOnline) dropboxSyncer.syncAll()
    }

    private suspend fun seedTarifications() {
        if (dao_M13TarificationInfos.getAll().isNotEmpty()) return
        val items = M13TarificationInfos.ref.get().await().children.mapNotNull { it.getValue(M13TarificationInfos::class.java) }
        if (items.isNotEmpty()) dao_M13TarificationInfos.insertAll(items)
    }

    private suspend fun seedVentPeriodes() {
        if (dao_M14VentPeriode.getAll().isNotEmpty()) return
        val items = M14VentPeriode.ref.get().await().children.mapNotNull { it.getValue(M14VentPeriode::class.java) }
        if (items.isNotEmpty()) dao_M14VentPeriode.insertAll(items)
    }

    private suspend fun seedBonVents() {
        if (dao_M8BonVent.getAll().isNotEmpty()) return
        val items = M8BonVent.ref.get().await().children.mapNotNull { it.getValue(M8BonVent::class.java) }
        if (items.isNotEmpty()) dao_M8BonVent.insertAll(items)
    }

    private suspend fun seedOperationVentCouleurs() {
        if (dao_M10OperationVentCouleur.getAll().isNotEmpty()) return
        val items = M10OperationVentCouleur.ref.get().await().children.mapNotNull { it.getValue(M10OperationVentCouleur::class.java) }
        if (items.isNotEmpty()) dao_M10OperationVentCouleur.insertAll(items)
    }

    private suspend fun seedAppCompts() {
        if (dao_M9AppCompt.getAll().isNotEmpty()) return
        val items = Z_AppCompt.ref.get().await().children.mapNotNull { it.getValue(Z_AppCompt::class.java) }
        if (items.isNotEmpty()) dao_M9AppCompt.insertAll(items)
    }

    private suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
        if (!isOnline) { markComplete(repo.name); return }
        try { setProgress(repo.name, 0.2f); block(); markComplete(repo.name) }
        catch (e: Exception) { markComplete(repo.name) }
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

    private suspend fun setProgress(name: String, value: Float) =
        mutex.withLock { progress[name] = value; emitAggregatedProgress() }

    private fun setProgressBlocking(name: String, value: Float) =
        repoScope.launch { setProgress(name, value) }

    private suspend fun markComplete(name: String) =
        mutex.withLock { progress[name] = 1f; emitAggregatedProgress() }

    private fun emitAggregatedProgress() =
        updateMainInitDataBaseProgressEtate(if (progress.isEmpty()) 0f else progress.values.average().toFloat())

    fun updateMainInitDataBaseProgressEtate(loadingProgress: Float) = on_Progress_Datas(loadingProgress)

    private fun isInternetAvailable(context: Context) = try {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo?.isConnected == true
    } catch (e: Exception) { false }
}
