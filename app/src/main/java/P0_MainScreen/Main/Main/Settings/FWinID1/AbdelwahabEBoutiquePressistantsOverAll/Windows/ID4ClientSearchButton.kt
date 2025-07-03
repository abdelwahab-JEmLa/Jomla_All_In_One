package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.ID2ClientRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Id9AppComptRepository
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun ID4ClientSearchButton(
    uiState: ViewModelPresistantButtonsSec8FWinID1.UiState,
    hClientRepository: ID2ClientRepository,
    zAppComptRepositoryComposable: Id9AppComptRepository,
    showLabels: Boolean,
    locationTracker: LocationTracker? = null,
    onClientSelected: (HClientInfos) -> Unit = {}
) {

    var isTextCollapsed by remember { mutableStateOf(false) }

    val id8BonVentRepository = uiState.id8BonVentRepository
    val onVentId8BonVent = id8BonVentRepository.onVentId8BonVent
    val defaultId8BonVent = id8BonVentRepository.defaultId8BonVent

    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<HClientInfos>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        delay(300)
        if (searchQuery.isNotEmpty()) {
            filteredClients = hClientRepository.datasValue.filter { client ->
                client.nom.contains(searchQuery, ignoreCase = true) ||
                        client.numTelephone.contains(searchQuery, ignoreCase = true)
            }
            showDropdown = filteredClients.isNotEmpty()
        } else {
            filteredClients = emptyList()
            showDropdown = false
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (!isSearchMode) {
            FloatingActionButton(
                onClick = { isSearchMode = true },
                modifier = Modifier.size(40.dp),
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Rechercher Client",
                    tint = Color.White
                )
            }

            if (showLabels) {
                Text(
                    text = if (isTextCollapsed) {
                        // Collapsed state - show only client name or short text
                        if (onVentId8BonVent.nomClientConcerned.isNotEmpty() && onVentId8BonVent.nomClientConcerned != "Non Defini") {
                            onVentId8BonVent.nomClientConcerned
                        } else {
                            "Client"
                        }
                    } else {
                        // Expanded state - show full text with timestamp
                        if (onVentId8BonVent.nomClientConcerned.isNotEmpty() && onVentId8BonVent.nomClientConcerned != "Non Defini") {
                            "${onVentId8BonVent.nomClientConcerned} - ${onVentId8BonVent.getCreationTimeString()}"
                        } else {
                            "Rechercher Client"
                        }
                    },
                    modifier = Modifier
                        .background(Color(0xFF4CAF50))
                        .padding(4.dp)
                        .clickable {
                            isTextCollapsed = !isTextCollapsed
                        },
                    color = Color.White
                )
            }
        } else {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .width(200.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .focusRequester(remember { FocusRequester() }),
                        placeholder = { Text("Nom ou téléphone...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        ),
                        leadingIcon = {
                            val currentLocation = locationTracker?.getCurrentPosition()

                            val newClient = HClientInfos(
                                nom = searchQuery.ifEmpty { "Err Definition" },
                                title = searchQuery.ifEmpty { "Nouveau Client" },
                                latitude = currentLocation?.latitude
                                    ?: HClientInfos.getCurrentDefaultLatitude(),
                                longitude = currentLocation?.longitude
                                    ?: HClientInfos.getCurrentDefaultLongitude(),
                                caMarqueGpsEstOuvert = currentLocation != null,
                                snippet = if (currentLocation != null) {
                                    "Lat: ${String.format("%.6f", currentLocation.latitude)}, " +
                                            "Lng: ${
                                                String.format(
                                                    "%.6f",
                                                    currentLocation.longitude
                                                )
                                            }"
                                } else {
                                    "Position non disponible"
                                }
                            )

                            val updatedDefaultOnVentID8BonVentEtAdd = defaultId8BonVent.copy(
                                creationTimestamps = System.currentTimeMillis(),
                                parentId2ClientInfosKeyID = newClient.keyID,
                                parentId2ClientInfosDebugKey = newClient.nom
                            )

                            val updatedAppCompt =
                                zAppComptRepositoryComposable.currentAppCompt?.copy(
                                    onVentId8BonVentKeyId = updatedDefaultOnVentID8BonVentEtAdd.keyID,
                                    onVentGBonVentDebugNameKey = updatedDefaultOnVentID8BonVentEtAdd.parentId2ClientInfosDebugKey
                                )

                            IconButton(
                                onClick = {
                                    hClientRepository.upsertData(newClient)
                                    id8BonVentRepository.upsert(updatedDefaultOnVentID8BonVentEtAdd)
                                    if (updatedAppCompt != null) { zAppComptRepositoryComposable.addOrUpdateData(updatedAppCompt) }

                                    onClientSelected(newClient)

                                    resetSearchMode {
                                        isSearchMode = false
                                        searchQuery = ""
                                        showDropdown = false
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Créer nouveau client",
                                    modifier = Modifier.semantics(mergeDescendants = true) {
                                        set(
                                            SemanticsPropertyKey("DebugID1=HClientInfos"),
                                            HClientInfos()
                                        )
                                    }
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    resetSearchMode {
                                        isSearchMode = false
                                        searchQuery = ""
                                        showDropdown = false
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer"
                                )
                            }
                        }
                    )
                }

                if (showDropdown) {
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .heightIn(max = 200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        LazyColumn {
                            items(filteredClients) { client ->
                                ClientSearchItem(
                                    client = client,
                                    onClick = {
                                        onClientSelected(client)
                                        resetSearchMode {
                                            isSearchMode = false
                                            searchQuery = ""
                                            showDropdown = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private inline fun resetSearchMode(action: () -> Unit) {
    action()
}

@SuppressLint("DefaultLocale")
@Composable
fun ClientSearchItem(
    client: HClientInfos,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = Color(client.actuelleEtat.color),
                    shape = CircleShape
                )
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client.nom,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (client.numTelephone.isNotEmpty()) {
                Text(
                    text = client.numTelephone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            if (client.caMarqueGpsEstOuvert && client.latitude != 0.0 && client.longitude != 0.0) {
                Text(
                    text = "📍 ${String.format("%.4f", client.latitude)}, ${
                        String.format(
                            "%.4f",
                            client.longitude
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        Icon(
            imageVector = client.clientTypeMode.icon,
            contentDescription = null,
            tint = client.clientTypeMode.color,
            modifier = Modifier.size(16.dp)
        )
    }
}
