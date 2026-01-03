package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleImageWithOverlay
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Tex.InfosArticleBottom
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState

@Composable
 fun SmallDisplayerDualColor(
    viewModel: HeadViewModel,
    article: ArticlesBasesStatsTable,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier.Companion,
    imageSize: DpSize,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,
    on_pour_send_data: (String, String) -> Unit,
    expandedColorIndex: Int? = null
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
            imageSize = imageSize,
            viewModelInitApp = viewModelInitApp,
            alwaysShowExpandIcon = true
        , on_pour_send_data = on_pour_send_data
        )

        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = 1,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.Companion.height(100.dp),
            contentScale = ContentScale.Companion.Crop,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize,
            viewModelInitApp = viewModelInitApp,
            alwaysShowExpandIcon = true,
            its_secondary_affiche = true
        , on_pour_send_data = on_pour_send_data
        )

        InfosArticleBottom(
            article = article,
            modifier = Modifier.Companion.padding(horizontal = 3.dp),
            uiState = uiState,
            lockHost
        )
    }
}
