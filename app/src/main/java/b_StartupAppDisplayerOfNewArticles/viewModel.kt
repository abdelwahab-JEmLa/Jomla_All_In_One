package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.AppSettingsSaverModel
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
    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val articlesBasesStatTabelles: List<ArticlesBasesStatsTabelle> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
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

    // Ensure the directory exists when initializing the path
    val viewModelImagesPath = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/").apply {
        if (!exists()) {
            mkdirs()
        }
    }
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val refAppSettingsSaverModel = firebaseDatabase.getReference("A_AppSettingsSaverModel")
    private val refDBJetPackExport = firebaseDatabase.getReference("e_DBJetPackExport")
    private val refCategorieModel = firebaseDatabase.getReference("H_CategorieTabele")
    private val refColorsArticles = firebaseDatabase.getReference("H_ColorsArticles")
    private val refSoldArticlesTabelle = firebaseDatabase.getReference("O_SoldArticlesTabelle")
    private val refClientsTabelle = firebaseDatabase.getReference("G_Clients")


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

    private val _currentSale = MutableStateFlow<SoldArticlesTabelle?>(null)
    val currentSale = _currentSale.asStateFlow()
    // Add to StartUpNewArticlesViewModels.kt
// In StartUpNewArticlesViewModels.kt
    fun deleteSoldArticle(vid: Long) {
        viewModelScope.launch {
            try {
                // Find the article to delete
                val articleToDelete = _uiState.value.soldArticlesModel.find {
                    it?.vid == vid
                }

                // Only proceed if article is found
                articleToDelete?.let { article ->
                    // Delete from database
                    database.soldArticlesTabelleDao().delete(article)

                    // Update the UI state
                    _uiState.update { state ->
                        state.copy(
                            soldArticlesModel = state.soldArticlesModel.filterNotNull().filter {
                                it.vid != vid
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete sale: ${e.message}") }
            }
        }
    }
    fun updateColorSelection(colorIndex: Int, colorId: Long, quantity: Int) {
        viewModelScope.launch {
            _currentSale.value?.let { sale ->
                val updatedSale = when (colorIndex) {
                    0 -> sale.copy(
                        color1IdPicked = colorId,
                        color1SoldQuantity = quantity
                    )
                    1 -> sale.copy(
                        color2IdPicked = colorId,
                        color2SoldQuantity = quantity
                    )
                    2 -> sale.copy(
                        color3IdPicked = colorId,
                        color3SoldQuantity = quantity
                    )
                    3 -> sale.copy(
                        color4IdPicked = colorId,
                        color4SoldQuantity = quantity
                    )
                    else -> sale
                }

                try {

                    // Update current sale state
                    _currentSale.value = updatedSale


                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to update sale: ${e.message}") }
                }
            }
        }
    }

    fun resetColorSelection(colorIndex: Int) {
        viewModelScope.launch {
            _currentSale.value?.let { sale ->
                val updatedSale = when (colorIndex) {
                    0 -> sale.copy(color1IdPicked = 0, color1SoldQuantity = 0)
                    1 -> sale.copy(color2IdPicked = 0, color2SoldQuantity = 0)
                    2 -> sale.copy(color3IdPicked = 0, color3SoldQuantity = 0)
                    3 -> sale.copy(color4IdPicked = 0, color4SoldQuantity = 0)
                    else -> sale
                }

                try {

                    // Update current sale state
                    _currentSale.value = updatedSale


                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to reset sale: ${e.message}") }
                }
            }
        }
    }

    fun saveSaleTransaction() {
        viewModelScope.launch {
            _currentSale.value?.let { sale ->
                try {
                    // Update the sale in the database
                    database.soldArticlesTabelleDao().insert(sale)

                    // Update the UI state
                    _uiState.update { state ->
                        val updatedSales = state.soldArticlesModel.map {
                            if (it?.vid == sale.vid) sale else it
                        }
                        state.copy(soldArticlesModel = updatedSales)
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to save sale: ${e.message}") }
                }
            }
        }
    }

    fun createNewSaleIfNotExist(article: ArticlesBasesStatsTabelle, clientBuyerNow: ClientsModel) {
        viewModelScope.launch {

            val maxId = _uiState.value.soldArticlesModel.maxOfOrNull { it?.vid ?: 1 } ?: 0

            val newSale = SoldArticlesTabelle(
                    vid = maxId + 1,
                    idArticle = article.idArticle.toLong(),
                    nameArticle = article.nomArticleFinale,
                    clientSoldToItId = clientBuyerNow.idClientsSu,
                    date = System.currentTimeMillis().toString(),
                )

            try {
                database.soldArticlesTabelleDao().insert(newSale)
                _currentSale.value = newSale
                _uiState.update { state ->
                    state.copy(soldArticlesModel = state.soldArticlesModel + newSale)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to create sale: ${e.message}") }
            }
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
                idClassementCategorieInCategoriesTabele = 1,
                displayedHeader = true
            )

            database.categoriesModelDao().insert(newArrivaleCategory)
        }
    }

    fun importFromFirebase() {
        viewModelScope.launch {
            try {
                setLoading(true)
                updateLoadingProgress(10f)


                appSettingsSaverModelInitialize(15f)


                updateLoadingProgress(20f)

                val categoriesSnapshot = refCategorieModel.get().await()
                updateLoadingProgress(40f)

                val categories = categoriesSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(CategoriesTabelle::class.java)
                }
                database.categoriesModelDao().insertAll(categories)

                createNewArrivaleCategoryIfNeeded(categories)
                updateLoadingProgress(70f)

                colorInitialize(80f)
                clientsInitialize(82f)
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
    private suspend fun appSettingsSaverModelInitialize(fl: Float) {
        val appSettingsSaverModelSnapshot = refAppSettingsSaverModel.get().await()
        val appSettingsSaverModel = appSettingsSaverModelSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(AppSettingsSaverModel::class.java)
        }
        database.appSettingsSaverModelDao().insertAll(appSettingsSaverModel)
        updateLoadingProgress(fl)
    }
    private suspend fun clientsInitialize(fl: Float) {
        val clientsSnapshot = refClientsTabelle.get().await()
        val clients = clientsSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(ClientsModel::class.java)
        }
        database.clientsModelDao().insertAll(clients)
        updateLoadingProgress(fl)
    }
    private suspend fun colorInitialize(fl: Float) {
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

            // Load all settings including client setting
            val settings = database.appSettingsSaverModelDao().getAll()
            if (!settings.any { it.name == "clientBuyerNowId" }) {
                database.appSettingsSaverModelDao().insert(
                    AppSettingsSaverModel(
                        id = System.currentTimeMillis(),
                        name = "clientBuyerNowId",
                        valueLong = 0
                    )
                )
            }

            val articles = database.articlesBasesStatsModelDao().getAll()
            val categories = database.categoriesModelDao().getAll()
            val colors = database.colorsArticlesDao().getAllOrdred()
            val soldArticles = database.soldArticlesTabelleDao().getAll()
            val clients = database.clientsModelDao().getAll()

            createNewArrivaleCategoryIfNeeded(categories)

            _uiState.update { it.copy(
                appSettingsSaverModel = database.appSettingsSaverModelDao().getAll(),
                articlesBasesStatTabelles = articles,
                categories = categories,
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

    fun updateCurrentClient(clientId: Long) {
        viewModelScope.launch {
            try {
                val currentSetting = _uiState.value.appSettingsSaverModel.find { it.name == "clientBuyerNowId" }
                    ?.copy(valueLong = clientId)
                    ?: AppSettingsSaverModel(
                        id = System.currentTimeMillis(),
                        name = "clientBuyerNowId",
                        valueLong = clientId
                    )

                database.appSettingsSaverModelDao().insert(currentSetting)

                _uiState.update { state ->
                    state.copy(
                        appSettingsSaverModel = state.appSettingsSaverModel.map {
                            if (it.name == "clientBuyerNowId") currentSetting else it
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
