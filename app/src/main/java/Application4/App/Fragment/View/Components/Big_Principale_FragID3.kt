package Application4.App.Fragment.View.Components

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.View.Components.A_Header.View.ColorImageCard_FragID3
import Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_App4
import Application4.App.Fragment.View.ViewS.Views.Pricipale_Tariffs_Vendeurs_FragID3
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun Big_Principale_FragID3(
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    relative_M1produit: M01Produit,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    tariffsList: List<M13TarificationInfos>,
    isThisProductExpanded: Boolean,
    shouldShowButtons: Boolean,
    on_pour_send_data: (String, String) -> Unit
) {
    ColorImageCard_FragID3(
        relative_M3CouleurProduitInfos = selectedCouleur,
        isSelected = true,
        on_pour_send_data = on_pour_send_data,
        modifier = Modifier.fillMaxWidth(),uiState_NewProtoPatterns_viewModel=uiState_NewProtoPatterns_viewModel
    )

    Spacer(modifier = Modifier.height(2.dp))

    if (shouldShowButtons) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Lenceur_Vent_Handler_App4(
                uiState_NewProtoPatterns_viewModel=uiState_NewProtoPatterns_viewModel,
                relative_M1produit = relative_M1produit,
                selectedCouleur = selectedCouleur,
                selectedTariff = selectedTariff,
                compactMode = !isThisProductExpanded,
            )

            Pricipale_Tariffs_Vendeurs_FragID3(
                uiState_NewProtoPatterns_viewModel=uiState_NewProtoPatterns_viewModel,
                relative_M1produit = relative_M1produit,
                tariffsList = tariffsList,
                selectedTariff = selectedTariff,
                onTariffSelected = onTariffSelected,
                compactMode = !isThisProductExpanded,
            )
        }
    }
}

/**
 * Helper function to save edited progressive tariff
 */
fun saveEditedProgressiveTariff(
    aCentralFacade: ACentralFacade,
    tariff: M13TarificationInfos,
    newPrice: Double
) {
    val updatedTariff = tariff.copy(
        prixCurrency = newPrice,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )

    aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
}
