package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID2

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable

fun testDataArticlesBasesStatsTable2(): List<ArticlesBasesStatsTable> {
    // Fixed: returning a list of 2 items instead of a single item
    return listOf(
        ArticlesBasesStatsTable(
            idArticle = 1, // Using product ID from JSON
            nomArticleFinale = "chahrazed 80g", // Using product name from JSON
            classementCate = 1.0,
            nomArab = "",
            nmbrCat = 1,
            couleur1 = "#FFFFFF",
            idcolor1 = 0L,
            nomCategorie = "Produits alimentaires",
            monPrixAchat = 8.5, // Example purchase price
            monPrixVent = 10.0, // Using price from JSON tarification
            clienPrixVentUnite = 10.0,
            monBenfice = 1.5, // Example profit
            minQuan = 10,
            catalogeParentID = 100L,
            diponibilityState = "disponible",
            articleHaveUniteImages = false,
            itsNewArrivale = false,
            imageDimention = "300x300",
            idForSearchArticles = 849L
            // Using default values for other fields
        ),
        ArticlesBasesStatsTable(
            idArticle = 2,
            nomArticleFinale = "milk 1L",
            classementCate = 2.0,
            nomArab = "",
            nmbrCat = 1,
            couleur1 = "#F5F5F5",
            idcolor1 = 1L,
            nomCategorie = "Produits laitiers",
            monPrixAchat = 12.0,
            monPrixVent = 14.5,
            clienPrixVentUnite = 14.5,
            monBenfice = 2.5,
            minQuan = 5,
            catalogeParentID = 101L,
            diponibilityState = "disponible",
            articleHaveUniteImages = true,
            itsNewArrivale = true,
            imageDimention = "300x300",
            idForSearchArticles = 850L
        )
    )
}
