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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.ProductDisplayController
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiTransferDatas_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiUpdateClientDisplayerStats_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

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
    private val repositorysMainGetter_app2: RepositorysMainGetter_app2,
) : ViewModel() {
    val wifi = WifiTransferDatas_app2(
        context = context, repositorysMainGetter_app2 = repositorysMainGetter_app2,
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

    val uiState by derivedStateOf {
        val products = repositorysMainGetter_app2.datasValue_M1Produit
        val categories = repositorysMainGetter_app2.datasValue_M16CategorieProduit
        val colors = repositorysMainGetter_app2.datasValue_M3CouleurProduitInfos
        val ready = products.isNotEmpty() && categories.isNotEmpty() && colors.isNotEmpty()
        UiState(
            list_M1Produit = products,
            list_M16CategorieProduit = categories,
            list_M3CouleurProduit = colors,
            list_grouped_datas = if (ready) get_grouped_datas(
                colors,
                products,
                categories
            ) else emptyList(),
            initDatasProgressEtate = if (ready) 1f else repositorysMainGetter_app2.active_Central_Values.mainInitDataBaseProgressEtate,
        ).also { state ->
            if (ready) {
                wifi.list_M1Produit = state.list_M1Produit
                wifi.list_M3CouleurProduit = state.list_M3CouleurProduit
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
