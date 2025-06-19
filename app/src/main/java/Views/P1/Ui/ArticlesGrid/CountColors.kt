package Views.P1.Ui.ArticlesGrid

import Views.P1.Ui.ArticlesGrid.A.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable

fun countColors(article: ArticlesBasesStatsTable): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }
}
