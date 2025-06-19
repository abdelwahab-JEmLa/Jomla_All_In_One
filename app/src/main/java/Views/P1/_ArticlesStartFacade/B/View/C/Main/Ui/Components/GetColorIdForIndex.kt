package Views.P1._ArticlesStartFacade.B.View.C.Main.Ui.Components

import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable

fun ArticlesBasesStatsTable.getColorIdForIndex(index: Int): Long? {
    return when (index) {
        0 -> idcolor1.takeIf { it != 0L }
        1 -> idcolor2.takeIf { it != 0L }
        2 -> idcolor3.takeIf { it != 0L }
        3 -> idcolor4.takeIf { it != 0L }
        else -> null
    }
}
