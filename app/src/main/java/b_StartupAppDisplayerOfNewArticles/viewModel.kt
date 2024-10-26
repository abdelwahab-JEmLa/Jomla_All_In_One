package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import a_RoomDB.ArticlesSelled
import a_RoomDB.CategoriesModel
import a_RoomDB.ColorsArticles
import a_RoomDB.Objects
import a_RoomDB.Suppliers
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


// ViewModelsDataBase.kt
data class ViewModelsDataBase(
    val articlesBasesStatsModel: List<ArticlesBasesStatsModel> = emptyList(),
    val categories: List<CategoriesModel> = emptyList(),
    val colors: List<ColorsArticles> = emptyList(),
    val soldArticles: List<ArticlesSelled> = emptyList(),
    val suppliers: List<Suppliers> = emptyList(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null
)

// HeadOfViewModels.kt
class StartUpNewArticlesViewModels(
    private val database: Objects
) : ViewModel() {
    private val _uiState = MutableStateFlow(ViewModelsDataBase())
    val uiState = _uiState.asStateFlow()

    private val _currentArticle = MutableStateFlow<ArticlesBasesStatsModel?>(null)
    val currentArticle = _currentArticle.asStateFlow()

    // Ensure the directory exists when initializing the path
    val viewModelImagesPath = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/").apply {
        if (!exists()) {
            mkdirs()
        }
    }
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val refDBJetPackExport = firebaseDatabase.getReference("e_DBJetPackExport")
    private val refCategorieModel = firebaseDatabase.getReference("H_CategorieTabele")
    private val refColorsArticles = firebaseDatabase.getReference("H_ColorsArticles")

    fun updateCurrentArticle(article: ArticlesBasesStatsModel) {
        _currentArticle.value= article
    }

    fun updateLoadingProgress(progress: Float) {
        _uiState.update { it.copy(loadingProgress = progress) }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(
            isLoading = isLoading,
            loadingProgress = if (!isLoading) 0f else it.loadingProgress
        ) }
    }

    init {
        viewModelScope.launch {
            loadData()
        }

    }

    fun importFromFirebase() {
        viewModelScope.launch {
            try {
                setLoading(true)
                updateLoadingProgress(10f)

                // Import categories
                val categoriesSnapshot = refCategorieModel.get().await()
                updateLoadingProgress(40f)

                val categories = categoriesSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(CategoriesModel::class.java)
                }
                database.categoriesModelDao().insertAll(categories)
                updateLoadingProgress(70f)

                // Import colors
                val colorsSnapshot = refColorsArticles.get().await()
                updateLoadingProgress(80f)

                val colors = colorsSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(ColorsArticles::class.java)
                }
                database.colorsArticlesDao().insertAll(colors)
                updateLoadingProgress(90f)

                // Import articlesBasesStatsModel
                val articlesSnapshot = refDBJetPackExport.get().await()
                val articles = articlesSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(ArticlesBasesStatsModel::class.java)
                }
                database.articlesBasesStatsModelDao().insertAll(articles)
                updateLoadingProgress(100f)

                // Refresh UI state
                loadData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun loadData() {
        try {
            setLoading(true)
            var progress = 0f

            // Simulate or track real progress
            while (progress < 100f) {
                progress += 10f
                updateLoadingProgress(progress)
                delay(100) // Simulate work being done
            }

            // Your actual loading logic here
            val articles = database.articlesBasesStatsModelDao().getAll()
            val categories = database.categoriesModelDao().getAll()
            val colors = database.colorsArticlesDao().getAllOrdred()

            _uiState.update { it.copy(
                articlesBasesStatsModel = articles,
                categories = categories,
                colors = colors,
                ) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        } finally {
            setLoading(false)
        }
    }
}
