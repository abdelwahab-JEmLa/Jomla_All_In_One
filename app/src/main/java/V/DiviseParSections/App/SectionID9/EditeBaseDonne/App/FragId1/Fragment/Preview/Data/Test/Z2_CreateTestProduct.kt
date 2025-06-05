package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable

// Helper function to create a test product
fun createTestProduct(): ArticlesBasesStatsTable {
    val randomId = (1000..9999).random().toLong()
    return ArticlesBasesStatsTable(
        id = randomId,
        nom = "Test Product $randomId",
        nomArab = "منتج تجريبي $randomId",
        prixVent = (100..1000).random().toDouble(),
        prixAchat = (50..800).random().toDouble(),
        cartonState = "Test",
        couleur1 = "🔴 Rouge 🔴",
        nombreUniteInt = (10..200).random(),
        nmbrCaron = 1,
        clientPrixVentUnite = (5..20).random().toDouble(),
        commmentSeVent = "U",
        articleHaveUniteImages = false,
        dernierFireBaseUpdateTimestamps = System.currentTimeMillis(),
    )
}
