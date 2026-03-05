package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
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
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_FragMap_Button_1(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "Mode Selection",
        icons = Pair(Icons.Default.Remove, Icons.Default.Add)
    )
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val isActive = currentValues.click_On_Marque == ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients

    // Get the color from the current Click_On_Marque enum
    val currentModeColor = currentValues.click_On_Marque.couleur

    val updatedButtonState = buttonState.copy(
        its_Active = isActive,
        colors = Pair(currentModeColor, Color.Gray)
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 200f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 200f) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y

                        offsetX = offsetX.coerceIn(0f, screenWidth.value - 100f)
                        offsetY = offsetY.coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Display mode label
                if (updatedButtonState.showLabels) {
                    Text(
                        text = getModeLabel(currentValues.click_On_Marque),
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
                    // Main FAB with icon based on current mode
                    FloatingActionButton(
                        modifier = Modifier
                            .getSemanticsTag(updatedButtonState, "buttonState")
                            .size(56.dp),
                        onClick = {
                            expanded = true
                        },
                        containerColor = currentModeColor,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = getModeIcon(currentValues.click_On_Marque),
                            contentDescription = "Select Click On Marque Mode",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Dropdown menu with all modes
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.widthIn(min = 280.dp)
                    ) {
                        ActiveCentralValues.Click_On_Marque.entries.forEach { clickMode ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        // Mode icon
                                        Icon(
                                            imageVector = getModeIcon(clickMode),
                                            contentDescription = null,
                                            tint = clickMode.couleur,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        // Mode text with description
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
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

                                        // Color indicator
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
                                    val newValues = currentValues.copy(
                                        click_On_Marque = clickMode
                                    )
                                    focusedValuesGetter.update_activeCentralValues(newValues)
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

// Helper function to get icon for each mode
private fun getModeIcon(mode: ActiveCentralValues.Click_On_Marque): ImageVector {
    return when (mode) {
        ActiveCentralValues.Click_On_Marque.Standart -> Icons.Default.Info
        ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> Icons.Default.Add
        ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> Icons.Default.ShoppingCart
        ActiveCentralValues.Click_On_Marque.Call -> Icons.Default.Call
        ActiveCentralValues.Click_On_Marque.Navigate -> Icons.Default.Explore
        ActiveCentralValues.Click_On_Marque.Marck_Ferme -> Icons.Default.Close
        ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> Icons.Default.LocalShipping
    }
}

// Helper function to get label for each mode
private fun getModeLabel(mode: ActiveCentralValues.Click_On_Marque): String {
    return when (mode) {
        ActiveCentralValues.Click_On_Marque.Standart -> "Standard"
        ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Ajouter Ciblage"
        ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Afficher Commande"
        ActiveCentralValues.Click_On_Marque.Call -> "Appeler Client"
        ActiveCentralValues.Click_On_Marque.Navigate -> "Navigation GPS"
        ActiveCentralValues.Click_On_Marque.Marck_Ferme -> "Marquer Fermé"
        ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> "Marquer Livré"
    }
}

// Helper function to get description for each mode
private fun getModeDescription(mode: ActiveCentralValues.Click_On_Marque): String {
    return when (mode) {
        ActiveCentralValues.Click_On_Marque.Standart -> "Afficher les détails du client"
        ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Ajouter à la liste de ciblage"
        ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Voir le bon de commande actif"
        ActiveCentralValues.Click_On_Marque.Call -> "Lancer un appel téléphonique"
        ActiveCentralValues.Click_On_Marque.Navigate -> "Ouvrir dans Google Maps"
        ActiveCentralValues.Click_On_Marque.Marck_Ferme -> "Marquer le client comme fermé"
        ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> "Marquer la commande comme livrée"
    }
}

