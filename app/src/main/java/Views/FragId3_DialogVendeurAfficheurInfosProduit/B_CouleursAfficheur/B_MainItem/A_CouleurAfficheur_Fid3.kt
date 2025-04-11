package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.Dialog.ColorSelectionDialog
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.View.GlideDisplayImageBykeyId
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_.WifiUpdateClientDisplayerStats
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
import androidx.compose.runtime.mutableLongStateOf
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
import org.koin.compose.koinInject

@Composable
fun B_CouleurAfficheur(
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
    currentClient: B_ClientsDataBase?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    parentCompose_1_2_ProduitAcheteOperationVid: Long,
) {
    // Using a simpler approach for visibility tracking
    var compose_1_1_CouleurAcheteOperationVid by remember { mutableLongStateOf(0L) }
    val _1_1_CouleurAcheteOperation_Repository = koinInject<_1_1_CouleurAcheteOperation_Repository>()
    val couleurActuelleId = color.idColore

    LaunchedEffect(key1 = parentCompose_1_2_ProduitAcheteOperationVid, key2 = couleurActuelleId) {
        // Only proceed if we have a valid parent ID - this is critical
        if (parentCompose_1_2_ProduitAcheteOperationVid <= 0) {
            return@LaunchedEffect
        }

        // Check if the color operation already exists
        val existing_1_1_CouleurAcheteOperation =
            _1_1_CouleurAcheteOperation_Repository.modelDatasSnapList.find {
                it.couleurId_ParentVID == couleurActuelleId &&
                        it.parentProduitAchateOperationVID == parentCompose_1_2_ProduitAcheteOperationVid
            }

        // Set the VID if it exists, otherwise create a new entry
        compose_1_1_CouleurAcheteOperationVid = if (existing_1_1_CouleurAcheteOperation != null) {
            existing_1_1_CouleurAcheteOperation.vid
        } else {
            // Create a new unique VID
            val maxVid = _1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .maxOfOrNull { it.vid } ?: 0
            val newVid = maxVid + 1

            // Create and add the new entry
            val newColorOp = _1_1_CouleurAcheteOperation(
                vid = newVid,
                couleurId_ParentVID = couleurActuelleId,
                parentProduitAchateOperationVID = parentCompose_1_2_ProduitAcheteOperationVid
            )

            // Explicitly add the data and verify
            _1_1_CouleurAcheteOperation_Repository.addData(newColorOp)

            // Verify the data was added by checking immediately after adding
            val wasAdded = _1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .any { it.vid == newVid }

            newVid
        }
    }

    // Rest of the code remains the same...
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
        // Updated condition to checkADD_1_4_PeriodeVent for any color position with quantity
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
                    color.let {
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
                    val articleproduitsMainDataBase =
                        viewModelInitApp._modelAppsFather.produitsMainDataBase.find { it.id.toInt() == article.idArticle }
                    GlideDisplayImageBykeyId(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp),
                        imageGlidReloadTigger = 0,
                        mainItem = articleproduitsMainDataBase,
                        size = 600.dp,
                        qualityImage = 100,
                        colorIndex = index
                    )

                }

                color.let { colorData ->
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

        if (showDialog) {
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
                color = color,
                compose_1_1_CouleurAcheteOperationVid=compose_1_1_CouleurAcheteOperationVid,
            )
        }
    }
}

@Composable
private fun QuantityBadge(
    quantity: Int,
    modifier: Modifier = Modifier,
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
    onColorClick: () -> Unit,
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
    onClick: () -> Unit,
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
