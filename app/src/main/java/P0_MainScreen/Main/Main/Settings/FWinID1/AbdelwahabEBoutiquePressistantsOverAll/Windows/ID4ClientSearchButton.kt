package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

// Complete implementation for paste-2.txt ID4ClientSearchButton with all fixes
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
    val defaultId8BonVent = getter.getDefaultM8BonVent()

    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<HClientInfos>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Add coroutine scope for handling focus requests
    val coroutineScope = rememberCoroutineScope()

    val searchQueryFlow = remember { MutableStateFlow("") }

    val repo8BonVent = viewModel.aCentralFacade.get.repo8BonVent
    val datasValue = repo8BonVent.datasValue
    val clientsWithCommandBonVents =
        remember(hClientRepository.datasValue, datasValue) {
            val allClients = hClientRepository.datasValue

            val clientsWithLatestCommandBonVents = allClients.filter { client ->
                val latestBonVent = datasValue
                    .filter { bonVent -> bonVent.parentM2ClientInfosKey == client.keyID }
                    .maxByOrNull { it.creationTimestamps }

                latestBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            }

            clientsWithLatestCommandBonVents.sortedByDescending { client ->
                getLatestBonVentForClient(client, repo8BonVent)?.creationTimestamps ?: 0L
            }
        }

    // FIX 1: Use coroutineScope to ensure focus request runs on main thread
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            filteredClients = clientsWithCommandBonVents
            showDropdown = clientsWithCommandBonVents.isNotEmpty()

            // Use coroutine scope to ensure main thread execution
            coroutineScope.launch {
                delay(100) // Small delay to ensure the field is rendered
                // This will run on the main thread by default in rememberCoroutineScope
                focusRequester.requestFocus()
            }
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
                onClick = { isSearchMode = true },
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
                    val timeElapsed = getTimeElapsedString(onVentId8BonVent.creationTimestamps)
                    val totalProducts = calculateTotalProducts(viewModel)

                    Text(
                        text = if (isTextCollapsed) {
                            nomClient
                        } else {
                            if (onVentId8BonVent.nomClientConcerned.isNotEmpty() &&
                                onVentId8BonVent.nomClientConcerned != "Non Defini") {
                                "$nomClient - $timeElapsed - $totalProducts produits"
                            } else {
                                "Rechercher Client"
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFF4CAF50))
                            .padding(4.dp)
                            .clickable { isTextCollapsed = !isTextCollapsed },
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
                        LazyColumn(
                            Modifier
                                .getSemanticsTag(clientsWithCommandBonVents,"clientsWithCommandBonVents")
                        ) {
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
        onFilteredClientsChange(clientsWithCommandBonVents)
        onShowDropdownChange(clientsWithCommandBonVents.isNotEmpty())
    } else {
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

    val addedDefaultOnVentID8BonVentEtAdd = defaultId8BonVent.copy(
        debugInfos = newClient.nom,
        creationTimestamps = System.currentTimeMillis(),
        parentM2ClientInfosKey = newClient.keyID,
        parentM2ClientInfosDebugName = newClient.nom
    )

    val updatedAppCompt = viewModel.getterFocusedVarsHandlerFacade.currentM9AppCompt?.copy(
        onVentM8BonVentKey = addedDefaultOnVentID8BonVentEtAdd.keyID,
        onVentM8BonVentDebugInfos = addedDefaultOnVentID8BonVentEtAdd.debugInfos
    )

    IconButton(
        onClick = {
            viewModel.setter.addNewM2ClientInfos(newClient)
            viewModel.aCentralFacade.focusedActiveValuesFacade.set.upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
                addedDefaultOnVentID8BonVentEtAdd,
                updatedAppCompt
            )

            onClientSelectedToToast(newClient)
            onResetSearchMode()
        },
        modifier = Modifier.semantics(mergeDescendants = true) {
            set(SemanticsPropertyKey("Debug  new M8BonVent"), addedDefaultOnVentID8BonVentEtAdd)
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

private fun calculateTotalProducts(
    viewModel: ViewModelPresistantButtonsSec8FWinID1
): Int {
    val allVents = viewModel.aCentralFacade.focusedActiveValuesFacade.get.onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent

    val ventsTrouve = allVents.filter {
        it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
    }
    val totalProducts = ventsTrouve.groupBy { it.parentM1ProduitInfosKeyId }.size

    return totalProducts
}

// Updated ClientSearchItem composable with state indicator
@SuppressLint("DefaultLocale")
@Composable
fun ClientSearchItem(
    client: HClientInfos,
    onClick: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val (editedM8BonVent, editedM9CurrCompt) =
        viewModel.aCentralFacade.focusedActiveValuesFacade.get
            .get_By_Client_Edited_M8BonVent_Et_M9CurrComptFacade(client)

    val bonVentRepository = viewModel.aCentralFacade.get.repo8BonVent
    val latestStateInfo = remember(client.keyID, bonVentRepository.datasValue) {
        getLatestBonVentStateInfo(client, bonVentRepository)
    }

    val timeElapsed = getTimeElapsedString(client.dernierTimeTampsSynchronisationAvecFireBase)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.aCentralFacade.focusedActiveValuesFacade.set.upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
                    editedM8BonVent,
                    editedM9CurrCompt
                )
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
                    color = Color(latestStateInfo?.first?.color ?: client.actuelleEtat.color),
                    shape = CircleShape
                )
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client.nom,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            latestStateInfo?.let { (state, creationTime) ->
                val stateTimeElapsed = getTimeElapsedString(creationTime)
                Text(
                    text = "Dernière commande: $stateTimeElapsed",
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

// Helper function to get the latest BonVent for a specific client
private fun getLatestBonVentForClient(
    client: HClientInfos,
    bonVentRepository: Repo8BonVent
): M8BonVent? {
    return bonVentRepository.datasValue
        .filter { bonVent -> bonVent.parentM2ClientInfosKey == client.keyID }
        .maxByOrNull { it.creationTimestamps }
}

// Update the existing getLatestBonVentStateInfo function to use the helper
private fun getLatestBonVentStateInfo(
    client: HClientInfos,
    bonVentRepository: Repo8BonVent
): Pair<M8BonVent.EtateActuellementEst, Long>? {
    val latestBonVent = getLatestBonVentForClient(client, bonVentRepository)

    return latestBonVent?.let {
        Pair(it.etateActuellementEst, it.creationTimestamps)
    }
}
