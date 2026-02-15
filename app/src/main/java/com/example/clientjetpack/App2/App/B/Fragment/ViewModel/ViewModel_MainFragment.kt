package com.example.clientjetpack.App2.App.B.Fragment.ViewModel

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val list_grouped_datas: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>  = emptyList(),
    val list_M1Produit: List<ArticlesBasesStatsTable> = emptyList(),
    val list_M16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val initDatasProgressEtate: Float = 0f,
)

@SuppressLint("StaticFieldLeak")
class ViewModel_MainFragment(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    private val dao_M1Produit = appDatabase.dao_M1Produit()
    private val dao_16CategorieProduit = appDatabase.dao_16CategorieProduit()
    private val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dao_M1Produit.getAllFlow(),
                dao_16CategorieProduit.getAllFlow(),
                dao_M3CouleurProduitInfos.getAllFlow()
            ) { products, categories, colors ->
                Triple(products, categories, colors)
            }.collect { (products, categories, colors) ->
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
            }
        }
    }

    fun get_grouped_datas(
        allColors: List<M3CouleurProduitInfos>,
        allProducts: List<ArticlesBasesStatsTable>,
        allCategories: List<M16CategorieProduit>
    ): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>> {

        // Get all available catalogues
        val allCatalogues = get_ListM21CataloguesCategorie()

        // Step 1: Group colors by product
        val productColorPairs = allColors
            .groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                allProducts.find { it.keyID == productKeyID }?.let { it to colors }
            }
            .sortedBy { (product, _) -> product.nom }

        // Step 2: Group products by category
        val categoryProductPairs = productColorPairs
            .groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, pairs) ->
                allCategories.find { it.id == categoryId }?.let { category ->
                    category to pairs
                }
            }
            .sortedBy { (category, _) -> category.positionDouble }

        // Step 3: Group categories by catalogue
        return allCatalogues
            .sortedBy { it.position }
            .mapNotNull { catalogue ->
                val categoriesInCatalogue = categoryProductPairs
                    .filter { (category, _) ->
                        category.catalogueParentId == catalogue.id
                    }
                    .sortedBy { (category, _) -> category.positionDouble }

                if (categoriesInCatalogue.isNotEmpty()) {
                    catalogue to categoriesInCatalogue
                } else {
                    null
                }
            }
    }
}
