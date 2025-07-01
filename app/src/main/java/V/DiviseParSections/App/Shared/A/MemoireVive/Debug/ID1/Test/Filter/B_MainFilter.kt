package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.List.View_MainList
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@SuppressLint("DefaultLocale")
@Composable
fun MainFilter(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    parentTag_ClientKey: String,
) {
    val getter = viewModel.getter
    val datasGBonVentRepository = getter.gBonVentRepository.datasValue

    val listGBonVentFilteredByClientKeySorted =
        datasGBonVentRepository
            .filter {
                it.parentHClientKeyID == getter
                    .zAppComptRepositoryComposable
                    .currentAppCompt
                    ?.bOuvertDialogMapMarqueHClientKey
            }
            .sortedByDescending { it.creationTimestamps }

    View_MainList(
        modifier = Modifier
            .testTag("MainFilter")
          ,
        viewModel,
        listGBonVentFilteredByClientKeySorted,
    )
}
