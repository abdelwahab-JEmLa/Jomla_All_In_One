package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

fun testDataproduitInfosList(): List<A_ProduitInfosTest> {
    return listOf(
        // Product 1: Frienda
        A_ProduitInfosTest(
            id = 53,
            nom = "Frienda®",
            nomArab = "فريندا",
            prixVent = 600.0,
            prixAchat = 470.0,
            cartonState = "",
            nomCategorie = "حلوى طرية 5 دج",
            couleur1 = "🌈 Multi Couleur 🎨",
            nombreUniteInt = 220,
            nmbrCaron = 1,
            clientPrixVentUnite = 5.0,
            commmentSeVent = "U",
            diponibilityState = "",
            articleHaveUniteImages = false,
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        ),

        // Product 2: Tonner
        A_ProduitInfosTest(
            id = 54,
            nom = "Tonner®",
            nomArab = "اونير",
            prixVent = 500.0,
            prixAchat = 460.0,
            cartonState = "itsCarton",
            nomCategorie = "Fun Candys",
            couleur1 = "🍫 chocolat 🍫",
            nombreUniteInt = 60,
            nmbrCaron = 1,
            clientPrixVentUnite = 10.0,
            commmentSeVent = "U",
            diponibilityState = "",
            articleHaveUniteImages = false,
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        ),

        // Product 3: golden sachet - This demonstrates the color text fallback feature
        A_ProduitInfosTest(
            id = 565,
            nom = "golden sachet®",
            nomArab = "غولدن ساشي",
            prixVent = 4.0,
            prixAchat = 750.0,
            cartonState = "",
            nomCategorie = "Chocolattes 5 Da",
            couleur1 = "🟦 ازرق 🟦",
            couleur2 = "🟫 بني 🟫",            //<--
            //TODO(1): pk comme ca a 2 couleur avec nom mais la 2 eme ne s affiche pas 
            nombreUniteInt = 200,
            nmbrCaron = 1,
            clientPrixVentUnite = 5.0,
            commmentSeVent = "",
            diponibilityState = "Non Dispo",
            articleHaveUniteImages = false,
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        ),

        // Product 4: Mini Chips
        A_ProduitInfosTest(
            id = 567,
            nom = "Mini Chips®",
            nomArab = "ميني شيبس",
            prixVent = 410.0,
            prixAchat = 390.0,
            cartonState = "itsCarton",
            nomCategorie = "شيبس",
            couleur1 = "🧀 فرماج 🧀",
            couleur2 = "🧆 barbecu 🧆",
            nombreUniteInt = 100,
            nmbrCaron = 1,
            clientPrixVentUnite = 5.0,
            commmentSeVent = "",
            diponibilityState = "",
            articleHaveUniteImages = false,
            imageDimention = "Demi",
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        ),

        // Product 5: Jellopy Matrag
        A_ProduitInfosTest(
            id = 572,
            nom = "Jellopy Matrag®",
            nomArab = "",
            prixVent = 1710.0,
            prixAchat = 1650.0,
            cartonState = "UNITE",
            nomCategorie = "",
            couleur1 = "Standar",
            nombreUniteInt = 0,
            nmbrCaron = 0,
            clientPrixVentUnite = 0.0,
            commmentSeVent = "",
            diponibilityState = "Non Dispo",
            articleHaveUniteImages = true,
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        ),

        // Product 6: TIZANA KBIR - Completed from JSON data
        A_ProduitInfosTest(    //<--
        //TODO(1): pk ca n affiche pas le arrow que il ya 
            id = 3832,
            nom = "#TIZANA KBIR",
            nomArab = "",
            prixVent = 150.0,
            prixAchat = 150.0,
            cartonState = "",
            nomCategorie = "ataye emballé",
            couleur1 = "Couleur 1",
            couleur2 = "Couleur_2",
            couleur3 = "Couleur_3",
            nombreUniteInt = 0,
            nmbrCaron = 0,
            clientPrixVentUnite = 0.0,
            commmentSeVent = "",
            diponibilityState = "",
            articleHaveUniteImages = false,
            imageDimention = "",
            itsNewArrivale = false,
            timestamps = 1748355111768,
            needUpdate = true,
            affichageUniteState = false,
            benficeTotaleEntreMoiEtClien = -150.0,
            benificeClient = -150.0,
            benificeTotaleEn2 = -75.0,
            classementCate = -156.0,
            dateCreationCategorie = "1740564102869",
            funChangeImagsDimention = true,
            idCategorie = 0.0,
            catalogeParentID = 0,
            lastUpdateState = "",
            neaon1 = 0.0,
            neaon2 = "",
            minQuan = 0,
            monBenfice = 0.0,
            afficheBoitSiUniter = null,
            autreNomDarticle = null,
            cLeDataOuvertDuParentList = false,
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
            monBeneficeUniter = 0.0,
            keyFireBase = ""
        )
    )
}
