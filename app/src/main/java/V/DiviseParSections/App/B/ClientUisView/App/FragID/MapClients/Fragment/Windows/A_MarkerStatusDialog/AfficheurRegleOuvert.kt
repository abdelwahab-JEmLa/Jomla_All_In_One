package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AfficheurRegleOuvert(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    relatedClients: B_ClientInfosProtoJuin3?,
    onClickToFerme: (C3_TransactionCommercial.EtateActuellementEst, Long) -> Unit,
) {
    val clientId = relatedClients?.id ?: 0L

    fun getLatestTransactionForClient(clientId: Long): C3_TransactionCommercial? {
        return uiState
            .c3_TransactionCommercialList
            .filter { it.clientAcheteurID == clientId }
            .maxByOrNull { it.timestamps }
    }

    val latestTransaction = relatedClients?.let { getLatestTransactionForClient(it.id) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            uiState.activeCompt?.let { activeCompt ->
                val relatedClientactiveTransaction =
                    viewModel.bProto_ClientsDataBase.find {
                        it.id == activeCompt.vid
                    }
                Text(
                    text = "ماهو تقرير الزبون السابق ${
                        relatedClients?.nom
                            ?: relatedClientactiveTransaction?.nom
                            ?: ""
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "وقت البدء: ${latestTransaction?.heurDebutInString ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "الحالة الحالية: ${latestTransaction?.etateActuellementEst?.nomArabe ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )

                ButtonAutreEtatesFun(
                    uiState = uiState,
                    viewModel = viewModel,
                    clickedClient = clientId,
                    etateActuellementEst =C3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE,
                    onClickToFerme = onClickToFerme
                )

                C3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI
                    .ButtonAutreEtates(
                        uiState = uiState,
                        viewModel = viewModel,
                        clickedClient = clientId,
                    )

                TextButton(
                    onClick = {
                        viewModel.updateActiveComptIdClientOuvertPoutCeCompt(0)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق الفاتورة مع عدم وضع اي تقرير")
                }
            }
        }
    }
}
