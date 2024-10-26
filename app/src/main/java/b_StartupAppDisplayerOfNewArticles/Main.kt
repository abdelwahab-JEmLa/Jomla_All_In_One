package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import a_RoomDB.CategoriesModel
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                showFilter = showFilter,  // Pass showFilter to ArticleGrid
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
private fun ArticleGrid(
    uiState: UiState,
    gridColumns: Int,
    filterText: String,
    showFilter: Boolean,  // Added showFilter parameter
    gridState: LazyGridState,
    onArticleClick: (ArticlesBasesStatsModel) -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(
            when {
                uiState.categories.find { it.nomCategorieInCategoriesTabele == "NewArrivale" } != null -> gridColumns
                else -> 2
            }
        ),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Only show banner when filter is not active
        if (!showFilter) {
            item(span = { GridItemSpan(gridColumns) }) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
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
