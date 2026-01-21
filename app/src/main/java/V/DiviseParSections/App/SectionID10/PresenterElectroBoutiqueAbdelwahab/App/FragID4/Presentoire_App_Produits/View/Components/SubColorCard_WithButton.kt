package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Components

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.ColorImageCard_FragID4
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
fun SubColorCard_WithButton_FragId4(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    selectedTariff: M13TarificationInfos,  // FIXED: Renamed parameter to match usage
    focusedValuesGetter: FocusedValuesGetter,
    on_pour_send_data: (String, String) -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
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

    Column(modifier = modifier) {
        // Color image card
        ColorImageCard_FragID4(
            relative_M3CouleurProduitInfos = couleur,
            isSelected = false, // Sub-colors are never "selected"
            on_pour_send_data = on_pour_send_data,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 80.dp else 40.dp)
        )
        focusedValuesGetter.active_Central_Values.hide_prix_lence_vent_buttons.ifFalse {
            Lenceur_Vent_Handler_FragID3(
                relative_M1produit = relative_M1produit,
                relative_M10OperationVentCouleur = colorOperation,
                selectedCouleur = couleur,
                selectedTariff = selectedTariff,  // FIXED: Now matches the parameter name
                compactMode = !isExpanded,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
