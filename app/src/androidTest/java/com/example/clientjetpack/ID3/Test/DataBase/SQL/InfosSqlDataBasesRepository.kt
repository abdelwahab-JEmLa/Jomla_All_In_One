// Modify the InfosSqlDataBasesRepository to support suspending functions
package com.example.clientjetpack.ID3.Test.DataBase.SQL

import com.example.clientjetpack.ID3.Test.DataBase.SQL.Home.FireBaseHandler
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Home.TestAppDatabase
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.DataBasesInfosSql
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfosSqlDataBasesRepository(
    val database: TestAppDatabase,
    private val fireBaseHandler: FireBaseHandler,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

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
            collectRoom()
        }
    }

    private fun collectRoom() {
        coroutineScope.launch {
            val produitsFlow = database.a_ProduitInfosDao().getAllProduits()
            val clientsFlow = database.b_ClientInfosDao().getAllClients()
            val typeTarificationsFlow = database.c_TypeTarificationInfosDao().getAllTypeTarifications()
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
        withContext(ioDispatcher) {
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
            val typeTarifications = database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
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
        withContext(ioDispatcher) {
            database.a_ProduitInfosDao().insertAll(data.a_ProduitInfos)
            database.b_ClientInfosDao().insertAll(data.b_ClientInfos)
            database.c_TypeTarificationInfosDao().insertAll(data.c_TypeTarificationInfos)
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            onSuccess()
        }
    }

    // Modified to support suspending operations
    suspend fun deleteAll() {
        withContext(ioDispatcher) {
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

    // Add non-suspending version with callback for backward compatibility
    fun deleteAll(
        onSuccess: () -> Unit = {}
    ) {
        coroutineScope.launch {
            try {
                deleteAll()
                onSuccess()
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
            if (firebaseData != null) {
                val updatedData = DataBasesInfosSql(
                    a_ProduitInfos = firebaseData.a_ProduitInfos.map { it.copy(needUpdate = true) }.toMutableList(),
                    b_ClientInfos = firebaseData.b_ClientInfos.map { it.copy(needUpdate = true) }.toMutableList(),
                    c_TypeTarificationInfos = firebaseData.c_TypeTarificationInfos.map { it.copy(needUpdate = true) }.toMutableList(),
                    d_TarificationInfos = firebaseData.d_TarificationInfos.map { it.copy(needUpdate = true) }.toMutableList()
                )
                setToFireBase(updatedData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadDataFromFirebaseAuRoomSiUnDataANeedUpdate() {
        try {
            val produits = database.a_ProduitInfosDao().getAllProduitsSync()
            val clients = database.b_ClientInfosDao().getAllClientsSync()
            val typeTarifications = database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
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
                            a_ProduitInfos = firebaseData.a_ProduitInfos.map { it.copy(needUpdate = false) }.toMutableList(),
                            b_ClientInfos = firebaseData.b_ClientInfos.map { it.copy(needUpdate = false) }.toMutableList(),
                            c_TypeTarificationInfos = firebaseData.c_TypeTarificationInfos.map { it.copy(needUpdate = false) }.toMutableList(),
                            d_TarificationInfos = firebaseData.d_TarificationInfos.map { it.copy(needUpdate = false) }.toMutableList()
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
