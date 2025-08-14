package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sélectionner une Période",
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

                // VentPeriod List
                LazyColumn_VentPeriod(
                    viewModel = viewModel,
                    currentActivePeriod = currentActiveFocuced_M14VentPeriode,
                    onPeriodSelected = { period ->
                        onDismiss(period)
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
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
    ) {
        val allPeriods = viewModel.aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue
        val allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

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
                    text = "Aucune période avec des achats trouvée",
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
