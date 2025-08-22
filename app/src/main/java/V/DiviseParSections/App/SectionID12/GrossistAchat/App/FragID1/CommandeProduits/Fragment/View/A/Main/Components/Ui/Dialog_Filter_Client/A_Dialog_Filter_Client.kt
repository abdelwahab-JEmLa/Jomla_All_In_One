package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_Client

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.filters_Central.calculateClientSalesSummary
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("DefaultLocale")
fun formatCurrency(amount: Double): String {
    return String.format("%.2f DA", amount)
}

@Composable
fun Dialog_Filter_Client(
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onDismiss: () -> Unit,
    active_M14VentPeriode_AuFilterAchats: M14VentPeriode?
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values

    val activeGrossist = active_Central_Values.active_M15Grossist_AuFilterAchats

    val clientsSalesSummary = remember(
        aCentralFacade.repositorysMainGetter.repo2Client.datasValue,
        aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeGrossist?.keyID
    ) {
        calculateClientSalesSummary(aCentralFacade,active_M14VentPeriode_AuFilterAchats)
    }

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
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
                // Header - Show active filters context
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sélectionner un Client",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Show active filters for context
                        if (active_M14VentPeriode_AuFilterAchats != null || activeGrossist != null) {
                            val filterTexts = mutableListOf<String>()
                            active_M14VentPeriode_AuFilterAchats?.let { filterTexts.add("Période: ${it.get_DebugInfos()}") }
                            activeGrossist?.let { filterTexts.add("Grossiste: ${it.nom}") }

                            Text(
                                text = "Filtres actifs: ${filterTexts.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (clientsSalesSummary.totalClients > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = "Résumé des ventes",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Résumé des Ventes Totales",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.People,
                                            contentDescription = "Clients",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${clientsSalesSummary.totalClients} clients",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = "${clientsSalesSummary.totalQuantity} articles",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = "Total: ${formatCurrency(clientsSalesSummary.totalSalesValue)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Clear Filter Option - Only clear client filter
                Card(
                    modifier = Modifier
                        .clickable {
                            focusedValuesGetter.removeClientFilter()
                            onDismiss()
                        }
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
                            contentDescription = "Supprimer le filtre client",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Supprimer le filtre client",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Client List
                LazyColumn_Client(
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    activePeriod = active_M14VentPeriode_AuFilterAchats,
                    activeGrossist = activeGrossist,
                    focusedValuesGetter = focusedValuesGetter,
                    on_Pour_Dissmiss = {
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
fun LazyColumn_Client(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    activePeriod: M14VentPeriode?,
    activeGrossist: M15Grossist?,
    focusedValuesGetter: FocusedValuesGetter,
    on_Pour_Dissmiss: () -> Unit,
) {
    // Filter clients based on both active period and active grossist
    val clientsWithPurchases = remember(
        viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activePeriod?.keyID,
        activeGrossist?.keyID
    ) {
        val allClients = viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue
        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // Filter achat operations by active period AND grossist if they are selected
        activePeriod?.let { period ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
        }

        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        // Get all client IDs that have purchases (considering both period and grossist filters)
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
                val message = when {
                    activePeriod != null && activeGrossist != null ->
                        "Aucun client avec des achats trouvé pour cette période et ce grossiste"
                    activePeriod != null ->
                        "Aucun client avec des achats trouvé pour cette période"
                    activeGrossist != null ->
                        "Aucun client avec des achats trouvé pour ce grossiste"
                    else ->
                        "Aucun client avec des achats trouvé"
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(clientsWithPurchases) { client ->
                Item_Client(
                    relative_client = client,
                    viewModel = viewModel,
                    activePeriod = activePeriod,
                    activeGrossist = activeGrossist,
                    on_Pour_Dissmiss = { on_Pour_Dissmiss() },
                )
            }
        }
    }
}
