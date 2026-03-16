package Application4.App.Fragment.View.Components

import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Application4.App.Fragment.View.ViewS.ColorImageCard_FragID3
import Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID3
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubColorCard_WithButton(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: M01Produit,
    selectedTariff: M13TarificationInfos,
    on_pour_send_data: (String, String) -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    shouldShowButtons: Boolean,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {
    val uiState = uiState_NewProtoPatterns_viewModel.first

    val colorOperation by remember(
        couleur.keyID,
        uiState.active_Central_Values.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent?.size
    ) {
        derivedStateOf {
            uiState.active_Central_Values.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                ?.find { it.parent_M3CouleurProduit_KeyID == couleur.keyID }
        }
    }

    Column(modifier = modifier
    ) {
        ColorImageCard_FragID3(
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_M3CouleurProduitInfos = couleur,
            isSelected = false,
            on_pour_send_data = on_pour_send_data,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 80.dp else 40.dp),
        )

        shouldShowButtons.ifTrue {
            Lenceur_Vent_Handler_FragID3(
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                relative_M1produit = relative_M1produit,
                selectedCouleur = couleur,
                selectedTariff = selectedTariff,
                compactMode = !isExpanded,
                modifier = Modifier.fillMaxWidth(),
                isWifiClientConnected = shouldShowButtons
            )
        }
    }
}
