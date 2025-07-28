package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.Filter

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View_MainList
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainFilter(
    markerStatusDialogM2Client: M2Client?,
    vm: E0AfficheHistoriqueTransactionsViewModel,
    modifier: Modifier,
) {
    val datasValue_repo8BonVent = vm.getter.repo8BonVent.datasValue
    val filtered by remember(
        datasValue_repo8BonVent.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        markerStatusDialogM2Client?.keyID
    ) {
        derivedStateOf {
            datasValue_repo8BonVent.filter {
                it.parent_M2Client_KeyID == (markerStatusDialogM2Client?.keyID ?: "")
            }.sortedByDescending { it.creationTimestamps }
        }
    }

    ElevatedCard(
        Modifier
            .getSemanticsTag(datasValue_repo8BonVent,"")
            .padding(2.dp)
    ) {
        View_MainList(filtered, vm)
    }
}
