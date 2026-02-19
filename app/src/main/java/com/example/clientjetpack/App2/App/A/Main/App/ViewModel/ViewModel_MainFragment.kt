package com.example.clientjetpack.App2.App.A.Main.App.ViewModel

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.toArticle
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.ProductDisplayController
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiTransferDatas_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiUpdateClientDisplayerStats_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UiState(
    val list_grouped_datas: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>  = emptyList(),
    val list_M1Produit: List<ArticlesBasesStatsTable> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val initDatasProgressEtate: Float = 0f,
)

@SuppressLint("StaticFieldLeak")
class ViewModel_MainFragment(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val focusedValuesGetter_app2: FocusedValuesGetter_app2,
) : ViewModel() {
    val wifi = WifiTransferDatas_app2(
        context = context,
        focusedValuesGetter_app2 = focusedValuesGetter_app2,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
    )

    // Single wifi state flow the UI collects from
    val wifiState = wifi.state
        .stateIn(viewModelScope, SharingStarted.Eagerly, ProductDisplayController())

    // -----------------------------------------------------------------------
    // Wifi delegates
    // -----------------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() { wifi.startAsHost(); wifi.updateTypePhone(isHost = true) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() { wifi.startAsClient(); wifi.updateTypePhone(isHost = false) }

    fun disconnect() = wifi.disconnect()

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) =
        wifi.sendOrderToClientDisplayer(orderName, data)

    fun sendOrderToClientDisplayerT(order: WifiUpdateClientDisplayerStats_app2, data: Any? = null) =
        wifi.sendOrderToClientDisplayerT(order, data)

    // -----------------------------------------------------------------------
    // Product-list state (Room + Firebase seed)
    // -----------------------------------------------------------------------

    private val dao_M1Produit = appDatabase.dao_M1Produit()
    private val dao_16CategorieProduit    = appDatabase.dao_16CategorieProduit()
    private val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var isSeedingFromFirebase = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao_M1Produit.getAllFlow(),
                dao_16CategorieProduit.getAllFlow(),
                dao_M3CouleurProduitInfos.getAllFlow()
            ) { products, categories, colors ->
                Triple(products, categories, colors)
            }.collect { (products, categories, colors) ->

                if (!isSeedingFromFirebase &&
                    (products.isEmpty() || categories.isEmpty() || colors.isEmpty())
                ) {
                    isSeedingFromFirebase = true
                    seedEmptyTablesFromFirebase(
                        productsEmpty   = products.isEmpty(),
                        categoriesEmpty = categories.isEmpty(),
                        colorsEmpty     = colors.isEmpty()
                    )
                    return@collect
                }

                _uiState.update {
                    it.copy(
                        list_M1Produit             = products,
                        list_M16CategorieProduit    = categories,
                        list_M3CouleurProduit        = colors,
                        list_grouped_datas           = get_grouped_datas(
                            allColors     = colors,
                            allProducts   = products,
                            allCategories = categories
                        ),
                        initDatasProgressEtate = 1f,
                    )
                }
                // Keep wifi's in-memory lists in sync so payload lookups stay accurate
                wifi.list_M1Produit = products
                wifi.list_M3CouleurProduit = colors
            }
        }
    }

    private suspend fun seedEmptyTablesFromFirebase(
        productsEmpty: Boolean,
        categoriesEmpty: Boolean,
        colorsEmpty: Boolean,
    ) {
        val TAG = "SeedFromFirebase"

        // Firestore s'initialise en offline puis active le réseau ~500ms après le démarrage
        // On attend que la connexion soit établie avant de tenter la requête
        var attempt = 0
        val maxAttempts = 5
        val retryDelayMs = 1500L

        suspend fun fetchProductsWithRetry(): com.google.firebase.firestore.QuerySnapshot? {
            while (attempt < maxAttempts) {
                attempt++
                try {
                    android.util.Log.d(TAG, "📡 tentative $attempt/$maxAttempts — SOURCE=SERVER")
                    val snapshot = ArticlesBasesStatsTable.refFirestore
                        .get(com.google.firebase.firestore.Source.SERVER)
                        .await()
                    android.util.Log.d(TAG, "✅ serveur répondu — ${snapshot.documents.size} docs")
                    return snapshot
                } catch (e: com.google.firebase.firestore.FirebaseFirestoreException) {
                    android.util.Log.w(TAG, "⏳ serveur pas prêt (tentative $attempt) — retry dans ${retryDelayMs}ms : ${e.message}")
                    kotlinx.coroutines.delay(retryDelayMs)
                }
            }
            android.util.Log.e(TAG, "❌ échec après $maxAttempts tentatives")
            return null
        }

        try {
            if (productsEmpty) {
                android.util.Log.d(TAG, "📦 productsEmpty=true → path: ${ArticlesBasesStatsTable.refFirestore.path}")

                val snapshot = fetchProductsWithRetry() ?: return

                if (snapshot.documents.isEmpty()) {
                    android.util.Log.w(TAG, "⚠️ collection Firestore vide")
                } else {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toArticle().also { result ->
                            if (result == null) android.util.Log.e(TAG, "  ❌ toArticle() null id=${doc.id}")
                            else android.util.Log.d(TAG, "  ✅ id=${doc.id} → nom='${result.nom}'")
                        }
                    }
                    android.util.Log.d(TAG, "📊 ${items.size}/${snapshot.documents.size} docs mappés")
                    if (items.isNotEmpty()) {
                        dao_M1Produit.upsertAllDatas(items)
                        android.util.Log.d(TAG, "✅ ${items.size} produits insérés dans Room")
                    }
                }
            }

            if (categoriesEmpty) {
                val items = M16CategorieProduit.ref.get().await()
                    .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                android.util.Log.d(TAG, "📂 categories: ${items.size}")
                if (items.isNotEmpty()) dao_16CategorieProduit.upsertAllDatas(items)
            }
            if (colorsEmpty) {
                val items = M3CouleurProduitInfos.ref.get().await()
                    .children.mapNotNull { it.getValue(M3CouleurProduitInfos::class.java) }
                android.util.Log.d(TAG, "🎨 couleurs: ${items.size}")
                if (items.isNotEmpty()) dao_M3CouleurProduitInfos.upsertAllDatas(items)
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "💥 seedEmptyTablesFromFirebase EXCEPTION", e)
            isSeedingFromFirebase = false
        }
    }

    fun get_grouped_datas(
        allColors: List<M3CouleurProduitInfos>,
        allProducts: List<ArticlesBasesStatsTable>,
        allCategories: List<M16CategorieProduit>
    ): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>> {

        val allCatalogues = get_ListM21CataloguesCategorie()

        val productColorPairs = allColors
            .groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                allProducts.find { it.keyID == productKeyID }?.let { it to colors }
            }
            .sortedBy { (product, _) -> product.nom }

        val categoryProductPairs = productColorPairs
            .groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, pairs) ->
                allCategories.find { it.id == categoryId }?.let { it to pairs }
            }
            .sortedBy { (category, _) -> category.positionDouble }

        return allCatalogues.sortedBy { it.position }.mapNotNull { catalogue ->
            val cats = categoryProductPairs
                .filter { (cat, _) -> cat.catalogueParentId == catalogue.id }
                .sortedBy { (cat, _) -> cat.positionDouble }
            if (cats.isNotEmpty()) catalogue to cats else null
        }
    }

    override fun onCleared() {
        super.onCleared()
        wifi.cancel()
    }
}
