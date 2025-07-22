package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
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
    repo2Client: Repo2Client = viewModel.aCentralFacade.repositorysMainGetter.repo2Client,
    marqueClick: Marker,
    marqueClickRelativeClient: M2Client?,
    onDismiss: () -> Unit,
    onClickToEditeMarquerPosition: (M2Client) -> Unit,
    onShowDeleteConfirmationChange: (Boolean) -> Unit = {},
    onClientTypeModeChange: (M2Client.ClientTypeMode?) -> Unit = {},
    onShowEditDialogChange: (Boolean) -> Unit = {},
    onShowPhoneDialogChange: (Boolean) -> Unit = {},
) {
    val clientTypeMode = marqueClickRelativeClient?.clientTypeMode
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
        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    val m2Client = repo2Client.datasValue.find { it.id == marqueClick.id.toLong() }

                    m2Client?.let { onClickToEditeMarquerPosition(m2Client) }
                    onDismiss()
                }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Edit location"
            )
        }

        Card(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    val newClientTypeMode = when (clientTypeMode) {
                        M2Client.ClientTypeMode.ANCIEN -> M2Client.ClientTypeMode.NEVEAU
                        M2Client.ClientTypeMode.NEVEAU -> M2Client.ClientTypeMode.EVITE
                        M2Client.ClientTypeMode.EVITE -> M2Client.ClientTypeMode.ANCIEN
                        null -> M2Client.ClientTypeMode.NEVEAU
                    }

                    // Update the client's type mode
                    marqueClickRelativeClient?.let { client ->
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
                text = marqueClickRelativeClient?.nom ?: "ClientAchteur",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )



            if (!marqueClickRelativeClient?.numTelephone.isNullOrEmpty()) {
                Text(
                    text = marqueClickRelativeClient?.numTelephone ?: "",
                    modifier = Modifier.clickable { onShowPhoneDialogChange(true) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
