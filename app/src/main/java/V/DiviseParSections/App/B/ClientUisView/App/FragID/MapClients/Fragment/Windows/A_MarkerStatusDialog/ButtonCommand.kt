package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import Z_CodePartageEntreApps.Repository.Main.Proto.C3_TransactionCommercial
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommandButton(
    modifier: Modifier = Modifier,
    viewModel: MapClientsViewModel,
    clientOuCaMarqueGpsEstOuvert: B_ClientInfosProtoJuin3?,
    uiState: UiState,
    etateActuellementEst1: C3_TransactionCommercial.EtateActuellementEst,
    clientId: Long,
    selectedMarker: Marker,
    onUpdateLongAppSetting: () -> Unit,
    context: Context,
) {
    val selectedMarkedID = selectedMarker.id.toLong()

    FilledTonalButton(
        onClick = {
            upsertLenceCommandeRepoGroupedProtoAvantJuin3(
                uiState = uiState,
                viewModel = viewModel,
                relatedClientID = clientId,
                newEtate = etateActuellementEst1
            ) {

            }


            updateProtoIndex0(viewModel, selectedMarkedID, onUpdateLongAppSetting)

            if (clientOuCaMarqueGpsEstOuvert != null) {
                viewModel.aCentralCompoRepositoryProtoJuin9.comptAppState
                    .updateActiveComptIdClientOuSonMarqueMapEstOuvert(clientOuCaMarqueGpsEstOuvert.id)
            }

            if (clientOuCaMarqueGpsEstOuvert != null) {
                viewModel.ouvrireBonVent(
                    ouvertClientOnVentObjectId = clientOuCaMarqueGpsEstOuvert.bsonObjectId,
                    ouvertClientOnVentNom = clientOuCaMarqueGpsEstOuvert.nom
                )
            }

            viewModel.startRecordIfNot()
        },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    etateActuellementEst1.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    etateActuellementEst1.color
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
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Mode Commande",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(etateActuellementEst1.name)
        }
    }
}

private fun updateProtoIndex0(
    viewModel: MapClientsViewModel,
    selectedMarkedID: Long,
    onUpdateLongAppSetting: () -> Unit
) {
    viewModel.updateLongAppSetting(selectedMarkedID)
    onUpdateLongAppSetting()
}

fun upsertLenceCommandeRepoGroupedProtoAvantJuin3(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    relatedClientID: Long,
    newEtate: C3_TransactionCommercial.EtateActuellementEst,
    onAddNew: (C3_TransactionCommercial) -> Unit,
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
            heurDebutInString = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())
        )
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            updatedBonAchat
        )
    } else {
        val newTrx = C3_TransactionCommercial(
            clientAcheteurID = clientId,
            nomClientConcerned = relatedClients?.nom!!,
            parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
            etateActuellementEst = newEtate,
            heurDebutInString = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())
        )
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            newTrx
        )
        onAddNew(newTrx)
    }
}
