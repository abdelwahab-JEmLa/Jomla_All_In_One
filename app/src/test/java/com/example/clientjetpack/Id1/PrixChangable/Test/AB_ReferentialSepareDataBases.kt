package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.compose.runtime.mutableStateListOf
import androidx.room.PrimaryKey
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp

class AB_ReferentialSepareDataBases {
    data class ProduitDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String,
    )

    data class ClientDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String,
    )

    data class TypeTarificationDataBase(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val typeTarificationEnum: TypeTarificationEnum,
        val itsTheActiveOne: Boolean = false,
        val clientId: Long, // Added client ID reference
    )

    enum class TypeTarificationEnum {
        ParBenifice,
        Historique,
        LeMaxPrixArrive
    }

    data class AA_TarificationDataBaseFacileEntre(
        val parentVidTypeTarification: Long,
        val vidTimestamp: Long,
        val idProduit: Long,
        val idClient: Long,
        val prixCurrency: Double,
    )
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

    class ClientDataBase_RepositoryImp {
        var modelList: List<AB_ReferentialSepareDataBases.ClientDataBase> = mutableStateListOf(
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 1L,  // Explicitly set ID
                nom = "Client A"
            ),
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 2L,  // Explicitly set ID
                nom = "Client B"
            ),
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 3L,  // Explicitly set ID
                nom = "Client C"
            ),
            AB_ReferentialSepareDataBases.ClientDataBase(
                id = 4L,  // Explicitly set ID
                nom = "Client D"
            )
        )
    }

    class TypeTarificationDataBase_RepositoryImp {
        // Hard-coded tarification types for each client instead of generating them dynamically
        var modelList: List<AB_ReferentialSepareDataBases.TypeTarificationDataBase> = mutableStateListOf(
            // Client 1 (ID: 1) tarification types
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 1L,
                typeTarificationEnum = AB_ReferentialSepareDataBases
                    .TypeTarificationEnum.ParBenifice,
                itsTheActiveOne = true,
                clientId = 1L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 2L,
                typeTarificationEnum = AB_ReferentialSepareDataBases
                    .TypeTarificationEnum.Historique,
                clientId = 1L
            ),


            // Client 2 (ID: 2) tarification types
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 4L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice,
                clientId = 2L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                itsTheActiveOne = true,

                id = 5L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique,
                clientId = 2L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 6L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive,
                clientId = 2L
            ),

        )
    }


    class TarificationDataBaseFacileEntre_RepositoryImp {
        var modelList: List<AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre> =
            mutableStateListOf(
                // Test data for Caramels (product id 1)
                AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre(
                    vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                    idProduit = 1L,
                    idClient = 1L, // Client 1
                    parentVidTypeTarification = 1L, // ParBenifice
                    prixCurrency = 2.99
                ),
                AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre(
                    vidTimestamp = createTimestamp(
                        day = 10,
                        hour = 14,
                        minute = 30
                    ),
                    idProduit = 1L,
                    idClient = 1L,
                    parentVidTypeTarification = 2L,
                    prixCurrency = 5.99
                ),

                // Test data for Chocolats (product id 2)
                AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre(
                    vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                    idProduit = 2L,
                    idClient = 1L, // Client 1
                    parentVidTypeTarification = 4L, // ParBenifice
                    prixCurrency = 4.99
                ),
                AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre(
                    vidTimestamp = System.currentTimeMillis() - 21600000, // 6 hours ago
                    idProduit = 2L,
                    idClient = 3L, // Client 3
                    parentVidTypeTarification = 5L, // Historique
                    prixCurrency = 5.49
                ),
                AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre(
                    vidTimestamp = System.currentTimeMillis(),
                    idProduit = 2L,
                    idClient = 4L, // Client 4
                    parentVidTypeTarification = 6L, // LeMaxPrixArrive
                    prixCurrency = 5.99
                )
            )

        fun add(data: AB_ReferentialSepareDataBases.AA_TarificationDataBaseFacileEntre) {
            val updatedList = modelList.toMutableList()
            updatedList.add(data)
            modelList = updatedList
        }
    }
}

