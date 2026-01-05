package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Item_Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoir_Echantilliants(
    modifier: Modifier = Modifier,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit = { _, _ -> }
) {
    // TODO(1): Filter colors where depot count > 0 and parent product state is PETITE_PROBABILITY
    val list_M3couleur = remember(repositorysMainGetter.repo03CouleurProduitInfos.datasValue) {
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue.filter { couleur ->
            couleur.count_Don_Depot > 0 &&
                    repositorysMainGetter.repoM1Produit.datasValue.find {
                        it.keyID == couleur.parentBProduitInfosKeyID
                    }?.disponibilityEtates == DisponibilityEtates.PETITE_PROBABILITY
        }
    }

    // Group colors by their parent product
    val groupe_Couleur_Par_Produit = remember(list_M3couleur) {
        list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                repositorysMainGetter.repoM1Produit.datasValue.find {
                    it.keyID == productKeyID
                }?.let { product -> product to colors }
            }
            .sortedBy { (product, _) -> product.nom }
    }

    // Group products by category
    val groupe_Par_Categorie = remember(groupe_Couleur_Par_Produit) {
        groupe_Couleur_Par_Produit.groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, productColorPairs) ->
                repositorysMainGetter.repoM16CategorieProduit.datasValue.find {
                    it.id == categoryId
                }?.let { category -> category to productColorPairs }
            }
            .sortedBy { (category, _) -> category.positionDouble }
    }

    Etager_LazyColumn(
        modifier = modifier,
        categoriesWithProducts = groupe_Par_Categorie,
        on_pour_send_data = on_pour_send_data
    )
}

@Composable
fun Etager_LazyColumn(
    modifier: Modifier = Modifier,
    categoriesWithProducts: List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit
) {
    val gridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        categoriesWithProducts.forEach { (category, productColorPairs) ->
            // TODO(1): Display sticky header for each category
            item(
                key = "header_${category.id}",
                span = StaggeredGridItemSpan.FullLine
            ) {
                CategoryStickyHeader(category = category)
            }

            // TODO(1): Display each LazyStigerList_Produits
            productColorPairs.forEach { (product, colors) ->
                item(
                    key = "product_${product.keyID}",
                    span = {
                        // Check if any color is expanded
                        val isExpanded = focusedValuesGetter.active_Central_Values
                            .expanded_M3CouleurProduitInfos?.let { expandedColor ->
                                colors.any { it.keyID == expandedColor.keyID }
                            } ?: false

                        if (isExpanded) {
                            StaggeredGridItemSpan.FullLine
                        } else {
                            StaggeredGridItemSpan.SingleLane
                        }
                    }
                ) {
                    LazyStigerList_Produits(
                        product = product,
                        colors = colors,
                        on_pour_send_data = on_pour_send_data
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStickyHeader(
    category: CategoriesTabelle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = category.nom,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LazyStigerList_Produits(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit
) {
    // TODO(1): Display a lazy staggered list with StaggeredGridItemSpan.FullLine if product color is expanded, otherwise normal
    val isExpanded = focusedValuesGetter.active_Central_Values
        .expanded_M3CouleurProduitInfos?.let { expandedColor ->
            colors.any { it.keyID == expandedColor.keyID }
        } ?: false

    Item_Produit(
        relative_M1produit = product,
        on_pour_send_data = on_pour_send_data,
        modifier = modifier
    )
}
