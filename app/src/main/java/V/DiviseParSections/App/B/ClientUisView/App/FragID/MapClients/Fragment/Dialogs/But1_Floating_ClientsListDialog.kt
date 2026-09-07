package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.performClickOnMarqueAction
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ScreenM14VentPeriod
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

/**
 * Floating dialog listing the clients currently displayed on the map (the
 * caller passes in the same mode-filtered + proximity-filtered list that's
 * rendered as markers, via getClientsCurrentlyVisibleOnMap — not the full,
 * unfiltered client database). Typing in the search field filters that list
 * by name/phone. Tapping a row simulates a tap on that client's marker, so
 * the same active Click_On_Marque mode (Standard / Ajouter Ciblage / Appeler
 * / Navigation / ...) that would fire from the map fires from here too.
 */
@Composable
fun But1_Floating_ClientsListDialog(
    mapView: MapView,
    clients: List<M2Client>,
    viewModel: MapClientsViewModel,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    list_M13TarificationInfos: List<M13TarificationInfos>,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var showPeriodsDialog by remember { mutableStateOf(false) }
    var modeMenuExpanded by remember { mutableStateOf(false) }

    var filterMenuExpanded by remember { mutableStateOf(false) }
    val currentFilterMode = viewModel.active_Datas.filter_marqueClient_enum_entries
        ?: MapClientsViewModel.VisibleClientsNow.showAll

    val compt = viewModel.active_Datas.active_M9Compt
    val currentMode = compt?.click_On_Marque ?: ActiveCentralValues.Click_On_Marque.Standart

    // Below 3 characters, search stays scoped to the clients currently shown
    // on the map (the same mode-filtered + proximity-filtered `clients` list
    // passed in). From 3 characters on, the search broadens to the full
    // client database, so the user can find any client by name — not only
    // one that's currently rendered as a marker.
    val allClients = viewModel.getter.repo2Client.datasValue
    val isCreditFilter = currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit ||
            currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term
    val isFournisseursCreditFilter = currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit ||
            currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit
    val isAnyCreditFilter = isCreditFilter || isFournisseursCreditFilter

    val repo8Bons = viewModel.getter.repo8BonVent.datasValue

    // Precalculate latest New_Situation_Credit montant for clients when under credit filter
    val creditMontantByClientKeyId = remember(allClients, repo8Bons, isCreditFilter, isFournisseursCreditFilter) {
        if (!isAnyCreditFilter) {
            emptyMap()
        } else {
            M2Client.calculateCreditsMap(
                clients = allClients,
                bons = repo8Bons,
                forFournisseurs = isFournisseursCreditFilter
            )
        }
    }

    val totalCreditChezClients = remember(creditMontantByClientKeyId) {
        creditMontantByClientKeyId.values.sum()
    }

    // Total credits of clients whose credit is flagged long term
    // (ces_credits_son_a_long_term == true). Shown next to the filter button
    // so it's visible regardless of which filter mode is active.
    val ignoredCreditMontantByClientKeyId = remember(allClients, repo8Bons, isFournisseursCreditFilter) {
        M2Client.calculateIgnoredCreditsMap(
            clients = allClients,
            bons = repo8Bons,
            forFournisseurs = isFournisseursCreditFilter
        )
    }
    val totalIgnoredCreditChezClients = remember(ignoredCreditMontantByClientKeyId) {
        ignoredCreditMontantByClientKeyId.values.sum()
    }

    val isGlobalModeFilter = currentFilterMode in listOf(
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term,
        MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME,
        MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR,
        MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter,
        MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX,
    )

    val baseClientsList = remember(clients, allClients, currentFilterMode, isCreditFilter, isGlobalModeFilter, creditMontantByClientKeyId) {
        if (isGlobalModeFilter) {
            filterClientsBasedOnMode(viewModel, currentFilterMode)
        } else {
            clients
        }
    }

    val filteredClients = remember(baseClientsList, allClients, searchQuery, isGlobalModeFilter) {
        val query = searchQuery.trim().lowercase()
        when {
            query.isEmpty() -> baseClientsList
            query.length < 3 -> baseClientsList.filter {
                it.nom.lowercase().contains(query) ||
                        it.numTelephone.lowercase().contains(query)
            }
            else -> {
                val searchPool = if (isGlobalModeFilter) baseClientsList else allClients
                searchPool.filter {
                    it.nom.lowercase().contains(query) ||
                            it.numTelephone.lowercase().contains(query)
                }
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
                        if (isAnyCreditFilter) {
                            val creditTitle = if (isFournisseursCreditFilter) "Total crédits fournisseurs" else "Total crédits"
                            Text(
                                text = "$creditTitle : ${"%.2f".format(totalCreditChezClients)} DA",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        if (totalIgnoredCreditChezClients > 0.0) {
                            Text(
                                text = "Crédits long terme : ${"%.2f".format(totalIgnoredCreditChezClients)} DA",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                            )
                        }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        TextButton(onClick = { modeMenuExpanded = true }) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color = currentMode.couleur, shape = CircleShape),
                            )
                            Text(
                                text = "Mode : ${getModeLabel(currentMode)}",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }     //<--
                        DropdownMenu(
                            expanded = modeMenuExpanded,
                            onDismissRequest = { modeMenuExpanded = false },
                            modifier = Modifier.widthIn(min = 240.dp),
                        ) {
                            // Les modes "toggle/état" (fixent un statut/flag sur le client au
                            // clic, plutôt que d'ouvrir une action) sont regroupés sous un
                            // header dédié, séparé du reste par un Divider — même pattern
                            // que le regroupement "Crédits" du menu Filtre ci-dessous.
                            // Les 4 boutons Set_* fixent explicitement les deux flags
                            // (client/fournisseur x court/long terme) en un clic, plutôt
                            // que de les inverser indépendamment.
                            val toggleClickModes = listOf(
                                ActiveCentralValues.Click_On_Marque.Set_Client_Court_Terme,
                                ActiveCentralValues.Click_On_Marque.Set_Client_Long_Terme,
                                ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Court_Terme,
                                ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Long_Terme,
                                ActiveCentralValues.Click_On_Marque.Toggle_Client_De_Jamale,
                            )
                            val otherClickModes = ActiveCentralValues.Click_On_Marque.entries
                                .filter { it !in toggleClickModes }

                            otherClickModes.forEach { clickMode ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(
                                                        color = clickMode.couleur,
                                                        shape = CircleShape
                                                    ),
                                            )
                                            Text(
                                                text = getModeLabel(clickMode),
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    },
                                    onClick = {
                                        compt?.let {
                                            viewModel.update_active_Compt(it.copy(click_On_Marque = clickMode))
                                        }
                                        viewModel.mapReloadTrigger++
                                        modeMenuExpanded = false
                                    },
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "Changeurs de statut",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                            toggleClickModes.forEach { clickMode ->
                                DropdownMenuItem(       //<--
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(
                                                        color = clickMode.couleur,
                                                        shape = CircleShape
                                                    ),
                                            )
                                            Text(
                                                text = getModeLabel(clickMode),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (clickMode == currentMode) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                                fontWeight = if (clickMode == currentMode) FontWeight.Bold else FontWeight.Normal,
                                            )
                                        }
                                    },
                                    onClick = {
                                        compt?.let {
                                            viewModel.update_active_Compt(it.copy(click_On_Marque = clickMode))
                                        }
                                        viewModel.mapReloadTrigger++
                                        modeMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }

                    Box {
                        TextButton(onClick = { filterMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Filtre : ${getFilterLabel(currentFilterMode)}",
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }      //<--
                        DropdownMenu(
                            expanded = filterMenuExpanded,
                            onDismissRequest = { filterMenuExpanded = false },
                            modifier = Modifier.widthIn(min = 240.dp),
                        ) {
                            // Les filtres liés au crédit sont regroupés sous un header
                            // "Crédits", séparé du reste par un Divider, avec leur
                            // couleur propre (celle définie sur l'enum) pour bien les
                            // distinguer des autres modes de filtre.
                            //
                            val creditFilterModes = listOf(
                                MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit,
                                MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term,
                                MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit,
                                MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit,
                            )
                            val otherFilterModes = MapClientsViewModel.VisibleClientsNow.entries
                                .filter { it !in creditFilterModes }

                            Text(
                                text = "Crédits",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                            creditFilterModes.forEach { filterMode ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(
                                                        color = filterMode.couleur,
                                                        shape = CircleShape,
                                                    ),
                                            )
                                            Text(
                                                text = getFilterLabel(filterMode),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (filterMode == currentFilterMode) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                                fontWeight = if (filterMode == currentFilterMode) FontWeight.Bold else FontWeight.Normal,
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.update_filter_marqueClient(filterMode)
                                        viewModel.mapReloadTrigger++
                                        filterMenuExpanded = false
                                    },
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            otherFilterModes.forEach { filterMode ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = getFilterLabel(filterMode),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (filterMode == currentFilterMode) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                            fontWeight = if (filterMode == currentFilterMode) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    },
                                    onClick = {
                                        viewModel.update_filter_marqueClient(filterMode)
                                        viewModel.mapReloadTrigger++
                                        filterMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }

                    TextButton(onClick = { showPeriodsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Périodes",
                            modifier = Modifier.padding(start = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

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
                        ) { client ->
                            val lastTransaction = remember(client.id, viewModel.mapReloadTrigger) {
                                viewModel.getter.repo8BonVent.datasValue
                                    ?.filter {
                                        it.parent_M2Client_KeyID == client.keyID
                                                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
                                    }
                                    ?.maxByOrNull { it.creationTimestamps }
                            }
                            ClientRow(
                                client = client,
                                currentMode = currentMode,
                                lastTransaction = lastTransaction,
                                getter = viewModel.getter,
                                onClick = {
                                    performClickOnMarqueAction(
                                        context = mapView.context,
                                        m2Client = client,
                                        currentMode = currentMode,
                                        viewModel = viewModel,
                                        fragmentNavigationHandler_NewProto = fragmentNavigationHandler_NewProto,
                                        list_M13TarificationInfos = list_M13TarificationInfos,
                                    )
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

    if (showPeriodsDialog) {
        Dialog(
            onDismissRequest = { showPeriodsDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
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
                        Text(
                            text = "Périodes de vente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(onClick = { showPeriodsDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                    ScreenM14VentPeriod(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ClientRow(
    client: M2Client,
    currentMode: ActiveCentralValues.Click_On_Marque,
    lastTransaction: M8BonVent?,
    getter: RepositorysMainGetter,
    onClick: () -> Unit,
) {
    val sumBonVents = lastTransaction?.let { lastTransaction.montant_principale_du_type }

    val creditLabel = if (lastTransaction != null && (sumBonVents ?: 0.0) > 0.0) {
        val dateHandler = DatesHandler()
        val date = dateHandler.getDateAndTimString(lastTransaction.creationTimestamps).date
        "%.2f DA".format(sumBonVents) + " · $date"
    } else {
        null
    }

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
            if (creditLabel != null) {
                Text(
                    text = "Crédit : $creditLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

private fun getFilterLabel(mode: MapClientsViewModel.VisibleClientsNow): String = when (mode) {
    MapClientsViewModel.VisibleClientsNow.showAll -> "Tous les clients"
    MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit -> "Crédit (court terme)"
    MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term -> "Crédit (long terme)"
    MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit -> "Crédit Fournisseurs / Grossistes (court terme)"
    MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit -> "Crédit Fournisseurs / Grossistes (long terme)"
    MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> "Commande confirmée"
    MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> "Commande livrée"
    MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> "Cible vendeur"
    MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> "Cible & Passé"
    MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly -> "Clients non absents"
    MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes -> "Collecteur commandes"
    MapClientsViewModel.VisibleClientsNow.showAtayClients -> "Atay / Moukassarat"
    MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> "Cible pour 2"
    MapClientsViewModel.VisibleClientsNow.showAlimentionlients -> "Alimentation"
    MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts -> "Produits confirmés"
}
