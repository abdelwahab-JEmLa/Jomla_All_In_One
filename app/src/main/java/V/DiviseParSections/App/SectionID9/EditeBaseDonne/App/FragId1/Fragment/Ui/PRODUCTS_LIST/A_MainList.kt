package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.ProductItem
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.A_ProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditeInfosMainList(
    modifier: Modifier = Modifier,
    filteredAndSortedProduitList: List<ArticlesBasesStatsTable>,
    aProduitdatabasecomposerepositorypj17: A_ProduitDataBaseComposeRepositoryPJ17,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = filteredAndSortedProduitList,
            key = { it.id }
        ) { produit ->
            ProductItem(
                mainComposRepository=aProduitdatabasecomposerepositorypj17,
                produit = produit
            )
        }
    }
}
