package Views.P1.Ui.ArticlesGrid

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable

fun ArticlesBasesStatsTable.getColorIdForIndex(index: Int): Long? {
    return when (index) {
        0 -> idcolor1.takeIf { it != 0L }
        1 -> idcolor2.takeIf { it != 0L }
        2 -> idcolor3.takeIf { it != 0L }
        3 -> idcolor4.takeIf { it != 0L }
        else -> null
    }
}
