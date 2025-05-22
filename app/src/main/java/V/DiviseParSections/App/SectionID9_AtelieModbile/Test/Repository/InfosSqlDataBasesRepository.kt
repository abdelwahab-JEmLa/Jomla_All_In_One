package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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

    val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val _roomProgress = MutableStateFlow(0f)
    private val _firebaseProgress = MutableStateFlow(0f)
    val roomProgress: StateFlow<Float> = _roomProgress.asStateFlow()
    val firebaseProgress: StateFlow<Float> = _firebaseProgress.asStateFlow()

    init {
        coroutineScope.launch {
            combine(roomProgress, firebaseProgress) { roomProg, firebaseProg ->
                (roomProg + firebaseProg) / 2f
            }.collect { combinedProgress ->
                progressRepo.value = combinedProgress
            }
        }

        coroutineScope.launch {
            updateProgress(0f)
            fireBase.getDataFromFirebase { dataList ->
                if (dataList.isEmpty()) {
                    upsertAllRoomEtFireBase(testD_TarificationInfosT2())
                } else {
                    coroutineScope.launch {
                        room.upsertAllAndReturnListIdToData(dataList) {
                            coroutineScope.launch {
                                updateRoomProgress(1f)
                                updateProgress(0.7f)
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
        progressRepo.value = progress.coerceIn(0f, 1f)
    }

    private fun updateRoomProgress(progress: Float) {
        _roomProgress.value = progress.coerceIn(0f, 1f)
    }

    private fun updateFirebaseProgress(progress: Float) {
        _firebaseProgress.value = progress.coerceIn(0f, 1f)
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        try {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    updateProgress(0.5f)
                    updateRoomProgress(0f)

                    room.upsertAllAndReturnListIdToData(dataList) { mapData ->
                        coroutineScope.launch {
                            updateRoomProgress(1f)
                            updateProgress(0.7f)

                            try {
                                updateFirebaseProgress(0f)

                                fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                                    coroutineScope.launch {
                                        updateFirebaseProgress(1f)
                                        updateProgress(1f)
                                        onAddSuccess(mapData)
                                    }
                                }
                            } catch (e: Exception) {
                                updateFirebaseProgress(0f)
                                updateProgress(0.7f)
                                onAddSuccess(mapData)
                            }
                        }
                    }
                } catch (e: Exception) {
                    updateRoomProgress(0f)
                    updateProgress(0.5f)
                }
            }
        } catch (e: Exception) {
            updateProgress(0f)
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
