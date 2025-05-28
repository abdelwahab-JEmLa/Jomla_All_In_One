package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SingleImageDisplay(
    imageInfo: ProductImageInfo,
    qualityImage: Int,
    onLoadComplete: () -> Unit,
    actualiseSonImage: Int = 0,
    imageRefreshKey: String? = null
) {
    // Check if we should show color text instead of image
    if (imageInfo.shouldShowColorText && imageInfo.colorName.isNotEmpty()) {
        ColorTextDisplay(
            colorName = imageInfo.colorName,
            modifier = Modifier.size(80.dp)
        )
        onLoadComplete()
    } else if (!imageInfo.exists) {
        // Show placeholder when no image exists
        OnImageExistPas()
        onLoadComplete()
    } else {
        // Use the existing GlidDisplaye for actual image display
        // We pass the imageRefreshKey as the imageGlidReloadTigger parameter
        GlidDisplaye(
            imageGlidReloadTigger = imageRefreshKey?.hashCode() ?: actualiseSonImage,
            mainItem = null, // We don't have the full product item here, just the image info
            size = 80.dp,
            onLoadComplete = onLoadComplete,
            qualityImage = qualityImage,
            colorIndex = imageInfo.couleurId - 1 // Convert from 1-based to 0-based index
        )
    }
}
