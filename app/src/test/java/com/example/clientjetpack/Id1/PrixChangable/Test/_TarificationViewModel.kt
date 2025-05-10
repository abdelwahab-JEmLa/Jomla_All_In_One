package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class _TarificationViewModel(
    private val tarificationDataBaseFacileEntre_RepositoryImp: B_GroupeRepositoryImp
        .TarificationDataBaseFacileEntre_RepositoryImp
): ViewModel(){
    // State for tarification data
    private val _imbriquantFlow = MutableStateFlow(A_DataBase_Imbricant(emptyList()))
    val imbriquantFlow: StateFlow<A_DataBase_Imbricant> = _imbriquantFlow

    init {
        loadImbriquantData()
    }

    private fun loadImbriquantData() {
        viewModelScope.launch {
            val produitRepository = B_GroupeRepositoryImp.ProduitDataBase_RepositoryImp()
            val clientRepository = B_GroupeRepositoryImp.ClientDataBase_RepositoryImp()
            val typeTarificationRepository = B_GroupeRepositoryImp.TypeTarificationDataBase_RepositoryImp()
            val tarificationEntries = tarificationDataBaseFacileEntre_RepositoryImp.modelList

            // Create list to hold all produits
            val produitsList = mutableListOf<A_DataBase_Imbricant.Produit>()

            // Loop through all produits
            for (produitDB in produitRepository.modelList) {
                val produitId = produitDB.id
                val produitClients = mutableListOf<A_DataBase_Imbricant.Produit.Client>()

                // Find all clients that have entries for this product
                val uniqueClientIds = mutableSetOf<Long>()
                for (entry in tarificationEntries) {
                    if (entry.idProduit == produitId) {
                        uniqueClientIds.add(entry.idClient)
                    }
                }

                // Process each client
                for (clientId in uniqueClientIds) {
                    val clientDB = clientRepository.modelList.find { it.id == clientId }
                    if (clientDB != null) {
                        // Find all entries for this client and product
                        val clientEntries = tarificationEntries.filter {
                            it.idProduit == produitId && it.idClient == clientId
                        }

                        // Get all unique tarification types for this client and product
                        val uniqueTypeIds = clientEntries.map { it.parentVidTypeTarification }.toSet()

                        // Build list of tarification types with their prices
                        val typeTarifications = mutableListOf<A_DataBase_Imbricant.Produit.Client.TypeTarification>()

                        for (typeId in uniqueTypeIds) {
                            // Get all entries for this specific tarification type
                            val typeEntries = clientEntries.filter { it.parentVidTypeTarification == typeId }
                                .sortedByDescending { it.vidTimestamp }

                            if (typeEntries.isNotEmpty()) {
                                // Get latest timestamp for this tarification type
                                val latestTimestamp = typeEntries.first().vidTimestamp

                                // Create price entries
                                val priceList = typeEntries.map { entry ->
                                    A_DataBase_Imbricant.Produit.Client.TypeTarification.Prix(
                                        vidTimestamp = entry.vidTimestamp,
                                        valeur = entry.prixCurrency
                                    )
                                }

                                // Create tarification type object
                                val typeTarification = A_DataBase_Imbricant.Produit.Client.TypeTarification(
                                    vidTimestamp = latestTimestamp,
                                    id = typeId,
                                    PrixsCurrency = priceList
                                )

                                typeTarifications.add(typeTarification)
                            }
                        }

                        if (typeTarifications.isNotEmpty()) {
                            // Find latest timestamp across all tarification types
                            val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }

                            val client = A_DataBase_Imbricant.Produit.Client(
                                vidTimestamp = clientLatestTimestamp,
                                id = clientId,
                                typeTarification = typeTarifications
                            )

                            produitClients.add(client)
                        }
                    }
                }

                // Find latest timestamp across all clients
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

            // Update the flow with the new A_DataBase_Imbricant
            _imbriquantFlow.value = A_DataBase_Imbricant(produitsList)
        }
    }

    fun refreshData() {
        loadImbriquantData()
    }
}
