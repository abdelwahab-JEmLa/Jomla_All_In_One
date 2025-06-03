package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.categorieSection(
    groupedProducts: Map<Long, List<ArticlesBasesStatsTable>>,
    availableCategories: List<Long>,
    onProductCategoryChanged: (ArticlesBasesStatsTable) -> Unit,
    categoryMap: Map<Long, CategoriesTabelle> = emptyMap(),
    onHeldPourDeplacement: (Long, Boolean) -> Unit,
    onClickPourChangeDeplaceApre: (Long, Boolean) -> Unit,
    onAddCategory: ((String) -> Unit)? = null,
    onUpdateCategory: ((Long, String) -> Unit)? = null
) {
    groupedProducts.forEach { (id, products) ->
        item(key = "header_$id") {
            E_StickyHeader(id, categoryMap[id],
                { onHeldPourDeplacement(id ?: 0L, it) },
                { onClickPourChangeDeplaceApre(id ?: 0L, it) })
        }
        item(key = "products_$id") {
            LazyRow(contentPadding = PaddingValues(12.dp, 8.dp)) {
                items(products, key = { "product_${it.id}" }) { produit ->
                    MainItemEditeCategories(
                        produit, availableCategories,
                        onCategoryChanged = onProductCategoryChanged,
                        Modifier.size(120.dp), categoryMap, onAddCategory, onUpdateCategory
                    )
                }
            }
        }
    }
}
