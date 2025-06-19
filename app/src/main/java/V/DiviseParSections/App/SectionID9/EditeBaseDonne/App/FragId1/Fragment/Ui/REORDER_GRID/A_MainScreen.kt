package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReorderMultiCategories(
    modifier: Modifier = Modifier,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    produitList: List<ArticlesBasesStatsTable> = emptyList()
) {
    MainList(modifier, viewModel, produitList)
}
