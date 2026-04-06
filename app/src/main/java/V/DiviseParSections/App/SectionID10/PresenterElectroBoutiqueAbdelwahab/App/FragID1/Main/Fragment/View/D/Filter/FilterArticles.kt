package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates

fun filterArticles(
    articles: List<M01Produit>,
    filterText: String,
    aCentralFacade: ACentralFacade,
): List<M01Produit> {
    val mode_edite_dispo =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt?.mode_edite_dispo

    // If mode_edite_dispo is true, return all articles without filtering
    if (mode_edite_dispo == true) {
        return articles
    }

    val its_mode_affiche_que_produits_au_depot =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .currentActive_M9AppCompt?.its_mode_affiche_que_produits_au_depot

    val repo03CouleurProduitInfosDatasValue =
        aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.datasValue

    return if (filterText.isEmpty()) {
        articles.filter { article ->
            // Base filtering conditions
            val baseCondition = article.disponibilityEtates != DisponibilityEtates.NON_DISPO &&
                    article.idParentCategorie != 0L &&
                    article.idForSearchArticles <= 0 &&
                    !article.nom.contains("New")

            // If depot mode is active, also check if product has colors with depot count > 0
            if (its_mode_affiche_que_produits_au_depot == true && baseCondition) {
                val hasColorsInDepot = repo03CouleurProduitInfosDatasValue.any { couleur ->
                    couleur.parentBProduitOldID == article.id &&
                            couleur.count_Don_Depot > 0
                }
                hasColorsInDepot
            } else {
                baseCondition
            }
        }
    } else {
        articles.filter { article ->
            article.nom.contains(filterText, ignoreCase = true)
        }
    }
}
