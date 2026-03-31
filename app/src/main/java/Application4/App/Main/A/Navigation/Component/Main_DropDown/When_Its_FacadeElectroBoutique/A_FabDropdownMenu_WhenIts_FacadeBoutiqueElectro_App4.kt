package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.Affiche_ProduitDataBaseEdites_ComposableViews_ActiveCompt_Update_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.SyncDropboxImages_DropdownMenuItem
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views.UpdateActiveComptDo_DropdownMenuItem
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
            UpdateActiveComptDo_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            Affiche_ProduitDataBaseEdites_ComposableViews_ActiveCompt_Update_DropdownMenuItem(
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                onDismissDropdown = onDismissDropdown
            )

            SyncDropboxImages_DropdownMenuItem(viewModelNewProtoPatterns, onDismissDropdown)

        //    PrioriterToggle_DropdownMenuItem(viewModelNewProtoPatterns)
                /*
            Upload_Filtered_M03Couleurs_DropdownMenuItem_App4(
                viewModelNewProtoPatterns,
                onDismissDropdown
            )                 */
        }
    }
}

