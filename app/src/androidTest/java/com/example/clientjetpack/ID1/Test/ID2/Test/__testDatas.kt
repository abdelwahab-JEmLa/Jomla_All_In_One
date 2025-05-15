package com.example.clientjetpack.ID1.Test.ID2.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Models.createTimestamp
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.D_TarificationInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models._InfosSqlDataBases

fun testDatas(): _InfosSqlDataBases {

        return _InfosSqlDataBases(
            a_ProduitInfos = mutableListOf(
                A_ProduitInfos(id = 1, nom = "Produit Optila"),
                A_ProduitInfos(id = 2, nom = "Produit Hnina"),
                A_ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            b_ClientInfos = mutableListOf(
                B_ClientInfos(
                    id = 1,
                    nom = "ClientAchteur Abderrahman",
                    idActiveTypeTarificationDataBase = 1
                ),
                B_ClientInfos(
                    id = 2,
                    nom = "ClientAchteur Beta",
                    idActiveTypeTarificationDataBase = 2
                ),
                B_ClientInfos(
                    id = 3,
                    nom = "ClientAchteur Gamma",
                    idActiveTypeTarificationDataBase = 3
                )
            ),
            d_TarificationInfos = mutableListOf(
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 20.99
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 5, hour = 13, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 25.50
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 5, hour = 14, minute = 30),
                    idProduit = 1,
                    idClient = 2,
                    idTypeTarification = 2,
                    prixCurrency = 9.75
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 6, hour = 3, minute = 30),
                    idProduit = 2,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 15.25
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 6, hour = 4, minute = 30),
                    idProduit = 3,
                    idClient = 1,
                    idTypeTarification = 3,
                    prixCurrency = 14.80
                )
            )
        )
    }
