package P3_DisplayeProductInfosToSeller.Ui
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel

@Composable
 fun ColorSelectionSection(
    currentSale: SoldArticlesTabelle,
    stats: ArticlesBasesStatsTable,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    reloadTrigger: Int
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically()
    ) {
        ColorsCardsP3(
            currentSale = currentSale,
            articlesBasesStatsTable = stats,
            colorsArticlesTabelleModel = colorsArticlesTabelleModel,
            viewModel = viewModel,
            relodeTigger = reloadTrigger,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
@Composable
fun ColorsCardsP3(
    currentSale: SoldArticlesTabelle?,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
) {
    val colors = listOf(
        articlesBasesStatsTable.idcolor1,
        articlesBasesStatsTable.idcolor2,
        articlesBasesStatsTable.idcolor3,
        articlesBasesStatsTable.idcolor4
    ).mapNotNull { colorId ->
        if (colorId != 0L) {
            colorsArticlesTabelleModel.find { it.idColore == colorId }
        } else null
    }
    Box(
        LazyRow(
        modifier = modifier
            .fillMaxWidth()
    ) {

        when (colors.size) {
            1 -> SingleColorLayout(
                currentSale=currentSale,
                article = articlesBasesStatsTable,
                color = colors[0],
                viewModel = viewModel,
                relodeTigger = relodeTigger,
            )
            else -> MultipleColorsLayout(
                currentSale=currentSale,
                article = articlesBasesStatsTable,
                colors = colors,
                viewModel = viewModel,
                relodeTigger = relodeTigger,
            )
        }
    }
        //TODO utilise ce ui on comments  au liex SingleColorLayout et multiple l pour affiche on conservenet le fuctionement originale
       // Card(
    //        modifier = modifier
    //            .fillMaxWidth()
    //            .padding(8.dp),
    //        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    //    ) {
    //        Column(
    //            modifier = Modifier
    //                .fillMaxWidth()
    //                .padding(8.dp),
    //            verticalArrangement = Arrangement.spacedBy(8.dp)
    //        ) {
    //
    //
    //            // Show color variants only if no color is selected
    //            if (displayController.windowsSelectedColorId == 0 && colors.size > 1) {
    //                LazyRow(
    //                    modifier = Modifier.fillMaxWidth(),
    //                    horizontalArrangement = Arrangement.spacedBy(8.dp),
    //                    contentPadding = PaddingValues(horizontal = 8.dp)
    //                ) {
    //                    items(colors.drop(1)) { color ->
    //                        Box(
    //                            modifier = Modifier
    //                                .size(200.dp)
    //                                .clip(RoundedCornerShape(8.dp))
    //                        ) {
    //                            ColorItem(
    //                                modifier = Modifier.fillMaxSize(),
    //                                article = articlesBasesStatsTable,
    //                                color = color,
    //                                index = colors.indexOf(color),
    //                                relodeTigger = relodeTigger,
    //                            )
    //                        }
    //                    }
    //                }
    //            }
    //            // Main large image - Show selected color or first color
    //            Box(
    //                modifier = Modifier
    //                    .fillMaxWidth()
    //                    .height(360.dp)
    //            ) {
    //                ColorItem(
    //                    modifier = Modifier.fillMaxSize(),
    //                    article = articlesBasesStatsTable,
    //                    color = selectedColor ?: colors.firstOrNull(),
    //                    index = if (selectedColor != null) colors.indexOf(selectedColor) else 0,
    //                    relodeTigger = relodeTigger,
    //                )
    //            }
    //        }
    //    }
    //
}

@Composable
private fun SingleColorLayout(
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle,
    viewModel: HeadViewModel,
    relodeTigger: Int,
) {
    val height = 250.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        ColorItemP3(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            currentSale = currentSale,
            article = article,
            color = color,
            index = 0,
            relodeTigger = relodeTigger,
            viewModel = viewModel,
            height = height,
        )
    }
}

@Composable
private fun MultipleColorsLayout(
    currentSale: SoldArticlesTabelle?,
    article: ArticlesBasesStatsTable,
    colors: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    relodeTigger: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        colors.chunked(2).forEach { rowColors ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(rowColors) { color ->
                    val index = colors.indexOf(color)
                    val height = 250.dp
                    Box(
                        modifier = Modifier
                            .size(height)
                    ) {
                        ColorItemP3(
                            modifier = Modifier.fillMaxSize(),
                            currentSale=currentSale,
                            article = article,
                            color = color,
                            index = index,
                            relodeTigger = relodeTigger,
                            viewModel = viewModel,
                            height=height
                        )
                    }
                }
            }
        }
    }
}
