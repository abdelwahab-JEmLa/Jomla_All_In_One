package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.getDataFromFirebase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.startNeedUpdateListener
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.stopNeedUpdateListener
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.SQl.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.A.SQL.Models.Function.testDatasDataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class InfosSqlDataBasesRepository(
    val database: AppDatabase,
    private val fireBaseOperationsHandler: FireBaseOperationsHandler,
    private val roomOperationsHandler: RoomOperationsHandler
) {
    private val TAG = "InfosSqlRepo"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex() // For thread-safe operations

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
          //  verifieFireBaseEstVide()
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
                    upsert(testData) {}
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
                                firebaseData.b_ClientInfosList.isNotEmpty() ||
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
                        b_ClientInfosList = clients.toMutableList(),
                        c_TypeTarificationInfos = typeTarifications.toMutableList(),
                        d_TarificationInfos = tarifications.toMutableList()
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

    fun addoneClientInfos(newData: B_ClientInfos): Boolean {
        coroutineScope.launch {
            mutex.withLock {
                try {
                    Log.d(TAG, "Adding B_ClientInfos: ${newData.id}")
                    val currentData = modelListFlow.value.firstOrNull()
                    if (currentData != null) {
                        // Check if client already exists
                        val existingClient = currentData.b_ClientInfosList.find { it.id == newData.id }
                        if (existingClient != null) {
                            Log.d(TAG, "Client ${newData.id} already exists, skipping addition")
                            return@withLock
                        }

                        val updatedData = currentData.copy(
                            b_ClientInfosList = currentData.b_ClientInfosList.toMutableList().apply {
                                add(newData)
                            }
                        )

                        upsert(updatedData)
                    } else {
                        Log.e(TAG, "Failed to upsert B_ClientInfos: no current data available")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding B_ClientInfos", e)
                }
            }
        }
        return true
    }

    fun updateMultiClientInfos(updatedClients: MutableList<B_ClientInfos>): Boolean {
        coroutineScope.launch {
            mutex.withLock {
                try {
                    Log.d(TAG, "Updating multiple B_ClientInfos")
                    val currentData = modelListFlow.value.firstOrNull()
                    if (currentData != null) {
                        // Create a new copy of the database with updated client list
                        val updatedData = currentData.copy(
                            b_ClientInfosList = updatedClients
                        )

                        upsert(updatedData)

                        // Update state flow
                        modelList = listOf(updatedData)
                        return@withLock
                    } else {
                        Log.e(TAG, "Failed to update B_ClientInfos: no current data available")
                        return@withLock
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating B_ClientInfos", e)
                    return@withLock
                }
            }
        }
        return true
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
