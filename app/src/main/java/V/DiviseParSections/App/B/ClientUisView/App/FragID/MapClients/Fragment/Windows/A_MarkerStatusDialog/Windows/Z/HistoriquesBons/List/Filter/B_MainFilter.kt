package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.Filter

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View_MainList
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.ID2HClientInfos.Repository.HClientInfos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    modifier: Modifier.Companion,
    vm: E0AfficheHistoriqueTransactionsViewModel,
    tagParentKey_Client: String
) {
    val filtered = vm.getter.gBonVentRepository.datasValue.filter {
        HClientInfos.extractSonKeyByParent(it.keyByParent) == HClientInfos.extractSonKeyByParent(tagParentKey_Client)
    }.sortedByDescending { it.creationTimestamps }

    View_MainList(vm, filtered, modifier)
}
