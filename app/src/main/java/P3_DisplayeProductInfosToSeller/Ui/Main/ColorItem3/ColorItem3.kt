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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            if (currentQuantity > 0) {  //-->
                //Hi Claud,what i went from u to do is to
                //Find All TODOs and Fix Them

                //TODO:
                // fait que ca soit au dessu de tiyt
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

