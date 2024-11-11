package c1_WindowsDesplayeArticle

import a_RoomDB.AppDatabase
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.room.Room
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import c2_Ui.ColorsCards
import c2_Ui.CompactQuantityPickerPC
import c2_Ui.ProductNameSection

@Preview(
    device = Devices.PHONE,
    widthDp = 360,
    heightDp = 800,
    showBackground = true
)
@Composable
fun DisplayArticleInfoToClientWindowsPreview() {
    val sampleArticleStats = ArticlesBasesStatsTable(
        idArticle = 65,
        nomArticleFinale = "Silca®",
        nomArab = "سيلكا",
        nomCategorie = "Fun Candys",
        monPrixAchat = 370.0,
        monPrixVent = 430.0,
        monBenfice = 60.0,
        nmbrUnite = 48,
        couleur1 = "🍓 Fraise 🍓",
        couleur2 = "🥤 Coca 🥤",
        couleur3 = "🍏 تفاح 🍏",
        couleur4 = "🍋 Citron 🍋",
        idcolor1 = 10,
        idcolor2 = 29,
        idcolor3 = 30,
        idcolor4 = 31
    )

    val sampleColorsList = listOf(
        ColorsArticlesTabelle(
            classementColore = 1,
            iconColore = "🎨",
            idColore = 1,
            nameColore = "Multi Couleur"
        ),
        ColorsArticlesTabelle(
            classementColore = 10,
            iconColore = "🍓",
            idColore = 10,
            nameColore = "Fraise"
        ),
        ColorsArticlesTabelle(
            classementColore = 29,
            iconColore = "🥤",
            idColore = 29,
            nameColore = "Coca"
        ),
        ColorsArticlesTabelle(
            classementColore = 30,
            iconColore = "🍏",
            idColore = 30,
            nameColore = "تفاح"
        ),
        ColorsArticlesTabelle(
            classementColore = 31,
            iconColore = "🍋",
            idColore = 31,
            nameColore = "Citron"
        )
    )

    val context = LocalContext.current
    val previewViewModel = remember {
        StartUpNewArticlesViewModels(
            context = context,
            database = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            ).build()
        )
    }

    MaterialTheme {
        DisplayeArticleInfoToClientWindowsPackageC(
            articleStatsDataBase = sampleArticleStats,
            colorsArticlesList = sampleColorsList,
            viewModel = previewViewModel,
            reloadTrigger = 0
        )
    }
}
