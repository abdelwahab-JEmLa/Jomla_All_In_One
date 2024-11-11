package c_WindowsDesplayeArticle

import a_RoomDB.AppDatabase
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R
import java.io.File



@Preview(
    device = Devices.PHONE,
    widthDp = 360,
    heightDp = 800,
    showBackground = true
)
@Composable
fun DisplayArticleInfoToClientWindowsPreview() {
    val sampleArticleStats = ArticlesBasesStatsTable(
        idArticle = 65,
        nomArticleFinale = "Silca®",
        nomArab = "سيلكا",
        nomCategorie = "Fun Candys",
        monPrixAchat = 370.0,
        monPrixVent = 430.0,
        monBenfice = 60.0,
        nmbrUnite = 48,
        couleur1 = "🍓 Fraise 🍓",
        couleur2 = "🥤 Coca 🥤",
        couleur3 = "🍏 تفاح 🍏",
        couleur4 = "🍋 Citron 🍋",
        idcolor1 = 10,
        idcolor2 = 29,
        idcolor3 = 30,
        idcolor4 = 31
    )

    val sampleColorsList = listOf(
        ColorsArticlesTabelle(
            classementColore = 1,
            iconColore = "🎨",
            idColore = 1,
            nameColore = "Multi Couleur"
        ),
        ColorsArticlesTabelle(
            classementColore = 10,
            iconColore = "🍓",
            idColore = 10,
            nameColore = "Fraise"
        ),
        ColorsArticlesTabelle(
            classementColore = 29,
            iconColore = "🥤",
            idColore = 29,
            nameColore = "Coca"
        ),
        ColorsArticlesTabelle(
            classementColore = 30,
            iconColore = "🍏",
            idColore = 30,
            nameColore = "تفاح"
        ),
        ColorsArticlesTabelle(
            classementColore = 31,
            iconColore = "🍋",
            idColore = 31,
            nameColore = "Citron"
        )
    )

    val context = LocalContext.current
    val previewViewModel = remember {
        StartUpNewArticlesViewModels(
            context = context,
            database = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            ).build()
        )
    }

    MaterialTheme {
        DisplayeArticleInfoToClientWindows(
            articleStatsDataBase = sampleArticleStats,
            colorsArticlesList = sampleColorsList,
            viewModel = previewViewModel,
            reloadTrigger = 0
        )
    }
}
@Composable
fun DisplayeArticleInfoToClientWindows(
    articleStatsDataBase: ArticlesBasesStatsTable,
    colorsArticlesList: List<ColorsArticlesTabelle> ,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
) {
    var isPickerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isPickerVisible = true
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                articleStatsDataBase?.let { stats ->
                    ProductNameSection(stats)

                    // Visual Divider with Label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = "اختر اللون",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                    // Colors Selection with Animation
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        ColorsCards(
                            articlesBasesStatsTable = stats,
                            viewModel = viewModel,
                            relodeTigger = reloadTrigger,
                            colorsArticlesList = colorsArticlesList,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun ProductNameSection(article: ArticlesBasesStatsTable) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Name Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = article.nomArticleFinale,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                if (article.nomArab.isNotEmpty()) {
                    Text(
                        text = article.nomArab,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ColorsCards(
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    viewModel: StartUpNewArticlesViewModels,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    colorsArticlesList: List<ColorsArticlesTabelle>,
) {
    val colors = listOf(
        articlesBasesStatsTable.idcolor1,
        articlesBasesStatsTable.idcolor2,
        articlesBasesStatsTable.idcolor3,
        articlesBasesStatsTable.idcolor4
    ).mapNotNull { colorId ->
        if (colorId != 0L) {
            colorsArticlesList.find { it.idColore == colorId }
        } else null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main large image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                ColorItem(
                    modifier = Modifier.fillMaxSize(),
                    article = articlesBasesStatsTable,
                    color = colors.firstOrNull(),
                    index = 0,
                    relodeTigger = relodeTigger,
                    viewModel = viewModel,
                    height = 360.dp
                )
            }

            // Color variants in LazyRow
            if (colors.size > 1) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(colors.drop(1)) { color ->
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            ColorItem(
                                modifier = Modifier.fillMaxSize(),
                                article = articlesBasesStatsTable,
                                color = color,
                                index = colors.indexOf(color),
                                relodeTigger = relodeTigger,
                                viewModel = viewModel,
                                height = 200.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorItem(
    modifier: Modifier,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    index: Int,
    relodeTigger: Int,
    viewModel: StartUpNewArticlesViewModels,
    height: Dp,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image
            ImageDisplayer(
                modifier = Modifier.fillMaxSize(),
                article = article,
                viewModel = viewModel,
                indexColor = index,
                reloadKey = relodeTigger
            )

            // Color info overlay at bottom
            color?.let { colorData ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                            )
                        ),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color name
                        Text(
                            text = colorData.nameColore,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Color icon
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    colorData.iconColore in listOf("©", "💯", "") -> {
                                        GlideImage(
                                            model = R.drawable.logo,
                                            contentDescription = "Logo",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = colorData.iconColore,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    viewModel: StartUpNewArticlesViewModels,
    indexColor: Int = 0,
    reloadKey: Any = Unit
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
        imageExist?.let { File(it) } ?: R.drawable.logo
    }

    val requestKey = remember(article.idArticle, indexColor, reloadKey) {
        "${article.idArticle}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
    }

    Box(modifier = modifier.fillMaxWidth()) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageSource)
                .size(350,350)  // Use original size to maintain aspect ratio
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



