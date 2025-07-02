package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.ViewVentCouleur_T1
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
 fun ListCouleurs(
    relatedVents: List<FCouleurVentOperationInfos>,
    viewModel: ViewModelsProduit_T1,
    allNonTrouve: Boolean
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                set(SemanticsPropertyKey("1vents"), relatedVents.map { it })
            },
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(relatedVents) { vent ->
            viewModel.getter.b1CouleurOuGoutProduitDataBaseRepository.datasValue
                .find { it.key == vent.parentCouleurInfosKeyID }?.let {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = if (allNonTrouve) 1.dp else 2.dp,
                        modifier = Modifier
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .graphicsLayer(
                                alpha = if (vent.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve) 0.5f else 1.0f
                            )
                    ) {
                        ViewVentCouleur_T1(
                            modifier = Modifier.padding(4.dp),
                            ventKey = vent.keyID,
                            size = 120.dp,
                            purchasedQuantity = vent.quantityAchete,
                            viewModel = viewModel
                        )
                    }
                }
        }
    }
}
