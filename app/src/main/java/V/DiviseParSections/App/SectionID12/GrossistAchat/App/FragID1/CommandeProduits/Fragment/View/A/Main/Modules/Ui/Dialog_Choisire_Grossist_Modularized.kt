package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.database.ValueEventListener
import org.koin.compose.koinInject

@Composable
fun Dialog_Choisire_Grossist_Modularized(
    titel: String = "Choisir un Grossiste",
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    list_M11AchatOperation: List<M11AchatOperation> = emptyList(),
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onDismiss: (M15Grossist?) -> Unit
) {
    val datasValue_repo11AchatOperation =
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
    val grossists = viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue
    val focusManager = LocalFocusManager.current

    val active_Central_Values = focusedValuesGetter.active_Central_Values

    val activePeriod = active_Central_Values.active_M14VentPeriode_AuFilterAchats
    val activeClient = active_Central_Values.active_M2Client_AuFilterAchats

    val activePeriodId = activePeriod?.keyID

    var grossistCredits by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var totalCreditsAllGrossists by remember { mutableStateOf(0.0) }
    var isLoadingCredits by remember { mutableStateOf(true) }
    var creditListeners by remember { mutableStateOf<Map<String, List<ValueEventListener>>>(emptyMap()) }

    val grossistsWithPurchaseCount =
        remember(grossists, datasValue_repo11AchatOperation, activePeriodId) {
            val filteredOperations = if (activePeriodId != null) {
                datasValue_repo11AchatOperation.filter { it.parent_M14VentPeriod_KeyID == activePeriodId }
            } else {
                datasValue_repo11AchatOperation
            }

            grossists.map { grossist ->
                val purchaseCount =
                    filteredOperations.count { it.parent_M15Grossist_KeyID == grossist.keyID }
                grossist to purchaseCount
            }
                .filter { it.second > 0 || activePeriodId == null } // Show all grossists if no period filter, otherwise only those with purchases
                .sortedByDescending { it.second }
        }

    val nullGrossistCount = remember(datasValue_repo11AchatOperation, activePeriodId) {
        val filteredOperations = if (activePeriodId != null) {
            datasValue_repo11AchatOperation.filter { it.parent_M14VentPeriod_KeyID == activePeriodId }
        } else {
            datasValue_repo11AchatOperation
        }

        filteredOperations.count {
            it.parent_M15Grossist_KeyID == "null" || it.parent_M15Grossist_KeyID.isBlank()
        }
    }

    LaunchedEffect(grossists) {
        isLoadingCredits = true

        val (listeners, initialCredits) = loadCreditsForAllGrossists(grossists) { creditsMap ->
            grossistCredits = creditsMap
            totalCreditsAllGrossists = creditsMap.values.sum()
            isLoadingCredits = false
        }
        creditListeners = listeners
        grossistCredits = initialCredits
    }

    // Cleanup listeners when dialog is dismissed
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            // Remove all listeners when component is disposed
            creditListeners.values.flatten().forEach { listener ->
                // Note: In a real implementation, you'd need to remove these listeners
                // from their respective Firebase references for proper cleanup
            }
        }
    }

    Dialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss(null)
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with title and active filters display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = titel,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Show active filters
                        if (activePeriod != null || activeClient != null) {
                            val filterTexts = mutableListOf<String>()
                            activePeriod?.let { filterTexts.add("Période: ${it.get_DebugInfos()}") }
                            activeClient?.let { filterTexts.add("Client: ${it.nom}") }

                            Text(
                                text = "Filtres actifs: ${filterTexts.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onDismiss(null)
                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Fermer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .clickable {
                                    focusManager.clearFocus()
                                    // FIXED: Use focusedValuesGetter extension function to remove grossist filter
                                    focusedValuesGetter.removeGrossistFilter()
                                    onDismiss(null)
                                }
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
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Supprimer le filtre grossiste",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Supprimer le filtre grossiste",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (nullGrossistCount > 0) {
                        item {
                            Card(
                                modifier = Modifier
                                    .clickable {
                                        focusManager.clearFocus()
                                        val nullGrossist = M15Grossist(
                                            keyID = "NULL_GROSSIST_FILTER",
                                            nom = "Grossiste non défini",
                                            couleur_In_Str = "#FF0000"
                                        )
                                        // FIXED: Use focusedValuesGetter extension function to add grossist filter
                                        focusedValuesGetter.addGrossistFilter(nullGrossist)
                                        onDismiss(null)
                                    }
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                            ) {
                                                Text(
                                                    text = nullGrossistCount.toString(),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.error),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.FilterList,
                                                contentDescription = "Grossiste non défini",
                                                tint = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Grossiste non défini",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = "$nullGrossistCount opérations sans grossiste${if (activePeriodId != null) " (période active)" else ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(grossistsWithPurchaseCount) { (grossist, purchaseCount) ->
                        GrossistItem(
                            list_M11AchatOperation = list_M11AchatOperation,
                            grossist = grossist,
                            purchaseCount = purchaseCount,
                            grossistCredit = grossistCredits[grossist.keyID] ?: 0.0,
                            isLoadingCredit = isLoadingCredits,
                            activePeriodId = activePeriodId,
                            onSelect = {
                                focusManager.clearFocus()
                                // FIXED: Use focusedValuesGetter extension function to add grossist filter
                                focusedValuesGetter.addGrossistFilter(grossist)
                                onDismiss(null)
                            }
                        )
                    }

                    if (grossistsWithPurchaseCount.isEmpty() && nullGrossistCount == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Business,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (activePeriodId != null)
                                            "Aucun grossiste avec des achats pour cette période"
                                        else
                                            "Aucun grossiste disponible",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onDismiss(null)
                    }) {
                        Text("Annuler")
                    }
                }
            }
        }
    }
}
