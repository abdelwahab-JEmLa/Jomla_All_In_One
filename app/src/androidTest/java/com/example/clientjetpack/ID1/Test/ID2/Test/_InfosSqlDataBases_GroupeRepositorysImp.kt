package com.example.clientjetpack.ID1.Test.ID2.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.D_TarificationInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class _InfosSqlDataBases_GroupeRepositorysImp(
    private val context: Context,
    val database: AppDatabase
) : _InfosSqlDataBases_GroupeRepositorys {
    
    private val fireBaseHandler = FireBaseHandler()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    // Implementation of A_ProduitInfos_Repository
    inner class A_ProduitInfos_RepositoryImpl : _InfosSqlDataBases_GroupeRepositorys.A_ProduitInfos_Repository {
        private val _modelListFlow = MutableStateFlow<List<A_ProduitInfos>>(emptyList())
        override var modelList: List<A_ProduitInfos>
            get() = _modelListFlow.value
            set(value) {
                _modelListFlow.value = value
            }

        override val modelListFlow: StateFlow<List<A_ProduitInfos>> = _modelListFlow.asStateFlow()

        init {
            coroutineScope.launch {
                database.produitInfosDao().getAllProduits().collect { produits ->
                    modelList = produits
                }
            }
        }

        override fun add(produitInfos: A_ProduitInfos, onSuccess: (A_ProduitInfos) -> Unit) {
            coroutineScope.launch {
                val id = database.produitInfosDao().insert(produitInfos)
                val insertedProduit = produitInfos.copy(id = id)
                setToFireBase(insertedProduit)
                onSuccess(insertedProduit)
            }
        }

        override fun deleteAll(onSuccess: () -> Unit) {
            coroutineScope.launch {
                database.produitInfosDao().deleteAll()
                onSuccess()
            }
        }

        private fun setToFireBase(produitInfos: A_ProduitInfos) {
            fireBaseHandler.addToFirebaseAsync(produitInfos, fireBaseHandler.getProduitRef())
        }

        suspend fun loadDataFromFirebase() {
            try {
                val loadedData = fireBaseHandler.loadDatasAsync(
                    fireBaseHandler.getProduitRef(),
                    A_ProduitInfos::class.java
                )
                coroutineScope.launch {
                    database.produitInfosDao().insertAll(loadedData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Implementation of B_ClientInfos_Repository
    inner class B_ClientInfos_RepositoryImpl : _InfosSqlDataBases_GroupeRepositorys.B_ClientInfos_Repository {
        private val _modelListFlow = MutableStateFlow<List<B_ClientInfos>>(emptyList())
        override var modelList: List<B_ClientInfos>
            get() = _modelListFlow.value
            set(value) {
                _modelListFlow.value = value
            }

        override val modelListFlow: StateFlow<List<B_ClientInfos>> = _modelListFlow.asStateFlow()

        init {
            coroutineScope.launch {
                database.clientInfosDao().getAllClients().collect { clients ->
                    modelList = clients
                }
            }
        }

        override fun add(client: B_ClientInfos) {
            coroutineScope.launch {
                val id = database.clientInfosDao().insert(client)
                val insertedClient = client.copy(id = id)
                setToFireBase(insertedClient)
            }
        }

        override fun update(client: B_ClientInfos, onSuccess: (B_ClientInfos) -> Unit) {
            coroutineScope.launch {
                database.clientInfosDao().update(client)
                setToFireBase(client)
                onSuccess(client)
            }
        }

        override fun deleteAll(onSuccess: () -> Unit) {
            coroutineScope.launch {
                database.clientInfosDao().deleteAll()
                onSuccess()
            }
        }

        private fun setToFireBase(client: B_ClientInfos) {
            fireBaseHandler.addToFirebaseAsync(client, fireBaseHandler.getClientRef())
        }

        suspend fun loadDataFromFirebase() {
            try {
                val loadedData = fireBaseHandler.loadDatasAsync(
                    fireBaseHandler.getClientRef(),
                    B_ClientInfos::class.java
                )
                coroutineScope.launch {
                    database.clientInfosDao().insertAll(loadedData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Implementation of C_TypeTarificationInfos_Repository
    inner class C_TypeTarificationInfos_RepositoryImpl : _InfosSqlDataBases_GroupeRepositorys.C_TypeTarificationInfos_Repository {
        private val _modelListFlow = MutableStateFlow<List<C_TypeTarificationInfos>>(emptyList())
        override var modelList: List<C_TypeTarificationInfos>
            get() = _modelListFlow.value
            set(value) {
                _modelListFlow.value = value
            }

        override val modelListFlow: StateFlow<List<C_TypeTarificationInfos>> = _modelListFlow.asStateFlow()

        init {
            coroutineScope.launch {
                database.typeTarificationInfosDao().getAllTypeTarifications().collect { types ->
                    modelList = types
                }
            }
        }

        override fun add(typeTarification: C_TypeTarificationInfos) {
            coroutineScope.launch {
                val id = database.typeTarificationInfosDao().insert(typeTarification)
                val insertedType = typeTarification.copy(id = id)
                setToFireBase(insertedType)
            }
        }

        override fun deleteAll(onSuccess: () -> Unit) {
            coroutineScope.launch {
                database.typeTarificationInfosDao().deleteAll()
                onSuccess()
            }
        }

        private fun setToFireBase(typeTarification: C_TypeTarificationInfos) {
            fireBaseHandler.addToFirebaseAsync(typeTarification, fireBaseHandler.getTypeTarificationRef())
        }

        suspend fun loadDataFromFirebase() {
            try {
                val loadedData = fireBaseHandler.loadDatasAsync(
                    fireBaseHandler.getTypeTarificationRef(),
                    C_TypeTarificationInfos::class.java
                )
                coroutineScope.launch {
                    database.typeTarificationInfosDao().insertAll(loadedData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Implementation of D_TarificationInfos_Repository
    inner class D_TarificationInfos_RepositoryImpl : _InfosSqlDataBases_GroupeRepositorys.D_TarificationInfos_Repository {
        private val _modelListFlow = MutableStateFlow<List<D_TarificationInfos>>(emptyList())
        override var modelList: List<D_TarificationInfos>
            get() = _modelListFlow.value
            set(value) {
                _modelListFlow.value = value
            }

        override val modelListFlow: StateFlow<List<D_TarificationInfos>> = _modelListFlow.asStateFlow()

        init {
            coroutineScope.launch {
                database.tarificationInfosDao().getAllTarifications().collect { tarifications ->
                    modelList = tarifications
                }
            }
        }

        override suspend fun loadDataFromFirebase() {
            try {
                val loadedData = fireBaseHandler.loadDatasAsync(
                    fireBaseHandler.getTarificationRef(),
                    D_TarificationInfos::class.java
                )
                coroutineScope.launch {
                    database.tarificationInfosDao().insertAll(loadedData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun add(tarification: D_TarificationInfos, onSuccess: (D_TarificationInfos) -> Unit) {
            coroutineScope.launch {
                database.tarificationInfosDao().insert(tarification)
                setToFireBase(tarification)
                onSuccess(tarification)
            }
        }

        override fun deleteAll(onSuccess: () -> Unit) {
            coroutineScope.launch {
                database.tarificationInfosDao().deleteAll()
                onSuccess()
            }
        }

        private fun setToFireBase(tarification: D_TarificationInfos) {
            fireBaseHandler.addToFirebaseAsync(tarification, fireBaseHandler.getTarificationRef())
        }
    }

    // Repository instances
    val produitRepository = A_ProduitInfos_RepositoryImpl()
    val clientRepository = B_ClientInfos_RepositoryImpl()
    val typeTarificationRepository = C_TypeTarificationInfos_RepositoryImpl()
    val tarificationRepository = D_TarificationInfos_RepositoryImpl()
}
