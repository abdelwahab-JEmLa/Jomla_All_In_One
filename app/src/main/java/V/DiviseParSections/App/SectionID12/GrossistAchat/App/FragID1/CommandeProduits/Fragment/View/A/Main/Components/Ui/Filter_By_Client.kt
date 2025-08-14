package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
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
import org.koin.compose.koinInject

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
                    currentActiveFilter = getCurrentActiveFilter(viewModel),
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
    currentActiveFilter: Repo11AchatOperation.FilterQuery?,
    onClientSelected: (M2Client) -> Unit
) {
    // Get current active period filter to filter clients accordingly
    val activePeriodId = when (currentActiveFilter) {
        is Repo11AchatOperation.FilterQuery.F14VentPeriode -> currentActiveFilter.m14VentPeriode.keyID // FIXED: Changed from 'data' to 'm14VentPeriode'
        else -> null
    }

    // Filter clients based on active period and show only those who have made purchases
    val clientsWithPurchases = remember(
        viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activePeriodId
    ) {
        val allClients = viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue
        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // Filter achat operations by active period if one is selected
        if (activePeriodId != null) {
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == activePeriodId
            }
        }

        // Get all client IDs that have purchases (considering period filter)
        val clientIdsWithPurchases = allAchatOperations.flatMap { achatOperation ->
            val relatedVentOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(allVentOperations)
            relatedVentOperations.mapNotNull { ventOperation ->
                val bonVent = allBonVents.find { it.keyID == ventOperation.parent_M8BonVent_KeyId }
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
                    text = if (activePeriodId != null)
                        "Aucun client avec des achats trouvé pour cette période"
                    else
                        "Aucun client avec des achats trouvé",
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
                    activePeriodId = activePeriodId,
                    onClientSelected = onClientSelected
                )
            }
        }
    }
}

@Composable
fun Item_Client(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    client: M2Client,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    activePeriodId: String?,
    onClientSelected: (M2Client) -> Unit
) {
    val clientPurchaseInfo = remember(
        client.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activePeriodId
    ) {
        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // Filter achat operations by active period if one is selected
        if (activePeriodId != null) {
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == activePeriodId
            }
        }

        // Get all BonVents for this client
        val clientBonVents = allBonVents.filter { it.parent_M2Client_KeyID == client.keyID }
        val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()

        // Get all vent operations for this client
        val clientVentOperations = allVentOperations.filter {
            it.parent_M8BonVent_KeyId in clientBonVentIds
        }

        // Get unique products (M1Produit) purchased by this client (considering period)
        val relatedAchatOperations = allAchatOperations.filter { achatOperation ->
            val relatedVentOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(clientVentOperations)
            relatedVentOperations.isNotEmpty()
        }

        val uniqueProducts = relatedAchatOperations.map {
            it.parent_M1Produit_KeyID
        }.toSet()

        // Calculate total quantity and total sales amount for this client (considering period)
        val totalQuantity = clientVentOperations.filter { ventOperation ->
            allAchatOperations.any { achatOperation ->
                achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation)).isNotEmpty()
            }
        }.sumOf { it.quantity }

        val relative_List_Vents =
            repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
                it.parent_M14VentPeriod_KeyId == activePeriodId
                        && it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve

            }

        val totalSalesValue = relative_List_Vents.sumOf {
            val parentM13TarificationPrix =
                repositorysMainGetter.find_M13Tarification_By_KeyID(it.parentM13TarificationKeyID)?.prixCurrency
                    ?: 0.0

            it.quantity * parentM13TarificationPrix
        }

        Triple(uniqueProducts.size, totalQuantity, totalSalesValue)
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

                // Show total sales amount
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (clientPurchaseInfo.third > 0) Icons.Default.TrendingUp else Icons.Default.AccountBalance,
                        contentDescription = "Ventes totales",
                        modifier = Modifier.size(14.dp),
                        tint = if (clientPurchaseInfo.third > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Ventes: ${String.format("%.2f", clientPurchaseInfo.third)} DA",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (clientPurchaseInfo.third > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        fontWeight = if (clientPurchaseInfo.third > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (activePeriodId != null) {
                        Text(
                            text = "(période active)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// Helper function to get current active filter
private fun getCurrentActiveFilter(viewModel: GrossistAchatSec12FragID1_ViewModel): Repo11AchatOperation.FilterQuery? {
    return viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.currentFilterQuery // FIXED: Now using the added property
}
