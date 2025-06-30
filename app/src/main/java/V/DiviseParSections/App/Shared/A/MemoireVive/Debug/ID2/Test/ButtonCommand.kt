package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.BSetter
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommandButton(
    modifier: Modifier = Modifier,
    viewModel: MapClientsViewModel,
    clientOuCaMarqueGpsEstOuvert: HClientInfos?,
    etateActuellementEst1: GBonVent.EtateActuellementEst,
    context: Context,
    onUpdateLongAppSetting: () -> Unit,
    viewClientKeyByParent: String,
) {
    val tag = "$viewClientKeyByParent--${etateActuellementEst1.name}"

    FilledTonalButton(
        modifier = modifier
            .fillMaxWidth()
            .testTag(tag)
        ,
        onClick = {
            if (clientOuCaMarqueGpsEstOuvert != null) {
                viewModel.ouvreBonVent(
                    clientOuCaMarqueGpsEstOuvert.id
                )

                onUpdateLongAppSetting()
            }
        },
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


fun upsertLenceCommandeRepoGroupedProtoAvantJuin3(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    relatedClientID: Long,
    newEtate: GBonVent.EtateActuellementEst,
    onAddNew: (GBonVent) -> Unit ={},
) {
    val relatedClients = viewModel.bProto_ClientsDataBase.find {
        it.id == (relatedClientID)
    }

    val activeComptApp= viewModel.getter.zAppComptRepositoryComposable.currentAppCompt
    val ceComptVendeurInsertBonsAchatAuPeriodID =
        activeComptApp?.ceComptVendeurInsertBonsAchatAuPeriodID

    val clientId = relatedClients?.id ?: 0L

    val existingBonAchat = viewModel.c3_BonAchate_List.find {
        it.parentHClientOldID == clientId
                && it.parentPeriodeVentOldID == ceComptVendeurInsertBonsAchatAuPeriodID
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
        val newTrx = GBonVent(
            parentHClientOldID = clientId,
            nomClientConcerned = relatedClients?.nom!!,
            parentPeriodeVentOldID = ceComptVendeurInsertBonsAchatAuPeriodID!!,
            heurDebutInString = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date()),
            etateActuellementEst = newEtate,
            parentID2ClientKeyByParent = BSetter.regexReturnParentKeysMap("null")[GBonVent.keyModel] ?: "",
            parentID7VentPeriodeKeyByParent = BSetter.regexReturnParentKeysMap("null")[Z_AppCompt.keyModelValID7] ?: "",
            parentID8C2TypeTransactionKeyByParent = BSetter.regexReturnParentKeysMap("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
        )
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            newTrx
        )
        onAddNew(newTrx)
    }
}
