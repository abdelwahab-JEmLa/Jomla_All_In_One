package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetterFocusedValues
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
import androidx.compose.material3.ElevatedCard
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
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
    val getter = uiState.focusedVarsHandlerFacade.getterFocusedValues
    var isTextCollapsed by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<HClientInfos>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val searchQueryFlow = remember { MutableStateFlow("") }

    val clientsWithCommandBonVents = getter.filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT

    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            filteredClients = emptyList()
            showDropdown = false

            withContext(Dispatchers.Main) {
                delay(100)
                focusRequester.requestFocus()
            }
        }
    }

    LaunchedEffect(searchQuery) {
        if (isSearchMode) searchQueryFlow.value = searchQuery
    }

    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isEmpty()) {
                        filteredClients = clientsWithCommandBonVents
                        showDropdown = clientsWithCommandBonVents.isNotEmpty()
                    } else {
                        val filtered = hClientRepository.datasValue.filter { client ->
                            client.nom.contains(query, ignoreCase = true) ||
                                    client.numTelephone.contains(query, ignoreCase = true)
                        }
                        filteredClients = filtered
                        showDropdown = filtered.isNotEmpty()
                    }
                }
        }
    }

    Row(
        modifier = Modifier
            .getSemanticsTag(hClientRepository.datasValue.filter { it.nom.contains("rach") }
                .map { it.keyID }.takeLast(4), "hClientRepository")
            .getSemanticsTag(clientsWithCommandBonVents, "clientsWithCommandBonVents", 1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (!isSearchMode) {
            FloatingActionButton(
                modifier = Modifier
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
                val onVentId8BonVent = getter.onVentM8BonVent

                Text(
                    text = if (isTextCollapsed) {
                        nomClient
                    } else {
                        onVentId8BonVent?.let { bon ->
                            val timeElapsed = getTimeElapsedString(bon.creationTimestamps)
                            val totalProducts =
                                viewModel.aCentralFacade.focusedActiveValuesFacade.getterFocusedValues
                                    .onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
                                    .filter { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve }
                                    .groupBy { it.parentM1ProduitInfosKeyId }.size

                            if (bon.parent_M2Client_DebugInfos.isNotEmpty() && bon.parent_M2Client_DebugInfos != "Non Defini") {
                                "$nomClient - $timeElapsed - $totalProducts produits"
                            } else "Rechercher Client"
                        } ?: run {
                            val count = clientsWithCommandBonVents.size
                            if (count > 0) "$count clients ouverts" else "Aucun client ouvert"
                        }
                    },
                    modifier = Modifier
                        .background(Color(0xFF4CAF50))
                        .padding(4.dp)
                        .clickable { isTextCollapsed = !isTextCollapsed },
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
                                defaultId8BonVent = getter.getDefaultM8BonVent(),
                                onClientSelectedToToast = onClientSelectedToToast,
                                onResetSearchMode = {
                                    isSearchMode = false
                                    searchQuery = ""
                                    showDropdown = false
                                },
                                viewModel = viewModel
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    isSearchMode = false
                                    searchQuery = ""
                                    showDropdown = false
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
                    View_List_DropDownButtons(
                        clientsWithCommandBonVents = clientsWithCommandBonVents,
                        filteredClients = filteredClients,
                        onClientSelectedToToast = onClientSelectedToToast,
                        onSearchModeChanged = { isSearchMode = it },
                        onSearchQueryChanged = { searchQuery = it },
                        onShowDropdownChanged = { showDropdown = it },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
@Composable
private fun View_List_DropDownButtons(
    clientsWithCommandBonVents: List<HClientInfos>,
    filteredClients: List<HClientInfos>,
    onClientSelectedToToast: (HClientInfos) -> Unit,
    onSearchModeChanged: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onShowDropdownChanged: (Boolean) -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .heightIn(max = 200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyColumn(
            Modifier.getSemanticsTag(
                clientsWithCommandBonVents,
                "clientsWithCommandBonVents"
            )
        ) {
            items(filteredClients) { client ->
                ClientSearchItem(
                    m2Client = client,
                    onClick = {
                        onClientSelectedToToast(client)
                        onSearchModeChanged(false)
                        onSearchQueryChanged("")
                        onShowDropdownChanged(false)
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

private fun getTimeElapsedString(creationTimestamp: Long): String {
    val elapsed = System.currentTimeMillis() - creationTimestamp
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
        snippet = currentLocation?.let {
            "Lat: ${String.format("%.6f", it.latitude)}, Lng: ${
                String.format(
                    "%.6f",
                    it.longitude
                )
            }"
        } ?: "Position non disponible"
    )

    val addedDefaultOnVentID8BonVentEtAdd = defaultId8BonVent.copy(
        creationTimestamps = System.currentTimeMillis(),
        parent_M2Client_KeyID = newClient.keyID,
        parent_M2Client_DebugInfos = newClient.nom
    )

    val updatedAppCompt = viewModel.getterFocusedVarsHandlerFacade.currentM9AppCompt?.copy(
        onVentM8BonVentKey = addedDefaultOnVentID8BonVentEtAdd.keyID,
        onVentM8BonVentDebugInfos = addedDefaultOnVentID8BonVentEtAdd.get_DebugInfos()
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
            set(SemanticsPropertyKey("Debug currentM9AppCompt avec new M8BonVent"), updatedAppCompt)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Créer nouveau client",
        )
    }
}
@SuppressLint("DefaultLocale")
@Composable
fun ClientSearchItem(
    m2Client: HClientInfos,
    onClick: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
) {
    val bonVentRepository = viewModel.aCentralFacade.getRepositorys.repo8BonVent

    // Add toast state management
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    val latestBonVent = remember(m2Client.keyID, bonVentRepository.datasValue) {
        bonVentRepository.datasValue
            .filter { it.parent_M2Client_KeyID == m2Client.keyID }
            .maxByOrNull { it.creationTimestamps }
    }
    val get = viewModel.aCentralFacade.focusedActiveValuesFacade.getterFocusedValues

    fun getTargetedM8BonVent(
        getFocusedVars: GetterFocusedValues,
        repo8BonVent: Repo8BonVent,
        m2Client: HClientInfos,
        onShowToast: (ToastData) -> Unit
    ): Triple<M8BonVent?, M8BonVent, Modifier>? {
        val currentPeriod = getFocusedVars.currentActiveFocuced_M14VentPeriode

        // Handle null currentPeriod with toast
        if (currentPeriod == null) {
            onShowToast(
                ToastData(
                    message = "Aucune période de vente active trouvée",
                    type = ToastType.ERROR,
                    duration = 4000L
                )
            )
            return null // Return null to indicate failure
        }

        val currentPeriodKeyID = currentPeriod.keyID
        val parentAppComptKeyID = currentPeriod.parent_M9AppCompt_KeyID ?: "null"

        val existingBonVent = repo8BonVent.datasValue.find { bonVent ->
            bonVent.parent_M14VentPeriod_KeyId == currentPeriodKeyID &&
                    bonVent.parent_M2Client_KeyID == m2Client.keyID
        }

        val newBonVent = M8BonVent().copy(
            parent_M9AppCompt_KeyID = parentAppComptKeyID,
            parent_M14VentPeriod_KeyId = currentPeriodKeyID,
            parent_M2Client_KeyID = m2Client.keyID,
            parent_M2Client_DebugInfos = m2Client.nom,
        )

        val semanticsModifier = Modifier.getSemanticsTag(newBonVent, "newBonVent")

        return Triple(existingBonVent, newBonVent, semanticsModifier)
    }

    val bonVentResult = getTargetedM8BonVent(
        get,
        bonVentRepository,
        m2Client
    ) { toastDataToShow ->
        toastData = toastDataToShow
    }

    // Render the toast using the composable properly
    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    // Only render the card if we have a valid result
    bonVentResult?.let { (existingBonVent, newBonVent, semanticsModifier) ->
        ElevatedCard(
            modifier = Modifier
                .padding(petitePaddine)
                .then(semanticsModifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val handleClick = {
                            if (existingBonVent != null) {
                                viewModel.aCentralFacade.setRepositorys.update_IfExist_Setter(
                                    existingBonVent
                                )
                            } else {
                                viewModel.aCentralFacade.focusedActiveValuesFacade.set.add_M8BonVent(
                                    newBonVent
                                )
                            }

                            viewModel.aCentralFacade.focusedActiveValuesFacade.set.setIN_M9CurrentApp_onVentM8BonVentKey(
                                existingBonVent ?: newBonVent
                            )
                        }
                        handleClick()
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
                            color = Color(
                                latestBonVent?.etateActuellementEst?.color
                                    ?: m2Client.actuelleEtat.color
                            ),
                            shape = CircleShape
                        )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = m2Client.nom,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    latestBonVent?.let {
                        Text(
                            text = "Dernière commande: ${getTimeElapsedString(it.creationTimestamps)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    if (m2Client.caMarqueGpsEstOuvert && m2Client.latitude != 0.0 && m2Client.longitude != 0.0) {
                        Text(
                            text = "📍 ${
                                String.format(
                                    "%.4f",
                                    m2Client.latitude
                                )
                            }, ${String.format("%.4f", m2Client.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Row {
                    Icon(
                        imageVector = m2Client.clientTypeMode.icon,
                        contentDescription = null,
                        tint = m2Client.clientTypeMode.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = m2Client.clientTypeMode.color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    } ?: run {
        // Optional: Show a placeholder or error state when bonVentResult is null
        ElevatedCard(
            modifier = Modifier
                .padding(petitePaddine)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = Color.Gray,
                            shape = CircleShape
                        )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = m2Client.nom,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Période de vente inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
