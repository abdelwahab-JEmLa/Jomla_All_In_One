package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.Objects
import a_RoomDB.SoldArticlesTabelle
import a_RoomDB.SuppliersTabelle
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
    val articlesBasesStatTabelles: List<ArticlesBasesStatsTabelle> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle> = emptyList(),
    val suppliers: List<SuppliersTabelle> = emptyList(),
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

    private val _currentArticle = MutableStateFlow<ArticlesBasesStatsTabelle?>(null)
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
    private val refSoldArticlesTabelle = firebaseDatabase.getReference("O_SoldArticlesTabelle")

    fun updateCurrentArticle(article: ArticlesBasesStatsTabelle) {
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
    data class ColorSelection(
        val colorId: Long = 0,
        val quantity: Int = 0
    )

    private val _selectedQuantities = MutableStateFlow<Map<Int, ColorSelection>>(emptyMap())
    val selectedQuantities = _selectedQuantities.asStateFlow()

    // Used by CompactQuantityPicker when user selects quantity
    fun updateColorSelection(colorIndex: Int, colorId: Long, quantity: Int) {
        _selectedQuantities.update { current ->
            current + (colorIndex to ColorSelection(colorId, quantity))
        }
    }

    fun saveSaleTransaction(article: ArticlesBasesStatsTabelle) {
        viewModelScope.launch {
            val currentSelections = _selectedQuantities.value
            if (currentSelections.isEmpty()) return@launch

            // Get max ID from uiState's soldArticlesModel instead of DAO
            val maxId = _uiState.value.soldArticlesModel
                .maxOfOrNull { it.vid }
                ?: 0

            val newSale = SoldArticlesTabelle(
                vid = maxId + 1,
                idArticle = article.idArticle.toLong(),
                nameArticle = article.nomArticleFinale,
                date = System.currentTimeMillis().toString(),
                color1IdPicked = currentSelections[0]?.colorId ?: 0,
                color1SoldQuantity = currentSelections[0]?.quantity ?: 0,
                color2IdPicked = currentSelections[1]?.colorId ?: 0,
                color2SoldQuantity = currentSelections[1]?.quantity ?: 0,
                color3IdPicked = currentSelections[2]?.colorId ?: 0,
                color3SoldQuantity = currentSelections[2]?.quantity ?: 0,
                color4IdPicked = currentSelections[3]?.colorId ?: 0,
                color4SoldQuantity = currentSelections[3]?.quantity ?: 0
            )

            database.soldArticlesTabelleDao().insert(newSale)

            // Update uiState with new sale
            _uiState.update { currentState ->
                currentState.copy(
                    soldArticlesModel = currentState.soldArticlesModel + newSale
                )
            }

            _selectedQuantities.value = emptyMap() // Reset selections after saving
        }
    }
    fun resetColorSelection(colorIndex: Int) {
        _selectedQuantities.update { current ->
            current + (colorIndex to ColorSelection(colorId = 0, quantity = 0))
        }
    }

    private suspend fun createNewArrivaleCategoryIfNeeded(existingCategories: List<CategoriesTabelle>) {
        val hasNewArrivale = existingCategories.any {
            it.nomCategorieInCategoriesTabele == "NewArrivale"
        }

        if (!hasNewArrivale) {
            val maxId = existingCategories.maxOfOrNull {
                it.idCategorieInCategoriesTabele
            } ?: 0

            val newArrivaleCategory = CategoriesTabelle(
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
                    snapshot.getValue(CategoriesTabelle::class.java)
                }
                database.categoriesModelDao().insertAll(categories)

                // Create NewArrivale category if needed
                createNewArrivaleCategoryIfNeeded(categories)
                updateLoadingProgress(70f)

                colorIntia(80f)

                soldArticlesTabelleIntia(85f)

                // Import articlesBasesStatsModel
                val articlesSnapshot = refDBJetPackExport.get().await()
                val articles = articlesSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(ArticlesBasesStatsTabelle::class.java)
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

    private suspend fun colorIntia(fl: Float) {
        // Import colors
        val colorsSnapshot = refColorsArticles.get().await()

        val colors = colorsSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(ColorsArticlesTabelle::class.java)
        }
        database.colorsArticlesDao().insertAll(colors)
        updateLoadingProgress(fl)
    }
    private suspend fun soldArticlesTabelleIntia(fl: Float) {

        val soldArticlesSnapshot = refSoldArticlesTabelle.get().await()

        val soldArticles = soldArticlesSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(SoldArticlesTabelle::class.java)
        }
        database.soldArticlesTabelleDao().insertAll(soldArticles)
        updateLoadingProgress(fl)
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
            val soldArticles = database.soldArticlesTabelleDao().getAll()

            // Ensure NewArrivale category exists when loading data
            createNewArrivaleCategoryIfNeeded(categories)

            _uiState.update { it.copy(
                articlesBasesStatTabelles = articles,
                categories = database.categoriesModelDao().getAll(), // Refresh categories after potential NewArrivale creation
                colorsArticlesTabelleModel = colors,
                soldArticlesModel = soldArticles
            ) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        } finally {
            setLoading(false)
        }
    }

}
