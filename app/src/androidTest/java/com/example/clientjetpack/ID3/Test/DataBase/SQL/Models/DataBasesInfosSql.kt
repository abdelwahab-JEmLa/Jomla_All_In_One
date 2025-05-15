package com.example.clientjetpack.ID3.Test.DataBase.SQL.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class DataBasesInfosSql(
    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val b_ClientInfos: MutableList<B_ClientInfos> = mutableListOf(),
    val c_TypeTarificationInfos: MutableList<C_TypeTarificationInfos> = mutableListOf(),
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf()
)

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "",
    val needUpdate: Boolean = false
)

@Entity
data class B_ClientInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val idActiveTypeTarificationDataBase: Long = 0,
    val needUpdate: Boolean = false
)

@Entity
data class C_TypeTarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val typeTarificationEnum: TypeTarificationEnum = TypeTarificationEnum.ParBenifice,
    val needUpdate: Boolean = false
)

@Entity
data class D_TarificationInfos(
    @PrimaryKey
    val vidTimestamp: Long = 0L,
    val idProduit: Long = 0L,
    val idClient: Long = 0L,
    val idTypeTarification: Long = 0L,
    val prixCurrency: Double = 0.0 ,
    val needUpdate: Boolean = false
)

enum class TypeTarificationEnum {
    ParBenifice,
    Historique,
    LeMaxPrixArrive
}
