package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.z.Com

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HearingDisabled
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Lence_pour_check_Handler(
    aCentralFacade: ACentralFacade = koinInject(),
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    allNonTrouve: Boolean = false,
    hasNonTrouve: Boolean = false,
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>
) {
    // Determine if all items have lence_pour_check = true
    val allLencePourCheck = remember(relative_List_M10OperationVentCouleur) {
        relative_List_M10OperationVentCouleur.isNotEmpty() &&
                relative_List_M10OperationVentCouleur.all { it.lence_pour_check }
    }

    IconButton(
        onClick = {
            // Toggle lence_pour_check for all items in the list
            val newLenceState = !allLencePourCheck
            relative_List_M10OperationVentCouleur.forEach { ventCouleur ->
                repo10OperationVentCouleur.update_If_Exist(
                    ventCouleur.copy(lence_pour_check = newLenceState)
                )
            }
        },
        modifier = Modifier.Companion
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (allLencePourCheck) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
                } else {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
                }
            )
    ) {
        Icon(
            imageVector = if (allLencePourCheck) Icons.Default.Hearing else Icons.Default.HearingDisabled,
            contentDescription = if (allLencePourCheck) "Disable lence pour check" else "Enable lence pour check",
            tint = if (allLencePourCheck) {
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            } else {
                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            },
            modifier = Modifier.Companion.size(20.dp)
        )
    }
}
