package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.z.Com

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ToggleButton_PremierCheckDonne(
    ventList: List<M10OperationVentCouleur>,
    onToggle: (Boolean) -> Unit,
    positionIndex: Int, // Position du produit dans la grille (1-4)
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val activeCentralValues = focusedValuesGetter.active_Central_Values

    // FIXED TODO: Check if secure click is active
    val isSecureClickEnabled = activeCentralValues.le_pourvoire_clike_checked_est_active

    val allChecked = remember(ventList) {
        ventList.isNotEmpty() && ventList.all { it.premier_Check_Donne }
    }

    // Determine what the new state should be when toggled
    val newStateWhenToggled = !allChecked

    FloatingActionButton(
        onClick = {
            // FIXED TODO: Only allow toggle if secure click is enabled
            if (isSecureClickEnabled) {
                onToggle(newStateWhenToggled)

                // FIXED TODO(1): When toggling from OFF to ON, deactivate lence_pour_check
                if (newStateWhenToggled) { // If we're setting to checked (ON)
                    ventList.forEach { ventCouleur ->
                        repo10OperationVentCouleur.update_If_Exist(
                            ventCouleur.copy(lence_pour_check = false)
                        )
                    }
                }
            }
        },
        modifier = modifier.size(48.dp),
        containerColor = when {
            !isSecureClickEnabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            allChecked -> Color(0xFFFFEB3B) // Yellow when checked
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = when {
            !isSecureClickEnabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            allChecked -> Color.Black
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Text(
            text = positionIndex.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                !isSecureClickEnabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                allChecked -> Color.Black
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
