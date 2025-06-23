package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Sec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.ImageDisplayerProtoAvantJuin3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.checkImageExists
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.countColors
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArticleItem(
    viewModel: Sec10Frag1ViewModel,
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
                viewModel.addNewDAchatCouleurOperationEtOuvreLe(
                    article, indexCouleur
                )
            },
            uiState = uiState,
            lockHost = lockHost,
            viewModelInitApp = viewModelInitApp
        )
    }
}

@Composable
fun ArticleImageWithOverlay(
    viewModel: Sec10Frag1ViewModel = koinViewModel(),
    viewModelHeadViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit,
    imageSize: DpSize,
    qualityImagePourcentage: Int = 100
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    onClickToOpenWindow(article, colorIndex)

                }
                .fillMaxSize()
        ) {
            val imageExists = remember(article.id, colorIndex, reloadTrigger) {
                checkImageExists(viewModelHeadViewModel, article, colorIndex, reloadTrigger)
            }

            ImageDisplayerProtoAvantJuin3(
                viewModel = viewModelHeadViewModel,
                article = article,
                indexColor = colorIndex,
                reloadKey = reloadTrigger,
                showOverlay = !imageExists,
                imageScale = contentScale,
                imageSize = imageSize,
                finalequalityImagePourcentage = qualityImagePourcentage,
                viewModelInitApp = viewModelInitApp,
            ){prd,indexCouleur->
                viewModel.aCentralDatasHandlerProtoJuin9
                    .ouvreAddDataDepuitIndexCouleur(indexCouleur)
            }
        }
    }
}
