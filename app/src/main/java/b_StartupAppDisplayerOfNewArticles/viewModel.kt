package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import a_RoomDB.ArticlesSelled
import a_RoomDB.CategoriesModel
import a_RoomDB.ColorsArticles
import a_RoomDB.Objects
import a_RoomDB.Suppliers
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// HeadOfViewModels.kt
data class ViewModelsDataBase(
    val articles: List<ArticlesBasesStatsModel> = emptyList(),
    val categories: List<CategoriesModel> = emptyList(),
    val colors: List<ColorsArticles> = emptyList(),
    val soldArticles: List<ArticlesSelled> = emptyList(),
    val suppliers: List<Suppliers> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HeadOfViewModels(
    private val database: Objects
) : ViewModel() {
    private val _uiState = MutableStateFlow(ViewModelsDataBase())
    val uiState = _uiState.asStateFlow()

    private val _currentArticle = MutableStateFlow<ArticlesBasesStatsModel?>(null)
    val currentArticle = _currentArticle.asStateFlow()

    init {
        viewModelScope.launch {
            loadInitialData()
        }
    }

    private suspend fun loadInitialData() {
        try {
            _uiState.update { it.copy(isLoading = true) }
            val articles = database.articlesBasesStatsModelDao().getAll()
            val categories = database.categoriesModelDao().getAll()

            _uiState.update { it.copy(
                articles = articles,
                categories = categories,
                isLoading = false
            ) }
        } catch (e: Exception) {
            _uiState.update { it.copy(
                error = e.message,
                isLoading = false
            ) }
        }
    }
}