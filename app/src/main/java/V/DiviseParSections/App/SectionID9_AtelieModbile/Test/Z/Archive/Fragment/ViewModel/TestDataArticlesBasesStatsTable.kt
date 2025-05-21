package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable

fun testDataArticlesBasesStatsTable(): ArticlesBasesStatsTable {
    // Use values from the provided JSON data
    return ArticlesBasesStatsTable(
        idArticle = 849, // Using product ID from JSON
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
        cLeDataOuvertDuParentList = true, // Set to true for open item
        diponibilityState = "disponible",
        articleHaveUniteImages = false,
        itsNewArrivale = false,
        imageDimention = "300x300",
        idForSearchArticles = 849L
        // Using default values for other fields
    )
}
