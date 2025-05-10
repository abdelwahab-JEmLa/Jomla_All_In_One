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
    class ProduitDataBase_RepositoryImp {
        data class ProduitDataBase(
            val vidTimestamp: Long,
            val id: Long,
            val name: String
        )

        var modelList: List<ProduitDataBase> = mutableStateListOf(
            ProduitDataBase(
                vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                id = 1L,
                name = "Caramels"
            ),
            ProduitDataBase(
                vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                id = 2L,
                name = "Chocolats"
            )
        )
    }

    class ClientDataBase_RepositoryImp {
        data class ClientDataBase(
            val vidTimestamp: Long,
            val id: Long,
            val name: String
        )

        var modelList: List<ClientDataBase> = mutableStateListOf(
            ClientDataBase(
                vidTimestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                id = 1L,
                name = "Client A"
            ),
            ClientDataBase(
                vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                id = 2L,
                name = "Client B"
            ),
            ClientDataBase(
                vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                id = 3L,
                name = "Client C"
            ),
            ClientDataBase(
                vidTimestamp = System.currentTimeMillis(),
                id = 4L,
                name = "Client D"
            )
        )
    }

    class TypeTarificationDataBase_RepositoryImp {
        data class TypeTarificationDataBase(
            val vidTimestamp: Long,
            val id: Long,
            val name: String
        )

        var modelList: List<TypeTarificationDataBase> = mutableStateListOf(
            TypeTarificationDataBase(
                vidTimestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                id = 1L,
                name = "ParBenifice"
            ),
            TypeTarificationDataBase(
                vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                id = 2L,
                name = "Historique"
            ),
            TypeTarificationDataBase(
                vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                id = 3L,
                name = "LeMaxPrixArrive"
            )
        )
    }
}
