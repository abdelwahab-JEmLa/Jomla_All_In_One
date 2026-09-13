package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_Separated_FragMap_Button_1

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Title_Filter
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Button_State
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.roundToInt

@Composable
fun But1_OnClickMode(
    buttonState: Button_State = Button_State.Companion.get_Default().copy(
        text_Label = "Mode Selection",
        icons = Pair(Icons.Default.Remove, Icons.Default.Add)
    ),
    viewModel: MapClientsViewModel,
) {
    val compt = viewModel.active_Datas.active_M9Compt
    val currentMode = compt?.click_On_Marque ?: ActiveCentralValues.Click_On_Marque.Standart
    val currentModeColor = currentMode.couleur

    val updatedButtonState = buttonState.copy(
        its_Active = currentMode == ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients,
        colors = Pair(currentModeColor, Color.Gray)
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 200f) }
    var expanded by remember { mutableStateOf(false) }

    // État du dialogue "Modifier le secteur" (voir DropdownMenuItem plus bas).
    var showSecteurDialog by remember { mutableStateOf(false) }
    var secteurQuery by remember { mutableStateOf("") }
    var secteurSuggestionsExpanded by remember { mutableStateOf(false) }
    val secteurFocusRequester = remember { FocusRequester() }

    // État du dialogue "Options de titre" (voir DropdownMenuItem "Titre" plus bas).
    var showTitleOptionsDialog by remember { mutableStateOf(false) }

    // Secteurs distincts déjà utilisés par les clients, pour l'auto-complétion
    // à partir de 3 lettres saisies. Rafraîchi via mapReloadTrigger, comme le
    // reste de l'écran (voir commentaire "Màj optimiste du trigger" dans le
    // ViewModel).
    val distinctSecteurs = remember(viewModel.mapReloadTrigger) {
        viewModel.bProto_ClientsDataBase
            .map { it.secteur }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
    val filteredSecteurSuggestions = remember(secteurQuery, distinctSecteurs) {
        val q = secteurQuery.trim()
        if (q.length < 3) emptyList() else distinctSecteurs.filter { it.contains(q, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth.value - 100f)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (updatedButtonState.showLabels) {
                    Text(
                        text = getModeLabel(currentMode),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(
                                color = currentModeColor.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Box {
                    FloatingActionButton(
                        modifier = Modifier
                            .getSemanticsTag(updatedButtonState, "buttonState")
                            .size(56.dp),
                        onClick = { expanded = true },
                        containerColor = currentModeColor,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = getModeIcon(currentMode),
                            contentDescription = "Select Click On Marque Mode",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Quick-reset button: only shown when a non-default mode is active,
                    // lets the user clear back to Standart with a single tap instead of
                    // opening the dropdown and picking "Standard" manually.
                    if (currentMode != ActiveCentralValues.Click_On_Marque.Standart) {
                        FloatingActionButton(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-8).dp)
                                .size(22.dp),
                            onClick = {
                                compt?.let {
                                    viewModel.update_active_Compt(it.copy(click_On_Marque = ActiveCentralValues.Click_On_Marque.Standart))
                                }
                                viewModel.mapReloadTrigger++
                                expanded = false
                            },
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Réinitialiser le mode au standard",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    // Dropdown items below update the active click mode
                    // (compt.click_On_Marque) and trigger a map reload.
                    //<--

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.widthIn(min = 280.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF747680),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Passer tous les ciblés",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Ajoute 'Passé pour période actuelle' aux clients ciblés",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.passAllCibleClientsForCurrentVentPeriod()
                                expanded = false
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = Color(0xFF747680),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Livrer toutes les confirmées",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Passe les commandes confirmées en 'Livré'",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.passAllConfirmedClientsToLivre()
                                expanded = false
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFF747680),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "Modifier le secteur",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Change le secteur des clients ciblés",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                showSecteurDialog = true
                                expanded = false
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = null,
                                        tint = if (compt?.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New) Color(0xFF4CAF50) else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = if (compt?.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New) "Titre: Nom Seul" else "Titre: Standard",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = if (compt?.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New) "Masque les détails et le téléphone" else "Affiche le nom et les détails complets",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                showTitleOptionsDialog = true
                                expanded = false
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        ActiveCentralValues.Click_On_Marque.entries.forEach { clickMode ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = getModeIcon(clickMode),
                                            contentDescription = null,
                                            tint = clickMode.couleur,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = getModeLabel(clickMode),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = getModeDescription(clickMode),
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    color = clickMode.couleur,
                                                    shape = RoundedCornerShape(2.dp)
                                                )
                                        )
                                    }
                                },
                                onClick = {
                                    compt?.let {
                                        viewModel.update_active_Compt(it.copy(click_On_Marque = clickMode))
                                    }
                                    viewModel.mapReloadTrigger++
                                    expanded = false
                                },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSecteurDialog) {

        Dialog(onDismissRequest = {
            showSecteurDialog = false
            secteurQuery = ""
            secteurSuggestionsExpanded = false
        }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    // Le champ doit être focus (et le clavier ouvert) dès
                    // l'apparition du dialogue, avec une valeur initiale vide
                    // (secteurQuery démarre déjà à "" — voir sa déclaration).
                    LaunchedEffect(Unit) {
                        secteurFocusRequester.requestFocus()
                        keyboardController?.show()
                    }
                    Text(
                        text = "Modifier le secteur",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "Applique le secteur saisi à tous les clients actuellement ciblés",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                    )
                    OutlinedTextField(
                        value = secteurQuery,
                        onValueChange = {
                            secteurQuery = it
                            secteurSuggestionsExpanded = it.trim().length >= 3
                        },
                        label = { Text("Secteur") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(secteurFocusRequester),
                    )
                    // À partir de 3 lettres saisies, propose les secteurs
                    // distincts déjà utilisés par des clients et qui
                    // correspondent au texte tapé.
                    if (secteurSuggestionsExpanded && filteredSecteurSuggestions.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            filteredSecteurSuggestions.forEach { suggestion ->
                                Text(
                                    text = suggestion,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            secteurQuery = suggestion
                                            secteurSuggestionsExpanded = false
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
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
                            showSecteurDialog = false
                            secteurQuery = ""
                            secteurSuggestionsExpanded = false
                        }) {
                            Text("Annuler")
                        }
                        TextButton(
                            onClick = {
                                viewModel.updateSecteurForAllCibleClients(secteurQuery)
                                showSecteurDialog = false
                                secteurQuery = ""
                                secteurSuggestionsExpanded = false
                            },
                            enabled = secteurQuery.isNotBlank(),
                        ) {
                            Text("Valider")
                        }
                    }
                }
            }
        }
    }

    if (showTitleOptionsDialog) {
        Dialog(onDismissRequest = { showTitleOptionsDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Options de titre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "Choisissez les infos affichées sur les marqueurs de la carte",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                    )
                    // Nom seul vs standard (détails + téléphone) — ancien
                    // comportement du DropdownMenuItem "Titre", conservé ici.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(end = 8.dp)) {
                            Text(
                                text = "Nom seul",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = "Masque les détails et le téléphone sur le marqueur",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                        Switch(
                            checked = compt?.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New,
                            onCheckedChange = { checked ->
                                compt?.let {
                                    val nextFilter = if (checked) Title_Filter.Tout_Sauf_Nom_Si_Non_New else Title_Filter.Rien
                                    viewModel.update_active_Compt(it.copy(title_Filter = nextFilter))
                                }
                                viewModel.mapReloadTrigger++
                            },
                        )
                    }

                    // N'a de sens qu'en mode "Nom seul" (le suffixe est ajouté
                    // partout dans Marker.title(), mais ce contrôle ne
                    // s'affiche que quand ce mode est actif).
                    if (compt?.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Column(modifier = Modifier.padding(end = 8.dp)) {
                                Text(
                                    text = "Suffixe après le nom",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    text = "Ajoute un point/suffixe après le nom du client",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                )
                            }
                            Switch(
                                checked = compt?.titre_affiche_suffixe_apres_nom == true,
                                onCheckedChange = { checked ->
                                    compt?.let {
                                        viewModel.update_active_Compt(it.copy(titre_affiche_suffixe_apres_nom = checked))
                                    }
                                    viewModel.mapReloadTrigger++
                                },
                            )
                        }
                    }

                    // Masque (mode "Nom seul" uniquement) le libellé des
                    // clients "new"/"ز" — voir titre_masque_bulle_clients_new
                    // et son usage dans Marker.title() (A_B_MarkersHandler.kt).
                    if (compt?.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Column(modifier = Modifier.padding(end = 8.dp)) {
                                Text(
                                    text = "Masquer les clients \"new\"",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    text = "Cache le libellé des clients dont le nom contient \"new\" ou \"ز\"",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                )
                            }
                            Switch(
                                checked = compt?.titre_masque_bulle_clients_new != false,
                                onCheckedChange = { checked ->
                                    compt?.let {
                                        viewModel.update_active_Compt(it.copy(titre_masque_bulle_clients_new = checked))
                                    }
                                    viewModel.mapReloadTrigger++
                                },
                            )
                        }
                    }

                    // Affiche le secteur du client sur le marqueur.
                    // NB: nécessite le champ `titre_affiche_secteur: Boolean` sur
                    // M9Compt (même convention que les autres titre_affiche_*
                    // ci-dessous) — à ajouter côté modèle si ce n'est pas déjà fait.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(end = 8.dp)) {
                            Text(
                                text = "Secteur",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = "Affiche le secteur du client sous le nom",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                        Switch(
                            checked = compt?.titre_affiche_secteur == true,
                            onCheckedChange = { checked ->
                                compt?.let {
                                    viewModel.update_active_Compt(it.copy(titre_affiche_secteur = checked))
                                }
                                viewModel.mapReloadTrigger++
                            },
                        )
                    }

                    // Affiche les infos de la dernière transaction. Le libellé
                    // d'état (etateActuellementEst.nomArabe) n'est affiché sous
                    // le nom que si ce toggle est actif — voir lastTrxInfosLine()
                    // et son usage dans Marker.title() (A_B_MarkersHandler.kt),
                    // désormais cohérent entre toutes les branches du titre.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(end = 8.dp)) {
                            Text(
                                text = "Dernière transaction",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = "Affiche les infos de la dernière transaction sous le nom",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                        Switch(
                            checked = compt?.titre_affiche_last_trx_infos == true,
                            onCheckedChange = { checked ->
                                compt?.let {
                                    viewModel.update_active_Compt(it.copy(titre_affiche_last_trx_infos = checked))
                                }
                                viewModel.mapReloadTrigger++
                            },
                        )
                    }

                    if (compt?.titre_affiche_last_trx_infos == true) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(end = 8.dp)) {
                                Text(
                                    text = "Seulement états notables",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                )
                                Text(
                                    text = "Livré, Confirmé, Fermé ou Acheteur absent uniquement",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                )
                            }
                            Switch(
                                checked = compt?.titre_affiche_last_trx_que_etats_notables == true,
                                onCheckedChange = { checked ->
                                    compt?.let {
                                        viewModel.update_active_Compt(it.copy(titre_affiche_last_trx_que_etats_notables = checked))
                                    }
                                    viewModel.mapReloadTrigger++
                                },
                            )
                        }
                    }

                    // Affiche la bulle d'info du marqueur.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(end = 8.dp)) {
                            Text(
                                text = "Bulle du marqueur",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = "Affiche la bulle d'info au-dessus du marqueur",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                        Switch(
                            checked = compt?.titre_affiche_buble != false,
                            onCheckedChange = { checked ->
                                compt?.let {
                                    viewModel.update_active_Compt(it.copy(titre_affiche_buble = checked))
                                }
                                viewModel.mapReloadTrigger++
                            },
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    ) {
                        TextButton(onClick = { showTitleOptionsDialog = false }) {
                            Text("Fermer")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Small floating trigger button that opens the clients-list dialog
 * (But1_Floating_ClientsListDialog). Placed next to But1_OnClickMode.
 */
@Composable
fun But1_Floating_ClientsListButton(
    onClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 270f) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth.value - 100f)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier.size(48.dp),
                onClick = onClick,
                containerColor = Color.DarkGray,
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Afficher la liste des clients",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
