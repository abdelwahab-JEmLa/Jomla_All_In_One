package P7_EStorePresentationToClient.Main

import com.example.clientjetpack.Models.ProductDisplayController
import P7_EStorePresentationToClient.Ui.ColorsCards
import P7_EStorePresentationToClient.Ui.CompactQuantityPickerPC
import P7_EStorePresentationToClient.Ui.ProductNameSection
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun WindowsPresentationInfoProduct(
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
                ProductNameSection(articleStatsDataBase)

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

                    AnimatedVisibility(
                        visible = displayController.windowsSelectedColorId > 0,
                        modifier = Modifier.weight(0.2f),
                        enter = slideInHorizontally(),
                        exit = slideOutHorizontally()
                    ) {
                        CompactQuantityPickerPC(
                            initialQuantity = displayController.windowsPickerDisplayedQuantity,
                            height = 600.dp
                        )
                    }
                }
            }
        }
    }
}

