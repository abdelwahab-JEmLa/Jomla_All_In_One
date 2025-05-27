package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MultipleImagesDisplay(
    imageFiles: List<ProductImageInfo>,
    size: Dp?,
    qualityImage: Int,
    onLoadComplete: () -> Unit
) {
    val imageSize = size ?: 80.dp

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(imageSize * imageFiles.size.coerceAtMost(3) + 12.dp)
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
