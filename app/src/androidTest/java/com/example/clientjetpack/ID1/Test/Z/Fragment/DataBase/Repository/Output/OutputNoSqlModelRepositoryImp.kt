package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Output

import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorys
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Models.NoSqlDataBases
import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Modules.covertireDepitSqlAuNonSqlShemaDataBase
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
        loadImbriquantData(tarificationRepository.modelList)
        observeTarificationData()
    }

    private fun observeTarificationData() {
        repositoryScope.launch {
            val tarificationRepositoryImp =
                tarificationRepository as? InputEtInfosSqlGroupeRepositorysImp.TarificationRepositoryImp

            tarificationRepositoryImp?._dataFlow?.collectLatest { tarificationEntries ->
                loadImbriquantData(tarificationEntries)
            }
        }
    }

    private fun loadImbriquantData(tarificationEntries: List<InputEtInfosSqlModels.Tarification>) {
        val noSqlDataBases = NoSqlDataBases(
            tarificationEntries.toMutableList(),
            produitRepository.modelList.toMutableList(),
            clientRepository.modelList.toMutableList()
        )

        _imbriquantFlow.value = covertireDepitSqlAuNonSqlShemaDataBase(
            noSqlDataBases
        )
    }
}
