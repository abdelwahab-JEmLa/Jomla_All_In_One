package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Filter

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.A.MemoireVive.ID2.Test.View.Z.List.View_MainList
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

@SuppressLint("DefaultLocale")
@Composable
fun MainFilter(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
) {
    val getter = viewModel.getter
    val datasGBonVentRepository = getter.gBonVentRepository.datasValue

    val listGBonVentFilteredByClientKeySorted =
        datasGBonVentRepository
            .filter {
                it.parentHClientKeyID == getter
                    .zAppComptRepositoryComposable
                    .currentAppCompt
                    ?.onVentFClientKeyID
            }
            .sortedByDescending { it.creationTimestamps }

    View_MainList(
        viewModel,
        listGBonVentFilteredByClientKeySorted,
    )
}
