package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun Dialog_Filter_Client(
    uiState: GrossistAchatSec12FragID1_ViewModel.UiState,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    onDismiss: (M2Client?) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss(null) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sélectionner un Client",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onDismiss(null) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clear Filter Option
                Card(
                    modifier = Modifier
                        .clickable { onDismiss(null) }
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Supprimer le filtre",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Supprimer le filtre",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Client List
                LazyColumn_Client(
                    viewModel = viewModel,
                    onClientSelected = { client ->
                        onDismiss(client)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LazyColumn_Client(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    onClientSelected: (M2Client) -> Unit
) {
    // Filter clients to show only those who have made purchases
    val clientsWithPurchases = remember(
        viewModel.aCentralFacade.repoMainGetter.repo2Client.datasValue,
        viewModel.aCentralFacade.repoMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repoMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repoMainGetter.repo11AchatOperation.datasValue
    ) {
        val allClients = viewModel.aCentralFacade.repoMainGetter.repo2Client.datasValue
        val allBonVents = viewModel.aCentralFacade.repoMainGetter.repo8BonVent.datasValue
        val allVentOperations = viewModel.aCentralFacade.repoMainGetter.repo10OperationVentCouleur.datasValue
        val allAchatOperations = viewModel.aCentralFacade.repoMainGetter.repo11AchatOperation.datasValue

        // Get all client IDs that have purchases
        val clientIdsWithPurchases = allAchatOperations.flatMap { achatOperation ->
            val relatedVentOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(allVentOperations)
            relatedVentOperations.mapNotNull { ventOperation ->
                val bonVent = allBonVents.find { it.keyID == ventOperation.parentM8BonVentKeyId }
                bonVent?.parent_M2Client_KeyID
            }
        }.toSet()

        // Filter clients to only include those with purchases
        allClients.filter { client ->
            client.keyID in clientIdsWithPurchases
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (clientsWithPurchases.isEmpty()) {
            item {
                Text(
                    text = "Aucun client avec des achats trouvé",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(clientsWithPurchases) { client ->
                Item_Client(
                    client = client,
                    viewModel = viewModel,
                    onClientSelected = onClientSelected
                )
            }
        }
    }
}

@Composable
fun Item_Client(
    client: M2Client,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    onClientSelected: (M2Client) -> Unit
) {
    // Calculate the number of different products this client has purchased
    val clientPurchaseInfo = remember(
        client.keyID,
        viewModel.aCentralFacade.repoMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repoMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repoMainGetter.repo11AchatOperation.datasValue
    ) {
        val allBonVents = viewModel.aCentralFacade.repoMainGetter.repo8BonVent.datasValue
        val allVentOperations = viewModel.aCentralFacade.repoMainGetter.repo10OperationVentCouleur.datasValue
        val allAchatOperations = viewModel.aCentralFacade.repoMainGetter.repo11AchatOperation.datasValue

        // Get all BonVents for this client
        val clientBonVents = allBonVents.filter { it.parent_M2Client_KeyID == client.keyID }
        val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()

        // Get all vent operations for this client
        val clientVentOperations = allVentOperations.filter {
            it.parentM8BonVentKeyId in clientBonVentIds
        }

        // Get unique products (M1Produit) purchased by this client
        val uniqueProducts = clientVentOperations.map {
            it.parentM1ProduitInfosKeyId
        }.toSet()

        // Calculate total quantity
        val totalQuantity = clientVentOperations.sumOf { it.quantity }

        Pair(uniqueProducts.size, totalQuantity)
    }

    Card(
        modifier = Modifier
            .clickable { onClientSelected(client) }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Client avatar or icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.nom.take(2).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Client info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = client.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Show purchase statistics
                Text(
                    text = "${clientPurchaseInfo.first} produits • ${clientPurchaseInfo.second} articles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
