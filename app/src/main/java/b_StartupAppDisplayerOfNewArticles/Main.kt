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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import c_WindosBuyAndDesplayeArticleStats.DisplayeImageECB
import com.example.clientjetpack.LoadingOverlay
import com.example.clientjetpack.R
import kotlinx.coroutines.delay

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
                delay(3000) // Pause for 3 seconds

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
                    .width(350.dp)
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
@Composable
fun ArticleItemECB(
    article: ArticlesBasesStatsModel,
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
                    .clickable { viewModel.updateCurrentArticle(article) },
                contentAlignment = Alignment.Center
            ) {
                DisplayeImageECB(
                    article = article,
                    index = 0,
                    reloadKey = reloadTrigger
                )
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
                text = category.nomCategorieInCategoriesTabele,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}



