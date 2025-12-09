package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.View_MainItem_CreditOuVersemment_Enhanced
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
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
    modifier: Modifier = Modifier,
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
        listGBonVentFilteredByClientKeySorted.forEach { transaction ->
            key(transaction.keyID) {
                // Use View_MainItem_CreditOuVersemment for Credit and Versement transactions
                when (transaction.etateActuellementEst) {
                    M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
                    M8BonVent.EtateActuellementEst.Credit,
                    M8BonVent.EtateActuellementEst.Versemment,
                    M8BonVent.EtateActuellementEst.Demande_Versemet -> {
                        View_MainItem_CreditOuVersemment_Enhanced(
                            viewModel = viewModel,
                            relative_M8BonVent = transaction,
                        )
                    }
                    // For all other transaction types, use the regular View_MainItem
                    else -> {
                        View_MainItem(
                            viewModel = viewModel,
                            relative_M8BonVent = transaction,
                        )
                    }
                }
            }
        }
    }
}
