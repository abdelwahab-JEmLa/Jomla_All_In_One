package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.Z.ClientSearchItem.View

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.CreateNewClientIcon
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List.View_List_DropDownButtons
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.util.concurrent.TimeUnit

@OptIn(FlowPreview::class)
@SuppressLint("DefaultLocale")
@Composable
fun ID4ClientSearchButton(
    uiState: ViewModelPresistantButtonsSec8FWinID1.UiState,
    hClientRepository: Repo2Client,
    showLabels: Boolean,
    locationTracker: LocationTracker? = null,
    onClientSelectedToToast: (M2Client) -> Unit = {},
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    repositorysMainGetter: RepositorysMainGetter=koinInject()
) {           //<--
//TODO(1): ici affiche to les clients ou il on un bon vent de current vent period 
    val getter = uiState.focusedVarsHandlerFacade.focusedValuesGetter
    var isTextCollapsed by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<M2Client>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    var isFournisseurMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val searchQueryFlow = remember { MutableStateFlow("") }

    val currentValues = getter.active_Central_Values
    val deletionList = currentValues.list_clients_por_suprime
    val deletionKeyIds = deletionList.map { it.keyID }.toSet()

    val clientsWithCommandBonVents =
        getter.filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT
            .filter { !deletionKeyIds.contains(it.keyID) } // Filter out clients in deletion list

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

    LaunchedEffect(isSearchMode, deletionKeyIds) {
        if (isSearchMode) {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isEmpty()) {
                        filteredClients = clientsWithCommandBonVents
                        showDropdown = clientsWithCommandBonVents.isNotEmpty()
                    } else {
                        val filtered = hClientRepository.datasValue
                            .filter { client ->
                                !deletionKeyIds.contains(client.keyID) &&
                                        (client.nom.contains(query, ignoreCase = true) ||
                                                client.numTelephone.contains(query, ignoreCase = true))
                            }
                        filteredClients = filtered
                        showDropdown = filtered.isNotEmpty()
                    }
                }
        }
    }

    Row(
        modifier = Modifier
            .getSemanticsTag(
                nomVal = "hClientRepository",
                data = hClientRepository.datasValue.filter { it.nom.contains("rach") }
                    .map { it.keyID }.takeLast(4)
            )
            .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                1,
                "clientsWithCommandBonVents",
                clientsWithCommandBonVents
            ),
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
                val nomClient = getter.activeOnVent_M2Client?.nom ?: ""
                val onVentId8BonVent = getter.activeOnVent_M8BonVent

                Text(
                    text = if (isTextCollapsed) {
                        nomClient
                    } else {
                        onVentId8BonVent?.let { bon ->
                            val timeElapsed = getTimeElapsedString(bon.creationTimestamps)

                            val (totalProducts, totalValue) = get_vents_datas(aCentralFacade)

                            if (bon.parent_M2Client_DebugInfos.isNotEmpty() && bon.parent_M2Client_DebugInfos != "Non Defini") {
                                "$nomClient - $timeElapsed - $totalProducts P - ${String.format("%.2f", totalValue)} DA"
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // FAB to open marker status dialog for active client
                        val activeClient = getter.activeOnVent_M2Client
                        if (activeClient != null) {
                            FloatingActionButton(
                                modifier = Modifier.size(32.dp),
                                onClick = {
                                    val currentValues = getter.active_Central_Values
                                    focusedValuesGetter.update_activeCentralValues(
                                        currentValues.copy(markerStatusDialogActiveM2Client = activeClient)
                                    )
                                },
                                containerColor = Color(0xFF9C27B0)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Statut du client",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Toggle Button for Client/Fournisseur
                        ClientFournisseurToggleButton(
                            isFournisseurMode = isFournisseurMode,
                            onToggle = { isFournisseurMode = !isFournisseurMode }
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .width(200.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .focusRequester(focusRequester),
                            value = searchQuery,
                            onValueChange = { newText ->
                                // Auto-capitalize first letter of each word
                                val capitalizedText = newText.split(" ").joinToString(" ") { word ->
                                    if (word.isNotEmpty()) {
                                        word.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase() else it.toString()
                                        }
                                    } else {
                                        word
                                    }
                                }
                                searchQuery = capitalizedText
                            },
                            placeholder = {
                                Text(
                                    if (isFournisseurMode) "Nom fournisseur ou téléphone..."
                                    else "Nom client ou téléphone..."
                                )
                            },
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
                                    viewModel = viewModel,
                                    isFournisseurMode = isFournisseurMode
                                )
                            },
                            trailingIcon = {
                                // FIXED TODO(1): Switch between Close and Delete icon based on search query
                                if (searchQuery.trim().equals("supp", ignoreCase = true)) {  //<--
                                //TODO(1): fait ici si supp de affiche les client a supp list 
                                    IconButton(
                                        onClick = {
                                            // Delete all clients in the deletion list from repository
                                            val clientsToDelete = currentValues.list_clients_por_suprime
                                            if (clientsToDelete.isNotEmpty()) {
                                                clientsToDelete.forEach { client ->
                                                    viewModel.aCentralFacade.repositorysMainSetter.delete_M2Client(client)
                                                }

                                                // Clear the deletion list
                                                focusedValuesGetter.update_activeCentralValues(
                                                    currentValues.copy(list_clients_por_suprime = emptyList())
                                                )

                                                // Show confirmation toast
                                                onClientSelectedToToast(
                                                    M2Client().copy(
                                                        nom = "${clientsToDelete.size} client(s) supprimé(s)"
                                                    )
                                                )
                                            }

                                            // Reset search mode
                                            isSearchMode = false
                                            searchQuery = ""
                                            showDropdown = false
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer les clients",
                                            tint = Color(0xFFFF5722) // Red color for delete
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            isSearchMode = false
                                            searchQuery = ""
                                            showDropdown = false
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Fermer"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                // Display current mode
                Text(
                    text = "Mode: ${if (isFournisseurMode) "Fournisseur" else "Client"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFournisseurMode) Color(0xFFFF9800) else Color(0xFF2196F3),
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 2.dp)
                )

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

fun get_vents_datas(aCentralFacade: ACentralFacade): Pair<Int, Double> {
    // Get the list of vents for this bon
    val onVentList = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

    val ventsTrouve = onVentList.filter {
        it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
    }

    val totalProducts = ventsTrouve.groupBy { it.parent_M1Produit_KeyId }.size

    // Calculate total value (similar to CartSummarySection)
    val totalValue = ventsTrouve.sumOf { vent ->
        val provisoireMonPrix = aCentralFacade.repositorysMainGetter
            .find_M13Tarification_By_KeyID(vent.parentM13TarificationKeyID)
            ?.prixCurrency ?: 0.0

        vent.quantity * provisoireMonPrix
    }
    return Pair(totalProducts, totalValue)
}

@Composable
private fun ClientFournisseurToggleButton(
    isFournisseurMode: Boolean,
    onToggle: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.size(32.dp),
        onClick = {
            onToggle()
        },
        containerColor = if (isFournisseurMode) Color(0xFFFF9800) else Color(0xFF2196F3)
    ) {
        Text(
            text = if (isFournisseurMode) "F" else "C",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

fun getTimeElapsedString(creationTimestamp: Long): String {
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
