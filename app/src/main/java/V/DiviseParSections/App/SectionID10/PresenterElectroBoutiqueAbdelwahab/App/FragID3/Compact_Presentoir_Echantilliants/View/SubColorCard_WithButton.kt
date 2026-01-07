package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.ColorImageCard_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubColorCard_WithButton(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    finale_Tariff: M13TarificationInfos,
    focusedValuesGetter: FocusedValuesGetter,
    onClick: () -> Unit,
    on_pour_send_data: (String, String) -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier.Companion
) {
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
        // Color image card with overlaid button - FIXED: Added fixed height
        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(40.dp)
        ) {
            ColorImageCard_FragID3(
                relative_M3CouleurProduitInfos = couleur,
                isSelected = false,
                onIconClick = onClick,
                on_pour_send_data = on_pour_send_data,
                modifier = Modifier.Companion.fillMaxWidth()
            )

            // Lenceur button overlaid at the bottom of the image
            Lenceur_Vent_Handler_FragID3(
                relative_M1produit = relative_M1produit,
                relative_M10OperationVentCouleur = colorOperation,
                selectedCouleur = couleur,
                finale_Tariff = finale_Tariff,
                compactMode = !isExpanded,
                attachedToImage = true,
                modifier = Modifier.Companion
                    .align(Alignment.Companion.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}
