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

    // Fixed: Calculate proper width considering spacing and arrow
    val baseWidth = imageSize * itemsToShow.size.coerceAtMost(maxDisplayedItems)
    val spacingWidth = 4.dp * (itemsToShow.size - 1).coerceAtLeast(0)
    val arrowWidth = if (hasMoreItems) imageSize + 8.dp else 0.dp
    val totalWidth = baseWidth + spacingWidth + arrowWidth

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(totalWidth)
    ) {
        items(itemsToShow) { imageInfo ->
            SingleImageItem(
                imageInfo = imageInfo,
                size = imageSize,
                qualityImage = qualityImage,
                onLoadComplete = onLoadComplete
            )
        }

        // Fixed: Show arrow when there are more than maxDisplayedItems
        if (hasMoreItems) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(imageSize)
                        .height(imageSize)
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Plus d'articles (${imageFiles.size - maxDisplayedItems} de plus)",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
