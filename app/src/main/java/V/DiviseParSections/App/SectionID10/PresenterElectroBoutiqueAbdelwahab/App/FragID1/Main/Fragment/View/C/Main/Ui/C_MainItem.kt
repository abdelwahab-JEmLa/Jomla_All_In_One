package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.countColors
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
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
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    isExpanded: Boolean = false,
    expandedElevation: Dp = 4.dp
) {
    val colorCount = countColors(article)

    val cardColor = when {
        uiState.productDisplayController.isHostPhone && isFirstVisible -> {
            Color.Red
        }
        isExpanded -> {
            MaterialTheme.colorScheme.primaryContainer // Highlight when expanded
        }
        else -> {
            MaterialTheme.colorScheme.surface
        }
    }

    // Animate scale when expanded
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        elevation = CardDefaults.cardElevation(defaultElevation = expandedElevation),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        // Use different layout when expanded
        val layout = when {
            isExpanded && colorCount > 1 -> E_ArticleLayout.DemiMulti
            isExpanded && colorCount == 1 -> E_ArticleLayout.DemiUno
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
