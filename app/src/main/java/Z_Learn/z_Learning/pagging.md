```kotlin
package b_StartupAppDisplayerOfNewArticles

// Core Compose imports

// Coil imports for image loading

// Paging imports

// Coroutines

// Android specific

// Custom imports (your model classes)

// Optional - for logging

// Extensions pour Coil (facultatif mais recommandé)

// Pour la gestion des événements du cycle de vie

// Pour les animations et transitions

// Pour les mesures de performances
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.clientjetpack.LoadingOverlay
import com.example.clientjetpack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

@Composable
fun StartupAppDisplayerOfNewArticles(
viewModel: StartUpNewArticlesViewModels,
onToggleNavBar: () -> Unit,
reloadTrigger: Int,
modifier: Modifier = Modifier,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
modeFilterToTest: Boolean = true,
onClickToOpenClientsW: () -> Unit,
isFabVisible: Boolean, onClickDonne: () -> Unit,
) {
var gridColumnsForNewArticels by remember { mutableStateOf(2) }
var showFilter by remember { mutableStateOf(false) }
var filterText by remember { mutableStateOf("") }
val gridState = rememberLazyStaggeredGridState()
val uiState by viewModel.uiState.collectAsState()

    ArticleDisplayScreen(
        uiState = uiState,
        gridColumns = gridColumnsForNewArticels,
        filterText = filterText,
        gridState = gridState,
        onFilterTextChange = { filterText = it },
        onToggleFilter = { showFilter = !showFilter },
        onChangeGridColumns = { gridColumnsForNewArticels = it },
        onToggleNavBar = onToggleNavBar,
        viewModel = viewModel,
        reloadTrigger = reloadTrigger,
        modifier = modifier,
        onClickToOpenWindos = onClickToOpenWindos,
        modeFilterToTest=modeFilterToTest,
        onClickToOpenClientsW = onClickToOpenClientsW,
        isFabVisible=isFabVisible, onClickDonne = onClickDonne
    )
}

// First, let's add the missing utility functions
private fun getImagePath(article: ArticlesBasesStatsTable, colorIndex: Int, viewModel: StartUpNewArticlesViewModels): String {
val baseFileName = "${article.idArticle}_${if (colorIndex == -1) "Unite" else (colorIndex + 1)}"
val baseImagePath = File(viewModel.viewModelImagesPath, baseFileName).absolutePath

    // Try both jpg and webp extensions
    return listOf("jpg", "webp")
        .asSequence()
        .map { "$baseImagePath.$it" }
        .firstOrNull { path -> File(path).exists() && File(path).canRead() }
        ?: baseImagePath // Return base path if no file exists
}

private fun ArticlesBasesStatsTable.getAvailableColorIndices(): List<Int> {
return listOf(
Triple(0, idcolor1, couleur1),
Triple(1, idcolor2, couleur2),
Triple(2, idcolor3, couleur3),
Triple(3, idcolor4, couleur4)
).filter { (_, id, name) -> id != 0L && !name.isNullOrEmpty() }
.map { it.first }
}

// Fix the ArticlePagingSource class by implementing missing methods and functions
class ArticlePagingSource(
private val articles: List<ArticlesBasesStatsTable>,
private val filterText: String,
private val context: Context,
private val viewModel: StartUpNewArticlesViewModels
) : PagingSource<Int, ArticlesBasesStatsTable>() {
private val pageSize = 10
private val preloadDistance = 2
private val imageLoader = ArticleImageLoader.getInstance(context)

    override fun getRefreshKey(state: PagingState<Int, ArticlesBasesStatsTable>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun filterAndGetPage(page: Int): List<ArticlesBasesStatsTable> {
        return articles
            .filter { article ->
                filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true)
            }
            .drop(page * pageSize)
            .take(pageSize)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTable> {
        val page = params.key ?: 0

        return try {
            val filteredArticles = filterAndGetPage(page)

            // Preload images for next page
            coroutineScope {
                launch(Dispatchers.IO) {
                    preloadImagesForPage(page + 1)
                }
            }

            LoadResult.Page(
                data = filteredArticles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (filteredArticles.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun preloadImagesForPage(page: Int) {
        val nextPageArticles = filterAndGetPage(page)
        nextPageArticles.forEach { article ->
            preloadArticleImages(article)
        }
    }

    private suspend fun preloadArticleImages(article: ArticlesBasesStatsTable) {
        article.getAvailableColorIndices().forEach { colorIndex ->
            val imagePath = getImagePath(article, colorIndex, viewModel)
            imageLoader.enqueue(
                ImageRequest.Builder(context)
                    .data(imagePath)
                    .build()
            )
        }
    }
}

// Fix ArticleImageWithOverlay by adding missing roundToPx extension
private fun Dp.roundToPx(): Int {
return value.roundToInt()
}

// Updated ArticleImageWithOverlay with fixed references
@Composable
private fun ArticleImageWithOverlay(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
colorIndex: Int,
reloadTrigger: Int,
uiState: UiState,
modifier: Modifier = Modifier,
contentScale: ContentScale = ContentScale.Fit,
onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit,
imageSize: DpSize
) {
val scope = rememberCoroutineScope()
val context = LocalContext.current
val lifecycle = LocalLifecycleOwner.current.lifecycle
val imageLoader = remember { ArticleImageLoader.getInstance(context) }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch(Dispatchers.IO) {
                imageLoader.memoryCache?.clear()
            }
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable { onClickToOpenWindow(article, colorIndex) }
                .fillMaxSize()
        ) {
            val imagePath = remember(article.idArticle, colorIndex) {
                getImagePath(article, colorIndex, viewModel)
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imagePath)
                    .crossfade(true)
                    .size(imageSize.width.roundToPx(), imageSize.height.roundToPx())
                    .memoryCacheKey("${article.idArticle}_${colorIndex}")
                    .diskCacheKey("${article.idArticle}_${colorIndex}")
                    .build(),
                contentDescription = "Article image ${article.idArticle}",
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}
@Composable
fun ArticleDisplayScreen(
uiState: UiState,
gridColumns: Int,
filterText: String,
gridState: LazyStaggeredGridState,
onFilterTextChange: (String) -> Unit,
onToggleFilter: () -> Unit,
onChangeGridColumns: (Int) -> Unit,
onToggleNavBar: () -> Unit,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
modifier: Modifier = Modifier,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
modeFilterToTest: Boolean = false,
onClickToOpenClientsW: () -> Unit,
isFabVisible: Boolean,
onClickDonne: () -> Unit,
) {
Box(
modifier = Modifier.fillMaxSize()
) {
// Main content in a Box to constrain its height
Box(
modifier = Modifier
.fillMaxSize()
.padding(bottom = if (isFabVisible) 80.dp else 0.dp) // Add padding at bottom when FAB is visible
) {
Column {
SearchFilter(
showFilter = isFabVisible,
filterText = filterText,
onFilterTextChange = onFilterTextChange,
onAddNotInBaseArticle = onClickToOpenWindos,
viewModel = viewModel,
uiState = uiState,
onClickDonne = onClickDonne
)

                ArticleGrid(
                    uiState = uiState,
                    gridColumns = gridColumns,
                    filterText = filterText,
                    showFilter = isFabVisible,
                    gridState = gridState,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos,
                    modeFilterToTest = modeFilterToTest
                )
            }
        }


        // FAB positioned at the bottom
        AnimatedVisibility(
            visible = isFabVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButtonGroup(
                onToggleNavBar = onToggleNavBar,
                onToggleOutlineFilter = onToggleFilter,
                onChangeGridColumns = onChangeGridColumns,
                onClickToOpenClientsListW = onClickToOpenClientsW,
                viewModel = viewModel,
                modifier = Modifier
            )
        }

        // Loading overlay
        if (uiState.isLoading) {
            LoadingOverlay(
                progress = uiState.loadingProgress,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
// 1. Implement a custom ImageLoader configuration
object ArticleImageLoader {
private var imageLoader: ImageLoader? = null

    fun getInstance(context: Context): ImageLoader {
        if (imageLoader == null) {
            imageLoader = ImageLoader.Builder(context)
                .memoryCache {
                    MemoryCache.Builder(context)
                        .maxSizePercent(0.25) // Use 25% of app memory
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve("image_cache"))
                        .maxSizeBytes(512L * 1024 * 1024) // 512MB
                        .build()
                }
                .crossfade(true)
                .build()
        }
        return imageLoader!!
    }
}



// 4. Implement efficient grid layout with improved recycling
@Composable
private fun ArticleGrid(
uiState: UiState,
gridColumns: Int,
filterText: String,
showFilter: Boolean,
gridState: LazyStaggeredGridState,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
modifier: Modifier = Modifier,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
modeFilterToTest: Boolean
) {
val context = LocalContext.current
val scope = rememberCoroutineScope()

    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density
    val itemsPerRow = if (screenWidth > 600) gridColumns else 2
    val pageSize = itemsPerRow * 4

    val pagingConfig = remember {
        PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false,
            prefetchDistance = pageSize,
            initialLoadSize = pageSize * 2,
            maxSize = pageSize * 5
        )
    }

    // Move paging data creation outside of remember to properly collect the Flow
    val categories = uiState.categories
    val pagers = categories.map { category ->
        category to remember(category, filterText) {
            Pager(pagingConfig) {
                ArticlePagingSource(
                    articles = uiState.articlesBasesStatTables.filter {
                        when {
                            category.nomCategorieInCategoriesTabele == "NewArrivale" -> it.itsNewArrivale
                            else -> it.nomCategorie == category.nomCategorieInCategoriesTabele && !it.itsNewArrivale
                        }
                    },
                    filterText = filterText,
                    context = context,
                    viewModel = viewModel
                )
            }
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(itemsPerRow),
        state = gridState,
        contentPadding = PaddingValues(3.dp),
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Article Grid" },
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalItemSpacing = 3.dp
    ) {
        pagers.forEach { (category, pager) {
            val pagingItems = pager.flow.collectAsLazyPagingItems()

            if (pagingItems.itemCount > 0) {
                if (category.displayedHeader) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        CategoryHeader(category)
                    }
                }

                items(
                    count = pagingItems.itemCount,
                    key = { index -> "${category.nomCategorieInCategoriesTabele}_${pagingItems[index]?.idArticle}" },
                    span = { index ->
                        val article = pagingItems[index]
                        if (article?.imageDimention == "Demi") {
                            StaggeredGridItemSpan.FullLine
                        } else {
                            StaggeredGridItemSpan.SingleLane
                        }
                    }
                ) { index ->
                    val article = pagingItems[index]
                    article?.let {
                        key(it.idArticle) {
                            ArticleItem(
                                article = it,
                                viewModel = viewModel,
                                reloadTrigger = reloadTrigger,
                                onClickToOpenWindos = onClickToOpenWindos,
                                uiState = uiState
                            )
                        }
                    }
                }
            }
        }
        }
    }




sealed class ArticleLayout {
data object DemiUno : ArticleLayout()
data object DemiDual : ArticleLayout()
data object DemiMulti : ArticleLayout()
data object SmallUno : ArticleLayout()
data object SmallDual : ArticleLayout()
data object SmallMulti : ArticleLayout()

    // Define size configurations for different layouts
    private val imageSize: DpSize
        get() = when (this) {
            is DemiUno,DemiDual, is DemiMulti -> DpSize(width = 500.dp, height = 500.dp)
            is SmallUno, is SmallDual, is SmallMulti -> DpSize(width = 170.dp, height = 170.dp)
        }

    @Composable
    fun Content(
        article: ArticlesBasesStatsTable,
        viewModel: StartUpNewArticlesViewModels,
        reloadTrigger: Int,
        onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
        uiState: UiState,
        modifier: Modifier = Modifier,
    ) {
        when (this) {
            is DemiUno -> SmallSingleColorDisplayer(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier
            )
            is DemiDual -> DemiDisplayerDualColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier
            )
            is DemiMulti -> DemiDisplayerMultiColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier
            )
            is SmallUno -> DemiSingleColorDisplayer(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier
            )
            is SmallDual -> SmallDisplayerDualColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier
            )
            is SmallMulti -> SmallDisplayerMultiColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier
            )
        }
    }
}

// Update the ArticleItem to use the new layout logic
@Composable
private fun ArticleItem(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
modifier: Modifier = Modifier,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
) {
val colorCount = countColors(article)

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val layout = when {
            article.imageDimention == "Demi" && colorCount == 1 -> ArticleLayout.DemiUno
            article.imageDimention == "Demi" && colorCount == 2 -> ArticleLayout.DemiDual
            article.imageDimention == "Demi" && colorCount > 2 -> ArticleLayout.DemiMulti
            colorCount == 1 -> ArticleLayout.SmallUno
            colorCount == 2 -> ArticleLayout.SmallDual
            colorCount > 2 -> ArticleLayout.SmallMulti
            else -> ArticleLayout.SmallUno
        }

        layout.Content(
            article = article,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos,
            uiState = uiState
        )
    }
}

// Add new layout components
@Composable
private fun SmallDisplayerDualColor(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
modifier: Modifier = Modifier, imageSize: DpSize
) {
Column(
modifier = modifier.padding(3.dp),
verticalArrangement = Arrangement.spacedBy(3.dp)
) {
ArticleImageWithOverlay(
article = article,
viewModel = viewModel,
colorIndex = 0,
reloadTrigger = reloadTrigger,
onClickToOpenWindow = onClickToOpenWindos,
uiState = uiState, imageSize = imageSize
)

        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 1,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.height(100.dp),
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState,
            contentScale = ContentScale.Crop, imageSize = imageSize
        )

        ArticleDetails(
            article = article,
            modifier = Modifier.padding(horizontal = 3.dp)
        )
    }
}

@Composable
private fun SmallDisplayerMultiColor(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
modifier: Modifier = Modifier,
imageSize: DpSize
) {
Column(
modifier = modifier.padding(3.dp),
verticalArrangement = Arrangement.spacedBy(3.dp)
) {
// Main image
ArticleImageWithOverlay(
article = article,
viewModel = viewModel,
colorIndex = 0,
reloadTrigger = reloadTrigger,
onClickToOpenWindow = onClickToOpenWindos,
uiState = uiState, imageSize = imageSize
)

        // Replace LazyColumn with Column since we have a small fixed number of items
        val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
        availableColors.forEach { index ->
            ArticleImageWithOverlay(
                article = article,
                viewModel = viewModel,
                colorIndex = index,
                reloadTrigger = reloadTrigger,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState,
                contentScale = ContentScale.Crop, imageSize = imageSize
            )
        }

        // Details
        ArticleDetails(
            article = article,
            modifier = Modifier.padding(horizontal = 3.dp)
        )
    }
}

@Composable
private fun DemiDisplayerMultiColor(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
modifier: Modifier = Modifier, imageSize: DpSize
) {
Column(modifier = modifier.padding(3.dp)) {
ArticleDetails(article)

        // Main image display
        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState, imageSize = imageSize
        )

        // Secondary images row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
            items(availableColors) { index ->
                val imageExists = remember(article.idArticle, index, reloadTrigger) {
                    checkImageExists(viewModel, article, index, reloadTrigger)
                }

                ArticleImageWithOverlay(
                    article = article,
                    viewModel = viewModel,
                    colorIndex = index,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier
                        .width(250.dp)
                        .height(if (!imageExists) 70.dp else 250.dp),
                    contentScale = if (!imageExists) ContentScale.Crop else ContentScale.Fit,
                    onClickToOpenWindow = onClickToOpenWindos,
                    uiState = uiState, imageSize = imageSize
                )
            }
        }
    }
}

@Composable
private fun DemiDisplayerDualColor(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
modifier: Modifier = Modifier, imageSize: DpSize
) {
Column(modifier = modifier.padding(3.dp)) {
ArticleDetails(article)
ArticleImageWithOverlay(
article = article,
viewModel = viewModel,
colorIndex = 0,
reloadTrigger = reloadTrigger,
onClickToOpenWindow = onClickToOpenWindos,
uiState = uiState, imageSize = imageSize
)

        Box(modifier = Modifier.height(100.dp)) {
            ArticleImageWithOverlay(
                article = article,
                viewModel = viewModel,
                colorIndex = 1,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState,
                contentScale = ContentScale.Crop, imageSize = imageSize
            )
        }
    }
}
@Composable
private fun SmallSingleColorDisplayer(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
modifier: Modifier = Modifier, imageSize: DpSize
) {
Column(modifier = modifier.padding(3.dp)) {
Box(
modifier = Modifier,
contentAlignment = Alignment.Center
) {
ArticleImageWithOverlay(
article = article,
viewModel = viewModel,
colorIndex = 0,
reloadTrigger = reloadTrigger,
onClickToOpenWindow = onClickToOpenWindos,
uiState = uiState, imageSize = imageSize
)
}
ArticleDetails(article)
}
}
@Composable
private fun DemiSingleColorDisplayer(
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
reloadTrigger: Int,
onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
modifier: Modifier = Modifier, imageSize: DpSize
) {
Column(modifier = modifier.padding(3.dp)) {
Box(
modifier = Modifier,
contentAlignment = Alignment.Center
) {
ArticleImageWithOverlay(
article = article,
viewModel = viewModel,
colorIndex = 0,
reloadTrigger = reloadTrigger,
onClickToOpenWindow = onClickToOpenWindos,
uiState = uiState, imageSize = imageSize
)
}
ArticleDetails(article)
}
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorIndicator(
iconColore: String,
modifier: Modifier = Modifier,
onClickToOpenWindow: () -> Unit,
imageSize: DpSize,

    ) {
    val demiSizeImage = imageSize.width>200.dp
    Box(modifier = modifier.clickable { onClickToOpenWindow() }) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            if (iconColore == "©" || iconColore == "💯"|| iconColore == "") {
                GlideImage(
                    model = R.drawable.logo,
                    contentDescription = "Logo",
                    modifier = Modifier.size(
                       if (demiSizeImage) 70.dp else 38.dp
                    )
                )
            } else {
                Text(
                    text = iconColore,
                    fontSize =  if (demiSizeImage) 45.sp else 38.sp,
                    fontWeight = FontWeight.Bold ,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (14).dp, y = 18.dp)
                .size(if (demiSizeImage) 70.dp else 50.dp)
                .clickable { onClickToOpenWindow() }
        ) {
            GlideImage(
                model =  R.drawable.hand ,
                contentDescription = "Click indicator",
                contentScale = ContentScale.Fit
            )
        }
    }
}



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ImageDisplayer(
modifier: Modifier = Modifier,
article: ArticlesBasesStatsTable,
viewModel: StartUpNewArticlesViewModels,
indexColor: Int,
reloadKey: Any,
onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit,
uiState: UiState,
showOverlay: Boolean,
imageScale: ContentScale = ContentScale.Fit,
cornerRadius: Dp = 4.dp,
imageSize: DpSize,
) {
var currentQuality by remember { mutableStateOf(5f) }
var isLoading by remember { mutableStateOf(true) }
var imageLoaded by remember { mutableStateOf(false) }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    LaunchedEffect(reloadKey) {
        isLoading = true
        imageLoaded = false
        currentQuality = 5f

        delay(300) // Initial loading delay
        currentQuality = 100f
        imageLoaded = true

        delay(700) // Keep blur for 700ms after image loads
        isLoading = false
    }

    val imagePath by remember(viewModel.viewModelImagesPath, article.idArticle, indexColor) {
        derivedStateOf {
            val baseFileName = "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"
            File(viewModel.viewModelImagesPath, baseFileName)
        }
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        key1 = imagePath,
        key2 = reloadKey
    ) {
        value = withContext(Dispatchers.IO) {
            listOf("jpg", "webp")
                .asSequence()
                .map { ext -> File("${imagePath.absolutePath}.$ext") }
                .firstOrNull { it.exists() && it.canRead() }
        }
    }

    Box(modifier = modifier.size(width = imageSize.width, height = imageSize.height)) {
        imageFile?.let { file ->
            GlideImage(
                model = file,
                contentDescription = "Article image ${article.idArticle}",
                contentScale = imageScale,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .graphicsLayer {
                        if (blurRadius > 0f) {
                            renderEffect = BlurEffect(
                                radiusX = blurRadius,
                                radiusY = blurRadius,
                                edgeTreatment = TileMode.Decal
                            )
                        }
                    }
            ) {
                it.apply {
                    applyImageOptions(article, indexColor, currentQuality) { isFirstResource ->
                        if (isFirstResource && currentQuality < 100f) {
                            currentQuality = 100f
                        }
                    }
                }
            }
        }

        if (showOverlay) {
            article.getColorIdForIndex(indexColor)?.let { colorId ->
                uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                    ColorOverlayWithBlur(
                        color = color,
                        cornerRadius = cornerRadius,
                        onClickToOpenWindow = { onClickToOpenWindow(article, indexColor) }
                    )
                }
            }
        }
    }
}

// Also update ColorOverlayWithBlur to use the same blur technique
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorOverlayWithBlur(
color: ColorsArticlesTabelle,
cornerRadius: Dp,
onClickToOpenWindow: () -> Unit,
) {
Box {
GlideImage(
model = R.drawable.logo,
contentScale = ContentScale.Crop,
modifier = Modifier
.fillMaxSize()
.clip(RoundedCornerShape(cornerRadius))
.graphicsLayer {
renderEffect = BlurEffect(
radiusX = 25f,
radiusY = 25f,
edgeTreatment = TileMode.Decal
)
},
contentDescription = null
)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.Black.copy(alpha = 0.4f))
        )

        ColorOverlay(
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
            onClickToOpenWindow = onClickToOpenWindow
        )
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorOverlay(
color: ColorsArticlesTabelle,
modifier: Modifier = Modifier,
onClickToOpenWindow: () -> Unit,
) {
Box(
modifier = modifier
.fillMaxSize(),
contentAlignment = Alignment.Center
) {
Row(
modifier = Modifier
.fillMaxWidth()
.height(IntrinsicSize.Min),
verticalAlignment = Alignment.CenterVertically
) {
// Color name with circular background
Box(
modifier = Modifier
.weight(0.6f)
.wrapContentHeight(),
contentAlignment = Alignment.Center
) {
Surface(
modifier = Modifier
.matchParentSize(),
shape = CircleShape,
color = Color.White.copy(alpha = 0.7f),
border = BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
) {}

                AutoResizedText(
                    text = color.nameColore,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onClickToOpenWindow() },
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
                Text(
                    text = color.iconColore,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold ,
                    modifier = Modifier.clickable { onClickToOpenWindow() },
                    color = Color.White,
                    maxLines = 1
                )
                // Fixed hand icon positioning
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (14).dp, y = 18.dp)
                        .size(60.dp)
                        .clickable { onClickToOpenWindow() }
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

// Utility functions
private fun checkImageExists(
viewModel: StartUpNewArticlesViewModels,
article: ArticlesBasesStatsTable,
colorIndex: Int,
reloadTrigger: Int
): Boolean {
val baseImagePath = File(
viewModel.viewModelImagesPath,
"${article.idArticle}_${if (colorIndex == -1) "Unite" else (colorIndex + 1)}"
).absolutePath

    return listOf("jpg", "webp").any { extension ->
        val file = File("$baseImagePath.$extension")
        file.exists() && file.canRead()
    }
}
private fun ArticlesBasesStatsTable.getColorIdForIndex(index: Int): Long? {
return when (index) {
0 -> idcolor1.takeIf { it != 0L }
1 -> idcolor2.takeIf { it != 0L }
2 -> idcolor3.takeIf { it != 0L }
3 -> idcolor4.takeIf { it != 0L }
else -> null
}
}

private fun countColors(article: ArticlesBasesStatsTable): Int {
return listOf(
article.couleur1,
article.couleur2,
article.couleur3,
article.couleur4
).count { !it.isNullOrEmpty() }
}

@Composable
fun AutoResizedText(
text: String,
modifier: Modifier = Modifier,
color: Color = MaterialTheme.colorScheme.onSurface,
style: TextStyle = MaterialTheme.typography.headlineMedium,
maxLines: Int = Int.MAX_VALUE
) {
var fontSize by remember(text) {
mutableStateOf(style.fontSize)
}

    var previousFontSize by remember {
        mutableStateOf(fontSize)
    }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                previousFontSize = fontSize
                fontSize *= 0.9f
            } else if (fontSize != previousFontSize) {
                previousFontSize = fontSize
            }
        }
    )
}





private fun RequestBuilder<Drawable>.applyImageOptions(
article: ArticlesBasesStatsTable,
indexColor: Int,
quality: Float,
onResourceReady: (Boolean) -> Unit
) = this
.thumbnail(
this.clone()
.transform(jp.wasabeef.glide.transformations.BlurTransformation(10))
)
.transition(DrawableTransitionOptions.withCrossFade())
.diskCacheStrategy(DiskCacheStrategy.ALL)
.priority(Priority.HIGH)
.signature(ObjectKey("${article.idArticle}_${indexColor}_${quality}"))
.listener(object : RequestListener<Drawable> {
override fun onLoadFailed(
e: GlideException?,
model: Any?,
target: Target<Drawable>,
isFirstResource: Boolean
) = false

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            onResourceReady(isFirstResource)
            return false
        }
    })





@Composable
private fun ArticleDetails(
article: ArticlesBasesStatsTable,
modifier: Modifier = Modifier
) {
Column(
modifier = modifier,
verticalArrangement = Arrangement.spacedBy(4.dp)
) {
Text(
text = article.nomArticleFinale,
style = MaterialTheme.typography.titleMedium
)
Text(
text = "Prix: ${article.monPrixVent}",
style = MaterialTheme.typography.bodyMedium
)
}
}
@Composable
private fun SearchFilter(
showFilter: Boolean,
filterText: String,
onFilterTextChange: (String) -> Unit,
onAddNotInBaseArticle: (ArticlesBasesStatsTable, Int) -> Unit,
modifier: Modifier = Modifier,
viewModel: StartUpNewArticlesViewModels,
uiState: UiState,
onClickDonne: () -> Unit
) {
val keyboardController = LocalSoftwareKeyboardController.current
val focusRequester = remember { FocusRequester() }
val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = showFilter,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        OutlinedTextField(
            value = filterText,
            onValueChange = onFilterTextChange,
            label = { Text("Filter Articles") },
            modifier = modifier
                .fillMaxWidth()
                .padding(3.dp)
                .focusRequester(focusRequester),
            leadingIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            viewModel.addNewEmptyArticle(filterText)?.let { newArticle ->
                                onAddNotInBaseArticle(newArticle, 0)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add New Article")
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            viewModel.addNewEmptyArticle(filterText)?.let { newArticle ->
                                onAddNotInBaseArticle(newArticle, 0)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onClickDonne()
                }
            )
        )
    }

    LaunchedEffect(showFilter) {
        if (showFilter) {
            focusRequester.requestFocus()
            onFilterTextChange("")
            keyboardController?.show()
        }
    }
}
                   ```
