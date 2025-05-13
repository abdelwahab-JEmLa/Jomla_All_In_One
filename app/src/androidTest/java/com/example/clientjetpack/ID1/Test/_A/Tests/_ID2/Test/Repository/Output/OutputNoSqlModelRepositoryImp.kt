package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Output

import android.util.Log
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Models.NoSqlDataBases
import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Modules.covertireDepitSqlAuNonSqlShemaDataBase
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Input.InputEtInfosSqlGroupeRepositorys
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OutputNoSqlModelRepositoryImp(
    private val inputEtInfosSqlGroupeRepositorys: InputEtInfosSqlGroupeRepositorys,
) : OutputNoSqlModelRepository {
    private val TAG = "OutputNoSqlModelRepo" // Tag for logging

    private val _imbriquantFlow = MutableStateFlow(
        OutputNoSqlModel(
            emptyList()
        )
    )
    override val dataFlow: StateFlow<OutputNoSqlModel> = _imbriquantFlow.asStateFlow()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Creating repository instances using the factory methods
    private val produitRepository = inputEtInfosSqlGroupeRepositorys.ProduitInfosRepository()
    private val clientRepository = inputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository()
    private val tarificationRepository = inputEtInfosSqlGroupeRepositorys.TarificationRepository()

    init {
        Log.d(TAG, "Initializing repository with initial data")
        loadImbriquantData(
            tarificationRepository.modelList,
            produitRepository.modelList,
            clientRepository.modelList
        )
        observeProduitData()
        observeTarificationData()
    }

    private fun observeTarificationData() {
        repositoryScope.launch {
            val tarificationRepositoryImp =
                tarificationRepository as? InputEtInfosSqlGroupeRepositorysImp.TarificationRepositoryImp

            tarificationRepositoryImp?._dataFlow?.collectLatest { tarificationEntries ->
                Log.d(TAG, "Tarification data updated: ${tarificationEntries.size} entries")
                loadImbriquantData(
                    tarificationEntries,
                    produitRepository.modelList,
                    clientRepository.modelList
                )
            }
        }
    }

    private fun observeProduitData() {
        repositoryScope.launch {
            val produitRepositoryImp =
                produitRepository as? InputEtInfosSqlGroupeRepositorysImp.ProduitDataBase_RepositoryImp

            produitRepositoryImp?._dataFlow?.collectLatest { produitEntries ->
                Log.d(TAG, "Product data updated: ${produitEntries.size} entries")
                loadImbriquantData(
                    tarificationRepository.modelList,
                    produitEntries,
                    clientRepository.modelList
                )
            }
        }
    }

    private fun loadImbriquantData(
        tarificationEntries: List<InputEtInfosSqlModels.Tarification>,
        produitEntries: List<InputEtInfosSqlModels.ProduitInfos>,
        clientEntries: List<InputEtInfosSqlModels.ClientDataBase>
    ) {
        Log.d(TAG, "Loading imbriquant data - Tarifications: ${tarificationEntries.size}, Products: ${produitEntries.size}")

        val noSqlDataBases = NoSqlDataBases(
            tarificationEntries.toMutableList(),
            produitEntries.toMutableList(),
            clientEntries.toMutableList()
        )

        val newData = covertireDepitSqlAuNonSqlShemaDataBase(noSqlDataBases)
        _imbriquantFlow.value = newData

        Log.d(TAG, "Updated imbriquant data - Products in output: ${newData.produits.size}")
    }

    fun refreshData() {
        Log.d(TAG, "Manual refresh requested")
        // Get the latest data from all repositories
        val latestTarificationData = tarificationRepository.modelList
        val latestProduitData = produitRepository.modelList
        val latestClientData = clientRepository.modelList

        Log.d(TAG, "Current product count: ${latestProduitData.size}")

        loadImbriquantData(
            latestTarificationData,
            latestProduitData,
            latestClientData
        )
    }
}
