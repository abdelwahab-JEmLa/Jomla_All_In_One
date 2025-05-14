// File: TarificationViewModel.kt
package com.example.clientjetpack.ID1.Test.Packages.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.ID1.Test.Packages.Function.createTimestamp
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.Packages.Repository.Output.OutputNoSqlModelRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel(
) : ViewModel() {
    val inputSqlGroupeRepositorys = InputEtInfosSqlGroupeRepositorysImp()

    val outputNoSqlRepository =
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
            outputNoSqlRepository.dataFlow.collectLatest { data ->
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


     fun addTest(viewModel: TarificationViewModel) {
        viewModel.addNewProduitInfos(
            InputEtInfosSqlModels.ProduitInfos(
                id = 5L,
                nom = "Produit 5"
            )
        )
        val newTarification =
            InputEtInfosSqlModels.Tarification(
                vidTimestamp = createTimestamp(day = 1, hour = 13, minute = 30),
                idProduit = 5L,
                idClient = 1L,
                idTypeTarification = 2L,
                prixCurrency = 20.99
            )

        viewModel.addNewTestDataTarification(
            newTarification
        )
    }

    fun addNewTestDataTarification(newTarification: InputEtInfosSqlModels.Tarification) {
        tarificationRepository.add(newTarification) { addedTarification ->
            refreshOutputData()
        }
    }

    fun addNewProduitInfos(data: InputEtInfosSqlModels.ProduitInfos) {
        inputSqlProduitInfosRepository.add(data) { addedProduct ->
            refreshOutputData()
        }
    }

    fun refreshOutputData() {
        (outputNoSqlRepository as? OutputNoSqlModelRepositoryImp)?.refreshData()
    }
}
