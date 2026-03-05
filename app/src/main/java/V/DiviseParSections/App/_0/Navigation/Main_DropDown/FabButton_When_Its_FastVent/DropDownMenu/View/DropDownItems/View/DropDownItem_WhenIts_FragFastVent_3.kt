package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Home.SortVentMode
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenIts_FragFastVent_3(
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    val currentValues = focusedValuesGetter.active_Central_Values

    // Determine current sort mode - default to PAR_Creation_Vent (Classement)
    val currentSortMode = when {
        currentValues.sortVentMode != null -> currentValues.sortVentMode!!
        currentValues.sortVentsParClassment -> SortVentMode.PAR_Creation_Vent
        else -> SortVentMode.PAR_Creation_Vent
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (currentSortMode) {
                SortVentMode.PAR_Creation_Vent -> MaterialTheme.colorScheme.primaryContainer
                SortVentMode.PAR_ENTREE -> MaterialTheme.colorScheme.secondaryContainer
                SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> MaterialTheme.colorScheme.tertiaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = when (currentSortMode) {
                        SortVentMode.PAR_Creation_Vent -> Icons.Default.Sort
                        SortVentMode.PAR_ENTREE -> Icons.Default.SortByAlpha
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> Icons.Default.AccessTime
                    },
                    contentDescription = null,
                    tint = when (currentSortMode) {
                        SortVentMode.PAR_Creation_Vent -> MaterialTheme.colorScheme.onPrimaryContainer
                        SortVentMode.PAR_ENTREE -> MaterialTheme.colorScheme.onSecondaryContainer
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )
            },
            text = {
                Text(
                    text = when (currentSortMode) {
                        // Currently on Classement, clicking will go to Alphabétique
                        SortVentMode.PAR_Creation_Vent -> "Actuel: Plus récent d'abord → Alphabétique"
                        // Currently on Alphabétique, clicking will go to Dernière Vérification
                        SortVentMode.PAR_ENTREE -> "Actuel: Alphabétique → Vérification"
                        // Currently on Vérification, clicking will go to Classement
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> "Actuel: Dernière Vérification → Classement"
                    },
                    color = when (currentSortMode) {
                        SortVentMode.PAR_Creation_Vent -> MaterialTheme.colorScheme.onPrimaryContainer
                        SortVentMode.PAR_ENTREE -> MaterialTheme.colorScheme.onSecondaryContainer
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )
            },
            onClick = {
                // Cycle: Classement → Alphabétique → Vérification → Classement
                val nextMode = when (currentSortMode) {
                    SortVentMode.PAR_Creation_Vent -> SortVentMode.PAR_ENTREE
                    SortVentMode.PAR_ENTREE -> SortVentMode.PAR_DERNIERE_UPDATE_LENCE
                    SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> SortVentMode.PAR_Creation_Vent
                }

                val updatedValues = currentValues.copy(
                    sortVentMode = nextMode,
                    // Keep backward compatibility
                    sortVentsParClassment = nextMode == SortVentMode.PAR_Creation_Vent
                )
                focusedValuesGetter.update_activeCentralValues(updatedValues)

                Toast.makeText(
                    context,
                    when (nextMode) {
                        SortVentMode.PAR_Creation_Vent -> "✓ Tri: Par Classement (Plus récent d'abord)"
                        SortVentMode.PAR_ENTREE -> "✓ Tri: Par Ordre Alphabétique (A-Z)"
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> "✓ Tri: Par Dernière Vérification"
                    },
                    Toast.LENGTH_SHORT
                ).show()

                onDismissDropdown()
            }
        )
    }
}
