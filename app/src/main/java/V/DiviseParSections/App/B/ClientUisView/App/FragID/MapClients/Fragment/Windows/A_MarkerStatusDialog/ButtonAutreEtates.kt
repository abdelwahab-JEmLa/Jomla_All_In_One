package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun C3_TransactionCommercial.EtateActuellementEst.ButtonAutreEtates(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    coroutineScope: CoroutineScope,
    relaterClientId: Long,
    context: Context,
) {
    val newEtate =
        this
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                upsertLenceAutresStatesRepoGroupedProtoAvanJuin3(
                    uiState=uiState,
                    viewModel=viewModel,
                    relatedClientID=relaterClientId,
                    newEtate= newEtate,
                )
                if (newEtate == C3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI
                    || newEtate == C3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
                ) {
                    viewModel.updateActiveComptIdClientOuvertPoutCeCompt(0)
                }

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
    val activeComptApp = uiState.activeCompt
    val ceComptVendeurInsertBonsAchatAuPeriodID =
        activeComptApp?.ceComptVendeurInsertBonsAchatAuPeriodID
    val clientId = relatedClients?.id ?: 0L

    val existingBonAchat = viewModel.c3_BonAchate_List.find {
        it.clientAcheteurID == clientId
                && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
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
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            C3_TransactionCommercial(
                clientAcheteurID = clientId,
                nomClientConcerned = relatedClients?.nom!!,
                parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
                etateActuellementEst = newEtate,
            )
        )
    }
}
