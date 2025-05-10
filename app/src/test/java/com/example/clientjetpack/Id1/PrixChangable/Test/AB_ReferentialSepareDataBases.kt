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
        var modelList: List<AB_ReferentialSepareDataBases.ProduitDataBase> =
            mutableStateListOf(
                AB_ReferentialSepareDataBases.ProduitDataBase(
                    nom = "Caramels"
                ),
                AB_ReferentialSepareDataBases.ProduitDataBase(
                    nom = "Chocolats"
                )
            )
    }

    class ClientDataBase_RepositoryImp {
        var modelList: List<AB_ReferentialSepareDataBases.ClientDataBase> =
            mutableStateListOf(
                AB_ReferentialSepareDataBases.ClientDataBase(
                    nom = "Client 1"
                ),
                AB_ReferentialSepareDataBases.ClientDataBase(
                    nom = "Client 2"
                ),
                AB_ReferentialSepareDataBases.ClientDataBase(
                    nom = "Client 3"
                ),
                AB_ReferentialSepareDataBases.ClientDataBase(
                    nom = "Client 4"
                )
            )
    }

    class TypeTarificationDataBase_RepositoryImp {
        var modelList: List<AB_ReferentialSepareDataBases.TypeTarificationDataBase> =
            mutableStateListOf(
                AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                    typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice
                ),
                AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                    typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique
                ),
                AB_ReferentialSepareDataBases.TypeTarificationDataBase(
                    typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive
                )
            )
    }
}
