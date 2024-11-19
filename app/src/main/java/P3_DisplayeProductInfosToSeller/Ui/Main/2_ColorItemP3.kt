package P3_DisplayeProductInfosToSeller.Ui.Main
import P3_DisplayeProductInfosToSeller.Modules.ImageDisplayer3
import P4_SoldCartScreen.ImageDisplayer4
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    // Calculate current quantity based on color index
    val currentQuantity = remember(index, currentSale) {
        when (index) {
            0 -> currentSale?.color1SoldQuantity
            1 -> currentSale?.color2SoldQuantity
            2 -> currentSale?.color3SoldQuantity
            3 -> currentSale?.color4SoldQuantity
            else -> null
        } ?: 0
    }

    // Animation for selection highlight
    val cardElevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 2f,
        label = "cardElevation"
    )

    // Main Card using ElevatedCard instead of Card
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                isSelected = true
                showDialog = true
                color?.let { updateColorToBeMain(it.idColore) }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = cardElevation.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Quantity Indicator Badge
            if (currentQuantity > 0) {
                QuantityBadge(
                    quantity = currentQuantity,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Product Image
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    ImageDisplayer3(
                        modifier = Modifier.fillMaxSize(),
                        article = article,
                        viewModel = viewModel,
                        indexColor = index,
                        reloadKey = reloadTrigger
                    )
                }

                // Color Information Section
                color?.let { colorData ->
                    ColorInfoSection(
                        colorData = colorData,
                        onColorClick = {
                            showDialog = true
                            updateColorToBeMain(colorData.idColore)
                        }
                    )
                }
            }
        }
    }

    // Quantity Selection Dialog
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
        color = Color.Red.copy(alpha = 0.9f)
    ) {
        Text(
            text = quantity.toString(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
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
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color Name
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

        // Color Icon
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
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ColorSelectionDialog(
    onDismiss: () -> Unit,
    currentQuantity: Int,
    colorName: String,
    onQuantitySelected: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Dialog Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = colorName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Quantity Grid
                QuantityGrid(
                    currentQuantity = currentQuantity,
                    onQuantitySelected = { quantity ->
                        onQuantitySelected(quantity)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun QuantityGrid(
    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit
) {
    val quantities = remember {
        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(240.dp)
    ) {
        items(quantities.size) { index ->
            val quantity = quantities[index]
            QuantityButton(
                quantity = quantity,
                isSelected = quantity == currentQuantity,
                onClick = { onQuantitySelected(quantity) }
            )
        }
    }
}

@Composable
private fun QuantityButton(
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


