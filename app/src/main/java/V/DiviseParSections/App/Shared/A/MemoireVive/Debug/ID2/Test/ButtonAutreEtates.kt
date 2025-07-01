package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
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
    val context = LocalContext.current
    val newEtate = this
    val keyHandBonVentOnClickButton = viewModel.setter.getKeyID8BonVentSetter(clickedClient, newEtate)

    FilledTonalButton(
        onClick = {
            viewModel.setter.upsertBonVentSetter(keyHandBonVentOnClickButton)

            if (newEtate == GBonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == GBonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.setter.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
        ,
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
