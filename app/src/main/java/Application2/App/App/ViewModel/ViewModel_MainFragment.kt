package Application2.App.App.ViewModel

import Application2.App.Base.Modules.ProductDisplayController_App2
import Application2.App.Base.Modules.WifiTransferDatas_PresenterApp
import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
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
    val list_ProductWithColors: List<Pair<M01Produit, List<M3CouleurProduitInfos>>> = emptyList(),
    val active_Central_Values: ActiveCentralValues_app2 = ActiveCentralValues_app2.get_Default(),
    val list_M1Produit: List<M01Produit> = emptyList(),
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
    private val dao_M3CouleurProduitInfos = appDatabase.dao_M03CouleurProduitInfos()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private fun getActiveCentralValues() = _uiState.value.active_Central_Values

    fun updateActiveCentralValues(updated: ActiveCentralValues_app2) {
        _uiState.update { it.copy(active_Central_Values = updated) }
        repositorysMainGetter_app2.update_ActiveCentralValues_app2(updated)
    }

    val wifi = WifiTransferDatas_PresenterApp(
        context = context,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
        onGetActiveCentralValues = ::getActiveCentralValues,
        onUpdateActiveCentralValues = ::updateActiveCentralValues,
    )

    val wifiState =
        wifi.state.stateIn(viewModelScope, SharingStarted.Eagerly, ProductDisplayController_App2())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        wifi.startAsClient(); wifi.updateTypePhone(isHost = false)
    }

    fun disconnect() = wifi.disconnect()

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) =
        wifi.sendOrderToClientDisplayer(orderName, data)

    fun sendOrderToClientDisplayerT(order: WifiUpdateClientDisplayerStats, data: Any? = null) =
        wifi.sendOrderToClientDisplayerT(order, data)

    init {
        val productsReady = MutableStateFlow(false)
        val colorsReady = MutableStateFlow(false)

        viewModelScope.launch(Dispatchers.IO) {
            combine(productsReady, colorsReady) { p, col ->
                listOf(p, col).count { it } / 2f
            }.collect { progress ->
                _uiState.update { it.copy(initDatasProgressEtate = progress) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao_M1Produit.getAllFlow(),
                dao_M3CouleurProduitInfos.getAllFlow()
            ) { products, colors ->
                productsReady.value = true
                colorsReady.value = true
                products to colors
            }.collect { (products, colors) ->
                _uiState.update {
                    it.copy(
                        list_M1Produit = products,
                        list_M3CouleurProduit = colors,
                        list_ProductWithColors = get_grouped_datas(colors, products),
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
    ): List<Pair<M01Produit, List<M3CouleurProduitInfos>>> {
        val colorsByProductId = allColors.groupBy { it.parentBProduitInfosKeyID }
        return allProducts
            .sortedBy { it.nom }
            .map { product -> product to (colorsByProductId[product.keyID] ?: emptyList()) }
    }

    override fun onCleared() {
        super.onCleared(); wifi.cancel()
    }
}
