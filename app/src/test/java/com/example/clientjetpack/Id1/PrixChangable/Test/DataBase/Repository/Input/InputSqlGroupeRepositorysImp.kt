package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input

import androidx.compose.runtime.mutableStateListOf
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.Test.ClientTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.Test.ProduitTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.Test.TarificationTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.Test.TypeTarificationTestData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InputSqlGroupeRepositorysImp : InputSqlGroupeRepositorys {
    // Instance fields to hold repository implementations
    private val produitRepository = ProduitDataBase_RepositoryImp()
    private val clientRepository = ClientDataBase_RepositoryImp()
    private val typeTarificationRepository = TypeTarificationDataBase_RepositoryImp()
    private val tarificationRepository = TarificationRepositoryImp()

    // Methods to expose repositories
    override fun ProduitDataBase_Repository(): InputSqlGroupeRepositorys.ProduitDataBase_Repository {
        return produitRepository
    }

    override fun ClientDataBase_Repository(): InputSqlGroupeRepositorys.ClientDataBase_Repository {
        return clientRepository
    }

    override fun TypeTarificationDataBase_Repository(): InputSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        return typeTarificationRepository
    }

    override fun TarificationRepository(): InputSqlGroupeRepositorys.TarificationRepository {
        return tarificationRepository
    }

    class ProduitDataBase_RepositoryImp :
        InputSqlGroupeRepositorys.ProduitDataBase_Repository {
        override var modelList: List<InputSqlModels.ProduitDataBase> = initDefaultData()

        private fun initDefaultData(): List<InputSqlModels.ProduitDataBase> {
            return mutableStateListOf<InputSqlModels.ProduitDataBase>().apply {
                addAll(ProduitTestData.initialTestData)
            }
        }
    }

    class ClientDataBase_RepositoryImp :
        InputSqlGroupeRepositorys.ClientDataBase_Repository {
        override var modelList: List<InputSqlModels.ClientDataBase> = initDefaultData()

        private fun initDefaultData(): List<InputSqlModels.ClientDataBase> {
            return mutableStateListOf<InputSqlModels.ClientDataBase>().apply {
                addAll(ClientTestData.initialTestData)
            }
        }

        override fun add(client: InputSqlModels.ClientDataBase) {
            val list = modelList as? MutableList ?: return
            val existingIndex = list.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                list.add(client)
            } else {
                list[existingIndex] = client
            }
        }

        override fun update(
            client: InputSqlModels.ClientDataBase,
            onSuccess: (InputSqlModels.ClientDataBase) -> Unit
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
        InputSqlGroupeRepositorys.TypeTarificationDataBase_Repository {
        override var modelList: List<InputSqlModels.TypeTarificationDataBase> = initDefaultData()

        private fun initDefaultData(): List<InputSqlModels.TypeTarificationDataBase> {
            return mutableStateListOf<InputSqlModels.TypeTarificationDataBase>().apply {
                addAll(TypeTarificationTestData.initialTestData)
            }
        }
    }

    class TarificationRepositoryImp :
        InputSqlGroupeRepositorys.TarificationRepository {
        val _dataFlow = MutableStateFlow(TarificationTestData.initialTestData)
        override var modelList: List<InputSqlModels.Tarification>
            get() = _dataFlow.value
            set(value) {
                _dataFlow.value = value
            }

        override fun add(
            data: InputSqlModels.Tarification,
            onSuccess: (InputSqlModels.Tarification) -> Unit
        ) {
            _dataFlow.update { currentList ->
                currentList + data
            }
            onSuccess(data)
        }
    }
}
