package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.InitFiltered_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.InitFull_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.NextStart_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.PrioriterToggle_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.Toggle_Active_section_ToggleButton_TagPreiorities__start_Collapsed_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.UpdateActiveCompt_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.UploadFilteredData_DropdownMenuItem.View.Upload_Filtered_M03Couleurs_DropdownMenuItem_App4
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
            // Init filtrée : deleteAll puis re-seed avec refs filtrées (comme Initializer_Funcs_app2)
            InitFiltered_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            // Init complète : deleteAll puis re-seed tout Firebase (comme Initializer_App4)
            InitFull_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            UpdateActiveCompt_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            SyncDropboxImages_DropdownMenuItem(viewModelNewProtoPatterns, onDismissDropdown)

            // Lets the user pick a Do value written to active_M9Compt.next_start
            NextStart_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            Toggle_Active_section_ToggleButton_TagPreiorities__start_Collapsed_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            PrioriterToggle_DropdownMenuItem(viewModelNewProtoPatterns)

            Upload_Filtered_M03Couleurs_DropdownMenuItem_App4(
                viewModelNewProtoPatterns,
                onDismissDropdown
            )
        }
    }
}
