package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A0_DataBasesGroup
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Test.testD_TarificationInfosT2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.A_ProduitInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class E_GroupedDataBasesRepository(
    val database: AppDatabase,
    private val fireBase: F_FireBaseOperationsHandler,
    private val room: F_RoomOperationsHandler
) {
    companion object {
        const val TAG = "E_GroupedDataBasesRepository"
    }

    private val repoCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val _modelListFlow = MutableStateFlow<List<A0_DataBasesGroup>>(emptyList())
    private var modelList: List<A0_DataBasesGroup>
        get() = _modelListFlow.value
        set(value) {
            _modelListFlow.value = value
        }

    val modelListFlow: StateFlow<List<A0_DataBasesGroup>> = _modelListFlow.asStateFlow()
    val mainProgressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    init {
        initializeTarificationInfosDatabase()
        initializeProduitInfosDatabase()
    }

    private fun initializeTarificationInfosDatabase() {
        try {
            repoCoroutineScope.launch(Dispatchers.IO) {
                try {
                    initializeTarificationInfos()
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing TarificationInfos database: ${e.message}", e)
                    updateProgress(1f)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in initializeTarificationInfosDatabase: ${e.message}", e)
            updateProgress(1f)
        }
    }

    private fun initializeProduitInfosDatabase() {
        try {
            repoCoroutineScope.launch(Dispatchers.IO) {
                try {
                    initializeProduitInfos()
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing ProduitInfos database: ${e.message}", e)
                    updateProgress(1f)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in initializeProduitInfosDatabase: ${e.message}", e)
            updateProgress(1f)
        }
    }

    private suspend fun initializeTarificationInfos() {
        updateProgress(0f)

        fireBase.getDataFromFirebase { dataList ->
            repoCoroutineScope.launch {
                if (dataList.isEmpty()) {
                    upsertAllRoomEtFireBase(testD_TarificationInfosT2())
                } else {
                    room.checkDataBaseIsEmpty { roomHandler ->
                        repoCoroutineScope.launch {
                            roomHandler.insertAllAndReturnListIdToData(dataList) { resultMap ->
                                Log.d(TAG, "Inserted ${resultMap.size} tarification records from Firebase")
                                updateProgress(1f)
                            }
                        }
                    }
                }
            }
        }

        collectRoom()
    }

    private suspend fun initializeProduitInfos() {
        updateProgress(0f)

        try {
            val existingProducts = getProduitInfosFromFirebase()

            if (existingProducts.isEmpty()) {
                Log.d(TAG, "No product data found in Firebase")
            } else {

            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ProduitInfos: ${e.message}", e)
        }

        collectRoom()
        updateProgress(1f)
    }

    private suspend fun getProduitInfosFromFirebase(): List<A_ProduitInfos> {
        return try {
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products from Firebase: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun insertProduitInfosToRoom(products: List<A_ProduitInfos>) {
        try {
            database.a_ProduitInfosDao().insertAll(products)
            Log.d(TAG, "Inserted ${products.size} products into Room database")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting products to Room: ${e.message}", e)
        }
    }

    fun <DataBase> initWithInstance(
        dataBase: DataBase,
        onSuccess: (Long) -> Unit = { }
    ) {
        try {
            repoCoroutineScope.launch(Dispatchers.IO) {
                try {
                    when (dataBase) {
                        is D_TarificationInfos -> {
                            upsertAllRoomEtFireBase(listOf(dataBase)) { resultMap ->
                                val firstId = resultMap.keys.firstOrNull() ?: -1L
                                onSuccess(firstId)
                            }
                        }
                        is A_ProduitInfos -> {
                            handleSingleProduitInfosInstance(dataBase, onSuccess)
                        }

                        else -> {
                            onSuccess(-1L)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error upserting data: ${e.message}", e)
                    Log.e(TAG, "Data: $dataBase")
                    onSuccess(-1L)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in initWithInstance: ${e.message}", e)
            onSuccess(-1L)
        }
    }

    private suspend fun handleSingleProduitInfosInstance(
        produitInfo: A_ProduitInfos,
        onSuccess: (Long) -> Unit
    ) {
        try {
        } catch (e: Exception) {
            Log.e(TAG, "Error handling single A_ProduitInfos instance: ${e.message}", e)
            onSuccess(-1L)
        }
    }

    private fun updateProgress(progress: Float) {
        mainProgressRepo.value = progress.coerceIn(0f, 1f)
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        try {
            repoCoroutineScope.launch(Dispatchers.IO) {
                try {
                    updateProgress(0.5f)

                    room.insertAllAndReturnListIdToData(dataList) { mapData ->
                        repoCoroutineScope.launch {
                            updateProgress(0.7f)

                            try {
                                fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                                    repoCoroutineScope.launch {
                                        updateProgress(1f)
                                        onAddSuccess(mapData)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Firebase upsert failed, but Room insert succeeded", e)
                                onAddSuccess(mapData)
                                updateProgress(1f)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Room insert failed", e)
                    updateProgress(1f)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in upsertAllRoomEtFireBase", e)
            updateProgress(1f)
        }
    }

    fun cleanup() {
        fireBase.stopNeedUpdateListener()
    }

    private fun collectRoom() {
        repoCoroutineScope.launch {
            val produitsFlow = database.a_ProduitInfosDao().getAllProduits()
            val tarificationsFlow = database.dTarificationInfosDao().getAllTarifications()

            combine(
                produitsFlow,
                tarificationsFlow
            ) { produits,  tarifications ->
                listOf(
                    A0_DataBasesGroup(
                        a_ProduitInfos = produits.toMutableList(),
                        d_TarificationInfos = tarifications.toMutableList()
                    )
                )
            }.collect { combinedData ->
                modelList = combinedData
                Log.d(TAG, "Updated model list with ${combinedData.firstOrNull()?.d_TarificationInfos?.size ?: 0} tarifications")
            }
        }
    }

    fun getTarificationInfos(): List<D_TarificationInfos> {
        return modelList.firstOrNull()?.d_TarificationInfos ?: emptyList()
    }

    fun getProduitInfos(): List<A_ProduitInfos> {
        return modelList.firstOrNull()?.a_ProduitInfos ?: emptyList()
    }

    fun reinitializeTarificationInfosDatabase() {
        Log.d(TAG, "Reinitializing TarificationInfos database")
        initializeTarificationInfosDatabase()
    }

    fun reinitializeProduitInfosDatabase() {
        Log.d(TAG, "Reinitializing ProduitInfos database")
        initializeProduitInfosDatabase()
    }

    fun reinitializeAllDatabases() {
        Log.d(TAG, "Reinitializing all databases")
        reinitializeTarificationInfosDatabase()
        reinitializeProduitInfosDatabase()
    }
}
