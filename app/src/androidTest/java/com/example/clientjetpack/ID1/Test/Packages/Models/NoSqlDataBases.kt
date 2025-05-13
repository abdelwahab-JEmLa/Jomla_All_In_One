package com.example.clientjetpack.ID1.Test.Packages.Models

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.InputEtInfosSqlModels

data class NoSqlDataBases(
    val tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification> = mutableListOf(),
    val produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos> = mutableListOf(),
    val clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase> = mutableListOf()
)

