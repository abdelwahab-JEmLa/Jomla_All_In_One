package com.example.clientjetpack.App2.App.A.Main.App.ViewModel

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.ProductDisplayController
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiTransferDatas_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiUpdateClientDisplayerStats_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.Initializer_Funcs_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val list_grouped_datas: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>> = emptyList(),
    val list_M1Produit: List<ArticlesBasesStatsTable> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val initDatasProgressEtate: Float = 0f,
)

@SuppressLint("StaticFieldLeak")
class ViewModel_MainFragment(
    private val context: Context,
    private val focusedValuesGetter_app2: FocusedValuesGetter_app2,
    private val repositorysMainGetter_app2: RepositorysMainGetter_app2,
) : ViewModel() {

    // ── Wifi ──────────────────────────────────────────────────────────────────
    val wifi = WifiTransferDatas_app2(
        context = context,
        focusedValuesGetter_app2 = focusedValuesGetter_app2,
        coroutineScope = viewModelScope,
        list_M1Produit = repositorysMainGetter_app2.datasValue_M1Produit,
        list_M3CouleurProduit = repositorysMainGetter_app2.datasValue_M3CouleurProduitInfos,
    )

    val wifiState = wifi.state
        .stateIn(viewModelScope, SharingStarted.Eagerly, ProductDisplayController())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() {
        wifi.startAsHost(); wifi.updateTypePhone(isHost = true)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        wifi.startAsClient(); wifi.updateTypePhone(isHost = false)
    }

    fun disconnect() = wifi.disconnect()

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) =
        wifi.sendOrderToClientDisplayer(orderName, data)

    fun sendOrderToClientDisplayerT(order: WifiUpdateClientDisplayerStats_app2, data: Any? = null) =
        wifi.sendOrderToClientDisplayerT(order, data)

    // ── UI state ──────────────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    // ── Seeding is fully owned by Initializer_Funcs_app2 ─────────────────────
    @Suppress("unused")
    private val initializer = Initializer_Funcs_app2(
        context = context,
        focusedValuesGetter_app2 = focusedValuesGetter_app2,
        repositorysMainGetter_app2.dao_M1Produit,
        repositorysMainGetter_app2.dao_16CategorieProduit,
        repositorysMainGetter_app2.dao_M3CouleurProduitInfos
    )

    // ── Observe repo state — react when seeding completes ────────────────────
    init {
        viewModelScope.launch(Dispatchers.Main) {
            combine(
                snapshotFlow { repositorysMainGetter_app2.datasValue_M1Produit },
                snapshotFlow { repositorysMainGetter_app2.datasValue_M16CategorieProduit },
                snapshotFlow { repositorysMainGetter_app2.datasValue_M3CouleurProduitInfos }
            ) { products, categories, colors ->
                Triple(products, categories, colors)
            }.collect { (products, categories, colors) ->
                if (products.isEmpty() || categories.isEmpty() || colors.isEmpty()) return@collect

                _uiState.update {
                    it.copy(
                        list_M1Produit = products,
                        list_M16CategorieProduit = categories,
                        list_M3CouleurProduit = colors,
                        list_grouped_datas = get_grouped_datas(
                            allColors = colors,
                            allProducts = products,
                            allCategories = categories
                        ),
                        initDatasProgressEtate = 1f,
                    )
                }
                wifi.list_M1Produit = products
                wifi.list_M3CouleurProduit = colors
            }
        }
    }

    // ── Grouping ──────────────────────────────────────────────────────────────
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
