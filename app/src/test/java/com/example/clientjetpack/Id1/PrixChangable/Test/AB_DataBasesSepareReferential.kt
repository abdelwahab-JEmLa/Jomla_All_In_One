package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.compose.runtime.mutableStateListOf
import androidx.room.PrimaryKey

class A_DataBasesSepareReferential {
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
        var modelList: List<A_DataBasesSepareReferential.ProduitDataBase> =
            mutableStateListOf(
                A_DataBasesSepareReferential.ProduitDataBase(
                    nom = "Caramels"
                ),
                A_DataBasesSepareReferential.ProduitDataBase(
                    nom = "Chocolats"
                )
            )
    }

    class ClientDataBase_RepositoryImp {
        var modelList: List<A_DataBasesSepareReferential.ClientDataBase> =
            mutableStateListOf(
                A_DataBasesSepareReferential.ClientDataBase(
                    nom = "Client 1"
                ),
                A_DataBasesSepareReferential.ClientDataBase(
                    nom = "Client 2"
                ),
                A_DataBasesSepareReferential.ClientDataBase(
                    nom = "Client 3"
                ),
                A_DataBasesSepareReferential.ClientDataBase(
                    nom = "Client 4"
                )
            )
    }

    class TypeTarificationDataBase_RepositoryImp {
        var modelList: List<A_DataBasesSepareReferential.TypeTarificationDataBase> =
            mutableStateListOf(
                A_DataBasesSepareReferential.TypeTarificationDataBase(
                    typeTarificationEnum = A_DataBasesSepareReferential.TypeTarificationEnum.ParBenifice
                ),
                A_DataBasesSepareReferential.TypeTarificationDataBase(
                    typeTarificationEnum = A_DataBasesSepareReferential.TypeTarificationEnum.Historique
                ),
                A_DataBasesSepareReferential.TypeTarificationDataBase(
                    typeTarificationEnum = A_DataBasesSepareReferential.TypeTarificationEnum.LeMaxPrixArrive
                )
            )
    }
}
