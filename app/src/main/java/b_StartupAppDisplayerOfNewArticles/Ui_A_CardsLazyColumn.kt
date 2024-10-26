package b_StartupAppDisplayerOfNewArticles

import a_RoomDB.ArticlesBasesStatsModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import c_WindosBuyAndDesplayeArticleStats.DisplayeImageECB

@Composable
fun ArticleItemECB(
    article: ArticlesBasesStatsModel,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { viewModel.updateCurrentArticle(article) },
                contentAlignment = Alignment.Center
            ) {
                DisplayeImageECB(
                    viewModel = viewModel,
                    article = article,
                    index = 0,
                    reloadKey = reloadTrigger
                )
            }
        }
    }
}
