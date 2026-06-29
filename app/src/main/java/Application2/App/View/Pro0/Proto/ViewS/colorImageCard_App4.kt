package Application2.App.View.Pro0.Proto.ViewS

import Application2.App.Fragment.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import Application2.App.View.Pro0.Proto.ViewS.Views.Image_Displaye_app2
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
        ) {
            Image_Displaye_app2(
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                expandState = expandState,
                contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )

            val parentProduct = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {     //<--
            //TODO(1): extract et fait qus si 15 de affiche 10 image au bas 5 fait que si 30 affiche da_20 et au bas 10 si 25 c da_20.png et 5 si 40 c 20 image 
            //et au bas autre 20 image si 50 affiche da_50.png si 60 c 50 image + 10 da si autre affiche un circle et le prix ace da la don 
                viewModel.uiState.value.list_ProductWithColors.find {
                    it.first.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
                }?.first ?: viewModel.uiState.value.list_M1Produit.find {
                    it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
                }
            }
            val price = parentProduct?.clientPrixVentUnite ?: 0.0
            val drawableRes = when (price.toInt()) {
                10 -> com.example.clientjetpack.R.drawable.da_10
                5 -> com.example.clientjetpack.R.drawable.da_5
                else -> null
            }
            if (drawableRes != null) {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(40.dp)
                )
            }
        }
    }
}
