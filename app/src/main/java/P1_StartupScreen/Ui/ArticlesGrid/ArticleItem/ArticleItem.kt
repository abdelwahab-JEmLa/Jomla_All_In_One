package P1_StartupScreen.Ui.ArticlesGrid.ArticleItem
import P1_StartupScreen.Ui.ArticlesGrid.ArticleLayout
import P1_StartupScreen.Ui.ArticlesGrid.ImageDisplayer1
import P1_StartupScreen.Ui.ArticlesGrid.checkImageExists
import P1_StartupScreen.Ui.ArticlesGrid.countColors
import P1_StartupScreen.Ui.ArticlesGrid.getColorIdForIndex
import a_RoomDB.ArticlesBasesStatsTable
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel

private const val TAG = "ArticleGridDebug"

@Composable
fun ArticleItem(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    isFirstVisible: Boolean = false
) {
    val colorCount = countColors(article)

    // Log article visibility state
    Log.d(
        TAG, """
        ArticleItem State:
        - Article ID: ${article.idArticle}
        - Is Host: ${uiState.productDisplayController.isHostPhone}
        - Is First Visible: $isFirstVisible
        - Color Count: $colorCount
    """.trimIndent())

    val cardColor = when {
        uiState.productDisplayController.isHostPhone && isFirstVisible -> {
            Log.d(TAG, "Setting RED color for first visible article ${article.idArticle}")
            Color.Red
        }
        else -> {
            Log.d(TAG, "Setting DEFAULT color for article ${article.idArticle}")
            MaterialTheme.colorScheme.surface
        }
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        val layout = when {
            article.imageDimention == "Demi" && colorCount == 1 -> ArticleLayout.DemiUno
            article.imageDimention == "Demi" && colorCount == 2 -> ArticleLayout.DemiDual
            article.imageDimention == "Demi" && colorCount > 2 -> ArticleLayout.DemiMulti
            colorCount == 1 -> ArticleLayout.SmallUno
            colorCount == 2 -> ArticleLayout.SmallDual
            colorCount > 2 -> ArticleLayout.SmallMulti
            else -> ArticleLayout.SmallUno
        }

        layout.Content(
            article = article,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos,
            uiState = uiState
        )
    }
}



@Composable
fun ArticleImageWithOverlay(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    colorIndex: Int,
    reloadTrigger: Int,
    uiState: UiState,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit,
    imageSize: DpSize
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable { onClickToOpenWindow(article, colorIndex) }
                .fillMaxSize()
        ) {
            val imageExists = remember(article.idArticle, colorIndex, reloadTrigger) {
                checkImageExists(viewModel, article, colorIndex, reloadTrigger)
            }

            ImageDisplayer1(
                article = article,
                viewModel = viewModel,
                indexColor = colorIndex,
                reloadKey = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindow,
                uiState = uiState,
                showOverlay = !imageExists,
                imageScale = contentScale,
                imageSize = imageSize
            )

            if (imageExists) {
                article.getColorIdForIndex(colorIndex)?.let { colorId ->
                    uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                        ColorIndicator(
                            iconColore = color.iconColore,
                            modifier = Modifier
                                .padding(3.dp)
                                .align(Alignment.BottomEnd)
                                .wrapContentSize()
                                .offset(x = (-10).dp, y = (-15).dp)
                            ,
                            imageSize = imageSize,
                            onClickToOpenWindow = { onClickToOpenWindow(article, colorIndex) }
                        )
                    }
                }
            }
        }
    }
}

