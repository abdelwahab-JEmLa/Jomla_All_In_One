package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.CategoriesModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.clientjetpack.LoadingOverlay

@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: HeadOfViewModels,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int
) {
    val uiState by viewModel.uiState.collectAsState()
    var gridColumns by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            state = gridState,
            contentPadding = PaddingValues(8.dp)
        ) {
            // Add banner as the first item spanning full width
            item(span = { GridItemSpan(gridColumns) }) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Filter UI
            if (showFilter) {
                item(span = { GridItemSpan(gridColumns) }) {
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
            }

            // Categories and articles
            uiState.categories.forEach { category ->
                val articlesInCategory = uiState.articlesBasesStatsModel.filter { article ->
                    article.nomCategorie == category.nomCategorieInCategoriesTabele &&
                            article.diponibilityState.isEmpty() &&
                            (filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true))
                }

                if (articlesInCategory.isNotEmpty() || category.nomCategorieInCategoriesTabele == "New Articles") {
                    item(span = { GridItemSpan(gridColumns) }) {
                        CategoryHeaderECB(
                            category = category,
                        )
                    }

                    items(
                        items = articlesInCategory,
                        key = { it.idArticle }
                    ) { article ->
                        ArticleItemECB(
                            article = article,
                            viewModel = viewModel,
                            reloadTrigger = reloadTrigger
                        )
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
            LoadingOverlay(
                progress = uiState.loadingProgress
            )
        }
    }
}


