package com.example.clientjetpack.ID3.Test.DataBase.Repo.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class _InfosSqlDataBases(
    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val b_ClientInfos: MutableList<B_ClientInfos> = mutableListOf(),
    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf()
)

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = ""
)

@Entity
data class B_ClientInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val idActiveTypeTarificationDataBase: Long = 0,
)

@Entity
data class C_TypeTarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val typeTarificationEnum: TypeTarificationEnum = TypeTarificationEnum.ParBenifice
)

@Entity
data class D_TarificationInfos(
    @PrimaryKey
    val vidTimestamp: Long = 0L,
    val idProduit: Long = 0L,
    val idClient: Long = 0L,
    val idTypeTarification: Long = 0L,
    val prixCurrency: Double = 0.0
)
