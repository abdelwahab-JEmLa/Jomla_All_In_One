package com.example.clientjetpack.Id1.PrixChangable.Test

import android.icu.util.Currency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class B_TarificationViewModel(
    private val tarificationDataBaseFacileEntre_RepositoryImp: TarificationDataBaseFacileEntre_RepositoryImp
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
                        val typeTarifications = mutableListOf<A_DataBase_Imbricant.Produit.Client.TypeTarification>()
                        val uniqueTypeIds = mutableSetOf<Long>()
                        
                        // Find all tarification types for this client and product
                        for (entry in tarificationEntries) {
                            if (entry.idProduit == produitId && entry.idClient == clientId) {
                                uniqueTypeIds.add(entry.idTypeTarification)
                            }
                        }
                        
                        // Process each tarification type
                        for (typeId in uniqueTypeIds) {
                            var latestEntry: AA_TarificationDataBaseFacileEntre? = null
                            var latestTimestamp: Long = 0
                            
                            // Find the latest entry for this type
                            for (entry in tarificationEntries) {
                                if (entry.idProduit == produitId && 
                                    entry.idClient == clientId && 
                                    entry.idTypeTarification == typeId && 
                                    entry.vidTimestamp > latestTimestamp) {
                                    
                                    latestEntry = entry
                                    latestTimestamp = entry.vidTimestamp
                                }
                            }
                            
                            if (latestEntry != null) {
                                val prixCurrency = A_DataBase_Imbricant.Produit.Client.TypeTarification.PrixCurrency(
                                    vidTimestamp = latestEntry.vidTimestamp,
                                    currency = Currency.getInstance("USD") // Default currency
                                )
                                
                                val typeTarification = A_DataBase_Imbricant.Produit.Client.TypeTarification(
                                    vidTimestamp = latestEntry.vidTimestamp,
                                    id = typeId,
                                    PrixsCurrency = listOf(prixCurrency)
                                )
                                
                                typeTarifications.add(typeTarification)
                            }
                        }
                        
                        if (typeTarifications.isNotEmpty()) {
                            var clientLatestTimestamp: Long = 0
                            for (typeTarif in typeTarifications) {
                                if (typeTarif.vidTimestamp > clientLatestTimestamp) {
                                    clientLatestTimestamp = typeTarif.vidTimestamp
                                }
                            }
                            
                            val client = A_DataBase_Imbricant.Produit.Client(
                                vidTimestamp = clientLatestTimestamp,
                                id = clientId,
                                typeTarification = typeTarifications
                            )
                            
                            produitClients.add(client)
                        }
                    }
                }
                
                var produitLatestTimestamp: Long = 0
                for (client in produitClients) {
                    if (client.vidTimestamp > produitLatestTimestamp) {
                        produitLatestTimestamp = client.vidTimestamp
                    }
                }
                
                if (produitLatestTimestamp == 0L) {
                    produitLatestTimestamp = System.currentTimeMillis()
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

    /**
     * Converts a timestamp to a formatted date string
     */
    fun strDateFromVidTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}
