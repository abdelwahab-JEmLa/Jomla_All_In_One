package c_WindosBuyAndDesplayeArticleStats

import a_RoomDB.ArticlesBasesStatsModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import b_StartupAppDisplayerOfNewArticles.ViewModelsDataBase
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.R
import java.io.File


@Composable
fun WindosBuyAndDesplayeArticleStats(
    uiState: ViewModelsDataBase,
    article: ArticlesBasesStatsModel,
    onDismiss: () -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    modifier: Modifier = Modifier,
    onReloadTrigger: () -> Unit,
    reloadTrigger: Int
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {


                        DisplayColorsCards(
                            article = article,
                            viewModel = viewModel,
                            onDismiss = onDismiss,
                            onReloadTrigger = onReloadTrigger,
                            relodeTigger = reloadTrigger
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DisplayColorsCards(article: ArticlesBasesStatsModel, viewModel: StartUpNewArticlesViewModels, modifier: Modifier = Modifier,
                       onDismiss: () -> Unit,
                       onReloadTrigger: () -> Unit,
                       relodeTigger: Int
) {



    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        if (article.articleHaveUniteImages) {
            item {
                ColorCard(
                    viewModel = viewModel,
                    article = article,
                    index = -1,
                    relodeTigger = relodeTigger
                )
            }
        }

        val couleursList = listOf(
            article.couleur1,
            article.couleur2,
            article.couleur3,
            article.couleur4
        ).filterNot { it.isNullOrEmpty() }

        itemsIndexed(couleursList) { index, couleur ->
            if (couleur != null) {
                ColorCard(
                    article = article,
                    index = index,
                    relodeTigger = relodeTigger,
                    viewModel = viewModel
                )
            }
        }


    }
}


@Composable
private fun ColorCard(
    article: ArticlesBasesStatsModel,
    index: Int,
    relodeTigger: Int,
    viewModel: StartUpNewArticlesViewModels,
) {

    Card(
        modifier = Modifier
            .size(200.dp)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            DisplayeImageECB(
                viewModel = viewModel,
                article = article,
                index = index,
                reloadKey = relodeTigger,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay content
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

            }
        }
    }

}

@Composable
fun DisplayeImageECB(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    index: Int = 0,
    reloadKey: Any = Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
      val viewModelImagesPath=  viewModel.viewModelImagesPath
    val baseImagePath = "$viewModelImagesPath${article.idArticle}_${if (index == -1) "Unite" else (index + 1)}"

    val imageExist by remember(article.idArticle, reloadKey) {
        mutableStateOf(
            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                listOf( baseImagePath).firstOrNull { path ->
                    File("$path.$extension").exists()
                }?.let { "$it.$extension" }
            }
        )
    }

    val imageSource = imageExist ?: R.drawable.baked_goods_1

    val requestKey = "${article.idArticle}_${if (index == -1) "Unite" else index}_$reloadKey"

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(imageSource)
            .size(Size(1000, 1000))
            .crossfade(true)
            .setParameter("key", requestKey, memoryCacheKey = requestKey)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}




