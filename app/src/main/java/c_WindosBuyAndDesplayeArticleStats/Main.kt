package c_WindosBuyAndDesplayeArticleStats

import a_RoomDB.ArticlesBasesStats
import a_RoomDB.ColorsArticles
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import b_StartupAppDisplayerOfNewArticles.UiState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.R
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File

@Composable
fun WindosBuyAndDesplayeArticleStats(
    uiState:UiState,
    article: ArticlesBasesStats,
    viewModel: StartUpNewArticlesViewModels,
    onDismiss: () -> Unit,
    onReloadTrigger: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.large
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ColorsCards(
                            uiState = uiState,
                            article = article,
                            viewModel = viewModel,
                            onDismiss = onDismiss,
                            onReloadTrigger = onReloadTrigger,
                            relodeTigger = reloadTrigger
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorsCards(
    article: ArticlesBasesStats,
    viewModel: StartUpNewArticlesViewModels,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onReloadTrigger: () -> Unit,
    relodeTigger: Int,
    uiState: UiState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Find corresponding colors from uiState.colors
        listOf(
            article.idcolor1,
            article.idcolor2,
            article.idcolor3,
            article.idcolor4
        ).forEachIndexed { index, colorId ->
            val correspondingColor = uiState.colorsArticlesModel.find { it.idColore == colorId }
            if (colorId != 0L && correspondingColor != null) {
                ColorItem(
                    article = article,
                    color = correspondingColor,
                    index = index,
                    relodeTigger = relodeTigger,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    article: ArticlesBasesStats,
    color: ColorsArticles?,
    index: Int,
    relodeTigger: Int,
    viewModel: StartUpNewArticlesViewModels,
) {
    var showPicker by remember { mutableStateOf(false) }
    var selectedQuantity by remember { mutableStateOf(1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image section
            Box(
                modifier = Modifier
                    .weight(0.6f)  // ~150dp pour une largeur totale de 250dp
                    .fillMaxHeight()
            ) {
                ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = index,
                    reloadKey = relodeTigger
                )

                color?.let { colorData ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = colorData.nameColore,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Picker section
            Box(
                modifier = Modifier
                    .weight(0.4f)  // ~100dp
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quantité",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val values = remember {
                            (1..15).map { it.toString() } +
                                    (20..25).map { it.toString() } +
                                    listOf("30", "40", "50")
                        }
                        val pickerState = rememberPickerState()

                        CompactPicker(
                            state = pickerState,
                            items = values,
                            visibleItemsCount = 3,
                            textModifier = Modifier.padding(vertical = 4.dp),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            ),
                            dividerColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = {
                            // Traiter la quantité sélectionnée
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Ajouter au panier")
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactPicker(
    items: List<String>,
    state: PickerState,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.size * 100 + visibleItemsMiddle
    )

    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeightPixels = remember { mutableStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.value)

    LaunchedEffect(listState) {
        snapshotFlow {
            val index = listState.firstVisibleItemIndex + visibleItemsMiddle
            items[index % items.size]
        }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items.size * 200) { index ->
                val item = items[index % items.size]
                Text(
                    text = item,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size -> itemHeightPixels.value = size.height }
                        .then(textModifier),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Selection indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .offset(y = itemHeightDp * visibleItemsMiddle)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
        )

        // Dividers
        HorizontalDivider(
            modifier = Modifier.offset(y = itemHeightDp * visibleItemsMiddle),
            color = dividerColor
        )

        HorizontalDivider(
            modifier = Modifier.offset(y = itemHeightDp * (visibleItemsMiddle + 1)),
            color = dividerColor
        )
    }
}

class PickerState {
    var selectedItem by mutableStateOf("1")
}

@Composable
fun rememberPickerState() = remember { PickerState() }



@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }




@Composable
private fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStats,
    viewModel: StartUpNewArticlesViewModels,
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
        imageExist?.let { File(it) } ?: R.drawable.baked_goods_1
    }

    val requestKey = remember(article.idArticle, indexColor, reloadKey) {
        "${article.idArticle}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
    }

    Box(modifier = modifier.fillMaxWidth()) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageSource)
                .size(Size.ORIGINAL)  // Use original size to maintain aspect ratio
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



