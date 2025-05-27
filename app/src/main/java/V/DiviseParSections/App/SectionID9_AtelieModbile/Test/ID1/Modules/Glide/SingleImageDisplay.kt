package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import java.security.MessageDigest

class ObjectKey(private val obj: Any) : Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(obj.toString().toByteArray())
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SingleImageDisplay(
    imageInfo: ProductImageInfo,
    qualityImage: Int,
    onLoadComplete: () -> Unit,
    actualiseSonImage: Int = 0
) {
    var isImageLoading by remember { mutableStateOf(true) }
    var imageRefreshKey by remember { mutableStateOf("${imageInfo.file.absolutePath}_$actualiseSonImage") }

    val TAG = "SingleImageDisplay"

    Log.d(TAG, "=== SingleImageDisplay Composable ===")
    Log.d(TAG, "Image file: ${imageInfo.file.absolutePath}")
    Log.d(TAG, "Exists: ${imageInfo.exists}")
    Log.d(TAG, "Product: ${imageInfo.productName}")
    Log.d(TAG, "Color ID: ${imageInfo.couleurId}")
    Log.d(TAG, "actualiseSonImage parameter: $actualiseSonImage")
    Log.d(TAG, "imageInfo.actualiseSonImage: ${imageInfo.actualiseSonImage}")
    Log.d(TAG, "shouldShowColorText: ${imageInfo.shouldShowColorText}")
    Log.d(TAG, "Current imageRefreshKey: $imageRefreshKey")

    LaunchedEffect(actualiseSonImage, imageInfo.file.absolutePath) {
        val oldKey = imageRefreshKey
        imageRefreshKey = "${imageInfo.file.absolutePath}_$actualiseSonImage"

        Log.d(TAG, "=== LaunchedEffect triggered ===")
        Log.d(TAG, "actualiseSonImage changed: $actualiseSonImage")
        Log.d(TAG, "File path: ${imageInfo.file.absolutePath}")
        Log.d(TAG, "Old refresh key: $oldKey")
        Log.d(TAG, "New refresh key: $imageRefreshKey")
        Log.d(TAG, "Setting isImageLoading = true")

        isImageLoading = true // Reset loading state when image should refresh
    }

    if (imageInfo.shouldShowColorText) {
        Log.d(TAG, "Displaying color text: ${imageInfo.colorName}")
        ColorTextDisplay(
            colorName = imageInfo.colorName,
            modifier = Modifier.fillMaxSize()
        )
        onLoadComplete()
    } else if (!imageInfo.exists) {
        Log.d(TAG, "Image doesn't exist, showing placeholder")
        OnImageExistPas()
    } else {
        Log.d(TAG, "=== Creating GlideImage ===")
        Log.d(TAG, "Model: ${imageInfo.file}")
        Log.d(TAG, "Signature key: $imageRefreshKey")
        Log.d(TAG, "Quality: $qualityImage")
        Log.d(TAG, "isImageLoading: $isImageLoading")

        GlideImage(
            model = imageInfo.file,
            contentDescription = "Product image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .blur(if (isImageLoading) 10.dp else 0.dp)
        ) { builder ->
            Log.d(TAG, "Configuring Glide builder...")
            builder
                .downsample(DownsampleStrategy.AT_MOST)
                .encodeQuality(qualityImage)
                .signature(ObjectKey(imageRefreshKey))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "=== Glide onLoadFailed ===")
                        Log.e(TAG, "Model: $model")
                        Log.e(TAG, "Error: ${e?.message}")
                        Log.e(TAG, "Root causes: ${e?.rootCauses}")
                        Log.e(TAG, "isFirstResource: $isFirstResource")

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
                        Log.d(TAG, "=== Glide onResourceReady ===")
                        Log.d(TAG, "Model: $model")
                        Log.d(TAG, "DataSource: $dataSource")
                        Log.d(TAG, "isFirstResource: $isFirstResource")
                        Log.d(TAG, "Resource size: ${resource.intrinsicWidth}x${resource.intrinsicHeight}")

                        isImageLoading = false
                        onLoadComplete()
                        return false
                    }
                }).also {
                    Log.d(TAG, "Glide builder configuration completed")
                }
        }
    }
}
