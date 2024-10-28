package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.CategoriesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.LoadingOverlay
import com.example.clientjetpack.R
import java.io.File

@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: StartUpNewArticlesViewModels,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    var gridColumnsForNewArticels by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()
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
    gridState: LazyGridState,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    val isNewArrivalsCategory = uiState.categories.any {
        it.nomCategorieInCategoriesTabele == "NewArrivale"
    }

    val effectiveGridColumns = if (isNewArrivalsCategory) gridColumns else 2

    LazyVerticalGrid(
        columns = GridCells.Fixed(effectiveGridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // Banner logic remains the same
        if (!showFilter) {
            item(span = { GridItemSpan(effectiveGridColumns) }) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }

        // Categories display
        displayCategories(
            uiState = uiState,
            filterText = filterText,
            gridColumns = effectiveGridColumns,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos
        )
    }
}
private fun LazyGridScope.displayCategories(
    uiState: UiState,
    filterText: String,
    gridColumns: Int,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    // New Arrivals category
    uiState.categories
        .find { it.nomCategorieInCategoriesTabele == "NewArrivale" }
        ?.let { category ->
            val articles = uiState.articlesBasesStatTabelles.filter {
                it.itsNewArrivale && matchesFilter(it, filterText)
            }
            if (articles.isNotEmpty()) {
                displayCategoryContent(
                    category = category,
                    articles = articles,
                    gridColumns = gridColumns,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos
                )
            }
        }

    // Other categories
    uiState.categories
        .filter { it.nomCategorieInCategoriesTabele != "NewArrivale" }
        .forEach { category ->
            val articles = uiState.articlesBasesStatTabelles.filter {
                it.nomCategorie == category.nomCategorieInCategoriesTabele &&
                        !it.itsNewArrivale &&
                        matchesFilter(it, filterText)
            }
            if (articles.isNotEmpty()) {
                displayCategoryContent(
                    category = category,
                    articles = articles,
                    gridColumns = gridColumns,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos
                )
            }
        }
}

private fun LazyGridScope.displayCategoryContent(
    category: CategoriesTabelle,
    articles: List<ArticlesBasesStatsTabelle>,
    gridColumns: Int,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    // Only show header if displayedHeader is true
    if (category.displayedHeader) {
        item(span = { GridItemSpan(gridColumns) }) {
            CategoryHeader(category)
        }
    }

    // Always show articles, regardless of displayedHeader
    items(
        count = articles.size,
        span = { index ->
            val article = articles[index]
            calculateSpan(article, gridColumns)
        }
    ) { index ->
        val article = articles[index]
        ArticleItem(
            article = article,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos
        )
    }
}
@Composable
private fun ArticleDisplayScreen(
    uiState: UiState,
    gridColumns: Int,
    showFilter: Boolean,
    filterText: String,
    gridState: LazyGridState,
    onFilterTextChange: (String) -> Unit,
    onToggleFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    onToggleNavBar: () -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
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
                showFilter = showFilter,  // Pass showFilter to ArticleGrid
                gridState = gridState,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger, onClickToOpenWindos = onClickToOpenWindos
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

@Composable
private fun ArticleItem(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    val hasThreeColors = countColors(article) == 3

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        if (hasThreeColors) {
            ThreeColorArticleDisplay(
                article = article,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger, onClickToOpenWindos = onClickToOpenWindos
            )
        } else {
            DisplayeArticleWhithOneColore(
                article = article,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger,
                modifier = Modifier, onClickToOpenWindos = onClickToOpenWindos
            )
        }
    }
}



@Composable
fun DisplayeArticleWhithOneColore(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier, onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                ImageDisplayer(
                    modifier=modifier,
                    article = article,
                    viewModel = viewModel,
                    indexColor = 0,
                    reloadKey = reloadTrigger,
                    onClickToOpenWindos
                )
            }
        }
    }
}

@Composable
private fun ThreeColorArticleDisplay(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Première image
            Box(
                modifier = Modifier
                    .height(500.dp)
                    .fillMaxWidth()
            ) {
                ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = 0,
                    reloadKey = reloadTrigger,
                    onClickToOpenWindos
                )
            }

            // Deuxième image
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = 1,
                    reloadKey = reloadTrigger,
                    onClickToOpenWindos
                )
            }

            // Troisième image
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                ImageDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = 2,
                    reloadKey = reloadTrigger,
                    onClickToOpenWindos
                )
            }
        }
    }
}


// 3. Make image width match container
@Composable
fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    indexColor: Int = 0,
    reloadKey: Any = Unit,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
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

    Box(modifier = modifier.fillMaxWidth()
        .clickable { onClickToOpenWindos(article,indexColor) }) {
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
private fun calculateSpan(article: ArticlesBasesStatsTabelle, gridColumns: Int): GridItemSpan {
    return when {
        countColors(article) == 3 && !article.funChangeImagsDimention -> GridItemSpan(gridColumns)
        else -> GridItemSpan(1)
    }
}

private fun matchesFilter(article: ArticlesBasesStatsTabelle, filterText: String): Boolean {
    return filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true)
}

private fun countColors(article: ArticlesBasesStatsTabelle): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }
}
