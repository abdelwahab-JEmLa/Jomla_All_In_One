package Application2.App.Fragment.ViewModel

import Application2.App.Base.Modules.ProductDisplayController_App2
import Application2.App.Base.Modules.WifiTransferDatas_PresenterApp
import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import Application4.App.Fragment.ID1.Fragment.ProductListFilterLogic
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit.Companion.filter_passive
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import EntreApps.Shared.Modules.Base.AppDatabase
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val list_ProductWithColors: List<Pair<M01Produit, List<M3CouleurProduitInfos>>> = emptyList(),
    val active_Central_Values: ActiveCentralValues_app2 = ActiveCentralValues_app2.get_Default(),
    val list_M1Produit: List<M01Produit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M21CataloguesCategorie: List<M21CataloguesCategorie> = emptyList(),
    val filter_des_produits: Filter_Affichage_Mode_Proto? = null,
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
    private val dao_M16CategorieProduit = appDatabase.dao_16CategorieProduit()

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
        onUpdateDepotCounts = { updates ->
            viewModelScope.launch(Dispatchers.IO) {
                // FIXED: Apply depot filtering after updating counts to ensure proper UI refresh
                val updatesMap = updates.toMap()

                // Step 1: Update depot counts for all affected colors
                val updatedColors = _uiState.value.list_M3CouleurProduit.map { couleur ->
                    updatesMap[couleur.keyID]
                        ?.let { newCount ->
                            couleur.copy(count_Don_Depot = newCount)
                                .also { dao_M3CouleurProduitInfos.update(it) }
                        }
                        ?: couleur
                }
                
                val filteredColors = run {
                    val currentMode = _uiState.value.filter_des_produits
                    if (currentMode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres)
                        updatedColors
                    else
                        ProductListFilterLogic.filterByDepot(updatedColors)
                }

                // Step 3: Re-filter products to only include those with visible colors
                val filteredProducts = _uiState.value.list_M1Produit.filter_passive(
                    filteredColors.map { it.parentBProduitInfosKeyID }.distinct()
                )

                // Step 4: Update UiState with the filtered lists
                _uiState.update { state ->
                    state.copy(
                        list_M1Produit = filteredProducts,
                        list_M3CouleurProduit = filteredColors,
                        list_ProductWithColors = get_grouped_datas(filteredColors, filteredProducts),
                    )
                }

                Log.d(
                    "DepotSync",
                    "[ViewModel] DAO + UiState updated & re-filtered — ${updates.size} depot counts updated, " +
                            "resulting in ${filteredColors.size} visible colors and ${filteredProducts.size} visible products"
                )
            }
        },
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

    init {
        val productsReady    = MutableStateFlow(false)
        val colorsReady      = MutableStateFlow(false)
        val categoriesReady  = MutableStateFlow(false)
        val cataloguesReady  = MutableStateFlow(false)

        viewModelScope.launch(Dispatchers.IO) {
            combine(productsReady, colorsReady, categoriesReady, cataloguesReady) { p, col, cat, cata ->
                listOf(p, col, cat, cata).count { it } / 4f
            }.collect { progress ->
                _uiState.update { it.copy(initDatasProgressEtate = progress) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val categories = dao_M16CategorieProduit.getAll()
            categoriesReady.value = true

            val catalogues = get_ListM21CataloguesCategorie()
            cataloguesReady.value = true
            val currentMode = _uiState.value.filter_des_produits
            val filteredColors = dao_M3CouleurProduitInfos.getAll().let { allColors ->
                if (currentMode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres)
                    allColors
                else
                    ProductListFilterLogic.filterByDepot(allColors)
            }
            val products = dao_M1Produit.getAll()
                .filter_passive(filteredColors.map { it.parentBProduitInfosKeyID }.distinct())

            productsReady.value = true
            colorsReady.value = true

            _uiState.update {
                it.copy(
                    list_M1Produit = products,
                    list_M3CouleurProduit = filteredColors,
                    list_M16CategorieProduit = categories,
                    list_M21CataloguesCategorie = catalogues,
                    list_ProductWithColors = get_grouped_datas(filteredColors, products),
                )
            }
            wifi.list_M1Produit = products
            wifi.list_M3CouleurProduit = filteredColors
        }

        viewModelScope.launch {
            wifi.state
                .distinctUntilChangedBy { it.filter_Affichage_Mode_Proto }
                .collect { wifiState ->
                    _uiState.update { it.copy(filter_des_produits = wifiState.filter_Affichage_Mode_Proto) }
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

