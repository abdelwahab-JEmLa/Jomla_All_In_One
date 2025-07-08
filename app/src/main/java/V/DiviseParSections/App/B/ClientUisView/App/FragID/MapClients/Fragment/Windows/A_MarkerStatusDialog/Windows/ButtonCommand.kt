package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun CommandButton(
    modifier: Modifier = Modifier,
    m2Client: HClientInfos,
    newEtate: M8BonVent.EtateActuellementEst,
    viewModel: MapClientsViewModel,
    context: Context,
    onUpdateLongAppSetting: () -> Unit,
) {
    val get = viewModel.aCentralFacade.focusedActiveValuesFacade.get
    val bonVentRepository = viewModel.aCentralFacade.get.repo8BonVent
    val handleBonVentSelection = remember(m2Client.keyID) {
        {
            val currentActiveFocuced_M14VentPeriode = get.currentActiveFocuced_M14VentPeriode
            val currentPeriodKey = currentActiveFocuced_M14VentPeriode?.keyID ?: ""

            val existingBonVent = bonVentRepository.datasValue.find { bonVent ->
                bonVent.parentM2ClientInfosKey == m2Client.keyID &&
                        bonVent.parentM7VentPeriodKeyId == currentPeriodKey
            }

            val targetBonVent = existingBonVent?.copy(
                etateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            ) ?: M8BonVent().copy(
                parentZAppComptNom = currentActiveFocuced_M14VentPeriode?.parent_M9AppCompt_KeyID?:"null",
                parentID7VentPeriodeKeyByParent = currentPeriodKey,
                debugInfos = m2Client.nom,
                parentM2ClientInfosKey = m2Client.keyID,
                parentM2ClientInfosDebugName = m2Client.nom,
                etateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )

            if (existingBonVent != null) {
                viewModel.aCentralFacade.focusedActiveValuesFacade.set.update_M8BonVent(targetBonVent)
            } else {
                viewModel.aCentralFacade.focusedActiveValuesFacade.set.add_M8BonVent(targetBonVent)
            }

            viewModel.aCentralFacade.focusedActiveValuesFacade.set.setIN_M9CurrentApp_onVentM8BonVentKey(targetBonVent)

            targetBonVent
        }
    }

    FilledTonalButton(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            handleBonVentSelection()

            viewModel.startRecordIfNot()
            viewModel.updateLongAppSetting(m2Client.id)
            onUpdateLongAppSetting()
        },
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
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Mode Commande",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(newEtate.name)
        }
    }
}
