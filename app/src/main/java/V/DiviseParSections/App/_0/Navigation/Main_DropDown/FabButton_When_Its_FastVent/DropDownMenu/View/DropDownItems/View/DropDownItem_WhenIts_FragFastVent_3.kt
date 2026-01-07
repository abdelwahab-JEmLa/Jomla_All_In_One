package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.SortVentMode
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
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
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    val currentValues = focusedValuesGetter.active_Central_Values

    // Determine sort mode from both new and legacy fields
    val sortMode = when {
        currentValues.sortVentMode != null -> currentValues.sortVentMode
        currentValues.sortVentsParClassment -> SortVentMode.PAR_Creation_Vent
        else -> SortVentMode.PAR_ENTREE
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (sortMode) {
                SortVentMode.PAR_Creation_Vent -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = when (sortMode) {
                        SortVentMode.PAR_Creation_Vent -> Icons.Default.SortByAlpha
                        SortVentMode.PAR_ENTREE -> Icons.Default.Sort
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> Icons.Default.AccessTime
                    },
                    contentDescription = null,
                    tint = when (sortMode) {
                        SortVentMode.PAR_Creation_Vent -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            text = {
                Text(
                    text = when (sortMode) {
                        SortVentMode.PAR_Creation_Vent -> "Trier par Entrée"
                        SortVentMode.PAR_ENTREE -> "Trier par Dernière Vérification"
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> "Trier par Classement"
                    },
                    color = when (sortMode) {
                        SortVentMode.PAR_Creation_Vent -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                // Cycle through sort modes: Classement -> Entrée -> Dernière Update -> Classement
                val nextMode = when (sortMode) {
                    SortVentMode.PAR_Creation_Vent -> SortVentMode.PAR_ENTREE
                    SortVentMode.PAR_ENTREE -> SortVentMode.PAR_DERNIERE_UPDATE_LENCE
                    SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> SortVentMode.PAR_Creation_Vent
                }

                val updatedValues = currentValues.copy(
                    sortVentMode = nextMode,
                    // Keep backward compatibility with sortVentsParClassment flag
                    sortVentsParClassment = nextMode == SortVentMode.PAR_Creation_Vent
                )
                focusedValuesGetter.update_activeCentralValues(updatedValues)

                Toast.makeText(
                    context,
                    when (nextMode) {
                        SortVentMode.PAR_Creation_Vent -> "Tri changé vers: Par Classement"
                        SortVentMode.PAR_ENTREE -> "Tri changé vers: Par Entrée"
                        SortVentMode.PAR_DERNIERE_UPDATE_LENCE -> "Tri changé vers: Par Dernière Vérification"
                    },
                    Toast.LENGTH_SHORT
                ).show()

                onDismissDropdown()
            }
        )
    }
}
