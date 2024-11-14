package P3_DisplayeProductInfosToSeller

import P1_StartupScreen.Ui.AutoResizedText
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

@Composable
fun P3DisplayeProductInfosToSeller(
    uiState: UiState,
    viewModel: HeadViewModel,
    onDismiss: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
) {
    val currentSale = viewModel.currentSaleInWindows.collectAsState().value
    val articlesBaseStats = currentSale?.let { sale ->
        uiState.articlesBasesStatTables.find {
            it.idArticle.toLong() ==   sale.idArticle
        }
    }

    var isDetailsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isDetailsVisible = true
    }


    if (currentSale != null) {
        MainUi(
            modifier,
            articlesBaseStats,
            currentSale,
            viewModel,
            reloadTrigger,
            uiState,
            isDetailsVisible,
            onDismiss
        )
    }
}

@Composable
private fun MainUi(
    modifier: Modifier,
    articlesBaseStats: ArticlesBasesStatsTable?,
    currentSale: SoldArticlesTabelle,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    uiState: UiState,
    isDetailsVisible: Boolean,
    onDismiss: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    showConfirmDialog = confirmDialog(showConfirmDialog, viewModel, onDismiss)

    Dialog(
        onDismissRequest = { showConfirmDialog = true },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                articlesBaseStats?.let { stats ->
                    ProductNameSection(stats)

                    // Visual Divider with Label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "اختر اللون",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    // Colors Selection with Animation
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        ColorsCards(
                            currentSale = currentSale,
                            articlesBasesStatsTable = stats,
                            viewModel = viewModel,
                            relodeTigger = reloadTrigger,
                            uiState = uiState,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }

                    // Details Card with Animation
                    Details(isDetailsVisible, stats)
                }

                ActionsButtonRow(
                    // TODO:  fait que ca soit s affiche toutjoure au base du dialoge
                    //comme button bar et fait anime infenie du button طلب
                    //comme    LaunchedEffect(Unit) {
                    //        while(true) {
                    //            if(isRed) {
                    //                delay(700)
                    //                isRed = false
                    //            }  else {
                    //                delay(6000)
                    //                isRed = true
                    //            }
                    //        }
                    //    }
                    //
                    //    Card(
                    //        modifier = Modifier
                    //            .fillMaxSize()
                    //            .wrapContentHeight(),
                    //        elevation = CardDefaults.cardElevation(4.dp),
                    //        colors = CardDefaults.cardColors(
                    //            containerColor = animateColorAsState(
                    //                if (isRed) Color.Red else Color.White,
                    //                label = "backgroundColor"
                    //            ).value
                    //        )
                    onConfirm = {
                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()
                    },
                    onCancel = {
                        if (currentSale != null) {
                            viewModel.deleteSoldArticle(currentSale.vid)
                        }
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun confirmDialog(
    showConfirmDialog: Boolean,
    viewModel: HeadViewModel,
    onDismiss: () -> Unit
): Boolean {
    var showConfirmDialog1 = showConfirmDialog
    if (showConfirmDialog1) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog1 = false },
            icon = { Icon(Icons.Outlined.Warning, contentDescription = null) },
            title = {
                Text(
                    text = stringResource(R.string.confirm_exit_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.save_changes_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.confirm_order_button))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(stringResource(R.string.discard_button))
                    }
                }
            }
        )
    }
    return showConfirmDialog1
}

@Composable
private fun ProductNameSection(article: ArticlesBasesStatsTable) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Name Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = article.nomArticleFinale,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                if (article.nomArab.isNotEmpty()) {
                    Text(
                        text = article.nomArab,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ColumnScope.Details(
    isDetailsVisible: Boolean,
    article: ArticlesBasesStatsTable
) {
    AnimatedVisibility(
        visible = isDetailsVisible,
        enter = fadeIn() + expandVertically(),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // Price Section
                InfoRow(
                    label = "السعر للوحدة",
                    value = "${article.monPrixVent} ",
                    unite = "دج"
                )

                InfoRow(
                    label = "السعر بالجملة",
                    value = "${
                        String.format(
                            "%.2f",
                            article.monPrixVent / article.nmbrUnite.toFloat()
                        )
                    } ",
                    unite = "دج"
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // Units Information
                InfoRow(
                    label = "عدد الحبات",
                    value = "${article.nmbrUnite} ",
                    unite = "وحدة"
                )

                // Carton Information
                InfoRow(
                    label = "في الكرتون",
                    value = "${article.nmbrCaron} ",
                    unite = "علبة"
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    unite: String,

    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = unite,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Icon(
            Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsSectionPreview() {
    MaterialTheme {
        ProductNameSection(
            ArticlesBasesStatsTable(
                nomArticleFinale = "Product Name",
                nomArab = "اسم المنتج",
                nmbrUnite = 12,
                nmbrCaron = 24,
                monPrixVent = 1200.0
            )
        )
    }
}
@Composable
private fun ActionsButtonRow(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
    ) {
        OutlinedButton(
            onClick = {
                onCancel()
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            )
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.cancel_button))
        }

        FilledTonalButton(
            onClick = onConfirm,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Outlined.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.confirm_purchase_button))
        }
    }
}
@Composable
private fun ColorsCards(
    currentSale: SoldArticlesTabelle?,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    uiState: UiState,
) {
    val colors = listOf(
        articlesBasesStatsTable.idcolor1,
        articlesBasesStatsTable.idcolor2,
        articlesBasesStatsTable.idcolor3,
        articlesBasesStatsTable.idcolor4
    ).mapNotNull { colorId ->
        if (colorId != 0L) {
            uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }
        } else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (colors.size) {
            1 -> SingleColorLayout(
                currentSale=currentSale,
                article = articlesBasesStatsTable,
                color = colors[0],
                viewModel = viewModel,
                relodeTigger = relodeTigger,
            )
            else -> MultipleColorsLayout(
                currentSale=currentSale,
                article = articlesBasesStatsTable,
                colors = colors,
                viewModel = viewModel,
                relodeTigger = relodeTigger,
            )
        }
    }
}

@Composable
private fun SingleColorLayout(
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle,
    viewModel: HeadViewModel,
    relodeTigger: Int,
) {
    val height = 250.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        ColorItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            currentSale = currentSale,
            article = article,
            color = color,
            index = 0,
            relodeTigger = relodeTigger,
            viewModel = viewModel,
            height = height,
        )
    }
}

@Composable
private fun MultipleColorsLayout(
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    colors: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    relodeTigger: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        colors.chunked(2).forEach { rowColors ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(rowColors) { color ->
                    val index = colors.indexOf(color)
                    val height = 250.dp
                    Box(
                        modifier = Modifier
                            .size(height)
                    ) {
                        ColorItem(
                            modifier = Modifier.fillMaxSize(),
                            currentSale=currentSale,
                            article = article,
                            color = color,
                            index = index,
                            relodeTigger = relodeTigger,
                            viewModel = viewModel,
                            height=height
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorItem(
    modifier: Modifier,
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    index: Int,
    relodeTigger: Int,
    viewModel: HeadViewModel,
    height: Dp,
) {
    var showPicker by remember {
        mutableStateOf(
            when (index) {
                0 -> currentSale?.color1SoldQuantity
                1 -> currentSale?.color2SoldQuantity
                2 -> currentSale?.color3SoldQuantity
                3 -> currentSale?.color4SoldQuantity
                else -> null
            }?.let { it > 0 } ?: false
        )
    }

    val currentQuantity = remember(index, currentSale) {
        when (index) {
            0 -> currentSale?.color1SoldQuantity
            1 -> currentSale?.color2SoldQuantity
            2 -> currentSale?.color3SoldQuantity
            3 -> currentSale?.color4SoldQuantity
            else -> null
        } ?: 0
    }

    LaunchedEffect(showPicker) {
        if (!showPicker) {
            viewModel.saveSaleTransactionToSoldAriclesList()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = index,
                    reloadKey = relodeTigger
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {


                    AnimatedVisibility(
                        visible = !showPicker,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        color?.let { colorData ->
                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(0.6f)
                                            .wrapContentHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.matchParentSize(),
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.7f),
                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
                                        ) {}

                                        AutoResizedText(
                                            text = colorData.nameColore,
                                            modifier = Modifier.clickable { showPicker = true },
                                            color = Color.Black,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(0.4f)
                                            .wrapContentHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier.matchParentSize(),
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.8f),
                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f))
                                        ) {}


                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                            tonalElevation = 4.dp,
                                            shadowElevation = 4.dp
                                        ) {
                                            if (colorData.iconColore == "©" || colorData.iconColore == "💯"|| colorData.iconColore == "") {
                                                GlideImage(
                                                    model = R.drawable.logo,
                                                    contentDescription = "Logo",
                                                    modifier = Modifier
                                                        .size(
                                                            38.dp
                                                        )
                                                        .clickable { showPicker = true }
                                                )
                                            } else {
                                                Text(
                                                    text = colorData.iconColore,
                                                    fontSize =  38.sp,
                                                    fontWeight = FontWeight.Bold ,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.clickable { showPicker = true }
                                                )
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .offset(x = 15.dp, y = 18.dp)
                                                .size(40.dp)
                                                .clickable { showPicker = true }
                                        ) {
                                            GlideImage(
                                                model = R.drawable.hand,
                                                contentDescription = "Click indicator",
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showPicker,
                modifier = Modifier.weight(0.3f),
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                if (showPicker && color != null) {
                    CompactQuantityPicker(
                        onClosePick = { showPicker = false },
                        colorIndex = index,
                        viewModel = viewModel,
                        initialQuantity = currentQuantity,
                        height=height
                    )
                }
            }
        }
    }
}

@Composable
fun CompactQuantityPicker(
    onClosePick: () -> Unit,
    colorIndex: Int,
    viewModel: HeadViewModel,
    initialQuantity: Int = 0,
    height: Dp
) {
    var isRed by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while(true) {
            if(isRed) {
                delay(700)
                isRed = false
            }  else {
                delay(6000)
                isRed = true
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = animateColorAsState(
                if (isRed) Color.Red else Color.White,
                label = "backgroundColor"
            ).value
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = {
                        viewModel.updateColorSelection(colorIndex, 0)
                        onClosePick()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Close picker",
                        tint = Color.Black
                    )
                }
            }

            val values = remember {
                (1..15).map { it.toString() } +
                        (20..25).map { it.toString() } +
                        listOf("30", "40", "50")
            }

            val valuesPickerState = rememberPickerState()

            LaunchedEffect(Unit) {
                valuesPickerState.selectedItem = "50"
                delay(3000)
                valuesPickerState.selectedItem = initialQuantity.toString()
            }

            Picker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .size(height),
                items = values,
                state = valuesPickerState,
                visibleItemsCount = 8,
                textModifier = Modifier.padding(4.dp),
                textStyle = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = animateColorAsState(
                        if (isRed) Color.White else Color.Red,
                        label = "textColor"
                    ).value
                ),
                dividerColor = animateColorAsState(
                    if (isRed) Color.White else Color.Red,
                    label = "dividerColor"
                ).value,
                startIndex = values.indexOfFirst { it == initialQuantity.toString() }.coerceAtLeast(0),
                onItemStat = { viewModel.updateColorSelection(colorIndex, it.toInt()) }
            )
        }
    }
}

@Composable
fun Picker(
    modifier: Modifier = Modifier,
    items: List<String>,
    state: PickerState = rememberPickerState(),
    startIndex: Int = 0,
    visibleItemsCount: Int = 8,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
    onItemStat: (String) -> Unit,
) {
    var minusToReglePosition by remember { mutableStateOf(7.0) }

    val centerPosition = 1
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = listScrollMiddle - listScrollMiddle % items.size - centerPosition + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)

    // Simplified fling behavior
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val itemHeightPixels = remember { mutableStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.value)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.3f to Color.Black,
            0.7f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + centerPosition) }
            .distinctUntilChanged()
            .collect { item ->
                state.selectedItem = item
                onItemStat(item)
                minusToReglePosition = 7.0
            }
    }

    val scope = rememberCoroutineScope()
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
                .pointerInput(Unit) {
                    var lastDragPosition = Offset.Zero
                    var totalDragDistance = 0f

                    detectDragGestures(
                        onDragStart = { lastDragPosition = it },
                        onDragEnd = {
                            scope.launch {
                                val velocity = totalDragDistance * 1.5f
                                val targetOffset =
                                    decayAnimationSpec.calculateTargetValue(0f, velocity)
                                val targetIndex =
                                    (targetOffset / itemHeightPixels.value).roundToInt()
                                listState.animateScrollBy(
                                    targetIndex * itemHeightPixels.value.toFloat(),
                                    spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                totalDragDistance = 0f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            lastDragPosition += dragAmount
                            totalDragDistance += dragAmount.y
                            scope.launch {
                                listState.scrollBy(-dragAmount.y * 1.5f)
                            }
                        }
                    )
                }
        ) {
            items(listScrollCount) { index ->
                Text(
                    text = getItem(index),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels.value = size.height }
                        .then(textModifier)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.offset(y = (itemHeightDp- minusToReglePosition.dp) * 1),
            color = dividerColor
        )

        HorizontalDivider(
            modifier = Modifier.offset(y = (itemHeightDp- minusToReglePosition.dp) * 2),
            color = dividerColor
        )
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }




@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}


@Composable
private fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    indexColor: Int = 0,
    reloadKey: Any = Unit
) {
    val context = LocalContext.current
    val viewModelImagesPath = viewModel.viewModelImagesPath

    val baseImagePath = remember(viewModelImagesPath, article.idArticle, indexColor) {
        File(viewModelImagesPath, "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}")
            .absolutePath
    }

    val imageExist by remember(baseImagePath, reloadKey) {
        mutableStateOf(
            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                val file = File("$baseImagePath.$extension")
                if (file.exists() && file.canRead()) {
                    file.absolutePath
                } else null
            }
        )
    }

    val imageSource = remember(imageExist) {
        imageExist?.let { File(it) } ?: R.drawable.logo
    }

    val requestKey = remember(article.idArticle, indexColor, reloadKey) {
        "${article.idArticle}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
    }

    Box(modifier = modifier.fillMaxWidth()) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageSource)
                .size(350,350)  // Use original size to maintain aspect ratio
                .crossfade(true)
                .setParameter("key", requestKey, memoryCacheKey = requestKey)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "Article image ${article.idArticle} color ${indexColor + 1}",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}



