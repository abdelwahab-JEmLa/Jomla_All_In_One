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
    val grpList_cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> = emptyList(),
    val active_Central_Values: ActiveCentralValues_app2 = ActiveCentralValues_app2.get_Default(),
    val list_M1Produit: List<M01Produit> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val initDatasProgressEtate: Float = 0f,
)

@SuppressLint("StaticFieldLeak")
class ViewModel_MainFragment(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val repositorysMainGetter_app2: RepositorysMainGetter_app2,
) : ViewModel() {

    private val dao_M1Produit = appDatabase.dao_M1Produit()
    private val dao_16CategorieProduit = appDatabase.dao_16CategorieProduit()
    private val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private fun getActiveCentralValues() = _uiState.value.active_Central_Values

    fun updateActiveCentralValues(updated: ActiveCentralValues_app2) {
        _uiState.update { it.copy(active_Central_Values = updated) }
        repositorysMainGetter_app2.update_ActiveCentralValues_app2(updated)
    }

    val wifi = WifiTransferDatas_app2(
        context = context,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
        onGetActiveCentralValues = ::getActiveCentralValues,
        onUpdateActiveCentralValues = ::updateActiveCentralValues,
    )

    val wifiState =
        wifi.state.stateIn(viewModelScope, SharingStarted.Eagerly, ProductDisplayController())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        wifi.startAsClient(); wifi.updateTypePhone(isHost = false)
    }

    fun disconnect() = wifi.disconnect()

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) =
        wifi.sendOrderToClientDisplayer(orderName, data)

    fun sendOrderToClientDisplayerT(order: WifiUpdateClientDisplayerStats_app2, data: Any? = null) = wifi.sendOrderToClientDisplayerT(order, data)

    init {
        // Track how many of the 3 sources have emitted at least once, to drive progress (0f → 1f).
        val productsReady   = MutableStateFlow(false)
        val categoriesReady = MutableStateFlow(false)
        val colorsReady     = MutableStateFlow(false)

        // Update progress whenever the ready-flags change.
        viewModelScope.launch(Dispatchers.IO) {
            combine(productsReady, categoriesReady, colorsReady) { p, c, col ->
                listOf(p, c, col).count { it } / 3f
            }.collect { progress ->
                _uiState.update { it.copy(initDatasProgressEtate = progress) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao_M1Produit.getAllFlow(),
                dao_16CategorieProduit.getAllFlow(),
                dao_M3CouleurProduitInfos.getAllFlow()
            ) { products, categories, colors ->
                productsReady.value   = true
                categoriesReady.value = true
                colorsReady.value     = true
                Triple(products, categories, colors)
            }.collect { (products, categories, colors) ->
                _uiState.update {
                    it.copy(
                        list_M1Produit = products,
                        list_M16CategorieProduit = categories,
                        list_M3CouleurProduit = colors,
                        grpList_cataloguesWithCategoriesAndProducts = get_grouped_datas(
                            colors,
                            products,
                            categories
                        ),
                    )
                }
                wifi.list_M1Produit = products
                wifi.list_M3CouleurProduit = colors
            }
        }
    }

    fun get_grouped_datas(
        allColors: List<M3CouleurProduitInfos>,
        allProducts: List<M01Produit>,
        allCategories: List<M16CategorieProduit>,
    ): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> {
        val productColorPairs = allColors
            .groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (id, colors) ->
                allProducts.find { it.keyID == id }?.let { it to colors }
            }
            .sortedBy { (p, _) -> p.nom }

        val categoryProductPairs = productColorPairs
            .groupBy { (p, _) -> p.idParentCategorie }
            .mapNotNull { (id, pairs) -> allCategories.find { it.id == id }?.let { it to pairs } }
            .sortedBy { (c, _) -> c.positionDouble }

        return get_ListM21CataloguesCategorie().sortedBy { it.position }.mapNotNull { catalogue ->
            val cats = categoryProductPairs
                .filter { (c, _) -> c.catalogueParentId == catalogue.id }
                .sortedBy { (c, _) -> c.positionDouble }
            if (cats.isNotEmpty()) catalogue to cats else null
        }
    }

    override fun onCleared() {
        super.onCleared(); wifi.cancel()
    }
}
