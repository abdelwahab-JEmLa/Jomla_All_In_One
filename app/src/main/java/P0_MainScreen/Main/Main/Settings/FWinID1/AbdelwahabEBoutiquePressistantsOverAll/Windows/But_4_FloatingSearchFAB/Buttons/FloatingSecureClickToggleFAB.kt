package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons

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

    // FIXED: Inverted logic - true means security is DISABLED (temporary unlock)
    val isSecurityDisabled = activeCentralValues.le_pourvoire_clike_checked_est_active

    var timeRemaining by remember { mutableStateOf(0) }

    // FIXED TODO(1): When affiche_Dialog_Fast_Affiche_Panie becomes true, start 5 second timer
    LaunchedEffect(activeCentralValues.affiche_Dialog_Fast_Affiche_Panie) {
        if (activeCentralValues.affiche_Dialog_Fast_Affiche_Panie && !isSecurityDisabled) {
            // Dialog opened - disable security for 5 seconds
            focusedValuesGetter.update_activeCentralValues(
                activeCentralValues.copy(le_pourvoire_clike_checked_est_active = true)
            )
        }
    }

    // Timer countdown when security is disabled
    LaunchedEffect(isSecurityDisabled) {
        if (isSecurityDisabled) {
            timeRemaining = 5
            while (timeRemaining > 0) {
                delay(1000L)
                timeRemaining--
            }
            // Re-enable security after countdown
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
    // FIXED: When any check is toggled, reset timer to 5 seconds
    val repo10 = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur
    val ventOperations = repo10.datasValue

    LaunchedEffect(
        ventOperations.map { it.premier_Check_Donne to it.lence_pour_check }
    ) {
        // If security is currently disabled (allowing clicks), reset to 5 seconds
        if (isSecurityDisabled && timeRemaining > 0) {
            timeRemaining = 5
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
                // FIXED: Manual toggle also resets to 5 seconds when disabling security
                val newState = !isSecurityDisabled
                focusedValuesGetter.update_activeCentralValues(
                    activeCentralValues.copy(
                        le_pourvoire_clike_checked_est_active = newState
                    )
                )
                // If we just disabled security, reset timer to 5
                if (newState) {
                    timeRemaining = 5
                }
            },
            containerColor = if (isSecurityDisabled) {
                Color(0xFFFFEB3B) // Yellow when security is DISABLED (unlocked)
            } else {
                Color(0xFF4CAF50) // Green when security is ENABLED (locked)
            },
        ) {
            Icon(
                imageVector = if (isSecurityDisabled) {
                    Icons.Default.LockOpen // Open lock = security disabled
                } else {
                    Icons.Default.Lock // Closed lock = security enabled
                },
                contentDescription = if (isSecurityDisabled) {
                    "Sécurité désactivée (${timeRemaining}s restantes)"
                } else {
                    "Sécurité activée"
                },
                tint = if (isSecurityDisabled) {
                    Color.Black
                } else {
                    Color.White
                }
            )
        }

        if (showLabels) {
            Text(
                text = if (isSecurityDisabled) {
                    "Débloqué: ${timeRemaining}s"
                } else {
                    "Sécurisé"
                },
                modifier = Modifier
                    .background(
                        if (isSecurityDisabled) {
                            Color(0xFFFFEB3B)
                        } else {
                            Color(0xFF4CAF50)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (isSecurityDisabled) {
                    Color.Black
                } else {
                    Color.White
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
