package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReorderMultiCategories(
    modifier: Modifier = Modifier,
    categoriesList: List<CategoriesTabelle> = emptyList(),
    onCategoriesReordered: (List<CategoriesTabelle>) -> Unit = {},
    produitList: List<ArticlesBasesStatsTable> = emptyList(),
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel
) {
    MainList(categoriesList, produitList, modifier, onCategoriesReordered, viewModel)
}

fun moveSelectedCategories(
    categories: List<CategoriesTabelle>,
    selectedIds: Set<Long>,
    targetId: Long,
    moveBefore: Boolean
): List<CategoriesTabelle> {
    if (selectedIds.isEmpty() || selectedIds.contains(targetId)) return categories

    val selected = categories.filter { selectedIds.contains(it.id) }
    val remaining = categories.filter { !selectedIds.contains(it.id) }

    val targetIndex = remaining.indexOfFirst { it.id == targetId }

    if (targetIndex == -1) return categories

    val insertIndex = if (moveBefore) targetIndex else targetIndex + 1
    val newList = remaining.toMutableList()

    selected.forEachIndexed { i, cat -> newList.add(insertIndex + i, cat) }

    return newList.mapIndexed { i, cat -> cat.copy(position = i + 1) }
}
