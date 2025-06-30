package V.DiviseParSections.App.Shared.A.MemoireVive.ID2.Test.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun View_MainList(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    listGBonVentFilteredByClientKeySorted: List<GBonVent>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // In C_MainList.kt
        listGBonVentFilteredByClientKeySorted.forEach { transaction ->
            key(transaction.keyByParent) {
                View_MainItem(
                    viewModel = viewModel,
                    bonVent = transaction,
                )
            }
        }
    }
}
