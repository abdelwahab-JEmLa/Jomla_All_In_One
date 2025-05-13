package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Output.OutputNoSqlModelRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel(
) : ViewModel() {
    private val TAG = "TarificationViewModel"
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
                Log.d(TAG, "Received updated output NoSQL data, products: ${data.produits.size}")
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
        Log.d(TAG, "Adding new tarification for product: ${newTarification.idProduit}")
        tarificationRepository.add(newTarification) { addedTarification ->
            Log.d(TAG, "Successfully added tarification for product: ${addedTarification.idProduit}")
            refreshOutputData()
        }
    }

    fun addNewProduitInfos(data: InputEtInfosSqlModels.ProduitInfos) {
        Log.d(TAG, "Adding new product: ${data.id} - ${data.nom}")
        inputSqlProduitInfosRepository.add(data) { addedProduct ->
            Log.d(TAG, "Successfully added product: ${addedProduct.id} - ${addedProduct.nom}")
            refreshOutputData()
        }
    }

    fun refreshOutputData() {
        Log.d(TAG, "Requesting output data refresh")
        (outputNoSqlRepository as? OutputNoSqlModelRepositoryImp)?.refreshData()
    }
}
