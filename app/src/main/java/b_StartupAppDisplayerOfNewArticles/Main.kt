package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import a_RoomDB.CategoriesModel
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import c_WindosBuyAndDesplayeArticleStats.DisplayeImageECB
import com.example.clientjetpack.LoadingOverlay

@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: StartUpNewArticlesViewModels,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
) {
    var gridColumns by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()
    val uiState by viewModel.uiState.collectAsState()

    ArticleDisplayScreen(
        uiState = uiState,
        gridColumns = gridColumns,
        showFilter = showFilter,
        filterText = filterText,
        gridState = gridState,
        onFilterTextChange = { filterText = it },
        onToggleFilter = { showFilter = !showFilter },
        onChangeGridColumns = { gridColumns = it },
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
private fun SearchFilter(
    showFilter: Boolean,
    filterText: String,
    onFilterTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            }
        )
    }
}

@Composable
private fun ArticleGrid(
    uiState: UiState,
    gridColumns: Int,
    filterText: String,
    gridState: LazyGridState,
    onArticleClick: (ArticlesBasesStatsModel) -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp)
    ) {
        item(span = { GridItemSpan(gridColumns) }) {
            ScrolleAdBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        uiState.categories.forEach { category ->
            val articlesInCategory = uiState.articlesBasesStatsModel.filter { article ->
                article.nomCategorie == category.nomCategorieInCategoriesTabele &&
                        (filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true))
            }

            if (articlesInCategory.isNotEmpty() || category.nomCategorieInCategoriesTabele == "New Articles") {
                categorySection(
                    category = category,
                    articles = articlesInCategory,
                    gridColumns = gridColumns,
                    onArticleClick = onArticleClick,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger
                )
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
    val tag = "CategorySection"

    item(span = { GridItemSpan(gridColumns) }) {
        CategoryHeaderECB(category = category)
    }

    items(
        count = articles.size,
        span = { index ->
            val article = articles[index]
            android.util.Log.d(tag, """
                Calculating span for article: ${article.nomArticleFinale}
                Colors count: ${countColors(article)}
                funChangeImagsDimention: ${article.funChangeImagsDimention}
            """.trimIndent())

            // Return GridItemSpan instead of just an integer
            GridItemSpan(1)
        }
    ) { index ->
        val article = articles[index]

        if (countColors(article) == 3) {
            if (!article.funChangeImagsDimention) {
                android.util.Log.d(tag, "Rendering in full width")
                Box(
                    modifier = Modifier.fillMaxWidth(1f)  // Force double largeur
                ) {
                    ThreeColorArticleDisplay(
                        article = article,
                        viewModel = viewModel,
                        reloadTrigger = reloadTrigger,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArticleClick(article) }
                    )
                }
            } else {
                android.util.Log.d(tag, "Rendering in grid of 2")
                ThreeColorArticleDisplay(
                    article = article,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onArticleClick(article) }
                )
            }
        } else {
            android.util.Log.d(tag, "Rendering normal article")
            DisplayeArticleWhithOneColore(
                article = article,
                viewModel = viewModel,
                reloadTrigger = reloadTrigger,
                modifier = Modifier.clickable { onArticleClick(article) }
            )
        }
    }
}

private fun countColors(article: ArticlesBasesStatsModel): Int {
    val colorCount = listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }

    android.util.Log.d("ColorCount", """
        Article: ${article.nomArticleFinale}
        Colors found: $colorCount
        Color1: ${article.couleur1?.isNotEmpty()}
        Color2: ${article.couleur2?.isNotEmpty()}
        Color3: ${article.couleur3?.isNotEmpty()}
        Color4: ${article.couleur4?.isNotEmpty()}
    """.trimIndent())

    return colorCount
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
                DisplayeImageECB(
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
                DisplayeImageECB(
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
                DisplayeImageECB(
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
                DisplayeImageECB(
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
