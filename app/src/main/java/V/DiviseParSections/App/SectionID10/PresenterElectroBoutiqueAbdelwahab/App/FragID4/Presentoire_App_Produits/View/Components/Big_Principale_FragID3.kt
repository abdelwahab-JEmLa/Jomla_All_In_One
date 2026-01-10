package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Components

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.ColorImageCard_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.Views.Pricipale_Tariffs_Vendeurs_FragID4
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun Big_Principale_FragID4(
    relative_M1produit: ArticlesBasesStatsTable,
    selectedCouleur: M3CouleurProduitInfos,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    datasValue: List<M13TarificationInfos>,
    isThisProductExpanded: Boolean,
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    ColorImageCard_FragID4(
        relative_M3CouleurProduitInfos = selectedCouleur,
        isSelected = true,
        on_pour_send_data = on_pour_send_data,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Lenceur_Vent_Handler_FragID4(
            relative_M1produit = relative_M1produit,
            relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
            selectedCouleur = selectedCouleur,
            selectedTariff = selectedTariff,
            compactMode = !isThisProductExpanded
        )

        Pricipale_Tariffs_Vendeurs_FragID4(
            relative_M1produit = relative_M1produit,
            tariffsList = datasValue,
            selectedTariff = selectedTariff,
            onTariffSelected = onTariffSelected,
            compactMode = !isThisProductExpanded
        )
    }
}
