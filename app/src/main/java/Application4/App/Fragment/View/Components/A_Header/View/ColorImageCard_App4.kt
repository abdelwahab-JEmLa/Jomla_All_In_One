package Application4.App.Fragment.View.Components.A_Header.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import Application4.App.Fragment.View.ViewS.Views.Image_Displaye
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
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
fun ColorImageCard_App4(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    header: @Composable () -> Unit = {}
) {
    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = roundedCorners
    ) {
        androidx.compose.foundation.layout.Column {
            header()
            Box(
                modifier = if (isSelected) {
                    Modifier.Companion
                        .fillMaxWidth()
                        .aspectRatio(370.dp / 500.dp)
                } else {
                    Modifier.Companion
                        .fillMaxWidth()
                        .wrapContentHeight()
                }
            ) {
                Image_Displaye(              //<--
                //TODO(1): pk quand une couleur est epaande et le video ce playe et je click sur une autre couleur avec video le video de clicked ne ce lance pas 
                    modifier = Modifier.Companion,
                    relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                    contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    list_M1Produit = uiState_NewProtoPatterns_viewModel.second.active_Datas
                        .list_M1Produit,
                )
            }
        }
    }
}
