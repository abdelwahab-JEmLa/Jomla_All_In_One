package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.PrioriterToggle_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.UpdateActiveCompt_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.UploadFilteredData_DropdownMenuItem.View.UploadFilteredData_DropdownMenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns = koinViewModel(),
) {
    Box(modifier = modifier) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
        ) {
            UpdateActiveCompt_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            PrioriterToggle_DropdownMenuItem(viewModelNewProtoPatterns)

            UploadFilteredData_DropdownMenuItem(
                viewModelNewProtoPatterns.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur
                ,onDismissDropdown
            )
        }
    }
}

