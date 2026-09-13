package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_ClientsListDialog

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_Separated_FragMap_Button_1.getModeLabel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.VisibleClientsNow
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.performClickOnMarqueAction
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ScreenM14VentPeriod
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
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.runtime.LaunchedEffect
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
import org.osmdroid.util.GeoPoint
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
    var showCreditBreakdown by remember { mutableStateOf(false) }

    var filterMenuExpanded by remember { mutableStateOf(false) }
    val currentFilterMode = viewModel.active_Datas.filter_marqueClient_enum_entries
        ?: MapClientsViewModel.VisibleClientsNow.showAll

    // Filtre additionnel par secteur (M2Client.secteur), indépendant de
    // currentFilterMode : se combine avec le mode de filtre actif plutôt que
    // de le remplacer (voir filteredClients plus bas).
    var showSecteurFilterDialog by remember { mutableStateOf(false) }
    var activeSecteurFilter by remember { mutableStateOf<String?>(null) }

    // Dialogue d'édition du secteur pour UN client précis (bouton crayon sur
    // chaque ligne, voir ClientRow.onUpdateSecteur) — distinct du dialogue du
    // FAB (But1_OnClickMode) qui met à jour tous les clients ciblés d'un coup.
    var clientForSecteurEdit by remember { mutableStateOf<M2Client?>(null) }
    var editSecteurQuery by remember { mutableStateOf("") }
    var editSecteurSuggestionsExpanded by remember { mutableStateOf(false) }

    val compt = viewModel.active_Datas.active_M9Compt
    val currentMode = compt?.click_On_Marque ?: ActiveCentralValues.Click_On_Marque.Standart

    // Dès que la recherche atteint 3 caractères, on bascule automatiquement le
    // filtre actif sur "Tous les clients" : sans ça, une recherche tapée
    // pendant qu'un filtre restrictif (crédit, cible, etc.) est actif ne
    // portait que sur les clients déjà retenus par ce filtre, ce qui
    // contredit le comportement voulu (chercher n'importe quel client de la
    // base dès 3 caractères — voir le commentaire sur allClients ci-dessous).
    LaunchedEffect(searchQuery) {
        val query = searchQuery.trim()
        if (query.length >= 3 && currentFilterMode != MapClientsViewModel.VisibleClientsNow.showAll) {
            viewModel.update_filter_marqueClient(MapClientsViewModel.VisibleClientsNow.showAll)
        }
    }

    // Below 3 characters, search stays scoped to the clients currently shown
    // on the map (the same mode-filtered + proximity-filtered `clients` list
    // passed in). From 3 characters on, the search broadens to the full
    // client database, so the user can find any client by name — not only
    // one that's currently rendered as a marker.
    val allClients = viewModel.getter.repo2Client.datasValue
    val distinctSecteurs = remember(allClients) {
        allClients
            .map { it.secteur }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
    val editSecteurSuggestions = remember(editSecteurQuery, distinctSecteurs) {
        val q = editSecteurQuery.trim()
        if (q.length < 3) emptyList() else distinctSecteurs.filter { it.contains(q, ignoreCase = true) }
    }
    val isCreditFilter = currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit ||
            currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term
    val isFournisseursCreditFilter = currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit ||
            currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit
    val isAnyCreditFilter = isCreditFilter || isFournisseursCreditFilter
    // Le mode actif est-il l'un des 2 filtres "long terme" (client ou
    // fournisseur) ? Détermine si le total/détail affiché doit venir de
    // calculateIgnoredCreditsMap (long terme) plutôt que calculateCreditsMap
    // (court terme) — sinon un fournisseur/client long terme n'apparaissait
    // jamais dans le total affiché, même s'il était bien inclus dans la liste
    // de clients filtrée par HandleFilter.filterClientsBasedOnMode.
    val isLongTermCreditFilter = currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term ||
            currentFilterMode ==
            MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit

    val repo8Bons = viewModel.getter.repo8BonVent.datasValue

    // Precalculate latest New_Situation_Credit montant for clients when under credit filter
    val creditMontantByClientKeyId = remember(allClients, repo8Bons, isCreditFilter, isFournisseursCreditFilter, isLongTermCreditFilter) {
        if (!isAnyCreditFilter) {
            emptyMap()
        } else if (isLongTermCreditFilter) {
            M2Client.calculateIgnoredCreditsMap(
                clients = allClients,
                bons = repo8Bons,
                forFournisseurs = isFournisseursCreditFilter
            )
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

    // Détail par client du total crédit affiché : nom + montant, trié du plus
    // gros crédit au plus petit, pour le clic sur le libellé "Total crédits".
    val creditBreakdownByClient = remember(creditMontantByClientKeyId, allClients) {
        creditMontantByClientKeyId.entries.mapNotNull { (keyId, montant) ->
            val client = allClients.find { it.keyID == keyId } ?: return@mapNotNull null
            client to montant
        }.sortedByDescending { it.second }
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

    val repo10Vents = viewModel.getter.repo10OperationVentCouleur.datasValue

    // Total des commandes confirmées dont le montant va arriver — voir
    // M8BonVent.calculateTotalCommandesConfirmees pour la logique complète
    // (partagée avec A_View_M14VentPeriod).
    val totalCommandesConfirmees = remember(allClients, repo8Bons, repo10Vents) {
        M8BonVent.calculateTotalCommandesConfirmees(
            clients = allClients,
            bons = repo8Bons,
            vents = repo10Vents,
            tariffs = viewModel.getter.repo13TarificationInfos.datasValue
        )
    }

    val isGlobalModeFilter = currentFilterMode in listOf(
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term,
        MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit,
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME,
        MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR,
        MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter,
        MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX,
        // Filtre global comme les autres ci-dessus : its_non_deletable_client_et_trxs
        // ne dépend pas de la position sur la carte, donc pas de restriction
        // de proximité pour ce filtre non plus.
        MapClientsViewModel.VisibleClientsNow.showNonDeletableClientsOnly,
    )

    val baseClientsList = remember(clients, allClients, currentFilterMode, isCreditFilter, isGlobalModeFilter, creditMontantByClientKeyId) {
        if (isGlobalModeFilter) {
            filterClientsBasedOnMode(viewModel, currentFilterMode)
        } else {
            clients
        }
    }

    val filteredClients = remember(baseClientsList, allClients, searchQuery, isGlobalModeFilter, activeSecteurFilter) {
        val query = searchQuery.trim().lowercase()
        val bySearch = when {
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
        // Le filtre secteur se combine avec la recherche/le mode ci-dessus,
        // il ne les remplace pas.
        activeSecteurFilter?.let { secteur -> bySearch.filter { it.secteur == secteur } } ?: bySearch
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
                            val creditTitle = when {
                                isFournisseursCreditFilter && isLongTermCreditFilter -> "Total crédits fournisseurs (long terme)"
                                isFournisseursCreditFilter -> "Total crédits fournisseurs"
                                isLongTermCreditFilter -> "Total crédits (long terme)"
                                else -> "Total crédits"
                            }
                            Text(
                                text = "$creditTitle : ${"%.2f".format(totalCreditChezClients)} DA",
                                modifier = Modifier.clickable { showCreditBreakdown = !showCreditBreakdown },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            if (showCreditBreakdown) {
                                Column(modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                                    if (creditBreakdownByClient.isEmpty()) {
                                        Text(
                                            text = "Aucun client avec crédit",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                        )
                                    } else {
                                        creditBreakdownByClient.forEach { (client, montant) ->
                                            Text(
                                                text = "${client.nom} : ${"%.2f".format(montant)} DA",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        // Rappel "Crédits long terme" affiché seulement quand le mode actif
                        // n'est PAS déjà un filtre long terme (sinon doublon avec le total
                        // ci-dessus qui montre déjà ce même montant).
                        if (!isLongTermCreditFilter && totalIgnoredCreditChezClients > 0.0) {
                            Text(
                                text = "Crédits long terme : ${"%.2f".format(totalIgnoredCreditChezClients)} DA",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                            )
                        }
                        if (totalCommandesConfirmees > 0.0) {
                            Text(
                                text = "Total commandes confirmées : ${"%.2f".format(totalCommandesConfirmees)} DA",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
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
                ) {//<--
                //TODO(1): cree moi  le button passe pour tout
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
                        }

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
                                ActiveCentralValues.Click_On_Marque.Set_Client_Court_Terme,      //<--
                                // Ces modes (+ Delete/Ferme/Cible/Livré via otherClickModes)
                                // ne ferment plus le dialogue après update : ils ne font que
                                // fixer un statut/flag, donc l'utilisateur reste dans la liste
                                // pour enchaîner sur d'autres clients sans rouvrir le menu.
                                ActiveCentralValues.Click_On_Marque.Set_Client_Long_Terme,
                                ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Court_Terme,
                                ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Long_Terme,
                                ActiveCentralValues.Click_On_Marque.Toggle_Client_De_Jamale,
                                ActiveCentralValues.Click_On_Marque.Toggle_Non_Deletable,
                            )
                            val otherClickModes = ActiveCentralValues.Click_On_Marque.entries
                                .filter { it !in toggleClickModes }
                            //<--
                            // Recentre le filtre de proximité (3km) sur la position actuelle
                            // de la carte. Utile après avoir scrollé/déplacé la carte pendant
                            // que le dialogue est ouvert : sans ça le filtre restait figé sur
                            // le centre capté à l'ouverture du dialogue (voir A_MapContent.kt,
                            // But1_Floating_ClientsListButton.onClick).
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = "Centrer sur la carte (3km)",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                },
                                onClick = {
                                    (mapView.mapCenter as? GeoPoint)?.let { center ->
                                        viewModel.relod_map_marques_du_3km_du_centre_map(
                                            center.latitude,
                                            center.longitude,
                                        )
                                    }
                                    modeMenuExpanded = false
                                },
                            )
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
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
                                        // Ne ferme pas le menu : voir commentaire sur toggleClickModes.
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

                            // Toggle_Non_Deletable (m2.its_non_deletable_client_et_trxs) est
                            // inclus dans toggleClickModes ci-dessus : comme les autres
                            // Set_*/Toggle_Client_De_Jamale, un clic ici active juste le mode
                            // sur le compte (click_On_Marque) — le flag n'est réellement
                            // basculé sur le client qu'au clic sur son marqueur/sa ligne,
                            // via performClickOnMarqueAction dans A_B_MarkersHandler.kt.
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
                                        // Ne ferme pas le menu : l'utilisateur peut enchaîner
                                        // sur un autre statut sans rouvrir le dropdown.
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
                                text = "Filtre : ${getFilterLabel(currentFilterMode)}" +
                                        (activeSecteurFilter?.let { " · $it" } ?: ""),
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }     
                        //<--     //<--
                        DropdownMenu(
                            expanded = filterMenuExpanded,
                            onDismissRequest = { filterMenuExpanded = false },
                            modifier = Modifier.widthIn(min = 240.dp),
                        ) {
                           
                            val creditFilterModes = listOf(
                                MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit,
                                MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term,
                                MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit,
                                MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit,
                                MapClientsViewModel.VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit,
                            )
                            val otherFilterModes = MapClientsViewModel.VisibleClientsNow.entries
                                .filter { it !in creditFilterModes }

                            Text(
                                text = "Crédits",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )   //<--
                            // "Court terme" (crédit affiché ici) = dernière situation
                            // crédit du client différente de zéro. Un client sans
                            // situation crédit enregistrée, ou dont la dernière
                            // situation est exactement 0, n'apparaît pas dans ce
                            // filtre — voir M2Client.calculateCreditsMap.
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

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // Filtre par secteur (M2Client.secteur) : se combine avec
                            // currentFilterMode ci-dessus au lieu de le remplacer — voir
                            // filteredClients. Un clic direct sur l'item réinitialise le
                            // filtre si un secteur est déjà actif ; sinon il ouvre le
                            // dialogue de sélection listant les secteurs distincts.
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = activeSecteurFilter?.let { "Secteur : $it   ✕" }
                                            ?: "Filtrer par secteur…",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (activeSecteurFilter != null) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                        fontWeight = if (activeSecteurFilter != null) FontWeight.Bold else FontWeight.Normal,
                                    )
                                },
                                onClick = {
                                    if (activeSecteurFilter != null) {
                                        activeSecteurFilter = null
                                        filterMenuExpanded = false
                                    } else {
                                        showSecteurFilterDialog = true
                                        filterMenuExpanded = false
                                    }
                                },
                            )
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
                                    // Marck_Ferme / Passe_Client / Livre_Client ne font que
                                    // fixer un statut sur le client (comme les modes
                                    // Set_*/Toggle_Client_De_Jamale plus haut) : on garde le
                                    // dialogue ouvert pour enchaîner sur d'autres clients.
                                    val modesSansFermeture = setOf(
                                        ActiveCentralValues.Click_On_Marque.Marck_Ferme,
                                        ActiveCentralValues.Click_On_Marque.Passe_Client,
                                        ActiveCentralValues.Click_On_Marque.Livre_Client,
                                    )
                                    if (currentMode !in modesSansFermeture) {
                                        onDismiss()
                                    }
                                },
                                onCenterOnMap = {
                                    mapView.controller.animateTo(GeoPoint(client.latitude, client.longitude))
                                    mapView.controller.setZoom(19.2)
                                    onDismiss()
                                },
                                onUpdateSecteur = {
                                    clientForSecteurEdit = client
                                    editSecteurQuery = client.secteur
                                    editSecteurSuggestionsExpanded = false
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

    if (showSecteurFilterDialog) {
        Dialog(
            onDismissRequest = { showSecteurFilterDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.7f)
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
                            text = "Filtrer par secteur",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(onClick = { showSecteurFilterDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                    Divider()
                    if (distinctSecteurs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Aucun secteur renseigné sur les clients",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(distinctSecteurs) { secteur ->
                                Text(
                                    text = secteur,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (secteur == activeSecteurFilter) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                    fontWeight = if (secteur == activeSecteurFilter) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            activeSecteurFilter = secteur
                                            showSecteurFilterDialog = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    clientForSecteurEdit?.let { client ->
        Dialog(onDismissRequest = {
            clientForSecteurEdit = null
            editSecteurQuery = ""
            editSecteurSuggestionsExpanded = false
        }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Modifier le secteur de ${client.nom}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedTextField(
                        value = editSecteurQuery,
                        onValueChange = {
                            editSecteurQuery = it
                            editSecteurSuggestionsExpanded = it.trim().length >= 3
                        },
                        label = { Text("Secteur") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    )
                    // À partir de 3 lettres saisies, propose les secteurs
                    // distincts déjà utilisés par des clients (même liste que
                    // pour le dialogue "Modifier le secteur" du FAB).
                    if (editSecteurSuggestionsExpanded && editSecteurSuggestions.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            editSecteurSuggestions.forEach { suggestion ->
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            editSecteurQuery = suggestion
                                            editSecteurSuggestionsExpanded = false
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    ) {
                        TextButton(onClick = {
                            clientForSecteurEdit = null
                            editSecteurQuery = ""
                            editSecteurSuggestionsExpanded = false
                        }) {
                            Text("Annuler")
                        }
                        TextButton(
                            onClick = {
                                viewModel.updateData(client.copy(secteur = editSecteurQuery.trim()))
                                clientForSecteurEdit = null
                                editSecteurQuery = ""
                                editSecteurSuggestionsExpanded = false
                            },
                        ) {
                            Text("Valider")
                        }
                    }
                }
            }
        }
    }
}

