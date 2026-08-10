package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * TODO(2.C): Floating dialog listing the clients currently displayed on the map.
 * Typing in the search field filters the list by name/phone. Tapping a row
 * simulates a tap on that client's marker, so the same active
 * Click_On_Marque mode (Standard / Ajouter Ciblage / Appeler / Navigation / ...)
 * that would fire from the map fires from here too.
 */
@Composable
fun But1_Floating_ClientsListDialog(
    mapView: MapView,
    clients: List<M2Client>,
    viewModel: MapClientsViewModel,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    val compt = viewModel.active_Datas.active_M9Compt
    val currentMode = compt?.click_On_Marque ?: ActiveCentralValues.Click_On_Marque.Standart

    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) {
            clients
        } else {
            val query = searchQuery.trim().lowercase()
            clients.filter {
                it.nom.lowercase().contains(query) ||
                        it.numTelephone.lowercase().contains(query)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f)
                .padding(16.dp),
        ) {
            Column(modifier = Modifier.fillMaxHeight()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "Clients sur la carte",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Mode actif : ${getModeLabel(currentMode)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = currentMode.couleur,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Rechercher un client...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                )

                Divider(modifier = Modifier.padding(top = 12.dp))

                if (filteredClients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Aucun client trouvé",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                    ) {
                        items(
                            filteredClients,
                            key = { client -> "${client.id}_${client.nom}_${client.numTelephone}" },
                        ) { client ->
                            ClientRow(
                                client = client,
                                currentMode = currentMode,
                                onClick = {
                                    // Find the marker already placed on the map for this
                                    // client and simulate a tap on it, so the exact same
                                    // Click_On_Marque logic used by the map (Standard,
                                    // ADD_Au_Ciblage_Clients, Call, Navigate, ...) runs.
                                    val marker = mapView.overlays
                                        .filterIsInstance<Marker>()
                                        .find { it.id == client.id.toString() }
                                    if (marker != null) {
                                        marker.onMarkerClick(marker, mapView)
                                    } else {
                                        // Marker not currently rendered (e.g. filtered out
                                        // of view) — fall back to opening its standard
                                        // detail dialog directly via the ViewModel.
                                        viewModel.set_M2Client_UiState_In_MarkerStatusDialog(client)
                                    }
                                    onDismiss()
                                },
                            )
                            Divider(color = Color.LightGray.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientRow(
    client: M2Client,
    currentMode: ActiveCentralValues.Click_On_Marque,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = currentMode.couleur, shape = CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client.nom,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (client.numTelephone.isNotEmpty() && client.numTelephone != "null") {
                Text(
                    text = client.numTelephone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
    }
}
