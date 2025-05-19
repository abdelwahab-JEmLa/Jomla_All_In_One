package V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.Function

import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase

fun testDatasDataBasesInfosSql(): DataBasesInfosSql {
    return DataBasesInfosSql(
        a_ProduitInfos = mutableListOf(
            A_ProduitInfos(id = 1, nom = "Produit Optila"),
            A_ProduitInfos(id = 2, nom = "Produit Hnina"),
            A_ProduitInfos(id = 3, nom = "Produit kemya")
        ),
        b_ClientInfosList = mutableListOf(
            B_ClientInfos(
                id = 1,
                nom = "ClientAchteur Abderrahman",
            ),
            B_ClientInfos(
                id = 2,
                nom = "ClientAchteur Beta",
            ),
            B_ClientInfos(
                id = 3,
                nom = "ClientAchteur Gamma",
            )
        ),
        c_TypeTarificationInfos = mutableListOf(
            C_TypeTarificationInfos(
                id = 1,
                entityCorrespond = TypeTarificationEnum.ParBenifice,
                nom = "Par Bénifice",
                keyFireBase = getKeyFireBase(1, "Par Bénifice")
            ),
            C_TypeTarificationInfos(
                id = 2,
                entityCorrespond = TypeTarificationEnum.Historique,
                nom = "Historique",
                keyFireBase = getKeyFireBase(2, "Historique")
            ),
            C_TypeTarificationInfos(
                id = 3,
                entityCorrespond = TypeTarificationEnum.LeMaxPrixArrive,
                nom = "Tariff Maximum",
                keyFireBase = getKeyFireBase(3, "Tariff Maximum")
            ),
            C_TypeTarificationInfos(
                id = 4,
                entityCorrespond = TypeTarificationEnum.PRIX_BASE,
                nom = TypeTarificationEnum.PRIX_BASE.name,
                keyFireBase = getKeyFireBase(4, TypeTarificationEnum.PRIX_BASE.name)
            )
        ),
        d_TarificationInfos = mutableListOf(
            D_TarificationInfos(
                vidTimestamp = createTimestamp(
                    day = 1,
                    hour = 12,
                    minute = 30
                ),
                idProduit = 1,
                idClient = 1,
                idTypeTarification = 1,
                prixCurrency = 20.99
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(
                    day = 5,
                    hour = 13,
                    minute = 30
                ),
                idProduit = 1,
                idClient = 1,
                idTypeTarification = 1,
                prixCurrency = 25.50
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(
                    day = 5,
                    hour = 14,
                    minute = 30
                ),
                idProduit = 1,
                idClient = 2,
                idTypeTarification = 2,
                prixCurrency = 9.75
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(
                    day = 6,
                    hour = 3,
                    minute = 30
                ),
                idProduit = 2,
                idClient = 1,
                idTypeTarification = 1,
                prixCurrency = 15.25
            ),
            D_TarificationInfos(
                vidTimestamp = createTimestamp(
                    day = 6,
                    hour = 4,
                    minute = 30
                ),
                idProduit = 3,
                idClient = 1,
                idTypeTarification = 3,
                prixCurrency = 14.80
            )
        )
    )
}
