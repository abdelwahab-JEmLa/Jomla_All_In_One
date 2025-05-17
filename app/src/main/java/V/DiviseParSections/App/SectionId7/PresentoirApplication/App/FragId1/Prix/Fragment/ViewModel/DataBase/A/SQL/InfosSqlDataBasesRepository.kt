package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.testDatasDataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Module.FireBase.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Module.FireBase.getDataFromFirebase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Module.FireBase.startNeedUpdateListener
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Module.FireBase.stopNeedUpdateListener
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Module.SQl.RoomOperationsHandler
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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
    private val fireBaseOperationsHandler: FireBaseOperationsHandler,
    private val roomOperationsHandler: RoomOperationsHandler
) {
    private val TAG = "InfosSqlRepo"
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
            verifierRoomEstEmptyInsertAllEtUiAprestartNeedUpdateListener()
            verifieFireBaseEstVide()
            collectRoom()
            fireBaseOperationsHandler.startNeedUpdateListener()
        }
    }

    private fun verifieFireBaseEstVide() {
        coroutineScope.launch {
            try {
                val isEmpty = fireBaseOperationsHandler.isDatabaseEmptyAsync()
                if (isEmpty) {
                    val testData = testDatasDataBasesInfosSql()
                    add(testData) {}
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun verifierRoomEstEmptyInsertAllEtUiAprestartNeedUpdateListener() {
        coroutineScope.launch {
            try {
                val isEmpty = roomOperationsHandler.isDatabaseEmpty()
                if (isEmpty) {
                    val firebaseData = fireBaseOperationsHandler.getDataFromFirebase()
                    if (firebaseData != null &&
                        (firebaseData.a_ProduitInfos.isNotEmpty() ||
                                firebaseData.b_ClientInfos.isNotEmpty() ||
                                firebaseData.c_TypeTarificationInfos.isNotEmpty() ||
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
                        a_ProduitInfos = produits.toMutableList(),
                        b_ClientInfos = clients.toMutableList(),
                        c_TypeTarificationInfos = typeTarifications.toMutableList(),
                        d_TarificationInfos = tarifications.toMutableList()
                    )
                )
            }.collect { combinedData ->
                modelList = combinedData
            }
        }
    }

    suspend fun add(
        data: DataBasesInfosSql
    ) {
        withContext(Dispatchers.IO) {
            try {
                roomOperationsHandler.insertAll(data)
                setToFireBase(data)
                collectLatestData()
            } catch (e: Exception) {
            }
        }
    }

    fun add(
        data: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        coroutineScope.launch {
            try {
                add(data)
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
