package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.imagesProduitsLocalExternalStorageBasePath
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

data class ProductImageInfo(
    val file: File,
    val couleurId: Int,
    val exists: Boolean = true
)

@Composable
fun A_GlideDisplayImageByKeyId_Proto_5(
    modifier: Modifier = Modifier,
    produitVID: Long? = null,
    size: Dp? = null,
    onLoadComplete: () -> Unit = {},
    qualityImage: Int = 3,
) {
    var imageFiles by remember { mutableStateOf<List<ProductImageInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val shouldUseDefaultImage by remember {
        mutableStateOf(produitVID == null)
    }

    suspend fun findAllValidImageFiles(produitId: Long): List<ProductImageInfo> {
        return withContext(Dispatchers.IO) {
            val extensions = listOf("jpg", "jpeg", "png", "webp")
            val validImages = mutableListOf<ProductImageInfo>()

            // Try to find images with couleurVID from 1 to 10
            for (couleurId in 1..10) {
                val keyImageId = "${produitId}_${couleurId}"
                val basePath = "$imagesProduitsLocalExternalStorageBasePath/$keyImageId"

                for (ext in extensions) {
                    val file = File("$basePath.$ext")
                    if (file.exists() && file.length() > 0) {
                        validImages.add(ProductImageInfo(file, couleurId, true))
                        break // Found valid image for this color, move to next color
                    }
                }
            }

            validImages
        }
    }

    // Load image files once on initial composition
    LaunchedEffect(produitVID) {
        withContext(Dispatchers.IO) {
            try {
                val defaultPath = "$imagesProduitsLocalExternalStorageBasePath/logo.webp"
                val defaultFile = File(defaultPath)

                if (!shouldUseDefaultImage && produitVID != null) {
                    val validFiles = findAllValidImageFiles(produitVID)

                    if (validFiles.isEmpty()) {
                        // No images found, use default
                        imageFiles = listOf(ProductImageInfo(defaultFile, 0, false))
                    } else {
                        // Use found images
                        imageFiles = validFiles
                    }
                } else {
                    // Use default image
                    imageFiles = listOf(ProductImageInfo(defaultFile, 0, false))
                }
            } catch (e: Exception) {
                val defaultFile = File("$imagesProduitsLocalExternalStorageBasePath/logo.webp")
                imageFiles = listOf(ProductImageInfo(defaultFile, 0, false))
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier.then(size?.let { Modifier.size(it) } ?: Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        if (imageFiles.isEmpty() || isLoading) {
            OnImageExistPas()
        } else if (imageFiles.size == 1) {
            // Single image display
            SingleImageDisplay(
                imageInfo = imageFiles.first(),
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        } else {
            // Multiple images in LazyRow
            MultipleImagesDisplay(
                imageFiles = imageFiles,
                size = size,
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SingleImageDisplay(
    imageInfo: ProductImageInfo,
    qualityImage: Int,
    onLoadComplete: () -> Unit
) {
    var isImageLoading by remember { mutableStateOf(true) }

    if (!imageInfo.exists) {
        OnImageExistPas()
    } else {
        GlideImage(
            model = imageInfo.file,
            contentDescription = "Product image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .blur(if (isImageLoading) 10.dp else 0.dp)
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
                        isImageLoading = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        isImageLoading = false
                        onLoadComplete()
                        return false
                    }
                })
        }
    }
}

@Composable
private fun MultipleImagesDisplay(
    imageFiles: List<ProductImageInfo>,
    size: Dp?,
    qualityImage: Int,
    onLoadComplete: () -> Unit
) {
    val imageSize = size ?: 80.dp

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(imageSize)
    ) {
        items(imageFiles) { imageInfo ->
            SingleImageItem(
                imageInfo = imageInfo,
                size = imageSize,
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SingleImageItem(
    imageInfo: ProductImageInfo,
    size: Dp,
    qualityImage: Int,
    onLoadComplete: () -> Unit
) {
    var isImageLoading by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.size(size),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!imageInfo.exists) {
                OnImageExistPas()
            } else {
                GlideImage(
                    model = imageInfo.file,
                    contentDescription = "Product color variant ${imageInfo.couleurId}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(if (isImageLoading) 8.dp else 0.dp)
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
                                isImageLoading = false
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                isImageLoading = false
                                onLoadComplete()
                                return false
                            }
                        })
                }
            }
        }
    }
}

@Composable
private fun OnImageExistPas() {
    Card(
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📦",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Pas d'image",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
