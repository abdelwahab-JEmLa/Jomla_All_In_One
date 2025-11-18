package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent_2
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent_3
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenIts_FragFastVent_4
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WindowsShare
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.DropDownItem_WindowsShare_WithCredit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FabDropdownMenu_WhenIts_FragFastVent (
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
            DropDownItem_WhenIts_FragFastVent_4(
                nomFun = "DropDownItem_WhenIts_FragFastVent_4",
                onDismissDropdown = onDismissDropdown
            )

            DropDownItem_WindowsShare_WithCredit(
                nomFun = "Partager PDF Avec Versement Credit ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WindowsShare(
                nomFun = "Partager PDF",
                onDismissDropdown = onDismissDropdown
            )

            DropDownItem_WhenIts_FragFastVent_3(
                nomFun ="sort par entre ou classement  ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenIts_FragFastVent_2(
                nomFun ="givre le neveau classement ",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenIts_FragFastVent(
                nomFun = "affiche_CheckList_ChoisiseurActiveFilter",
                onDismissDropdown = onDismissDropdown
            )
        }
    }
}
