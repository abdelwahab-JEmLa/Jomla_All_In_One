package com.example.clientjetpack.Id1.PrixChangable.Test.Models

data class NoSql(
    val tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification>,
    val produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos>,
    val clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase>,
)

