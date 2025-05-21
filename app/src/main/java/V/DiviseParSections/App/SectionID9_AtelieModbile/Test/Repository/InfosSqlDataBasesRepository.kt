package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfosSqlDataBasesRepository(
    val database: AppDatabase,
    private val fireBase: FireBaseOperationsHandler,
    private val room: RoomOperationsHandler
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
            fireBase.isDatabaseEmpty {
                coroutineScope.launch {
                    val dataTestList: List<D_TarificationInfos> =
                        fireBase.getDataFromFirebase {
                            it.ifEmpty {
                                testD_TarificationInfosT2()
                            }
                        }?.d_TarificationInfos ?: testD_TarificationInfosT2()

                    upsertAllRoomEtFireBase(dataTestList) { mapData ->
                        Log.d(
                            "Repository", "Added with keys: ${mapData.keys}"
                        )
                    }
                }
            }

            collectRoom()
            fireBase.startNeedUpdateListener()
        }
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        try {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    room.upsertAllAndReturnListIdToData(dataList) { mapData ->
                        coroutineScope.launch {
                            try {
                                fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
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
        fireBase.deleteTarificationInfosNode()
    }

    fun cleanup() {
        fireBase.stopNeedUpdateListener()
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
                room.insertAll(data)
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
            val data = room.getAllData()
            modelList = listOf(data)
            onSuccess()
        } catch (e: Exception) {
            Log.e("Repository", "Error in collectLatestData: ${e.message}")
        }
    }

    suspend fun deleteAllRoom() {
        withContext(Dispatchers.IO) {
            try {
                room.deleteAll()
                collectLatestData()
            } catch (e: Exception) {
                Log.e("Repository", "Error in deleteAllRoom: ${e.message}")
            }
        }
    }
}
