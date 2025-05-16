package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.FireBaseHandler
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
            addNeedUpdateAuAllSiEmpty()
            loadDataFromFirebaseAuRoomSiUnDataANeedUpdate()
            {
                setToFireBase(testDatasDataBasesInfosSql())
            }
            collectRoom()
        }
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

    // Modified to support suspending operations
    suspend fun add(
        data: DataBasesInfosSql
    ) {
        withContext(Dispatchers.IO) {
            try {
                insertToRoom(data)
                setToFireBase(data)
                collectLatestData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Add non-suspending version with callback for backward compatibility
    fun add(
        data: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        coroutineScope.launch {
            try {
                add(data)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
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
            e.printStackTrace()
        }
    }

    private suspend fun insertToRoom(
        data: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        withContext(Dispatchers.IO) {
            database.a_ProduitInfosDao().insertAll(data.a_ProduitInfos)
            database.b_ClientInfosDao().insertAll(data.b_ClientInfos)
            database.c_TypeTarificationInfosDao().insertAll(data.c_TypeTarificationInfos)
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            onSuccess()
        }
    }

    // Modified to support suspending operations
    suspend fun deleteAllRoom() {
        withContext(Dispatchers.IO) {
            try {
                database.a_ProduitInfosDao().deleteAll()
                database.b_ClientInfosDao().deleteAll()
                database.c_TypeTarificationInfosDao().deleteAll()
                database.dTarificationInfosDao().deleteAll()
                collectLatestData()
            } catch (e: Exception) {
                e.printStackTrace()
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
            e.printStackTrace()
        }
    }

    private suspend fun addNeedUpdateAuAllSiEmpty() {
        try {
            val firebaseData = fireBaseHandler.getDataFromFirebase()

            // Log for debugging database state
            android.util.Log.d(
                "InfosSqlRepo",
                "Firebase data retrieval: ${if (firebaseData != null) "Success" else "FAILED - Database is empty"}"
            )
            if (firebaseData == null) {
                android.util.Log.d(
                    "InfosSqlRepo",
                    "Firebase data is null. Check Firebase connection and data structure."
                )
                android.util.Log.d(
                    "InfosSqlRepo",
                    "Reference path: ${fireBaseHandler.getRefPath()}"
                )
            }

            if (firebaseData != null) {
                val updatedData = DataBasesInfosSql(
                    a_ProduitInfos = firebaseData.a_ProduitInfos.map { it.copy(needUpdate = true) }
                        .toMutableList(),
                    b_ClientInfos = firebaseData.b_ClientInfos.map { it.copy(needUpdate = true) }
                        .toMutableList(),
                    c_TypeTarificationInfos = firebaseData.c_TypeTarificationInfos.map {
                        it.copy(
                            needUpdate = true
                        )
                    }.toMutableList(),
                    d_TarificationInfos = firebaseData.d_TarificationInfos.map { it.copy(needUpdate = true) }
                        .toMutableList()
                )
                setToFireBase(updatedData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadDataFromFirebaseAuRoomSiUnDataANeedUpdate(
        siFireBaseEstVide: () -> Unit = {}
    ) {
        try {
            val produits = database.a_ProduitInfosDao().getAllProduitsSync()
            val clients = database.b_ClientInfosDao().getAllClientsSync()
            val typeTarifications =
                database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()

            val needsUpdate = produits.any { it.needUpdate } ||
                    clients.any { it.needUpdate } ||
                    typeTarifications.any { it.needUpdate } ||
                    tarifications.any { it.needUpdate }

            if (needsUpdate) {
                val firebaseData = fireBaseHandler.getDataFromFirebase()
                if (firebaseData != null) {
                    insertToRoom(firebaseData) {
                        // Reset needUpdate flags in Firebase after successful sync to Room
                        val updatedData = DataBasesInfosSql(
                            a_ProduitInfos = firebaseData.a_ProduitInfos.map { it.copy(needUpdate = false) }
                                .toMutableList(),
                            b_ClientInfos = firebaseData.b_ClientInfos.map { it.copy(needUpdate = false) }
                                .toMutableList(),
                            c_TypeTarificationInfos = firebaseData.c_TypeTarificationInfos.map {
                                it.copy(
                                    needUpdate = false
                                )
                            }.toMutableList(),
                            d_TarificationInfos = firebaseData.d_TarificationInfos.map {
                                it.copy(
                                    needUpdate = false
                                )
                            }.toMutableList()
                        )
                        setToFireBase(updatedData)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
