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
    //   @Entity
    //data class ArticlesBasesStatsTabelle(
    //    @PrimaryKey var idArticle: Int = 0,
    //    var nomArticleFinale: String = "",
    //    var classementCate: Double = 0.0,
    //    var nomArab: String = "",
    //    var autreNomDarticle: String? = null,
    //    var nmbrCat: Int = 0,
    //    var couleur1: String? = null,
    //    var idcolor1: Long = 0,
    //    var couleur2: String? = null,
    //    var idcolor2: Long = 0,
    //    var couleur3: String? = null,
    //    var idcolor3: Long = 0,
    //    var couleur4: String? = null,
    //    var idcolor4: Long = 0,
    //    var nomCategorie2: String? = null,
    //    var nmbrUnite: Int = 0,
    //    var nmbrCaron: Int = 0,
    //    var affichageUniteState: Boolean = false,
    //    var commmentSeVent: String? = null,
    //    var afficheBoitSiUniter: String? = null,
    //    var monPrixAchat: Double = 0.0,
    //    var clienPrixVentUnite: Double = 0.0,
    //    var minQuan: Int = 0,
    //    var monBenfice: Double = 0.0,
    //    var monPrixVent: Double = 0.0,
    //    var diponibilityState: String = "",
    //    var neaon2: String = "",
    //    var idCategorie: Double = 0.0,
    //    var funChangeImagsDimention: Boolean = false, //imgStatIsSmall
    //    var nomCategorie: String = "",
    //    var neaon1: Double = 0.0,
    //    var lastUpdateState: String = "",
    //    var cartonState: String = "",
    //    var dateCreationCategorie: String = "",
    //    var prixDeVentTotaleChezClient: Double = 0.0,
    //    var benficeTotaleEntreMoiEtClien: Double = 0.0,
    //    var benificeTotaleEn2: Double = 0.0,
    //    var monPrixAchatUniter: Double = 0.0,
    //    var monPrixVentUniter: Double = 0.0,
    //    var benificeClient: Double = 0.0,
    //    var monBeneficeUniter: Double = 0.0,
    //    //Stats
    //    var articleHaveUniteImages: Boolean = false,
    //    var itsNewArrivale: Boolean = false,
    //) {
    //    // No-argument constructor for Firebase
    //    constructor() : this(0)
    //}
    //@Entity
    //data class ColorsArticlesTabelle(
    //    @PrimaryKey var idColore: Long = 0,
    //    val nameColore: String = "",
    //    val iconColore: String = "",
    //    var classementColore: Int = 0
    //){
    //    // No-argument constructor for Firebase
    //    constructor() : this(0)
    //}
    //
    //@Entity
    //data class CategoriesTabelle(
    //    @PrimaryKey(autoGenerate = true)
    //    val idCategorieInCategoriesTabele: Long = 0,
    //    val nomCategorieInCategoriesTabele: String = "",
    //    var idClassementCategorieInCategoriesTabele: Int = 0 ,
    //    var displayedHeader: Boolean = false,
    //
    //    ) {
    //    constructor() : this(0, "", 0)
    //}
    //
    //@Entity
    //data class SoldArticlesTabelle(
    //    @PrimaryKey(autoGenerate = true) val vid: Long = 0,
    //    val idArticle: Long = 0,
    //    val nameArticle: String = "",
    //    val clientSoldToItId: Long = 0,
    //    val date: String = "",
    //    val color1IdPicked: Long = 0,
    //    val color1SoldQuantity: Int = 0,
    //    val color2IdPicked: Long = 0,
    //    val color2SoldQuantity: Int = 0,
    //    val color3IdPicked: Long = 0,
    //    val color3SoldQuantity: Int = 0,
    //    val color4IdPicked: Long = 0,
    //    val color4SoldQuantity: Int = 0,
    //    val confimed: Boolean = false,
    //
    //    ) {
    //    constructor() : this(0)
    //}
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
            Box(     //TODO ffait que ca soit on loop don un box divise par 1f pour chacune son heit 300 divise par eu
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
                ImageDisplayer(      //TODO au lieu ImageDisplayer fait que ca soit dont une funtion contie au dessu end
                    //          on trouve le rlated color in color tableau affiche le imoji
                    modifier = Modifier.fillMaxSize(),
                    article = article,
                    viewModel = viewModel,
                    indexColor = 2,
                    reloadKey = reloadTrigger,
                    onClickToOpenWindos
                )
            }
            //TODO imptrove la visibility du code
            //cree desplaye de nom article
            //cree desplye prix   article.prixVent

        }
    }
}


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
