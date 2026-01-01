package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun FloatingSecureClickToggleFAB(
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val isSecureClickEnabled = activeCentralValues.le_pourvoire_clike_checked_est_active

    var timeRemaining by remember { mutableStateOf(0) }

    // FIXED TODO(1): Listen for changes to premier_Check_Donne, lence_pour_check,
    // or affiche_Dialog_Fast_Affiche_Panie and add 5 more seconds
    LaunchedEffect(isSecureClickEnabled) {
        if (isSecureClickEnabled) {
            timeRemaining = 5
            while (timeRemaining > 0) {
                delay(1000L)
                timeRemaining--
            }
            // Auto-disable after countdown
            focusedValuesGetter.update_activeCentralValues(
                focusedValuesGetter.active_Central_Values.copy(
                    le_pourvoire_clike_checked_est_active = false
                )
            )
        } else {
            timeRemaining = 0
        }
    }

    // Monitor operations for premier_Check_Donne or lence_pour_check toggles
    val repo10 = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur
    val ventOperations = repo10.datasValue

    // Track changes to add time when operations are toggled
    LaunchedEffect(
        ventOperations.map { it.premier_Check_Donne to it.lence_pour_check },
        activeCentralValues.affiche_Dialog_Fast_Affiche_Panie
    ) {
        // Only add time if secure click is already enabled
        if (isSecureClickEnabled && timeRemaining > 0) {
            // Add 5 more seconds (capped at 10 total)
            timeRemaining = minOf(timeRemaining + 5, 10)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                focusedValuesGetter.update_activeCentralValues(
                    activeCentralValues.copy(
                        le_pourvoire_clike_checked_est_active = !isSecureClickEnabled
                    )
                )
            },
            containerColor = if (isSecureClickEnabled) {
                Color(0xFF4CAF50) // Green when enabled
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ) {
            Icon(
                imageVector = if (isSecureClickEnabled) {
                    Icons.Default.Lock
                } else {
                    Icons.Default.LockOpen
                },
                contentDescription = if (isSecureClickEnabled) {
                    "Click sécurisé activé"
                } else {
                    "Click sécurisé désactivé"
                },
                tint = if (isSecureClickEnabled) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        if (showLabels) {
            Text(
                text = if (isSecureClickEnabled) {
                    "Click Sécurisé: ON (${timeRemaining}s)"
                } else {
                    "Click Sécurisé: OFF"
                },
                modifier = Modifier
                    .background(
                        if (isSecureClickEnabled) {
                            Color(0xFF4CAF50)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (isSecureClickEnabled) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
