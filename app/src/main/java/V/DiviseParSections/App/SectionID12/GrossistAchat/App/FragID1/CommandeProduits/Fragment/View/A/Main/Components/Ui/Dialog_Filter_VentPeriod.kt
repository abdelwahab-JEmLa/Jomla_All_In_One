package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Dialog_Filter_VentPeriod(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onDismiss: (M14VentPeriode?) -> Unit
) {
    val currentActiveFocuced_M14VentPeriode = focusedValuesGetter.currentActiveFocuced_M14VentPeriode

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
                // Header - UPDATED to show active filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sélectionner une Période",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (activeGrossist != null || activeClient != null) {
                            val filterTexts = mutableListOf<String>()
                            activeGrossist?.let { filterTexts.add("Grossiste: ${it.nom}") }
                            activeClient?.let { filterTexts.add("Client: ${it.nom}") }

                            Text(
                                text = "Filtres actifs: ${filterTexts.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = { onDismiss(null) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clear Filter Option - UPDATED
                Card(
                    modifier = Modifier
                        .clickable {
                            // UPDATED: Clear only period filter, keep others
                            viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.removePeriodFilter()
                            onDismiss(null)
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
                            contentDescription = "Supprimer le filtre période",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Supprimer le filtre période",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // VentPeriod List
                LazyColumn_VentPeriod(
                    viewModel = viewModel,
                    currentActivePeriod = currentActiveFocuced_M14VentPeriode,
                    onPeriodSelected = { period ->
                        // UPDATED: Add period filter while keeping other active filters
                        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.addPeriodFilter(period)
                        onDismiss(null)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LazyColumn_VentPeriod(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    currentActivePeriod: M14VentPeriode?,
    onPeriodSelected: (M14VentPeriode) -> Unit
) {

    // Get all vent periods and filter those that have associated achat operations
    val periodsWithAchats = remember(
        viewModel.aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeGrossist
    ) {
        val allPeriods = viewModel.aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue
        var allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // UPDATED: Filter by active grossist if one is selected
        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        // Get all period IDs that have associated achat operations
        val periodIdsWithAchats = allAchatOperations.map {
            it.parent_M14VentPeriod_KeyID
        }.filter { it != "null" }.toSet()

        // Filter periods to only include those with achat operations
        val filteredPeriods = allPeriods.filter { period ->
            period.keyID in periodIdsWithAchats
        }.sortedByDescending { it.creationTimestamp }

        // Put current active period at the top if it exists
        currentActivePeriod?.let { activePeriod ->
            if (activePeriod.keyID in periodIdsWithAchats) {
                val withoutActive = filteredPeriods.filter { it.keyID != activePeriod.keyID }
                listOf(activePeriod) + withoutActive
            } else {
                filteredPeriods
            }
        } ?: filteredPeriods
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (periodsWithAchats.isEmpty()) {
            item {
                Text(
                    text = if (activeGrossist != null)
                        "Aucune période avec des achats trouvée pour ce grossiste"
                    else
                        "Aucune période avec des achats trouvée",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(periodsWithAchats) { period ->
                Item_VentPeriod(
                    period = period,
                    viewModel = viewModel,
                    isCurrentActive = period.keyID == currentActivePeriod?.keyID,
                    activeGrossist = activeGrossist,  // UPDATED: Pass active grossist
                    onPeriodSelected = onPeriodSelected
                )
            }
        }
    }
}

@Composable
fun Item_VentPeriod(
    period: M14VentPeriode,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    isCurrentActive: Boolean,
    activeGrossist: M15Grossist? = null,  // UPDATED: Add active grossist parameter
    onPeriodSelected: (M14VentPeriode) -> Unit
) {
    // UPDATED: Calculate statistics considering active grossist
    val periodStats = remember(
        period.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeGrossist
    ) {
        var achatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
            .filter { it.parent_M14VentPeriod_KeyID == period.keyID }

        // UPDATED: Filter by active grossist if one is selected
        activeGrossist?.let { grossist ->
            achatOperations = achatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        val totalOperations = achatOperations.size
        val totalQuantity = achatOperations.sumOf { it.sumAchatQantity }
        val uniqueProducts = achatOperations.map { it.parent_M1Produit_KeyID }.toSet().size

        Triple(totalOperations, totalQuantity, uniqueProducts)
    }

    // Format date
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = remember(period.creationTimestamp) {
        dateFormatter.format(Date(period.creationTimestamp))
    }

    Card(
        modifier = Modifier
            .clickable { onPeriodSelected(period) }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentActive) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Period icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isCurrentActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCurrentActive) Icons.Default.CheckCircle else Icons.Default.DateRange,
                    contentDescription = "Période",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Period info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = period.get_DebugInfos(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isCurrentActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isCurrentActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Actuelle)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Créée le: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // UPDATED: Show statistics with grossist context
                val statsText = if (activeGrossist != null) {
                    "${periodStats.first} opérations • ${periodStats.second} articles • ${periodStats.third} produits (${activeGrossist.nom})"
                } else {
                    "${periodStats.first} opérations • ${periodStats.second} articles • ${periodStats.third} produits"
                }

                Text(
                    text = statsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun Item_VentPeriod(
    period: M14VentPeriode,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    isCurrentActive: Boolean,
    onPeriodSelected: (M14VentPeriode) -> Unit
) {
    // Calculate statistics for this period
    val periodStats = remember(
        period.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
    ) {
        val achatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
            .filter { it.parent_M14VentPeriod_KeyID == period.keyID }

        val totalOperations = achatOperations.size
        val totalQuantity = achatOperations.sumOf { it.sumAchatQantity }
        val uniqueProducts = achatOperations.map { it.parent_M1Produit_KeyID }.toSet().size

        Triple(totalOperations, totalQuantity, uniqueProducts)
    }

    // Format date
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = remember(period.creationTimestamp) {
        dateFormatter.format(Date(period.creationTimestamp))
    }

    Card(
        modifier = Modifier
            .clickable { onPeriodSelected(period) }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentActive) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Period icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isCurrentActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCurrentActive) Icons.Default.CheckCircle else Icons.Default.DateRange,
                    contentDescription = "Période",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Period info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = period.get_DebugInfos(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isCurrentActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isCurrentActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Actuelle)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Créée le: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Show statistics
                Text(
                    text = "${periodStats.first} opérations • ${periodStats.second} articles • ${periodStats.third} produits",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
