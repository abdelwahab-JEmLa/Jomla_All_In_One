package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.countColors
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState

@Composable
fun ArticleItem(
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    viewModelheadViewModelViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    article: ArticlesBasesStatsTable,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    uiState: UiState,
    isFirstVisible: Boolean = false,
    lockHost: Boolean,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit
) {
    val colorCount = countColors(article)

    val cardColor = when {
        uiState.productDisplayController.isHostPhone && isFirstVisible -> {
            Color.Red
        }

        else -> {
            MaterialTheme.colorScheme.surface
        }
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        val layout = when {
            colorCount == 1 -> E_ArticleLayout.SmallUno
            colorCount == 2 -> E_ArticleLayout.SmallDual
            colorCount > 2 -> E_ArticleLayout.SmallMulti
            else -> E_ArticleLayout.SmallUno
        }

        layout.Content(
            article = article,
            viewModelheadViewModelViewModel = viewModelheadViewModelViewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = { article, indexCouleur ->
                onClickToOpenWindos(article, indexCouleur)
            },
            uiState = uiState,
            lockHost = lockHost,
            viewModelInitApp = viewModelInitApp
        )
    }
}

