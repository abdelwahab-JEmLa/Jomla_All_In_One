package A1_ActivePackages.E_StorePresentationToClientPackage.Main

import A0_MainObjectsAPP.Models.ProductDisplayController
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun DisplayArticleInfoToClientWindowsPreview() {

    val sampleProductDisplayController = ProductDisplayController(
        selectedColorId =0
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
        DisplayeArticleInfoToClientWindowsPackageC(
            displayController=sampleProductDisplayController,
            articleStatsDataBase = sampleArticleStats,
            colorsArticlesList = sampleColorsList,
            reloadTrigger = 0
        )
    }
}

