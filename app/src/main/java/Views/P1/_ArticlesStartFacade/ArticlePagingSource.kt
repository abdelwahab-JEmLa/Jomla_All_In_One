package Views.P1._ArticlesStartFacade

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.clientjetpack.Models.UiState
import org.koin.core.context.GlobalContext


class ArticlePagingSource(
    private val articles: List<ArticlesBasesStatsTable>,
    private val filterText: String,
    private val currentClient: B_ClientsDataBase?,
    private val uiState: UiState,
    private val a_ProduitRepository: A_ProduitRepository = GlobalContext.get().get()
) : PagingSource<Int, ArticlesBasesStatsTable>() {
    private val pageSize = 10
    private val cachedFilteredArticles = mutableMapOf<Int, List<ArticlesBasesStatsTable>>()


    override fun getRefreshKey(state: PagingState<Int, ArticlesBasesStatsTable>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { anchorPage ->
                anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
            }
        }
    }

    private fun filterArticles(): List<ArticlesBasesStatsTable> {
        return if (filterText.isEmpty()) {
            articles.filter { article ->
                // Find the corresponding product model
                val productModel = a_ProduitRepository.modelDatas
                    .find { it.id.toInt() == article.idArticle }

                val isTemporaryClient = currentClient?.etatesMutable?.clientTypeMode ==
                        B_ClientsDataBase.EtatesMutable.ClientTypeMode.NEVEAU

                // Check if the product is completely unavailable for all clients
                val isProductUnavailableForAll = productModel?.enumVarNonDispoPourClients ==
                        A_Produit.NON_DISPO_POUR_CLIENTS.TOUT

                // Check if the product is unavailable specifically for temporary clients
                val isProductUnavailableForTemporary = productModel?.enumVarNonDispoPourClients ==
                        A_Produit.NON_DISPO_POUR_CLIENTS.NEVEAU

                // Common filtering conditions
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

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTable> {
        val page = params.key ?: 0

        return try {
            val filteredArticles = cachedFilteredArticles.getOrPut(page) {
                filterArticles()
                    .drop(page * pageSize)
                    .take(pageSize)
            }

            LoadResult.Page(
                data = filteredArticles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (filteredArticles.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } finally {
            // Clean up cache to prevent memory leaks
            cleanupCache(page)
        }
    }

    private fun cleanupCache(currentPage: Int) {
        cachedFilteredArticles.keys
            .filter { it < currentPage - 1 || it > currentPage + 1 }
            .forEach { cachedFilteredArticles.remove(it) }
    }
}
