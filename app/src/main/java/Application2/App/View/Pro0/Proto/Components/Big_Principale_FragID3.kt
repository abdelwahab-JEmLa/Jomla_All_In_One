package Application2.App.View.Pro0.Proto.Components

import Application2.App.View.Pro0.Proto.ViewS.ColorImageCard_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.M3CouleurProduitInfos
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Big_Principale_AppEcranPresntoireJemlaCom(
    big_presenter_couleur_produit: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    // FIX: receive the WiFi-aware tap handler from Item_Produit and pass it down
    onImageTap: ((M3CouleurProduitInfos) -> Unit)? = null,
) {
    ColorImageCard_AppEcranPresntoireJemlaCom(
        relative_M3CouleurProduitInfos = big_presenter_couleur_produit,
        expandState = expandState,
        isSelected = true,
        modifier = Modifier.fillMaxWidth(),
        // If a WiFi-aware handler was provided, use it; otherwise fall back to the default local tap
        onImageClick = if (onImageTap != null) {
            { onImageTap(big_presenter_couleur_produit) }
        } else null,
    )
}
