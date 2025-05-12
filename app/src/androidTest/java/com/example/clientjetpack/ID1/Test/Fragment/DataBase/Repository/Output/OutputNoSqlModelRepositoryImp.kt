package com.example.clientjetpack.ID1.Test.Fragment.DataBase.Repository.Output

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorys
import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OutputNoSqlModelRepositoryImp(
    private val inputEtInfosSqlGroupeRepositorys: InputEtInfosSqlGroupeRepositorys
) : OutputNoSqlModelRepository {
    private val _imbriquantFlow = MutableStateFlow(
        com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel(
            emptyList()
        )
    )
    override val dataFlow: StateFlow<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel> = _imbriquantFlow.asStateFlow()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Creating repository instances using the factory methods
    private val produitRepository = inputEtInfosSqlGroupeRepositorys.ProduitInfosRepository()
    private val clientRepository = inputEtInfosSqlGroupeRepositorys.ClientDataBase_Repository()
    private val tarificationRepository = inputEtInfosSqlGroupeRepositorys.TarificationRepository()

    init {
        loadImbriquantData()
        observeTarificationData()
    }

    private fun observeTarificationData() {
        repositoryScope.launch {
            // Access _dataFlow from tarificationRepository but with appropriate casting
            val tarificationRepositoryImp = tarificationRepository as? InputEtInfosSqlGroupeRepositorysImp.TarificationRepositoryImp
            tarificationRepositoryImp?._dataFlow?.collectLatest { _ ->
                loadImbriquantData()
            }
        }
    }

    override fun loadImbriquantData() {
        repositoryScope.launch {
            val tarificationEntries = tarificationRepository.modelList

            val produitsList = mutableListOf<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit>()

            for (produitDB in produitRepository.modelList) {
                val produitId = produitDB.id
                val produitClients = mutableListOf<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client>()

                val uniqueClientIds = mutableSetOf<Long>()
                for (entry in tarificationEntries) {
                    if (entry.idProduit == produitId) {
                        uniqueClientIds.add(entry.idClient)
                    }
                }

                for (clientId in uniqueClientIds) {
                    val clientDB = clientRepository.modelList.find { it.id == clientId }
                    if (clientDB != null) {
                        val clientEntries = tarificationEntries.filter {
                            it.idProduit == produitId && it.idClient == clientId
                        }

                        val uniqueTypeIds = clientEntries.map { it.idTypeTarification }.toSet()

                        val typeTarifications = mutableListOf<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client.TypeTarification>()

                        for (typeId in uniqueTypeIds) {
                            val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                                .sortedByDescending { it.vidTimestamp }

                            if (typeEntries.isNotEmpty()) {
                                val latestTimestamp = typeEntries.first().vidTimestamp

                                val priceList = typeEntries.map { entry ->
                                    com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                                        vidTimestamp = entry.vidTimestamp,
                                        valeur = entry.prixCurrency
                                    )
                                }

                                val typeTarification =
                                    com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client.TypeTarification(
                                        vidTimestamp = latestTimestamp,
                                        id = typeId,
                                        PrixsCurrency = priceList
                                    )

                                typeTarifications.add(typeTarification)
                            }
                        }

                        if (typeTarifications.isNotEmpty()) {
                            val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }

                            val client = com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client(
                                vidTimestamp = clientLatestTimestamp,
                                id = clientId,
                                typeTarification = typeTarifications
                            )

                            produitClients.add(client)
                        }
                    }
                }

                val produitLatestTimestamp = if (produitClients.isNotEmpty()) {
                    produitClients.maxOf { it.vidTimestamp }
                } else {
                    System.currentTimeMillis()
                }

                val produit = com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit(
                    vidTimestamp = produitLatestTimestamp,
                    id = produitId,
                    clients = produitClients
                )

                produitsList.add(produit)
            }

            _imbriquantFlow.value =
                com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel(
                    produitsList
                )
        }
    }
}
