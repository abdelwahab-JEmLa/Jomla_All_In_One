package Views.FragId3_DialogVendeurAfficheurInfosProduit.Modules

import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.clientjetpack.Models.AppSettingsSaverModel
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
    val currentClientPrice = 440.0

    // Create sample price history with proper timestamps
    val currentTime = System.currentTimeMillis()
    val oneDayInMillis = 24 * 60 * 60 * 1000L

    // Enhanced price history map with more realistic data
    val samplePriceHistory = mapOf(
        Pair(65L, 1L) to listOf(
            PriceRecord(430.0, 1L, currentTime - (oneDayInMillis * 7)),
            PriceRecord(435.0, 1L, currentTime - (oneDayInMillis * 5)),
            PriceRecord(currentClientPrice, 1L, currentTime - (oneDayInMillis * 2))
        ),
        Pair(65L, 3L) to listOf(
            PriceRecord(460.0, 3L, currentTime - (oneDayInMillis * 7)),
        ),
        Pair(65L, 2L) to listOf(
            PriceRecord(440.0, 2L, currentTime - (oneDayInMillis * 6)),
            PriceRecord(460.0, 2L, currentTime - (oneDayInMillis * 4)),
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

}
