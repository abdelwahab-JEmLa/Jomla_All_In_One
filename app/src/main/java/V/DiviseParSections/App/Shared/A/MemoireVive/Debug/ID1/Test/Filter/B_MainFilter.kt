package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.List.View_MainList
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(vm: E0AfficheHistoriqueTransactionsViewModel, tagParentKey_Client: String) {
    val filtered = vm.getter.gBonVentRepository.datasValue.filter {
        HClientInfos.extractSonKeyByParent(it.keyByParent) == HClientInfos.extractSonKeyByParent(tagParentKey_Client)
    }.sortedByDescending { it.creationTimestamps }

    View_MainList(vm, filtered, Modifier)
}
