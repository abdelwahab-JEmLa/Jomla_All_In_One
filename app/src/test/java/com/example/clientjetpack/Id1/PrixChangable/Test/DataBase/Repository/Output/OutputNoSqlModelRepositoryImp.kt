package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Output

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.InputSqlGroupeRepositorysImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OutputNoSqlModelRepositoryImp(
    private val tarificationDataBaseFacileEntreRepositoryImp:
    InputSqlGroupeRepositorysImp.TarificationDataBaseFacileEntreRepositoryImp
) : OutputNoSqlModelRepository {
    private val _imbriquantFlow = MutableStateFlow(OutputNoSqlModel(emptyList()))
    override val imbriquantFlow: StateFlow<OutputNoSqlModel> = _imbriquantFlow.asStateFlow()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        loadImbriquantData()
        observeTarificationData()
    }

    private fun observeTarificationData() {
        repositoryScope.launch {
            tarificationDataBaseFacileEntreRepositoryImp._dataFlow.collectLatest { _ ->
                loadImbriquantData()
            }
        }
    }

    override fun loadImbriquantData() {
        repositoryScope.launch {
            val produitRepository = InputSqlGroupeRepositorysImp.ProduitDataBase_RepositoryImp()
            val clientRepository = InputSqlGroupeRepositorysImp.clientRepository
            val tarificationEntries = tarificationDataBaseFacileEntreRepositoryImp.modelList

            val produitsList = mutableListOf<OutputNoSqlModel.Produit>()

            for (produitDB in produitRepository.modelList) {
                val produitId = produitDB.id
                val produitClients = mutableListOf<OutputNoSqlModel.Produit.Client>()

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

                        val typeTarifications = mutableListOf<OutputNoSqlModel.Produit.Client.TypeTarification>()

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

                                val typeTarification =
                                    OutputNoSqlModel.Produit.Client.TypeTarification(
                                        vidTimestamp = latestTimestamp,
                                        id = typeId,
                                        PrixsCurrency = priceList
                                    )

                                typeTarifications.add(typeTarification)
                            }
                        }

                        if (typeTarifications.isNotEmpty()) {
                            val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }

                            val client = OutputNoSqlModel.Produit.Client(
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

                val produit = OutputNoSqlModel.Produit(
                    vidTimestamp = produitLatestTimestamp,
                    id = produitId,
                    clients = produitClients
                )

                produitsList.add(produit)
            }

            _imbriquantFlow.value = OutputNoSqlModel(produitsList)
        }
    }
}
