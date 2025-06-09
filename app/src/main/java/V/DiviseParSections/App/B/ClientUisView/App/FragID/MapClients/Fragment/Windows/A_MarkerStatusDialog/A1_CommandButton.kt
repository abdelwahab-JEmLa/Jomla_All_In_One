package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker

@Composable
fun CommandButton(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_MapClients_App2FragID1,
    etateActuellementEst1: C3_TransactionCommercial.EtateActuellementEst,
    coroutineScope: CoroutineScope,
    clientId: Long,
    selectedMarker: Marker,
    onUpdateLongAppSetting: () -> Unit,
    onDismiss: () -> Unit,
    context: Context,
) {
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                upsertLenceCommandeRepoGroupedProtoAvanJuin3(
                    viewModel,
                    clientId,
                    etateActuellementEst1
                )

                val selectedMarkedID = selectedMarker.id.toLong()
                viewModel.updateLongAppSetting(selectedMarkedID)
                onUpdateLongAppSetting()

                onDismiss()

                viewModel.startRecordIfNot()

            }
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
