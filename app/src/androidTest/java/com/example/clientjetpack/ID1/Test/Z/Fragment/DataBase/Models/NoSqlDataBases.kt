package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models

import com.example.clientjetpack.ID1.Test._ID1.Test.Models.InputEtInfosSqlModels

data class NoSqlDataBases(
    val tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification> = mutableListOf(),
    val produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos> = mutableListOf(),
    val clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase> = mutableListOf()
)
