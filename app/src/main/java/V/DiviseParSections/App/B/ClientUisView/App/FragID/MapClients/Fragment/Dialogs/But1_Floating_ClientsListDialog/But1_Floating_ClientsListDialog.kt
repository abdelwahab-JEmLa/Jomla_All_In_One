package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_ClientsListDialog

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_Separated_FragMap_Button_1.getModeLabel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel.VisibleClientsNow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.AlertDialog
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
import kotlin.math.abs

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
    var showLivrerConfirmedDialog by remember { mutableStateOf(false) }
    // Modes dont l'activation (sélection dans le dropdown "Mode") doit être
    // confirmée avant d'être appliquée — Passer/Livrer un client affectent
    // ensuite chaque marqueur cliqué tant que le mode reste actif.
    var pendingConfirmClickMode by remember { mutableStateOf<ActiveCentralValues.Click_On_Marque?>(null) }
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
        if (query.length >= 3 && currentFilterMode != VisibleClientsNow.showAll) {
            viewModel.update_filter_marqueClient(VisibleClientsNow.showAll)
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
            VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit ||
            currentFilterMode ==
            VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term
    val isFournisseursCreditFilter = currentFilterMode ==
            VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit ||
            currentFilterMode ==
            VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit
    val isAnyCreditFilter = isCreditFilter || isFournisseursCreditFilter
    // Le mode actif est-il l'un des 2 filtres "long terme" (client ou
    // fournisseur) ? Détermine si le total/détail affiché doit venir de
    // calculateIgnoredCreditsMap (long terme) plutôt que calculateCreditsMap
    // (court terme) — sinon un fournisseur/client long terme n'apparaissait
    // jamais dans le total affiché, même s'il était bien inclus dans la liste
    // de clients filtrée par HandleFilter.filterClientsBasedOnMode.
    val isLongTermCreditFilter = currentFilterMode ==
            VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term ||
            currentFilterMode ==
            VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit

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

    // its_limited_a900 == false signifie que ce mode est "global" (ne dépend
    // pas de la position sur la carte) — voir VisibleClientsNow.
    val isGlobalModeFilter = !currentFilterMode.its_limited_a900

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
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {


                    item {
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
                                       //<--
                                       //TODO(1): ajout un button qui togle m9.lance_dialoge_client_et_ne_lance_pas_le_map_pour_ressources
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
                                            // Passer/Livrer un client ne s'activent qu'après
                                            // confirmation — voir le dialogue pendingConfirmClickMode
                                            // plus bas : une fois actif, chaque marqueur cliqué sur
                                            // la carte applique cette action tant que le mode reste
                                            // sélectionné, donc une activation accidentelle est
                                            // coûteuse à rattraper.
                                            if (clickMode == ActiveCentralValues.Click_On_Marque.Passe_Client ||
                                                clickMode == ActiveCentralValues.Click_On_Marque.Livre_Client
                                            ) {
                                                pendingConfirmClickMode = clickMode
                                            } else {
                                                compt?.let {
                                                    viewModel.update_active_Compt(it.copy(click_On_Marque = clickMode))
                                                }
                                                viewModel.mapReloadTrigger++
                                            }
                                            // Ne ferme pas le menu : voir commentaire sur toggleClickModes.
                                        },
                                    )
                                }
                            }
                        }
                    }
                    item {
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
                                    VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit,
                                    VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term,
                                    VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit,
                                    VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit,
                                    VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit,
                                )
                                val otherFilterModes = VisibleClientsNow.entries
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
                    }
                    item {
                        TextButton(onClick = { viewModel.passAllCibleClientsForCurrentVentPeriod() }) {
                            Icon(
                                imageVector = Icons.Default.SettingsBackupRestore,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Passer les ciblés",
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    item {
                        TextButton(onClick = { showLivrerConfirmedDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Livrer les confirmées",
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    item {
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

    if (pendingConfirmClickMode != null) {
        val mode = pendingConfirmClickMode!!
        val isLivre = mode == ActiveCentralValues.Click_On_Marque.Livre_Client
        AlertDialog(
            onDismissRequest = { pendingConfirmClickMode = null },
            title = {
                Text(if (isLivre) "Activer le mode \"Livrer le client\" ?" else "Activer le mode \"Passer le client\" ?")
            },
            text = {
                Text(
                    if (isLivre) {
                        "Tant que ce mode reste actif, chaque client dont vous cliquez le marqueur " +
                                "ou la ligne sera marqué comme livré."
                    } else {
                        "Tant que ce mode reste actif, chaque client dont vous cliquez le marqueur " +
                                "ou la ligne sera marqué comme passé."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    compt?.let {
                        viewModel.update_active_Compt(it.copy(click_On_Marque = mode))
                    }
                    viewModel.mapReloadTrigger++
                    pendingConfirmClickMode = null
                }) {
                    Text("Activer")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmClickMode = null }) {
                    Text("Annuler")
                }
            },
        )
    }

    if (showLivrerConfirmedDialog) {
        AlertDialog(
            onDismissRequest = { showLivrerConfirmedDialog = false },
            title = { Text("Livrer les clients confirmés ?") },
            text = {
                Text(
                    "Tous les clients dont la dernière commande est \"confirmée\" " +
                            "seront marqués comme \"livrés\". Cette action est irréversible."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.passAllConfirmedClientsToLivre()
                    showLivrerConfirmedDialog = false
                }) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLivrerConfirmedDialog = false }) {
                    Text("Annuler")
                }
            },
        )
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

@Composable
fun ClientRow(
    client: M2Client,
    currentMode: ActiveCentralValues.Click_On_Marque,
    lastTransaction: M8BonVent?,
    getter: RepositorysMainGetter,
    onClick: () -> Unit,
    onCenterOnMap: () -> Unit,
    onUpdateSecteur: () -> Unit,
) {
    val sumBonVents = lastTransaction?.let { lastTransaction.montant_principale_du_type }

    // montant_principale_du_type = sumCredits - sumVersements (voir
    // M8BonVent.fun_calculative_du_main_val). Pour un client normal, positif =
    // il nous doit de l'argent, négatif = on lui doit (versements en trop).
    // Pour un fournisseur c'est l'inverse — voir M2Client.calculateCreditsMap.
    // On affiche donc toujours le montant réel (+ ou -) avec un libellé qui
    // en précise le sens, plutôt que de cacher les valeurs négatives.
    val creditLabel = if (lastTransaction != null && sumBonVents != null && sumBonVents != 0.0) {
        val dateHandler = DatesHandler()
        val date = dateHandler.getDateAndTimString(lastTransaction.creationTimestamps).date
        val doitNousDeArgent = if (client.its_Fournisseur_Grossisst_A_Jomla) sumBonVents < 0.0 else sumBonVents > 0.0
        val prefix = if (doitNousDeArgent) "+" else "-"
        "$prefix${"%.2f".format(abs(sumBonVents))} DA · $date"
    } else {
        null
    }

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.Companion
                .size(10.dp)
                .background(color = currentMode.couleur, shape = CircleShape),
        )
        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = client.nom,
                fontWeight = FontWeight.Companion.Medium,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (client.numTelephone.isNotEmpty() && client.numTelephone != "null") {
                Text(
                    text = client.numTelephone,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Companion.Gray,
                )
            }
            if (client.secteur.isNotBlank()) {
                Text(
                    text = "Secteur : ${client.secteur}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Companion.Gray,
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
        // Ouvre le dialogue de modification du secteur pour CE client précis
        // (distinct du dialogue "Modifier le secteur" du FAB, qui met à jour
        // tous les clients ciblés d'un coup) — action indépendante de onClick.
        IconButton(onClick = onUpdateSecteur) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifier le secteur de ${client.nom}",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        // Centre la carte sur ce client sans déclencher l'action du mode actif
        // (Standard / Appeler / Navigation / ...) ni fermer le dialogue —
        // action indépendante de onClick.
        IconButton(onClick = onCenterOnMap) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centrer la carte sur ${client.nom}",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

fun getFilterLabel(mode: VisibleClientsNow): String = when (mode) {
    MapClientsViewModel.VisibleClientsNow.showAll -> "Tous les clients"
    VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit -> "Crédit (court terme)"
    VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term -> "Crédit (long terme)"
    VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit -> "Crédit Fournisseurs / Grossistes (court terme)"
    VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit -> "Crédit Fournisseurs / Grossistes (long terme)"
    VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit -> "Clients de Jamale avec crédit"
    VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> "Commande confirmée"
    VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> "Commande livrée"
    VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> "Cible vendeur"
    VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> "Cible & Passé"
    VisibleClientsNow.showNonAbsentClientsOnly -> "Clients non absents"
    VisibleClientsNow.affichePourCollecteurCommendes -> "Collecteur commandes"
    VisibleClientsNow.showAtayClients -> "Atay / Moukassarat"
    VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> "Cible pour 2"
    VisibleClientsNow.showAlimentionlients -> "Alimentation"
    VisibleClientsNow.showClientsWithConfirmedProducts -> "Produits confirmés"
    VisibleClientsNow.showNonDeletableClientsOnly -> "Clients non supprimables"
}
