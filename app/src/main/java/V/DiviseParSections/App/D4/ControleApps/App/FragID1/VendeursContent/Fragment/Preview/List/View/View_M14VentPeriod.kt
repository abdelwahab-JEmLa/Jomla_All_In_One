package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
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
fun View_M14VentPeriod(
    viewModel: ViewModel_M14VentPeriod,
    aCentralFacade: ACentralFacade= viewModel.aCentralFacade,
    relative_M14VentPeriode: M14VentPeriode,
    relative_M9: Z_AppCompt? = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentM9AppCompt,
) {
    val focusedActiveValuesFacade = viewModel.aCentralFacade.focusedActiveValuesFacade
    val currentActiveFocuced_M14VentPeriode = focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
    val active = (currentActiveFocuced_M14VentPeriode?.keyID ?: "") == relative_M14VentPeriode.keyID

    val backgroundColor = when {
        active -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val heurDebutInString = "Now Test HH:mm"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (relative_M9 != null) {
                    aCentralFacade.repositorysMainSetter.update_M9AppCompt(
                        relative_M9.copy(
                            current_OnVent_M14VentPeriode_KeyID = relative_M14VentPeriode.keyID ,
                            current_OnVent_M14VentPeriode_DebugInfos = relative_M14VentPeriode.get_DebugInfos()
                        )
                    )
                }
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
                text = "m14VentPeriode: ${relative_M14VentPeriode.get_DebugInfos()}",
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
