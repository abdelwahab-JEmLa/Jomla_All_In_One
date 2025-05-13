// File: OutputNoSqlModelRepositoryImp.kt
package com.example.clientjetpack.ID1.Test.Packages.Repository.Output

import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Models.NoSqlDataBases
import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel
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

    fun covertireDepitSqlAuNonSqlShemaDataBase(noSqlDataBases: NoSqlDataBases): OutputNoSqlModel {
        val tarificationEntries = noSqlDataBases.tarificationEntries
        val produitInfos = noSqlDataBases.produitInfos
        val clientDataBase = noSqlDataBases.clientDataBase

        val produitsList = mutableListOf<OutputNoSqlModel.Produit>()

        // Process each product in the database
        for (produitDB in produitInfos) {
            val produitId = produitDB.id
            val produitClients = mutableListOf<OutputNoSqlModel.Produit.Client>()

            // Find clients for this product
            val uniqueClientIds = mutableSetOf<Long>()
            for (entry in tarificationEntries) {
                if (entry.idProduit == produitId) {
                    uniqueClientIds.add(entry.idClient)
                }
            }


            for (clientId in uniqueClientIds) {
                val clientDB = clientDataBase.find { it.id == clientId }
                if (clientDB != null) {
                    val clientEntries = tarificationEntries.filter {
                        it.idProduit == produitId && it.idClient == clientId
                    }

                    val uniqueTypeIds = clientEntries.map { it.idTypeTarification }.toSet()
                    val typeTarifications =
                        mutableListOf<OutputNoSqlModel.Produit.Client.TypeTarification>()

                    for (typeId in uniqueTypeIds) {
                        val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                            .sortedByDescending { it.vidTimestamp }

                        if (typeEntries.isNotEmpty()) {
                            val latestTimestamp = typeEntries.first().vidTimestamp
                            val priceList = typeEntries.map { entry ->
                                OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                                    vidTimestamp = entry.vidTimestamp,
                                    valeur = entry.prixCurrency
                                )
                            }

                            typeTarifications.add(
                                OutputNoSqlModel.Produit.Client.TypeTarification(
                                    vidTimestamp = latestTimestamp,
                                    id = typeId,
                                    PrixsCurrency = priceList
                                )
                            )
                        }
                    }

                    if (typeTarifications.isNotEmpty()) {
                        val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }
                        produitClients.add(
                            OutputNoSqlModel.Produit.Client(
                                vidTimestamp = clientLatestTimestamp,
                                id = clientId,
                                typeTarification = typeTarifications
                            )
                        )
                    }
                }
            }

            // Even if no clients found, still include the product in the list
            val produitLatestTimestamp = if (produitClients.isNotEmpty()) {
                produitClients.maxOf { it.vidTimestamp }
            } else {
                System.currentTimeMillis()
            }

            produitsList.add(
                OutputNoSqlModel.Produit(
                    vidTimestamp = produitLatestTimestamp,
                    id = produitId,
                    clients = produitClients
                )
            )
        }

        return OutputNoSqlModel(produitsList)
    }

}
