package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.Components

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.ColorImageCard_FragID4
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID4
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A card component for displaying sub-colors (non-primary colors) with a button below.
 *
 * This component stacks a color image card with a sales button directly underneath it.
 * The button is positioned below the image in a vertical column layout.
 *
 * FIXED: Added shouldShowButtons parameter to control visibility of sales controls
 * This ensures clients only see the color image, not the interactive sales buttons.
 *
 * @param couleur The color product information to display
 * @param relative_M1produit The parent product information
 * @param selectedTariff The pricing tariff to use for sales
 * @param focusedValuesGetter Getter for focused/active values from repository
 * @param on_pour_send_data Callback for sending data updates
 * @param isExpanded Whether the parent product is in expanded state
 * @param shouldShowButtons Whether to show interactive sales controls (host only)
 * @param modifier Modifier for customizing the component
 */
@Composable
fun SubColorCard_WithButton_FragId4(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    selectedTariff: M13TarificationInfos,
    focusedValuesGetter: FocusedValuesGetter,
    on_pour_send_data: (String, String) -> Unit,
    isExpanded: Boolean,
    shouldShowButtons: Boolean = true, // FIXED: Added parameter with default value
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
        // Color image card - always visible to both host and client
        ColorImageCard_FragID4(
            relative_M3CouleurProduitInfos = couleur,
            isSelected = false, // Sub-colors are never "selected"
            on_pour_send_data = on_pour_send_data,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 80.dp else 40.dp)
        )

        // FIXED: Only show sales button to host phone (not clients)
        // This prevents clients from seeing interactive sales controls
        if (shouldShowButtons) {
            Spacer(modifier = Modifier.height(4.dp))

            // Sales button directly below the image
            Lenceur_Vent_Handler_FragID4(
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
