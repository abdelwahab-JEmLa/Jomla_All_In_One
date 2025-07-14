package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.AppBar.Settings.TopAppBar_With_DropDownMenu
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_Client
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.Dialog_Choisire_Grossist_Modularized
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.List_GroupeAchatProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
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

 

    // Grossist selection dialog for main screen
    if (uiState.dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen) {
        val repo11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation

        Dialog_Choisire_Grossist_Modularized(
            titel = "Choisir un Grossiste",
            viewModel = viewModel,
            list_M11AchatOperation = repo11AchatOperation.datasValue
        ) { grossistSelected ->
            // Handle grossist selection
            if (grossistSelected != null) {
                repo11AchatOperation.updateFilterQuery(
                    Repo11AchatOperation.FilterQuery.Grossist(grossistSelected)
                )
            } else {
                repo11AchatOperation.updateFilterQuery(
                    Repo11AchatOperation.FilterQuery.NO_FILTER
                )
            }

            // Close the dialog
            viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(
                pour_MainScreen = false
            )
        }
    }

    if (uiState.show_Dialog_filter_AChats_Par_Client_Acheteur) {
        Dialog_Filter_Client(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { selectedClient ->
                // Handle client selection similar to grossist selection
                val repo11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation

                if (selectedClient != null) {
                    // Apply client filter
                    repo11AchatOperation.updateFilterQuery(
                        Repo11AchatOperation.FilterQuery.Client(selectedClient)
                    )
                } else {
                    // Clear filter
                    repo11AchatOperation.updateFilterQuery(
                        Repo11AchatOperation.FilterQuery.NO_FILTER
                    )
                }

                // Close the dialog
                viewModel.update_show_Dialog_filter_AChats_Par_Client_Acheteur(false)
            }
        )
    }
}
