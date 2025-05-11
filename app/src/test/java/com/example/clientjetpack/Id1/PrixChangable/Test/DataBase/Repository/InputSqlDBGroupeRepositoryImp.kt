package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository

import androidx.compose.runtime.mutableStateListOf
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.AB_ReferentialSepareDataBases
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.ClientTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.ProduitTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.TarificationTestData
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test.TypeTarificationTestData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InputSqlDBGroupeRepositoryImp : B_SqlInputDataBaseGroupeRepository {

    class ProduitDataBase_RepositoryImp :
        B_SqlInputDataBaseGroupeRepository.ProduitDataBase_Repository {
        override var modelList: List<AB_ReferentialSepareDataBases.ProduitDataBase> = initDefaultData()

         private fun initDefaultData(): List<AB_ReferentialSepareDataBases.ProduitDataBase> {
            return mutableStateListOf<AB_ReferentialSepareDataBases.ProduitDataBase>().apply {
                addAll(ProduitTestData.initialTestData)
            }
        }
    }

    class ClientDataBase_RepositoryImp :
        B_SqlInputDataBaseGroupeRepository.ClientDataBase_Repository {
        override var modelList: List<AB_ReferentialSepareDataBases.ClientDataBase> = initDefaultData()

         private fun initDefaultData(): List<AB_ReferentialSepareDataBases.ClientDataBase> {
            return mutableStateListOf<AB_ReferentialSepareDataBases.ClientDataBase>().apply {
                addAll(ClientTestData.initialTestData)
            }
        }

        override fun add(client: AB_ReferentialSepareDataBases.ClientDataBase) {
            val list = modelList as? MutableList ?: return
            val existingIndex = list.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                list.add(client)
            } else {
                list[existingIndex] = client
            }
        }

        override fun update(
            client: AB_ReferentialSepareDataBases.ClientDataBase,
            onSuccess: (AB_ReferentialSepareDataBases.ClientDataBase) -> Unit
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
        B_SqlInputDataBaseGroupeRepository.TypeTarificationDataBase_Repository {
        override var modelList: List<AB_ReferentialSepareDataBases.TypeTarificationDataBase> = initDefaultData()

         private fun initDefaultData(): List<AB_ReferentialSepareDataBases.TypeTarificationDataBase> {
            return mutableStateListOf<AB_ReferentialSepareDataBases.TypeTarificationDataBase>().apply {
                addAll(TypeTarificationTestData.initialTestData)
            }
        }
    }

    class TarificationDataBaseFacileEntreRepositoryImp :
        B_SqlInputDataBaseGroupeRepository.A_TarificationDataBaseFacileEntreRepository {
        val _dataFlow = MutableStateFlow(TarificationTestData.initialTestData)
        override var modelList: List<AB_ReferentialSepareDataBases.A_TarificationDataBaseFacileEntre>
            get() = _dataFlow.value
            set(value) {
                _dataFlow.value = value
            }

        override fun add(
            data: AB_ReferentialSepareDataBases.A_TarificationDataBaseFacileEntre,
            onSuccess: (AB_ReferentialSepareDataBases.A_TarificationDataBaseFacileEntre) -> Unit
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
