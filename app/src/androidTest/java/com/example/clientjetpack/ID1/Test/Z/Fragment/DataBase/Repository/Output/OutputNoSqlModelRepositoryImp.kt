package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Output

import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorys
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.junit.Assert

class OutputNoSqlModelRepositoryImp(
    private val inputEtInfosSqlGroupeRepositorys: InputEtInfosSqlGroupeRepositorys
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
        loadImbriquantData()
        observeTarificationData()
    }

    private fun observeTarificationData() {
        repositoryScope.launch {
            // Access _dataFlow from tarificationRepository with appropriate casting
            val tarificationRepositoryImp = tarificationRepository as? InputEtInfosSqlGroupeRepositorysImp.TarificationRepositoryImp

            // Explicitly collect from the repository's dataFlow to detect changes
            tarificationRepositoryImp?._dataFlow?.collectLatest { tarificationEntries ->
                // Reload data whenever the tarification entries change
                loadImbriquantData()
            }
        }
    }

    override fun loadImbriquantData() {
        repositoryScope.launch {
            // Fetch the latest tarification entries
            val tarificationEntries = tarificationRepository.modelList

            val produitsList = mutableListOf<OutputNoSqlModel.Produit>()

            for (produitDB in produitRepository.modelList) {
                val produitId = produitDB.id
                val produitClients = mutableListOf<OutputNoSqlModel.Produit.Client>()

                // Find all unique clients for this product
                val uniqueClientIds = mutableSetOf<Long>()
                for (entry in tarificationEntries) {
                    if (entry.idProduit == produitId) {
                        uniqueClientIds.add(entry.idClient)
                    }
                }

                for (clientId in uniqueClientIds) {
                    Assert.assertTrue(clientRepository.modelList.isNotEmpty())

                    val clientDB = clientRepository.modelList.find { it.id == clientId }
                    if (clientDB != null) {
                        // Filter tarification entries for this product and client
                        val clientEntries = tarificationEntries.filter {
                            it.idProduit == produitId && it.idClient == clientId
                        }

                        // Find all unique tarification types for this client
                        val uniqueTypeIds = clientEntries.map { it.idTypeTarification }.toSet()

                        val typeTarifications = mutableListOf<OutputNoSqlModel.Produit.Client.TypeTarification>()

                        for (typeId in uniqueTypeIds) {
                            // Get entries for this tarification type and sort by timestamp
                            val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                                .sortedByDescending { it.vidTimestamp }

                            if (typeEntries.isNotEmpty()) {
                                val latestTimestamp = typeEntries.first().vidTimestamp

                                // Create price list from all entries of this type
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

            // Update the flow with the new data
            _imbriquantFlow.value = OutputNoSqlModel(produitsList)
        }
    }
}
