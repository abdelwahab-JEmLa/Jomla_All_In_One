package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.GBonVent
import android.widget.Toast
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
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun GBonVent.EtateActuellementEst.ButtonAutreEtates(
    viewModel: MapClientsViewModel,
    clickedClient: Long,
) {
    val context = LocalContext.current
    val newEtate = this
    val client = viewModel.getter.hClientRepository.findHClientInfos(clickedClient)
    val bonVent = viewModel.getter.getBonVentForDisplay(clickedClient, newEtate)

    FilledTonalButton(
        onClick = {
            bonVent.onSuccess { bonVentData ->
                viewModel.setter.upsertNewBonVentParDiplayed(bonVentData)

                if (newEtate == GBonVent.EtateActuellementEst.COMMANDE_LIVRAI
                    || newEtate == GBonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                ) {
                    viewModel.setter.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()
                }
            }.onFailure { error ->
                 Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
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
            val ventPeriodKey = viewModel.getter.parametresAppComptNonSaved.activePeriodKeyHand
            val clientKey = client?.nom?.withOutFireBaseInvalidCharacters()!!
            val etateKey = newEtate.name.withOutFireBaseInvalidCharacters()

            val keyHandBonVentOnClickButton = GBonVent.getKey(ventPeriodKey, clientKey, etateKey)

            Text(keyHandBonVentOnClickButton, fontSize = 8.sp)
        }
    }
}

