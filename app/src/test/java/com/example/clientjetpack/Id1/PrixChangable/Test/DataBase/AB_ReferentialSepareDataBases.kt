package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase

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
        val clientRepository = ClientDataBase_RepositoryImp()
    }

    class ClientDataBase_RepositoryImp {
        var modelList = mutableStateListOf(
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 1L,
                nom = "Client A",
                idActiveTypeTarificationDataBase = 1L  // Set to match the tarification type
            ),
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 2L,  // Explicitly set ID
                nom = "Client B",
            ),
        )

        fun add(client: AB_ReferentialSepareDataBases.ClientDataBase) {
            val existingIndex = modelList.indexOfFirst { it.id == client.id }
            if (existingIndex == -1) {
                modelList.add(client)
            } else {
                modelList[existingIndex] = client
            }
        }

        fun update(
            client: AB_ReferentialSepareDataBases.ClientDataBase,
            onSuccess: (AB_ReferentialSepareDataBases.ClientDataBase) -> Unit = {},
        ) {
            val index = modelList.indexOfFirst { it.id == client.id }
            if (index != -1) {
                modelList[index] = client
                onSuccess(client)
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

    fun addNewData(
        data: AB_ReferentialSepareDataBases.ClientDataBase,
    ) {
        clientRepository.add(data)
    }
}
