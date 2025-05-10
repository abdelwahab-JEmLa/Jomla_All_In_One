package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.compose.runtime.mutableStateListOf
import androidx.room.PrimaryKey

class AB_ReferentialSepareDataBases {
    data class ProduitDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String,
    )

    data class ClientDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String = "Non Difinie",
        val idActiveTypeTarificationDataBase: Long = 0,
    )

    data class TypeTarificationDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val typeTarificationEnum: TypeTarificationEnum,
    )

    enum class TypeTarificationEnum {
        ParBenifice,
        Historique,
        LeMaxPrixArrive
    }
}

class B_GroupeRepositoryImp {
    // Explicitly set IDs to match the ones referenced in AA_TarificationDataBaseFacileEntre
    class ProduitDataBase_RepositoryImp {
        var modelList: List<AB_ReferentialSepareDataBases.ProduitDataBase> = mutableStateListOf(
            AB_ReferentialSepareDataBases.ProduitDataBase(
                id = 1L,  // Explicitly set ID to 1 to match references
                nom = "Caramels"
            ),
            AB_ReferentialSepareDataBases.ProduitDataBase(
                id = 2L,  // Explicitly set ID to 2 to match references
                nom = "Chocolats"
            )
        )
    }

    companion object {
        // Make clientRepository accessible statically to ensure we always reference the same list
        val clientRepository = ClientDataBase_RepositoryImp()
    }

    class ClientDataBase_RepositoryImp {
        // Changed from List to MutableList to make direct updates possible
        var modelList = mutableStateListOf(
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 2L,  // Explicitly set ID
                nom = "Client B",
            ),
        )

        // Added methods to ClientDataBase_RepositoryImp to modify its own list
        fun add(client: AB_ReferentialSepareDataBases.ClientDataBase) {
            // Check if client with this ID already exists
            val existingIndex = modelList.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                // Add only if not already exists
                modelList.add(client)
            } else {
                // Update if exists
                modelList[existingIndex] = client
            }
        }

        fun update(client: AB_ReferentialSepareDataBases.ClientDataBase) {
            val index = modelList.indexOfFirst { it.id == client.id }
            if (index != -1) {
                modelList[index] = client
            }
        }
    }

    class TypeTarificationDataBase_RepositoryImp {
        var modelList: List<AB_ReferentialSepareDataBases.TypeTarificationDataBase> =
            mutableStateListOf(
                AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                    id = 1L,  // Explicitly set ID
                    typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice
                ),
                AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                    id = 2L,  // Explicitly set ID
                    typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique
                ),
                AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                    id = 3L,  // Explicitly set ID
                    typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive
                )
            )
    }

    fun addNewData(data: AB_ReferentialSepareDataBases.ClientDataBase) {
        // Fixed: Use the add method on the clientRepository
        clientRepository.add(data)
    }

    fun updateData(data: AB_ReferentialSepareDataBases.ClientDataBase) {
        // Fixed: Use the update method on the clientRepository
        clientRepository.update(data)
    }
}
