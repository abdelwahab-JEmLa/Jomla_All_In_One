package com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Models

data class NoSqlDataBases(
    val tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification> = mutableListOf(),
    val produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos> = mutableListOf(),
    val clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase> = mutableListOf()
)
