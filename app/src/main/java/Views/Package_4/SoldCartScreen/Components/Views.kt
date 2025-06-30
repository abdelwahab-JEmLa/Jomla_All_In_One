package Views.Package_4.SoldCartScreen.Components

import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_4._SoldCartScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.ViewModel.UiState
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel
import java.io.File

@Composable
fun ImageDisplayer4(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    indexColor: Int = 0,
    reloadKey: Any = Unit,
    uiState: UiState
) {
    val context = LocalContext.current
    val viewModelImagesPath = viewModel.viewModelImagesPath

    val baseImagePath = remember(viewModelImagesPath, article.id, indexColor) {
        File(
            viewModelImagesPath,
            "${article.id}_${if (indexColor == -1) "Unite" else (indexColor + 1)}"
        )
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

    val requestKey = remember(article.id, indexColor, reloadKey) {
        "${article.id}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Background image with reduced opacity when no actual image exists
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageSource)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .setParameter("key", requestKey, memoryCacheKey = requestKey)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "Article image ${article.id} color ${indexColor + 1}",
            modifier = Modifier
                .fillMaxWidth()
                .then(if (imageExist == null) Modifier.alpha(0.7f) else Modifier),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun CartItem(
    soldArticle: SoldArticlesTabelle,
    baseArticle: ArticlesBasesStatsTable,
    colors: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    onOpenArticleStats: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState, viewModelInitApp: ViewModelInitApp, clientBuyerNow: HClientInfos
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Name and Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = baseArticle.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${baseArticle.prixVent} دج",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Color Variants
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Using add list of color quantities with their indices
                val colorQuantities = listOf(
                    soldArticle.color1SoldQuantity,
                    soldArticle.color2SoldQuantity,
                    soldArticle.color3SoldQuantity,
                    soldArticle.color4SoldQuantity
                )

                colorQuantities.forEachIndexed { index, quantity ->
                    if (quantity > 0) {
                        item {
                            ColorItemWithQuantity(
                                article = baseArticle,
                                colorIndex = index,
                                quantity = quantity,
                                onDelete = {
                                    viewModel.resetColorSelectionFromSoldArt(
                                        soldArticle,
                                        index
                                    )
                                },
                                viewModel = viewModel,
                                onOpenArticleStats = onOpenArticleStats,
                                uiState = uiState,
                                colors = colors,
                                viewModelInitApp = viewModelInitApp,
                                clientBuyerNow = clientBuyerNow
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCartMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "سلة المشتريات فارغة",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OrderSuccessMessage() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "تم ارسال الطلب ستصلك الطلبية ان شاء الله قبل 24 ساعة",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ColorItemWithQuantity(
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    quantity: Int,
    onDelete: () -> Unit,
    viewModel: HeadViewModel,
    onOpenArticleStats: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    colors: List<ColorsArticlesTabelle>,
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
    clientBuyerNow: HClientInfos
) {
    ElevatedCard(
        modifier = modifier
            .width(150.dp)
            .height(200.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Delete ButtonAutreEtates
            IconButton(
                onClick = {
                    onDelete()
                    _SoldCartScreen(viewModelInitApp)
                        .onClickOnMain(viewModelInitApp, colorIndex, article, clientBuyerNow)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete color",
                    tint = Color.White
                )
            }

            // Image with Quantity Overlay
            ImageDisplayer4(
                modifier = Modifier.fillMaxSize(),
                article = article,
                viewModel = viewModel,
                indexColor = colorIndex,
                uiState = uiState
            )

            // Color info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Get color name
                val colorId = when (colorIndex) {
                    0 -> article.idcolor1
                    1 -> article.idcolor2
                    2 -> article.idcolor3
                    3 -> article.idcolor4
                    else -> 0L
                }
                val colorName = colors.find { it.idColore == colorId }?.nameColore ?: ""

                Text(
                    text = colorName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "×$quantity",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
