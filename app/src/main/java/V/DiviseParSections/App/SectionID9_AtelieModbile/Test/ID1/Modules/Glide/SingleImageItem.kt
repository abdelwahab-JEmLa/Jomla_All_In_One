package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SingleImageItem(
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
            if (imageInfo.shouldShowColorText) {
                // Show color text instead of image
                ColorTextDisplay(
                    colorName = imageInfo.colorName,
                    modifier = Modifier.fillMaxSize()
                )
                onLoadComplete()
            } else if (!imageInfo.exists) {
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
                        .downsample(DownsampleStrategy.AT_MOST)
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
