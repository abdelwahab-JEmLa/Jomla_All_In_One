package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues.FilterState_Facad_Boutique
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.Filter.FilterDropdownMenu_Its_FacadeElectroBoutique
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu_WhenIts_FacadeBoutiqueElectro(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    // FIXED: Get current FilterState from FocusedValuesGetter
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val currentFilterState = activeCentralValues.filterStateFacadBoutique ?: FilterState_Facad_Boutique()

    Box(modifier = modifier) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
        ) {
            // Single menu item - Opens the FilterDropdownMenu dialog
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Text(
                        text = "Filtres et tri",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    // FIXED: Update affiche_dialog_editeur in FilterState instead of local state
                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            filterStateFacadBoutique = currentFilterState.copy(
                                affiche_dialog_editeur = true
                            )
                        )
                    )
                    onDismissDropdown()
                }
            )
        }

        // FIXED: Show dialog based on affiche_dialog_editeur from FilterState
        if (currentFilterState.affiche_dialog_editeur) {
            FilterDropdownMenu_Its_FacadeElectroBoutique(
                onDismiss = {
                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            filterStateFacadBoutique = currentFilterState.copy(
                                affiche_dialog_editeur = false
                            )
                        )
                    )
                }
            )
        }
    }
}
