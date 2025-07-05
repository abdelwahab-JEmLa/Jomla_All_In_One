package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
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
import androidx.compose.material.icons.filled.Receipt
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
    hClientRepository: Repo2Client,
    zAppComptRepositoryComposable: Repo9AppCompt,
    showLabels: Boolean,
    locationTracker: LocationTracker? = null,
    onClientSelectedToToast: (HClientInfos) -> Unit = {},
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val focusedVarsHandlerFacade = uiState.focusedVarsHandlerFacade
    val getter = focusedVarsHandlerFacade.getter

    var isTextCollapsed by remember { mutableStateOf(false) }

    val onVentId8BonVent = getter.onVentM8BonVent
    val defaultId8BonVent = getter.defaultM8BonVent

    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<HClientInfos>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Extracted function for performing click and search
    LaunchedEffect(Unit) {
        performInitialSearch(
            onSearchModeChange = { isSearchMode = it },
            onSearchQueryChange = { searchQuery = it },
            focusRequester = focusRequester
        )
    }

    LaunchedEffect(searchQuery) {
        performClientSearch(
            searchQuery = searchQuery,
            hClientRepository = hClientRepository,
            onFilteredClientsChange = { filteredClients = it },
            onShowDropdownChange = { showDropdown = it }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
    ) {
        if (!isSearchMode) {
            FloatingActionButton(
                modifier = Modifier
                    .size(40.dp)
                    .semantics(mergeDescendants = true) {
                        set(
                            SemanticsPropertyKey("D1Debug== onVentId8BonVent"), onVentId8BonVent
                        )
                        set(
                            SemanticsPropertyKey("D1Debug== currentM9AppCompt"),
                            viewModel.getterFocusedVarsHandlerFacade.currentM9AppCompt
                        )
                    },
                onClick = {
                    isSearchMode = true
                },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Rechercher Client",
                    tint = Color.White
                )
            }

            if (showLabels) {
                val nomClient = getter.onVentM2ClientInfos?.nom ?: ""

                if (onVentId8BonVent != null) {
                    Text(
                        text = if (isTextCollapsed) {
                            nomClient
                        } else {
                            if (onVentId8BonVent.nomClientConcerned.isNotEmpty() && onVentId8BonVent.nomClientConcerned != "Non Defini") {
                                "$nomClient - ${onVentId8BonVent.getCreationTimeString()}"
                            } else {
                                "Rechercher Client"
                            }
                        }, modifier = Modifier
                            .background(Color(0xFF4CAF50))
                            .padding(4.dp)
                            .clickable {
                                isTextCollapsed = !isTextCollapsed
                            }, color = Color.White
                    )
                }
            }
        } else {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .semantics {

                            }
                            .width(200.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .focusRequester(focusRequester),
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Nom ou téléphone...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        ),
                        leadingIcon = {
                            CreateNewClientIcon(
                                searchQuery = searchQuery,
                                locationTracker = locationTracker,
                                defaultId8BonVent = defaultId8BonVent,
                                onClientSelectedToToast = onClientSelectedToToast,
                                onResetSearchMode = {
                                    resetSearchMode {
                                        isSearchMode = false
                                        searchQuery = ""
                                        showDropdown = false
                                    }
                                },
                                viewModel = viewModel
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    resetSearchMode {
                                        isSearchMode = false
                                        searchQuery = ""
                                        showDropdown = false
                                    }
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Close, contentDescription = "Fermer"
                                )
                            }
                        })
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
                                    viewModel = viewModel,
                                    client = client,
                                    onClick = {
                                        onClientSelectedToToast(client)
                                        resetSearchMode {
                                            isSearchMode = false
                                            searchQuery = ""
                                            showDropdown = false
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}

// Extracted function for initial search setup
private suspend fun performInitialSearch(
    onSearchModeChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    if (false) {
        delay(2000)
        onSearchModeChange(true)
        delay(500)
        onSearchQueryChange("mah")
        focusRequester.requestFocus()
    }
}

// Extracted function for client search logic
private suspend fun performClientSearch(
    searchQuery: String,
    hClientRepository: Repo2Client,
    onFilteredClientsChange: (List<HClientInfos>) -> Unit,
    onShowDropdownChange: (Boolean) -> Unit
) {
    delay(300)
    if (searchQuery.isNotEmpty()) {
        val filtered = hClientRepository.datasValue.filter { client ->
            client.nom.contains(searchQuery, ignoreCase = true) ||
                    client.numTelephone.contains(searchQuery, ignoreCase = true)
        }
        onFilteredClientsChange(filtered)
        onShowDropdownChange(filtered.isNotEmpty())
    } else {
        onFilteredClientsChange(emptyList())
        onShowDropdownChange(false)
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun CreateNewClientIcon(
    searchQuery: String,
    locationTracker: LocationTracker?,
    defaultId8BonVent: GBonVent,
    onClientSelectedToToast: (HClientInfos) -> Unit,
    onResetSearchMode: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val currentLocation = locationTracker?.getCurrentPosition()

    val newClient = HClientInfos(
        nom = searchQuery.ifEmpty { "Err Definition" },
        title = searchQuery.ifEmpty { "Nouveau Client" },
        latitude = currentLocation?.latitude ?: HClientInfos.getCurrentDefaultLatitude(),
        longitude = currentLocation?.longitude ?: HClientInfos.getCurrentDefaultLongitude(),
        caMarqueGpsEstOuvert = currentLocation != null,
        snippet = if (currentLocation != null) {
            "Lat: ${String.format("%.6f", currentLocation.latitude)}, " +
                    "Lng: ${String.format("%.6f", currentLocation.longitude)}"
        } else {
            "Position non disponible"
        }
    )

    val updatedDefaultOnVentID8BonVentEtAdd = defaultId8BonVent.copy(
        debugInfos = newClient.nom,

        creationTimestamps = System.currentTimeMillis(),
        parentM2ClientInfosKey = newClient.keyID,
        parentM2ClientInfosDebugName = newClient.nom
    )

    val updatedAppCompt = viewModel.getterFocusedVarsHandlerFacade.currentM9AppCompt?.copy(
        onVentM8BonVentKey = updatedDefaultOnVentID8BonVentEtAdd.keyID,
        onVentM8BonVentDebugInfos = updatedDefaultOnVentID8BonVentEtAdd.debugInfos
    )

    IconButton(
        onClick = {
            viewModel.setter.addNewM2ClientInfos(newClient)
            viewModel.setter.addNewM8BonVent(updatedDefaultOnVentID8BonVentEtAdd)
            if (updatedAppCompt != null) {
                viewModel.setter.updateFocuseM9AppCompt(updatedAppCompt)
            }

            onClientSelectedToToast(newClient)
            onResetSearchMode()
        },
        modifier = Modifier.semantics(mergeDescendants = true) {
            set(SemanticsPropertyKey("Debug  new M8BonVent"), updatedDefaultOnVentID8BonVentEtAdd)

            set(
                SemanticsPropertyKey("Debug currentM9AppCompt avec  new M8BonVent"),
                updatedAppCompt
            )
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Créer nouveau client",
        )
    }
}

private inline fun resetSearchMode(action: () -> Unit) {
    action()
}

@SuppressLint("DefaultLocale")
@Composable
fun ClientSearchItem(
    client: HClientInfos,
    onClick: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {

    val updatedDefaultId8BonVent = viewModel.getterFocusedVarsHandlerFacade.defaultM8BonVent.copy(
        debugInfos = client.nom,
        parentM2ClientInfosKey = client.keyID,
        parentM2ClientInfosDebugName = client.nom
    )

    val newCurrentM9AppCompt = viewModel.getterFocusedVarsHandlerFacade.currentM9AppCompt?.copy(
        onVentM8BonVentKey = updatedDefaultId8BonVent.keyID,
        onVentM8BonVentDebugInfos = updatedDefaultId8BonVent.debugInfos
    )

    Row(
        modifier = Modifier
            .semantics {
                set(
                    SemanticsPropertyKey("1D == [updatedDefaultId8BonVent]"),
                    updatedDefaultId8BonVent
                )
                set(SemanticsPropertyKey("2D == [newCurrentM9AppCompt]"), newCurrentM9AppCompt)
            }
            .fillMaxWidth()
            .clickable {
                viewModel.setter.addNewM8BonVent(updatedDefaultId8BonVent)

                if (newCurrentM9AppCompt != null) {
                    viewModel.setter.updateFocuseM9AppCompt(newCurrentM9AppCompt)
                }

                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = Color(client.actuelleEtat.color), shape = CircleShape
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
                        String.format("%.4f", client.longitude)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }
        Row {
            Icon(
                imageVector = client.clientTypeMode.icon,
                contentDescription = null,
                tint = client.clientTypeMode.color,
                modifier = Modifier.size(16.dp)
            )
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = client.clientTypeMode.color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
