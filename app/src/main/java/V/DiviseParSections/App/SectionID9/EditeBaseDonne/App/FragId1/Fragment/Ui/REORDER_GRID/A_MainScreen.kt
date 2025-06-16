package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import Z_CodePartageEntreApps.Repository.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
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
