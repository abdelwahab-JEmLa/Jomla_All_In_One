package Application2.App.App.ViewModel

import Application2.App.Base.Modules.ProductDisplayController
import Application2.App.Base.Modules.WifiTransferDatas_app2
import Application2.App.Base.Modules.WifiUpdateClientDisplayerStats_app2
import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.AppDatabase
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val grpList_cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>  = emptyList(),
    val list_M1Produit: List<M01Produit> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val active_Central_Values: ActiveCentralValues_app2 = ActiveCentralValues_app2.get_Default(),
    val initDatasProgressEtate: Float = 0f,
)

@SuppressLint("StaticFieldLeak")
class ViewModel_MainFragment(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val repositorysMainGetter_app2: RepositorysMainGetter_app2,
) : ViewModel() {

    private val dao_M1Produit         = appDatabase.dao_M1Produit()
    private val dao_16CategorieProduit = appDatabase.dao_16CategorieProduit()
    private val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    // -----------------------------------------------------------------------
    // active_Central_Values — single source of truth in UiState
    // -----------------------------------------------------------------------

    /** Read the current value — passed as lambda into WifiTransferDatas_app2. */
    private fun getActiveCentralValues(): ActiveCentralValues_app2 =
        _uiState.value.active_Central_Values

    /** Write an updated value — passed as lambda into WifiTransferDatas_app2. */
    fun updateActiveCentralValues(updated: ActiveCentralValues_app2) {
        _uiState.update { it.copy(active_Central_Values = updated) }
        // Keep the legacy repo in sync so other parts of the app still work
        repositorysMainGetter_app2.update_ActiveCentralValues_app2(updated)
    }

    // -----------------------------------------------------------------------
    // Wifi — no repo dependency, wired via lambdas
    // -----------------------------------------------------------------------

    val wifi = WifiTransferDatas_app2(
        context = context,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
        onGetActiveCentralValues = ::getActiveCentralValues,
        onUpdateActiveCentralValues = ::updateActiveCentralValues,
    )

    val wifiState = wifi.state
        .stateIn(viewModelScope, SharingStarted.Eagerly, ProductDisplayController())

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
    // Init progress — called by Initializer_Funcs_app2
    // -----------------------------------------------------------------------

    /**
     * Called by Initializer_Funcs_app2 to report seeding progress.
     * When progress reaches 1f, triggers the first full DB→UI sync.
     */
    fun updateInitProgress(progress: Float) {
        _uiState.update { it.copy(initDatasProgressEtate = progress) }
        if (progress >= 1f) {
            viewModelScope.launch(Dispatchers.IO) {
                val products   = dao_M1Produit.getAll()
                val categories = dao_16CategorieProduit.getAll()
                val colors     = dao_M3CouleurProduitInfos.getAll()
                _uiState.update {
                    it.copy(
                        list_M1Produit           = products,
                        list_M16CategorieProduit  = categories,
                        list_M3CouleurProduit     = colors,
                        grpList_cataloguesWithCategoriesAndProducts = get_grouped_datas(
                            allColors     = colors,
                            allProducts   = products,
                            allCategories = categories,
                        ),
                        active_Central_Values = repositorysMainGetter_app2.active_Central_Values,
                        initDatasProgressEtate = 1f,
                    )
                }
                wifi.list_M1Produit = products
                wifi.list_M3CouleurProduit = colors
            }
        }
    }

    // -----------------------------------------------------------------------
    // DB flow — only runs after loading is complete
    // -----------------------------------------------------------------------

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao_M1Produit.getAllFlow(),
                dao_16CategorieProduit.getAllFlow(),
                dao_M3CouleurProduitInfos.getAllFlow()
            ) { products, categories, colors ->
                Triple(products, categories, colors)
            }.collect { (products, categories, colors) ->
                if (_uiState.value.initDatasProgressEtate < 1f) return@collect

                _uiState.update {
                    it.copy(
                        list_M1Produit          = products,
                        list_M16CategorieProduit = categories,
                        list_M3CouleurProduit    = colors,
                        grpList_cataloguesWithCategoriesAndProducts = get_grouped_datas(
                            allColors     = colors,
                            allProducts   = products,
                            allCategories = categories,
                        ),
                    )
                }
                wifi.list_M1Produit = products
                wifi.list_M3CouleurProduit = colors
            }
        }
    }

    // -----------------------------------------------------------------------
    // Grouping
    // -----------------------------------------------------------------------

    fun get_grouped_datas(
        allColors: List<M3CouleurProduitInfos>,
        allProducts: List<M01Produit>,
        allCategories: List<M16CategorieProduit>
    ): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> {

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
