package Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideDisplayImageBykeyId(
    imageGlidReloadTigger: Int = 0,
    mainItem: _ModelAppsFather.ProduitModel? = null,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {}
) {
    var imageFile by remember { mutableStateOf<File?>(null) }
    var forceReload by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var lastReloadTime by remember { mutableLongStateOf(0L) }
    var prevTrigger by remember { mutableIntStateOf(0) }
    var reloadSuccess by remember { mutableStateOf(false) }

    val keyImageId = if (mainItem == null) "null" else "${mainItem.id}_1"
    var shouldUseDefaultImage by remember {
        mutableStateOf(keyImageId == "null" || mainItem?.coloursEtGouts?.any { it.sonImageNeExistPas } == true)
    }

    // Check for image existence changes
    LaunchedEffect(mainItem?.coloursEtGouts) {
        val hasNoImage = mainItem?.coloursEtGouts?.any { it.sonImageNeExistPas } == true
        if (hasNoImage != shouldUseDefaultImage) {
            shouldUseDefaultImage = hasNoImage
            forceReload++
            isLoading = true
            reloadSuccess = true
        }
    }

    // Handle reload trigger
    LaunchedEffect(keyImageId) {
        while (true) {
            val now = System.currentTimeMillis()
            if (now - lastReloadTime > 500L && imageGlidReloadTigger != prevTrigger) {
                lastReloadTime = now
                prevTrigger = imageGlidReloadTigger
                forceReload++
                isLoading = true
                reloadSuccess = true
            }
            delay(500L)
        }
    }

    // Load image file
    LaunchedEffect(keyImageId, forceReload, shouldUseDefaultImage) {
        withContext(Dispatchers.IO) {
            val defaultPath = "$imagesProduitsLocalExternalStorageBasePath/logo.webp"
            imageFile = when {
                shouldUseDefaultImage -> File(defaultPath)
                keyImageId == "null" -> File(defaultPath)
                else -> {
                    val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"
                    listOf("jpg", "jpeg", "png", "webp")
                        .map { File("$basePath.$it") }
                        .firstOrNull { it.exists() && it.length() > 0 }
                        ?: File(defaultPath)
                }
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
        ) { builder ->
            builder
                .downsample(com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.AT_MOST)
                .encodeQuality(3)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey("${keyImageId}_${forceReload}_${if(shouldUseDefaultImage) "default" else "custom"}"))
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
                progress = { loadProgress },
                modifier = Modifier.size(48.dp),
                color = Color.Blue,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
    }
}
