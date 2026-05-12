package A_Main.Shared.Init

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await


object Empty_App_Initialize_M1_3_16_AllRefs_App4Proto2 {

    suspend fun getReturn_M1_3_16_AllRefs(
        context: Context,
        on_Progress_Datas: (Float) -> Unit,
    ): SeedResult {
        val mutex = Mutex()
        val progress = mutableMapOf<String, Float>()

        fun emitAggregatedProgress() =
            on_Progress_Datas(if (progress.isEmpty()) 0f else progress.values.average().toFloat())

        suspend fun setProgress(name: String, value: Float) =
            mutex.withLock { progress[name] = value; emitAggregatedProgress() }

        suspend fun markComplete(name: String) =
            mutex.withLock { progress[name] = 1f; emitAggregatedProgress() }

        suspend fun seedRepo(
            repo: Repo,
            isOnline: Boolean,
            block: suspend () -> Unit,
        ) {
            if (!isOnline) { markComplete(repo.name); return }
            try { setProgress(repo.name, 0.2f); block(); markComplete(repo.name) }
            catch (_: Exception) { markComplete(repo.name) }
        }

        var allColors     = emptyList<M3CouleurProduitInfos>()
        var allProducts   = emptyList<M01Produit>()
        var allCategories = emptyList<M16CategorieProduit>()

        suspend fun seedColors() {
            allColors = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val color = child.getValue(M3CouleurProduitInfos::class.java) ?: return@mapNotNull null
                    if (color.keyID.isBlank() || color.keyID != nodeKey) color.copy(keyID = nodeKey) else color
                }
        }

        suspend fun seedProducts() {
            val colorParentKeys = allColors.map { it.parentBProduitInfosKeyID }.toSet()
            allProducts = M01Produit.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val product = child.getValue(M01Produit::class.java) ?: return@mapNotNull null
                    if (product.keyID.isBlank() || product.keyID != nodeKey) product.copy(keyID = nodeKey)
                    else product
                }
                .filter { it.keyID in colorParentKeys }
        }

        suspend fun seedCategories() {
            val categoryIds = allProducts.map { it.idParentCategorie }.toSet()
            allCategories = M16CategorieProduit.ref.get().await()
                .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                .filter { it.id in categoryIds }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }
        seedRepo(Repo.M1Produit,             isOnline) { seedProducts() }
        seedRepo(Repo.M16CategorieProduit,   isOnline) { seedCategories() }

        on_Progress_Datas(1f)
        return A_Main.Shared.Init.SeedResult(allColors, allProducts, allCategories)
    }
}
