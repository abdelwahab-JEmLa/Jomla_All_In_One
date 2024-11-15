package P3_DisplayeProductInfosToSeller.Main
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.ViewModel.HeadViewModel

@Preview(showBackground = true)
@Composable
fun MainUiPreview() {
    val context = LocalContext.current

    // Sample data for preview
    val sampleArticleStats = ArticlesBasesStatsTable(
        idArticle = 65,
        nomArticleFinale = "Silca®",
        nomArab = "سيلكا",
        nomCategorie = "Fun Candys",
        monPrixAchat = 370.0,
        monPrixVent = 430.0,
        monBenfice = 60.0,
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

    MaterialTheme {
        MainUi(
            articlesBaseStats = sampleArticleStats,
            colorsArticlesTabelleModel = sampleColors,
            currentSale = sampleSale,
            viewModel = viewModel,
            reloadTrigger = 0,
            isDetailsVisible = true,
            onDismiss = {}
        )
    }
}
