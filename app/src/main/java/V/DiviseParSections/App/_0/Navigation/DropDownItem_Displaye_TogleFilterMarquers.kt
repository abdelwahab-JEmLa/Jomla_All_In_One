package V.DiviseParSections.App._0.Navigation

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun DropDownItem_Displaye_TogleFilterMarquers(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val isFilterMarkersVisible = currentValues.affiche_Floating_Button_TogleFilterMarquers

    Card(
        modifier = Modifier.Companion.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFilterMarkersVisible)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (isFilterMarkersVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Filter Markers",
                    tint = if (isFilterMarkersVisible)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            text = {
                Text(
                    text = if (isFilterMarkersVisible)
                        "Hide Filter Markers"
                    else
                        "Show Filter Markers",
                    color = if (isFilterMarkersVisible)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                // Toggle the filter markers visibility
                val newValues = currentValues.copy(
                    affiche_Floating_Button_TogleFilterMarquers = !isFilterMarkersVisible
                )
                focusedValuesGetter.update_activeCentralValues(newValues)
            }
        )
    }
}
