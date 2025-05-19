package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun testBonAchatT2(): BonAchatT2 {
    // Use values from the provided JSON data
    return BonAchatT2(
        vid = 1, // Using timestamp from JSON
        clientAcheteurID = 4L, // Using client ID from JSON
        nomClientConcerned = "abdelhamid", // Using client name from JSON
        timestamps = System.currentTimeMillis(),
        heurDebutInString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
        heurFinInString = "Non Defini",
        cLeDataOuvertDuParentList = true, // Set to true since we need an open transaction
        cActive = true,
        cJustPourVoirPanie = false,
        ouvert = true
        // Using default values for other fields
    )
}

fun testD_TarificationInfosT2(): List<D_TarificationInfosT2> {
    // Fixed: Return a list directly instead of wrapping in another object
    return listOf(
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 1,
                hour = 12,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2().idArticle.toLong(),
            idParentBonAchat = testBonAchatT2().vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.ParBenifice,
            prixCurrency = 20.99
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 5,
                hour = 13,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2().idArticle.toLong(),
            idParentBonAchat = testBonAchatT2().vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
            prixCurrency = 25.50
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 5,
                hour = 14,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2().idArticle.toLong(),
            idParentBonAchat = testBonAchatT2().vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 9.75
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 6,
                hour = 3,
                minute = 30
            ),
            idProduit = 2L,
            idParentBonAchat = testBonAchatT2().vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.ParBenifice,
            prixCurrency = 15.25
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 6,
                hour = 4,
                minute = 30
            ),
            idProduit = 3L,
            idParentBonAchat = testBonAchatT2().vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
            prixCurrency = 14.80
        )
    )
}

fun testDataArticlesBasesStatsTable2(): ArticlesBasesStatsTable {
    // Use values from the provided JSON data
    return ArticlesBasesStatsTable(
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
        cLeDataOuvertDuParentList = true, // Set to true for open item
        diponibilityState = "disponible",
        articleHaveUniteImages = false,
        itsNewArrivale = false,
        imageDimention = "300x300",
        idForSearchArticles = 849L
        // Using default values for other fields
    )
}
