package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun FloatingUpdateAllChecksFAB(
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val repo10 = aCentralFacade.repositorysMainSetter.repo10OperationVentCouleur

    // Check if security is disabled
    val isSecurityDisabled = activeCentralValues.le_pourvoire_clike_checked_est_active

    // Get all vents where lence_pour_check is true
    val ventsToUpdate = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        .filter { it.lence_pour_check }

    val hasVentsToUpdate = ventsToUpdate.isNotEmpty()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                if (!isSecurityDisabled) {
                    Toast.makeText(
                        context,
                        "⚠️ Déverrouillez la sécurité pour modifier (bouton cadenas jaune)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@FloatingActionButton
                }

                if (!hasVentsToUpdate) {
                    Toast.makeText(
                        context,
                        "✓ Aucune vente à mettre à jour",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@FloatingActionButton
                }

                // Update all vents where lence_pour_check is true
                coroutineScope.launch {
                    var successCount = 0
                    var errorCount = 0

                    ventsToUpdate.forEach { vent ->
                        try {
                            val updatedVent = vent.copy(
                                premier_Check_Donne = true,
                                lence_pour_check = false
                            )
                            repo10.update_If_Exist(updatedVent)
                            successCount++
                        } catch (e: Exception) {
                            errorCount++
                        }
                    }

                    val message = if (errorCount == 0) {
                        "✓ ${successCount} vente${if (successCount > 1) "s" else ""} mise${if (successCount > 1) "s" else ""} à jour"
                    } else {
                        "⚠️ ${successCount} succès, ${errorCount} erreur${if (errorCount > 1) "s" else ""}"
                    }

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            },
            containerColor = when {
                !isSecurityDisabled -> Color(0xFFBDBDBD) // Gray when locked
                hasVentsToUpdate -> Color(0xFF4CAF50) // Green when has updates
                else -> Color(0xFF9E9E9E) // Gray when nothing to update
            },
            contentColor = when {
                !isSecurityDisabled -> Color(0xFF757575)
                hasVentsToUpdate -> Color.White
                else -> Color.White
            }
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = if (!isSecurityDisabled) {
                    "🔒 Déverrouiller pour mettre à jour"
                } else if (hasVentsToUpdate) {
                    "Mettre à jour ${ventsToUpdate.size} vente${if (ventsToUpdate.size > 1) "s" else ""}"
                } else {
                    "Aucune vente à mettre à jour"
                },
                tint = when {
                    !isSecurityDisabled -> Color(0xFF757575)
                    hasVentsToUpdate -> Color.White
                    else -> Color.White
                }
            )
        }

        if (showLabels) {
            Text(
                text = when {
                    !isSecurityDisabled -> "🔒 Verrouillé"
                    hasVentsToUpdate -> "Tout valider (${ventsToUpdate.size})"
                    else -> "Rien à valider"
                },
                modifier = Modifier
                    .background(
                        when {
                            !isSecurityDisabled -> Color(0xFFBDBDBD)
                            hasVentsToUpdate -> Color(0xFF4CAF50)
                            else -> Color(0xFF9E9E9E)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = when {
                    !isSecurityDisabled -> Color(0xFF757575)
                    else -> Color.White
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
