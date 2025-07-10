package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListCouleurs(
    produitWithColors: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>,
    viewModel: ViewModelsProduit_T1,
) {
    val produit = produitWithColors.first
    val colors = produitWithColors.second

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(colors, key = { it.key }) { color ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
            ) {
                ViewVentCouleur_T1(
                    modifier = Modifier.padding(4.dp),
                    m3Couleur= color,
                    produit = produit,
                    viewModel = viewModel,
                    size = 120.dp
                )
            }
        }
    }
}
