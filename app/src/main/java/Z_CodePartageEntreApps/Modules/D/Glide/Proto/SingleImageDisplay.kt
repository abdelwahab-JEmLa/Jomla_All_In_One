package Z_CodePartageEntreApps.Modules.D.Glide.Proto

import EntreApps.Shared.Models.M01Produit
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SingleImageDisplay(
    imageInfo: CalculeCouleurHandler.ProductImageInfo,
    qualityImage: Int,
    onLoadComplete: () -> Unit,
    actualiseSonImage: Int = 0,
    imageRefreshKey: String? = null,
    product: M01Produit?
) {
    // Check if we should show color text instead of image
    if (imageInfo.shouldShowColorText && imageInfo.colorName.isNotEmpty()) {
        ColorTextDisplay(
            colorName = imageInfo.colorName,
            modifier = Modifier.size(80.dp)
        )
        onLoadComplete()
    } else if (!imageInfo.exists) {
        // FIXED: Pass image path and product name to show debugging info
        OnImageExistPas(
            imagePath = imageInfo.file.absolutePath,
            productName = imageInfo.productName
        )
        onLoadComplete()
    } else {
        // Use the existing GlidDisplaye for actual image display
        // We pass the imageRefreshKey as the imageGlidReloadTigger parameter
        GlidDisplaye(
            imageGlidReloadTigger = imageRefreshKey?.hashCode() ?: actualiseSonImage,
            mainItem = product, // We don't have the full product item here, just the image info
            size = 80.dp,
            onLoadComplete = onLoadComplete,
            qualityImage = qualityImage,
            colorIndex = imageInfo.couleurId - 1 // Convert from 1-based to 0-based index
        )
    }
}
