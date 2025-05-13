package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
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
    private val produitRepository = ProduitDataBase_RepositoryImp()
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

    class ProduitDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.ProduitDataBase_Repository {
        override var modelList: List<InputEtInfosSqlModels.ProduitInfos> = initDefaultData()

        private fun initDefaultData(): List<InputEtInfosSqlModels.ProduitInfos> {
            return mutableStateListOf<InputEtInfosSqlModels.ProduitInfos>().apply {
                addAll(
                    initialProductsData
                )
            }
        }
    }

    class ClientDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository {
        override var modelList: List<InputEtInfosSqlModels.ClientDataBase> = initDefaultData()

        private fun initDefaultData(): List<InputEtInfosSqlModels.ClientDataBase> {
            return mutableStateListOf<InputEtInfosSqlModels.ClientDataBase>().apply {
                addAll(initialClientsData)
            }
        }

        override fun add(client: InputEtInfosSqlModels.ClientDataBase) {
            val list = modelList as? MutableList ?: return
            val existingIndex = list.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                list.add(client)
            } else {
                list[existingIndex] = client
            }
        }

        override fun update(
            client: InputEtInfosSqlModels.ClientDataBase,
            onSuccess: (InputEtInfosSqlModels.ClientDataBase) -> Unit,
        ) {
            val list = modelList as? MutableList ?: return
            val index = list.indexOfFirst { it.id == client.id }
            if (index != -1) {
                list[index] = client
                onSuccess(client)
            }
        }
    }

    class TypeTarificationDataBase_RepositoryImp :
        InputEtInfosSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        override var modelList: List<InputEtInfosSqlModels.TypeTarificationDataBase> =
            initDefaultData()

        private fun initDefaultData(): List<InputEtInfosSqlModels.TypeTarificationDataBase> {
            return mutableStateListOf<InputEtInfosSqlModels.TypeTarificationDataBase>().apply {
                addAll(
                    initialTypeTarificationData
                )
            }
        }
    }

    class TarificationRepositoryImp(
        private val fireBaseHandler: FireBaseHandler,
        private val parentDbRef: DatabaseReference,
        private val repositoryScope: CoroutineScope
    ) : InputEtInfosSqlGroupeRepositorys.TarificationRepository {
        val _dataFlow = MutableStateFlow<List<InputEtInfosSqlModels.Tarification>>(emptyList())

        private val sonDataBaseRef: DatabaseReference =
            parentDbRef.child("A_Tarification")

        override var modelList: List<InputEtInfosSqlModels.Tarification>
            get() = _dataFlow.value
            set(value) {
                _dataFlow.value = value
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
            _dataFlow.value = loadedData
        }

        override fun add(
            tarification: InputEtInfosSqlModels.Tarification,
            onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit
        ) {
            repositoryScope.launch {

            }
        }
    }
}
