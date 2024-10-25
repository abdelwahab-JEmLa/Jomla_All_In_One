package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesAcheteModele
import a_RoomDB.CategoriesTabelleECB
import a_RoomDB.ColorsArticles
import a_RoomDB.DataBaseArticles
import a_RoomDB.TabelleSuppliersSA
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


// State class
data class CreatAndEditeInBaseDonnRepositeryModels(
    val articlesBaseDonneECB: List<DataBaseArticles> = emptyList(),
    val categoriesECB: List<CategoriesTabelleECB> = emptyList(),
    val colorsArticles: List<ColorsArticles> = emptyList(),
    val articlesAcheteModele: List<ArticlesAcheteModele> = emptyList(),
    val tabelleSuppliersSA: List<TabelleSuppliersSA> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// HeadOfViewModels.kt
class HeadOfViewModels(private val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(CreatAndEditeInBaseDonnRepositeryModels())
    val uiState = _uiState.asStateFlow()

    private val _currentEditedArticle = MutableStateFlow<DataBaseArticles?>(null)
    val currentEditedArticle = _currentEditedArticle.asStateFlow()

    private val _uploadProgress = MutableStateFlow(100f)
    val uploadProgress = _uploadProgress.asStateFlow()

    private val _textProgress = MutableStateFlow("")
    val textProgress = _textProgress.asStateFlow()

    private val database = FirebaseDatabase.getInstance()
    private val articlesRef = database.getReference("e_DBJetPackExport")
    private val categoriesRef = database.getReference("H_CategorieTabele")
    private val colorsRef = database.getReference("H_ColorsArticles")
    private val purchasedArticlesRef = database.getReference("ArticlesAcheteModeleAdapted")
    private val suppliersRef = database.getReference("F_Suppliers")

    init {
        viewModelScope.launch {
            initDataFromFirebase()
        }
    }

    fun updateProgressBar(
        actionName: String = "",
        progress: Int = 100,
        isComplete: Boolean = false,
        delayMs: Long = 0
    ) {
        viewModelScope.launch {
            _uploadProgress.value = if (isComplete) 0f else progress.toFloat()
            _textProgress.value = actionName
            if (delayMs > 0) delay(delayMs)
        }
    }

    private suspend fun initDataFromFirebase() = withContext(Dispatchers.IO) {
        try {
            _uiState.update { it.copy(isLoading = true) }

            updateProgressBar("Fetching data...", 0)

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
                    articlesAcheteModele = purchasedArticles,
                    tabelleSuppliersSA = suppliers,
                    isLoading = false
                )
            }

            updateProgressBar("Data loaded successfully", 100, true, 1000)
        } catch (e: Exception) {
            _uiState.update { it.copy(
                error = e.message,
                isLoading = false
            ) }
            updateProgressBar("Error loading data", 0, true)
        }
    }

    private suspend fun fetchArticles() = articlesRef
        .get()
        .await()
        .children
        .mapNotNull { snapshot ->
            snapshot.getValue(DataBaseArticles::class.java)?.apply {
                idArticle = snapshot.key?.toIntOrNull() ?: 0
            }
        }

    private suspend fun fetchCategories() = categoriesRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(CategoriesTabelleECB::class.java) }
        .sortedBy { it.idClassementCategorieInCategoriesTabele }

    private suspend fun fetchColors() = colorsRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(ColorsArticles::class.java) }

    private suspend fun fetchPurchasedArticles() = purchasedArticlesRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(ArticlesAcheteModele::class.java) }

    private suspend fun fetchSuppliers() = suppliersRef
        .get()
        .await()
        .children
        .mapNotNull { it.getValue(TabelleSuppliersSA::class.java) }
        .sortedBy { it.classmentSupplier }
}



