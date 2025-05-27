package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val maxDisplayedItems = 3
    val itemsToShow = imageFiles.take(maxDisplayedItems)
    val hasMoreItems = imageFiles.size > maxDisplayedItems

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(imageSize * itemsToShow.size.coerceAtMost(maxDisplayedItems) +
                if (hasMoreItems) imageSize + 16.dp else 12.dp)
    ) {
        items(itemsToShow) { imageInfo ->
            SingleImageItem(
                imageInfo = imageInfo,
                size = imageSize,
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        }

        if (hasMoreItems) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(imageSize)
                        .height(imageSize)
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Plus d'articles",
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(32.dp)
                            .height(32.dp)
                    )
                }
            }
        }
    }
}
