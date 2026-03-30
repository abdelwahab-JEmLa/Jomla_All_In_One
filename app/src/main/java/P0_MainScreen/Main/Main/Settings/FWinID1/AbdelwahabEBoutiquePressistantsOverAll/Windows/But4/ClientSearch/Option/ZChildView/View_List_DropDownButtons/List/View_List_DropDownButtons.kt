package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List.Z.ClientSearchItem.View.ClientSearchItem
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.sortClientsByLastVentOperation
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun View_List_DropDownButtons(
    clientsWithCommandBonVents: List<M2Client>,
    filteredClients: List<M2Client>,
    onClientSelectedToToast: (M2Client) -> Unit,
    onSearchModeChanged: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onShowDropdownChanged: (Boolean) -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1 ,
    repositorysMainGetter: RepositorysMainGetter = koinInject()
) {
    // Sort clients by their last vent operation timestamp (most recent first)
    val sortedClients = remember(filteredClients, viewModel) {
        sortClientsByLastVentOperation(filteredClients, repositorysMainGetter)
    }

    Card(
        modifier = Modifier
            .width(200.dp)
            .heightIn(max = 200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyColumn(
            Modifier.getSemanticsTag(
                nomVal = "clientsWithCommandBonVents",
                data = clientsWithCommandBonVents
            )
        ) {
            items(sortedClients) { client ->
                ClientSearchItem(
                    m2Client = client,
                    onClick = {
                        onClientSelectedToToast(client)
                        onSearchModeChanged(false)
                        onSearchQueryChanged("")
                        onShowDropdownChanged(false)
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}
