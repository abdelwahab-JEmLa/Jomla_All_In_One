package Views.Z_P3.Ui.Main.ColorItem3

import Views.Z_P3.Ui.Objects.ImageDisplayer3
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3._DisplayeProductInfosToSeller
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
import androidx.compose.runtime.LaunchedEffect
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
    color: ColorsArticlesTabelle,
    index: Int,
    reloadTrigger: Int,
    viewModel: HeadViewModel,
    height: Dp,
    updateColorToBeMain: (Long) -> Unit,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
) {
    var showDialog by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }

    // Enhanced quantity tracking that considers all color positions
    val currentQuantity = remember(color.idColore, currentSale) {
        currentSale?.let { sale ->
            when (color.idColore) {
                sale.color1IdPicked -> sale.color1SoldQuantity
                sale.color2IdPicked -> sale.color2SoldQuantity
                sale.color3IdPicked -> sale.color3SoldQuantity
                sale.color4IdPicked -> sale.color4SoldQuantity
                else -> 0
            }
        } ?: 0
    }

    // Check if this color has any quantity across all positions
    val hasQuantity = remember(currentSale) {
        currentSale?.let { sale ->
            (sale.color1IdPicked == color.idColore && sale.color1SoldQuantity > 0) ||
                    (sale.color2IdPicked == color.idColore && sale.color2SoldQuantity > 0) ||
                    (sale.color3IdPicked == color.idColore && sale.color3SoldQuantity > 0) ||
                    (sale.color4IdPicked == color.idColore && sale.color4SoldQuantity > 0)
        } ?: false
    }

    // Track whether this color is currently selected as main
    val isMainColor = remember(color.idColore, currentSale) {
        color.idColore == currentSale?.color1IdPicked
    }

    val cardElevation by animateFloatAsState(
        targetValue = if (isSelected || isMainColor) 8f else 2f,
        label = "cardElevation"
    )

    LaunchedEffect(key1 = currentSale?.idArticle) {
        // Updated condition to check for any color position with quantity
        if (hasQuantity) {
            _DisplayeProductInfosToSeller(viewModelInitApp)
                .onClickComposeQuantityButton(
                    1,
                    currentSale,
                    currentClient,
                    color,
                )
        }
    }

    Box(
        modifier = modifier.height(height)
    ) {
        // Show quantity badge if there's any quantity for this color
        if (currentQuantity > 0) {
            QuantityBadge(
                quantity = currentQuantity,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .zIndex(1f)
            )
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clickable {
                    isSelected = true
                    showDialog = true
                    color?.let {
                        updateColorToBeMain(it.idColore)
                        viewModel.sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                            it.idColore
                        )
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (isMainColor)
                    MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                contentColor = if (isMainColor)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = cardElevation.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
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

        if (showDialog && color != null) {
            ColorSelectionDialog(
                currentSale = currentSale,
                viewModelInitApp = viewModelInitApp,
                onDismiss = {
                    showDialog = false
                    isSelected = false
                },
                currentQuantity = currentQuantity,
                colorName = color.nameColore,
                onQuantitySelected = { newQuantity ->
                    viewModel.updateColorSelection(color.idColore, newQuantity)
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientWindowsLazyRowSupColorsScrolle.prefix,
                        index
                    )
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                        color.idColore
                    )
                    viewModel.saveSaleTransactionToSoldAriclesList()
                    showDialog = false
                    isSelected = false
                },
                currentClient = currentClient,
                indexColoreAcheter = index,
                colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                color = color
            )
        }
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
                .padding(end = 2.dp),
            style = MaterialTheme.typography.bodySmall,
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
fun ColorIcon(
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

