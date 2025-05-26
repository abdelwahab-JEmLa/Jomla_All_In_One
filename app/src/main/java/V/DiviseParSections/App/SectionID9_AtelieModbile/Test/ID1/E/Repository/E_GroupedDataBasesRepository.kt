package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A0_DataBasesGroup
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Test.testD_TarificationInfosT2
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
    private val fireBase: F0_FireBaseOperationsHandler,
    private val room: G_RoomOperationsHandler
) {
    companion object {
        private const val TAG = "GroupedDataBasesRepo"
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
        initializeDatabase<D_TarificationInfos>()
    }

    private inline fun <reified DataBase : Any> initializeDatabase() {
        repoCoroutineScope.launch(Dispatchers.IO) {
            try {
                when (DataBase::class) {
                    D_TarificationInfos::class -> {
                        initializeTarificationInfos()
                    }
                    A_ProduitInfos::class -> {
                        initializeProduitInfos()
                    }
                    else -> {
                        updateProgress(1f)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing database", e)
                updateProgress(0f)
            }
        }
    }

    private fun initializeTarificationInfos() {
        updateProgress(0f)
        Log.d(TAG, "Starting initialization of TarificationInfos")

        fireBase.getDataFromFirebase { tariffDataList, produitInfoList ->
            repoCoroutineScope.launch {
                try {
                    if (tariffDataList.isEmpty()) {
                        Log.d(TAG, "No tariff data found, inserting test data")
                        val testData = testD_TarificationInfosT2()
                        upsertAllRoomEtFireBase(testData)
                    } else {
                        Log.d(TAG, "Found ${tariffDataList.size} tariff items from Firebase")
                        room.checkDataBaseIsEmpty { roomHandler ->
                            repoCoroutineScope.launch {
                                roomHandler.insertAllAndReturnListIdToData(tariffDataList) {
                                    updateProgress(0.5f)
                                }
                            }
                        }
                    }

                    val migrateOldData = false
                    if (migrateOldData) {
                        try {
                            Log.d(TAG, "Starting migration of old data")
                            fireBase.deleteRef<A_ProduitInfos>()

                            val (originalCount, processedMap) = fireBase.getAncienDB_changeKeysFireBase()

                            Log.d(TAG, "Migration completed: $originalCount original items, ${processedMap.size} processed items")
                        } catch (migrationError: Exception) {
                            Log.e(TAG, "Migration failed", migrationError)
                        }
                    }

                    // Check if Room database is empty for products and handle accordingly
                    val isRoomEmpty = !room.inlineCheckDataBaseIsNotEmpty<A_ProduitInfos>()
                    val hasFirebaseProducts = produitInfoList.isNotEmpty()

                    Log.d(TAG, "Room empty for products: $isRoomEmpty, Firebase has products: $hasFirebaseProducts")

                    if (isRoomEmpty && hasFirebaseProducts) {
                        Log.d(TAG, "Inserting ${produitInfoList.size} products from Firebase to Room")
                        val insertResult = room.insertAllAndReturnListIdToDataInline<A_ProduitInfos>(produitInfoList)

                        Log.d(TAG, "Product insertion completed: ${insertResult.size} products inserted")
                        if (insertResult.isNotEmpty()) {
                            Log.d(TAG, "Successfully inserted products with IDs: ${insertResult.keys.take(5)}...")
                        }

                        updateProgress(0.8f)
                    } else {
                        if (!isRoomEmpty) {
                            Log.d(TAG, "Room already contains product data, skipping insertion")
                        }
                        if (!hasFirebaseProducts) {
                            Log.d(TAG, "No product data available from Firebase")
                        }
                        updateProgress(0.8f)
                    }

                    initializeDatabase<A_ProduitInfos>()

                } catch (e: Exception) {
                    Log.e(TAG, "Error in initializeTarificationInfos", e)
                    updateProgress(0f)
                }
            }
        }

        collectRoom()
    }

    private fun initializeProduitInfos() {
        Log.d(TAG, "Initializing ProduitInfos")
        updateProgress(0.9f)
        collectRoom()
        updateProgress(1f)
        Log.d(TAG, "ProduitInfos initialization completed")
    }

    private fun updateProgress(progress: Float) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        mainProgressRepo.value = clampedProgress
        Log.v(TAG, "Progress updated to: ${(clampedProgress * 100).toInt()}%")
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        repoCoroutineScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting upsert operation for ${dataList.size} items")
                updateProgress(0.1f)

                room.insertAllAndReturnListIdToData(dataList) { mapData ->
                    repoCoroutineScope.launch {
                        updateProgress(0.5f)

                        if (mapData.isNotEmpty()) {
                            Log.d(TAG, "Upserting ${mapData.size} items to Firebase")
                            fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                                repoCoroutineScope.launch {
                                    updateProgress(1f)
                                    Log.d(TAG, "Upsert operation completed successfully")
                                    onAddSuccess(mapData)
                                }
                            }
                        } else {
                            Log.w(TAG, "No data to upsert to Firebase")
                            updateProgress(1f)
                            onAddSuccess(emptyMap())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in upsert operation", e)
                updateProgress(0f)
                onAddSuccess(emptyMap())
            }
        }
    }

    private fun collectRoom() {
        repoCoroutineScope.launch {
            try {
                Log.d(TAG, "Starting Room data collection")
                val produitsFlow = database.a_ProduitInfosDao().getAllProduits()
                val tarificationsFlow = database.dTarificationInfosDao().getAllTarifications()

                combine(
                    produitsFlow,
                    tarificationsFlow
                ) { produits: List<A_ProduitInfos>, tarifications: List<D_TarificationInfos> ->
                    Log.v(TAG, "Collected ${produits.size} products and ${tarifications.size} tarifications")
                    listOf(
                        A0_DataBasesGroup(
                            a_ProduitInfos = produits.toMutableList(),
                            d_TarificationInfos = tarifications.toMutableList()
                        )
                    )
                }.collect { combinedData ->
                    modelList = combinedData
                    Log.d(TAG, "Model list updated with combined data")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error collecting Room data", e)
            }
        }
    }
}
