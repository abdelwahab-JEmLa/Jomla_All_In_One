package b_StartupAppDisplayerOfNewArticles

// Coil imports

// Coroutines

// File operations

// Your models/state classes (make sure these match your project structure)
import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ColorsArticlesTabelle
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.example.clientjetpack.LoadingOverlay
import com.example.clientjetpack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit
) {
    val isNewArrivalsCategory = uiState.categories.any {
        it.nomCategorieInCategoriesTabele == "NewArrivale"
    }

    val effectiveGridColumns = if (isNewArrivalsCategory) gridColumns else 2

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(effectiveGridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        // Banner logic
        if (!showFilter) {
            item(span = StaggeredGridItemSpan.FullLine) {
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

private fun LazyStaggeredGridScope.displayCategories(
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
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState
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
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState
                )
            }
        }
}

private fun LazyStaggeredGridScope.displayCategoryContent(
    category: CategoriesTabelle,
    articles: List<ArticlesBasesStatsTabelle>,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    // Only show header if displayedHeader is true
    if (category.displayedHeader) {
        item(span = StaggeredGridItemSpan.FullLine) {
            CategoryHeader(category)
        }
    }

    items(
        items = articles,
        span = { article ->
            // If the article has "Demi" imageDimension, make it span full width
            if (article.imageDimention == "Demi") {
                StaggeredGridItemSpan.FullLine
            } else {
                StaggeredGridItemSpan.SingleLane
            }
        }
    ) { article ->
        ArticleItem(
            article = article,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos,
            uiState = uiState
        )
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
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    val colorCount = countColors(article)

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        when {
            article.imageDimention == "Demi" && colorCount > 1 -> {
                DemiDiplayerMultiColor(
                    article = article,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState
                )
            }
            colorCount == 3 -> {
                SmalleDiplayerHave3Color(
                    article = article,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState
                )
            }
            else -> {
                ArticleDiplayerHave1Color(
                    article = article,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier,
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
private fun DemiDiplayerMultiColor(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    Column(modifier = Modifier.padding(8.dp)) {
        ArticleDetails(article)
        ColorImageWithDetails(
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.height(300.dp),
            onClickToOpenWindos = onClickToOpenWindos,
            uiState = uiState
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
            contentAlignment = Alignment.Center
        ) {
            Row {
                // Calculate available colors excluding the first one (index 0)
                val availableColors = (1..3).filter { index ->
                    article.getColorIdForIndex(index) != null
                }

                availableColors.forEach { index ->
                    ColorImageWithDetails(
                        article = article,
                        viewModel = viewModel,
                        colorIndex = index,
                        reloadTrigger = reloadTrigger,
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp),
                        onClickToOpenWindos = onClickToOpenWindos,
                        uiState = uiState
                    )
                }
            }
        }
    }
}




@Composable
private fun ArticleDiplayerHave1Color(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
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
                    modifier =modifier,
                    article = article,
                    viewModel = viewModel,
                    indexColor = 0,
                    reloadKey = reloadTrigger,
                    onClickToOpenWindos,
                    uiState
                )
            }
            ArticleDetails(article)
        }
    }
}
@Composable
private fun SmalleDiplayerHave3Color(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
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
            // Main large image
            ColorImageWithDetails(
                article = article,
                viewModel = viewModel,
                colorIndex = 0,
                reloadTrigger = reloadTrigger,
                modifier = Modifier.height(200.dp),
                onClickToOpenWindos = onClickToOpenWindos,
                uiState = uiState
            )

            // Secondary images in a loop
            repeat(2) { index ->
                ColorImageWithDetails(
                    article = article,
                    viewModel = viewModel,
                    colorIndex = index + 1,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier.height(70.dp),
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState
                )
            }

            ArticleDetails(
                article = article,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
private fun ColorImageWithDetails(
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    colorIndex: Int,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        val imageExists = remember(article.idArticle, colorIndex, reloadTrigger) {
            checkImageExists(
                viewModel = viewModel,
                article = article,
                colorIndex = colorIndex,
                reloadTrigger = reloadTrigger
            )
        }

        ImageDisplayer(
            modifier = Modifier.fillMaxSize(),
            article = article,
            viewModel = viewModel,
            indexColor = colorIndex,
            reloadKey = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos,
            uiState = uiState
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
private fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    indexColor: Int = 0,
    reloadKey: Any = Unit,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModelImagesPath = viewModel.viewModelImagesPath

    // État pour suivre la qualité actuelle (de 25 à 100)
    var currentQuality by remember { mutableStateOf(25f) }

    val baseImagePath = remember(viewModelImagesPath, article.idArticle, indexColor) {
        File(viewModelImagesPath, "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}")
            .absolutePath
    }

    val imageExist by produceState<String?>(null, baseImagePath, reloadKey) {
        value = withContext(Dispatchers.IO) {
            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                val file = File("$baseImagePath.$extension")
                if (file.exists() && file.canRead()) {
                    file.absolutePath
                } else null
            }
        }
    }

    // Animation de la qualité
    LaunchedEffect(imageExist, article.idArticle, indexColor) {
        currentQuality = 25f // Reset à 25%
        // Progression sur 1.5 secondes
        val steps = 75 // Nombre de pas pour aller de 25 à 100
        val delayPerStep = 1500L / steps // Temps entre chaque pas
        val incrementPerStep = 75f / steps // Augmentation par pas

        // Animation progressive
        repeat(steps) {
            delay(delayPerStep)
            currentQuality = (currentQuality + incrementPerStep).coerceAtMost(100f)
        }
    }

    // Calcul de la taille basée sur la qualité actuelle
    val currentSize = remember(currentQuality) {
        // Interpolation linéaire entre 128 (25%) et 512 (100%)
        val size = (128 + (512 - 128) * (currentQuality - 25) / 75).toInt()
        Size(size, size)
    }

    // Image request avec la qualité actuelle
    val imageRequest = remember(imageExist, article.idArticle, indexColor, currentSize) {
        ImageRequest.Builder(context)
            .data(imageExist?.let { File(it) } ?: R.drawable.baked_goods_1)
            .size(currentSize)
            .scale(Scale.FILL)
            .crossfade(true)
            .memoryCacheKey("${article.idArticle}_${indexColor}_${currentQuality}")
            .diskCacheKey("${article.idArticle}_${indexColor}_${currentQuality}")
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickToOpenWindos(article, indexColor) }
    ) {
        var isLoading by remember { mutableStateOf(true) }

        AsyncImage(
            model = imageRequest,
            contentDescription = "Article image ${article.idArticle} color ${indexColor + 1}",
            modifier = Modifier
                .fillMaxWidth()
                .then(if (imageExist == null) Modifier.alpha(0.7f) else Modifier),
            contentScale = ContentScale.FillWidth,
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
            }
        )

        // Indicateur de chargement
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    progress = (currentQuality - 25f) / 75f // Progression basée sur la qualité
                )
            }
        }

        // Overlay pour images manquantes
        if (imageExist == null) {
            article.getColorIdForIndex(indexColor)?.let { colorId ->
                uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                    ColorOverlay(color = color)
                }
            }
        }

        // Préchargement de l'image suivante
        LaunchedEffect(article.idArticle, indexColor) {
            scope.launch(Dispatchers.IO) {
                val nextIndex = (indexColor + 1) % 4
                val nextImagePath = File(
                    viewModelImagesPath,
                    "${article.idArticle}_${if (nextIndex == -1) "Unite" else (nextIndex + 1)}"
                ).absolutePath

                // Précharger plusieurs qualités de la prochaine image
                listOf(128, 256, 384, 512).forEach { size ->
                    ImageRequest.Builder(context)
                        .data(nextImagePath)
                        .size(Size(size, size))
                        .scale(Scale.FILL)
                        .build()
                        .let { request ->
                            context.imageLoader.enqueue(request)
                        }
                }
            }
        }

        // Affichage du pourcentage de qualité (à des fins de débogage, à supprimer en production)
        Text(
            text = "${currentQuality.toInt()}%",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ColorOverlay(
    color: ColorsArticlesTabelle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = color.iconColore,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.displayLarge.fontSize,
            overflow = TextOverflow.Visible,
            softWrap = true
        )

        Text(
            text = color.nameColore,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f),
            textAlign = TextAlign.Center
        )
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
