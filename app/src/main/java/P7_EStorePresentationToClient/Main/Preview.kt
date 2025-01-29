package P7_EStorePresentationToClient.Main

import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.clientjetpack.Models.ProductDisplayController

@Preview
@Composable
fun DisplayArticleInfoToClientWindowsPreview() {
    val sampleProductDisplayController = ProductDisplayController(
        clientWindowsSelectedColorId = 29,
        clientWindowsPickerDisplayedQuantity = 25
    )

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

    val sampleColorsList = listOf(
        ColorsArticlesTabelle(
            iconColore = "🎨",
            idColore = 1,
            nameColore = "Multi Couleur"
        ),
        ColorsArticlesTabelle(
            iconColore = "🍓",
            idColore = 10,
            nameColore = "Fraise"
        ),
        ColorsArticlesTabelle(
            iconColore = "🥤",
            idColore = 29,
            nameColore = "Coca"
        ),
        ColorsArticlesTabelle(
            iconColore = "🍏",
            idColore = 30,
            nameColore = "تفاح"
        ),
        ColorsArticlesTabelle(
            iconColore = "🍋",
            idColore = 31,
            nameColore = "Citron"
        )
    )

    MaterialTheme {
        FragmentDisplayeInfoProductToClient7(
            displayController = sampleProductDisplayController,
            articleStatsDataBase = sampleArticleStats,
            colorsArticlesList = sampleColorsList,
            reloadTrigger = 0
        )
    }
}
