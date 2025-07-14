package V.DiviseParSections.App.D4.ControleApps.App.FragID2.D.Fragment

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun View_M9AppCompt(
    relative_M9AppCompt: Z_AppCompt,
    viewModel: ViewModel_M9AppCompt,
) {
    val focusedActiveValuesFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
    val currentActiveFocuced_M14VentPeriode = focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
    val active = (currentActiveFocuced_M14VentPeriode?.keyID ?: "") == relative_M9AppCompt.keyID

    val backgroundColor = when {
        active -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val heurDebutInString = "Now Test HH:mm"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (active) {
            Text(
                text = "Selected Periode",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "m14VentPeriode: ${relative_M9AppCompt.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Heure de début: $heurDebutInString",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
