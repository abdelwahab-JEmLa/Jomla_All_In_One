package com.example.clientjetpack.ViewModel

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.Z.Archive.AppSettingsSaverModel
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesAcheteModele
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.DiviseurDeDisplayProductForEachClient
import Z_CodePartageEntreApps.Model.Z.Archive.ProductDisplayController
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SuppliersTabelle
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.BuildConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class UiState(
    val articlesBasesStatTables: List<ArticlesBasesStatsTable> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),

    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val devicesTypeManager: List<DevicesTypeManager> = emptyList(),
    val newProduitsList: List<A_ProduitModel> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
    val suppliers: List<SuppliersTabelle> = emptyList(),

    val diviseurDeDisplayProductForEachClient: List<DiviseurDeDisplayProductForEachClient> = emptyList(),

    val productDisplayController: ProductDisplayController,
    val maxPriceMap: Map<Pair<Long, Long>, List<PriceRecord>> = emptyMap(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null,
)

@SuppressLint("StaticFieldLeak")
open class HeadViewModel(
    val aCentralFacade: ACentralFacade,
    val context: Context,
    val database: AppDatabase,
    val a_MasterRepositorys: A_MasterRepositorysGrpProtoJuin3
) : ViewModel() {
    val getter = aCentralFacade.getRepositorys

    private val tag = "HeadViewModel"
    private val firestore = Firebase.firestore

    val _uiState = MutableStateFlow(UiState(productDisplayController = ProductDisplayController()))
    open val uiState = _uiState.asStateFlow()

    private val connectionManager = WifiTransferDatas(
        context = context,
        a_CentralCompoRepositoryProtoJuin9 = getter,
    ) { payload -> handleRetoureDataPayload(payload) }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        viewModelScope.launch {
            connectionManager.sendData("$orderName$data")
        }
    }

    private fun handleRetoureDataPayload(payload: String) {
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            when (messageType) {
                WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition -> updateDisplayController {
                    copy(mainGridScrollPosition = content.toInt())
                }

                WifiUpdateClientDisplayerStats.ClientWindowsDisplayedProductId -> updateDisplayController {
                    copy(clientWindowsDisplayedProductId = content.toLong())
                }

                WifiUpdateClientDisplayerStats.DISMISS_PRODUCT_INFO -> updateDisplayController {
                    copy(
                        clientWindowsDisplayedProductId = null, searchWindowsDisplaye = ""
                    )
                }

                else -> {}
            }
        } ?: Log.d(tag, "📩 Unhandled message received: $payload")
    }

    private fun setupMaxPriceObserver() {
        viewModelScope.launch {
            // Setup real-time listener for price changes
            firestore.collection("HistoriqueDesFactures").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(tag, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    processHistoricalData(snapshot.documents)
                }
            }
        }
    }

    private fun processHistoricalData(documents: List<DocumentSnapshot>) {
        try {
            val priceHistory = documents.mapNotNull { doc ->
                doc.toObject(ArticlesAcheteModele::class.java)?.let { article ->
                    val productId = article.idArticle.toLong()
                    val clientId = article.idClient?.toLongOrNull() ?: 0L
                    val price = article.monPrixVentFireStoreBM
                    val date = article.dateDachate.toLongOrNull() ?: System.currentTimeMillis()

                    Triple(productId, clientId, PriceRecord(price, clientId, date))
                }
            }.groupBy(keySelector = { Pair(it.first, it.second) }, valueTransform = { it.third })

            _uiState.update { currentState ->
                currentState.copy(maxPriceMap = priceHistory)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error processing historical data", e)
        }
    }

    open fun getMaxPrice(productId: Long): Double {
        return uiState.value.maxPriceMap.filter { it.key.first == productId.toLong() }.values.flatten()
            .maxOfOrNull { it.price } ?: 0.0
    }

    open fun getHistoryProductForClient(productId: Long, clientId: Long): List<PriceRecord> {
        val key = Pair(productId.toLong(), clientId)
        return uiState.value.maxPriceMap[key] ?: emptyList()
    }


    private fun observeConnectionState() {
        viewModelScope.launch {
            connectionManager.connectionUiState.collect { connectionState ->

                updateDisplayController {
                    copy(
                        isConnected = connectionState.isConnected,
                        connectionStatus = connectionState.connectionStatus,
                    )
                }

                _uiState.update { it.copy(error = connectionState.error) }
            }
        }
    }

    private fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        _uiState.update { it.copy(productDisplayController = update(it.productDisplayController)) }
    }


    private fun getHostDevices(): List<String> {
        return uiState.value.devicesTypeManager.filter { it.isHost }.map { it.name }
    }

    fun updateTypePhone(type: Boolean = false) {
        updateDisplayController {
            copy(isHostPhone = type)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun initializeConnection() {
        val currentDevice = Build.MODEL.lowercase()
        val isHostDevice = getHostDevices().any { deviceName ->
            currentDevice.contains(deviceName)
        }


        if (isHostDevice) {
            updateTypePhone(true)
        } else {
            updateTypePhone(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() = connectionManager.startAsHost()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() = connectionManager.startAsClient()

    fun disconnect() = connectionManager.disconnect()


    // Ensure the directory exists when initializing the path
    val viewModelImagesPath =
        File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/").apply {
            if (!exists()) {
                mkdirs()
            }
        }

    //  ***
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val refAppSettingsSaverModel = firebaseDatabase.getReference("A_AppSettingsSaverModel")
    private val refDBJetPackExport = firebaseDatabase.getReference("e_DBJetPackExport")
    private val refCategorieModel = firebaseDatabase.getReference("H_CategorieTabele")
    private val refColorsArticles = firebaseDatabase.getReference("H_ColorsArticles")
    private val refSoldArticlesTabelle = firebaseDatabase.getReference("O_SoldArticlesTabelle")
    private val refClientsTabelle = firebaseDatabase.getReference("G_Clients")
    val refDevicesTypeManager = firebaseDatabase.getReference("P_DevicesTypeManager")
    private val diviseurDeDisplayProductForEachClientRef =
        firebaseDatabase.getReference("3_DiviseurDeDisplayProductForEachClient")


    private fun setLoading(isLoading: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = isLoading, loadingProgress = if (!isLoading) 0f else it.loadingProgress
            )
        }
    }


    val _currentSaleInWindows = MutableStateFlow<SoldArticlesTabelle?>(null)
    val currentSaleInWindows = _currentSaleInWindows.asStateFlow()

    fun clearSoldArticlesData() {
        viewModelScope.launch {
            try {
                setLoading(true)

                // Clear Room database
                database.soldArticlesModelDao().deleteAll()


                refSoldArticlesTabelle.removeValue()

                // Reset UI state for sold articles
                _uiState.update { it.copy(soldArticlesModel = emptyList()) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to clear data: ${e.message}") }
            } finally {
                setLoading(false)
            }
        }
    }

    fun clearSupAICommend(): Unit {
        // Clear Firebase reference
        firebaseDatabase.getReference("K_GroupeurBonCommendToSupplierRef").removeValue()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addNewEmptyArticle(nameArticleNIB: String): ArticlesBasesStatsTable? {
        return try {
            val currentState = _uiState.value

            val now = LocalDateTime.now() // Get the current date and time
            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Define your desired format
            val formattedDate = now.format(formatter)

            // Calculate new ID (max existing ID + 1)
            val maxIdArticle = currentState.articlesBasesStatTables.maxOfOrNull { it.id } ?: 0
            // Calculate new ID (max existing ID + 1)
            val maxidForSearchArticles =
                currentState.articlesBasesStatTables.maxOfOrNull { it.idForSearchArticles } ?: 0
            // Create new article with incremented ID
            val newArticle = ArticlesBasesStatsTable(
                id = maxIdArticle + 1,
                nom = nameArticleNIB,
                idcolor1 = 1,
                dateCreationCategorie = formattedDate,
                idForSearchArticles = maxidForSearchArticles
            )

            // Add the new article to the existing list
            val updatedArticles = currentState.articlesBasesStatTables + newArticle

            // Update state with the new list
            _uiState.update { it.copy(articlesBasesStatTables = updatedArticles) }
            // Add to Firebase
            refDBJetPackExport.child(newArticle.id.toString()).setValue(newArticle)

            // Add to Room database
            database.ArticlesBasesStatsModelDao().insert(newArticle)
            newArticle

        } catch (exception: Exception) {
            _uiState.update {
                it.copy(error = "Failed to upsert article: ${exception.message}")
            }
            null
        }
    }

    fun addNewClient(name: String) {

    }

    // Add addNew callback for navigation
    private var _onNavigateToSellerProduct = { _: Long -> }

    fun setNavigationCallback(callback: (Long) -> Unit) {
        _onNavigateToSellerProduct = callback
    }

    fun openWindowsNewSaleWithUpdateCurrent(
        relatedArticleDataBaseId: Long,
        currentClient: Long,
        indexColor: Int,
    ) {
        viewModelScope.launch {
            try {
                // Get the current UI state once to avoid race conditions
                val currentState = _uiState.value

                // Find the maximum VID from existing sales
                val maxId =
                    currentState.soldArticlesModel.filterNotNull().maxOfOrNull { it.vid } ?: 0

                // Find the related article
                val article =
                    currentState.articlesBasesStatTables.find { it.id.toLong() == relatedArticleDataBaseId }
                        ?: throw IllegalStateException("Article not found with ID: $relatedArticleDataBaseId")

                // Create base sale object
                val newSale = SoldArticlesTabelle(
                    vid = maxId + 1,
                    idArticle = relatedArticleDataBaseId,
                    nameArticle = article.nom,
                    clientSoldToItId = currentClient,
                    date = System.currentTimeMillis().toString()
                )

                // Apply color selection based on index
                val updatedSale = when (indexColor) {
                    0 -> newSale.copy(
                        color1IdPicked = article.idcolor1,
                        color1SoldQuantity = 1,
                        // Reset other colors
                        color2IdPicked = 0,
                        color2SoldQuantity = 0,
                        color3IdPicked = 0,
                        color3SoldQuantity = 0,
                        color4IdPicked = 0,
                        color4SoldQuantity = 0
                    )

                    1 -> newSale.copy(
                        color2IdPicked = article.idcolor2,
                        color2SoldQuantity = 1,
                        // Reset other colors
                        color1IdPicked = 0,
                        color1SoldQuantity = 0,
                        color3IdPicked = 0,
                        color3SoldQuantity = 0,
                        color4IdPicked = 0,
                        color4SoldQuantity = 0
                    )

                    2 -> newSale.copy(
                        color3IdPicked = article.idcolor3,
                        color3SoldQuantity = 1,
                        // Reset other colors
                        color1IdPicked = 0,
                        color1SoldQuantity = 0,
                        color2IdPicked = 0,
                        color2SoldQuantity = 0,
                        color4IdPicked = 0,
                        color4SoldQuantity = 0
                    )

                    3 -> newSale.copy(
                        color4IdPicked = article.idcolor4,
                        color4SoldQuantity = 1,
                        // Reset other colors
                        color1IdPicked = 0,
                        color1SoldQuantity = 0,
                        color2IdPicked = 0,
                        color2SoldQuantity = 0,
                        color3IdPicked = 0,
                        color3SoldQuantity = 0
                    )

                    else -> throw IllegalArgumentException("Invalid color index: $indexColor")
                }

                // Update the current sale in windows
                _currentSaleInWindows.value = updatedSale

                // Trigger navigation via callback
                _onNavigateToSellerProduct(updatedSale.vid)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 sale: ${e.message}") }
            }
        }
    }

    fun updateColorSelection(colorId: Long, quantity: Int) {
        viewModelScope.launch {
            _currentSaleInWindows.value?.let { sale ->
                val article =
                    _uiState.value.articlesBasesStatTables.find { it.id.toLong() == sale.idArticle }

                // Find which color slot to upsertLenceCommandeRepoGroupedProtoAvantJuin3 by searching through colorIdPicked fields
                val updatedSale = when (colorId) {
                    sale.color1IdPicked -> sale.copy(color1SoldQuantity = quantity)
                    sale.color2IdPicked -> sale.copy(color2SoldQuantity = quantity)
                    sale.color3IdPicked -> sale.copy(color3SoldQuantity = quantity)
                    sale.color4IdPicked -> sale.copy(color4SoldQuantity = quantity)
                    else -> {
                        // If color not found in existing slots, find first empty slot (0L)
                        when {
                            sale.color1IdPicked == 0L -> sale.copy(
                                color1IdPicked = colorId, color1SoldQuantity = quantity
                            )

                            sale.color2IdPicked == 0L -> sale.copy(
                                color2IdPicked = colorId, color2SoldQuantity = quantity
                            )

                            sale.color3IdPicked == 0L -> sale.copy(
                                color3IdPicked = colorId, color3SoldQuantity = quantity
                            )

                            sale.color4IdPicked == 0L -> sale.copy(
                                color4IdPicked = colorId, color4SoldQuantity = quantity
                            )

                            else -> {
                                // If no empty slots, upsertLenceCommandeRepoGroupedProtoAvantJuin3 first slot as fallback
                                sale.copy(
                                    color1IdPicked = colorId, color1SoldQuantity = quantity
                                )
                            }
                        }
                    }
                }

                try {
                    val totalQuantity = updatedSale.run {
                        color1SoldQuantity + color2SoldQuantity + color3SoldQuantity + color4SoldQuantity
                    }

                    if (totalQuantity == 0) {
                        deleteSoldArticle(updatedSale.vid)

                    } else {
                        _currentSaleInWindows.value = updatedSale
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 sale: ${e.message}") }
                }
            }
        }
    }

    //fun HeadViewModel.updateColorSelection(colorId: Long, quantity: Int) {
//    viewModelScope.launch {
//        _currentSaleInWindows.value?.let { sale ->
//            val updatedSale = when {
//                // Case 1: Color exists in one of the slots
//                sale.color1IdPicked == colorId -> sale.copy(color1SoldQuantity = quantity)
//                sale.color2IdPicked == colorId -> sale.copy(color2SoldQuantity = quantity)
//                sale.color3IdPicked == colorId -> sale.copy(color3SoldQuantity = quantity)
//                sale.color4IdPicked == colorId -> sale.copy(color4SoldQuantity = quantity)
//
//                // Case 2: Find first empty slot for new color
//                else -> findEmptySlotAndUpdate(sale, colorId, quantity)
//            }
//
//            try {
//                val totalQuantity = updatedSale.calculateTotalQuantity()
//
//                if (totalQuantity == 0) {
//                    deleteSoldArticle(updatedSale.vid)
//                } else {
//                    _currentSaleInWindows.value = updatedSale
//                    // Update UI state to reflect changes
//                    _uiState.upsertLenceCommandeRepoGroupedProtoAvantJuin3 { currentState ->
//                        currentState.copy(
//                            selectedColorId = colorId,
//                            lastUpdatedQuantity = quantity
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.upsertLenceCommandeRepoGroupedProtoAvantJuin3 { it.copy(error = "Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 sale: ${e.message}") }
//            }
//        }
//    }
//}
//
//private fun findEmptySlotAndUpdate(
//    sale: SoldArticlesTabelle,
//    colorId: Long,
//    quantity: Int
//): SoldArticlesTabelle {
//    return when {
//        sale.color1IdPicked == 0L -> sale.copy(
//            color1IdPicked = colorId,
//            color1SoldQuantity = quantity
//        )
//        sale.color2IdPicked == 0L -> sale.copy(
//            color2IdPicked = colorId,
//            color2SoldQuantity = quantity
//        )
//        sale.color3IdPicked == 0L -> sale.copy(
//            color3IdPicked = colorId,
//            color3SoldQuantity = quantity
//        )
//        sale.color4IdPicked == 0L -> sale.copy(
//            color4IdPicked = colorId,
//            color4SoldQuantity = quantity
//        )
//        else -> sale.copy( // Update first slot if no empty slots
//            color1IdPicked = colorId,
//            color1SoldQuantity = quantity
//        )
//    }
//}
//
//private fun SoldArticlesTabelle.calculateTotalQuantity(): Int {
//    return color1SoldQuantity +
//            color2SoldQuantity +
//            color3SoldQuantity +
//            color4SoldQuantity
//}

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


                // Update Room database
                database.soldArticlesModelDao().insert(updatedSale)

                // Update UI state
                _uiState.update { state ->
                    state.copy(
                        soldArticlesModel = state.soldArticlesModel.map { article ->
                            if (article?.vid == updatedSale.vid) updatedSale else article
                        })
                }

                // Update Firebase
                firebaseDatabase.getReference("O_SoldArticlesTabelle")
                    .child(updatedSale.vid.toString()).setValue(updatedSale).await()


            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to reset sale: ${e.message}") }
            }
        }
    }

    fun clearCurrentSale() {
        _currentSaleInWindows.value = null
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
                    database.soldArticlesModelDao().delete(article)

                    // Update the UI state
                    _uiState.update { state ->
                        state.copy(
                            soldArticlesModel = state.soldArticlesModel.filterNotNull().filter {
                                it.vid != vid
                            })
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
                    database.soldArticlesModelDao().insert(sale)

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
                        .child(sale.vid.toString()).setValue(sale).await()

                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to save sale: ${e.message}") }
                }
            }
        }
    }


    fun updateLongAppSetting(name: String, value: Long) {
        viewModelScope.launch {
            try {

                val existingSettings = _uiState.value.appSettingsSaverModel
                val maxId = existingSettings.maxOfOrNull { it.id } ?: 0

                val currentSetting = existingSettings.find { it.name == name }?.copy(
                    valueLong = value, date = Date()
                ) ?: AppSettingsSaverModel(
                    id = maxId + 1, name = name, valueLong = value, date = Date()
                )


                // Update Firebase
                firebaseDatabase.getReference("A_AppSettingsSaverModel")
                    .child(currentSetting.id.toString()).setValue(currentSetting).await()


            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private suspend fun createNewArrivaleCategoryIfNeeded(existingCategories: List<CategoriesTabelle>) {
        val hasNewArrivale = existingCategories.any {
            it.nom == "NewArrivale"
        }

        if (!hasNewArrivale) {
            val maxId = existingCategories.maxOfOrNull {
                it.id
            } ?: 0

            val newArrivaleCategory = CategoriesTabelle(
                id = maxId + 1, nom = "NewArrivale", position = 1, displayedHeader = true
            )

            database.categoriesModelDao().insert(newArrivaleCategory)

        }
    }

    /**EXPO INTIA*/
    fun exportToWarningDataBaseBakup() {
        viewModelScope.launch {
            try {
                setLoading(true)


                // Get current timestamp for the backup
                val timestamp = System.currentTimeMillis()
                val backupRef = firebaseDatabase.getReference("WarningDataBaseBakup/$timestamp")

                // Create addNew backup object with all relevant data
                val backupData = hashMapOf(
                    "articlesBasesStatTables" to _uiState.value.articlesBasesStatTables,
                    "appSettingsSaverModel" to _uiState.value.appSettingsSaverModel,
                    "categories" to _uiState.value.categories,
                    "colorsArticlesTabelleModel" to _uiState.value.colorsArticlesTabelleModel,
                    "soldArticlesModel" to _uiState.value.soldArticlesModel,
                    "suppliers" to _uiState.value.suppliers,
                    "backupTimestamp" to timestamp,
                    "backupDate" to SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                    ).format(Date(timestamp))
                )

                // Update loading progress as we save each collection
                val totalCollections = backupData.size
                var currentCollection = 0

                backupData.forEach { (key, value) ->
                    try {
                        backupRef.child(key).setValue(value).await()
                        currentCollection++
                        // Adjust progress to start from 30% and go to 100%
                        updateLoadingProgress(0.3f + (0.7f * currentCollection.toFloat() / totalCollections))
                    } catch (e: Exception) {
                        _uiState.update { it.copy(error = "Failed to backup $key: ${e.message}") }
                    }
                }

                // Add metadata about the backup
                backupRef.child("metadata").setValue(
                    hashMapOf(
                        "totalCollections" to totalCollections,
                        "backupComplete" to true,
                        "deviceInfo" to Build.MODEL,
                        "appVersion" to BuildConfig.VERSION_NAME,
                        "totalArticlesExported" to _uiState.value.articlesBasesStatTables.size
                    )
                ).await()

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Backup failed: ${e.message}") }
            } finally {
                setLoading(false)
            }
        }
    }

    // Optional: Add addNew separate function if you want to export articles only
    fun exportArticlesBasesStatsTableOnly() {
        viewModelScope.launch {
            try {
                setLoading(true)

                val totalArticles = _uiState.value.articlesBasesStatTables.size
                var currentArticle = 0

                _uiState.value.articlesBasesStatTables.forEach { article ->
                    try {
                        refDBJetPackExport.child(article.id.toString()).setValue(article).await()

                        currentArticle++
                        updateLoadingProgress(currentArticle.toFloat() / totalArticles)
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(error = "Failed to export article ${article.id}: ${e.message}")
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Articles export failed: ${e.message}") }
            } finally {
                setLoading(false)
            }
        }
    }


    private suspend fun diviseurDeDisplayProductForEachClientInit(fl: Float) {
        val snapshot = diviseurDeDisplayProductForEachClientRef.get().await()
        val it = snapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(DiviseurDeDisplayProductForEachClient::class.java)
        }

        database.diviseurDeDisplayProductForEachClientDao().deleteAll()

        database.diviseurDeDisplayProductForEachClientDao().insertAll(it)
        updateLoadingProgress(fl)
    }

    private suspend fun devicesTypeManagerInitialize(fl: Float) {
        val devicesTypeManagerSnapshot = refDevicesTypeManager.get().await()
        val devicesTypeManager = devicesTypeManagerSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(DevicesTypeManager::class.java)
        }

        database.devicesTypeManagerDao().deleteAll()

        database.devicesTypeManagerDao().insertAll(devicesTypeManager)
        updateLoadingProgress(fl)
    }

    private fun setupAppSettingsListener() {
        refAppSettingsSaverModel.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    // Convert Firebase snapshot to Room entities
                    val appSettings = snapshot.children.mapNotNull { childSnapshot ->
                        childSnapshot.getValue(AppSettingsSaverModel::class.java)
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            appSettingsSaverModel = appSettings
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
                _uiState.update { it.copy(error = error.message) }
            }
        })
    }

    private suspend fun clientsInitialize(fl: Float) {
        val clientsSnapshot = refClientsTabelle.get().await()
        val clients = clientsSnapshot.children.mapNotNull { snapshot ->
            snapshot.getValue(HClientInfos::class.java)
        }
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
        database.soldArticlesModelDao().insertAll(soldArticles)
        updateLoadingProgress(fl)
    }


    fun importFromFirebase() {
        viewModelScope.launch {
            try {

                setLoading(true)
                updateLoadingProgress(10f)

                devicesTypeManagerInitialize(17f)

                updateLoadingProgress(20f)


                updateLoadingProgress(70f)

                colorInitialize(80f)
                clientsInitialize(82f)
                soldArticlesTabelleIntia(85f)
                diviseurDeDisplayProductForEachClientInit(85f)


                /*     val articlesSnapshot = refDBJetPackExport.get().await()
                     val articles = articlesSnapshot.children.mapNotNull { snapshot ->
                         snapshot.getValue(ArticlesBasesStatsTable::class.java)
                     }
               //      database.ArticlesBasesStatsModelDao().insertAll(articles)        */
                updateLoadingProgress(100f)

                loadDataCollectOfUiStateFromRoom()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                setLoading(false)
            }
        }
    }

    init {
        viewModelScope.launch {
            if (database.ArticlesBasesStatsModelDao().getAll().size == 0) {
                importFromFirebase()
            }
            loadDataCollectOfUiStateFromRoom()
        }
        observeConnectionState()
        setupMaxPriceObserver()
    }

    fun updateLoadingProgress(progress: Float) {
        _uiState.update { it.copy(loadingProgress = progress) }
    }

    private fun collectDatasAuUiStateMasterRepositorysProtoJuin3() {
        viewModelScope.launch {
            a_MasterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    // Update the UI state with the progress from the master model
                    _uiState.value = _uiState.value.copy(
                        articlesBasesStatTables = model.repoStateA_ProduitInfos?.modelListFlow
                            ?: emptyList(),
                        categories = model.repoStateC_CategorieProduitInfos?.modelListFlow
                            ?: emptyList(),
                        loadingProgress = model.progress
                    )

                    // Optional: Set loading to false when progress reaches 100%
                    if (model.progress >= 1.0f) {
                        setLoading(false)
                    }
                }
            }
        }
    }

    private suspend fun loadDataCollectOfUiStateFromRoom() {
        try {
            collectDatasAuUiStateMasterRepositorysProtoJuin3()

            setLoading(true)
            var progress = 0f

            while (progress < 100f) {
                progress += 10f
                updateLoadingProgress(progress)
                delay(100)
            }
            setupAppSettingsListener()

            val colors = database.colorsArticlesDao().getAllOrdred()
            val soldArticles = database.soldArticlesModelDao().getAll()
            val devicesTypeManager = database.devicesTypeManagerDao().getAll()

            val diviseurDeDisplayProductForEachClient =
                database.diviseurDeDisplayProductForEachClientDao().getAll()



            _uiState.update {
                it.copy(
                    colorsArticlesTabelleModel = colors,
                    soldArticlesModel = soldArticles,
                    devicesTypeManager = devicesTypeManager,

                    diviseurDeDisplayProductForEachClient = diviseurDeDisplayProductForEachClient,

                    )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        } finally {
            setLoading(false)
        }
    }

}

// Update the price mapping to include client ID
data class PriceRecord(
    val price: Double, val clientId: Long, val date: Long
)
