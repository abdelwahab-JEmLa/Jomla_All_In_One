package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import a_RoomDB.CategoriesModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.LoadingOverlay
import com.example.clientjetpack.R
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun ScrolleAdBanner(
    modifier: Modifier = Modifier
) {
    var currentBannerIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val cardWidth = with(density) { 350.dp.toPx() }
    val totalCards = 3

    // Custom auto-scroll behavior
    LaunchedEffect(Unit) {
        while (true) {
            // Forward scroll (left to right)
            while (currentBannerIndex < totalCards - 1) {
                delay(1500)

                // Calculate steps for forward scroll
                val totalSteps = 35
                val stepSize = cardWidth / totalSteps

                // Smooth scroll to next card
                for (step in 0 until totalSteps) {
                    val nextPosition = (currentBannerIndex * cardWidth) + (step * stepSize)
                    scrollState.scrollTo(nextPosition.toInt())
                    delay(10) // 10ms delay between each step
                }

                currentBannerIndex++
            }

            // At this point we're at the last card
            delay(3000) // Pause before returning

            // Reverse scroll (right to left)
            val totalSteps = 35
            val maxScroll = (totalCards - 1) * cardWidth
            val stepSize = maxScroll / totalSteps

            // Smooth scroll back to start
            for (step in 0 until totalSteps) {
                val nextPosition = maxScroll - (step * stepSize)
                scrollState.scrollTo(nextPosition.toInt())
                delay(10) // 10ms delay between each step
            }

            // Reset position
            currentBannerIndex = 0
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val images = listOf(
            R.drawable.baked_goods_1,
            R.drawable.baked_goods_2,
            R.drawable.baked_goods_3
        )

        images.forEachIndexed { index, imageRes ->
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .height(150.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Banner image ${index + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

//CategoryHeaderECB
@Composable
fun CategoryHeader(
    category: CategoriesModel,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.nomCategorieInCategoriesTabele,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}
@Composable
fun ArticleGrid(
    uiState: UiState,
    gridColumns: Int,
    filterText: String,
    gridState: LazyGridState,
    onArticleClick: (ArticlesBasesStatsModel) -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(gridColumns) }) {     //TODO fait que si le clavie s affiche ca ce cache
            ScrolleAdBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        // Afficher d'abord la catégorie NewArrivale si elle existe
        val newArrivaleCategory = uiState.categories.find {
            it.nomCategorieInCategoriesTabele == "NewArrivale"
        }

        newArrivaleCategory?.let { category ->
            val newArrivaleArticles = uiState.articlesBasesStatsModel.filter { article ->
                article.itsNewArrivale && matchesFilter(article, filterText)
            }

            if (newArrivaleArticles.isNotEmpty()) {
                categorySection(category, newArrivaleArticles, gridColumns, onArticleClick, viewModel, reloadTrigger)
            }
        }

        // Afficher les autres catégories
        uiState.categories
            .filter { it.nomCategorieInCategoriesTabele != "NewArrivale" }
            .forEach { category ->
                val articlesInCategory = uiState.articlesBasesStatsModel.filter { article ->
                    article.nomCategorie == category.nomCategorieInCategoriesTabele &&
                            !article.itsNewArrivale &&
                            matchesFilter(article, filterText)
                }

                if (articlesInCategory.isNotEmpty()) {
                    categorySection(category, articlesInCategory, gridColumns, onArticleClick, viewModel, reloadTrigger)
                }
            }
    }
}

private fun LazyGridScope.categorySection(
    category: CategoriesModel,
    articles: List<ArticlesBasesStatsModel>,
    gridColumns: Int,
    onArticleClick: (ArticlesBasesStatsModel) -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int
) {
    // En-tête de la catégorie
    item(span = { GridItemSpan(gridColumns) }) {
        CategoryHeader(category)
    }

    // Articles
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
            onArticleClick = onArticleClick
        )
    }
}


@Composable
private fun ArticleItem(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onArticleClick: (ArticlesBasesStatsModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasThreeColors = countColors(article) == 3

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onArticleClick(article) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        if (hasThreeColors) {
            ThreeColorArticleDisplay(
                article = article,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger
            )
        } else {
            DisplayeArticleWhithOneColore(
                article = article,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger,
                modifier = Modifier
            )
        }
    }
}

private fun calculateSpan(article: ArticlesBasesStatsModel, gridColumns: Int): GridItemSpan {
    return when {
        countColors(article) == 3 && !article.funChangeImagsDimention -> GridItemSpan(gridColumns)
        else -> GridItemSpan(1)
    }
}

private fun matchesFilter(article: ArticlesBasesStatsModel, filterText: String): Boolean {
    return filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true)
}

private fun countColors(article: ArticlesBasesStatsModel): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }
}
@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: StartUpNewArticlesViewModels,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
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
        onArticleClick = viewModel::updateCurrentArticle,
        viewModel = viewModel,
        reloadTrigger = reloadTrigger,
        modifier = modifier
    )
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
    onArticleClick: (ArticlesBasesStatsModel) -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
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
                gridState = gridState,
                onArticleClick = onArticleClick,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger
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
fun DisplayeArticleWhithOneColore(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier
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
                    .aspectRatio(1f)
                    .clickable { viewModel.updateCurrentArticle(article) },
                contentAlignment = Alignment.Center
            ) {
                ImageDisplayer(
                    viewModel = viewModel,
                    article = article,
                    indexColor = 0,
                    reloadKey = reloadTrigger,
                    modifier=modifier
                )
            }
        }
    }
}

@Composable
private fun ThreeColorArticleDisplay(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
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
                    viewModel = viewModel,
                    article = article,
                    indexColor = 0,
                    reloadKey = reloadTrigger,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Deuxième image
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                ImageDisplayer(
                    viewModel = viewModel,
                    article = article,
                    indexColor = 1,
                    reloadKey = reloadTrigger,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Troisième image
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                ImageDisplayer(
                    viewModel = viewModel,
                    article = article,
                    indexColor = 2,
                    reloadKey = reloadTrigger,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun SearchFilter(
    showFilter: Boolean,
    filterText: String,
    onFilterTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    if (showFilter) {
        OutlinedTextField(
            value = filterText,
            onValueChange = onFilterTextChange,
            label = { Text("Filter Articles") },
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
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
}
// 3. Make image width match container
@Composable
fun ImageDisplayer(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    indexColor: Int = 0,
    reloadKey: Any = Unit,
    modifier: Modifier = Modifier
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
