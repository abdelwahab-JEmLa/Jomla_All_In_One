package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_MainPresenter

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_MainPresenter.Filter.FilterDropdownMenu
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

/**
 * FIXED: Now properly uses the updated FilterDropdownMenu which integrates
 * with FocusedValuesGetter automatically. No need to pass filterState manually.
 */
@Composable
fun FabDropdownMenu_WhenIts_FacadeBoutiqueElectro(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    var showFilterDialog by remember { mutableStateOf(false) }

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
                    showFilterDialog = true
                    onDismissDropdown()
                }
            )
        }

        // FIXED: Filter Dialog now automatically retrieves and updates FilterState
        // through FocusedValuesGetter - no manual state management needed
        if (showFilterDialog) {
            FilterDropdownMenu(
                onDismiss = { showFilterDialog = false }
            )
        }
    }
}
