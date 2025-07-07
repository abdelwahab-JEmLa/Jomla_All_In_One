package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.concurrent.TimeUnit

@OptIn(FlowPreview::class)
@SuppressLint("DefaultLocale")
@Composable
fun ID4ClientSearchButton(
    uiState: ViewModelPresistantButtonsSec8FWinID1.UiState,
    hClientRepository: Repo2Client,
    showLabels: Boolean,
    locationTracker: LocationTracker? = null,
    onClientSelectedToToast: (HClientInfos) -> Unit = {},
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val focusedVarsHandlerFacade = uiState.focusedVarsHandlerFacade
    val getter = focusedVarsHandlerFacade.get

    var isTextCollapsed by remember { mutableStateOf(false) }

    val onVentId8BonVent = getter.onVentM8BonVent
    val defaultId8BonVent = getter.defaultM8BonVent

    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<HClientInfos>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Create a flow for search query debouncing
    val searchQueryFlow = remember { MutableStateFlow("") }

    // Get clients with command mode BonVents
    val repo8BonVent = viewModel.aCentralFacade.mainRepositorysGetterFacade.repo8BonVent
    val clientsWithCommandBonVents = remember(hClientRepository.datasValue, repo8BonVent.datasValue) {
        getClientsWithCommandModeBonVents(hClientRepository, repo8BonVent)
    }

    // Initialize dropdown when search mode becomes active
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            // Show clients with command mode BonVents immediately when search mode is activated
            filteredClients = clientsWithCommandBonVents
            showDropdown = clientsWithCommandBonVents.isNotEmpty()

            // Focus the search field
            delay(100) // Small delay to ensure the field is rendered
            focusRequester.requestFocus()
        }
    }

    // Debounced search with flow
    LaunchedEffect(searchQuery) {
        if (isSearchMode) {
            searchQueryFlow.value = searchQuery
        }
    }

    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            searchQueryFlow
                .debounce(300) // Reduced debounce time for better responsiveness
                .distinctUntilChanged()
                .collect { debouncedQuery ->
                    performClientSearch(
                        viewModel = viewModel,
                        clientsWithCommandBonVents = clientsWithCommandBonVents,
                        searchQuery = debouncedQuery,
                        hClientRepository = hClientRepository,
                        onFilteredClientsChange = { filteredClients = it },
                        onShowDropdownChange = { showDropdown = it },
                        isSearchMode = isSearchMode
                    )
                }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
    ) {
        if (!isSearchMode) {
            FloatingActionButton(
                modifier = Modifier
                    .getSemanticsTag(clientsWithCommandBonVents, "clientsWithCommandBonVents")
                    .size(40.dp),
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
                    // Calculate time elapsed since creation
                    val timeElapsed = getTimeElapsedString(onVentId8BonVent.creationTimestamps)
                    val totalProducts = calculateTotalProducts(onVentId8BonVent)

                    Text(
                        text = if (isTextCollapsed) {
                            nomClient
                        } else {
                            if (onVentId8BonVent.nomClientConcerned.isNotEmpty() && onVentId8BonVent.nomClientConcerned != "Non Defini") {
                                "$nomClient - $timeElapsed - $totalProducts produits"
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
            }
        } else {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
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
                                    viewModel = viewModel,
                                    client = client,
                                    onClick = {
                                        onClientSelectedToToast(client)
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

private fun performClientSearch(
    searchQuery: String,
    hClientRepository: Repo2Client,
    onFilteredClientsChange: (List<HClientInfos>) -> Unit,
    onShowDropdownChange: (Boolean) -> Unit,
    isSearchMode: Boolean,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    clientsWithCommandBonVents: List<HClientInfos>,
) {
    if (!isSearchMode) {
        onFilteredClientsChange(emptyList())
        onShowDropdownChange(false)
        return
    }

    if (searchQuery.isEmpty()) {
        // When search is empty, show clients with command mode BonVents
        onFilteredClientsChange(clientsWithCommandBonVents)
        onShowDropdownChange(clientsWithCommandBonVents.isNotEmpty())
    } else {
        // Filter all clients based on search query
        val filtered = hClientRepository.datasValue.filter { client ->
            client.nom.contains(searchQuery, ignoreCase = true) ||
                    client.numTelephone.contains(searchQuery, ignoreCase = true)
        }
        onFilteredClientsChange(filtered)
        onShowDropdownChange(filtered.isNotEmpty())
    }
}

// Helper function to calculate time elapsed since creation
private fun getTimeElapsedString(creationTimestamp: Long): String {
    val now = System.currentTimeMillis()
    val elapsed = now - creationTimestamp

    val days = TimeUnit.MILLISECONDS.toDays(elapsed)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsed) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60

    return when {
        days > 0 -> "${days}j ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "< 1m"
    }
}

private fun calculateTotalProducts(bonVent: M8BonVent): Int {
    // This is a placeholder - you would need to implement the actual logic
    // based on your product/item structure
    return 0 // Replace with actual calculation
}
private fun getClientsWithCommandModeBonVents(
    hClientRepository: Repo2Client,
    bonVentRepository: Repo8BonVent
): List<HClientInfos> {
    // Get all BonVents with ON_MODE_COMMEND_ACTUELLEMENT state
    val commandModeBonVents = bonVentRepository.datasValue.filter { bonVent ->
        bonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    }

    // Get client IDs from these BonVents
    val clientKeysInCommandMode = commandModeBonVents.map { it.parentM2ClientInfosKey }.toSet()

    // Filter clients that have BonVents in command mode
    val clientsWithCommandBonVents = hClientRepository.datasValue.filter { client ->
        client.keyID in clientKeysInCommandMode
    }

    // Sort by most recent BonVent update time
    return clientsWithCommandBonVents.sortedByDescending { client ->
        commandModeBonVents
            .filter { it.parentM2ClientInfosKey == client.keyID }
            .maxOfOrNull { it.dernierTimeTampsSynchronisationAvecFireBase } ?: 0L
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun CreateNewClientIcon(
    searchQuery: String,
    locationTracker: LocationTracker?,
    defaultId8BonVent: M8BonVent,
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
                viewModel.setter.updateFocuceM9AppCompt(updatedAppCompt)
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

    // Calculate time elapsed since last update
    val timeElapsed = getTimeElapsedString(client.dernierTimeTampsSynchronisationAvecFireBase)

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
                    viewModel.setter.updateFocuceM9AppCompt(newCurrentM9AppCompt)
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

            // Show time elapsed since last update
            Text(
                text = "Dernière mise à jour: $timeElapsed",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
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

            // Show BonVent state
            Text(
                text = "État: ${client.actuelleEtat.nomArabe}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(client.actuelleEtat.color)
            )
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
