package Z_CodePartageEntreApps.Modules.Glide

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlidDisplaye(
    modifier: Modifier = Modifier,
    imageGlidReloadTigger: Int = 0,
    mainItem: ArticlesBasesStatsTable? = null,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {},
    qualityImage: Int = 3,
    colorIndex: Int = 0
) {
    val imagesProduitsLocalExternalStorageBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    var imageFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var imageFileName by remember { mutableStateOf("") }

    val keyImageId = if (mainItem == null) "logo" else {
        val id = if (mainItem.id==0L) mainItem.bsonObjectId else mainItem.id
        "${id}_${colorIndex + 1}"
    }

    val imageKey by remember(keyImageId, imageGlidReloadTigger) {
        derivedStateOf { "$keyImageId-$imageGlidReloadTigger-${System.currentTimeMillis()}" }
    }

    val shouldUseDefaultImage = mainItem == null

    suspend fun findValidImageFile(basePath: String): File? {
        return withContext(Dispatchers.IO) {
            val extensions = listOf("jpg", "jpeg", "png", "webp")
            for (ext in extensions) {
                val file = File("$basePath.$ext")
                if (file.exists() && file.length() > 0) {
                    imageFileName = file.name
                    return@withContext file
                }
            }
            null
        }
    }

    LaunchedEffect(imageKey) {
        withContext(Dispatchers.IO) {
            isLoading = true
            try {
                val defaultPath = "$imagesProduitsLocalExternalStorageBasePath/logo.webp"
                val defaultFile = File(defaultPath)

                if (shouldUseDefaultImage) {
                    imageFile = defaultFile
                    imageFileName = "logo.webp"
                } else {
                    val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"
                    val validFile = findValidImageFile(basePath)

                    if (validFile != null && validFile.exists()) {
                        imageFile = validFile
                    } else {
                        imageFile = defaultFile
                        imageFileName = "logo.webp"
                    }
                }
            } catch (e: Exception) {
                imageFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
                imageFileName = "logo.webp"
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        val displayFile = imageFile ?: File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")

        if (displayFile.exists()) {
            GlideImage(
                model = displayFile,
                contentDescription = "Product $keyImageId",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .blur(if (isLoading) 10.dp else 0.dp)
            ) { builder ->
                builder
                    .downsample(com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.AT_MOST)
                    .encodeQuality(qualityImage)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
                    .signature(com.bumptech.glide.load.Key {
                        imageKey.toByteArray()
                    })
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            isLoading = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            isLoading = false
                            onLoadComplete()
                            return false
                        }
                    })
            }
        } else {
            OnImageExistPas()
            isLoading = false
        }
    }
}
