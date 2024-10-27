package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStats
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


// UiState.kt
data class UiState(
    val articlesBasesStats: List<ArticlesBasesStats> = emptyList(),
    val categories: List<CategoriesModel> = emptyList(),
    val colorsArticlesModel: List<ColorsArticles> = emptyList(),
    val soldArticles: List<ArticlesSelled> = emptyList(),
    val suppliers: List<Suppliers> = emptyList(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null
)

// HeadOfViewModels.kt
open class StartUpNewArticlesViewModels(
    private val database: Objects
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _currentArticle = MutableStateFlow<ArticlesBasesStats?>(null)
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

    fun updateCurrentArticle(article: ArticlesBasesStats) {
        _currentArticle.value= article
    }

    private fun updateLoadingProgress(progress: Float) {
        _uiState.update { it.copy(loadingProgress = progress) }
    }

    private fun setLoading(isLoading: Boolean) {
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

    private suspend fun createNewArrivaleCategoryIfNeeded(existingCategories: List<CategoriesModel>) {
        val hasNewArrivale = existingCategories.any {
            it.nomCategorieInCategoriesTabele == "NewArrivale"
        }

        if (!hasNewArrivale) {
            val maxId = existingCategories.maxOfOrNull {
                it.idCategorieInCategoriesTabele
            } ?: 0

            val newArrivaleCategory = CategoriesModel(
                idCategorieInCategoriesTabele = maxId + 1,
                nomCategorieInCategoriesTabele = "NewArrivale",
                idClassementCategorieInCategoriesTabele = 1
            )

            database.categoriesModelDao().insert(newArrivaleCategory)
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

                // Create NewArrivale category if needed
                createNewArrivaleCategoryIfNeeded(categories)
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
                    snapshot.getValue(ArticlesBasesStats::class.java)
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

            while (progress < 100f) {
                progress += 10f
                updateLoadingProgress(progress)
                delay(100)
            }

            val articles = database.articlesBasesStatsModelDao().getAll()
            val categories = database.categoriesModelDao().getAll()
            val colors = database.colorsArticlesDao().getAllOrdred()

            // Ensure NewArrivale category exists when loading data
            createNewArrivaleCategoryIfNeeded(categories)

            _uiState.update { it.copy(
                articlesBasesStats = articles,
                categories = database.categoriesModelDao().getAll(), // Refresh categories after potential NewArrivale creation
                colorsArticlesModel = colors,
            ) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        } finally {
            setLoading(false)
        }
    }

}
