package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.REORDER_GRID

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReorderMultiCategories(
    modifier: Modifier = Modifier,
    categoriesList: List<CategoriesTabelle> = emptyList(),
    onCategoriesReordered: (List<CategoriesTabelle>) -> Unit = {},
    produitList: List<ArticlesBasesStatsTable> = emptyList()
) {
    MainList(categoriesList, produitList, modifier, onCategoriesReordered)
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
