package V.DiviseParSections.App.Shared.A.MemoireVive.App.View.W.Filter

import V.DiviseParSections.App.Shared.A.MemoireVive.App.View.Z.List.View_MainList
import V.DiviseParSections.App.Shared.A.MemoireVive.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
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
