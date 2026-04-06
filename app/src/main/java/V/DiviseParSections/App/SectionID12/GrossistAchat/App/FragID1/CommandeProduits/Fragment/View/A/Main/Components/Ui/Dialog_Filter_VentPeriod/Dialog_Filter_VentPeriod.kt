package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_VentPeriod

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Vents.Models.M15Grossist
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject

@Composable
fun Dialog_Filter_VentPeriod(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onDismiss: (M14VentPeriode?) -> Unit
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values

    val activeGrossist = active_Central_Values.active_M15Grossist_AuFilterAchats
    val activeClient = active_Central_Values.active_M2Client_AuFilterAchats
    val currentActiveFocuced_M14VentPeriode = active_Central_Values.active_M14VentPeriode_AuFilterAchats

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
                // Header - Show active filters
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

                // Clear Filter Option
                Card(
                    modifier = Modifier
                        .clickable {
                            focusedValuesGetter.removePeriodFilter()
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
                    activeGrossist = activeGrossist,
                    focusedValuesGetter = focusedValuesGetter,
                    onPeriodSelected_To_onDismiss = { period ->
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
    activeGrossist: M15Grossist?,
    focusedValuesGetter: FocusedValuesGetter, // Add this parameter
    onPeriodSelected_To_onDismiss: (M14VentPeriode) -> Unit
) {

    // Get all vent periods and filter those that have associated achat operations
    val periodsWithAchats = remember(
        viewModel.aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeGrossist
    ) {
        val allPeriods = viewModel.aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue
        var allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // Filter by active grossist if one is selected
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
                    relative_Period = period,
                    viewModel = viewModel,
                    activeGrossist = activeGrossist,
                    onPeriodSelected_To_onDismiss = onPeriodSelected_To_onDismiss
                )
            }
        }
    }
}
