package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.getDataFromFirebase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.startNeedUpdateListener
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.stopNeedUpdateListener
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.SQl.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class InfosSqlDataBasesRepository(
    val database: AppDatabase,
    private val fireBaseOperationsHandler: FireBaseOperationsHandler,
    private val roomOperationsHandler: RoomOperationsHandler
) {
    private val TAG = "InfosSqlRepo"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private val _modelListFlow = MutableStateFlow<List<DataBasesInfosSql>>(emptyList())
    private var modelList: List<DataBasesInfosSql>
        get() = _modelListFlow.value
        set(value) {
            _modelListFlow.value = value
        }

    val modelListFlow: StateFlow<List<DataBasesInfosSql>> = _modelListFlow.asStateFlow()

    init {
        coroutineScope.launch {
            //  verifieFireBaseEstVide()

            verifierRoomEstEmptyInsertAllEtUiAprestartNeedUpdateListener()
            collectRoom()
            fireBaseOperationsHandler.startNeedUpdateListener()
        }
    }

   /* private fun verifieAddFireBaseEstVide() {
        coroutineScope.launch {
            try {
                val isEmpty = fireBaseOperationsHandler.isDatabaseEmptyAsync()
                if (isEmpty) {
                    val testData = testDatasDataBasesInfosSql()
                    upsert(testData) {}
                }
            } catch (e: Exception) {
            }
        }
    }        */

    private fun verifierRoomEstEmptyInsertAllEtUiAprestartNeedUpdateListener() {
        coroutineScope.launch {
            try {
                val isEmpty = roomOperationsHandler.isDatabaseEmpty()
                if (isEmpty) {
                    val firebaseData = fireBaseOperationsHandler.getDataFromFirebase()
                    if (firebaseData != null &&
                        (
                                firebaseData.d_TarificationInfos.isNotEmpty())
                    ) {
                        roomOperationsHandler.insertAll(firebaseData)
                        collectLatestData()
                    }
                }
            } catch (e: Exception) {
            }
        }
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
                setToFireBase(data)
                collectLatestData()
            } catch (_: Exception) {}
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
        }
    }

    suspend fun deleteAllRoom() {
        withContext(Dispatchers.IO) {
            try {
                roomOperationsHandler.deleteAll()
                collectLatestData()
            } catch (e: Exception) {
            }
        }
    }

    private fun setToFireBase(
        dataBasesInfosSql: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        try {
            fireBaseOperationsHandler.addToFirebaseAsync(dataBasesInfosSql) {
                onSuccess()
            }
        } catch (e: Exception) {
        }
    }
}
