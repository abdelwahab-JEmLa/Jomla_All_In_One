package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_1
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_3
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_4
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FabDropdownMenu_WhenItsAchatsFragment(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(y = (-90).dp)
    ) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropDownItem_WhenItsAchatsFragment_4(
                nomFun = "supp vents",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenItsAchatsFragment_3(
                nomFun = "afficheFloatingOutlinedSearcher_of_Achat",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenItsAchatsFragment_2(
                nomFun = "update_M8BonVent confirmeCommande_TimeTamp",
                onDismissDropdown = onDismissDropdown
            )

            DropDownItem_WhenItsAchatsFragment_1(
                nomFun = "",
                onDismissDropdown = onDismissDropdown
            )

        }
    }
}
