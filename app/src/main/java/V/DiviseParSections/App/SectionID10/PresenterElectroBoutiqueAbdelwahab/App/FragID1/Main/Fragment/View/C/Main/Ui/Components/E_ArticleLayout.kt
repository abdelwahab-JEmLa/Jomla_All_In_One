package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleImageWithOverlay
import Z_CodePartageEntreApps.Model.E_AppsOptionsStates.ApplicationEstInstalleDonTelephone.Companion.metricsWidthPixels
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState

sealed class E_ArticleLayout {
    data object DemiUno : E_ArticleLayout()
    data object DemiDual : E_ArticleLayout()
    data object DemiMulti : E_ArticleLayout()
    data object SmallUno : E_ArticleLayout()
    data object SmallDual : E_ArticleLayout()
    data object SmallMulti : E_ArticleLayout()

    // Define size configurations for different layouts
    private val imageSize: DpSize
        get() = when (this) {
            is DemiUno, DemiDual, is DemiMulti -> DpSize(width = 500.dp, height = 500.dp)
            is SmallUno, is SmallDual, is SmallMulti -> {

                val size =if( metricsWidthPixels > 400) 300.dp  else 170.dp

                DpSize(width = size, height = size)
            }
        }

    @Composable
    fun Content(
        article: ArticlesBasesStatsTable,
        viewModelheadViewModelViewModel: HeadViewModel,
        reloadTrigger: Int,
        onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
        uiState: UiState,
        modifier: Modifier = Modifier, lockHost: Boolean, viewModelInitApp: ViewModelInitApp,
    ) {
        when (this) {
            is DemiUno -> SmallSingleColorDisplayer(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is DemiDual -> DemiDisplayerDualColor(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is DemiMulti -> DemiDisplayerMultiColor(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is SmallUno -> DemiSingleColorDisplayer(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is SmallDual -> SmallDisplayerDualColor(
                viewModelheadViewModelViewModel,
                article,
                reloadTrigger,
                onClickToOpenWindos,
                uiState,
                modifier = modifier,
                imageSize = this.imageSize,
                lockHost = lockHost,
                viewModelInitApp = viewModelInitApp
            )
            is SmallMulti -> SmallDisplayerMultiColor(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
        }
    }
}

// Add new layout components
@Composable
private fun SmallDisplayerDualColor(
    viewModel: HeadViewModel,
    article: ArticlesBasesStatsTable,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier, imageSize: DpSize, lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    Column(
        modifier = modifier.padding(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        ArticleImageWithOverlay(
            viewModelHeadViewModel = viewModel,
            article = article,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize, viewModelInitApp = viewModelInitApp
        )

        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = 1,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.height(100.dp),
            contentScale = ContentScale.Crop,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize, viewModelInitApp = viewModelInitApp
        )

        InfosArticleBottom(
            article = article,
            modifier = Modifier.padding(horizontal = 3.dp),
            uiState=uiState,
            lockHost
        )
    }
}

@Composable
private fun SmallDisplayerMultiColor(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier,
    imageSize: DpSize, lockHost: Boolean, viewModelInitApp: ViewModelInitApp
) {
    Column(
        modifier = modifier.padding(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Main image
        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize,
            qualityImagePourcentage= 48,
            viewModelInitApp = viewModelInitApp
        )

        // Replace LazyColumn with Column since we have add small fixed number of items
        val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
        availableColors.forEach { index ->
            ArticleImageWithOverlay(
                article = article,
                viewModelHeadViewModel = viewModel,
                colorIndex = index,
                reloadTrigger = reloadTrigger,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentScale = ContentScale.Crop,
                onClickToOpenWindow = onClickToOpenWindos,
                imageSize = imageSize,
                viewModelInitApp = viewModelInitApp
            )
        }

        // Details
        InfosArticleBottom(
            article = article,
            modifier = Modifier.padding(horizontal = 3.dp),
            uiState = uiState,
            lockHost
        )
    }
}

@Composable
private fun DemiDisplayerMultiColor(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier, imageSize: DpSize, lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    Column(modifier = modifier.padding(3.dp)) {
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone=lockHost)

        // Main image display
        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize, viewModelInitApp = viewModelInitApp
        )

        // Secondary images row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
            items(availableColors) { index ->
                val imageExists = remember(article.id, index, reloadTrigger) {
                    checkImageExists(viewModel, article, index, reloadTrigger)
                }

                ArticleImageWithOverlay(
                    article = article,
                    viewModelHeadViewModel = viewModel,
                    colorIndex = index,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier
                        .width(250.dp)
                        .height(if (!imageExists) 70.dp else 250.dp),
                    contentScale = if (!imageExists) ContentScale.Crop else ContentScale.Fit,
                    onClickToOpenWindow = onClickToOpenWindos,
                    imageSize = imageSize, viewModelInitApp = viewModelInitApp
                )
            }
        }
    }
}


@Composable
private fun DemiDisplayerDualColor(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier, imageSize: DpSize, lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    Column(modifier = modifier.padding(3.dp)) {
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone=lockHost)
        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize, viewModelInitApp = viewModelInitApp
        )

        Box(modifier = Modifier.height(100.dp)) {
            ArticleImageWithOverlay(
                article = article,
                viewModelHeadViewModel = viewModel,
                colorIndex = 1,
                reloadTrigger = reloadTrigger,
                contentScale = ContentScale.Crop,
                onClickToOpenWindow = onClickToOpenWindos,
                imageSize = imageSize, viewModelInitApp = viewModelInitApp
            )
        }
    }
}
@Composable
private fun SmallSingleColorDisplayer(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier, imageSize: DpSize, lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    Column(modifier = modifier.padding(3.dp)) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            ArticleImageWithOverlay(
                article = article,
                viewModelHeadViewModel = viewModel,
                colorIndex = 0,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                imageSize = imageSize, viewModelInitApp = viewModelInitApp
            )
        }
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone=lockHost)
    }
}
@Composable
private fun DemiSingleColorDisplayer(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier, imageSize: DpSize, lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    Column(modifier = modifier.padding(3.dp)) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            ArticleImageWithOverlay(
                article = article,
                viewModelHeadViewModel = viewModel,
                colorIndex = 0,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                imageSize = imageSize, viewModelInitApp = viewModelInitApp
            )
        }
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone=lockHost)
    }
}
