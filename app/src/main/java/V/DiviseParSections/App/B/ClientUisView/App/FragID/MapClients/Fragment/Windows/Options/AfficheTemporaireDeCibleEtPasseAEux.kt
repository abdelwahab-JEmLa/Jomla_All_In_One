package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisabledVisible
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AfficheTemporaireDeCibleEtPasseAEux(
    showLabels: Boolean,
    viewModel: ViewModel_MapClients_App2FragID1,
    onFilterChanged: (ViewModel_MapClients_App2FragID1.VisibleClientsNow) -> Unit,
) {
    var isTemporaryFilterActive by remember { mutableStateOf(false) }

    var timerJob by remember { mutableStateOf<Job?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val couleurButton1 = if (isTemporaryFilterActive) Color(0xFF4CAF50) else Color(0xFFF44336)

        FloatingActionButton(
            onClick = {
                if (!isTemporaryFilterActive) {
                    // Start temporary filter mode
                    isTemporaryFilterActive = true

                    // Change to CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX immediately
                    onFilterChanged(ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX)

                    // Start 20-second timer
                    timerJob = coroutineScope.launch {
                        delay(20000) // 20 seconds

                        // Revert back to AFFICHE_CIBLE_POUR_VENDEUR
                        onFilterChanged(ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR)
                        isTemporaryFilterActive = false
                        timerJob = null
                    }
                } else {
                    // Second click: cancel temporary filter and revert immediately
                    timerJob?.cancel() // Cancel the timer
                    timerJob = null
                    isTemporaryFilterActive = false
                    onFilterChanged(ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR)
                }
                // Also show the day filter dialog
            },
            modifier = Modifier.size(40.dp),
            containerColor = couleurButton1
        ) {
            Icon(
                if (isTemporaryFilterActive) Icons.Filled.FilterAlt else Icons.Filled.DisabledVisible,
                "Filter by day"
            )
        }

        if (showLabels) {
            Text(
                if (isTemporaryFilterActive)
                    "مؤقت: ${viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList}"
                else
                    "فلتر: ${viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList}",
                modifier = Modifier
                    .background(couleurButton1)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
