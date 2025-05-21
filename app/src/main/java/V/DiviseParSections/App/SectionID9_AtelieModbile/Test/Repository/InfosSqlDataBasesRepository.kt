package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfosSqlDataBasesRepository(
    val database: AppDatabase,
    private val fireBaseOperationsHandler: FireBaseOperationsHandler,
    private val roomOperationsHandler: RoomOperationsHandler
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _modelListFlow = MutableStateFlow<List<DataBasesInfosSql>>(emptyList())
    private var modelList: List<DataBasesInfosSql>
        get() = _modelListFlow.value
        set(value) {
            _modelListFlow.value = value
        }

    val modelListFlow: StateFlow<List<DataBasesInfosSql>> = _modelListFlow.asStateFlow()

    init {
        coroutineScope.launch {
            val isEmpty = fireBaseOperationsHandler.isDatabaseEmptyAsync {
                coroutineScope.launch {
                    upsertAllAndReturnListIdToData(testD_TarificationInfosT2()) { mapData ->
                        Log.d("Repository", "Added with keys: ${mapData.keys}")
                    }
                    delay(1000)
                }
            }

            if (!isEmpty) {
                fireBaseOperationsHandler.getDataFromFirebase() { tarificationsList ->
                    upsertAllAndReturnListIdToData(tarificationsList) { mapData ->
                        // Log all IDs from the returned map
                        val idsStr = mapData.keys.joinToString(", ")
                        Log.d(
                            "Repository",
                            "Successfully processed tarification IDs from Firebase: $idsStr"
                        )

                        // If you need additional details about each tarification
                        mapData.forEach { (id, tarif) ->
                            Log.d(
                                "Repository",
                                "Tarification $id: Name=${tarif.nom}, Price=${tarif.prixCurrency}, Type=${tarif.typeTarificationEnumT2Correspond}"
                            )
                        }
                    }
                }
            }

            collectRoom()
            fireBaseOperationsHandler.startNeedUpdateListener()
        }
    }

    private fun upsertAllAndReturnListIdToData(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        try {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    roomOperationsHandler.upsertAllAndReturnListIdToData(dataList) { mapData ->
                        coroutineScope.launch {
                            try {
                                fireBaseOperationsHandler.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                                    Log.d(
                                        "Repository",
                                        "Firebase entries created with keys: ${firebaseMap.keys}"
                                    )
                                    onAddSuccess(mapData)
                                }
                            } catch (e: Exception) {
                                Log.e("Repository", "Error in Firebase upsert: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Repository", "Error in Room upsert: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error launching coroutine: ${e.message}")
        }
    }

    private fun deleteTarificationInfosNodeFromFirebase() {
        fireBaseOperationsHandler.deleteTarificationInfosNode()
    }

    fun cleanup() {
        fireBaseOperationsHandler.stopNeedUpdateListener()
    }

    private fun collectRoom() {
        coroutineScope.launch {
            val produitsFlow = database.a_ProduitInfosDao().getAllProduits()
            val clientsFlow = database.b_ClientInfosDao().getAllClients()
            val typeTarificationsFlow =
                database.c_TypeTarificationInfosDao().getAllTypeTarifications()
            val tarificationsFlow = database.dTarificationInfosDao().getAllTarifications()

            combine(
                produitsFlow,
                clientsFlow,
                typeTarificationsFlow,
                tarificationsFlow
            ) { produits, clients, typeTarifications, tarifications ->
                listOf(
                    DataBasesInfosSql(
                        c_TypeTarificationInfos = typeTarifications.toMutableList()
                    )
                )
            }.collect { combinedData ->
                modelList = combinedData
            }
        }
    }

    suspend fun upsert(
        data: DataBasesInfosSql
    ) {
        withContext(Dispatchers.IO) {
            try {
                roomOperationsHandler.insertAll(data)
                collectLatestData()
            } catch (e: Exception) {
                Log.e("Repository", "Error in upsert: ${e.message}")
            }
        }
    }

    fun upsert(
        data: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        coroutineScope.launch {
            try {
                upsert(data)
                onSuccess()
            } catch (e: Exception) {
                Log.e("Repository", "Error in upsert with callback: ${e.message}")
            }
        }
    }

    private suspend fun collectLatestData(
        onSuccess: () -> Unit = {}
    ) {
        try {
            val data = roomOperationsHandler.getAllData()
            modelList = listOf(data)
            onSuccess()
        } catch (e: Exception) {
            Log.e("Repository", "Error in collectLatestData: ${e.message}")
        }
    }

    suspend fun deleteAllRoom() {
        withContext(Dispatchers.IO) {
            try {
                roomOperationsHandler.deleteAll()
                collectLatestData()
            } catch (e: Exception) {
                Log.e("Repository", "Error in deleteAllRoom: ${e.message}")
            }
        }
    }
}

