package P1_StartupScreen.Ui.ArticlesGrid
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel

// Update the ArticleItem to use the new layout logic
@Composable
fun ArticleItem(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
) { //-->
//Hi Claud,what i went from u to do is to
//Find All TODOs and Fix Them 

//TODO:
//  fait que si le telephone est hos ajout au topEnd un cercle elecated card qui contien icon de donne quend le product est first visible
    val colorCount = countColors(article)

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorIndicator(
    iconColore: String,
    modifier: Modifier = Modifier,
    onClickToOpenWindow: () -> Unit,
    imageSize: DpSize,

    ) {
    val demiSizeImage = imageSize.width>200.dp
    Box(modifier = modifier.clickable { onClickToOpenWindow() }) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            if (iconColore == "©" || iconColore == "💯"|| iconColore == "") {
                GlideImage(
                    model = R.drawable.logo,
                    contentDescription = "Logo",
                    modifier = Modifier.size(
                        if (demiSizeImage) 70.dp else 38.dp
                    )
                )
            } else {
                Text(
                    text = iconColore,
                    fontSize =  if (demiSizeImage) 45.sp else 38.sp,
                    fontWeight = FontWeight.Bold ,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (14).dp, y = 18.dp)
                .size(if (demiSizeImage) 70.dp else 50.dp)
                .clickable { onClickToOpenWindow() }
        ) {
            GlideImage(
                model =  R.drawable.hand ,
                contentDescription = "Click indicator",
                contentScale = ContentScale.Fit
            )
        }
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

            ImageDisplayer(
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
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorOverlay(
    color: ColorsArticlesTabelle,
    modifier: Modifier = Modifier,
    onClickToOpenWindow: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color name with circular background
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .matchParentSize(),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
                ) {}

                AutoResizedText(
                    text = color.nameColore,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onClickToOpenWindow() },
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.matchParentSize(),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f))
                ) {}
                Text(
                    text = color.iconColore,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold ,
                    modifier = Modifier.clickable { onClickToOpenWindow() },
                    color = Color.White,
                    maxLines = 1
                )
                // Fixed hand icon positioning
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (14).dp, y = 18.dp)
                        .size(60.dp)
                        .clickable { onClickToOpenWindow() }
                ) {
                    GlideImage(
                        model = R.drawable.hand,
                        contentDescription = "Click indicator",
                        contentScale = ContentScale.Fit
                    )
                }


            }
        }
    }
}
