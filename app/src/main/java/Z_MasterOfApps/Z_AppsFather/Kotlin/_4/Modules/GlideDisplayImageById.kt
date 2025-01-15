package com.example.Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.bumptech.glide.signature.ObjectKey
import com.example.Z_AppsFather.Kotlin._1.Model.Parent.ArticleInfosModel
import com.example.c_serveur.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val MIN_RELOAD_INTERVAL = 500L
private const val IMAGE_QUALITY = 3

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideDisplayImageById(
    itemMain: ArticleInfosModel,
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

    // Monitor product changes and trigger reloads
    LaunchedEffect(itemMain.id) {
        while (true) {
            itemMain.let { product ->
                val currentTime = System.currentTimeMillis()
                val currentTrigger = product.imageGlidReloadTigger

                if (currentTime - lastReloadTimestamp > MIN_RELOAD_INTERVAL &&
                    currentTrigger != previousTrigger) {
                    lastReloadTimestamp = currentTime
                    previousTrigger = currentTrigger
                    forceReload++
                    isLoading = true
                    reloadSuccess = true

                    if (product.sonImageBesoinActualisation) {
                        delay(1000)
                        product.besoinToBeUpdated = true
                        product.imageGlidReloadTigger++
                    }
                }
            }
            delay(MIN_RELOAD_INTERVAL)
        }
    }

    // Load image file
    LaunchedEffect(itemMain.id, forceReload) {
        withContext(Dispatchers.IO) {
            val imagePath = "$imagesProduitsLocalExternalStorageBasePath/${itemMain.id}_1"
            imageFile = listOf("jpg", "jpeg", "png", "webp")
                .map { File("$imagePath.$it") }
                .firstOrNull { it.exists() && it.length() > 0 }
        }
    }

    // Display image
    Box(modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize())) {
        GlideImage(
            model = imageFile ?: R.drawable.ic_launcher_background,
            contentDescription = "Product $itemMain.id",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        ) { builder ->
            builder
                .downsample(com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.AT_MOST)
                .encodeQuality(IMAGE_QUALITY)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey("${itemMain.id}_${forceReload}"))
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
                        if (reloadSuccess) {
                            onLoadComplete()
                            reloadSuccess = false
                        }
                        return false
                    }
                })
        }
    }
}
