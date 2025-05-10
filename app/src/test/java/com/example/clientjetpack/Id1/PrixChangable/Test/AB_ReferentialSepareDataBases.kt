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
        var modelList: List<AB_ReferentialSepareDataBases.TypeTarificationDataBase> = mutableStateListOf(
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
}
