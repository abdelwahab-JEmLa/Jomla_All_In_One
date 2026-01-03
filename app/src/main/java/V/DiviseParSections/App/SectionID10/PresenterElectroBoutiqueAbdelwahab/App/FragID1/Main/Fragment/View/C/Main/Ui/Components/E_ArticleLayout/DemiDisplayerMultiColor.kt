package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleImageWithOverlay
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Tex.InfosArticleBottom
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.checkImageExists
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.getColorIdForIndex
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState
import org.koin.compose.koinInject

@Composable
 fun DemiDisplayerMultiColor(
    article: ArticlesBasesStatsTable,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier.Companion,
    imageSize: DpSize,
    lockHost: Boolean,
    repo03CouleurProduitInfos: Repo03CouleurProduitInfos = koinInject(),
    viewModelInitApp: ViewModelInitApp,
    expandedColorIndex: Int? = null   ,
    on_pour_send_data: (String, String) -> Unit,
) {
    Column(modifier = modifier.padding(3.dp)) {
        InfosArticleBottom(article, uiState = uiState, cAfficheurTelephone = lockHost)

        val availableColors = (0..3).filter { article.getColorIdForIndex(it) != null }
        val primaryColorIndex = expandedColorIndex ?: 0
        val secondaryColors = availableColors.filter { it != primaryColorIndex }

        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = primaryColorIndex,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = DpSize(500.dp, 500.dp),
            viewModelInitApp = viewModelInitApp,
            alwaysShowExpandIcon = true,
            contentScale = ContentScale.Companion.Fit ,
        on_pour_send_data = on_pour_send_data
        )

        if (secondaryColors.isNotEmpty()) {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                secondaryColors.forEach { index ->
                    val imageExists = remember(article.id, index, reloadTrigger) {
                        checkImageExists(
                            viewModel,
                            article,
                            index,
                            reloadTrigger,
                            repo03CouleurProduitInfos
                        )
                    }

                    ArticleImageWithOverlay(
                        article = article,
                        viewModelHeadViewModel = viewModel,
                        colorIndex = index,
                        reloadTrigger = reloadTrigger,
                        modifier = Modifier.Companion
                            .weight(1f)
                            .height(120.dp),
                        contentScale = ContentScale.Companion.Crop,
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
    }
}
