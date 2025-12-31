package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List.Z.ClientSearchItem.View.ClientSearchItem
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
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

/**
 * Trie une liste de clients par leur dernière opération de vente (M10OperationVentCouleur)
 * Les clients avec les opérations les plus récentes apparaissent en premier
 */
fun sortClientsByLastVentOperation(
    clients: List<M2Client>,
    repositorysMainGetter: RepositorysMainGetter
): List<M2Client> {
    return clients.sortedByDescending { client ->
        getLastVentOperationTimestamp(client, repositorysMainGetter)
    }
}

/**
 * Récupère le timestamp de la dernière opération de vente pour un client donné
 * @return Le timestamp de la dernière synchronisation ou 0L si aucune opération n'existe
 */
private fun getLastVentOperationTimestamp(
    client: M2Client,
    repositorysMainGetter: RepositorysMainGetter
): Long {
    // Get the last M8BonVent for this client
    val lastBonVent = repositorysMainGetter.get_Last_M8BonVent_Par_M2Client(client)
        ?: return 0L

    // Find all M10OperationVentCouleur for this BonVent
    val ventOperations = repositorysMainGetter.repo10OperationVentCouleur.datasValue
        .filter { it.parent_M8BonVent_KeyId == lastBonVent.keyID }

    // Get the most recent dernierTimeTampsSynchronisationAvecFireBase
    return ventOperations.maxOfOrNull { it.dernierTimeTampsSynchronisationAvecFireBase } ?: 0L
}

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
