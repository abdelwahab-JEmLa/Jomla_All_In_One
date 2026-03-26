package Application4.App.A.Start.Init

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Dao13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.Dao14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.SQL.Dao_M8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
object Initializer_App4 {

    suspend fun initializeAllRepositories(
        context: Context,
        appDatabase: AppDatabase,
        on_Progress_Datas: (Float) -> Unit,
        dao_M1Produit: Dao_M1Produit,
        dao_16CategorieProduit: Dao_M16CategorieProduit,
        dao_M03CouleurProduitInfos: Dao_M03CouleurProduitInfos,
        dao_M13TarificationInfos: Dao13TarificationInfos,
        dao_M14VentPeriode: Dao14VentPeriode,
        dao_M8BonVent: Dao_M8BonVent,
        dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
        dao_M9AppCompt: Dao_M9AppCompt,
        callerScope: CoroutineScope,
    ) {
        val mutex = Mutex()
        val progress = mutableMapOf<String, Float>()
        fun emit() = on_Progress_Datas(if (progress.isEmpty()) 0f else progress.values.average().toFloat())
        suspend fun setProgress(name: String, value: Float) = mutex.withLock { progress[name] = value; emit() }
        suspend fun markComplete(name: String) = mutex.withLock { progress[name] = 1f; emit() }

        mutex.withLock {
            listOf("M1Produit", "M16CategorieProduit", "M3CouleurProduitInfos",
                "M13TarificationInfos", "M14VentPeriode", "M8BonVent",
                "M10OperationVentCouleur", "Z_AppCompt").forEach { progress[it] = 0f }
        }

        val isOnline = isInternetAvailable(context)
        suspend fun seedRepo(name: String, block: suspend () -> Unit) {
            if (!isOnline) { markComplete(name); return }
            try { setProgress(name, 0.2f); block(); markComplete(name) } catch (_: Exception) { markComplete(name) }
        }

        coroutineScope {
            launch(Dispatchers.IO) {
                seedRepo("M1Produit") {
                    if (dao_M1Produit.getAll().isNotEmpty()) return@seedRepo
                    M01Produit.ref.get().await().children.mapNotNull { it.getValue(M01Produit::class.java) }
                        .takeIf { it.isNotEmpty() }?.let { dao_M1Produit.insertAll(it) }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("M16CategorieProduit") {
                    if (dao_16CategorieProduit.getAll().isNotEmpty()) return@seedRepo
                    fetchWithRetry(M16CategorieProduit.refFirestore)
                        ?.documents?.mapNotNull { it.toObject(M16CategorieProduit::class.java) }
                        ?.takeIf { it.isNotEmpty() }?.let { dao_16CategorieProduit.insertAll(it) }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("M3CouleurProduitInfos") {
                    if (dao_M03CouleurProduitInfos.getAll().isEmpty()) {
                        fetchWithRetry(M3CouleurProduitInfos.refFirestore)
                            ?.documents?.mapNotNull { it.toObject(M3CouleurProduitInfos::class.java) }
                            ?.takeIf { it.isNotEmpty() }?.let { dao_M03CouleurProduitInfos.insertAll(it) }
                            ?: return@seedRepo
                    }
                    if (isOnline) callerScope.launch(Dispatchers.IO) {
                        DropboxImageSyncer.syncAll(dao_M03CouleurProduitInfos) { p ->
                            callerScope.launch { setProgress("M3CouleurProduitInfos", p) }
                        }
                    }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("M13TarificationInfos") {
                    if (dao_M13TarificationInfos.getAll().isNotEmpty()) return@seedRepo
                    M13TarificationInfos.ref.get().await().children.mapNotNull { it.getValue(M13TarificationInfos::class.java) }
                        .takeIf { it.isNotEmpty() }?.let { dao_M13TarificationInfos.insertAll(it) }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("M14VentPeriode") {
                    if (dao_M14VentPeriode.getAll().isNotEmpty()) return@seedRepo
                    M14VentPeriode.ref.get().await().children.mapNotNull { it.getValue(M14VentPeriode::class.java) }
                        .takeIf { it.isNotEmpty() }?.let { dao_M14VentPeriode.insertAll(it) }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("M8BonVent") {
                    if (dao_M8BonVent.getAll().isNotEmpty()) return@seedRepo
                    M8BonVent.ref.get().await().children.mapNotNull { it.getValue(M8BonVent::class.java) }
                        .takeIf { it.isNotEmpty() }?.let { dao_M8BonVent.insertAll(it) }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("M10OperationVentCouleur") {
                    if (dao_M10OperationVentCouleur.getAll().isNotEmpty()) return@seedRepo
                    M10OperationVentCouleur.ref.get().await().children.mapNotNull { it.getValue(M10OperationVentCouleur::class.java) }
                        .takeIf { it.isNotEmpty() }?.let { dao_M10OperationVentCouleur.insertAll(it) }
                }
            }
            launch(Dispatchers.IO) {
                seedRepo("Z_AppCompt") {
                    if (dao_M9AppCompt.getAll().isNotEmpty()) return@seedRepo
                    Z_AppCompt.ref.get().await().children.mapNotNull { it.getValue(Z_AppCompt::class.java) }
                        .takeIf { it.isNotEmpty() }?.let { dao_M9AppCompt.insertAll(it) }
                }
            }
        }
        on_Progress_Datas(1f)
    }

    private suspend fun fetchWithRetry(ref: CollectionReference, maxAttempts: Int = 5, retryDelayMs: Long = 1500L): QuerySnapshot? {
        repeat(maxAttempts) {
            try { return ref.get(Source.SERVER).await() } catch (_: FirebaseFirestoreException) { delay(retryDelayMs) }
        }
        return null
    }

    private fun isInternetAvailable(context: Context) = try {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
    } catch (_: Exception) { false }
}
