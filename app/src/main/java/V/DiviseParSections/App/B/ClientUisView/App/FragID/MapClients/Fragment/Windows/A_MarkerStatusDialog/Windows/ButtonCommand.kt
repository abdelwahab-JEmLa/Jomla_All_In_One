package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
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
    m2Client: M2Client,
    newEtate: M8BonVent.EtateActuellementEst,
    viewModel: MapClientsViewModel,
    context: Context,
    onUpdateLongAppSetting: () -> Unit,
) {
    val handleBonVentSelection_With_Semantics_Debug = remember(m2Client.keyID) {
        val get = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
        val bonVentRepository = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent

        val currentActiveFocuced_M14VentPeriode = get.currentActiveFocuced_M14VentPeriode
        val currentActiveFocuced_M14VentPeriode_KeyID = currentActiveFocuced_M14VentPeriode?.keyID ?: "null"
        val parent_M9AppCompt_KeyID = currentActiveFocuced_M14VentPeriode?.parent_M9AppCompt_KeyID ?: "null"

        val existingBonVent = bonVentRepository.datasValue.find { bonVent ->
            bonVent.parent_M14VentPeriod_KeyId == currentActiveFocuced_M14VentPeriode_KeyID
                    && bonVent.parent_M2Client_KeyID == m2Client.keyID
                    && bonVent.etateActuellementEst == newEtate
        }

        val new = M8BonVent().copy(
            parent_M9AppCompt_KeyID = parent_M9AppCompt_KeyID,
            parent_M14VentPeriod_KeyId = currentActiveFocuced_M14VentPeriode_KeyID,
            parent_M2Client_KeyID = m2Client.keyID,
            parent_M2Client_DebugInfos = m2Client.nom,
            etateActuellementEst = newEtate
        )

        val targetBonVent = existingBonVent?.copy(
            etateActuellementEst = newEtate
        ) ?: new

        val handleClick = {
            if (existingBonVent != null) {
                viewModel.aCentralFacade.repositorysMainSetter.update_IfExist_Setter(targetBonVent)
            } else {
                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.add_M8BonVent(targetBonVent)
            }

            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.setIN_M9CurrentApp_onVentM8BonVentKey(
                targetBonVent
            )
        }

        val semMod = Modifier.getSemanticsTag(nomVal = "new", data = new)

        Pair(semMod, handleClick)
    }

    FilledTonalButton(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            handleBonVentSelection_With_Semantics_Debug.second

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
