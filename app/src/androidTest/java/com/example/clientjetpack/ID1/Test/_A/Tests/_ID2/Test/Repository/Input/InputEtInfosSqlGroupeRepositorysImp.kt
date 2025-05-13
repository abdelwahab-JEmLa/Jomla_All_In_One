package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Input

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.clientjetpack.ID1.Test.Packages.Init.initialClientsData
import com.example.clientjetpack.ID1.Test.Packages.Init.initialProductsData
import com.example.clientjetpack.ID1.Test.Packages.Init.initialTestData
import com.example.clientjetpack.ID1.Test.Packages.Init.initialTypeTarificationData
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
        private val TAG = "ProduitRepository"
        private val _modelList = mutableStateListOf<InputEtInfosSqlModels.ProduitInfos>()
        val _dataFlow = MutableStateFlow<List<InputEtInfosSqlModels.ProduitInfos>>(emptyList())

        override var modelList: List<InputEtInfosSqlModels.ProduitInfos>
            get() = _dataFlow.value
            set(value) {
                _modelList.clear()
                _modelList.addAll(value)
                _dataFlow.value = _modelList.toList()
                Log.d(TAG, "Product list updated, size: ${_modelList.size}")
            }

        init {
            initDefaultData()
        }

        private fun initDefaultData() {
            _modelList.clear()
            _modelList.addAll(initialProductsData)
            _dataFlow.value = _modelList.toList()
            Log.d(TAG, "Default product data initialized, size: ${_modelList.size}")
        }

        override fun add(
            produitInfos: InputEtInfosSqlModels.ProduitInfos,
            onSuccess: (InputEtInfosSqlModels.ProduitInfos) -> Unit
        ) {
            repositoryScope.launch {
                try {
                    Log.d(TAG, "Adding product: ${produitInfos.id} - ${produitInfos.nom}")

                    // Check if product already exists
                    val existingIndex = _modelList.indexOfFirst { it.id == produitInfos.id }
                    if (existingIndex >= 0) {
                        // Update existing product
                        _modelList[existingIndex] = produitInfos
                        Log.d(TAG, "Updated existing product at index $existingIndex")
                    } else {
                        // Add new product
                        _modelList.add(produitInfos)
                        Log.d(TAG, "Added new product, new size: ${_modelList.size}")
                    }

                    // Update the flow
                    _dataFlow.value = _modelList.toList()
                    Log.d(TAG, "Product list updated in flow, size: ${_dataFlow.value.size}")

                    onSuccess(produitInfos)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding product: ${e.message}", e)
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
        private val TAG = "TarificationRepo"
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
                Log.d(TAG, "Tarification list updated, size: ${_modelList.size}")
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
            Log.d(TAG, "Loaded tarification data from Firebase, size: ${loadedData.size}")
        }

        override fun add(
            tarification: InputEtInfosSqlModels.Tarification,
            onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit
        ) {
            repositoryScope.launch {
                try {
                    Log.d(TAG, "Adding tarification for product: ${tarification.idProduit}")
                    // Add to the mutable list and update the StateFlow
                    _modelList.add(tarification)
                    _dataFlow.value = _modelList.toList()
                    Log.d(TAG, "Tarification added, new size: ${_modelList.size}")

                    onSuccess(tarification)
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding tarification: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }
}
