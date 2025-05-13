// File: InputEtInfosSqlGroupeRepositorysImp.kt
package com.example.clientjetpack.ID1.Test.Packages.Repository.Input

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.runtime.mutableStateListOf
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Init.initialClientsData
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Init.initialProductsData
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Init.initialTestData
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Init.initialTypeTarificationData
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class InputEtInfosSqlGroupeRepositorysImp(
) : InputEtInfosSqlGroupeRepositorys {
    val fireBaseHandler = FireBaseHandler()
    private val inputEtInfosSqlGroupeRepositorysImpDataBaseRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val produitRepository = ProduitDataBase_RepositoryImp(
        repositoryScope,
        fireBaseHandler
    )
    private val clientRepository = ClientDataBase_RepositoryImp()
    private val typeTarificationRepository = TypeTarificationDataBase_RepositoryImp()
    private val tarificationRepository = TarificationRepositoryImp(
        fireBaseHandler,
        inputEtInfosSqlGroupeRepositorysImpDataBaseRef,
        repositoryScope
    )

    override fun ProduitInfosRepository(): InputEtInfosSqlGroupeRepositorys.ProduitDataBase_Repository {
        return produitRepository
    }

    override fun ClientDataBase_Repository(): InputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository {
        return clientRepository
    }

    override fun TypeTarificationInfosRepository(): InputEtInfosSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        return typeTarificationRepository
    }

    override fun TarificationRepository(): InputEtInfosSqlGroupeRepositorys.TarificationRepository {
        return tarificationRepository
    }

    class ProduitDataBase_RepositoryImp(
        val repositoryScope: CoroutineScope,
        val fireBaseHandler: FireBaseHandler
    ) : InputEtInfosSqlGroupeRepositorys.ProduitDataBase_Repository {
        private val _modelList = mutableStateListOf<InputEtInfosSqlModels.ProduitInfos>()
        val _dataFlow = MutableStateFlow<List<InputEtInfosSqlModels.ProduitInfos>>(emptyList())

        override var modelList: List<InputEtInfosSqlModels.ProduitInfos>
            get() = _dataFlow.value
            set(value) {
                _modelList.clear()
                _modelList.addAll(value)
                _dataFlow.value = _modelList.toList()
            }

        init {
            initDefaultData()
        }

        private fun initDefaultData() {
            _modelList.clear()
            _modelList.addAll(initialProductsData)
            _dataFlow.value = _modelList.toList()
        }

        override fun add(
            produitInfos: InputEtInfosSqlModels.ProduitInfos,
            onSuccess: (InputEtInfosSqlModels.ProduitInfos) -> Unit
        ) {
            repositoryScope.launch {
                try {
                    // Check if product already exists
                    val existingIndex = _modelList.indexOfFirst { it.id == produitInfos.id }
                    if (existingIndex >= 0) {
                        // Update existing product
                        _modelList[existingIndex] = produitInfos
                    } else {
                        // Add new product
                        _modelList.add(produitInfos)
                    }

                    // Update the flow
                    _dataFlow.value = _modelList.toList()

                    onSuccess(produitInfos)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Other repository classes remain unchanged
    class ClientDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository {
        private val _modelList = mutableStateListOf<InputEtInfosSqlModels.ClientDataBase>()

        override var modelList: List<InputEtInfosSqlModels.ClientDataBase> = _modelList

        init {
            initDefaultData()
        }

        private fun initDefaultData() {
            _modelList.clear()
            _modelList.addAll(initialClientsData)
        }

        override fun add(client: InputEtInfosSqlModels.ClientDataBase) {
            val existingIndex = _modelList.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                _modelList.add(client)
            } else {
                _modelList[existingIndex] = client
            }
        }

        override fun update(
            client: InputEtInfosSqlModels.ClientDataBase,
            onSuccess: (InputEtInfosSqlModels.ClientDataBase) -> Unit,
        ) {
            val index = _modelList.indexOfFirst { it.id == client.id }
            if (index != -1) {
                _modelList[index] = client
                onSuccess(client)
            }
        }
    }

    class TypeTarificationDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        private val _modelList = mutableStateListOf<InputEtInfosSqlModels.TypeTarificationDataBase>()

        override var modelList: List<InputEtInfosSqlModels.TypeTarificationDataBase> = _modelList

        init {
            initDefaultData()
        }

        private fun initDefaultData() {
            _modelList.clear()
            _modelList.addAll(initialTypeTarificationData)
        }
    }

    class TarificationRepositoryImp(
        private val fireBaseHandler: FireBaseHandler,
        private val parentDbRef: DatabaseReference,
        private val repositoryScope: CoroutineScope
    ) : InputEtInfosSqlGroupeRepositorys.TarificationRepository {
        val _dataFlow = MutableStateFlow<List<InputEtInfosSqlModels.Tarification>>(emptyList())

        // Using a mutable list to hold the actual data
        private val _modelList = mutableStateListOf<InputEtInfosSqlModels.Tarification>()

        private val sonDataBaseRef: DatabaseReference =
            parentDbRef.child("A_Tarification")

        override var modelList: List<InputEtInfosSqlModels.Tarification>
            get() = _dataFlow.value
            set(value) {
                _modelList.clear()
                _modelList.addAll(value)
                _dataFlow.value = _modelList.toList()
            }

        init {
            repositoryScope.launch {
                loadDataFromFirebase()

                if (_dataFlow.value.isEmpty()) {
                    fireBaseHandler.addAllToFireBaseAsync(
                        initialTestData,
                        sonDataBaseRef
                    )
                    loadDataFromFirebase()
                }
            }
        }

        override suspend fun loadDataFromFirebase() {
            val loadedData = fireBaseHandler.loadDatasAsync(
                sonDataBaseRef,
                InputEtInfosSqlModels.Tarification::class.java
            )
            modelList = loadedData
        }

        override fun add(
            tarification: InputEtInfosSqlModels.Tarification,
            onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit
        ) {
            repositoryScope.launch {
                try {
                    // Add to the mutable list and update the StateFlow
                    _modelList.add(tarification)
                    _dataFlow.value = _modelList.toList()

                    onSuccess(tarification)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
