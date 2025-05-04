package Views.P1.Ui.ArticlesGrid

import Views.P1.Ui.ArticlesGrid.ArticleItem.ColorOverlay
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.clientjetpack.Repositorys.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayer1(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    indexColor: Int,
    reloadKey: Any,
    onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    showOverlay: Boolean,
    imageScale: ContentScale = ContentScale.Fit,
    cornerRadius: Dp = 4.dp,
    imageSize: DpSize,
    finalequalityImagePourcentage: Int = 100,
    viewModelInitApp: ViewModelInitApp,
    ) {
    val a_ProduitModelRepository = viewModelInitApp.produitModelRepository

    val produitDepuitNewDATABASE = a_ProduitModelRepository
        .modelDatas.find { it.id.toInt() == article.idArticle }

    var currentQuality by remember { mutableStateOf(5f) }
    var isLoading by remember { mutableStateOf(true) }
    var imageLoaded by remember { mutableStateOf(false) }

    // Calculate the initial and target quality based on device capabilities and image size
    val initialQuality = remember(imageSize) {
        // Start with lower quality for faster initial load
        (imageSize.width.value * imageSize.height.value / 10000).coerceIn(5f, 25f)
    }

    val targetQuality = remember(finalequalityImagePourcentage) {
        // Use the finalequalityImagePourcentage parameter to determine final quality
        finalequalityImagePourcentage.toFloat().coerceIn(30f, 100f)
    }

    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    LaunchedEffect(reloadKey) {
        isLoading = true
        imageLoaded = false
        currentQuality = initialQuality

        // Progressive loading strategy
        delay(300) // Initial loading delay
        currentQuality = targetQuality // Transition to target quality
        imageLoaded = true

        delay(700) // Keep blur for 700ms after image loads
        isLoading = false
    }

    val imagePath by remember(viewModel.viewModelImagesPath, article.idArticle, indexColor) {
        derivedStateOf {
            val baseFileName =
                "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"
            File(viewModel.viewModelImagesPath, baseFileName)
        }
    }

    val imageFile by produceState<File?>(
        initialValue = null,
        key1 = imagePath,
        key2 = reloadKey
    ) {
        value = withContext(Dispatchers.IO) {
            listOf("jpg", "webp")
                .asSequence()
                .map { ext -> File("${imagePath.absolutePath}.$ext") }
                .firstOrNull { it.exists() && it.canRead() }
        }
    }

    Box(modifier = modifier.size(width = imageSize.width, height = imageSize.height)) {
        imageFile?.let { file ->
            GlideImage(
                model = file,
                contentDescription = "Article image ${article.idArticle}",
                contentScale = imageScale,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .graphicsLayer {
                        if (blurRadius > 0f) {
                            renderEffect = BlurEffect(
                                radiusX = blurRadius,
                                radiusY = blurRadius,
                                edgeTreatment = TileMode.Decal
                            )
                        }
                    }
            ) {
                it.apply {
                    applyImageOptions(article, indexColor, currentQuality) { isFirstResource ->
                        if (isFirstResource && currentQuality < targetQuality) {
                            currentQuality = targetQuality
                        }
                    }
                }
            }
        }

        if (showOverlay) {
            article.getColorIdForIndex(indexColor)?.let { colorId ->
                uiState.colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                    ColorOverlayWithBlur(
                        color = color,
                        cornerRadius = cornerRadius,
                        onClickToOpenWindow = { onClickToOpenWindow(article, indexColor) }
                    )
                }
            }
        }

        produitDepuitNewDATABASE?.let { produit ->
            if (produit.probablementNonDispo) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFA000).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(
                                topStart = cornerRadius,
                                topEnd = cornerRadius
                            )
                        )
                ) {
                    Text(
                        text = "احتمال انو غير متوفر لكن نحاولو نبحثولك عليه",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ColorOverlayWithBlur(
    color: ColorsArticlesTabelle,
    cornerRadius: Dp,
    onClickToOpenWindow: () -> Unit,
) {

    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.White) // White background holder
                .graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = 25f,
                        radiusY = 25f,
                        edgeTreatment = TileMode.Decal
                    )
                }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.Black.copy(alpha = 0.15f))
        )

        ColorOverlay(
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
            onClickToOpenWindow = onClickToOpenWindow
        )
    }
}


// Utility functions
fun checkImageExists(
    viewModel: HeadViewModel,
    article: ArticlesBasesStatsTable,
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

fun ArticlesBasesStatsTable.getColorIdForIndex(index: Int): Long? {
    return when (index) {
        0 -> idcolor1.takeIf { it != 0L }
        1 -> idcolor2.takeIf { it != 0L }
        2 -> idcolor3.takeIf { it != 0L }
        3 -> idcolor4.takeIf { it != 0L }
        else -> null
    }
}

fun countColors(article: ArticlesBasesStatsTable): Int {
    return listOf(
        article.couleur1,
        article.couleur2,
        article.couleur3,
        article.couleur4
    ).count { !it.isNullOrEmpty() }
}

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    maxLines: Int = Int.MAX_VALUE
) {
    var fontSize by remember(text) {
        mutableStateOf(style.fontSize)
    }

    var previousFontSize by remember {
        mutableStateOf(fontSize)
    }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                previousFontSize = fontSize
                fontSize *= 0.9f
            } else if (fontSize != previousFontSize) {
                previousFontSize = fontSize
            }
        }
    )
}


fun RequestBuilder<Drawable>.applyImageOptions(
    article: ArticlesBasesStatsTable,
    indexColor: Int,
    quality: Float,
    onResourceReady: (Boolean) -> Unit
) = this
    .thumbnail(
        this.clone()
            .transform(jp.wasabeef.glide.transformations.BlurTransformation(10))
    )
    .transition(DrawableTransitionOptions.withCrossFade())
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .priority(Priority.HIGH)
    .signature(ObjectKey("${article.idArticle}_${indexColor}_${quality}"))
    .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ) = false

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            onResourceReady(isFirstResource)
            return false
        }
    })


@Composable
fun InfosArticleBottom(
    article: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    uiState: UiState,
    cAfficheurTelephone: Boolean
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = "زبون جديد في طور عرض الخدمة",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        if (cAfficheurTelephone) {

            // Check if monPrixVent is greater than 0
            if (article.monPrixVent > 0) {
                // Row to display both prices side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total price
                    Text(
                        text = remember {
                            val currencyFormat =
                                NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                                    currency = Currency.getInstance("DZD")
                                }
                            currencyFormat.format(article.monPrixVent)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (article.nmbrUnite > 1) {
                        Text("->")
                        Text(
                            text = remember {
                                val currencyFormat =
                                    NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                                        currency = Currency.getInstance("DZD")
                                    }
                                val unitPrice =
                                    article.monPrixVent / article.nmbrUnite

                                currencyFormat.format(unitPrice)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                Text(
                    text = "ان شاء الله نحاولو نديرولك سعر شباب",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = article.nomArticleFinale,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
