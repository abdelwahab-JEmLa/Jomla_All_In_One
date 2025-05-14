package com.example.clientjetpack.ID1.Test._A.Tests.Packages.Models


data class NoSqlDataBases(
    val tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification> = mutableListOf(),
    val produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos> = mutableListOf(),
    val clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase> = mutableListOf()
)

