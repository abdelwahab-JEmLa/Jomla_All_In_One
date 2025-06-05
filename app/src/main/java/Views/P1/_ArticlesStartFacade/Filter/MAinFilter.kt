package Views.P1._ArticlesStartFacade.Filter

import Views.P1._ArticlesStartFacade.ArticlePagingSource
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.DisponibilityEtates
import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.B_ClientsDataBase

fun ArticlePagingSource.filterArticles(): List<ArticlesBasesStatsTable> {
        return if (filterText.isEmpty()) {
            articles.filter { article ->
                // Find the corresponding product model
                val productModel = a_ProduitRepository.modelDatas
                    .find { it.id == article.id }

                val isTemporaryClient = currentClient?.etatesMutable?.clientTypeMode ==
                        B_ClientsDataBase.EtatesMutable.ClientTypeMode.NEVEAU

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
                        !(isTemporaryClient && isProductUnavailableForTemporary) &&
                        article.idForSearchArticles <= 0 &&
                        !article.nomArticleFinale.contains("New")
            }
        } else {
            // Filtering for search text
            articles.filter { article ->
                article.nomArticleFinale.contains(filterText, ignoreCase = true) ||
                        article.idForSearchArticles > 0
            }
        }
    }
