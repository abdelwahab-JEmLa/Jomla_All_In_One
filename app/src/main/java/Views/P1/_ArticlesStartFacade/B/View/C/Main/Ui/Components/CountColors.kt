package Views.P1._ArticlesStartFacade.B.View.C.Main.Ui.Components

import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable

fun countColors(article: ArticlesBasesStatsTable): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }
}
