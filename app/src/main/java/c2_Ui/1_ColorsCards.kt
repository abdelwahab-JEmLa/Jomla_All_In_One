package c2_Ui

import a1_Models.ProductDisplayController
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import c3_Objects.ImageDisplayerPC
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R

// In ColorsCards.kt
@Composable
fun ColorsCards(
    displayController: ProductDisplayController,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
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

    val selectedColor = colors.find { it.idColore.toInt() == displayController.selectedColorId }

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
            // Main large image - Show selected color or first color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                ColorItem(
                    modifier = Modifier.fillMaxSize(),
                    article = articlesBasesStatsTable,
                    color = selectedColor ?: colors.firstOrNull(),
                    index = if (selectedColor != null) colors.indexOf(selectedColor) else 0,
                    relodeTigger = relodeTigger,
                )
            }

            // Show color variants only if no color is selected
            if (displayController.selectedColorId == 0 && colors.size > 1) {
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
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image
            ImageDisplayerPC(
                modifier = Modifier.fillMaxSize(),
                article = article,
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
