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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Custom PreviewHeadViewModel that extends HeadViewModel
class PreviewHeadViewModel(
    context: android.content.Context,
    database: AppDatabase,
    private val previewPriceHistory: Map<Pair<Long, Long>, List<PriceRecord>>
) : HeadViewModel(context, database) {

    private val _previewUiState = MutableStateFlow(UiState(
        productDisplayController = ProductDisplayController(),
        maxPriceMap = previewPriceHistory
    ))

    override val uiState: StateFlow<UiState> = _previewUiState

    override fun getMaxPrice(productId: Int): Double {
        return previewPriceHistory
            .filter { it.key.first == productId.toLong() }
            .values
            .flatten()
            .maxOfOrNull { it.price } ?: 0.0
    }

    override fun getHistoryProductForClient(productId: Int, clientId: Long): List<PriceRecord> {
        val key = Pair(productId.toLong(), clientId)
        return previewPriceHistory[key] ?: emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun P3_DisplayeProductInfosToSellerPreview() {
    val context = LocalContext.current

    // Base prices for our example
    val basePurchasePrice = 370.0
    val baseSellingPrice = 430.0
    val maxPrice = 450.0
    val currentClientPrice = 440.0

    // Create sample price history with proper timestamps
    val currentTime = System.currentTimeMillis()
    val oneDayInMillis = 24 * 60 * 60 * 1000L

    // Enhanced price history map with more realistic data
    val samplePriceHistory = mapOf(
        // History for current client (ID: 1)
        Pair(65L, 1L) to listOf(
            PriceRecord(430.0, 1L, currentTime - (oneDayInMillis * 7)),
            PriceRecord(435.0, 1L, currentTime - (oneDayInMillis * 5)),
            PriceRecord(currentClientPrice, 1L, currentTime - (oneDayInMillis * 2))
        ),
        // History for other client (ID: 2) including max price
        Pair(65L, 2L) to listOf(
            PriceRecord(440.0, 2L, currentTime - (oneDayInMillis * 6)),
            PriceRecord(maxPrice, 2L, currentTime - (oneDayInMillis * 4)),
            PriceRecord(445.0, 2L, currentTime - (oneDayInMillis * 1))
        )
    )

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
        date = currentTime.toString(),
        color1IdPicked = 10,
        color1SoldQuantity = 1
    )

    // Use the custom PreviewHeadViewModel instead of the regular HeadViewModel
    val viewModel = remember(context) {
        PreviewHeadViewModel(
            context,
            AppDatabase.DatabaseModule.getDatabase(context),
            samplePriceHistory
        )
    }

    // Create sample UiState with complete settings
    val sampleUiState = UiState(
        articlesBasesStatTables = listOf(sampleArticleStats),
        colorsArticlesTabelleModel = sampleColors,
        productDisplayController = ProductDisplayController(),
        maxPriceMap = samplePriceHistory,
        appSettingsSaverModel = listOf(
            AppSettingsSaverModel(
                name = "clientBuyerNowId",
                valueLong = 1L
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
