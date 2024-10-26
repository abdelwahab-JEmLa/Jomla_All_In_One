package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel

fun countColors(article: ArticlesBasesStatsModel): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { it?.isNotEmpty() ?: false }
}

