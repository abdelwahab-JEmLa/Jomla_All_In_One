package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class _TarificationViewModel(
    private val tarificationDataBaseFacileEntre_RepositoryImp: TarificationDataBaseFacileEntre_RepositoryImp
): ViewModel(){
    private val _imbriquantFlow = MutableStateFlow(A_DataBase_Imbricant(emptyList()))
    val imbriquantFlow: StateFlow<A_DataBase_Imbricant> = _imbriquantFlow.asStateFlow()

    init {
        observeTarificationData()
    }

    private fun observeTarificationData() {
        viewModelScope.launch {
            tarificationDataBaseFacileEntre_RepositoryImp._dataFlow.collectLatest { _ ->
                loadImbriquantData()
            }
        }
    }

    private fun loadImbriquantData() {
        viewModelScope.launch {
            val produitRepository = B_GroupeRepositoryImp.ProduitDataBase_RepositoryImp()
            val clientRepository = B_GroupeRepositoryImp.clientRepository
            val tarificationEntries = tarificationDataBaseFacileEntre_RepositoryImp.modelList

            val produitsList = mutableListOf<A_DataBase_Imbricant.Produit>()

            for (produitDB in produitRepository.modelList) {
                val produitId = produitDB.id
                val produitClients = mutableListOf<A_DataBase_Imbricant.Produit.Client>()

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

                        val typeTarifications = mutableListOf<A_DataBase_Imbricant.Produit.Client.TypeTarification>()

                        for (typeId in uniqueTypeIds) {
                            val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                                .sortedByDescending { it.vidTimestamp }

                            if (typeEntries.isNotEmpty()) {
                                val latestTimestamp = typeEntries.first().vidTimestamp

                                val priceList = typeEntries.map { entry ->
                                    A_DataBase_Imbricant.Produit.Client.TypeTarification.Prix(
                                        vidTimestamp = entry.vidTimestamp,
                                        valeur = entry.prixCurrency
                                    )
                                }

                                val typeTarification = A_DataBase_Imbricant.Produit.Client.TypeTarification(
                                    vidTimestamp = latestTimestamp,
                                    id = typeId,
                                    PrixsCurrency = priceList
                                )

                                typeTarifications.add(typeTarification)
                            }
                        }

                        if (typeTarifications.isNotEmpty()) {
                            val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }

                            val client = A_DataBase_Imbricant.Produit.Client(
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

                val produit = A_DataBase_Imbricant.Produit(
                    vidTimestamp = produitLatestTimestamp,
                    id = produitId,
                    clients = produitClients
                )

                produitsList.add(produit)
            }

            _imbriquantFlow.value = A_DataBase_Imbricant(produitsList)
        }
    }
}
