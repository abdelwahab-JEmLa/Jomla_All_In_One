package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.CategoriesModel
import a_RoomDB.ArticlesBasesStatsModel
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import c_WindosBuyAndDesplayeArticleStats.DisplayeImageECB

@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: HeadOfViewModels,
    onToggleNavBar: () -> Unit,
    onNewArticleAdded: (ArticlesBasesStatsModel) -> Unit,
    reloadTrigger: Int
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFloatingButtons by remember { mutableStateOf(false) }
    var gridColumns by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // Search Filter
            AnimatedVisibility(
                visible = showFilter,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

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

            // Grid Content
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                state = gridState,
                contentPadding = PaddingValues(8.dp)
            ) {
                uiState.categoriesECB.forEach { category ->
                    val articlesInCategory = uiState.articlesBaseDonneECB.filter { article ->
                        article.nomCategorie == category.name &&
                                article.diponibilityState.isEmpty() &&
                                (filterText.isEmpty() || article.nomArticleFinale.contains(filterText, ignoreCase = true))
                    }

                    if (articlesInCategory.isNotEmpty() || category.name == "New Articles") {
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
                                onClickOnImg = onNewArticleAdded,
                                viewModel = viewModel,
                                reloadTrigger = reloadTrigger
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Buttons
        FloatingActionButtons(
            showFloatingButtons = showFloatingButtons,
            onToggleNavBar = onToggleNavBar,
            onToggleFloatingButtons = { showFloatingButtons = !showFloatingButtons },
            onToggleOutlineFilter = { showFilter = !showFilter },
            onChangeGridColumns = { gridColumns = it }
        )
    }
}




@Composable
fun ArticleItemECB(
    article: ArticlesBasesStatsModel,
    onClickOnImg: (ArticlesBasesStatsModel) -> Unit,
    viewModel: HeadOfViewModels,
    reloadTrigger: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { onClickOnImg(article) },
                contentAlignment = Alignment.Center
            ) {
                DisplayeImageECB(
                    article = article,
                    index = 0,
                    reloadKey = reloadTrigger
                )

                if (article.funChangeImagsDimention) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color.Red,    // Add this line to make the icon red
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
//CategoryHeaderECB
@Composable
fun CategoryHeaderECB(
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
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}



