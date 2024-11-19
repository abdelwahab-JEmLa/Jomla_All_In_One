package P7_EStorePresentationToClient.Modules
import a_RoomDB.ArticlesBasesStatsTable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

@Composable
fun ImageDisplayerPC(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    indexColor: Int = 0,
    reloadKey: Any = Unit
) {
    val context = LocalContext.current

    // Move file operations outside of composition
    val imagePath = remember(article.idArticle, indexColor, reloadKey) {
        val viewModelImagesPath = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/")
        if (!viewModelImagesPath.exists()) {
            viewModelImagesPath.mkdirs()
        }

        val baseImagePath = File(
            viewModelImagesPath,
            "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"
        ).absolutePath

        listOf("jpg", "webp")
            .firstNotNullOfOrNull { extension ->
                val file = File("$baseImagePath.$extension")
                if (file.exists() && file.canRead()) {
                    file.absolutePath
                } else null
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        if (imagePath != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(File(imagePath))
                    .crossfade(true)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = "Article image ${article.idArticle} color ${indexColor + 1}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}
