package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class E_InfosSqlDataBasesRepository(
    val database: AppDatabase,
    private val fireBase: F_FireBaseOperationsHandler,
    private val room: F_RoomOperationsHandler
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _modelListFlow = MutableStateFlow<List<DataBasesInfosSql>>(emptyList())
    private var modelList: List<DataBasesInfosSql>
        get() = _modelListFlow.value
        set(value) {
            _modelListFlow.value = value
        }

    val modelListFlow: StateFlow<List<DataBasesInfosSql>> = _modelListFlow.asStateFlow()

    val mainProgressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    init {
        coroutineScope.launch {
            updateProgress(0f)
            fireBase.getDataFromFirebase { dataList ->
                if (dataList.isEmpty()) {
                    upsertAllRoomEtFireBase(testD_TarificationInfosT2())
                } else {
                    coroutineScope.launch {
                        room.checkDataBaseIsEmpty { roomHandler ->
                            coroutineScope.launch {
                                roomHandler.insertAllAndReturnListIdToData(dataList) {
                                    updateProgress(1f)
                                }
                            }
                        }
                    }
                }
            }

            collectRoom()
            fireBase.startNeedUpdateListener()

            updateProgress(1f)
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
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    updateProgress(0.5f)

                    room.insertAllAndReturnListIdToData(dataList) { mapData ->
                        coroutineScope.launch {
                            updateProgress(0.7f)

                            try {
                                fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                                    coroutineScope.launch {
                                        updateProgress(1f)
                                        onAddSuccess(mapData)
                                    }
                                }
                            } catch (e: Exception) {
                                onAddSuccess(mapData)
                                updateProgress(1f)
                            }
                        }
                    }
                } catch (e: Exception) {
                    updateProgress(1f)
                }
            }
        } catch (e: Exception) {
            updateProgress(1f)
        }
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
                        a_ProduitInfos = produits.toMutableList(),
                        c_TypeTarificationInfos = typeTarifications.toMutableList(),
                        d_TarificationInfos = tarifications.toMutableList()
                    )
                )
            }.collect { combinedData ->
                modelList = combinedData
            }
        }
    }
}
