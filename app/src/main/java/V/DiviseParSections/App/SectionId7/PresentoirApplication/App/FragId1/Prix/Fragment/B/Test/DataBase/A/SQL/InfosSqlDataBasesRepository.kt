package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.FireBaseHandler
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home.getDataFromFirebase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.testDatasDataBasesInfosSql
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
            addNeedUpdateAuAllSiEmpty()
            loadDataFromFirebaseAuRoomSiUnDataANeedUpdate {
                // This callback is now properly called when Firebase is empty
                Log.d(TAG, "Firebase is empty, uploading test data")
                val testData = testDatasDataBasesInfosSql()
                setToFireBase(testData)

                // Fixed the suspension function call in callback issue
                coroutineScope.launch {
                    insertToRoom(testData) {
                        Log.d(TAG, "Test data uploaded to Firebase and inserted to Room successfully")
                    }
                }
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
                Log.e(TAG, "Error adding data: ${e.message}", e)
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
                Log.e(TAG, "Error adding data with callback: ${e.message}", e)
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
            Log.e(TAG, "Error collecting latest data: ${e.message}", e)
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
                Log.e(TAG, "Error inserting to Room: ${e.message}", e)
            }
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
                Log.e(TAG, "Error deleting all data from Room: ${e.message}", e)
            }
        }
    }


    private fun setToFireBase(
        dataBasesInfosSql: DataBasesInfosSql,
        onSuccess: () -> Unit = {}
    ) {
        try {
            Log.d(TAG, "Uploading data to Firebase")
            fireBaseHandler.addToFirebaseAsync(dataBasesInfosSql) {
                Log.d(TAG, "Data uploaded to Firebase successfully")
                onSuccess()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading to Firebase: ${e.message}", e)
        }
    }

    private suspend fun addNeedUpdateAuAllSiEmpty() {
        try {
            val firebaseData = fireBaseHandler.getDataFromFirebase()

            // Log for debugging database state
            Log.d(
                TAG,
                "Firebase data retrieval: ${if (firebaseData != null) "Success" else "FAILED - Database is empty"}"
            )
            if (firebaseData == null) {
                Log.d(
                    TAG,
                    "Firebase data is null. Check Firebase connection and data structure."
                )
                Log.d(
                    TAG,
                    "Reference path: ${fireBaseHandler.getRefPath()}"
                )
                return // No data to process
            }

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
        } catch (e: Exception) {
            Log.e(TAG, "Error in addNeedUpdateAuAllSiEmpty: ${e.message}", e)
        }
    }

    private suspend fun loadDataFromFirebaseAuRoomSiUnDataANeedUpdate(
        siFireBaseEstVide: () -> Unit = {}
    ) {
        try {
            // First check if Firebase has data
            val firebaseData = fireBaseHandler.getDataFromFirebase()
            if (firebaseData == null) {
                Log.d(TAG, "Firebase database is empty, calling siFireBaseEstVide callback")
                siFireBaseEstVide() // Call the callback when Firebase is empty
                return
            }

            // If Firebase has data, check if local data needs update
            val produits = database.a_ProduitInfosDao().getAllProduitsSync()
            val clients = database.b_ClientInfosDao().getAllClientsSync()
            val typeTarifications =
                database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()

            val roomIsEmpty = produits.isEmpty() && clients.isEmpty() &&
                    typeTarifications.isEmpty() && tarifications.isEmpty()

            val needsUpdate = roomIsEmpty || produits.any { it.needUpdate } ||
                    clients.any { it.needUpdate } ||
                    typeTarifications.any { it.needUpdate } ||
                    tarifications.any { it.needUpdate }

            if (needsUpdate) {
                Log.d(TAG, "Local data needs update, syncing from Firebase")
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
                    Log.d(TAG, "Data synced from Firebase successfully")
                }
            } else {
                Log.d(TAG, "Local data is up to date, no sync needed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadDataFromFirebaseAuRoomSiUnDataANeedUpdate: ${e.message}", e)
        }
    }
}
