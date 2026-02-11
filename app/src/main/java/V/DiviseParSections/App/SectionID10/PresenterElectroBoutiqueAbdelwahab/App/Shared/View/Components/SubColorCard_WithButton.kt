package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.Components

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.ViewS.ColorImageCard_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.compose.koinInject

/**
 * A card component for displaying sub-colors (non-primary colors) with a button below.
 *
 * This component stacks a color image card with a sales button directly underneath it.
 * The button is positioned below the image in a vertical column layout.
 *
 * @param couleur The color product information to display
 * @param relative_M1produit The parent product information
 * @param selectedTariff The pricing tariff to use for sales
 * @param focusedValuesGetter Getter for focused/active values from repository
 * @param on_pour_send_data Callback for sending data updates
 * @param isExpanded Whether the parent product is in expanded state
 * @param modifier Modifier for customizing the component
 */
@Composable
fun SubColorCard_WithButton(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    selectedTariff: M13TarificationInfos,
    focusedValuesGetter: FocusedValuesGetter,
    on_pour_send_data: (String, String) -> Unit,
    isExpanded: Boolean,
    wifiTransferDatas: WifiTransferDatas = koinInject(),
    modifier: Modifier = Modifier,
    headViewModel: HeadViewModel = koinInject(),
    shouldShowButtons: Boolean
) {
    val uiState by headViewModel.uiState.collectAsState()
    // Find the sales operation for this specific color
    // This is recalculated whenever the color changes or the sales list is updated
    val colorOperation by remember(
        couleur.keyID,
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.size
    ) {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M3CouleurProduit_KeyID == couleur.keyID }
        }
    }

    Column(modifier = modifier
        .semantics(mergeDescendants = true) {
            set(
                value = uiState.productDisplayController.isHostPhone,
                key = SemanticsPropertyKey("isHostPhone")
            )
        }
        .semantics(mergeDescendants = true) {
            set(value =
            uiState.productDisplayController.isConnected, key = SemanticsPropertyKey(".isConnected"))
        }
        .semantics(mergeDescendants = true) {
            set(value =
                wifiTransferDatas.connectionUiState.value.isConnected, key = SemanticsPropertyKey(".wifiTransferDatas"))
        }
    ) {
        // Color image card
        ColorImageCard_FragID3(
            relative_M3CouleurProduitInfos = couleur,
            isSelected = false, // Sub-colors are never "selected"
            on_pour_send_data = on_pour_send_data,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 80.dp else 40.dp)
        )

        shouldShowButtons.ifTrue  {
            Lenceur_Vent_Handler_FragID3(
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
