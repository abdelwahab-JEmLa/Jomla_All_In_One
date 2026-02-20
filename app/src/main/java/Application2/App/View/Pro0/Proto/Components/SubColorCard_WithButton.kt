package Application2.App.View.Pro0.Proto.Components

import Application2.App.View.Pro0.Proto.ViewS.ColorImageCard_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SubColorCard_WithButton_app2(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: M01Produit,
    expandState: ProduitExpandState,
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ColorImageCard_AppEcranPresntoireJemlaCom(
            relative_M3CouleurProduitInfos = couleur,
            expandState = expandState,
            isSelected = false,
            modifier = Modifier.fillMaxWidth().height(if (isExpanded) 80.dp else 40.dp),
            onImageClick = { expandState.selectColor(couleur) },
        )
    }
}
