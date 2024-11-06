package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ColorsArticlesTabelle
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    isFabVisible: Boolean, onClickDonne: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchFilter(
                showFilter = isFabVisible,
                filterText = filterText,
                onFilterTextChange = onFilterTextChange,
                onAddNotInBaseArticle=onClickToOpenWindos,
                viewModel = viewModel,
                uiState=uiState,
                onClickDonne  = onClickDonne
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

        // Positionnement en bas à droite avec un léger espace (16.dp) depuis le bord
        AnimatedVisibility(
            visible = isFabVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButtonGroup(
                onToggleNavBar = onToggleNavBar,
                onToggleOutlineFilter = onToggleFilter,
                onChangeGridColumns = onChangeGridColumns,
                onClickToOpenClientsListW = onClickToOpenClientsW,
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)  // Ajout d'un padding pour un espace depuis le bord
            )
        }

        if (uiState.isLoading) {
            LoadingOverlay(progress = uiState.loadingProgress)
        }
    }
}


class ArticlePagingSource(
    private val articles: List<ArticlesBasesStatsTable>,
    private val filterText: String,
    private val modeFilterToTest: Boolean
) : PagingSource<Int, ArticlesBasesStatsTable>() {

    // Configuration class to make the criteria clearer
    private data class ArticleConfiguration(
        val imageDimension: String,
        val colorCount: Int,
        val description: String
    )

    // Define all 8 required configurations
    private val requiredConfigurations = listOf(
        ArticleConfiguration("", 1, "Empty dimension with 1 color"),
        ArticleConfiguration("", 2, "Empty dimension with 2 colors"),
        ArticleConfiguration("", 3, "Empty dimension with 3 colors"),
        ArticleConfiguration("", 4, "Empty dimension with 4 colors"),
        ArticleConfiguration("Demi", 1, "Demi dimension with 1 color"),
        ArticleConfiguration("Demi", 2, "Demi dimension with 2 colors"),
        ArticleConfiguration("Demi", 3, "Demi dimension with 3 colors"),
        ArticleConfiguration("Demi", 4, "Demi dimension with 4 colors")
    )

    override fun getRefreshKey(state: PagingState<Int, ArticlesBasesStatsTable>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun countArticleColors(article: ArticlesBasesStatsTable): Int {
        return listOf(
            article.couleur1,
            article.couleur2,
            article.couleur3,
            article.couleur4
        ).count { !it.isNullOrEmpty() }
    }

    private fun findArticleForConfiguration(
        config: ArticleConfiguration,
        excludedIds: Set<Int>
    ): ArticlesBasesStatsTable? {
        return articles.firstOrNull { article ->
            article.idArticle !in excludedIds &&
                    article.imageDimention == config.imageDimension &&
                    countArticleColors(article) == config.colorCount &&
                    (filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true))
        }
    }

    private fun findAllConfigurationMatches(): List<ArticlesBasesStatsTable> {
        val matchingArticles = mutableListOf<ArticlesBasesStatsTable>()
        val usedIds = mutableSetOf<Int>()
        var missingConfigurations = mutableListOf<ArticleConfiguration>()

        // Try to find one article for each configuration
        for (config in requiredConfigurations) {
            val match = findArticleForConfiguration(config, usedIds)
            if (match != null) {
                matchingArticles.add(match)
                usedIds.add(match.idArticle)
            } else {
                missingConfigurations.add(config)
            }
        }

        // Log missing configurations for debugging if needed
        if (missingConfigurations.isNotEmpty()) {
            println("Warning: Could not find articles for the following configurations:")
            missingConfigurations.forEach { config ->
                println("- ${config.description}")
            }
        }

        return matchingArticles
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTable> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        val filteredArticles = when {
            !modeFilterToTest -> articles // Show all articles when modeFilterToTest is false
            else -> findAllConfigurationMatches() // Find exactly one article per configuration
        }

        val start = page * pageSize
        val items = filteredArticles.drop(start).take(pageSize)

        // For debugging: verify we have the expected number of articles
        if (modeFilterToTest && filteredArticles.size != requiredConfigurations.size) {
            println("Warning: Found ${filteredArticles.size} articles instead of expected ${requiredConfigurations.size}")
        }

        return LoadResult.Page(
            data = items,
            prevKey = if (page == 0) null else page - 1,
            nextKey = if (items.isEmpty()) null else page + 1
        )
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
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    modeFilterToTest: Boolean
) {
    val pagingConfig = PagingConfig(
        pageSize = 3,
        enablePlaceholders = true,
        prefetchDistance = 2
    )

    // Create separate pagers for each category
    val categoryPagers = remember(uiState.categories, filterText) {
        uiState.categories.associateWith { category ->
            Pager(pagingConfig) {
                ArticlePagingSource(
                    articles = if (category.nomCategorieInCategoriesTabele == "NewArrivale") {
                        uiState.articlesBasesStatTables.filter { it.itsNewArrivale }
                    } else {
                        uiState.articlesBasesStatTables.filter {
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
        mutableMapOf<CategoriesTabelle, LazyPagingItems<ArticlesBasesStatsTable>>()
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
        contentPadding = PaddingValues(3.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalItemSpacing = 3.dp
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
                imageScale = contentScale,
                imageSize = imageSize
            )

            if (imageExists) {
                article.getColorIdForIndex(colorIndex)?.let { colorId ->
                    uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                        ColorIndicator(
                            iconColore = color.iconColore,
                            modifier = Modifier
                                .padding(3.dp)
                                .align(Alignment.BottomEnd)
                                .wrapContentSize()
                                .offset(x = (-10).dp, y = (-15).dp)
                            ,
                            imageSize = imageSize,
                            onClickToOpenWindow = { onClickToOpenWindow(article, colorIndex) }
                        )
                    }
                }
            }
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
     
    var currentQuality by remember { mutableStateOf(15f) }

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

    Box(modifier = modifier.size(width = imageSize.width, height = imageSize.height))
    {
        imageFile?.let { file ->
            GlideImage(
                model = file,
                contentDescription = "Article image ${article.idArticle}",
                contentScale = imageScale,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
            ) {
                it.applyImageOptions(article, indexColor, currentQuality) { isFirstResource ->
                    if (isFirstResource && currentQuality < 100f) {
                        currentQuality = 100f
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
                        onClickToOpenWindow={ onClickToOpenWindow(article, indexColor) }
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorOverlayWithBlur(
    color: ColorsArticlesTabelle,
    cornerRadius: Dp,
    onClickToOpenWindow: () -> Unit,
    ) {
    Box {
        // Blurred background image
        GlideImage(
            model = R.drawable.logo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
            contentDescription = null
        ) {
            it.transform(jp.wasabeef.glide.transformations.BlurTransformation(25))
        }

        // Semi-transparent black overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // Color overlay with content
        ColorOverlay(
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
            ,
            onClickToOpenWindow= onClickToOpenWindow
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

    val relatedArticlesBasesStatsTable = uiState.articlesBasesStatTables.find { it.idArticle ==1  }


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
                        viewModel.updataFirtEmptyArticle(filterText)
                        if (relatedArticlesBasesStatsTable != null) {
                            onAddNotInBaseArticle(relatedArticlesBasesStatsTable,0)
                        }
                    }) { Icon(Icons.Default.Add, contentDescription = "Add New Article")
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        viewModel.updataFirtEmptyArticle(filterText)
                        if (relatedArticlesBasesStatsTable != null) {
                            onAddNotInBaseArticle(relatedArticlesBasesStatsTable,0)
                        }
                    }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide()
               onClickDonne()
            })
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





