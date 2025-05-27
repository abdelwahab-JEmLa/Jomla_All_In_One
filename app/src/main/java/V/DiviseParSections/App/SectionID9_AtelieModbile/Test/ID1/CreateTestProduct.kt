package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

// Helper function to create a test product
fun createTestProduct(): A_ProduitInfosTest {
    val randomId = (1000..9999).random().toLong()
    return A_ProduitInfosTest(
        id = randomId,
        nom = "Test Product $randomId",
        nomArab = "منتج تجريبي $randomId",
        prixVent = (100..1000).random().toDouble(),
        prixAchat = (50..800).random().toDouble(),
        cartonState = "Test",
        nomCategorie = "Test Category",
        couleur1 = "🔴 Rouge 🔴",
        nombreUniteInt = (10..200).random(),
        nmbrCaron = 1,
        clienPrixVentUnite = (5..20).random().toDouble(),
        commmentSeVent = "U",
        diponibilityState = "",
        articleHaveUniteImages = false,
        timestamps = System.currentTimeMillis(),
        needUpdate = true
    )
}
