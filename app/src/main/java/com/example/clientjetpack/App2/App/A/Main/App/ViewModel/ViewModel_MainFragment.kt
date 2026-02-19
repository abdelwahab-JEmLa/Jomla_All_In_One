package com.example.clientjetpack.App2.App.A.Main.App.ViewModel

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
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
                        try {
                            val d = doc.data ?: run {
                                android.util.Log.e(TAG, "  ❌ doc.data NULL id=${doc.id}")
                                return@mapNotNull null
                            }

                            fun str(key: String, default: String = "") = (d[key] as? String) ?: default
                            fun bool(key: String, default: Boolean = false) = (d[key] as? Boolean) ?: default
                            fun long(key: String, default: Long = 0L) = (d[key] as? Long) ?: (d[key] as? Number)?.toLong() ?: default
                            fun int(key: String, default: Int = 0) = (d[key] as? Long)?.toInt() ?: (d[key] as? Number)?.toInt() ?: default
                            fun double(key: String, default: Double = 0.0) = (d[key] as? Double) ?: (d[key] as? Number)?.toDouble() ?: default

                            ArticlesBasesStatsTable(
                                id = long("id"),
                                keyID = str("keyID"),
                                creationTimestamp = long("creationTimestamp"),
                                dernierTimeTampsSynchronisationAvecFireBase = long("dernierTimeTampsSynchronisationAvecFireBase"),
                                bsonObjectId = str("bsonObjectId"),
                                dernierFireBaseUpdateTimestamps = long("dernierFireBaseUpdateTimestamps"),
                                count_Don_Depot = int("count_Don_Depot"),
                                processPositioningInFactory = runCatching {
                                    ArticlesBasesStatsTable.ProcessPositioningInFactoryID1.valueOf(str("processPositioningInFactory", "CreeAuGeneralHandler"))
                                }.getOrDefault(ArticlesBasesStatsTable.ProcessPositioningInFactoryID1.CreeAuGeneralHandler),
                                idParentCategorie = long("idParentCategorie"),
                                positionDonSonCesFrereCategorieProduits = int("positionDonSonCesFrereCategorieProduits"),
                                nom = str("nom"),
                                nomMutable = str("nomMutable"),
                                etateActuelleOnFusionAvecBaseDonne = runCatching {
                                    ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne.valueOf(str("etateActuelleOnFusionAvecBaseDonne", "CategorieOriginaleDefinie"))
                                }.getOrDefault(ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie),
                                nombreUniteInt = int("nombreUniteInt", 1),
                                nombreProduitDonSonCarton = int("nombreProduitDonSonCarton", 1),
                                its_Carton = bool("its_Carton"),
                                cartonState = str("cartonState"),
                                heldPrioriteDemandAuGrossist = bool("heldPrioriteDemandAuGrossist"),
                                position_store_3jamale = int("position_store_3jamale"),
                                dernier_timeTamps_position_store_3jamale = long("dernier_timeTamps_position_store_3jamale"),
                                prixDefiniParGerant = double("prixDefiniParGerant"),
                                prixVent = double("prixVent"),
                                cachePrixVent = bool("cachePrixVent"),
                                pourcentage_Prix_Progressive = int("pourcentage_Prix_Progressive", 60),
                                prixAchat = double("prixAchat"),
                                prixAchatDernierTimeTempUpdate = long("prixAchatDernierTimeTempUpdate"),
                                clientPrixVentUnite = double("clientPrixVentUnite"),
                                afficheUniteAuPrint = bool("afficheUniteAuPrint"),
                                actualiseSonImage = int("actualiseSonImage"),
                                actualiseSonImageTest2 = int("actualiseSonImageTest2"),
                                afficheCesDetailPourComptBsonId = str("afficheCesDetailPourComptBsonId"),
                                disponibilityEtates = runCatching {
                                    V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.valueOf(str("disponibilityEtates", "NON_DISPO"))
                                }.getOrDefault(V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.NON_DISPO),
                                disponibilityEtates_Pour_presentaion_par_Camion = runCatching {
                                    V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.valueOf(str("disponibilityEtates_Pour_presentaion_par_Camion", "NON_DISPO"))
                                }.getOrDefault(V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.NON_DISPO),
                                keyFireBase = str("keyFireBase"),
                                nomArab = str("nomArab"),
                                autreNomDarticle = d["autreNomDarticle"] as? String,
                                couleur1 = d["couleur1"] as? String ?: "couleur1",
                                couleur2 = d["couleur2"] as? String,
                                couleur3 = d["couleur3"] as? String,
                                couleur4 = d["couleur4"] as? String,
                                couleur5 = d["couleur5"] as? String,
                                couleur6 = d["couleur6"] as? String,
                                couleur7 = d["couleur7"] as? String,
                                couleur8 = d["couleur8"] as? String,
                                couleur9 = d["couleur9"] as? String,
                                idcolor1 = long("idcolor1", 1),
                                idcolor2 = long("idcolor2"),
                                idcolor3 = long("idcolor3"),
                                idcolor4 = long("idcolor4"),
                                idcolor5 = long("idcolor5"),
                                idcolor6 = long("idcolor6"),
                                idcolor7 = long("idcolor7"),
                                idcolor8 = long("idcolor8"),
                                idcolor9 = long("idcolor9"),
                                nomCategorie2 = d["nomCategorie2"] as? String,
                                affichageUniteState = bool("affichageUniteState"),
                                commmentSeVent = d["commmentSeVent"] as? String,
                                afficheBoitSiUniter = d["afficheBoitSiUniter"] as? String,
                                minQuan = int("minQuan"),
                                monBenfice = double("monBenfice"),
                                neaon2 = str("neaon2"),
                                funChangeImagsDimention = bool("funChangeImagsDimention"),
                                nomCategorie = str("nomCategorie"),
                                neaon1 = double("neaon1"),
                                lastUpdateState = str("lastUpdateState"),
                                dateCreationCategorie = str("dateCreationCategorie"),
                                prixDeVentTotaleChezClient = double("prixDeVentTotaleChezClient"),
                                benficeTotaleEntreMoiEtClien = double("benficeTotaleEntreMoiEtClien"),
                                benificeTotaleEn2 = double("benificeTotaleEn2"),
                                monPrixAchatUniter = double("monPrixAchatUniter"),
                                monPrixVentUniter = double("monPrixVentUniter"),
                                articleHaveUniteImages = bool("articleHaveUniteImages"),
                                itsNewArrivale = bool("itsNewArrivale"),
                                imageDimention = str("imageDimention"),
                                idForSearchArticles = long("idForSearchArticles"),
                                quantite_Boit_Par_Carton = int("quantite_Boit_Par_Carton", 1),
                                setIN_Vent_Its_Quantity_Represent = runCatching {
                                    V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.valueOf(
                                        str("setIN_Vent_Its_Quantity_Represent", "quantity_Par_Boit")
                                    )
                                }.getOrDefault(V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit),
                            ).also {
                                android.util.Log.d(TAG, "  ✅ id=${doc.id} → nom='${it.nom}'")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e(TAG, "  💥 mapping THREW id=${doc.id}", e)
                            null
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
