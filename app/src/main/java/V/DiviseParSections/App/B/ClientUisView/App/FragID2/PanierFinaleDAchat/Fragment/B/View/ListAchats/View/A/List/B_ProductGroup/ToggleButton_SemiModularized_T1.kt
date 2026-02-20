package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import EntreApps.Shared.Models.M01Produit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ToggleButton_SemiModularized_T1(
    allNonTrouve: Boolean,
    hasNonTrouve: Boolean,
    viewModel: ZViewModel_Sec1Frag3,
    relative_M1Produit: M01Produit?
) {
    IconButton(
        onClick = {
            relative_M1Produit?.keyID?.let {
                viewModel.aCentralFacade.repositorysMainSetter.toggleEtateDeliveryNonTrouveVentOuFacade(
                    it
                )
            }
        },
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            )
    ) {
        Icon(
            imageVector = if (hasNonTrouve) Icons.Default.Cancel else Icons.Default.CheckCircle,
            contentDescription = if (hasNonTrouve) "Mark as found" else "Mark as not found",
            tint = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f),
            modifier = Modifier.size(20.dp)
        )
    }
}
