package P7_EStorePresentationToClient.Main

import P7_EStorePresentationToClient.Ui.ColorsCards
import P7_EStorePresentationToClient.Ui.ProductNameSection7
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Models.ProductDisplayController

@Composable
fun FragmentDisplayeInfoProductToClient7(
    displayController: ProductDisplayController,
    articleStatsDataBase: ArticlesBasesStatsTable,
    colorsArticlesList: List<ColorsArticlesTabelle>,
    reloadTrigger: Int,
    modifier: Modifier = Modifier
) {
    var isPickerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isPickerVisible = true
    }

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
                ProductNameSection7(articleStatsDataBase)

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AnimatedVisibility(
                        visible = true,
                        modifier = Modifier.weight(0.8f),
                        enter = fadeIn() + expandVertically()
                    ) {
                        ColorsCards(
                            displayController = displayController,
                            articlesBasesStatsTable = articleStatsDataBase,
                            modifier = Modifier.fillMaxWidth(),
                            relodeTigger = reloadTrigger,
                            colorsArticlesList = colorsArticlesList
                        )
                    }
                }

        }
    }
}

