package Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.clientjetpack.ViewModel.HeadViewModel
import java.io.File

@Composable
fun ImageDisplayer3(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    indexColor: Int = 0,
    reloadKey: Any = Unit
) {
    val context = LocalContext.current
    val viewModelImagesPath = viewModel.viewModelImagesPath

    val baseImagePath = remember(viewModelImagesPath, article.id, indexColor) {
        File(viewModelImagesPath, "${article.id}_${if (indexColor == -1) "Unite" else (indexColor + 1)}")
            .absolutePath
    }

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

    val imageSource = remember(imageExist) {
        imageExist?.let { File(it) }
    }

    val requestKey = remember(article.id, indexColor, reloadKey) {
        "${article.id}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
    }

    Box(modifier = modifier.fillMaxWidth()) {
        if (imageSource != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(imageSource)
                    .size(150,150)
                    .crossfade(true)
                    .setParameter("key", requestKey, memoryCacheKey = requestKey)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = "Article image ${article.id} color ${indexColor + 1}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            )
        }
    }
}
