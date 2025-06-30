package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.GBonVent
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
fun GBonVent.EtateActuellementEst.ButtonAutreEtates(
    viewModel: MapClientsViewModel,
    clickedClient: Long,
) {
    val repo = viewModel.gBonVentRepo
    val clientKey = findKeyByID(viewModel, clickedClient)

    val context = LocalContext.current
    val newEtate = this

    FilledTonalButton(
        onClick = {
            // Check if the last BonVent data matches the criteria
            val lastBonVent = repo.datasValue.lastOrNull()
            val shouldUpdateLast = lastBonVent?.let { bonVent ->
                bonVent.parentHClientKeyID == clientKey
                        && bonVent.etateActuellementEst == newEtate
                        && bonVent.parentPeriodeVentKeyID == viewModel.getter.zAppComptRepositoryComposable.currentAppCompt?.onVentHVentPeriodKeyId
            } ?: false

            if (shouldUpdateLast && lastBonVent != null) {
                // Update the last BonVent using BSetter method
                viewModel.setter.updateComptAppErExistKey(
                    key = lastBonVent.keyID,
                    clientOldId = clickedClient,
                    etate = newEtate
                )
            } else {
                // Create new BonVent with generated key using BSetter method
                val newGeneratedKey = GBonVent.generePushKey()
                viewModel.setter.ajouteNewBonVent(
                    key = newGeneratedKey,
                    clientOldId = clickedClient,
                    etate = newEtate
                )
            }

            if (newEtate == GBonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == GBonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.setter.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()
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

            val data = repo.datasValue
                .lastOrNull {
                    it.parentHClientKeyID == clientKey
                            && it.etateActuellementEst == newEtate
                            && it.parentPeriodeVentKeyID == viewModel.getter.zAppComptRepositoryComposable.currentAppCompt?.onVentHVentPeriodKeyId
                }

            Text(data?.keyID?.takeLast(4)?.uppercase() ?: "new == ${GBonVent.generePushKey().takeLast(4).uppercase()}")
        }
    }
}

fun findKeyByID(
    viewModel: MapClientsViewModel,
    clickedClient: Long
) = viewModel.getter.hClientRepository.datasValue.find { it.id == clickedClient }?.keyID
