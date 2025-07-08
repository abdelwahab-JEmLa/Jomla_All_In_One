package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
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
    val aCentralFacade = viewModel.aCentralFacade
    val focusedVarsHandlerFacade = aCentralFacade.focusedVarsHandlerFacade
    val get = focusedVarsHandlerFacade.get
    val context = LocalContext.current
    val newEtate = this

    val m2Client = aCentralFacade.mainRepositorysGetterFacade.repo2Client.datasValue.find { it.id == clickedClient }!!

    val defaultM8BonVent = get.getDefaultM8BonVent().copy(
        debugInfos = m2Client.nom,
        parentM2ClientInfosKey = m2Client.keyID,
        parentM2ClientInfosDebugName = m2Client.nom,
        etateActuellementEst = newEtate
    )

    FilledTonalButton(
        onClick = {
            focusedVarsHandlerFacade.set.add_New_M8BonVentFacade(defaultM8BonVent)

            if (newEtate == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                viewModel.aCentralFacade.focusedVarsHandlerFacade.set.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
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
