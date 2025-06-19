package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
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
