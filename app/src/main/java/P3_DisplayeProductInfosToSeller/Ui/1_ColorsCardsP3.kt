package P3_DisplayeProductInfosToSeller.Ui
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
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
