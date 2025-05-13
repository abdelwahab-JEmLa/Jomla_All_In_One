package com.example.clientjetpack.ID1.Test._ID1.Test.Models

import androidx.room.PrimaryKey

class InputEtInfosSqlModels {
    data class ProduitInfos(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val nom: String = ""
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
        val typeTarificationEnum: TypeTarificationEnum = TypeTarificationEnum.ParBenifice
    )

    enum class TypeTarificationEnum {
        ParBenifice,
        Historique,
        LeMaxPrixArrive
    }

    data class Tarification(
        val vidTimestamp: Long = 0L,
        val idProduit: Long = 0L,
        val idClient: Long = 0L,
        val idTypeTarification: Long = 0L,
        val prixCurrency: Double = 0.0
    )
}
