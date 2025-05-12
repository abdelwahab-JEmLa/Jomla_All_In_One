package com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Output.OutputNoSqlModelRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel(
) : ViewModel() {
    val inputSqlGroupeRepositorys = InputEtInfosSqlGroupeRepositorysImp()

    val outputNoSqlModelRepository =
        OutputNoSqlModelRepositoryImp(inputSqlGroupeRepositorys)

    private val _OutputNoSqlFlow = MutableStateFlow(
        OutputNoSqlModel(
            emptyList()
        )
    )
    val outputNoSqlFlow: StateFlow<OutputNoSqlModel> = _OutputNoSqlFlow.asStateFlow()

    private val inputSqlClientRepo = inputSqlGroupeRepositorys.ClientDataBase_Repository()

    private val typeTarificationInputSqlRepo = inputSqlGroupeRepositorys
        .TypeTarificationInfosRepository()
    private val inputSqlProduitInfosRepository = inputSqlGroupeRepositorys
        .ProduitInfosRepository()

    private val tarificationRepository = inputSqlGroupeRepositorys.TarificationRepository()

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

    fun getSqlProduitInfos(id: Long): InputEtInfosSqlModels.ProduitInfos? {
        return inputSqlProduitInfosRepository.modelList.find { it.id == id }
    }

    fun getSqlClient(idClient: Long): InputEtInfosSqlModels.ClientDataBase? {
        return inputSqlClientRepo.modelList.find { it.id == idClient }
    }

    fun getSqlTypeTarification(id: Long): InputEtInfosSqlModels.TypeTarificationDataBase? {
        return typeTarificationInputSqlRepo.modelList.find { it.id == id }
    }

    fun addNewTestDataTarificationEtClient(newTarification: InputEtInfosSqlModels.Tarification) {

        tarificationRepository.add(newTarification) { addedTarification ->
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
}
