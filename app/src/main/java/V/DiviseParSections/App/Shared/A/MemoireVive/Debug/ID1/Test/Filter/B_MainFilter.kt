package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.List.View_MainList
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import android.annotation.SuppressLint
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@SuppressLint("DefaultLocale")
@Composable
fun MainFilter(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    parentTag_ClientKey: String,
) {
    val client = viewModel.getter.hClientRepository.findHClientInfosByKey(parentTag_ClientKey)

    val getter = viewModel.getter
    val datasGBonVentRepository = getter.gBonVentRepository.datasValue

    val listGBonVentFilteredByClientKeySorted =
        datasGBonVentRepository
            .filter {
                it.parentHClientKeyID == client.keyID
            }
            .sortedByDescending { it.creationTimestamps }

    Card (Modifier.testTag("${datasGBonVentRepository.map { it.keyByParent }} ${client.id}")) {
        View_MainList(
            viewModel,
            listGBonVentFilteredByClientKeySorted,
        )
    }
}
