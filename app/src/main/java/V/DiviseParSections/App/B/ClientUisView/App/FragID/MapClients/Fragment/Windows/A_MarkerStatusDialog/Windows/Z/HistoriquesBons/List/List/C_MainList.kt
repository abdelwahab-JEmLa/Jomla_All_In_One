package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun View_MainList(
    listGBonVentFilteredByClientKeySorted: List<M8BonVent>,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    modifier: Modifier =Modifier,
) {
    Column(
        modifier = Modifier
            .getSemanticsTag(
                nomVal = "listGBonVentFilteredByClientKeySorted",
                data = listGBonVentFilteredByClientKeySorted
            )
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        // In C_MainList.kt
        listGBonVentFilteredByClientKeySorted.forEach { transaction ->
            key(transaction.keyID) {
                View_MainItem(

                    viewModel = viewModel,
                    relative_M8BonVent = transaction,
                )
            }
        }
    }
}
