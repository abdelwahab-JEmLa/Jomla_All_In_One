package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
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
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    aProduitdatabasecomposerepositorypj17: A_ProduitDataBaseComposeRepositoryPJ17,
    onPrixUpdate: (ArticlesBasesStatsTable) -> Unit = {}
) {
    val produitList = aProduitdatabasecomposerepositorypj17.datasValue
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = produitList,
            key = { it.id }
        ) { produit ->
            ProductItem(
                aProduitdatabasecomposerepositorypj17=aProduitdatabasecomposerepositorypj17,
                viewModel=viewModel,
                produit = produit
            )
        }
    }
}
