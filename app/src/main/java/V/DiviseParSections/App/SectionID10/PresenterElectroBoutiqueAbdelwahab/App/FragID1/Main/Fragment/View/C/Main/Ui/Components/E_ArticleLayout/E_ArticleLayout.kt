package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleImageWithOverlay
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Tex.InfosArticleBottom
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.E_AppsOptionsStates.ApplicationEstInstalleDonTelephone.Companion.metricsWidthPixels
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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

     val imageSize: DpSize
        get() = when (this) {
            is DemiUno, DemiDual, is DemiMulti -> DpSize(width = 500.dp, height = 500.dp)
            is SmallUno, is SmallDual, is SmallMulti -> {
                val size = if(metricsWidthPixels > 400) 300.dp else 170.dp
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
        modifier: Modifier = Modifier,
        lockHost: Boolean,
        viewModelInitApp: ViewModelInitApp,
        on_pour_send_data: (String, String) -> Unit,
        expandedColorIndex: Int? = null
    ) {
        when (this) {
            is DemiUno -> SmallSingleColorDisplayer(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex
            , on_pour_send_data = on_pour_send_data
            )
            is DemiDual -> DemiDisplayerDualColor(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex
            , on_pour_send_data = on_pour_send_data
            )
            is DemiMulti -> DemiDisplayerMultiColor(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex
            , on_pour_send_data = on_pour_send_data
            )
            is SmallUno -> DemiSingleColorDisplayer(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex
            , on_pour_send_data = on_pour_send_data
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
                viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex              , on_pour_send_data = on_pour_send_data

            )
            is SmallMulti -> SmallDisplayerMultiColor(
                article, viewModelheadViewModelViewModel, reloadTrigger, onClickToOpenWindos, uiState,
                imageSize = this.imageSize,
                modifier = modifier, lockHost = lockHost, viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex             , on_pour_send_data = on_pour_send_data

            )
        }
    }
}

@Composable
 fun DemiDisplayerDualColor(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier,
    imageSize: DpSize,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,
    on_pour_send_data: (String, String) -> Unit,
    expandedColorIndex: Int? = null
) {
    Column(modifier = modifier.padding(3.dp)) {
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone = lockHost)

        val primaryColorIndex = expandedColorIndex ?: 0
        val secondaryColorIndex = if (primaryColorIndex == 0) 1 else 0

        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = primaryColorIndex,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = DpSize(500.dp, 500.dp),
            viewModelInitApp = viewModelInitApp,
            alwaysShowExpandIcon = true,
            contentScale = ContentScale.Fit
        , on_pour_send_data = on_pour_send_data
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            ArticleImageWithOverlay(
                article = article,
                viewModelHeadViewModel = viewModel,
                colorIndex = secondaryColorIndex,
                reloadTrigger = reloadTrigger,
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                contentScale = ContentScale.Crop,
                onClickToOpenWindow = onClickToOpenWindos,
                imageSize = DpSize(150.dp, 300.dp),
                viewModelInitApp = viewModelInitApp,
                alwaysShowExpandIcon = true,
                its_secondary_affiche = true
            , on_pour_send_data = on_pour_send_data
            )
        }
    }
}

@Composable
 fun SmallSingleColorDisplayer(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier,
    imageSize: DpSize,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,

    on_pour_send_data: (String, String) -> Unit,
    expandedColorIndex: Int? = null
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
                imageSize = imageSize,
                viewModelInitApp = viewModelInitApp,
                alwaysShowExpandIcon = true
            , on_pour_send_data = on_pour_send_data

            // FIXED: Changed from false to true
            )
        }
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone = lockHost)
    }
}

@Composable
 fun DemiSingleColorDisplayer(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier,
    imageSize: DpSize,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,

    on_pour_send_data: (String, String) -> Unit,
    expandedColorIndex: Int? = null
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
                imageSize = imageSize,
                viewModelInitApp = viewModelInitApp,
                alwaysShowExpandIcon = true

            , on_pour_send_data = on_pour_send_data
            // FIXED: Changed from false to true
            )
        }
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone = lockHost)
    }
}
