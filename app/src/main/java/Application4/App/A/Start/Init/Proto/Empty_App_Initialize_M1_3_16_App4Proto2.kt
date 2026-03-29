package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Ref_list_Filtred_Keys_M3Couleur_Main_Values
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await


object Empty_App_Initialize_M1_3_16_App4Proto2 {
    private const val TAG = "SeedInit"
    enum class Repo { M1Produit, M16CategorieProduit, M3CouleurProduitInfos }

    // ─────────────────────────────────────────────────────────────────────────
    // Existing: filtered by active M3 keys (used by DeleteInsertAll_Active_Key)
    // ─────────────────────────────────────────────────────────────────────────
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
        var seededFilterKeys = emptyList<Ref_list_Filtred_Keys_M3Couleur_Main_Values>()
        var seededProducts   = emptyList<M01Produit>()
        var seededCategories = emptyList<M16CategorieProduit>()

        suspend fun seedColors() {
            Log.d(TAG, "seedColors: fetching ref keys…")
            val refKeysSnap = M3CouleurProduitInfos.ref_listKeys_M3CouleurProduitInfos
                .get().await()
            val allowedKeys = refKeysSnap.children.mapNotNull { it.key }.toSet()
            Log.d(TAG, "seedColors: allowedKeys.size=${allowedKeys.size}  keys=$allowedKeys")

            seededFilterKeys = refKeysSnap.children
                .mapNotNull { it.getValue(Ref_list_Filtred_Keys_M3Couleur_Main_Values::class.java) }
            Log.d(TAG, "seedColors: seededFilterKeys.size=${seededFilterKeys.size}")

            val allColors = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val color   = child.getValue(M3CouleurProduitInfos::class.java)
                        ?: return@mapNotNull null
                    if (color.keyID.isBlank() || color.keyID != nodeKey) color.copy(keyID = nodeKey)
                    else color
                }
            Log.d(TAG, "seedColors: raw Firebase colors count=${allColors.size}")

            seededColors = allColors.filter { it.keyID in allowedKeys }
            Log.d(TAG, "seedColors: seededColors after filter=${seededColors.size}" +
                    (if (seededColors.isEmpty() && allColors.isNotEmpty())
                        " ⚠️ ALL FILTERED OUT — keyID mismatch? sample keyIDs=${allColors.take(3).map { it.keyID }}"
                    else ""))
        }

        suspend fun seedProducts() {
            Log.d(TAG, "seedProducts: seededColors.size=${seededColors.size}")
            if (seededColors.isEmpty()) {
                Log.w(TAG, "seedProducts: ⚠️ seededColors is empty — products will be empty too. Check seedColors logs above.")
                return
            }

            val m3ParentKeys = seededColors.map { it.parentBProduitInfosKeyID }.toSet()
            Log.d(TAG, "seedProducts: m3ParentKeys.size=${m3ParentKeys.size}  sample=${m3ParentKeys.take(3)}")

            val classementByProduitKey = seededFilterKeys
                .map { it.parentProduitKeyID to it.parentProduitClassement }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, v) -> v.max() }

            val allProducts = M01Produit.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val product = child.getValue(M01Produit::class.java)
                        ?: return@mapNotNull null
                    if (product.keyID.isBlank() || product.keyID != nodeKey) product.copy(keyID = nodeKey)
                    else product
                }
            Log.d(TAG, "seedProducts: raw Firebase products count=${allProducts.size}")

            seededProducts = allProducts
                .filter { it.keyID in m3ParentKeys }
                .map { produit ->
                    classementByProduitKey[produit.keyID]
                        ?.takeIf { it != produit.classement_By_FilterKeys_M3 }
                        ?.let { produit.copy(classement_By_FilterKeys_M3 = it) }
                        ?: produit
                }
            Log.d(TAG, "seedProducts: seededProducts after filter=${seededProducts.size}" +
                    (if (seededProducts.isEmpty() && allProducts.isNotEmpty())
                        " ⚠️ ALL FILTERED OUT — parentBProduitInfosKeyID mismatch? sample product keyIDs=${allProducts.take(3).map { it.keyID }}"
                    else ""))
        }

        suspend fun seedCategories() {
            Log.d(TAG, "seedCategories: seededProducts.size=${seededProducts.size}")
            val m1CategoryIds = seededProducts.map { it.idParentCategorie }.toSet()
            Log.d(TAG, "seedCategories: distinct category ids=${m1CategoryIds.size}  ids=$m1CategoryIds")
            val allCats = M16CategorieProduit.ref.get().await()
                .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
            Log.d(TAG, "seedCategories: raw Firebase categories count=${allCats.size}")
            seededCategories = allCats.filter { it.id in m1CategoryIds }
            Log.d(TAG, "seedCategories: seededCategories after filter=${seededCategories.size}")
        }

        suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
            if (!isOnline) {
                Log.w(TAG, "seedRepo: ${repo.name} SKIPPED — device is offline")
                markComplete(repo.name); return
            }
            try {
                setProgress(repo.name, 0.2f)
                block()
                markComplete(repo.name)
            } catch (e: Exception) {
                Log.e(TAG, "seedRepo: ${repo.name} FAILED — ${e::class.simpleName}: ${e.message}", e)
                markComplete(repo.name)
            }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)
        Log.d(TAG, "getReturne_M1_3_16: isOnline=$isOnline")

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }

        // After seedProducts() applies classements from filterKeys onto products, sync those
        // classements back into seededFilterKeys so callers (e.g. the upload button) always
        // receive filter keys whose parentProduitClassement reflects the current sort order.
        seedRepo(Repo.M1Produit, isOnline) {
            seedProducts()
            val classementByProductKey = seededProducts.associateBy(
                keySelector   = { it.keyID },
                valueTransform = { it.classement_By_FilterKeys_M3 }
            )
            seededFilterKeys = seededFilterKeys.map { key ->
                val updated = classementByProductKey[key.parentProduitKeyID]
                    ?: return@map key
                if (updated != key.parentProduitClassement) key.copy(parentProduitClassement = updated)
                else key
            }
            Log.d(TAG, "seedProducts: seededFilterKeys classements synced for ${seededFilterKeys.size} keys")
        }

        seedRepo(Repo.M16CategorieProduit, isOnline) { seedCategories() }

        on_Progress_Datas(1f)
        Log.i(TAG, "getReturne_M1_3_16 DONE — " +
                "colors=${seededColors.size} filterKeys=${seededFilterKeys.size} " +
                "products=${seededProducts.size} categories=${seededCategories.size}")
        return SeedResult(seededColors, seededFilterKeys, seededProducts, seededCategories)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // New: NO active-key filtering — fetches every record from each ref.
    // Used by DeleteInsertAll_Ref_All_Datas.
    // Products are kept only if they have at least one matching colour.
    // Categories are kept only if they have at least one matching product.
    // ─────────────────────────────────────────────────────────────────────────
    suspend fun getReturne_M1_3_16_AllRefs(
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

        var allColors     = emptyList<M3CouleurProduitInfos>()
        var allProducts   = emptyList<M01Produit>()
        var allCategories = emptyList<M16CategorieProduit>()

        suspend fun seedColors() {
            Log.d(TAG, "AllRefs.seedColors: fetching ALL M3 ref records (no key filter)…")
            allColors = M3CouleurProduitInfos.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val color   = child.getValue(M3CouleurProduitInfos::class.java)
                        ?: return@mapNotNull null
                    if (color.keyID.isBlank() || color.keyID != nodeKey) color.copy(keyID = nodeKey)
                    else color
                }
            Log.d(TAG, "AllRefs.seedColors: count=${allColors.size}")
        }

        suspend fun seedProducts() {
            Log.d(TAG, "AllRefs.seedProducts: fetching ALL M1 ref records (no key filter)…")
            val colorParentKeys = allColors.map { it.parentBProduitInfosKeyID }.toSet()
            allProducts = M01Produit.ref.get().await()
                .children.mapNotNull { child ->
                    val nodeKey = child.key ?: return@mapNotNull null
                    val product = child.getValue(M01Produit::class.java)
                        ?: return@mapNotNull null
                    if (product.keyID.isBlank() || product.keyID != nodeKey) product.copy(keyID = nodeKey)
                    else product
                }
                .filter { it.keyID in colorParentKeys }
            Log.d(TAG, "AllRefs.seedProducts: count=${allProducts.size}")
        }

        suspend fun seedCategories() {
            Log.d(TAG, "AllRefs.seedCategories: fetching ALL M16 ref records (no key filter)…")
            val productCategoryIds = allProducts.map { it.idParentCategorie }.toSet()
            allCategories = M16CategorieProduit.ref.get().await()
                .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                .filter { it.id in productCategoryIds }
            Log.d(TAG, "AllRefs.seedCategories: count=${allCategories.size}")
        }

        suspend fun seedRepo(repo: Repo, isOnline: Boolean, block: suspend () -> Unit) {
            if (!isOnline) {
                Log.w(TAG, "AllRefs.seedRepo: ${repo.name} SKIPPED — device is offline")
                markComplete(repo.name); return
            }
            try {
                setProgress(repo.name, 0.2f)
                block()
                markComplete(repo.name)
            } catch (e: Exception) {
                Log.e(TAG, "AllRefs.seedRepo: ${repo.name} FAILED — ${e::class.simpleName}: ${e.message}", e)
                markComplete(repo.name)
            }
        }

        mutex.withLock { Repo.entries.forEach { progress[it.name] = 0f } }
        val isOnline = isInternetAvailable(context)
        Log.d(TAG, "getReturne_M1_3_16_AllRefs: isOnline=$isOnline")

        seedRepo(Repo.M3CouleurProduitInfos, isOnline) { seedColors() }
        seedRepo(Repo.M1Produit,             isOnline) { seedProducts() }
        seedRepo(Repo.M16CategorieProduit,   isOnline) { seedCategories() }

        on_Progress_Datas(1f)
        Log.i(TAG, "getReturne_M1_3_16_AllRefs DONE — " +
                "colors=${allColors.size} products=${allProducts.size} categories=${allCategories.size}")
        // filterKeys is empty — not used in the unfiltered path
        return SeedResult(allColors, emptyList(), allProducts, allCategories)
    }

    private fun isInternetAvailable(context: Context): Boolean = try {
        @Suppress("DEPRECATION")
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo?.isConnected == true
    } catch (_: Exception) { false }

    data class SeedResult(
        val colors: List<M3CouleurProduitInfos> = emptyList(),
        val filterKeys: List<Ref_list_Filtred_Keys_M3Couleur_Main_Values> = emptyList(),
        val products: List<M01Produit> = emptyList(),
        val categories: List<M16CategorieProduit> = emptyList(),
    )
}
