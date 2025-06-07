package Views.P1.Ui.ArticlesGrid.B.Main.Filter

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.DisponibilityEtates
import android.util.Log

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
                   !article.nom.contains("New")
       }
   } else {
       Log.d("Filter","Filtred $filterText")

       Log.d("Filter","Filtred ${articles.size}")
       Log.d("Filter","map ${articles.map { it.nom }}")

       articles.filter { article ->
           article.nom.contains(filterText, ignoreCase = true)
       }
   }
}
