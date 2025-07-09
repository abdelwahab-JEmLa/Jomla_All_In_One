package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun M8BonVent.EtateActuellementEst.ButtonAutreEtates(
    viewModel: MapClientsViewModel,
    clickedClient: Long,
) {
    val context = LocalContext.current
    val newEtate = this
    val aCentralFacade = viewModel.aCentralFacade
    val m2Client = aCentralFacade.getRepositorys.repo2Client.datasValue.find { it.id == clickedClient }!!

    val handleBonVentSelection_With_Semantics_Debug = remember(m2Client.keyID) {
        val get = viewModel.aCentralFacade.focusedActiveValuesFacade.getterFocusedValues
        val bonVentRepository = viewModel.aCentralFacade.getRepositorys.repo8BonVent

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
                viewModel.aCentralFacade.setRepositorys.update_IfExist_Setter(targetBonVent)
            } else {
                viewModel.aCentralFacade.focusedActiveValuesFacade.set.add_M8BonVent(targetBonVent)
            }

            viewModel.aCentralFacade.focusedActiveValuesFacade.set.setIN_M9CurrentApp_onVentM8BonVentKey(
                targetBonVent
            )
        }

        val semMod = Modifier.getSemanticsTag(new, "new")

        Pair(semMod, handleClick)
    }

    FilledTonalButton(
        modifier = handleBonVentSelection_With_Semantics_Debug.first.fillMaxWidth(),
        onClick = {
            handleBonVentSelection_With_Semantics_Debug.second

            if (newEtate == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                viewModel.aCentralFacade.focusedActiveValuesFacade.set.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
            }
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
                imageVector = Icons.Default.Person,
                contentDescription = newEtate.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(newEtate.nomArabe)
        }
    }
}
