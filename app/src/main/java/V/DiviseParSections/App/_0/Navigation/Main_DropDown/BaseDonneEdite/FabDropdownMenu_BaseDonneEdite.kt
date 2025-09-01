package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownItem_WhenItsAchatsFragment_1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu_BaseDonneEdite(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
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
            DropDownItemWBaseDonne_2(
                nomFun = "FABs Mode Edites Produit",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItemWBaseDonne_1(
                nomFun = "Givre le neveau Classement",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenItsAchatsFragment_1(
                nomFun = "",
                onDismissDropdown = onDismissDropdown
            )
        }
    }
}
