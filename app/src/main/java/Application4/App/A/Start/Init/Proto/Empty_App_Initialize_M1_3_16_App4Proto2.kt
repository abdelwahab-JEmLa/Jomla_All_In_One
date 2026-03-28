package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Ref_list_Filtred_Keys_M3Couleur_Main_Values
import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await


object Empty_App_Initialize_M1_3_16_App4Proto2 {
    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos }

    suspend fun getReturne_M1_3_16(
        context: Context,
        on_Progress_Datas: (Float) -> Unit,
    ): SeedResult {
        val mutex = Mutex()
        val progress = mutableMapOf<String, Float>()

        fun emitAggregatedProgress() {
            on_Progress_Datas(if (progress.isEmpty()) 0f else progress.values.average().toFloat())
        }
        suspend fun setProgress(name: String, value: Float) =
            mutex.withLock { progress[name] = value; emitAggregatedProgress() }
        suspend fun markComplete(name: String) =
            mutex.withLock { progress[name] = 1f; emitAggregatedProgress() }

        var seededColors     = emptyList<M3CouleurProduitInfos>()
        var seededProducts   = emptyList<M01Produit>()
        var seededCategories = emptyList<M16CategorieProduit>()

        suspend fun seedColors() {
            val allowedKeys = M3CouleurProduitInfos.ref_listKeys_M3CouleurProduitInfos
                .get().await().children.mapNotNull { it.key }.toSet()
            seededColors = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { it.getValue(M3CouleurProduitInfos::class.java) }
                .filter { it.keyID in allowedKeys }
        }

        suspend fun seedProducts() {
            val m3ParentKeys = seededColors.map { it.parentBProduitInfosKeyID }.toSet()
            val classementByProduitKey = M3CouleurProduitInfos.ref_listKeys_M3CouleurProduitInfos
                .get().await().children
                .mapNotNull { snap ->
                    snap.getValue(Ref_list_Filtred_Keys_M3Couleur_Main_Values::class.java)
                        ?.let { it.parentProduitKeyID to it.parentProduitClassement }
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, v) -> v.max() }
            seededProducts = M01Produit.ref.get().await()
                .children.mapNotNull { it.getValue(M01Produit::class.java) }
                .filter { it.keyID in m3ParentKeys }
                .map { produit ->
                    classementByProduitKey[produit.keyID]
                        ?.takeIf { it != produit.classement_By_FilterKeys_M3 }
                        ?.let { produit.copy(classement_By_FilterKeys_M3 = it) }
                        ?: produit
                }
        }

        suspend fun seedCategories() {
            val m1CategoryIds = seededProducts.map { it.idParentCategorie }.toSet()
            seededCategories = M16CategorieProduit.ref.get().await()
                .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                .filter { it.id in m1CategoryIds }
        }

        suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
            if (!isOnline) { markComplete(repo.name); return }
            try { setProgress(repo.name, 0.2f); block(); markComplete(repo.name) }
            catch (_: Exception) { markComplete(repo.name) }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }
        seedRepo(Repo.M1Produit, isOnline) { seedProducts() }
        seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() }

        on_Progress_Datas(1f)
        return SeedResult(seededColors, seededProducts, seededCategories)
    }

    private fun isInternetAvailable(context: Context): Boolean = try {
        @Suppress("DEPRECATION")
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo?.isConnected == true
    } catch (_: Exception) { false }
    data class SeedResult(
        val colors: List<M3CouleurProduitInfos> = emptyList(),
        val products: List<M01Produit> = emptyList(),
        val categories: List<M16CategorieProduit> = emptyList(),
    )

}
