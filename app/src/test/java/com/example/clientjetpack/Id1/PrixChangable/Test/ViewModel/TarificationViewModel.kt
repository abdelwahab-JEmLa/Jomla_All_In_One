package com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.InputSqlGroupeRepositorysImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel(
    private val tarificationDataBaseFacileEntreRepositoryImp:
    InputSqlGroupeRepositorysImp.TarificationDataBaseFacileEntreRepositoryImp
): ViewModel(){
    private val _imbriquantFlow = MutableStateFlow(OutputViewModelNoSqlModel(emptyList()))
    val imbriquantFlow: StateFlow<OutputViewModelNoSqlModel> = _imbriquantFlow.asStateFlow()

    init {
        loadImbriquantData()
        observeTarificationData()
    }

    private fun observeTarificationData() {
        viewModelScope.launch {
            tarificationDataBaseFacileEntreRepositoryImp._dataFlow.collectLatest { _ ->
                loadImbriquantData()
            }
        }
    }

    private fun loadImbriquantData() {
        viewModelScope.launch {
            val produitRepository = InputSqlGroupeRepositorysImp.ProduitDataBase_RepositoryImp()
            val clientRepository = InputSqlGroupeRepositorysImp.clientRepository
            val tarificationEntries = tarificationDataBaseFacileEntreRepositoryImp.modelList

            val produitsList = mutableListOf<OutputViewModelNoSqlModel.Produit>()

            for (produitDB in produitRepository.modelList) {
                val produitId = produitDB.id
                val produitClients = mutableListOf<OutputViewModelNoSqlModel.Produit.Client>()

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

                        val typeTarifications = mutableListOf<OutputViewModelNoSqlModel.Produit.Client.TypeTarification>()

                        for (typeId in uniqueTypeIds) {
                            val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                                .sortedByDescending { it.vidTimestamp }

                            if (typeEntries.isNotEmpty()) {
                                val latestTimestamp = typeEntries.first().vidTimestamp

                                val priceList = typeEntries.map { entry ->
                                    OutputViewModelNoSqlModel.Produit.Client.TypeTarification.Prix(
                                        vidTimestamp = entry.vidTimestamp,
                                        valeur = entry.prixCurrency
                                    )
                                }

                                val typeTarification =
                                    OutputViewModelNoSqlModel.Produit.Client.TypeTarification(
                                        vidTimestamp = latestTimestamp,
                                        id = typeId,
                                        PrixsCurrency = priceList
                                    )

                                typeTarifications.add(typeTarification)
                            }
                        }

                        if (typeTarifications.isNotEmpty()) {
                            val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }

                            val client = OutputViewModelNoSqlModel.Produit.Client(
                                vidTimestamp = clientLatestTimestamp,
                                id = clientId,
                                typeTarification = typeTarifications
                            )

                            produitClients.add(client)
                        }
                    } else {
                        println("WARNING: Client with ID $clientId has tarification entries but is not found in client repository!")
                    }
                }

                val produitLatestTimestamp = if (produitClients.isNotEmpty()) {
                    produitClients.maxOf { it.vidTimestamp }
                } else {
                    System.currentTimeMillis()
                }

                val produit = OutputViewModelNoSqlModel.Produit(
                    vidTimestamp = produitLatestTimestamp,
                    id = produitId,
                    clients = produitClients
                )

                produitsList.add(produit)
            }

            _imbriquantFlow.value = OutputViewModelNoSqlModel(produitsList)
        }
    }
}
