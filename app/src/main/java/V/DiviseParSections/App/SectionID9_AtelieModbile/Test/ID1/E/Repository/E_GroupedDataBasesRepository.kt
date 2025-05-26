package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A0_DataBasesGroup
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Test.testD_TarificationInfosT2
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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
        println("DEBUG: Repository initialization started")
        // Start with TarificationInfos first as it's the primary database
        initializeDatabase<D_TarificationInfos>()
    }

    private inline fun <reified DataBase : Any> initializeDatabase() {
        repoCoroutineScope.launch(Dispatchers.IO) {
            try {
                println("DEBUG: Initializing database for ${DataBase::class.simpleName}")
                when (DataBase::class) {
                    D_TarificationInfos::class -> {
                        initializeTarificationInfos()
                    }
                    A_ProduitInfos::class -> {
                        initializeProduitInfos()
                    }
                    else -> {
                        println("WARNING: Unknown database type: ${DataBase::class.simpleName}")
                        updateProgress(1f)
                    }
                }
            } catch (e: Exception) {
                println("ERROR: Failed to initialize database ${DataBase::class.simpleName}: ${e.message}")
                e.printStackTrace()
                updateProgress(0f)
            }
        }
    }

    private fun initializeTarificationInfos() {
        println("DEBUG: Starting TarificationInfos initialization")
        updateProgress(0f)

        // Verify Firebase reference before proceeding
        fireBase.verifyDatabaseStructure { structure ->
            println("DEBUG: Firebase structure: $structure")
        }

        fireBase.getDataFromFirebase { tariffDataList, produitInfoList ->
            repoCoroutineScope.launch {
                try {
                    println("DEBUG: Retrieved from Firebase - Tariffs: ${tariffDataList.size}, Products: ${produitInfoList.size}")

                    if (tariffDataList.isEmpty()) {
                        println("DEBUG: No tariff data found, inserting test data")
                        val testData = testD_TarificationInfosT2()
                        println("DEBUG: Test data size: ${testData.size}")
                        upsertAllRoomEtFireBase(testData)
                    } else {
                        println("DEBUG: Found ${tariffDataList.size} tariff records, checking Room database")
                        room.checkDataBaseIsEmpty { roomHandler ->
                            repoCoroutineScope.launch {
                                println("DEBUG: Room database is empty, inserting tariff data")
                                roomHandler.insertAllAndReturnListIdToData(tariffDataList) {
                                    println("DEBUG: Tariff data inserted to Room successfully")
                                    updateProgress(0.5f)
                                }
                            }
                        }
                    }

                    // Handle legacy data migration
                    val migrateOldData = true // Set this based on your needs
                    if (migrateOldData) {
                        println("DEBUG: Starting legacy data migration")
                        try {
                            val (originalCount, processedMap) = fireBase.getAncienDB_changeKeysFireBase()
                            println("DEBUG: Legacy migration completed - Original: $originalCount, Processed: ${processedMap.size}")
                        } catch (migrationError: Exception) {
                            println("ERROR: Legacy migration failed: ${migrationError.message}")
                            migrationError.printStackTrace()
                        }
                    }

                    // Handle ProduitInfos
                    if (!room.inlineCheckDataBaseIsNotEmpty<A_ProduitInfos>() && produitInfoList.isNotEmpty()) {
                        println("DEBUG: Inserting ${produitInfoList.size} product records to Room")
                        val insertResult = room.insertAllAndReturnListIdToDataInline<A_ProduitInfos>(produitInfoList)
                        println("DEBUG: Product insertion completed, result size: ${insertResult.size}")
                        updateProgress(0.8f)
                    } else if (room.inlineCheckDataBaseIsNotEmpty<A_ProduitInfos>()) {
                        println("DEBUG: ProduitInfos already exists in Room")
                        updateProgress(0.8f)
                    } else {
                        println("DEBUG: No product data to insert")
                        updateProgress(0.8f)
                    }

                    // Initialize ProduitInfos database after tarification is complete
                    initializeDatabase<A_ProduitInfos>()

                } catch (e: Exception) {
                    println("ERROR: Exception during TarificationInfos initialization: ${e.message}")
                    e.printStackTrace()
                    updateProgress(0f)
                }
            }
        }

        // Start collecting Room data
        collectRoom()
    }

    private fun initializeProduitInfos() {
        println("DEBUG: Starting ProduitInfos initialization")
        updateProgress(0.9f)

        // ProduitInfos initialization is simpler as it's handled in TarificationInfos
        // Just ensure Room collection is active
        collectRoom()
        updateProgress(1f)

        println("DEBUG: ProduitInfos initialization completed")
    }

    private fun updateProgress(progress: Float) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        mainProgressRepo.value = clampedProgress
        println("DEBUG: Progress updated to: ${(clampedProgress * 100).toInt()}%")
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        println("DEBUG: Starting Room and Firebase upsert with ${dataList.size} items")
        repoCoroutineScope.launch(Dispatchers.IO) {
            try {
                updateProgress(0.1f)

                room.insertAllAndReturnListIdToData(dataList) { mapData ->
                    println("DEBUG: Room insertion completed, proceeding to Firebase")
                    repoCoroutineScope.launch {
                        updateProgress(0.5f)

                        if (mapData.isNotEmpty()) {
                            fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                                repoCoroutineScope.launch {
                                    println("DEBUG: Firebase insertion completed successfully")
                                    updateProgress(1f)
                                    onAddSuccess(mapData)
                                }
                            }
                        } else {
                            println("WARNING: No data returned from Room insertion")
                            updateProgress(1f)
                            onAddSuccess(emptyMap())
                        }
                    }
                }
            } catch (e: Exception) {
                println("ERROR: Exception during upsert: ${e.message}")
                e.printStackTrace()
                updateProgress(0f)
                onAddSuccess(emptyMap())
            }
        }
    }

    private fun collectRoom() {
        println("DEBUG: Starting Room data collection")
        repoCoroutineScope.launch {
            try {
                val produitsFlow = database.a_ProduitInfosDao().getAllProduits()
                val tarificationsFlow = database.dTarificationInfosDao().getAllTarifications()

                combine(
                    produitsFlow,
                    tarificationsFlow
                ) { produits: List<A_ProduitInfos>, tarifications: List<D_TarificationInfos> ->
                    println("DEBUG: Room data collected - Products: ${produits.size}, Tarifications: ${tarifications.size}")
                    listOf(
                        A0_DataBasesGroup(
                            a_ProduitInfos = produits.toMutableList(),
                            d_TarificationInfos = tarifications.toMutableList()
                        )
                    )
                }.collect { combinedData ->
                    println("DEBUG: Combined data updated with ${combinedData.size} groups")
                    modelList = combinedData
                }
            } catch (e: Exception) {
                println("ERROR: Exception during Room collection: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
