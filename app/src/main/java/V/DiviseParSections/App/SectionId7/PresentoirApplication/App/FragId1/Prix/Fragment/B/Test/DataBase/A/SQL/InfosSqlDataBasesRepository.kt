package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.FireBaseHandler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.getDataFromFirebase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.startNeedUpdateListener
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.stopNeedUpdateListener
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.testDatasDataBasesInfosSql
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
    private val fireBaseHandler: FireBaseHandler,
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
            fireBaseHandler.startNeedUpdateListener()
        }
    }

    private fun verifieFireBaseEstVide() {
        coroutineScope.launch {
            try {
                val isEmpty = fireBaseHandler.isDatabaseEmptyAsync()
                if (isEmpty) {
                    addTestDataAuFireBaseEtRoomEtUiAprestartNeedUpdateListener()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun addTestDataAuFireBaseEtRoomEtUiAprestartNeedUpdateListener() {
        coroutineScope.launch {
            try {
                val testData = testDatasDataBasesInfosSql()
                add(testData) {
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun verifierRoomEstEmptyInsertAllEtUiAprestartNeedUpdateListener() {
        coroutineScope.launch {
            try {
                val produits = database.a_ProduitInfosDao().getAllProduitsSync()
                val clients = database.b_ClientInfosDao().getAllClientsSync()
                val typeTarifications = database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
                val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()

                if (produits.isEmpty() && clients.isEmpty() && typeTarifications.isEmpty() && tarifications.isEmpty()) {
                    val firebaseData = fireBaseHandler.getDataFromFirebase()
                    if (firebaseData != null &&
                        (firebaseData.a_ProduitInfos.isNotEmpty() ||
                                firebaseData.b_ClientInfos.isNotEmpty() ||
                                firebaseData.c_TypeTarificationInfos.isNotEmpty() ||
                                firebaseData.d_TarificationInfos.isNotEmpty())) {

                        insertToRoom(firebaseData) {
                            coroutineScope.launch {
                                collectLatestData()
                            }
                        }
                    } else {
                        addTestDataAuFireBaseEtRoomEtUiAprestartNeedUpdateListener()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun cleanup() {
        fireBaseHandler.stopNeedUpdateListener()
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
                insertToRoom(data)
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
            val produits = database.a_ProduitInfosDao().getAllProduitsSync()
            val clients = database.b_ClientInfosDao().getAllClientsSync()
            val typeTarifications =
                database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()

            modelList = listOf(
                DataBasesInfosSql(
                    a_ProduitInfos = produits.toMutableList(),
                    b_ClientInfos = clients.toMutableList(),
                    c_TypeTarificationInfos = typeTarifications.toMutableList(),
                    d_TarificationInfos = tarifications.toMutableList()
                )
            )

            onSuccess()
        } catch (e: Exception) {
        }
    }

    private suspend fun insertToRoom(
        data: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        withContext(Dispatchers.IO) {
            try {
                database.a_ProduitInfosDao().insertAll(data.a_ProduitInfos)
                database.b_ClientInfosDao().insertAll(data.b_ClientInfos)
                database.c_TypeTarificationInfosDao().insertAll(data.c_TypeTarificationInfos)
                database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
                onSuccess()
            } catch (e: Exception) {
            }
        }
    }

    suspend fun deleteAllRoom() {
        withContext(Dispatchers.IO) {
            try {
                database.a_ProduitInfosDao().deleteAll()
                database.b_ClientInfosDao().deleteAll()
                database.c_TypeTarificationInfosDao().deleteAll()
                database.dTarificationInfosDao().deleteAll()
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
            fireBaseHandler.addToFirebaseAsync(dataBasesInfosSql) {
                onSuccess()
            }
        } catch (e: Exception) {
        }
    }
}
