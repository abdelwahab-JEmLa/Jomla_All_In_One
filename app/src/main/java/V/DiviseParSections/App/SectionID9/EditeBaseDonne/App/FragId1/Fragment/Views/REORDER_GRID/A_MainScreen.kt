package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.REORDER_GRID

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReorderMultiCategories(
    modifier: Modifier = Modifier,
    categoriesList: List<C_CategorieProduitInfos> = emptyList(),
    onCategoriesReordered: (List<C_CategorieProduitInfos>) -> Unit = {},
    produitList: List<A_ProduitInfosProtoJuin3> = emptyList()
) {
    MainList(categoriesList, produitList, modifier, onCategoriesReordered)
}

fun moveSelectedCategories(
    categories: List<C_CategorieProduitInfos>,
    selectedIds: Set<Long>,
    targetId: Long,
    moveBefore: Boolean
): List<C_CategorieProduitInfos> {
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
