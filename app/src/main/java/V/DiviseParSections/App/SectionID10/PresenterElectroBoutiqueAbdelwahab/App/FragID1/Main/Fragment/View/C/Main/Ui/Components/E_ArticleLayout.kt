package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleImageWithOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
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
import com.example.clientjetpack.ViewModel.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel

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
        viewModel: HeadViewModel,
        reloadTrigger: Int,
        onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
        uiState: UiState,
        modifier: Modifier = Modifier, lockHost: Boolean, viewModelInitApp: ViewModelInitApp,
    ) {
        when (this) {
            is DemiUno -> SmallSingleColorDisplayer(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is DemiDual -> DemiDisplayerDualColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is DemiMulti -> DemiDisplayerMultiColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is SmallUno -> DemiSingleColorDisplayer(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is SmallDual -> SmallDisplayerDualColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
            is SmallMulti -> SmallDisplayerMultiColor(
                article, viewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp
            )
        }
    }
}

// Add new layout components
@Composable
private fun SmallDisplayerDualColor(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
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
            article = article,
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState, imageSize = imageSize, viewModelInitApp = viewModelInitApp
        )

        ArticleImageWithOverlay(
            article = article,
            viewModel = viewModel,
            colorIndex = 1,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.height(100.dp),
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState,
            contentScale = ContentScale.Crop, imageSize = imageSize, viewModelInitApp = viewModelInitApp
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
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState,
            imageSize = imageSize ,
            qualityImagePourcentage= 48, viewModelInitApp = viewModelInitApp
        )

        // Replace LazyColumn with Column since we have a small fixed number of items
        val availableColors = (1..3).filter { article.getColorIdForIndex(it) != null }
        availableColors.forEach { index ->
            ArticleImageWithOverlay(
                article = article,
                viewModel = viewModel,
                colorIndex = index,
                reloadTrigger = reloadTrigger,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState,
                contentScale = ContentScale.Crop,
                imageSize = imageSize, viewModelInitApp = viewModelInitApp
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
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState, imageSize = imageSize, viewModelInitApp = viewModelInitApp
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
                    viewModel = viewModel,
                    colorIndex = index,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier
                        .width(250.dp)
                        .height(if (!imageExists) 70.dp else 250.dp),
                    contentScale = if (!imageExists) ContentScale.Crop else ContentScale.Fit,
                    onClickToOpenWindow = onClickToOpenWindos,
                    uiState = uiState, imageSize = imageSize, viewModelInitApp = viewModelInitApp
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
            viewModel = viewModel,
            colorIndex = 0,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            uiState = uiState, imageSize = imageSize, viewModelInitApp = viewModelInitApp
        )

        Box(modifier = Modifier.height(100.dp)) {
            ArticleImageWithOverlay(
                article = article,
                viewModel = viewModel,
                colorIndex = 1,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState,
                contentScale = ContentScale.Crop, imageSize = imageSize, viewModelInitApp = viewModelInitApp
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
                viewModel = viewModel,
                colorIndex = 0,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState, imageSize = imageSize, viewModelInitApp = viewModelInitApp
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
                viewModel = viewModel,
                colorIndex = 0,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindow = onClickToOpenWindos,
                uiState = uiState, imageSize = imageSize, viewModelInitApp = viewModelInitApp
            )
        }
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone=lockHost)
    }
}
