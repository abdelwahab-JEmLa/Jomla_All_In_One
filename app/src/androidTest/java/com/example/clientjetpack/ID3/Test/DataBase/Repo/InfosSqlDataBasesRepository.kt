package com.example.clientjetpack.ID3.Test.DataBase.Repo

import com.example.clientjetpack.ID3.Test.DataBase.Repo.Home.FireBaseHandler
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Home.TestAppDatabase
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.DataBasesInfosSql
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
    private val fireBaseHandler: FireBaseHandler
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

    fun add(
        data: DataBasesInfosSql,
        onSuccess: (DataBasesInfosSql) -> Unit={}
    ) {
        coroutineScope.launch {
            insertToRoom(data)
            setToFireBase(data)
            onSuccess(data)
        }
    }

    private suspend fun insertToRoom(data: DataBasesInfosSql) {
        withContext(Dispatchers.IO) {
            database.a_ProduitInfosDao().insertAll(data.a_ProduitInfos)
            database.b_ClientInfosDao().insertAll(data.b_ClientInfos)
            database.c_TypeTarificationInfosDao().insertAll(data.c_TypeTarificationInfos)
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
        }
    }

    fun deleteAll(onSuccess: () -> Unit={}) {
        coroutineScope.launch {
            database.a_ProduitInfosDao().deleteAll()
            database.b_ClientInfosDao().deleteAll()
            database.c_TypeTarificationInfosDao().deleteAll()
            database.dTarificationInfosDao().deleteAll()
            onSuccess()
        }
    }

    private fun setToFireBase(dataBasesInfosSql: DataBasesInfosSql) {
        fireBaseHandler.addToFirebaseAsync(dataBasesInfosSql)
    }

    private suspend fun addNeedUpdateAuAllSiEmpty() {
        // If any table is empty, fetch data from Firebase
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
    }

    private suspend fun loadDataFromFirebaseAuRoomSiUnDataANeedUpdate() {
        try {
            // Check if any data needs update
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
                    insertToRoom(firebaseData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
