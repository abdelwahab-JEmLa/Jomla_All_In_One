package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.But.View.cleanupInvalidOperations_Np
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.But.View.cleanupOldBonVents_Np
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.koinInject

@Composable
fun Fab_CleanupM8AndM10(
    on_vent_key: String,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    onDismissDropdown: () -> Unit,
) {
    val sizeM8 = repositorysMainGetter.repo8BonVent.datasValue.size
    val sizeM10 = repositorysMainGetter.repo10OperationVentCouleur.datasValue.size
    val sizeM11 = repositorysMainGetter.repo11AchatOperation.datasValue.size

    // Click guard: prevents a second accidental trigger while cleanup is running
    var isRunning by remember { mutableStateOf(false) }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = null,
                tint = if (isRunning)
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = if (isRunning)
                    "Running..."
                else
                    "Cleanup  M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isRunning)
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.onSurface
            )
        },
        enabled = !isRunning,
        onClick = {
            if (isRunning) return@DropdownMenuItem
            isRunning = true

            cleanupOldBonVents_Np(
                repositorysMainGetter.repo8BonVent,
                repositorysMainGetter.repo8BonVent.datasValue,
                on_vent_key = on_vent_key
            )
            cleanupInvalidOperations_Np(
                repositorysMainGetter.repo10OperationVentCouleur,
                on_vent_key = on_vent_key
            )
            M11AchatOperation.Companion.remove_ref()

            onDismissDropdown()
        }
    )
}
