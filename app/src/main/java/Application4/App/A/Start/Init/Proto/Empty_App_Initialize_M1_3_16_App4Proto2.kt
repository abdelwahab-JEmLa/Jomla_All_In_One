package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

object Empty_App_Initialize_M1_3_16_App4Proto2 {
    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos }
    suspend fun getReturn_Filtred_For_Presenter_M1_3_16(
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

        var seededColors     = emptyList<M3CouleurProduitInfos>()
        var seededProducts   = emptyList<M01Produit>()
        var seededCategories = emptyList<M16CategorieProduit>()

        suspend fun seedColors() {
            seededColors = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val color = child.getValue(M3CouleurProduitInfos::class.java) ?: return@mapNotNull null
                    if (color.keyID.isBlank() || color.keyID != nodeKey) color.copy(keyID = nodeKey) else color
                }

            Log.d("SeedColors", "allColorsFetched total=${seededColors.size}")
            Log.d("SeedColors", "  its_pour_affiche_au_presenter=true  : ${seededColors.count { it.its_pour_affiche_au_presenter == true }}")
            Log.d("SeedColors", "  its_pour_affiche_au_presenter=false/null: ${seededColors.count { it.its_pour_affiche_au_presenter != true }}")
            if (seededColors.isNotEmpty()) {
                Log.d("SeedColors", "  sample its_pour_affiche_au_presenter values (first 5): " +
                        seededColors.take(5).map { "${it.keyID.take(6)}→${it.its_pour_affiche_au_presenter}" })
            }

            seededColors = seededColors
                .filter { it.its_pour_affiche_au_presenter == true }
                .sortedBy { it.parentProduit_Classement }

            Log.d("SeedColors", "seededColors after presenter-filter + sort by parentProduit_Classement: ${seededColors.size}")
        }

        suspend fun seedProducts() {
            if (seededColors.isEmpty()) return

            val m3ParentKeys = seededColors.map { it.parentBProduitInfosKeyID }.toSet()

            // Derive echatillant product keys from M3 colours (source of truth is now M3, not M1)
            val echatillantProductKeys = seededColors
                .filter { it.its_in_echantiallants == true }
                .map { it.parentBProduitInfosKeyID }
                .toSet()


            val allProducts = M01Produit.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val product = child.getValue(M01Produit::class.java) ?: return@mapNotNull null
                    if (product.keyID.isBlank() || product.keyID != nodeKey) product.copy(keyID = nodeKey)
                    else product
                }

            // Include products referenced by active M3 keys OR by echatillant M3 colours
            seededProducts = allProducts
                .filter { it.keyID in m3ParentKeys || it.keyID in echatillantProductKeys }

            // Include all colours for echatillant products even if outside the active key filter
            if (echatillantProductKeys.isNotEmpty()) {
                val existingColorKeys = seededColors.map { it.keyID }.toSet()
                val extraColors = seededColors.filter {
                    it.parentBProduitInfosKeyID in echatillantProductKeys && it.keyID !in existingColorKeys
                }
                if (extraColors.isNotEmpty()) seededColors = seededColors + extraColors
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
            try {
                setProgress(repo.name, 0.2f)
                block()
                markComplete(repo.name)
            } catch (_: Exception) {
                markComplete(repo.name)
            }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }

        seedRepo(Repo.M1Produit, isOnline) {
            seedProducts()
        }

        seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() }

        on_Progress_Datas(1f)
        return SeedResult(seededColors, seededProducts, seededCategories)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // No active-key filtering — fetches every record (used by DeleteInsertAll_Ref_All_Datas)
    // ─────────────────────────────────────────────────────────────────────────
    suspend fun getReturne_M1_3_16_AllRefs(
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
            val productCategoryIds = allProducts.map { it.idParentCategorie }.toSet()
            allCategories = M16CategorieProduit.ref.get().await()
                .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                .filter { it.id in productCategoryIds }
        }

        suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
            if (!isOnline) { markComplete(repo.name); return }
            try {
                setProgress(repo.name, 0.2f)
                block()
                markComplete(repo.name)
            } catch (_: Exception) {
                markComplete(repo.name)
            }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }
        seedRepo(Repo.M1Produit,             isOnline) { seedProducts() }
        seedRepo(Repo.M16CategorieProduit,   isOnline) { seedCategories() }

        on_Progress_Datas(1f)
        return SeedResult(allColors, allProducts, allCategories)
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
