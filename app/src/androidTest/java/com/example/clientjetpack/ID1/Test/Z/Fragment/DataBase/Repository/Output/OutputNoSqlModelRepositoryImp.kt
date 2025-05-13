package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Output

import com.example.clientjetpack.ID1.Test._ID1.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorys
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.ID1.Test._ID1.Test.Models.NoSqlDataBases
import com.example.clientjetpack.ID1.Test._ID1.Test.Modules.covertireDepitSqlAuNonSqlShemaDataBase
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
        val noSqlDataBases = NoSqlDataBases(
            tarificationRepository.modelList.toMutableList(),
            produitRepository.modelList.toMutableList(),
            clientRepository.modelList.toMutableList()
        )

        covertireDepitSqlAuNonSqlShemaDataBase(
            noSqlDataBases
        )
        observeTarificationData()
    }

    private fun observeTarificationData() {
        repositoryScope.launch {
            val tarificationRepositoryImp =
                tarificationRepository as? InputEtInfosSqlGroupeRepositorysImp.TarificationRepositoryImp

            tarificationRepositoryImp?._dataFlow?.collectLatest { tarificationEntries ->
                val noSqlDataBases = NoSqlDataBases(
                    tarificationEntries.toMutableList(),
                    produitRepository.modelList.toMutableList(),
                    clientRepository.modelList.toMutableList()
                )

                covertireDepitSqlAuNonSqlShemaDataBase(
                    noSqlDataBases
                )
            }
        }
    }
}
