package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons

import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.sortClientsByLastVentOperation
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

// ✅ Toggle button for cycling through CLIENTS (not BonVents)
@Composable
fun FloatingBonVentToggleFAB(
    showLabels: Boolean,
    modifier: Modifier = Modifier.Companion,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    // Get repositories from central facade
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter

    // Get current active values
    val currentBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val currentPeriod = focusedValuesGetter.currentActiveFocuced_M14VentPeriode

    val clientsWithBonVents = remember(currentPeriod, repositorysMainGetter.repo8BonVent.datasValue, repositorysMainGetter.repo2Client.datasValue) {
        if (currentPeriod == null) {
            emptyList()
        } else {
            // Find all unprinted BonVents for this period
            val bonVentsInPeriod = repositorysMainGetter.repo8BonVent.datasValue
                .filter { bonVent ->
                    bonVent.parent_M14VentPeriod_KeyId == currentPeriod.keyID &&
                            !bonVent.a_etai_imprime_au_moi_ne_foit  // ✅ Fixed: use boolean instead of vala_supp
                }

            // Get unique clients who have these BonVents
            val clientIds = bonVentsInPeriod.map { it.parent_M2Client_KeyID }.distinct()
            val clients = repositorysMainGetter.repo2Client.datasValue
                .filter { it.keyID in clientIds }

            // Sort clients by last sale operation
            sortClientsByLastVentOperation(clients, repositorysMainGetter)
        }
    }

    val clientCount = clientsWithBonVents.size

    // Find the index of the current client
    val currentClientIndex = currentBonVent?.let { bonVent ->
        clientsWithBonVents.indexOfFirst { it.keyID == bonVent.parent_M2Client_KeyID }
    } ?: -1

    val nextClientInfo = remember(currentClientIndex, clientCount, clientsWithBonVents) {
        if (clientsWithBonVents.isEmpty()) {
            null
        } else {
            val nextIndex = if (currentClientIndex >= clientCount - 1 || currentClientIndex < 0) {
                0 // Return to first client
            } else {
                currentClientIndex + 1 // Next client
            }
            val nextClient = clientsWithBonVents[nextIndex]
            Pair(nextIndex, nextClient.nom)
        }
    }

    // Show toast messages
    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.Companion.size(40.dp),
            onClick = {
                if (clientsWithBonVents.isNotEmpty()) {
                    // If at last or invalid index, return to first (index 0)
                    val nextClientIndex = if (currentClientIndex >= clientCount - 1 || currentClientIndex < 0) {
                        0 // Return to first client
                    } else {
                        currentClientIndex + 1 // Next client
                    }
                    val nextClient = clientsWithBonVents[nextClientIndex]

                    try {
                        // Find or create BonVent for this client
                        val existingBonVent = repositorysMainGetter.repo8BonVent.datasValue.find { bonVent ->
                            bonVent.parent_M14VentPeriod_KeyId == currentPeriod?.keyID &&
                                    bonVent.parent_M2Client_KeyID == nextClient.keyID &&
                                    !bonVent.a_etai_imprime_au_moi_ne_foit  // ✅ Fixed: use boolean
                        }

                        val bonVentToUse = if (existingBonVent != null) {
                            // Update existing BonVent
                            repositorysMainSetter.update_M8BonVent(existingBonVent)
                            existingBonVent
                        } else {
                            // Create new BonVent for this client
                            val newBonVent = M8BonVent().copy(
                                parent_M9AppCompt_KeyID = focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: "",
                                parent_M14VentPeriod_KeyId = currentPeriod?.keyID ?: "",
                                parent_M2Client_KeyID = nextClient.keyID,
                                parent_M2Client_DebugInfos = nextClient.nom,
                                its_working_for_wholesaler = true
                            )
                            aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
                                .add_M8BonVent(newBonVent)
                            newBonVent
                        }

                        // Set as active focused BonVent
                        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
                            .setIN_M9CurrentApp_onVentM8BonVentKey(bonVentToUse)

                        // Show toast notification with client name
                        toastData = ToastData(
                            message = "Client ${nextClientIndex + 1}/$clientCount: ${nextClient.nom}",
                            type = ToastType.INFO,
                            duration = 1500L
                        )
                    } catch (e: Exception) {
                        toastData = ToastData(
                            message = "Erreur: ${e.message}",
                            type = ToastType.ERROR,
                            duration = 2000L
                        )
                    }
                } else {
                    toastData = ToastData(
                        message = "Aucun client avec BonVent disponible",
                        type = ToastType.WARNING,
                        duration = 1500L
                    )
                }
            },
            containerColor = if (clientCount > 1) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ) {
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "Client suivant",
                tint = if (clientCount > 1) Color.Companion.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showLabels) {
            Text(
                text = when {
                    clientCount == 0 -> "Aucun client"
                    clientCount == 1 -> "Client unique: ${clientsWithBonVents.firstOrNull()?.nom ?: ""}"
                    nextClientInfo != null -> {
                        val (nextIdx, nextName) = nextClientInfo
                        "Actuel: ${currentClientIndex + 1}/$clientCount | Suivant: ${nextIdx + 1} - $nextName"
                    }
                    else -> "Client ${currentClientIndex + 1}/$clientCount"
                },
                modifier = Modifier.Companion
                    .background(
                        if (clientCount > 1) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (clientCount > 1) Color.Companion.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
