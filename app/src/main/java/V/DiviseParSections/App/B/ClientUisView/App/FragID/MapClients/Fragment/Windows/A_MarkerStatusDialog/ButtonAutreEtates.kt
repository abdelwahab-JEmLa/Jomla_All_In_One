package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.C3_TransactionCommercial
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun C3_TransactionCommercial.EtateActuellementEst.ButtonAutreEtates(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    clickedClient: Long,
) {
    val context = LocalContext.current
    val newEtate = this

    FilledTonalButton(
        onClick = {
            upsertLenceAutresStatesRepoGroupedProtoAvanJuin3(
                uiState = uiState,
                viewModel = viewModel,
                relatedClientID = clickedClient,
                newEtate = newEtate,
            )

            if (newEtate == C3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == C3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.aCentralCompoRepositoryProtoJuin9.comptAppState
                    .updateActiveComptIdClientOuSonMarqueMapEstOuvert(0)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    newEtate.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    newEtate.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = newEtate.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(newEtate.nomArabe)
        }
    }
}

fun upsertLenceAutresStatesRepoGroupedProtoAvanJuin3(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    relatedClientID: Long,
    newEtate: C3_TransactionCommercial.EtateActuellementEst,
) {
    val relatedClients = viewModel.bProto_ClientsDataBase.find {
        it.id == (relatedClientID)
    }
    val activeComptApp = viewModel.appState.activeCompt

    val ceComptVendeurInsertBonsAchatAuPeriodID =
        activeComptApp?.ceComptVendeurInsertBonsAchatAuPeriodID

    val clientId = relatedClients?.id ?: 0L
    val clientName = relatedClients?.nom ?: "Unknown Client"
    val periodId = ceComptVendeurInsertBonsAchatAuPeriodID ?: run {
        return
    }

    val existingBonAchat = viewModel.c3_BonAchate_List.find {
        it.clientAcheteurID == clientId
                && it.parentVID_1_4_PeriodeVent == periodId
                && it.etateActuellementEst == newEtate
    }

    if (existingBonAchat != null) {
        val updatedBonAchat = existingBonAchat.copy(
            timestamps = System.currentTimeMillis(),
        )
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            updatedBonAchat
        )
    } else {
        val newTransaction = C3_TransactionCommercial(
            clientAcheteurID = clientId,
            nomClientConcerned = clientName,
            parentVID_1_4_PeriodeVent = periodId,
            etateActuellementEst = newEtate,
        )
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            newTransaction
        )
    }
}
