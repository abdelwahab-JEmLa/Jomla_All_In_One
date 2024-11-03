package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.AppDatabase
import a_RoomDB.AppSettingsSaverModel
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import a_RoomDB.SuppliersTabelle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import d_SoldCartScreen.getTotalQuantity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Date


// UiState.kt
data class UiState(
    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val articlesBasesStatTables: List<ArticlesBasesStatsTable> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
    val clientsModel: List<ClientsModel> = emptyList(),
    val suppliers: List<SuppliersTabelle> = emptyList(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null
)

// StartUpNewArticlesViewModels.kt
class StartUpNewArticlesViewModels(
    private val database: AppDatabase
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
            loadDataOfUiStateFromRoom()
        }
    }

    private val _currentSaleInWindows = MutableStateFlow<SoldArticlesTabelle?>(null)
    val currentSaleInWindows = _currentSaleInWindows.asStateFlow()


    fun openWindowsNewSaleWithUpdateCurrent(
        relatedArticleDataBaseId: Long,
        currentClient: Long,
        indexColor: Int,
    ) {
        viewModelScope.launch {
            try {
                val maxId = _uiState.value.soldArticlesModel
                    .filterNotNull()
                    .maxOfOrNull { it.vid } ?: 0

                val article = _uiState.value.articlesBasesStatTables
                    .find { it.idArticle.toLong() == relatedArticleDataBaseId }

                val newSale = SoldArticlesTabelle(
                    vid = maxId + 1,
                    idArticle = relatedArticleDataBaseId,
                    nameArticle = article?.nomArticleFinale ?: "",
                    clientSoldToItId = currentClient,
                    date = System.currentTimeMillis().toString()
                ).let { sale ->
                    when (indexColor) {
                        0 -> sale.copy(color1IdPicked = article?.idcolor1 ?: 0, color1SoldQuantity = 1)
                        1 -> sale.copy(color2IdPicked = article?.idcolor2 ?: 0, color2SoldQuantity = 1)
                        2 -> sale.copy(color3IdPicked = article?.idcolor3 ?: 0, color3SoldQuantity = 1)
                        3 -> sale.copy(color4IdPicked = article?.idcolor4 ?: 0, color4SoldQuantity = 1)
                        else -> sale
                    }
                }

                _currentSaleInWindows.value = newSale
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update sale: ${e.message}") }
            }
        }
    }

    fun updateColorSelection(colorIndex: Int, quantity: Int) {
        viewModelScope.launch {
            _currentSaleInWindows.value?.let { sale ->
                val article = _uiState.value.articlesBasesStatTables
                    .find { it.idArticle.toLong() == sale.idArticle }

                val updatedSale = when (colorIndex) {
                    0 -> sale.copy(
                        color1IdPicked = article?.idcolor1?.takeIf { it != 0L } ?: sale.color1IdPicked,
                        color1SoldQuantity = quantity
                    )
                    1 -> sale.copy(
                        color2IdPicked = article?.idcolor2?.takeIf { it != 0L } ?: sale.color2IdPicked,
                        color2SoldQuantity = quantity
                    )
                    2 -> sale.copy(
                        color3IdPicked = article?.idcolor3?.takeIf { it != 0L } ?: sale.color3IdPicked,
                        color3SoldQuantity = quantity
                    )
                    3 -> sale.copy(
                        color4IdPicked = article?.idcolor4?.takeIf { it != 0L } ?: sale.color4IdPicked,
                        color4SoldQuantity = quantity
                    )
                    else -> sale
                }

                try {
                    val totalQuantity = updatedSale.run {
                        color1SoldQuantity + color2SoldQuantity +
                                color3SoldQuantity + color4SoldQuantity
                    }

                    if (totalQuantity == 0) {
                        deleteSoldArticle(updatedSale.vid)
                        clearCurrentSale()
                    } else {
                        _currentSaleInWindows.value = updatedSale
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to update sale: ${e.message}") }
                }
            }
        }
    }
    fun resetColorSelectionFromSoldArt(soldArticle: SoldArticlesTabelle, colorIndex: Int) {
        viewModelScope.launch {
            try {
                // Create updated sale with reset color quantities
                val updatedSale = when (colorIndex) {
                    0 -> soldArticle.copy(color1IdPicked = 0, color1SoldQuantity = 0)
                    1 -> soldArticle.copy(color2IdPicked = 0, color2SoldQuantity = 0)
                    2 -> soldArticle.copy(color3IdPicked = 0, color3SoldQuantity = 0)
                    3 -> soldArticle.copy(color4IdPicked = 0, color4SoldQuantity = 0)
                    else -> soldArticle
                }

                if (updatedSale.getTotalQuantity() == 0) {
                    // If no quantities remain, delete the article completely
                    deleteSoldArticle(updatedSale.vid)
                } else {
                    // Update Room database
                    database.soldArticlesTabelleDao().insert(updatedSale)

                    // Update Firebase
                    firebaseDatabase.getReference("O_SoldArticlesTabelle")
                        .child(updatedSale.vid.toString())
                        .setValue(updatedSale)
                        .await()

                    // Update UI state
                    _uiState.update { state ->
                        state.copy(
                            soldArticlesModel = state.soldArticlesModel.map { article ->
                                if (article?.vid == updatedSale.vid) updatedSale else article
                            }
                        )
                    }

                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to reset sale: ${e.message}") }
            }
        }
    }
    fun clearCurrentSale() {
        _currentSaleInWindows.value=null
    }



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

    fun saveSaleTransactionToSoldAriclesList() {
        viewModelScope.launch {
            _currentSaleInWindows.value?.let { sale ->
                try {
                    // Update the sale in the database
                    database.soldArticlesTabelleDao().insert(sale)

                    // Update the UI state
                    _uiState.update { state ->
                        val existingSale = state.soldArticlesModel.find { it?.vid == sale.vid }
                        val updatedSales = if (existingSale != null) {
                            // Update existing sale
                            state.soldArticlesModel.map {
                                if (it?.vid == sale.vid) sale else it
                            }
                        } else {
                            // Add new sale to the list
                            state.soldArticlesModel + sale
                        }
                        state.copy(soldArticlesModel = updatedSales)
                    }

                    // Update Firebase
                    firebaseDatabase.getReference("O_SoldArticlesTabelle")
                        .child(sale.vid.toString())
                        .setValue(sale)
                        .await()

                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to save sale: ${e.message}") }
                }
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
                    snapshot.getValue(ArticlesBasesStatsTable::class.java)
                }
                database.articlesBasesStatsModelDao().insertAll(articles)
                updateLoadingProgress(100f)

                loadDataOfUiStateFromRoom()
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


    private suspend fun loadDataOfUiStateFromRoom() {
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
                articlesBasesStatTables = articles,
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

    fun updateLongAppSetting(name: String, value: Long) {
        viewModelScope.launch {
            try {

                val existingSettings = _uiState.value.appSettingsSaverModel
                val maxId = existingSettings.maxOfOrNull { it.id } ?: 0

                val currentSetting = existingSettings.find { it.name == name }
                    ?.copy(
                        valueLong = value,
                        date = Date()
                    )
                    ?: AppSettingsSaverModel(
                        id = maxId + 1,
                        name = name,
                        valueLong = value,
                        date = Date()
                    )

                // Update local database
                database.appSettingsSaverModelDao().insert(currentSetting)

                // Update Firebase
                firebaseDatabase.getReference("A_AppSettingsSaverModel")
                    .child(currentSetting.id.toString())
                    .setValue(currentSetting)
                    .await()

                _uiState.update { state ->
                    state.copy(
                        appSettingsSaverModel = state.appSettingsSaverModel.map {
                            if (it.name == name) currentSetting else it
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
