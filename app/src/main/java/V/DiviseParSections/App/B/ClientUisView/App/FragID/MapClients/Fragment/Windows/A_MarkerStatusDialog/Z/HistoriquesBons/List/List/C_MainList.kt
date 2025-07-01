package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.petitePaddine
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.A.MemoireVive.ID2.Test.View.Z.List.View_MainItem
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun View_MainList(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    listGBonVentFilteredByClientKeySorted: List<GBonVent>,
    modifier: Modifier =Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(petitePaddine)
            .testTag(listGBonVentFilteredByClientKeySorted.size.toString())
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
