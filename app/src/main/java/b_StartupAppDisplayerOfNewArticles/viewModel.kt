package b_StartupAppDisplayerOfNewArticles

import a_MainAppCompnents.CategoriesDao
import a_RoomDB.ArticlesBasesStats
import a_RoomDB.ArticlesSelled
import a_RoomDB.Categories
import a_RoomDB.ColorsArticles
import a_RoomDB.Suppliers
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


// State class
data class CreatAndEditeInBaseDonnRepositeryModels(
    val articlesBaseDonneECB: List<ArticlesBasesStats> = emptyList(),
    val categoriesECB: List<Categories> = emptyList(),
    val colorsArticles: List<ColorsArticles> = emptyList(),
    val articlesSelled: List<ArticlesSelled> = emptyList(),
    val suppliers: List<Suppliers> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// HeadOfViewModels.kt
class HeadOfViewModels(
    private val context: Context,
    private val categoriesDao: CategoriesDao,

    ) : ViewModel() {
    private val _uiState = MutableStateFlow(CreatAndEditeInBaseDonnRepositeryModels())
    val uiState = _uiState.asStateFlow()

    private val _currentEditedArticle = MutableStateFlow<ArticlesBasesStats?>(null)
    val currentEditedArticle = _currentEditedArticle.asStateFlow()

    private val _uploadProgress = MutableStateFlow(100f)
    val uploadProgress = _uploadProgress.asStateFlow()


    private val firebase = FirebaseDatabase.getInstance()
    private val articlesRef = firebase.getReference("e_DBJetPackExport")
    private val categoriesRef = firebase.getReference("H_CategorieTabele")
    private val colorsRef = firebase.getReference("H_ColorsArticles")
    private val purchasedArticlesRef = firebase.getReference("ArticlesAcheteModeleAdapted")
    private val suppliersRef = firebase.getReference("F_Suppliers")

    init {
        viewModelScope.launch {
            initDataFromFirebase()
        }
    }



    private suspend fun initDataFromFirebase() = withContext(Dispatchers.IO) {
        try {
            _uiState.update { it.copy(isLoading = true) }

            val articles = fetchArticles()
            val categories = fetchCategories()
            val colors = fetchColors()
            val purchasedArticles = fetchPurchasedArticles()
            val suppliers = fetchSuppliers()

            _uiState.update { state ->
                state.copy(
                    articlesBaseDonneECB = articles,
                    categoriesECB = categories,
                    colorsArticles = colors,
                    articlesSelled = purchasedArticles,
                    suppliers = suppliers,
                    isLoading = false
                )
            }

        } catch (e: Exception) {
            _uiState.update { it.copy(
                error = e.message,
                isLoading = false
            ) }
        }
    }

    private suspend fun fetchArticles() = articlesRef
        .get()
        .await()
        .children
        .mapNotNull { snapshot ->
            snapshot.getValue(ArticlesBasesStats::class.java)
        }

    private suspend fun fetchCategories() = categoriesRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(Categories::class.java) }
        .sortedBy { it.position }

    private suspend fun fetchColors() = colorsRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(ColorsArticles::class.java) }

    private suspend fun fetchPurchasedArticles() = purchasedArticlesRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(ArticlesSelled::class.java) }

    private suspend fun fetchSuppliers() = suppliersRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(Suppliers::class.java) }
        .sortedBy { it.classmentSupplier }
}



