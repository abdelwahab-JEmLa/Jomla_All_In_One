package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.HClientInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.osmdroid.views.MapView

@Composable
fun MarkerEditModeOverlay(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Text(
            text = "Mode Édition de Marqueur",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FloatingActionButton(
                onClick = onCancel,
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(Icons.Default.Close, "Cancel")
            }

            FloatingActionButton(
                onClick = onConfirm,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Check, "Confirm")
            }
        }
    }
}

fun handleMarkerPositionUpdate(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    mapView: MapView,
    editingMarkerId: Long,
) {
    val clientToUpdate = uiState.b_ClientInfosProtoJuin3List.find {
        it.id == editingMarkerId
    }

    clientToUpdate?.let { client ->
        val centerPoint = mapView.mapCenter
        val updatedClient = HClientInfos().apply {
            id = client.id
            nom = client.nom
            numTelephone = client.numTelephone
            couleur = client.couleur
            bonDuClientsSu = client.bonDuClientsSu
            currentCreditBalance = client.currentCreditBalance
            positionDonClientsList = client.positionDonClientsList
            cUnClientTemporaire = client.cUnClientTemporaire
            auFilterFAB = client.auFilterFAB
            typeDeSonMagasine = client.typeDeSonMagasine
            clientTypeMode = client.clientTypeMode

            latitude = centerPoint.latitude
            longitude = centerPoint.longitude
            title = client.title
            snippet = client.snippet
            actuelleEtat = client.actuelleEtat
        }

        viewModel.updateData(updatedClient)
    }
}
