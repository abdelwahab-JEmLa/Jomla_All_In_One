package Views.P1._ArticlesStartFacade.B.View.D.Filter

import Views.P1._ArticlesStartFacade.A.ViewModel.Modules.ArticlePagingSource
import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
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
