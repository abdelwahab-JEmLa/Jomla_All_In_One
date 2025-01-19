package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.*
import java.io.File

private const val MIN_RELOAD_INTERVAL = 500L
private const val IMAGE_QUALITY = 3
private const val DEFAULT_IMAGE_ID = 10L
private const val DEFAULT_IMAGE = "10_1.jpg"

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideDisplayImageById2(
    itemMainId: Long,
    imageGlidReloadTigger: Int,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {}
) {
    var imageFile by remember { mutableStateOf<File?>(null) }
    var forceReload by remember { mutableIntStateOf(0) }
    var reloadSuccess by remember { mutableStateOf(false) }
    var previousTrigger by remember { mutableIntStateOf(0) }
    var lastReloadTimestamp by remember { mutableLongStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(itemMainId) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val currentTrigger = imageGlidReloadTigger

            if (currentTime - lastReloadTimestamp > MIN_RELOAD_INTERVAL &&
                currentTrigger != previousTrigger
            ) {
                lastReloadTimestamp = currentTime
                previousTrigger = currentTrigger
                forceReload++
                isLoading = true
                reloadSuccess = true
            }
            delay(MIN_RELOAD_INTERVAL)
        }
    }

    LaunchedEffect(itemMainId, forceReload) {
        withContext(Dispatchers.IO) {
            val imagePath = "$imagesProduitsLocalExternalStorageBasePath/${itemMainId}_1"
            imageFile = if (itemMainId == DEFAULT_IMAGE_ID) {
                File("$imagesProduitsLocalExternalStorageBasePath/$DEFAULT_IMAGE")
            } else {
                listOf("jpg", "jpeg", "png", "webp")
                    .map { File("$imagePath.$it") }
                    .firstOrNull { it.exists() && it.length() > 0 }
            }
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            model = imageFile ?: File("$imagesProduitsLocalExternalStorageBasePath/$DEFAULT_IMAGE"),
            contentDescription = "Product $itemMainId",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        ) { builder ->
            builder
                .downsample(com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.AT_MOST)
                .encodeQuality(IMAGE_QUALITY)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey("${itemMainId}_${forceReload}"))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        isLoading = false
                        loadProgress = 0f
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
                        loadProgress = 1f
                        if (reloadSuccess) {
                            onLoadComplete()
                            reloadSuccess = false
                        }
                        return false
                    }
                })
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                progress = loadProgress,
                color = Color.Blue
            )
        }
    }
}
