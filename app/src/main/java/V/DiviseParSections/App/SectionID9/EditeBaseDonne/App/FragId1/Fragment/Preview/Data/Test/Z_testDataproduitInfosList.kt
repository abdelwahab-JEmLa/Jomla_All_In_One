package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test

import Views.P1.Ui.ArticlesGrid.A.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Views.P1.Ui.ArticlesGrid.A.List.Repository.A_ProduitDataBase.Repository.DisponibilityEtates

// 3. Fix testDataproduitInfosList to include proper availability states
fun testDataproduitInfosList(): List<ArticlesBasesStatsTable> {
    val idParentCategorie1 = 1L
    return listOf(
        ArticlesBasesStatsTable(
            id = 54,
            nom = "Tonner®",
            nomArab = "اونير",
            prixVent = 500.0,
            prixAchat = 460.0,
            cartonState = "itsCarton",
            couleur1 = "🍫 chocolat 🍫",
            nombreUniteInt = 60,
            clientPrixVentUnite = 10.0,
            commmentSeVent = "U",
            disponibilityEtates = DisponibilityEtates.DISPO,
            articleHaveUniteImages = false,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
            idParentCategorie = idParentCategorie1
        ),
        ArticlesBasesStatsTable(
            id = 565,
            nom = "golden sachet®",
            nomArab = "غولدن ساشي",
            prixVent = 4.0,
            prixAchat = 750.0,
            cartonState = "",
            couleur1 = "🟦 ازرق 🟦",
            couleur2 = "🟫 بني 🟫",
            nombreUniteInt = 200,
            clientPrixVentUnite = 5.0,
            commmentSeVent = "",
            disponibilityEtates = DisponibilityEtates.NON_DISPO,
            articleHaveUniteImages = false,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
            idParentCategorie = idParentCategorie1
        ),
        ArticlesBasesStatsTable(
            id = 567,
            nom = "Mini Chips®",
            nomArab = "ميني شيبس",
            prixVent = 410.0,
            prixAchat = 390.0,
            cartonState = "itsCarton",
            couleur1 = "🧀 فرماج 🧀",
            couleur2 = "🧆 barbecu 🧆",
            nombreUniteInt = 100,
            clientPrixVentUnite = 5.0,
            commmentSeVent = "",
            disponibilityEtates = DisponibilityEtates.DISPO,
            articleHaveUniteImages = false,
            imageDimention = "Demi",
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
            idParentCategorie = 3L
        ),
        ArticlesBasesStatsTable(
            id = 572,
            nom = "Jellopy Matrag®",
            nomArab = "",
            prixVent = 1710.0,
            prixAchat = 1650.0,
            cartonState = "UNITE",
            couleur1 = "Standar",
            nombreUniteInt = 0,
            clientPrixVentUnite = 0.0,
            commmentSeVent = "",
            disponibilityEtates = DisponibilityEtates.NON_DISPO,
            articleHaveUniteImages = true,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
            idParentCategorie = idParentCategorie1
        ),

        ArticlesBasesStatsTable(
            id = 3832,
            nom = "#TIZANA KBIR",
            nomArab = "",
            prixVent = 150.0,
            prixAchat = 150.0,
            cartonState = "",
            nombreUniteInt = 0,
            clientPrixVentUnite = 0.0,
            commmentSeVent = "",
            articleHaveUniteImages = false,
            imageDimention = "",
            itsNewArrivale = false,
            dernierFireBaseUpdateTimestamps = 1748355111768,
            affichageUniteState = false,
            benficeTotaleEntreMoiEtClien = -150.0,
            benificeTotaleEn2 = -75.0,
            dateCreationCategorie = "1740564102869",
            funChangeImagsDimention = true,
            catalogeParentID = 0,
            lastUpdateState = "",
            neaon1 = 0.0,
            neaon2 = "",
            minQuan = 0,
            monBenfice = 0.0,
            afficheBoitSiUniter = null,
            autreNomDarticle = null,
            idForSearchArticles = 0,
            idcolor1 = 0,
            idcolor2 = 0,
            idcolor3 = 0,
            idcolor4 = 0,
            couleur4 = null,
            nomCategorie2 = null,
            prixDeVentTotaleChezClient = 0.0,
            monPrixAchatUniter = 0.0,
            monPrixVentUniter = 0.0,
            keyFireBase = "",
            idParentCategorie = idParentCategorie1
        ),


    )
}
