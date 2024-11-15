package P3_DisplayeProductInfosToSeller.Ui
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val colors = listOf(
                    stats.idcolor1,
                    stats.idcolor2,
                    stats.idcolor3,
                    stats.idcolor4
                ).mapNotNull { colorId ->
                    if (colorId != 0L) {
                        colorsArticlesTabelleModel.find { it.idColore == colorId }
                    } else null
                }

                // Main color (color1) displayed in full width
                colors.firstOrNull()?.let { mainColor ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        ColorItemP3(
                            modifier = Modifier.fillMaxSize(),
                            currentSale = currentSale,
                            article = stats,
                            color = mainColor,
                            index = 0,
                            relodeTigger = reloadTrigger,
                            viewModel = viewModel,
                            height = 360.dp
                        )
                    }
                }

                // Other colors in LazyRow (if any)
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
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
                                ColorItemP3(
                                    modifier = Modifier.fillMaxSize(),
                                    currentSale = currentSale,
                                    article = stats,
                                    color = color,
                                    index = colors.indexOf(color),
                                    relodeTigger = reloadTrigger,
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
}

