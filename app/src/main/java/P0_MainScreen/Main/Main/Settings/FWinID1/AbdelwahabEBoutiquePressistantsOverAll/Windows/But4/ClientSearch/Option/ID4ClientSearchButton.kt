package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List.View_List_DropDownButtons
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
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
    viewModel: ViewModelPresistantButtonsSec8FWinID1
) {
    val getter = uiState.focusedVarsHandlerFacade.focusedValuesGetter
    var isTextCollapsed by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredClients by remember { mutableStateOf<List<M2Client>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    var isFournisseurMode by remember { mutableStateOf(false) } // New state for toggle
    val focusRequester = remember { FocusRequester() }
    val searchQueryFlow = remember { MutableStateFlow("") }

    val clientsWithCommandBonVents =
        getter.filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT

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

                            // Get the list of vents for this bon
                            val onVentList = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                                .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

                            val ventsTrouve = onVentList.filter {
                                it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
                            }

                            val totalProducts = ventsTrouve.groupBy { it.parent_M1Produit_KeyId }.size

                            // Calculate total value (similar to CartSummarySection)
                            val totalValue = ventsTrouve.sumOf { vent ->
                                val provisoireMonPrix = viewModel.aCentralFacade.repositorysMainGetter
                                    .find_M13Tarification_By_KeyID(vent.parentM13TarificationKeyID)
                                    ?.prixCurrency ?: 0.0

                                vent.quantity * provisoireMonPrix
                            }

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
                    // Separate Toast Button
                    ToastCommandeButton()

                    // Toggle Button for Client/Fournisseur (without toast)
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

                // Display current mode
                Text(
                    text = "Mode: ${if (isFournisseurMode) "Fournisseur" else "Client"}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
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

@Composable
private fun ToastCommandeButton() {
    val context = LocalContext.current

    FloatingActionButton(
        modifier = Modifier.size(32.dp),
        onClick = {
            //<--
            //TODO(1): fait ici de cree un bon de vent son client = cherceher au lcient ou son nom ==  //"Jamel Bel" son etate on command
            //cree des vents apre actelisatio depuit firebase vents pour chaque vent du actuelle vent period et du catalogeue confeserie  et leur .places_au_depot
            // groupe par couleur key id la quant sum  et imprime au blototh avec afficheCouleurs et aller au fragment fast searche
            Toast.makeText(context, "Bon Commande Actulle Maj Cree", Toast.LENGTH_SHORT).show()
        },
        containerColor = Color(0xFF4CAF50)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Créer Bon Commande",
            tint = Color.White
        )
    }
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
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun CreateNewClientIcon(
    searchQuery: String,
    locationTracker: LocationTracker?,
    defaultId8BonVent: M8BonVent,
    onClientSelectedToToast: (M2Client) -> Unit,
    onResetSearchMode: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    isFournisseurMode: Boolean = false, // New parameter
    repositorysMainSetter: RepositorysMainSetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
) {
    val currentLocation = locationTracker?.getCurrentPosition()

    val newClient = M2Client(
        keyID =M2Client.generePushKey(),
        creationTimestamps = System.currentTimeMillis(),
        nom = searchQuery.ifEmpty {" Person ${M2Client.generePushKey().takeLast(4)}"},
        title = searchQuery.ifEmpty {
            if (isFournisseurMode) "Nouveau Fournisseur" else "Nouveau Client"
        },
        latitude = 36.720027701275505,
        longitude = 3.1436710147865483,
        caMarqueGpsEstOuvert = currentLocation != null,
        its_Fournisseur = isFournisseurMode, // Set based on toggle state
        snippet = currentLocation?.let {
            "Lat: ${String.format("%.6f", it.latitude)}, Lng: ${
                String.format(
                    "%.6f",
                    it.longitude
                )
            }"
        } ?: "Position non disponible",
        edite_Exact_Gps_est_fait = true,
        parentComptCreateurKEyID = focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: ""
    )

    val addedDefaultOnVentID8BonVentEtAdd = defaultId8BonVent.copy(
        creationTimestamps = System.currentTimeMillis(),
        parent_M2Client_KeyID = newClient.keyID,
        parent_M2Client_DebugInfos = newClient.nom,
        its_working_for_wholesaler = true
    )

    val updatedAppCompt = viewModel.getterFocusedVarsHandlerFacade.currentActive_M9AppCompt?.copy(
        onVentM8BonVentKey = addedDefaultOnVentID8BonVentEtAdd.keyID,
        onVentM8BonVentDebugInfos = addedDefaultOnVentID8BonVentEtAdd.get_DebugInfos()
    )

    IconButton(
        onClick = {
            repositorysMainSetter.upsert_M2Client(newClient)
            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
                addedDefaultOnVentID8BonVentEtAdd,
                updatedAppCompt
            )
            onClientSelectedToToast(newClient)
            onResetSearchMode()
        },
        modifier = Modifier.semantics(mergeDescendants = true) {
            set(SemanticsPropertyKey("Debug new M8BonVent"), addedDefaultOnVentID8BonVentEtAdd)
            set(SemanticsPropertyKey("Debug currentM9AppCompt avec new M8BonVent"), updatedAppCompt)
            set(SemanticsPropertyKey("Debug isFournisseurMode"), isFournisseurMode)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = if (isFournisseurMode) "Créer nouveau fournisseur" else "Créer nouveau client",
            tint = if (isFournisseurMode) Color(0xFFFF9800) else Color(0xFF2196F3)
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
