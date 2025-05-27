package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

fun testDataproduitInfosList(): List<A_ProduitInfosTest> {
    return listOf(
        // Product 1: Frienda
        A_ProduitInfosTest(
            id = 53,
            nom = "Frienda®",
            nomArab = "فريندا",
            prixVent = 600.0,
            monPrixAchat = 470.0,
            cartonState = "",
            nomCategorie = "حلوى طرية 5 دج",
            couleur1 = "?? Multi Couleur 🎨",
            nmbrUnite = 220,
            nmbrCaron = 1,
            clienPrixVentUnite = 5.0,
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
            monPrixAchat = 460.0,
            cartonState = "itsCarton",
            nomCategorie = "Fun Candys",
            couleur1 = "🍫 chocolat 🍫",
            nmbrUnite = 60,
            nmbrCaron = 1,
            clienPrixVentUnite = 10.0,
            commmentSeVent = "U",
            diponibilityState = "",
            articleHaveUniteImages = false,
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        ),

        // Product 3: golden sachet
        A_ProduitInfosTest(
            id = 565,
            nom = "golden sachet®",
            nomArab = "غولدن ساشي",
            prixVent = 4.0,
            monPrixAchat = 750.0,
            cartonState = "",
            nomCategorie = "Chocolattes 5 Da",
            couleur1 = "🟦 ازرق 🟦",
            couleur2 = "🟫 بني 🟫",
            nmbrUnite = 200,
            nmbrCaron = 1,
            clienPrixVentUnite = 5.0,
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
            monPrixAchat = 390.0,
            cartonState = "itsCarton",
            nomCategorie = "شيبس",
            couleur1 = "🧀 فرماج 🧀",
            couleur2 = "🧆 barbecu 🧆",
            nmbrUnite = 100,
            nmbrCaron = 1,
            clienPrixVentUnite = 5.0,
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
            monPrixAchat = 1650.0,
            cartonState = "UNITE",
            nomCategorie = "",
            couleur1 = "Standar",
            nmbrUnite = 0,
            nmbrCaron = 0,
            clienPrixVentUnite = 0.0,
            commmentSeVent = "",
            diponibilityState = "Non Dispo",
            articleHaveUniteImages = true,
            timestamps = System.currentTimeMillis(),
            needUpdate = true
        )
    )
}
