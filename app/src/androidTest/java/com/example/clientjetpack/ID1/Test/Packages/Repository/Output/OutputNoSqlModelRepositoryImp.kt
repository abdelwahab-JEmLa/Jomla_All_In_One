// File: OutputNoSqlModelRepositoryImp.kt
package com.example.clientjetpack.ID1.Test.Packages.Repository.Output

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Repository.Output.OutputNoSqlModelRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.NoSqlDataBases
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Modules.covertireDepitSqlAuNonSqlShemaDataBase
import com.example.clientjetpack.ID1.Test.Packages.Repository.Input.InputEtInfosSqlGroupeRepositorys
import com.example.clientjetpack.ID1.Test.Packages.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
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
        val noSqlDataBases = NoSqlDataBases(
            tarificationEntries.toMutableList(),
            produitEntries.toMutableList(),
            clientEntries.toMutableList()
        )

        val newData = covertireDepitSqlAuNonSqlShemaDataBase(noSqlDataBases)
        _imbriquantFlow.value = newData
    }

    fun refreshData() {
        // Get the latest data from all repositories
        val latestTarificationData = tarificationRepository.modelList
        val latestProduitData = produitRepository.modelList
        val latestClientData = clientRepository.modelList

        loadImbriquantData(
            latestTarificationData,
            latestProduitData,
            latestClientData
        )
    }
}
