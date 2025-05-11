package com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputEtInfosSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Output.OutputNoSqlModelRepositoryImp
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel() : ViewModel() {
    private val inputSqlGroupeRepositorys =
        InputEtInfosSqlGroupeRepositorysImp()
    private val outputNoSqlModelRepository =
        OutputNoSqlModelRepositoryImp(inputSqlGroupeRepositorys)

    private val _OutputNoSqlFlow = MutableStateFlow(OutputNoSqlModel(emptyList()))
    val outputNoSqlFlow: StateFlow<OutputNoSqlModel> = _OutputNoSqlFlow.asStateFlow()

    private val inputSqlClientRepo = inputSqlGroupeRepositorys.ClientDataBase_Repository()
    private val typeTarificationInputSqlRepo = inputSqlGroupeRepositorys
        .TypeTarificationInfosRepository()
    private val inputSqlProduitInfosRepository= inputSqlGroupeRepositorys
        .ProduitInfosRepository()

    init {
        observeTarificationData()
    }

    private fun observeTarificationData() {
        viewModelScope.launch {
            outputNoSqlModelRepository.dataFlow.collectLatest { data ->
                _OutputNoSqlFlow.value = data
            }
        }
    }

    fun addNewTestDataTarificationEtClient() {
        val newTarification = InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(day = 10, hour = 16, minute = 30),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 9.99
        )


        // Add the new tarification data
        inputSqlGroupeRepositorys.TarificationRepository().add(newTarification) { addedTarification ->
            val client = inputSqlClientRepo.modelList.find { clientToUpdate ->
                clientToUpdate.id == addedTarification.idClient
            }?.copy(
                idActiveTypeTarificationDataBase = addedTarification.idTypeTarification
            )
            if (client != null) {
                inputSqlClientRepo.update(client)
            }
        }
    }

    fun getProduitInfos(id: Long): InputEtInfosSqlModels.ProduitInfos? {
       return inputSqlProduitInfosRepository.modelList.find { it.id == id }
    }

    fun getClient(idClient: Long): InputEtInfosSqlModels.ClientDataBase? {
       return inputSqlClientRepo.modelList.find { it.id == idClient }
    }
    fun getTypeTarification(id: Long): InputEtInfosSqlModels.TypeTarificationDataBase? {
       return typeTarificationInputSqlRepo.modelList.find { it.id == id }
    }
}
