package Application2.App.View.Pro0.Proto.ViewS

import Application2.App.Fragment.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import Application2.App.View.Pro0.Proto.ViewS.Views.Image_Displaye_app2
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ColorImageCard_AppEcranPresntoireJemlaCom(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp),
    viewModel: ViewModel_MainFragment
) {
    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = roundedCorners
    ) {
        Box(
            modifier = if (isSelected) {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(370f / 500f)
            } else {
                Modifier.fillMaxSize()
            }
        ) {     //<--
        //TODO(1): ajout au top end un floting image de drawble da_10.png si prix vent u == 10 et si c 5  da_5.png
            Image_Displaye_app2(
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                expandState = expandState,
                contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
        }
    }
}
