package P3_DisplayeProductInfosToSeller.Main

import a_RoomDB.AppSettingsSaverModel


import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.clientjetpack.Models.PriceRecord
import com.example.clientjetpack.Models.ProductDisplayController
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.ViewModel.HeadViewModel

@Preview(showBackground = true)
@Composable
fun P3_DisplayeProductInfosToSellerPreview() {
//TODO :
// pk le max prix et ancien = 0.0 regle le preview 
    val context = LocalContext.current

    // Base prices for our example
    val basePurchasePrice = 370.0
    val baseSellingPrice = 430.0
    val maxPrice = 450.0  // Plus haut prix historique
    val currentClientPrice = 440.0 // Prix pour le client actuel

    // Sample data for preview
    val sampleArticleStats = ArticlesBasesStatsTable(
        idArticle = 65,
        nomArticleFinale = "Silca®",
        nomArab = "سيلكا",
        nomCategorie = "Fun Candys",
        monPrixAchat = basePurchasePrice,
        monPrixVent = baseSellingPrice,
        monBenfice = baseSellingPrice - basePurchasePrice,
        nmbrUnite = 48,
        idcolor1 = 10,
        idcolor2 = 29,
        idcolor3 = 30,
        idcolor4 = 31
    )

    val sampleColors = listOf(
        ColorsArticlesTabelle(iconColore = "🎨", idColore = 1, nameColore = "Multi Couleur"),
        ColorsArticlesTabelle(iconColore = "🍓", idColore = 10, nameColore = "Fraise"),
        ColorsArticlesTabelle(iconColore = "🥤", idColore = 29, nameColore = "Coca"),
        ColorsArticlesTabelle(iconColore = "🍏", idColore = 30, nameColore = "تفاح"),
        ColorsArticlesTabelle(iconColore = "🍋", idColore = 31, nameColore = "Citron")
    )

    val sampleSale = SoldArticlesTabelle(
        vid = 1,
        idArticle = 65,
        nameArticle = "Silca®",
        clientSoldToItId = 1,
        date = System.currentTimeMillis().toString(),
        color1IdPicked = 10,
        color1SoldQuantity = 1
    )

    // Create view model using DatabaseModule
    val viewModel = remember(context) {
        HeadViewModel(
            context,
            AppDatabase.DatabaseModule.getDatabase(context)
        )
    }

    // Create richer price history showing evolution of prices
    val currentTime = System.currentTimeMillis()
    val oneDayInMillis = 24 * 60 * 60 * 1000L

    val samplePriceHistory = mapOf(
        // History for client 1 (current client)
        Pair(65L, 1L) to listOf(
            PriceRecord(430.0, 1L, currentTime - (oneDayInMillis * 5)), // 5 days ago
            PriceRecord(435.0, 1L, currentTime - (oneDayInMillis * 3)), // 3 days ago
            PriceRecord(currentClientPrice, 1L, currentTime), // Current price
        ),
        // History for client 2 (includes max price)
        Pair(65L, 2L) to listOf(
            PriceRecord(440.0, 2L, currentTime - (oneDayInMillis * 4)), // 4 days ago
            PriceRecord(maxPrice, 2L, currentTime - (oneDayInMillis * 2)), // 2 days ago (max price)
            PriceRecord(445.0, 2L, currentTime), // Current price
        )
    )

    // Create sample UiState with rich price history
    val sampleUiState = UiState(
        articlesBasesStatTables = listOf(sampleArticleStats),
        colorsArticlesTabelleModel = sampleColors,
        productDisplayController = ProductDisplayController(),
        maxPriceMap = samplePriceHistory,
        // Add sample app settings to identify current client
        appSettingsSaverModel = listOf(
            AppSettingsSaverModel(
                name = "clientBuyerNowId",
                valueLong = 1L // Set current client to client 1
            )
        )
    )

    MaterialTheme {
        MainUi(
            articlesBaseStats = sampleArticleStats,
            colorsArticlesTabelleModel = sampleColors,
            currentSale = sampleSale,
            viewModel = viewModel,
            reloadTrigger = 0,
            isDetailsVisible = true,
            onDismiss = {},
            uiState = sampleUiState
        )
    }
}
