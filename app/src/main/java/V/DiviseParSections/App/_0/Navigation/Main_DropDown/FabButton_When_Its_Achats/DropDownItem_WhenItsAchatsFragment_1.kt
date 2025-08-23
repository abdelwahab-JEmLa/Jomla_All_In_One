package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenItsAchatsFragment_1(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    // Fixed: Now it's a toggle button for affiche_Floating_Button_SelecteCategorieEtAddNewProduit
    val currentValues = focusedValuesGetter.active_Central_Values
    val isFloatingButtonVisible = currentValues.affiche_Floating_Button_SelecteCategorieEtAddNewProduit

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFloatingButtonVisible) {
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
                    imageVector = if (isFloatingButtonVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = if (isFloatingButtonVisible) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            text = {
                Text(
                    text = if (isFloatingButtonVisible)
                        "Hide Category Selection Button"
                    else
                        "Show Category Selection Button",
                    color = if (isFloatingButtonVisible) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                // Toggle the floating button visibility
                val updatedValues = currentValues.copy(
                    affiche_Floating_Button_SelecteCategorieEtAddNewProduit = !isFloatingButtonVisible
                )
                focusedValuesGetter.update_activeCentralValues(updatedValues)

                Toast.makeText(
                    context,
                    if (isFloatingButtonVisible)
                        "Category Selection Button Hidden"
                    else
                        "Category Selection Button Shown",
                    Toast.LENGTH_SHORT
                ).show()

                onDismissDropdown()
            }
        )
    }
}
