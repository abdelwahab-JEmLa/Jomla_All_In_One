package c_WindosBuyAndDesplayeArticleStats

import a_RoomDB.ArticlesBasesStatsModel
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.R
import java.io.File

@Composable
fun ImageDisplayer(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    index: Int = 0,
    reloadKey: Any = Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModelImagesPath = viewModel.viewModelImagesPath

    // Construct base path ensuring directory exists
    val baseImagePath = remember(viewModelImagesPath, article.idArticle, index) {
        File(viewModelImagesPath, "${article.idArticle}_${if (index == -1) "Unite" else (index + 1)}")
            .absolutePath
    }

    // Check for image existence with supported extensions
    val imageExist by remember(baseImagePath, reloadKey) {
        mutableStateOf(
            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                val file = File("$baseImagePath.$extension")
                if (file.exists() && file.canRead()) {
                    file.absolutePath
                } else null
            }
        )
    }

    // Use fallback if image doesn't exist
    val imageSource = remember(imageExist) {
        imageExist?.let { File(it) } ?: R.drawable.baked_goods_1
    }

    // Create unique cache key for image
    val requestKey = remember(article.idArticle, index, reloadKey) {
        "${article.idArticle}_${if (index == -1) "Unite" else index}_$reloadKey"
    }

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
        contentDescription = "Article image ${article.idArticle} color ${index + 1}",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}




