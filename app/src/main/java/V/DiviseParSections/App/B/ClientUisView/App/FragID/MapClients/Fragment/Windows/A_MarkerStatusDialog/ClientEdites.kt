package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.osmdroid.views.overlay.Marker

@Composable
fun ClientEdites(
    viewModel: MapClientsViewModel,
    onClickToEditeMarquerPosition: (Long) -> Unit,
    selectedMarker: Marker,
    onDismiss: () -> Unit,
    clientTypeMode: B_ClientInfosProtoJuin3.ClientTypeMode?,
    relatedClients: B_ClientInfosProtoJuin3?,

    onShowDeleteConfirmationChange: (Boolean) -> Unit = {},
    onClientTypeModeChange: (B_ClientInfosProtoJuin3.ClientTypeMode?) -> Unit = {},
    onShowEditDialogChange: (Boolean) -> Unit = {},
    onShowPhoneDialogChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Delete Icon
        Card(
            modifier = Modifier
                .background(color = Color.Red)
                .clickable {
                    onShowDeleteConfirmationChange(true)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete client",
            )
        }
        // Location Edit Icon
        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    onClickToEditeMarquerPosition(selectedMarker.id.toLong())
                    onDismiss()
                }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Edit location"
            )
        }

        // ClientAchteur Type Mode Toggle
        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    val newClientTypeMode = when (clientTypeMode) {
                        B_ClientInfosProtoJuin3.ClientTypeMode.ANCIEN -> B_ClientInfosProtoJuin3.ClientTypeMode.NEVEAU
                        B_ClientInfosProtoJuin3.ClientTypeMode.NEVEAU -> B_ClientInfosProtoJuin3.ClientTypeMode.EVITE
                        B_ClientInfosProtoJuin3.ClientTypeMode.EVITE -> B_ClientInfosProtoJuin3.ClientTypeMode.ANCIEN
                        null -> B_ClientInfosProtoJuin3.ClientTypeMode.NEVEAU
                    }

                    // Update the client's type mode
                    relatedClients?.let { client ->
                        client.clientTypeMode = newClientTypeMode
                        viewModel.updateData(client)
                    }

                    onClientTypeModeChange(newClientTypeMode)
                }
        ) {
            clientTypeMode?.let {
                Icon(
                    imageVector = it.icon,
                    contentDescription = "Toggle ClientAchteur Type",
                    tint = it.color
                )
            }
        }
    }

    Card(
        modifier = Modifier
            .clickable { onShowEditDialogChange(true) }
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = relatedClients?.nom ?: "ClientAchteur",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (!relatedClients?.numTelephone.isNullOrEmpty()) {
                Text(
                    text = relatedClients?.numTelephone ?: "",
                    modifier = Modifier.clickable { onShowPhoneDialogChange(true) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
