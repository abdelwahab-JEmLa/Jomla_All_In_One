package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Modules

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter.filterArticles
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.clientjetpack.ViewModel.UiState
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext

class ArticlePagingSource(
    val articles: List<ArticlesBasesStatsTable>,
    val filterText: String,
    val currentClient: HClientInfos?,
    private val uiState: UiState,
    val a_ProduitRepository: A_ProduitRepository = GlobalContext.get().get()
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

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTable> {
        val page = params.key ?: 0

        // Wait until progressRepo reaches 1.0 (data fully loaded)
        val progress = a_ProduitRepository.progressRepo.first()
        if (progress < 1.0f) {
            // Return empty page while waiting for data to load
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

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
