package A_Main.Shared.Proto

import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
 const val DBG_TAG              = "TargetedM3_Seed"
 const val DBG_M3_KEY           = "-OatXXVji-6A_OPcfUQa"
 const val DBG_PROD_KEY    = "-OatXXVji-6A_OPcfUQb"

object Empty_App_Initialize_M1_3_16_Filtered_App4Proto2 {

    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos }

    data class SeedResult(
        val colors: List<M3CouleurProduitInfos> = emptyList(),
        val products: List<M01Produit> = emptyList(),
        val categories: List<M16CategorieProduit> = emptyList(),
    )

    // ── Centralized filter predicates ──────────────────────────────────────────
    object ColorFilters {
        fun isVisible(color: M3CouleurProduitInfos) = color.its_pour_affiche_au_presenter
        fun isNotEchantillon(color: M3CouleurProduitInfos) = !color.its_in_echantiallants
    }

    fun isInternetAvailable(context: Context): Boolean = try {
        @Suppress("DEPRECATION")
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo?.isConnected == true
    } catch (_: Exception) { false }

    // ── Seed ───────────────────────────────────────────────────────────────────
    suspend fun getReturn_Filtred_For_Presenter_M1_3_16(
        context: Context,
        on_Progress_Datas: (Float) -> Unit,
    ): SeedResult {
        val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                AppType.JomLaElectroLivreurGrossist_PresenterScreen

        val mutex = Mutex()
        val progress = mutableMapOf<String, Float>()

        fun emitAggregatedProgress() =
            on_Progress_Datas(if (progress.isEmpty()) 0f else progress.values.average().toFloat())

        suspend fun setProgress(name: String, value: Float) =
            mutex.withLock { progress[name] = value; emitAggregatedProgress() }

        suspend fun markComplete(name: String) =
            mutex.withLock { progress[name] = 1f; emitAggregatedProgress() }

        suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
            if (!isOnline) { markComplete(repo.name); return }
            try { setProgress(repo.name, 0.2f); block(); markComplete(repo.name) }
            catch (_: Exception) { markComplete(repo.name) }
        }

        var colors     = emptyList<M3CouleurProduitInfos>()
        var products   = emptyList<M01Produit>()
        var categories = emptyList<M16CategorieProduit>()

        suspend fun seedColors() {
            colors = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val color = child.getValue(M3CouleurProduitInfos::class.java) ?: return@mapNotNull null
                    if (color.keyID.isBlank() || color.keyID != nodeKey) color.copy(keyID = nodeKey) else color
                }
                .filter { ColorFilters.isVisible(it) }
                .filter { if (isPresenter) ColorFilters.isNotEchantillon(it) else true }
                .sortedBy { it.parentProduit_Classement }

            // ── DEBUG: targeted M3 after seedColors ────────────────────────────
            val targeted = colors.find { it.keyID == DBG_M3_KEY }
            Log.d(DBG_TAG, "[seedColors] targeted M3 present=${targeted != null}" +
                    " | keyID=${targeted?.keyID}" +
                    " | parent=${targeted?.parentBProduitInfosKeyID}" +
                    " | img=${targeted?.nomImageFichieSansEtansion}" +
                    " | visible=${targeted?.its_pour_affiche_au_presenter}" +
                    " | totalColors=${colors.size}")
        }

        suspend fun seedProducts() {
            if (colors.isEmpty()) return
            val m3ParentKeys = colors.map { it.parentBProduitInfosKeyID }.toSet()
            val echatillantKeys = colors
                .filter { !ColorFilters.isNotEchantillon(it) }
                .map { it.parentBProduitInfosKeyID }.toSet()
            val allProducts = M01Produit.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val product = child.getValue(M01Produit::class.java) ?: return@mapNotNull null
                    if (product.keyID.isBlank() || product.keyID != nodeKey) product.copy(keyID = nodeKey)
                    else product
                }
            products = allProducts.filter { it.keyID in m3ParentKeys || it.keyID in echatillantKeys }
            if (echatillantKeys.isNotEmpty()) {
                val existingColorKeys = colors.map { it.keyID }.toSet()
                val extraColors = colors.filter {
                    it.parentBProduitInfosKeyID in echatillantKeys && it.keyID !in existingColorKeys
                }
                if (extraColors.isNotEmpty()) colors = colors + extraColors
            }

            // ── DEBUG: targeted M3 parent product after seedProducts ───────────
            val targetedProduct = products.find { it.keyID == DBG_PROD_KEY }
            val targetedColor   = colors.find   { it.keyID == DBG_M3_KEY }
            Log.d(DBG_TAG, "[seedProducts] targeted product present=${targetedProduct != null}" +
                    " | productKeyID=${targetedProduct?.keyID}" +
                    " | productNom=${targetedProduct?.nom}" +
                    " | targetedM3 still in colors=${targetedColor != null}" +
                    " | totalProducts=${products.size}")
        }

        suspend fun seedCategories() {
            if (isPresenter) return
            val categoryIds = products.map { it.idParentCategorie }.toSet()
            categories = M16CategorieProduit.ref.get().await()
                .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                .filter { it.id in categoryIds }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }
        seedRepo(Repo.M1Produit,             isOnline) { seedProducts() }
        seedRepo(Repo.M16CategorieProduit,   isOnline) { seedCategories() }

        // ── DEBUG: final SeedResult summary ───────────────────────────────────
        val finalTargetedColor   = colors.find   { it.keyID == DBG_M3_KEY }
        val finalTargetedProduct = products.find { it.keyID == DBG_PROD_KEY }
        Log.d(DBG_TAG, "[SeedResult] targetedM3 in final colors=${finalTargetedColor != null}" +
                " | targetedProduct in final products=${finalTargetedProduct != null}" +
                " | colors=${colors.size} | products=${products.size} | categories=${categories.size}")

        on_Progress_Datas(1f)
        return SeedResult(colors, products, categories)
    }
}
