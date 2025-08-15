package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.AppBar.Settings.TopAppBar_With_DropDownMenu
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_Client
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_VentPeriod.Dialog_Filter_VentPeriod
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.Dialog_Choisire_Grossist_Modularized
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.List_GroupeAchatProduit
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun Screen_GrossistAchatSec12FragID1(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar_With_DropDownMenu(viewModel, uiState = uiState)
        List_GroupeAchatProduit(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            viewModel = viewModel
        )
    }

    if (uiState.dialog_Filter_VentPeriod_showDialog) {
        Dialog_Filter_VentPeriod(viewModel) { period ->
            if (period != null) {
                focusedValuesGetter.addPeriodFilter(period)
            } else {
                focusedValuesGetter.removePeriodFilter()
            }
            viewModel.update_dialog_Filter_VentPeriod_showDialog(false)
        }
    }

    if (uiState.dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen) {
        Dialog_Choisire_Grossist_Modularized(
            titel = "Choisir un Grossiste",
            viewModel = viewModel,
            list_M11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
        ) { grossist ->
            if (grossist != null) {
                focusedValuesGetter.addGrossistFilter(grossist)
            } else {
                focusedValuesGetter.removeGrossistFilter()
            }
            viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(pour_MainScreen = false)
        }
    }

    if (uiState.show_Dialog_filter_AChats_Par_Client_Acheteur) {
        Dialog_Filter_Client(uiState, viewModel) { client ->
            if (client != null) {
                focusedValuesGetter.addClientFilter(client)
            } else {
                focusedValuesGetter.removeClientFilter()
            }
            viewModel.update_show_Dialog_filter_AChats_Par_Client_Acheteur(false)
        }
    }
}
