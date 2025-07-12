package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Components.Ui.AppBar.Settings.TopAppBar_With_DropDownMenu
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Components.Ui.Dialog_Filter_Client
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Modules.Ui.Dialog_Choisire_Grossist_Modularized
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List.List_GroupeAchatProduit
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun Screen_GrossistAchatSec12FragID1(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar_With_DropDownMenu(
            viewModel,
            uiState
        )

        List_GroupeAchatProduit(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            viewModel = viewModel
        )
    }

    if (uiState.showDialog) {
        Dialog_Filter_Client(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.updateShowDialog(false) }
        )
    }

    val repo11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation

    if (uiState.dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen) {
        Dialog_Choisire_Grossist_Modularized(
            viewModel = viewModel,
        ) { grossistSelected ->
            if (grossistSelected != null) {
                // Update the filter with the selected grossist
                repo11AchatOperation.updateFilterQuery(
                    V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation.FilterQuery.Grossist(grossistSelected)
                )
            } else {
                // Clear the filter if no grossist is selected
                repo11AchatOperation.updateFilterQuery(
                    V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation.FilterQuery.NO_FILTER
                )
            }
            viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(pour_MainScreen = false)
        }
    }
}
