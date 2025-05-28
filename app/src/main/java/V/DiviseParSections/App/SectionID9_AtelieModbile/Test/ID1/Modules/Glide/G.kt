package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import android.util.Log
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
    mainItem: A_ProduitInfosTest? = null,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {},
    qualityImage: Int = 3,
    colorIndex: Int = 0
) {
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var imageFileName by remember { mutableStateOf("") }

    // FIXED: Generate proper key for color-specific images
    val keyImageId = if (mainItem == null) "logo" else "${mainItem.id}_${colorIndex + 1}"

    val imageKey by remember(keyImageId, imageGlidReloadTigger) {
        derivedStateOf { "$keyImageId-$imageGlidReloadTigger-${System.currentTimeMillis()}" }
    }

    // FIXED: Only use default image when mainItem is null, not when colorIndex is 0
    val shouldUseDefaultImage = mainItem == null

    // Function to find valid image file
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

    // FIXED: Load image file with proper error handling and fallback logic
    LaunchedEffect(imageKey) {
        withContext(Dispatchers.IO) {
            isLoading = true
            try {
                val defaultPath = "$imagesProduitsLocalExternalStorageBasePath/logo.webp"
                val defaultFile = File(defaultPath)

                if (shouldUseDefaultImage) {
                    // Use default logo
                    imageFile = defaultFile
                    imageFileName = "logo.webp"
                    Log.d("GlideDisplay", "Using default image: ${defaultFile.absolutePath}")
                } else {
                    // Try to find product-specific image
                    val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"
                    val validFile = findValidImageFile(basePath)

                    if (validFile != null && validFile.exists()) {
                        imageFile = validFile
                        Log.d("GlideDisplay", "Using product image: ${validFile.absolutePath}")
                    } else {
                        // Fallback to default if product image doesn't exist
                        imageFile = defaultFile
                        imageFileName = "logo.webp"
                        Log.d("GlideDisplay", "Product image not found, using default: ${defaultFile.absolutePath}")
                    }
                }
            } catch (e: Exception) {
                Log.e("GlideDisplay", "Error loading image file for key: $keyImageId", e)
                // Final fallback
                imageFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
                imageFileName = "logo.webp"
            } finally {
                // FIXED: Always set loading to false
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        // FIXED: Ensure we have a valid file before displaying
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
                    // FIXED: Use proper caching strategy
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
                    // FIXED: Use a proper signature for cache invalidation
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
                            Log.e("GlideDisplay", "Load failed for $keyImageId at ${displayFile.absolutePath}", e)
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
                            Log.d("GlideDisplay", "Load complete for $keyImageId from ${dataSource.name}")
                            isLoading = false
                            onLoadComplete()
                            return false
                        }
                    })
            }
        } else {
            // FIXED: Show placeholder when file doesn't exist
            Log.w("GlideDisplay", "Image file doesn't exist: ${displayFile.absolutePath}")
            OnImageExistPas()
            isLoading = false
        }
    }
}
