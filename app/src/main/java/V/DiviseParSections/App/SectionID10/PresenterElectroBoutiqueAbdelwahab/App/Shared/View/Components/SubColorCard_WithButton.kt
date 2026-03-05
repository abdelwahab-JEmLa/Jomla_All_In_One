package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.Components

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.UiState
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.ViewModel_NewProtoPatterns
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.ViewS.ColorImageCard_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID3
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun SubColorCard_WithButton(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: M01Produit,
    selectedTariff: M13TarificationInfos,
    on_pour_send_data: (String, String) -> Unit,
    isExpanded: Boolean,
    wifiTransferDatas: WifiTransferDatas = koinInject(),
    modifier: Modifier = Modifier,
    headViewModel: HeadViewModel = koinInject(),
    shouldShowButtons: Boolean,
    uiState_viewModel: Pair<UiState, ViewModel_NewProtoPatterns>
) {
    val uiState_headViewModel by headViewModel.uiState.collectAsState()
    val uiState = uiState_viewModel.first

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
        .semantics(mergeDescendants = true) {
            set(
                value = uiState_headViewModel.productDisplayController.isHostPhone,
                key = SemanticsPropertyKey("isHostPhone")
            )
        }
        .semantics(mergeDescendants = true) {
            set(
                value = uiState_headViewModel.productDisplayController.isConnected,
                key = SemanticsPropertyKey(".isConnected")
            )
        }
        .semantics(mergeDescendants = true) {
            set(
                value = wifiTransferDatas.connectionUiState.value.isConnected,
                key = SemanticsPropertyKey(".wifiTransferDatas")
            )
        }
    ) {
        ColorImageCard_FragID3(
            uiState_viewModel=uiState_viewModel,
            relative_M3CouleurProduitInfos = couleur,
            isSelected = false,
            on_pour_send_data = on_pour_send_data,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 80.dp else 40.dp),
        )

        shouldShowButtons.ifTrue {
            Lenceur_Vent_Handler_FragID3(
                uiState_viewModel=uiState_viewModel,
                isWifiClientConnected = shouldShowButtons,
                relative_M1produit = relative_M1produit,
                relative_M10OperationVentCouleur = colorOperation,
                selectedCouleur = couleur,
                selectedTariff = selectedTariff,
                compactMode = !isExpanded,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
