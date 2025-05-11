package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models

import androidx.room.PrimaryKey

class InputSqlModels {

    data class ProduitInfos(
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

    data class Tarification(
        val vidTimestamp: Long,
        val idProduit: Long,
        val idClient: Long,
        val idTypeTarification: Long,
        val prixCurrency: Double,
    )
}

