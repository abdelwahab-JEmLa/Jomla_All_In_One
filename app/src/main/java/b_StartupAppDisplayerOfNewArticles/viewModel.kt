package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ClientsModel
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
    val clientsModel: List<ClientsModel> = emptyList(),
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
    private val refClientsTabelle = firebaseDatabase.getReference("G_Clients")


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

    private var currentClientId: Long = 0L
    private var currentSaleId: Long? = null

    private val _currentSale = MutableStateFlow<SoldArticlesTabelle?>(null)
    val currentSale = _currentSale.asStateFlow()

    fun setCurrentClientAndArticle(client: ClientsModel, article: ArticlesBasesStatsTabelle) {
        viewModelScope.launch {
            currentClientId = client.idClientsSu

            // Find existing sale for this client and article
            val existingSale = _uiState.value.soldArticlesModel.find {
                it.idArticle == article.idArticle.toLong() &&
                        it.clientSoldToItId == currentClientId
            }

            currentSaleId = existingSale?.vid
            _currentSale.value = existingSale

            // Reset color selections to match existing sale if found
            if (existingSale != null) {
                _selectedQuantities.value = mapOf(
                    0 to ColorSelection(existingSale.color1IdPicked, existingSale.color1SoldQuantity),
                    1 to ColorSelection(existingSale.color2IdPicked, existingSale.color2SoldQuantity),
                    2 to ColorSelection(existingSale.color3IdPicked, existingSale.color3SoldQuantity),
                    3 to ColorSelection(existingSale.color4IdPicked, existingSale.color4SoldQuantity)
                ).filterValues { it.colorId != 0L }
            } else {
                _selectedQuantities.value = emptyMap()
            }
        }
    }

    fun resetColorSelection(colorIndex: Int) {
        _selectedQuantities.update { current ->
            current.filterKeys { it != colorIndex }
        }

        // Also update the current sale record if it exists
        currentSaleId?.let { saleId ->
            viewModelScope.launch {
                val updatedSale = _currentSale.value?.let { sale ->
                    when (colorIndex) {
                        0 -> sale.copy(color1IdPicked = 0L, color1SoldQuantity = 0)
                        1 -> sale.copy(color2IdPicked = 0L, color2SoldQuantity = 0)
                        2 -> sale.copy(color3IdPicked = 0L, color3SoldQuantity = 0)
                        3 -> sale.copy(color4IdPicked = 0L, color4SoldQuantity = 0)
                        else -> sale
                    }
                }

                updatedSale?.let {
                    database.soldArticlesTabelleDao().insert(it)
                    _currentSale.value = it
                    _uiState.update { state ->
                        state.copy(
                            soldArticlesModel = state.soldArticlesModel.map { sale ->
                                if (sale.vid == saleId) it else sale
                            }
                        )
                    }
                }
            }
        }
    }

    fun saveSaleTransaction(article: ArticlesBasesStatsTabelle) {
        viewModelScope.launch {
            val currentSelections = _selectedQuantities.value
            if (currentSelections.isEmpty() || currentClientId == 0L) return@launch

            val newSale = if (currentSaleId != null) {
                // Update existing sale
                _currentSale.value?.copy(
                    color1IdPicked = currentSelections[0]?.colorId ?: 0,
                    color1SoldQuantity = currentSelections[0]?.quantity ?: 0,
                    color2IdPicked = currentSelections[1]?.colorId ?: 0,
                    color2SoldQuantity = currentSelections[1]?.quantity ?: 0,
                    color3IdPicked = currentSelections[2]?.colorId ?: 0,
                    color3SoldQuantity = currentSelections[2]?.quantity ?: 0,
                    color4IdPicked = currentSelections[3]?.colorId ?: 0,
                    color4SoldQuantity = currentSelections[3]?.quantity ?: 0
                )
            } else {
                // Create new sale
                val maxId = _uiState.value.soldArticlesModel.maxOfOrNull { it.vid } ?: 0
                SoldArticlesTabelle(
                    vid = maxId + 1,
                    idArticle = article.idArticle.toLong(),
                    nameArticle = article.nomArticleFinale,
                    clientSoldToItId = currentClientId,
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
            }

            newSale?.let { sale ->
                database.soldArticlesTabelleDao().insert(sale)
                _currentSale.value = sale
                currentSaleId = sale.vid

                _uiState.update { state ->
                    state.copy(
                        soldArticlesModel = if (currentSaleId != null) {
                            state.soldArticlesModel.map {
                                if (it.vid == currentSaleId) sale else it
                            }
                        } else {
                            state.soldArticlesModel + sale
                        }
                    )
                }
            }
        }
    }


    data class ColorSelection(
        val colorId: Long,
        val quantity: Int
    )

    private val _selectedQuantities = MutableStateFlow<Map<Int, ColorSelection>>(emptyMap())
    val selectedQuantities = _selectedQuantities.asStateFlow()

    fun updateColorSelection(colorIndex: Int, colorId: Long, quantity: Int) {
        _selectedQuantities.update { current ->
            current + (colorIndex to ColorSelection(colorId, quantity))
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


    // Update HeadOfViewModels.kt to include clients initialization
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
            val clients = database.clientsModelDao().getAll()

            createNewArrivaleCategoryIfNeeded(categories)

            _uiState.update { it.copy(
                articlesBasesStatTabelles = articles,
                categories = database.categoriesModelDao().getAll(),
                colorsArticlesTabelleModel = colors,
                soldArticlesModel = soldArticles,
                clientsModel = clients
            ) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        } finally {
            setLoading(false)
        }
    }

    private suspend fun clientsIntia(fl: Float) {
        val clientsSnapshot = refClientsTabelle.get().await()
        val clients = clientsSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(ClientsModel::class.java)
        }
        database.clientsModelDao().insertAll(clients)
        updateLoadingProgress(fl)
    }

    fun importFromFirebase() {
        viewModelScope.launch {
            try {
                setLoading(true)
                updateLoadingProgress(10f)

                val categoriesSnapshot = refCategorieModel.get().await()
                updateLoadingProgress(40f)

                val categories = categoriesSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(CategoriesTabelle::class.java)
                }
                database.categoriesModelDao().insertAll(categories)

                createNewArrivaleCategoryIfNeeded(categories)
                updateLoadingProgress(70f)

                colorIntia(80f)
                clientsIntia(82f)  // Added clients initialization
                soldArticlesTabelleIntia(85f)

                val articlesSnapshot = refDBJetPackExport.get().await()
                val articles = articlesSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(ArticlesBasesStatsTabelle::class.java)
                }
                database.articlesBasesStatsModelDao().insertAll(articles)
                updateLoadingProgress(100f)

                loadData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                setLoading(false)
            }
        }
    }

}
