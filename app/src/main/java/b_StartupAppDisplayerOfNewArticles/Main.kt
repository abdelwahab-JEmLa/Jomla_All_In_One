package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import c_WindosBuyAndDesplayeArticleStats.ImageDisplayer
import com.example.clientjetpack.LoadingOverlay

// StartupAppDisplayerOfNewArticles.kt
@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: StartUpNewArticlesViewModels,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int
) {
    val uiState by viewModel.uiState.collectAsState()
    var gridColumns by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            if (showFilter) {
                OutlinedTextField(
                    value = filterText,
                    onValueChange = { filterText = it },
                    label = { Text("Filter Articles") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                )
            }

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
                                article.diponibilityState.isEmpty() &&
                                (filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true))
                    }

                    if (articlesInCategory.isNotEmpty() || category.nomCategorieInCategoriesTabele == "New Articles") {
                        item(span = { GridItemSpan(gridColumns) }) {
                            CategoryHeaderECB(category = category)
                        }

                        items(
                            items = articlesInCategory,
                            key = { it.idArticle }
                        ) { article ->
                            val colorCount = countColors(article)
                            if (colorCount == 3) {
                                ThreeColorArticleDisplay(
                                    article = article,
                                    viewModel = viewModel,
                                    reloadTrigger = reloadTrigger,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                DisplayeArticleWhithOneColore(
                                    article = article,
                                    viewModel = viewModel,
                                    reloadTrigger = reloadTrigger
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButtonGroup(
            onToggleNavBar = onToggleNavBar,
            onToggleOutlineFilter = { showFilter = !showFilter },
            onChangeGridColumns = { gridColumns = it },
            viewModel = viewModel
        )

        if (uiState.isLoading) {
            LoadingOverlay(progress = uiState.loadingProgress)
        }
    }
}

@Composable
fun ThreeColorArticleDisplay(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageDisplayer(
                    viewModel = viewModel,
                    article = article,
                    index = 0,
                    reloadKey = reloadTrigger,
                    modifier = Modifier.aspectRatio(1f)
                )
                ImageDisplayer(
                    viewModel = viewModel,
                    article = article,
                    index = 1,
                    reloadKey = reloadTrigger,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.5f)
            ) {
                ImageDisplayer(
                    viewModel = viewModel,
                    article = article,
                    index = 2,
                    reloadKey = reloadTrigger,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


