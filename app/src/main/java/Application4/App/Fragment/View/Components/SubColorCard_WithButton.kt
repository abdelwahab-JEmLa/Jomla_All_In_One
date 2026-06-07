package Application4.App.Fragment.View.Components

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import Application4.App.Fragment.View.Components.A_Header.View.ColorImageCard_FragID3
import Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_App4
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubColorCard_WithButton(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: M01Produit,
    selectedTariff: M13TarificationInfos,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    shouldShowButtons: Boolean,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel

    Column(modifier = modifier
    ) {
        ColorImageCard_FragID3(
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_M3CouleurProduitInfos = couleur,
            isSelected = false,
            
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 80.dp else 40.dp),
        )

        shouldShowButtons.ifTrue {
            Lenceur_Vent_Handler_App4(
                modifier = Modifier.fillMaxWidth(),
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                relative_M1produit = relative_M1produit,
                selectedCouleur = couleur,
                selectedTariff = selectedTariff,
                compactMode = !isExpanded,
                listM10OperationVentCouleur_FilteredBy_activeM8BonVent =
                    viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
            )
        }
    }
}
