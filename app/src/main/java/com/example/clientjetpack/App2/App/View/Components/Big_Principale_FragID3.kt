package com.example.clientjetpack.App2.App.View.Components

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
import com.example.clientjetpack.App2.App.View.ViewS.ColorImageCard_AppEcranPresntoireJemlaCom
import com.example.clientjetpack.App2.App.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_AppEcranPresntoireJemlaCom
import com.example.clientjetpack.App2.App.View.ViewS.Views.Pricipale_Tariffs_Vendeurs_AppEcranPresntoireJemlaCom
import org.koin.compose.koinInject

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun Big_Principale_AppEcranPresntoireJemlaCom(
    relative_M1produit: ArticlesBasesStatsTable,
    selectedCouleur: M3CouleurProduitInfos,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    tariffsList: List<M13TarificationInfos>,
    isThisProductExpanded: Boolean,
    shouldShowButtons: Boolean,
    on_pour_send_data: (String, String) -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    modifier: Modifier = Modifier
) {
    ColorImageCard_AppEcranPresntoireJemlaCom(
        relative_M3CouleurProduitInfos = selectedCouleur,
        isSelected = true,
        on_pour_send_data = on_pour_send_data,
        modifier = Modifier.fillMaxWidth()
    )

    // Reduced spacing from 8.dp to 2.dp for tighter layout
    Spacer(modifier = Modifier.height(2.dp))

    if (shouldShowButtons) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Added to minimize vertical space
                .background(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(4.dp), // Reduced from 8.dp to 4.dp for tighter button group
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced from 4.dp to 2.dp
        ) {
            // FIXED: count_Don_Depot is now automatically tracked inside Lenceur_Vent_Handler_AppEcranPresntoireJemlaCom
            Lenceur_Vent_Handler_AppEcranPresntoireJemlaCom(
                relative_M1produit = relative_M1produit,
                relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
                selectedCouleur = selectedCouleur,
                selectedTariff = selectedTariff,
                compactMode = !isThisProductExpanded,
            )

            Pricipale_Tariffs_Vendeurs_AppEcranPresntoireJemlaCom(
                relative_M1produit = relative_M1produit,
                tariffsList = tariffsList,
                selectedTariff = selectedTariff,
                onTariffSelected = onTariffSelected,
                compactMode = !isThisProductExpanded
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
