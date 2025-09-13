package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup.ProductHeader_SemiModularized.NonTrouve_Handler
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Produit_Vent(
    produitKeyId: String,
    ventList: List<M10OperationVentCouleur>,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    modifier: Modifier = Modifier
) {
    val produit = remember(produitKeyId) {
        repositorysMainGetter.find_M1Produit_ByKeyID(produitKeyId)
    }

    // Calculate NonTrouve states for the handler
    val hasNonTrouve = remember(ventList) {
        ventList.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    val allNonTrouve = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    }

    // Format first vent creation time
    val firstVentCreationTime = remember(ventList) {
        ventList.firstOrNull()?.let { firstVent ->
            val timestamp = if (firstVent.creationTimestamps > 0) {
                firstVent.creationTimestamps
            } else {
                firstVent.dernierTimeTampsSynchronisationAvecFireBase
            }

            if (timestamp > 0) {
                val sdf = SimpleDateFormat("HH:mm:ss a", Locale.getDefault())
                sdf.format(Date(timestamp))
            } else {
                null
            }
        }
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header row with product name and NonTrouve_Handler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product name
                Text(
                    text = produit?.nom ?: "Produit inconnu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onSurface
                )

                // NonTrouve_Handler component
                NonTrouve_Handler(
                    aCentralFacade = aCentralFacade,
                    allNonTrouve = allNonTrouve,
                    hasNonTrouve = hasNonTrouve,
                    relative_List_M10OperationVentCouleur = ventList
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Show first vent creation time
            firstVentCreationTime?.let { timeString ->
                Text(
                    text = "Première commande: $timeString",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Show total quantity
            val totalQuantity = ventList.sumOf { it.quantity }
            Text(
                text = "Quantité totale: $totalQuantity",
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer
                else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Show number of operations
            Text(
                text = "${ventList.size} opération(s)",
                style = MaterialTheme.typography.bodySmall,
                color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Show latest operation details if available
            ventList.firstOrNull()?.let { latestVent ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "État: ${latestVent.etateActuellementEst}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
