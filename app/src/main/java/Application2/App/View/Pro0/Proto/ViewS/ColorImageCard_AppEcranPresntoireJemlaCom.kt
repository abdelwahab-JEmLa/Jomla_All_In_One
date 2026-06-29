package Application2.App.View.Pro0.Proto.ViewS

import Application2.App.Fragment.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import Application2.App.View.Pro0.Proto.ViewS.Views.Image_Displaye_app2
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Retourne la liste des drawables à superposer pour un prix donné.
 *  Paliers : 5→[da_5], 10→[da_10], 15→[da_10,da_5], 20→[da_20],
 *  25→[da_20,da_5], 30→[da_20,da_10], 40→[da_20,da_20],
 *  50→[da_50], 60→[da_50,da_10], sinon cercle+texte géré dans le composable. */
fun getPrixDrawables(price: Int): List<Int> {
    val da5   = com.example.clientjetpack.R.drawable.da_5
    val da10  = com.example.clientjetpack.R.drawable.da_10
    val da20  = com.example.clientjetpack.R.drawable.da_20
    val da50  = com.example.clientjetpack.R.drawable.da_50
    return when (price) {
        5        -> listOf(da5)
        10       -> listOf(da10)
        15       -> listOf(da10, da5)
        20       -> listOf(da20)
        25       -> listOf(da20, da5)
        30       -> listOf(da20, da10)
        40       -> listOf(da20, da20)
        50       -> listOf(da50)
        60       -> listOf(da50, da10)
        else     -> emptyList()
    }
}

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

            val parentProduct = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
                viewModel.uiState.value.list_ProductWithColors.find {
                    it.first.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
                }?.first ?: viewModel.uiState.value.list_M1Produit.find {
                    it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
                }
            }
            if (isSelected) {
                val price = parentProduct?.clientPrixVentUnite ?: 0.0
                val drawables = getPrixDrawables(price.toInt())
                if (drawables.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        drawables.forEachIndexed { index, res ->
                            Image(
                                painter = painterResource(id = res),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .offset(x = (index * 14).dp, y = (index * 14).dp)
                            )
                        }
                    }
                } else if (price > 0.0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${price.toInt()}دج",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(4.dp),
                        )
                    }
                }
            }
        }
    }
}
