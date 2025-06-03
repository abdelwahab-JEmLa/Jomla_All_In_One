package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.Test

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable

fun testDataArticlesBasesStatsTable2(): List<ArticlesBasesStatsTable> {
    // Fixed: returning a list of 2 items instead of a single item
    return listOf(
        ArticlesBasesStatsTable(
            id = 1, // Using product ID from JSON
            nomArticleFinale = "chahrazed 80g", // Using product name from JSON
            nomArab = "",
            nmbrCat = 1,
            couleur1 = "#FFFFFF",
            idcolor1 = 0L,
            nomCategorie = "Produits alimentaires",
            prixAchat = 8.5, // Example purchase price
            prixVent = 10.0, // Using price from JSON tarification
            clientPrixVentUnite = 10.0,
            monBenfice = 1.5, // Example profit
            minQuan = 10,
            catalogeParentID = 100L,
            articleHaveUniteImages = false,
            itsNewArrivale = false,
            imageDimention = "300x300",
            idForSearchArticles = 849L
            // Using default values for other fields
        ),
        ArticlesBasesStatsTable(
            id = 2,
            nomArticleFinale = "milk 1L",
            nomArab = "",
            nmbrCat = 1,
            couleur1 = "#F5F5F5",
            idcolor1 = 1L,
            nomCategorie = "Produits laitiers",
            prixAchat = 12.0,
            prixVent = 14.5,
            clientPrixVentUnite = 14.5,
            monBenfice = 2.5,
            minQuan = 5,
            catalogeParentID = 101L,
            articleHaveUniteImages = true,
            itsNewArrivale = true,
            imageDimention = "300x300",
            idForSearchArticles = 850L
        )
    )
}
