package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Modules.ArticlePagingSource
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.Model.A_Produit.A_Produit

fun ArticlePagingSource.filterArticles(): List<ArticlesBasesStatsTable> {
        return if (filterText.isEmpty()) {
            articles.filter { article ->
                // Find the corresponding product model
                val productModel = a_ProduitRepository.modelDatas
                    .find { it.id == article.id }


                // Check if the product is completely unavailable for all clientAchteurs
                val isProductUnavailableForAll = productModel?.enumVarNonDispoPourClients ==
                        A_Produit.NON_DISPO_POUR_CLIENTS.TOUT

                // Check if the product is unavailable specifically for temporary clientAchteurs
                val isProductUnavailableForTemporary = productModel?.enumVarNonDispoPourClients ==
                        A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU

                // Common filtering conditions
                article.disponibilityEtates!= DisponibilityEtates.NON_DISPO &&
                article.idParentCategorie!=0L  &&
                article.idParentCategorie!=null  &&
                !isProductUnavailableForAll &&
                        article.idForSearchArticles <= 0 &&
                        !article.nom.contains("New")
            }
        } else {
            // Filtering for search text
            articles.filter { article ->
                article.nom.contains(filterText, ignoreCase = true) ||
                        article.idForSearchArticles > 0
            }
        }
    }
