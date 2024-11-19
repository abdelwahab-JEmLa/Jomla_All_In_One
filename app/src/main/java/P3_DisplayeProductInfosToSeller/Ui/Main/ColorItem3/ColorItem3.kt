package P3_DisplayeProductInfosToSeller.Ui.Main.ColorItem3
import P3_DisplayeProductInfosToSeller.Modules.ImageDisplayer3
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.WifiUpdateClientDisplayerStats

@Composable
fun ColorItem3(
    modifier: Modifier = Modifier,
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    index: Int,
    reloadTrigger: Int,
    viewModel: HeadViewModel,
    height: Dp,
    updateColorToBeMain: (Long) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }

    val currentQuantity = remember(index, currentSale) {
        when (index) {
            0 -> currentSale?.color1SoldQuantity
            1 -> currentSale?.color2SoldQuantity
            2 -> currentSale?.color3SoldQuantity
            3 -> currentSale?.color4SoldQuantity
            else -> null
        } ?: 0
    }

    val cardElevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 2f,
        label = "cardElevation"
    )

    Box(
        modifier = modifier.height(height)
    ) {
        // Quantity Badge - Now properly positioned above all content
        if (currentQuantity > 0) {
            QuantityBadge(
                quantity = currentQuantity,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .zIndex(1f) // Ensures badge stays on top
            )
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clickable {
                    isSelected = true
                    showDialog = true
                    color?.let { updateColorToBeMain(it.idColore) }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = cardElevation.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Product Image with proper aspect ratio
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    ImageDisplayer3(
                        modifier = Modifier.fillMaxSize(),
                        article = article,
                        viewModel = viewModel,
                        indexColor = index,
                        reloadKey = reloadTrigger
                    )
                }

                // Color Information with improved layout
                color?.let { colorData ->
                    ColorInfoSection(
                        colorData = colorData,
                        onColorClick = {
                            updateColorToBeMain(colorData.idColore)
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog with improved UI
    if (showDialog && color != null) {
        ColorSelectionDialog(
            onDismiss = {
                showDialog = false
                isSelected = false
            },
            currentQuantity = currentQuantity,
            colorName = color.nameColore,
            onQuantitySelected = { quantity ->
                viewModel.updateColorSelection(index, quantity)
                viewModel.sendOrderToClientDisplayer(
                    WifiUpdateClientDisplayerStats.ClientWindowsLazyRowSupColorsScrolle.prefix,
                    quantity
                )
                viewModel.sendOrderToClientDisplayer(
                    WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                    color.idColore
                )
                viewModel.saveSaleTransactionToSoldAriclesList()
            }
        )
    }
}

@Composable
private fun QuantityBadge(
    quantity: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    ) {
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun ColorInfoSection(
    colorData: ColorsArticlesTabelle,
    onColorClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = colorData.nameColore,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        ColorIcon(
            iconColore = colorData.iconColore,
            onClick = onColorClick
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorIcon(
    iconColore: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                iconColore == "©" || iconColore == "💯" || iconColore.isEmpty() -> {
                    GlideImage(
                        model = R.drawable.logo,
                        contentDescription = "Color logo",
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                else -> {
                    Text(
                        text = iconColore,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
