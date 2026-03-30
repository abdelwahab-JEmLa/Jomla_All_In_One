package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup.ProductHeader_SemiModularized

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun NonTrouve_Handler(
    aCentralFacade: ACentralFacade = koinInject(),
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    allNonTrouve: Boolean=false,
    hasNonTrouve: Boolean =false,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>
) {
    IconButton(
        onClick = {
            relative_List_M10OperationVentCouleur.map { vent ->
                val newState =
                    if (vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve)
                        M10OperationVentCouleur.EtateDelivery.NonTrouve
                    else M10OperationVentCouleur.EtateDelivery.Trouve

                repo10OperationVentCouleur.addOrUpdateData(vent.copy(etateDelivery = newState))

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
            imageVector = if (hasNonTrouve) Icons.Default.Cancel else Icons.Default.Print,
            contentDescription = if (hasNonTrouve) "Mark as found" else "Mark as not found",
            tint = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f),
            modifier = Modifier.size(20.dp)
        )
    }
}
