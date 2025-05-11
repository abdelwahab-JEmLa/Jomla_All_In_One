package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository

import androidx.compose.runtime.mutableStateListOf
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.ClientTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.ProduitTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.TarificationTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.TypeTarificationTestData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InputSqlGroupeRepositorysImp : InputSqlGroupeRepositorys {

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

    class TarificationDataBaseFacileEntreRepositoryImp :
        InputSqlGroupeRepositorys.A_TarificationDataBaseFacileEntreRepository {
        val _dataFlow = MutableStateFlow(TarificationTestData.initialTestData)
        override var modelList: List<InputSqlModels.A_TarificationDataBaseFacileEntre>
            get() = _dataFlow.value
            set(value) {
                _dataFlow.value = value
            }

        override fun add(
            data: InputSqlModels.A_TarificationDataBaseFacileEntre,
            onSuccess: (InputSqlModels.A_TarificationDataBaseFacileEntre) -> Unit
        ) {
            _dataFlow.update { currentList ->
                currentList + data
            }
            onSuccess(data)
        }
    }
    companion object {
        val clientRepository = ClientDataBase_RepositoryImp()
        val produitRepository = ProduitDataBase_RepositoryImp()
        val typeTarificationRepository = TypeTarificationDataBase_RepositoryImp()
        val tarificationDataBaseFacileEntreRepositoryImp = TarificationDataBaseFacileEntreRepositoryImp()
    }
}
