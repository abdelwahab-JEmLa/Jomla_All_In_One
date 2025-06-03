package Views.P1.Ui.ArticlesGrid.B.Main.Filter

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.DisponibilityEtates

fun filterArticles(
    articles: List<ArticlesBasesStatsTable>,
    filterText: String,
): List<ArticlesBasesStatsTable> {
   return if (filterText.isEmpty()) {
       articles.filter { article ->
           article.disponibilityEtates != DisponibilityEtates.NON_DISPO &&
                   article.idParentCategorie != 0L &&
                   article.idParentCategorie != null &&
                   article.idForSearchArticles <= 0 &&
                   !article.nomArticleFinale.contains("New")
       }
   } else {
       articles.filter { article ->
           article.nomArticleFinale.contains(filterText, ignoreCase = true) ||
                   article.idForSearchArticles > 0
       }
   }
}
