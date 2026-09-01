package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Title_Filter
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun But1_OnClickMode(
    buttonState: Button_State = Button_State.get_Default().copy(
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
                                compt?.let {
                                    val nextFilter = if (it.title_Filter == Title_Filter.Tout_Sauf_Nom_Si_Non_New) Title_Filter.Rien else Title_Filter.Tout_Sauf_Nom_Si_Non_New
                                    viewModel.update_active_Compt(it.copy(title_Filter = nextFilter))
                                }
                                viewModel.mapReloadTrigger++
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

private fun getModeIcon(mode: ActiveCentralValues.Click_On_Marque): ImageVector = when (mode) {
    ActiveCentralValues.Click_On_Marque.Standart -> Icons.Default.Info
    ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> Icons.Default.Add
    ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> Icons.Default.ShoppingCart
    ActiveCentralValues.Click_On_Marque.Lence_New_Command -> Icons.Default.Add
    ActiveCentralValues.Click_On_Marque.Call -> Icons.Default.Call
    ActiveCentralValues.Click_On_Marque.Navigate -> Icons.Default.Explore
    ActiveCentralValues.Click_On_Marque.Marck_Ferme -> Icons.Default.Close
    ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> Icons.Default.LocalShipping
    ActiveCentralValues.Click_On_Marque.Cree_et_envoi_whatsapp_pdf -> Icons.Default.Share
    ActiveCentralValues.Click_On_Marque.Delete_Client -> Icons.Default.Delete
    ActiveCentralValues.Click_On_Marque.Passe_Client -> Icons.Default.CheckCircle
    ActiveCentralValues.Click_On_Marque.Livre_Client -> Icons.Default.Check
}

fun getModeLabel(mode: ActiveCentralValues.Click_On_Marque): String = when (mode) {
    ActiveCentralValues.Click_On_Marque.Standart -> "Standard"
    ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Ajouter Ciblage"
    ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Afficher Commande"
    ActiveCentralValues.Click_On_Marque.Lence_New_Command -> "Lancer Nouvelle Commande"
    ActiveCentralValues.Click_On_Marque.Call -> "Appeler Client"
    ActiveCentralValues.Click_On_Marque.Navigate -> "Navigation GPS"
    ActiveCentralValues.Click_On_Marque.Marck_Ferme -> "Marquer Fermé"
    ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> "Marquer Livré"
    ActiveCentralValues.Click_On_Marque.Cree_et_envoi_whatsapp_pdf -> "Envoyer PDF WhatsApp"
    ActiveCentralValues.Click_On_Marque.Delete_Client -> "Supprimer Client"
    ActiveCentralValues.Click_On_Marque.Passe_Client -> "Passer le client"
    ActiveCentralValues.Click_On_Marque.Livre_Client -> "Livrer le client"
}

private fun getModeDescription(mode: ActiveCentralValues.Click_On_Marque): String = when (mode) {
    ActiveCentralValues.Click_On_Marque.Standart -> "Afficher les détails du client"
    ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Ajouter à la liste de ciblage"
    ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Voir le bon de commande actif"
    ActiveCentralValues.Click_On_Marque.Lence_New_Command -> "Créer et ouvrir directement une nouvelle commande"
    ActiveCentralValues.Click_On_Marque.Call -> "Lancer un appel téléphonique"
    ActiveCentralValues.Click_On_Marque.Navigate -> "Ouvrir dans Google Maps"
    ActiveCentralValues.Click_On_Marque.Marck_Ferme -> "Marquer le client comme fermé"
    ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> "Marquer la commande comme livrée"
    ActiveCentralValues.Click_On_Marque.Cree_et_envoi_whatsapp_pdf -> "Créer et envoyer le bon PDF via WhatsApp"
    ActiveCentralValues.Click_On_Marque.Delete_Client -> "Supprimer définitivement le client de la carte"
    ActiveCentralValues.Click_On_Marque.Passe_Client -> "Créer un bon Passe_Pour_Current_vent_period pour ce client"
    ActiveCentralValues.Click_On_Marque.Livre_Client -> "Créer un bon COMMANDE_LIVRAI pour ce client"
}
