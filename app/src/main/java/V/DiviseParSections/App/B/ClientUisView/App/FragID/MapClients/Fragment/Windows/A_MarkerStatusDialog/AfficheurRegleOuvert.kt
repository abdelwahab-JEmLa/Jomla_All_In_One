package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AfficheurRegleOuvert(
    repositorysModel: GroupeRepositorysProtoAvJuin3Model,
    clientId: Long,
    activeTransactionId: Long,
    viewModel: ViewModel_MapClients_App2FragID1,
    relatedClients: B_ClientDataBase?,
    coroutineScope: CoroutineScope,
    context: Context,
) {
    fun getLatestTransactionForClient(clientId: Long): C3_BonAchate? {
        return repositorysModel
            .c3_BonAchate_Repository.modelDatasSnapList
            .filter { it.clientAcheteurID == clientId }
            .maxByOrNull { it.timestamps }
    }

    val latestTransaction = getLatestTransactionForClient(clientId)
    val hasOngoingTransaction = latestTransaction?.etateActuellementEst ==
            C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    val ouvertTransaction = viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model
        .c3_BonAchate_Repository.getOuvert_1_3_TransactionCommercial()

    if (hasOngoingTransaction || ouvertTransaction != null) {
        val transaction = repositorysModel
            .c3_BonAchate_Repository.modelDatasSnapList
            .find {
                it.clientAcheteurID == clientId &&
                        it.etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            }

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
                // Check if there's an active transaction
                if (activeTransactionId != 0L) {
                    val relatedClientactiveTransaction =
                        viewModel.bProto_ClientsDataBase.find {
                            it.id == activeTransactionId
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
                        text = "وقت البدء: ${transaction?.heurDebutInString ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "الحالة الحالية: ${transaction?.etateActuellementEst?.nomArabe ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    C3_BonAchate.EtateActuellementEst.A_COMMANDE_CONFIRME
                        .Button(
                            coroutineScope = coroutineScope,
                            viewModel = viewModel,
                            clientId = clientId,
                            context = context
                        )
                    C3_BonAchate.EtateActuellementEst.COMMANDE_LIVRAI
                        .Button(
                            coroutineScope = coroutineScope,
                            viewModel = viewModel,
                            clientId = clientId,
                            context = context
                        )

                    // Add button to close transaction
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                ouvertTransaction?.let { tx ->
                                    val updatedTransaction = tx.copy(ouvert = false)
                                    viewModel.repo_0_0_HeadSQLRepositorys.upsertUneDataEtReturnVID(
                                        updatedTransaction
                                    )

                                    repositorysModel.activeVId_C3_BonAchate_Repository.value = 0
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إغلاق الفاتورة مع عدم وضع اي تقرير")
                    }
                }
            }
        }
    }
}
