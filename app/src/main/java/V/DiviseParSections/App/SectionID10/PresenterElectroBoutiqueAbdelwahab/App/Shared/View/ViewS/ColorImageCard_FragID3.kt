package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.ViewS

import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.UiState
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.ViewModel_NewProtoPatterns
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.Shared.View.ViewS.Views.Image_Displaye
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ColorImageCard_FragID3(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp), // Default: all corners rounded
    uiState_viewModel: Pair<UiState, ViewModel_NewProtoPatterns>
) {
    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = roundedCorners
    ) {
        Box(
            modifier = if (isSelected) {
                // Selected (main color): use fixed aspect ratio
                Modifier.Companion
                    .fillMaxWidth()
                    .aspectRatio(370.dp / 500.dp)
            } else {
                // Sub-color: wrap to content height
                Modifier.Companion
                    .fillMaxWidth()
                    .wrapContentHeight()
            }
        ) {
            // Image always clickable
            Image_Displaye(
                uiState_viewModel=uiState_viewModel,
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                modifier = Modifier.Companion ,
                on_pour_send_data = on_pour_send_data
            )
        }
    }
}
