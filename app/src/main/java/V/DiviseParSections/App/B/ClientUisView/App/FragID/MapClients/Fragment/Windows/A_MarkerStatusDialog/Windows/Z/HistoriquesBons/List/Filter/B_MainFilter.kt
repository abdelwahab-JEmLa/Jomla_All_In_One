package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.Filter

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View_MainList
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainFilter(
    markerStatusDialogM2Client: HClientInfos?,
    vm: E0AfficheHistoriqueTransactionsViewModel,
    modifier: Modifier,
) {
    val datasValue = vm.getter.repo8BonVent.datasValue
    val filtered = datasValue.filter {
        it.parentM2ClientInfosKey == (markerStatusDialogM2Client?.keyID ?: "")
    }.sortedByDescending { it.creationTimestamps }

    ElevatedCard(
        Modifier
            .getSemanticsTag(datasValue, "datasValue")
            .padding(2.dp)
    ) {
        View_MainList(filtered, vm)
    }
}
