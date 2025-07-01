package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.List.View_MainList
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.Z_AppCompt
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.wear.compose.material.Text

@Composable
fun MainFilter(vm: E0AfficheHistoriqueTransactionsViewModel, tagParentKey_Client: String) {
    val targetKey =  HClientInfos.extractSonKeyByParent(tagParentKey_Client)

    val filtered = vm.getter.gBonVentRepository.datasValue.filter {
        HClientInfos.extractSonKeyByParent(it.keyByParent) == targetKey
    }.sortedByDescending { it.creationTimestamps }

    Column {
       Z_AppCompt.extractSonKeyByParent(tagParentKey_Client,)?.let { Text("ID7: $it", color = Red) }
        View_MainList(vm, filtered, Modifier)
    }
}
