package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.List.View_MainList
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

private fun String.extractClientKey(keyComplet: String): String? {
    return keyComplet
        .split("--${this}-")
        .getOrNull(1)
        ?.split("--")
        ?.first()
}

@SuppressLint("DefaultLocale")
@Composable
fun MainFilter(vm: E0AfficheHistoriqueTransactionsViewModel, keyParent_Client: String) {
    val data = vm.getter.gBonVentRepository.datasValue
    val targetKey = HClientInfos.keyModel.extractClientKey(keyComplet = keyParent_Client)

    val filtered = data.filter {
        HClientInfos.keyModel.extractClientKey(it.keyByParent) == targetKey
    }.sortedByDescending { it.creationTimestamps }

    View_MainList(vm, filtered, Modifier
        .testTag(Z_AppCompt.keyModelValID7.extractClientKey(keyComplet = keyParent_Client) ?: "")
    )
}
