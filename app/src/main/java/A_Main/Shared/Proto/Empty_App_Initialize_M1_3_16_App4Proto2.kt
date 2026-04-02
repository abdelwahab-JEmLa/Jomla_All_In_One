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

object Empty_App_Initialize_M1_3_16_App4Proto2 {
    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos }

    suspend fun getReturn_Filtred_For_Presenter_M1_3_16(
        context: Context,
        on_Progress_Datas: (Float) -> Unit,
    ): SeedResult {
        val mutex = Mutex()
        val progress = mutableMapOf<String, Float>()
        val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                AppType.JomLaElectroLivreurGrossist_PresenterScreen

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
                .also { presenterFiltered ->
                    // In presenter mode, additionally strip échantillon colors.
                    // They are not shown in the main presenter list and will not be backfilled
                    // by insertMissingEchatillantsProductsAndColors (also skipped in presenter mode).
                    if (isPresenter) {
                        val beforeEchant = presenterFiltered.size
                        val afterEchant  = presenterFiltered.count { it.its_in_echantiallants != true }
                        Log.d("SeedColors",
                            "Presenter mode: excluding échantillon colors — " +
                                    "before=$beforeEchant after=$afterEchant " +
                                    "(${beforeEchant - afterEchant} dropped)")
                    }
                }
                .filter { color ->
                    // Drop échantillon colors in presenter mode
                    if (isPresenter) color.its_in_echantiallants != true else true
                }
                .sortedBy { it.parentProduit_Classement }

            Log.d("SeedColors", "seededColors after presenter-filter + sort by parentProduit_Classement: ${seededColors.size}")

            if (isPresenter) {
                Log.d("PresenterSeed", "━━━ [1/3] COLORS ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                Log.d("PresenterSeed", "  total seeded colors          : ${seededColors.size}")
                Log.d("PresenterSeed", "  distinct parent product keys  : ${seededColors.map { it.parentBProduitInfosKeyID }.toSet().size}")
                Log.d("PresenterSeed", "  échantillon colors (excluded) : ${seededColors.count { it.its_in_echantiallants == true }} (should be 0)")
            }
        }

        suspend fun seedProducts() {
            if (seededColors.isEmpty()) return

            val m3ParentKeys = seededColors.map { it.parentBProduitInfosKeyID }.toSet()

            // Derive echatillant product keys from M3 colours (source of truth is M3, not M1).
            // In presenter mode this will always be empty because its_in_echantiallants colors
            // were already filtered out in seedColors().
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

            if (isPresenter) {
                Log.d("PresenterSeed", "━━━ [2/3] PRODUCTS ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                Log.d("PresenterSeed", "  total fetched from Firebase   : ${allProducts.size}")
                Log.d("PresenterSeed", "  seeded after m3 key filter    : ${seededProducts.size}")
                Log.d("PresenterSeed", "  dropped (no matching color)   : ${allProducts.size - seededProducts.size}")
                Log.d("PresenterSeed", "  echatillant product keys      : ${echatillantProductKeys.size} (should be 0 in presenter mode)")
            }
        }

        suspend fun seedCategories() {
            // Presenter screen does not display or use categories — skip the Firebase fetch.
            // This saves bandwidth and avoids a pointless round-trip on every presenter boot.
            if (isPresenter) {
                Log.d("PresenterSeed", "━━━ [3/3] CATEGORIES ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                Log.d("PresenterSeed", "  skipped (not used in presenter screen)")
                return
            }
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
        seedRepo(Repo.M1Produit, isOnline) { seedProducts() }
        seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() }

        on_Progress_Datas(1f)

        if (isPresenter) {
            Log.d("PresenterSeed", "━━━ SEED SUMMARY (Presenter mode) ━━━━━━━━━━━━━━━━━━")
            Log.d("PresenterSeed", "  colors     : ${seededColors.size}")
            Log.d("PresenterSeed", "  products   : ${seededProducts.size}")
            Log.d("PresenterSeed", "  categories : ${seededCategories.size} (skipped)")
            Log.d("PresenterSeed", "  light DBs  : skipped (m13/m14/m8/m10)")
            Log.d("PresenterSeed", "  échantillons backfill : skipped")
            Log.d("PresenterSeed", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }

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
