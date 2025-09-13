package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FilterList
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
fun DropDownItem_WhenIts_FragFastVent(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    val currentValues = focusedValuesGetter.active_Central_Values

    // FIXED: Use the correct boolean field for CheckList visibility
    val isCheckListVisible = currentValues.affiche_CheckList_ChoisiseurActiveFilter

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCheckListVisible) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (isCheckListVisible) Icons.Default.Checklist else Icons.Default.FilterList,
                    contentDescription = null,
                    tint = if (isCheckListVisible) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            text = {
                Text(
                    text = if (isCheckListVisible)
                        "Masquer CheckList Filtres"
                    else
                        "Afficher CheckList Filtres",
                    color = if (isCheckListVisible) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                // FIXED: Toggle the correct field for CheckList visibility
                val updatedValues = currentValues.copy(
                    affiche_CheckList_ChoisiseurActiveFilter = !isCheckListVisible
                )
                focusedValuesGetter.update_activeCentralValues(updatedValues)

                Toast.makeText(
                    context,
                    if (isCheckListVisible)
                        "CheckList des Filtres Masquée"
                    else
                        "CheckList des Filtres Affichée",
                    Toast.LENGTH_SHORT
                ).show()

                onDismissDropdown()
            }
        )
    }
}
