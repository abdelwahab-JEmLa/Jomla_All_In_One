package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel

import EntreApps.Shared.Models.Home.FocusedValues_FluidApp
import EntreApps.Shared.Models.Home.RepositorysMainSetter_SeparatedApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.AppDatabase
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val grpList_cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> = emptyList(),
    val active_Central_Values: ActiveCentralValues = ActiveCentralValues.get_Default(),
    val list_M1Produit: List<M01Produit> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val initDatasProgressEtate: Float = 0f,
)

@SuppressLint("StaticFieldLeak")
class ViewModel_FragID4(
    private val context: Context,
    private val appDatabase: AppDatabase,
    val repositorysMainSetter_SeparatedApps: RepositorysMainSetter_SeparatedApps = RepositorysMainSetter_SeparatedApps(
        appDatabase
    ),
    val focusedValues_FluidApp: FocusedValues_FluidApp = FocusedValues_FluidApp()
) : ViewModel() {
    private val dao_M1Produit = appDatabase.dao_M1Produit()
    private val dao_16CategorieProduit = appDatabase.dao_16CategorieProduit()
    private val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        val productsReady = MutableStateFlow(false)
        val categoriesReady = MutableStateFlow(false)
        val colorsReady = MutableStateFlow(false)

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
                productsReady.value = true
                categoriesReady.value = true
                colorsReady.value = true
                Triple(products, categories, colors)
            }.collect { (products, categories, colors) ->
                _uiState.update {
                    it.copy(
                        list_M1Produit = products,
                        list_M16CategorieProduit = categories,
                        list_M3CouleurProduit = colors,
                        active_Central_Values = focusedValues_FluidApp.active_Central_Values
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
