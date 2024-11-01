package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ColorsArticlesTabelle
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: StartUpNewArticlesViewModels,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    modeFilterToTest:Boolean =true,
) {
    var gridColumnsForNewArticels by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModel.uiState.collectAsState()

    ArticleDisplayScreen(
        uiState = uiState,
        gridColumns = gridColumnsForNewArticels,
        showFilter = showFilter,
        filterText = filterText,
        gridState = gridState,
        onFilterTextChange = { filterText = it },
        onToggleFilter = { showFilter = !showFilter },
        onChangeGridColumns = { gridColumnsForNewArticels = it },
        onToggleNavBar = onToggleNavBar,
        viewModel = viewModel,
        reloadTrigger = reloadTrigger,
        modifier = modifier, onClickToOpenWindos = onClickToOpenWindos
        ,modeFilterToTest=modeFilterToTest
    )
}

@Composable
private fun SearchFilter(
    showFilter: Boolean,
    filterText: String,
    onFilterTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

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
                .padding(8.dp)
                .focusRequester(focusRequester),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
    }

    LaunchedEffect(showFilter) {
        if (showFilter) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}



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
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    modeFilterToTest: Boolean
) {
    val pagingConfig = PagingConfig(
        pageSize = 6,
        enablePlaceholders = false,
        prefetchDistance = 20
    )

    // Create separate pagers for each category
    val categoryPagers = remember(uiState.categories, filterText) {
        uiState.categories.associate { category ->
            category to Pager(pagingConfig) {
                ArticlePagingSource(
                    articles = if (category.nomCategorieInCategoriesTabele == "NewArrivale") {
                        uiState.articlesBasesStatTabelles.filter { it.itsNewArrivale }
                    } else {
                        uiState.articlesBasesStatTabelles.filter {
                            it.nomCategorie == category.nomCategorieInCategoriesTabele && !it.itsNewArrivale
                        }
                    },
                    filterText = filterText,
                    modeFilterToTest=modeFilterToTest
                )
            }
        }
    }

    // Create a map of category to their respective LazyPagingItems
    val categoryPagingItems = remember(categoryPagers) {
        mutableMapOf<CategoriesTabelle, LazyPagingItems<ArticlesBasesStatsTabelle>>()
    }

    // Collect paging items for each category
    categoryPagers.forEach { (category, pager) ->
        val pagingItems = pager.flow.collectAsLazyPagingItems()
        categoryPagingItems[category] = pagingItems
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(if (uiState.categories.any {
                it.nomCategorieInCategoriesTabele == "NewArrivale"
            }) gridColumns else 2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        if (!showFilter) {
            item(span = StaggeredGridItemSpan.FullLine) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }

        // Display categories in order, with NewArrivale first
        uiState.categories.sortedBy {
            if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1
        }.forEach { category ->
            val lazyPagingItems = categoryPagingItems[category]

            if (lazyPagingItems != null && lazyPagingItems.itemCount > 0) {
                // Show category header if needed
                if (category.displayedHeader) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        CategoryHeader(category)
                    }
                }

                // Display category items
                items(
                    count = lazyPagingItems.itemCount,
                    span = { index ->
                        val article = lazyPagingItems[index]
                        if (article?.imageDimention == "Demi") {
                            StaggeredGridItemSpan.FullLine
                        } else {
                            StaggeredGridItemSpan.SingleLane
                        }
                    }
                ) { index ->
                    val article = lazyPagingItems[index]
                    article?.let {
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

@Composable
private fun ArticleDisplayScreen(
    uiState: UiState,
    gridColumns: Int,
    showFilter: Boolean,
    filterText: String,
    gridState: LazyStaggeredGridState,
    onFilterTextChange: (String) -> Unit,
    onToggleFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    onToggleNavBar: () -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    modeFilterToTest: Boolean
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            SearchFilter(
                showFilter = showFilter,
                filterText = filterText,
                onFilterTextChange = onFilterTextChange
            )

            ArticleGrid(
                uiState = uiState,
                gridColumns = gridColumns,
                filterText = filterText,
                showFilter = showFilter,
                gridState = gridState,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger, onClickToOpenWindos = onClickToOpenWindos
                        ,modeFilterToTest=modeFilterToTest
            )
        }

        FloatingActionButtonGroup(
            onToggleNavBar = onToggleNavBar,
            onToggleOutlineFilter = onToggleFilter,
            onChangeGridColumns = onChangeGridColumns,
            viewModel = viewModel
        )

        if (uiState.isLoading) {
            LoadingOverlay(progress = uiState.loadingProgress)
        }
    }
}

class ArticlePagingSource(
    private val articles: List<ArticlesBasesStatsTabelle>,
    private val filterText: String,
    private val modeFilterToTest: Boolean
) : PagingSource<Int, ArticlesBasesStatsTabelle>() {
    override fun getRefreshKey(state: PagingState<Int, ArticlesBasesStatsTabelle>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun countArticleColors(article: ArticlesBasesStatsTabelle): Int {
        return listOf(
            article.couleur1,
            article.couleur2,
            article.couleur3,
            article.couleur4
        ).count { !it.isNullOrEmpty() }
    }

    private fun matchesLayoutCriteria(article: ArticlesBasesStatsTabelle): Boolean {
        val colorCount = countArticleColors(article)
        return when {
            // Empty imageDimension cases
            article.imageDimention == "" && colorCount == 1 -> true
            article.imageDimention == "" && colorCount == 2 -> true
            article.imageDimention == "" && colorCount == 3 -> true
            article.imageDimention == "" && colorCount == 4 -> true
            // Demi imageDimension cases
            article.imageDimention == "Demi" && colorCount == 1 -> true
            article.imageDimention == "Demi" && colorCount == 2 -> true
            article.imageDimention == "Demi" && colorCount == 3 -> true
            article.imageDimention == "Demi" && colorCount == 4 -> true
            else -> false
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTabelle> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        val filteredArticles = articles.filter { article ->
            when {
                !modeFilterToTest -> true // Show all articles when modeFilterToTest is false
                filterText.isEmpty() -> {
                    // First filter by layout criteria
                    matchesLayoutCriteria(article)
                }
                else -> {
                    // When there's filter text, combine all conditions
                    matchesLayoutCriteria(article) &&
                            article.nomArticleFinale.contains(filterText, ignoreCase = true)
                }
            }
        }

        val start = page * pageSize
        val items = filteredArticles.drop(start).take(pageSize)

        return LoadResult.Page(
            data = items,
            prevKey = if (page == 0) null else page - 1,
            nextKey = if (items.isEmpty()) null else page + 1
        )
    }
}

// Update the ArticleLayout sealed class to include all required layouts
sealed class ArticleLayout {
    data object SingleGrid : ArticleLayout() // Single item in grid
    data object SingleFull : ArticleLayout() // Single item full width
    data object DemiDual : ArticleLayout() // Half-width dual color
    data object DemiMulti : ArticleLayout() // Half-width multi color
    data object SmallDual : ArticleLayout() // Small dual color
    data object SmallMulti : ArticleLayout() // Small multi color

    @Composable
    fun Content(
        article: ArticlesBasesStatsTabelle,
        viewModel: StartUpNewArticlesViewModels,
        reloadTrigger: Int,
        onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
        uiState: UiState,
        modifier: Modifier = Modifier
    ) {
        when (this) {
            is SingleGrid -> SingleColorDisplayer(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                modifier.fillMaxWidth()
            )
            is SingleFull -> SingleColorDisplayer(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                modifier.fillMaxWidth()
            )
            is DemiDual -> DemiDisplayerDualColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState, modifier
            )
            is DemiMulti -> DemiDisplayerMultiColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState, modifier
            )
            is SmallDual -> SmallDisplayerDualColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState, modifier
            )
            is SmallMulti -> SmallDisplayerMultiColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState, modifier
            )
        }
    }
}

// Update the ArticleItem to use the new layout logic
@Composable
private fun ArticleItem(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    val colorCount = countColors(article)

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val layout = when {
            article.imageDimention == "Demi" && colorCount > 2 -> ArticleLayout.DemiMulti
            article.imageDimention == "Demi" && colorCount == 2 -> ArticleLayout.DemiDual
            colorCount > 2 -> ArticleLayout.SmallMulti
            colorCount == 2 -> ArticleLayout.SmallDual
            article.imageDimention == "Full" -> ArticleLayout.SingleFull
            else -> ArticleLayout.SingleGrid
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
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState
        )

        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 1,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.height(40.dp),
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState,
            contentScale = ContentScale.Crop
        )

        ArticleDetails(
            article = article,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun SmallDisplayerMultiColor(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
            items(availableColors) { index ->
                ArticleImageWithOverlay(
                    article = article,
                    viewModel = viewModel,
                    colorIndex = index,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    onClickToOpenWindow = onClickToOpenWindos,
                    uiState = uiState,
                    contentScale = ContentScale.Crop
                )
            }
        }

        ArticleDetails(
            article = article,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun DemiDisplayerDualColor(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        ArticleDetails(article)
        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState
        )

        Box(modifier = Modifier.height(40.dp)) {
            ArticleImageWithOverlay(
                article = article,
                viewModel = viewModel,
                colorIndex = 1,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun SingleColorDisplayer(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
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
                uiState = uiState
            )
        }
        ArticleDetails(article)
    }
}

// Utility functions
private fun checkImageExists(
    viewModel: StartUpNewArticlesViewModels,
    article: ArticlesBasesStatsTabelle,
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

private fun ArticlesBasesStatsTabelle.getColorIdForIndex(index: Int): Long? {
    return when (index) {
        0 -> idcolor1.takeIf { it != 0L }
        1 -> idcolor2.takeIf { it != 0L }
        2 -> idcolor3.takeIf { it != 0L }
        3 -> idcolor4.takeIf { it != 0L }
        else -> null
    }
}

private fun countColors(article: ArticlesBasesStatsTabelle): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }
}
@Composable
private fun ArticleImageWithOverlay(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    colorIndex: Int,
    reloadTrigger: Int,
    uiState: UiState,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    onClickToOpenWindow: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    Box(modifier = modifier) {
        val imageExists = remember(article.idArticle, colorIndex, reloadTrigger) {
            checkImageExists(viewModel, article, colorIndex, reloadTrigger)
        }

        ImageDisplayer(
            article = article,
            viewModel = viewModel,
            indexColor = colorIndex,
            reloadKey = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindow,
            uiState = uiState,
            showOverlay = !imageExists,
            imageScale = contentScale
        )

        if (imageExists) {
            article.getColorIdForIndex(colorIndex)?.let { colorId ->
                uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                    ColorIndicator(
                        iconColore = color.iconColore,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DemiDisplayerMultiColor(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        ArticleDetails(article)
        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
            items(availableColors) { index ->
                ArticleImageWithOverlay(
                    article = article,
                    viewModel = viewModel,
                    colorIndex = index,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier.width(250.dp),
                    onClickToOpenWindow = onClickToOpenWindos,
                    uiState = uiState
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ImageDisplayer(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    indexColor: Int,
    reloadKey: Any,
    onClickToOpenWindow: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    showOverlay: Boolean,
    imageScale: ContentScale = ContentScale.Fit,
    cornerRadius: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    var currentQuality by remember { mutableStateOf(25f) }
    val baseImagePath = remember(viewModel.viewModelImagesPath, article.idArticle, indexColor) {
        File(
            viewModel.viewModelImagesPath,
            "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"
        ).absolutePath
    }

    val imageExist by produceState<String?>(
        initialValue = null,
        key1 = baseImagePath,
        key2 = reloadKey
    ) {
        value = withContext(Dispatchers.IO) {
            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                val file = File("$baseImagePath.$extension")
                if (file.exists() && file.canRead()) file.absolutePath else null
            }
        }
    }

    Box(modifier = modifier) {
        GlideImage(
            model = imageExist?.let { File(it) },
            contentDescription = "Article image ${article.idArticle}",
            contentScale = imageScale,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .clickable { onClickToOpenWindow(article, indexColor) }
        ) {
            it.applyImageOptions(article, indexColor, currentQuality) {
                if (it && currentQuality < 100f) currentQuality = 100f
            }
        }

        if (showOverlay) {
            article.getColorIdForIndex(indexColor)?.let { colorId ->
                uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                    ColorOverlayWithBlur(color = color, cornerRadius = cornerRadius)
                }
            }
        }
    }
}

private fun RequestBuilder<Drawable>.applyImageOptions(
    article: ArticlesBasesStatsTabelle,
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorOverlayWithBlur(
    color: ColorsArticlesTabelle,
    cornerRadius: Dp,
    modifier: Modifier = Modifier
) {
    Box {
        GlideImage(
            model = R.drawable.baked_goods_1,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
            contentDescription = null
        ) {
            it.transform(jp.wasabeef.glide.transformations.BlurTransformation(25))
        }

        ColorOverlay(
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}


@Composable
private fun ColorOverlay(
    color: ColorsArticlesTabelle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AutoResizedTextClas(
                text = color.iconColore,
                modifier = modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                color = Color.White
            )

            AutoResizedTextClas(
                text = color.nameColore,
                modifier = modifier
                    .fillMaxWidth()
                    .weight(0.2f),
                color = Color.White
            )
        }
    }
}

@Composable
private fun ColorIndicator(
    iconColore: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = iconColore,
            modifier = Modifier.padding(4.dp)
        )
    }
}


@Composable
private fun ArticleDetails(
    article: ArticlesBasesStatsTabelle,
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
fun AutoResizedTextClas(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = Int.MAX_VALUE
) {
    val initialFontSize = MaterialTheme.typography.bodyMedium.fontSize
    var fontSize by remember { mutableStateOf(initialFontSize) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowHeight) {
                fontSize *= 0.9f
            } else {
                readyToDraw = true
            }
        }
    )
}



