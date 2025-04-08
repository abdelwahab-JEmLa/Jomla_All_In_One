package Z_CodePartageEntreApps.View

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun GlideDisplayImageBykeyId(
    modifier: Modifier = Modifier,
    imageGlidReloadTigger: Int = 0,
    mainItem: A_ProduitModel? = null,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {},
    qualityImage: Int = 3,
    colorIndex: Int = 0
) {
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var imageFileName by remember { mutableStateOf("") }

    val keyImageId = if (mainItem == null) "null" else "${mainItem.id}_${colorIndex + 1}"
    var shouldUseDefaultImage by remember {
        mutableStateOf(keyImageId == "null" || mainItem?.coloursEtGouts?.any { it.sonImageNeExistPas } == true)
    }

    // Function to checkADD_1_4_PeriodeVent if file exists and is valid
    suspend fun findValidImageFile(basePath: String): File? {
        return withContext(Dispatchers.IO) {
            val extensions = listOf("jpg", "jpeg", "png", "webp")
            for (ext in extensions) {
                val file = File("$basePath.$ext")
                if (file.exists() && file.length() > 0) {
                    Log.d("GlideDisplay", "Found valid image file: ${file.absolutePath}")
                    imageFileName = file.name
                    return@withContext file
                }
            }
            Log.d("GlideDisplay", "No valid image file found at $basePath")
            null
        }
    }

    // Load image file once on initial composition
    LaunchedEffect(keyImageId) {
        withContext(Dispatchers.IO) {
            try {
                val defaultPath = "$imagesProduitsLocalExternalStorageBasePath/logo.webp"
                if (!shouldUseDefaultImage && keyImageId != "null") {
                    val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"
                    val validFile = findValidImageFile(basePath)
                    imageFile = validFile ?: File(defaultPath)
                    if (validFile == null) {
                        imageFileName = "logo.webp"
                    }
                } else {
                    imageFile = File(defaultPath)
                    imageFileName = "logo.webp"
                }
                Log.d("GlideDisplay", "Final image path: ${imageFile?.absolutePath}")
            } catch (e: Exception) {
                Log.e("GlideDisplay", "Error loading image file", e)
                imageFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
                imageFileName = "logo.webp"
            }
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            model = imageFile ?: File("$imagesProduitsLocalExternalStorageBasePath/logo.webp"),
            contentDescription = "Product $keyImageId",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .blur(if (isLoading) 10.dp else 0.dp)  // Apply blur effect during loading
        ) { builder ->
            builder
                .downsample(com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.AT_MOST)
                .encodeQuality(qualityImage)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("GlideDisplay", "Load failed for $keyImageId", e)
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
                        Log.d("GlideDisplay", "Load complete for $keyImageId")
                        isLoading = false
                        onLoadComplete()
                        return false
                    }
                })
        }
    }
}
