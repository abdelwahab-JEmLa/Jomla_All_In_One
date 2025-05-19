package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun testBonAchatT2(): List<BonAchatT2> {
    return listOf(
        BonAchatT2(
            vid = 1,
            clientAcheteurID = 4L,
            nomClientConcerned = "abdelhamid",
            timestamps = System.currentTimeMillis(),
            heurDebutInString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            heurFinInString = "Non Defini",
            cActive = true,
            cJustPourVoirPanie = false,
            ouvert = true
        ),
        BonAchatT2(
            vid = 2,
            clientAcheteurID = 5L,
            nomClientConcerned = "sara",
            timestamps = System.currentTimeMillis() - 86400000, // Yesterday
            heurDebutInString = "14:30",
            heurFinInString = "Non Defini",
            cActive = true,
            cJustPourVoirPanie = true,
            ouvert = false
        )
    )
}

fun testD_TarificationInfosT2(): List<D_TarificationInfosT2> {
    // This was already returning a list, so no changes needed to the return type
    return listOf(
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 1,
                hour = 12,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2()[0].idArticle.toLong(),
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.ParBenifice,
            prixCurrency = 20.99
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 5,
                hour = 13,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2()[0].idArticle.toLong(),
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
            prixCurrency = 25.50
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 5,
                hour = 14,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2()[0].idArticle.toLong(),
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 9.75
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 6,
                hour = 3,
                minute = 30
            ),
            idProduit = testDataArticlesBasesStatsTable2()[1].idArticle.toLong(),
            idParentBonAchat = testBonAchatT2()[0].vid,
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
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
            prixCurrency = 14.80
        )
    )
}

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
