package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
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
import androidx.compose.runtime.Stable
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Refactored client search component with improved threading, state management, and code organization
 */
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
    val searchState = remember { ClientSearchState() }
    val coroutineScope = rememberCoroutineScope()

    val clientsWithCommandBonVents = remember(hClientRepository.datasValue, viewModel.aCentralFacade.get.repo8BonVent.datasValue) {
        ClientSearchUtils.getClientsWithActiveBonVents(
            hClientRepository = hClientRepository,
            bonVentRepository = viewModel.aCentralFacade.get.repo8BonVent
        )
    }

    // Handle search mode transitions with proper threading
    LaunchedEffect(searchState.isSearchMode) {
        if (searchState.isSearchMode) {
            searchState.filteredClients = clientsWithCommandBonVents
            searchState.showDropdown = clientsWithCommandBonVents.isNotEmpty()

            // Ensure focus request runs on main thread with proper timing
            coroutineScope.launch(Dispatchers.Main) {
                delay(150) // Increased delay for better reliability
                try {
                    searchState.focusRequester.requestFocus()
                } catch (e: Exception) {
                    // Handle potential focus request errors gracefully
                    println("Focus request failed: ${e.message}")
                }
            }
        }
    }

    // Debounced search implementation
    LaunchedEffect(searchState.searchQuery) {
        if (searchState.isSearchMode) {
            searchState.searchQueryFlow.value = searchState.searchQuery
        }
    }

    LaunchedEffect(searchState.isSearchMode) {
        if (searchState.isSearchMode) {
            searchState.searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { debouncedQuery ->
                    ClientSearchUtils.performSearch(
                        searchQuery = debouncedQuery,
                        clientsWithCommandBonVents = clientsWithCommandBonVents,
                        hClientRepository = hClientRepository,
                        onResult = { filtered ->
                            searchState.filteredClients = filtered
                            searchState.showDropdown = filtered.isNotEmpty()
                        }
                    )
                }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
    ) {
        if (!searchState.isSearchMode) {
            NormalModeContent(
                searchState = searchState,
                clientsWithCommandBonVents = clientsWithCommandBonVents,
                showLabels = showLabels,
                uiState = uiState,
                viewModel = viewModel
            )
        } else {
            SearchModeContent(
                searchState = searchState,
                clientsWithCommandBonVents = clientsWithCommandBonVents,
                locationTracker = locationTracker,
                onClientSelectedToToast = onClientSelectedToToast,
                viewModel = viewModel
            )
        }
    }
}

/**
 * State holder for client search functionality
 */
@Stable
class ClientSearchState {
    var isSearchMode by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var filteredClients by mutableStateOf<List<HClientInfos>>(emptyList())
    var showDropdown by mutableStateOf(false)
    var isTextCollapsed by mutableStateOf(false)
    val focusRequester = FocusRequester()
    val searchQueryFlow = MutableStateFlow("")

    fun resetSearch() {
        isSearchMode = false
        searchQuery = ""
        showDropdown = false
        filteredClients = emptyList()
    }
}

/**
 * Normal mode content - shows the search button and client info
 */
@Composable
private fun NormalModeContent(
    searchState: ClientSearchState,
    clientsWithCommandBonVents: List<HClientInfos>,
    showLabels: Boolean,
    uiState: ViewModelPresistantButtonsSec8FWinID1.UiState,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val getter = uiState.focusedVarsHandlerFacade.get

    FloatingActionButton(
        modifier = Modifier
            .getSemanticsTag(clientsWithCommandBonVents, "clientsWithCommandBonVents")
            .size(40.dp),
        onClick = { searchState.isSearchMode = true },
        containerColor = Color(0xFF4CAF50)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Rechercher Client",
            tint = Color.White
        )
    }

    if (showLabels) {
        ClientInfoLabel(
            searchState = searchState,
            getter = getter,
            viewModel = viewModel
        )
    }
}

/**
 * Client info label component
 */
@Composable
private fun ClientInfoLabel(
    searchState: ClientSearchState,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    getter: GetFocusedVars
) {
    val nomClient = getter.onVentM2ClientInfos?.nom ?: ""
    val onVentId8BonVent = getter.onVentM8BonVent

    if (onVentId8BonVent != null) {
        val timeElapsed = TimeUtils.getTimeElapsedString(onVentId8BonVent.creationTimestamps)
        val totalProducts = ClientSearchUtils.calculateTotalProducts(viewModel)

        Text(
            text = if (searchState.isTextCollapsed) {
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
                .clickable { searchState.isTextCollapsed = !searchState.isTextCollapsed },
            color = Color.White
        )
    }
}

/**
 * Search mode content - shows the search field and dropdown
 */
@Composable
private fun SearchModeContent(
    searchState: ClientSearchState,
    clientsWithCommandBonVents: List<HClientInfos>,
    locationTracker: LocationTracker?,
    onClientSelectedToToast: (HClientInfos) -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    Column {
        SearchField(
            searchState = searchState,
            locationTracker = locationTracker,
            onClientSelectedToToast = onClientSelectedToToast,
            viewModel = viewModel
        )

        if (searchState.showDropdown) {
            SearchDropdown(
                searchState = searchState,
                clientsWithCommandBonVents = clientsWithCommandBonVents,
                onClientSelectedToToast = onClientSelectedToToast,
                viewModel = viewModel
            )
        }
    }
}

/**
 * Search field component
 */
@Composable
private fun SearchField(
    searchState: ClientSearchState,
    locationTracker: LocationTracker?,
    onClientSelectedToToast: (HClientInfos) -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val getter = viewModel.aCentralFacade.focusedActiveValuesFacade.get
    val defaultId8BonVent = getter.getDefaultM8BonVent()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .width(200.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .focusRequester(searchState.focusRequester),
            value = searchState.searchQuery,
            onValueChange = { searchState.searchQuery = it },
            placeholder = { Text("Nom ou téléphone...") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
            ),
            leadingIcon = {
                CreateNewClientIcon(
                    searchQuery = searchState.searchQuery,
                    locationTracker = locationTracker,
                    defaultId8BonVent = defaultId8BonVent,
                    onClientSelectedToToast = onClientSelectedToToast,
                    onResetSearchMode = { searchState.resetSearch() },
                    viewModel = viewModel
                )
            },
            trailingIcon = {
                IconButton(onClick = { searchState.resetSearch() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer"
                    )
                }
            }
        )
    }
}

/**
 * Search dropdown component
 */
@Composable
private fun SearchDropdown(
    searchState: ClientSearchState,
    clientsWithCommandBonVents: List<HClientInfos>,
    onClientSelectedToToast: (HClientInfos) -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .heightIn(max = 200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyColumn(
            Modifier.getSemanticsTag(clientsWithCommandBonVents, "clientsWithCommandBonVents")
        ) {
            items(searchState.filteredClients) { client ->
                ClientSearchItem(
                    viewModel = viewModel,
                    client = client,
                    onClick = {
                        onClientSelectedToToast(client)
                        searchState.resetSearch()
                    }
                )
            }
        }
    }
}

/**
 * Create new client icon component
 */
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
            set(SemanticsPropertyKey("Debug new M8BonVent"), addedDefaultOnVentID8BonVentEtAdd)
            set(
                SemanticsPropertyKey("Debug currentM9AppCompt avec new M8BonVent"),
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

/**
 * Client search item component
 */
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
        ClientSearchUtils.getLatestBonVentStateInfo(client, bonVentRepository)
    }

    val timeElapsed = TimeUtils.getTimeElapsedString(client.dernierTimeTampsSynchronisationAvecFireBase)

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
        // State indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = Color(latestStateInfo?.first?.color ?: client.actuelleEtat.color),
                    shape = CircleShape
                )
        )

        // Client info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client.nom,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            latestStateInfo?.let { (state, creationTime) ->
                val stateTimeElapsed = TimeUtils.getTimeElapsedString(creationTime)
                Text(
                    text = "Dernière commande: $stateTimeElapsed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // GPS location if available
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

        // Client type icons
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

/**
 * Utility functions for client search operations
 */
object ClientSearchUtils {

    fun getClientsWithActiveBonVents(
        hClientRepository: Repo2Client,
        bonVentRepository: Repo8BonVent
    ): List<HClientInfos> {
        val allClients = hClientRepository.datasValue
        val allBonVents = bonVentRepository.datasValue

        return allClients.filter { client ->
            val latestBonVent = allBonVents
                .filter { bonVent -> bonVent.parentM2ClientInfosKey == client.keyID }
                .maxByOrNull { it.creationTimestamps }

            latestBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        }.sortedByDescending { client ->
            getLatestBonVentForClient(client, bonVentRepository)?.creationTimestamps ?: 0L
        }
    }

    fun performSearch(
        searchQuery: String,
        clientsWithCommandBonVents: List<HClientInfos>,
        hClientRepository: Repo2Client,
        onResult: (List<HClientInfos>) -> Unit
    ) {
        val result = if (searchQuery.isEmpty()) {
            clientsWithCommandBonVents
        } else {
            hClientRepository.datasValue.filter { client ->
                client.nom.contains(searchQuery, ignoreCase = true) ||
                        client.numTelephone.contains(searchQuery, ignoreCase = true)
            }
        }
        onResult(result)
    }

    fun calculateTotalProducts(viewModel: ViewModelPresistantButtonsSec8FWinID1): Int {
        val allVents = viewModel.aCentralFacade.focusedActiveValuesFacade.get
            .onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent

        return allVents
            .filter { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve }
            .groupBy { it.parentM1ProduitInfosKeyId }
            .size
    }

    fun getLatestBonVentForClient(
        client: HClientInfos,
        bonVentRepository: Repo8BonVent
    ): M8BonVent? {
        return bonVentRepository.datasValue
            .filter { bonVent -> bonVent.parentM2ClientInfosKey == client.keyID }
            .maxByOrNull { it.creationTimestamps }
    }

    fun getLatestBonVentStateInfo(
        client: HClientInfos,
        bonVentRepository: Repo8BonVent
    ): Pair<M8BonVent.EtateActuellementEst, Long>? {
        return getLatestBonVentForClient(client, bonVentRepository)?.let {
            Pair(it.etateActuellementEst, it.creationTimestamps)
        }
    }
}

/**
 * Utility functions for time operations
 */
object TimeUtils {

    fun getTimeElapsedString(creationTimestamp: Long): String {
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
}
