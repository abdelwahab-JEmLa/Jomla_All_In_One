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
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice,
                itsTheActiveOne = true,
                clientId = 1L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 2L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique,
                itsTheActiveOne = false,
                clientId = 1L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 3L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive,
                itsTheActiveOne = false,
                clientId = 1L
            ),

            // Client 2 (ID: 2) tarification types
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 4L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice,
                itsTheActiveOne = true,
                clientId = 2L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 5L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique,
                itsTheActiveOne = false,
                clientId = 2L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 6L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive,
                itsTheActiveOne = false,
                clientId = 2L
            ),

            // Client 3 (ID: 3) tarification types
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 7L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice,
                itsTheActiveOne = true,
                clientId = 3L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 8L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique,
                itsTheActiveOne = false,
                clientId = 3L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 9L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive,
                itsTheActiveOne = false,
                clientId = 3L
            ),

            // Client 4 (ID: 4) tarification types
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 10L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice,
                itsTheActiveOne = true,
                clientId = 4L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 11L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique,
                itsTheActiveOne = false,
                clientId = 4L
            ),
            AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                id = 12L,
                typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive,
                itsTheActiveOne = false,
                clientId = 4L
            )
        )
    }
}
