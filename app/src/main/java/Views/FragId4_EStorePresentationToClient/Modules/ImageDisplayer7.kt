package Views.FragId4_EStorePresentationToClient.Modules
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

// 3. ImageDisplayer7.kt modifications:
@Composable
fun ImageDisplayer7(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    indexColor: Int = 0,
    reloadKey: Any = Unit,
    sizeScreen: Dp
) {
    val context = LocalContext.current

    // Move file operations outside of composition
    val imagePath = remember(article.id, indexColor, reloadKey) {
        val viewModelImagesPath = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/")
        if (!viewModelImagesPath.exists()) {
            viewModelImagesPath.mkdirs()
        }

        val baseImagePath = File(
            viewModelImagesPath,
            "${article.id}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"
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
        modifier = modifier.fillMaxSize() // Changé pour remplir tout l'espace disponible
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
                contentDescription = "Article image ${article.id} color ${indexColor + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}
