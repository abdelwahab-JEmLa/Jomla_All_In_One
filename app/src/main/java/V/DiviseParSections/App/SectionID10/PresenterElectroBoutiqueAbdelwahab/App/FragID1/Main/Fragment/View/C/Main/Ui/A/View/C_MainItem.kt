package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.A_Expanded_Produit_Ac_Multi_Couleurs
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout.E_ArticleLayout
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import V.DiviseParSections.App.Shared.ViewModel.UiState
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ArticleItem(
    relative_M1produit: M01Produit,
    repositorysMainGetter: RepositorysMainGetter= koinInject(),
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    viewModelheadViewModelViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    uiState: UiState,
    isFirstVisible: Boolean = false,
    lockHost: Boolean,
    onClickToOpenWindos: (M01Produit, Int) -> Unit,
    isExpanded: Boolean = false,
    expandedElevation: Dp = 4.dp,
    
    expandedColorIndex: Int? = null
) {

    val colorCount = repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
        .size

    val cardColor = when {
        uiState.productDisplayController.isHostPhone && isFirstVisible -> {
            Color.Red
        }
        isExpanded -> {
            MaterialTheme.colorScheme.primaryContainer
        }
        else -> {
            MaterialTheme.colorScheme.surface
        }
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = expandedElevation),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        if (isExpanded && colorCount > 2) {
            A_Expanded_Produit_Ac_Multi_Couleurs(
                relative_M1produit = relative_M1produit,
                repositorysMainGetter = repositorysMainGetter ,
            )
        } else {
            val layout = when {
                isExpanded && colorCount == 2 -> E_ArticleLayout.DemiDual
                isExpanded && colorCount == 1 -> E_ArticleLayout.DemiUno
                colorCount == 1 -> E_ArticleLayout.SmallUno
                colorCount == 2 -> E_ArticleLayout.SmallDual
                colorCount > 2 -> E_ArticleLayout.SmallMulti
                else -> E_ArticleLayout.SmallUno
            }

            layout.Content(
                article = relative_M1produit,
                viewModelheadViewModelViewModel = viewModelheadViewModelViewModel,
                reloadTrigger = reloadTrigger,
                onClickToOpenWindos = { article, indexCouleur ->
                    onClickToOpenWindos(article, indexCouleur)
                },
                uiState = uiState,
                lockHost = lockHost,
                viewModelInitApp = viewModelInitApp,
                expandedColorIndex = expandedColorIndex
            , 
            )
        }
    }
}
