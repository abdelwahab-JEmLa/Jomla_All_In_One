package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ToggleButton
import org.koin.compose.koinInject

@Composable
fun Coupe_Colle_Buttons(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    relative_Period: M14VentPeriode
) {
    val context = LocalContext.current
    val activeCentralValues by focusedValuesGetter::active_Central_Values
    val heldPeriod = activeCentralValues.held_Period_Pour_copie_Leur_Vents
    val isCopyActive = heldPeriod != null && heldPeriod.keyID == relative_Period.keyID

    fun handel_Click_CoupeHold() {
        val newHeldPeriod = if (isCopyActive) null else relative_Period
        focusedValuesGetter.update_activeCentralValues(
            activeCentralValues.copy(
                held_Period_Pour_copie_Leur_Vents = newHeldPeriod
            )
        )
    }

    fun handel_Click_Colle() {
        if (heldPeriod != null) {
            focusedValuesGetter.update_activeCentralValues(
                activeCentralValues.copy(
                    held_Period_Pour_copie_Leur_Vents = null
                )
            )

            // Show toast message
            Toast.makeText(
                context,
                "Coupe terminée de ${heldPeriod.get_DebugInfos()} vers ${relative_Period.get_DebugInfos()}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Aucune période sélectionnée pour la copie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Copy Toggle Button
            ToggleButton(
                checked = isCopyActive,
                onCheckedChange = { handel_Click_CoupeHold() },
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Couper",
                        tint = if (isCopyActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = if (isCopyActive) "Copié" else "Couper",
                        color = if (isCopyActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Paste Button
            OutlinedButton(
                onClick = { handel_Click_Colle() },
                enabled = heldPeriod != null,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Coller"
                    )
                    Text("Coller")
                }
            }
        }
    }
}
