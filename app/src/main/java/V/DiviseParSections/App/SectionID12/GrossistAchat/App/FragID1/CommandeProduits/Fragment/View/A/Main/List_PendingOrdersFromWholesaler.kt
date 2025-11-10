package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.View_AchatProduitOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun List_PendingOrdersFromWholesaler(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    productsWithOrders: List<Triple<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>, Int>>,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade
) {
    // Create mock M11AchatOperation entries for each product-color combination with pending orders
    val groupedAchatOperations by remember(productsWithOrders) {
        derivedStateOf {
            productsWithOrders.mapNotNull { (product, colors, _) ->
                // Filter colors with pending orders
                val colorsWithOrders = colors.filter { it.a_cammende_depuit_grossist > 0 }
                if (colorsWithOrders.isEmpty()) return@mapNotNull null

                // Create a mock M11AchatOperation for each color
                val achatOperations = colorsWithOrders.map { color ->
                    M11AchatOperation(
                        keyID = "pending_${product.keyID}_${color.keyID}",
                        parent_M3CouleurProduit_KeyID = color.keyID,
                        parent_M3CouleurProduit_DebugInfos = color.nomCouleurStrSiSonImageDispo,
                        sumAchatQantity = color.a_cammende_depuit_grossist,
                        prix_Achat_De_Cette_Grossist = 0.0, // No price for pending orders
                        parent_M15Grossist_KeyID = "", // No grossist assigned yet
                        parent_M15Grossist_DebugInfos = "",
                        parent_M14VentPeriod_KeyID = "",
                        parent_M14VentPeriod_DebugInfos = "",
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                }

                // Create a Map.Entry compatible structure
                product.keyID to achatOperations
            }.toMap().entries.toList()
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Commandes en attente",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Commandes en attente du grossiste",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Display products using View_AchatProduitOperation (same as regular purchase list)
        items(groupedAchatOperations) { groupeAchatProduit ->
            View_AchatProduitOperation(
                viewModel = viewModel,
                groupeAchatProduit = groupeAchatProduit
            )
        }
    }
}
