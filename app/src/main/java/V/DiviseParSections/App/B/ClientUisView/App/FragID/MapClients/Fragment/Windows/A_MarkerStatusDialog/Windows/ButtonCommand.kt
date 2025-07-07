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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun CommandButton(
    modifier: Modifier = Modifier,
    m2Client: HClientInfos?,
    etateActuellementEst: M8BonVent.EtateActuellementEst,
    viewModel: MapClientsViewModel,
    context: Context,
    onUpdateLongAppSetting: () -> Unit,
) {
    val focusedVarsHandlerFacade = viewModel.aCentralFacade.focusedVarsHandlerFacade
    val get = focusedVarsHandlerFacade.get
    val (editedM8BonVent, editedM9CurrCompt) = get
            .get_By_Client_Edited_M8BonVent_Et_M9CurrComptFacade(
                m2Client!!
            )

    FilledTonalButton(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            focusedVarsHandlerFacade.set.upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
                editedM8BonVent,
                editedM9CurrCompt
            )
            onUpdateLongAppSetting()
        },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    etateActuellementEst.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    etateActuellementEst.color
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
            Text(etateActuellementEst.name)
        }
    }
}
