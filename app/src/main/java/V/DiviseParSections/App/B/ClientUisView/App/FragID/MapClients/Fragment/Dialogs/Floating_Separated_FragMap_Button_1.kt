// File 1: Fixed Button State and Floating Button Component with Dropdown
package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
        colors = Pair(currentModeColor, Color.Gray) // Use enum color as primary, gray as secondary
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
                if (updatedButtonState.showLabels) {
                    Text(
                        text = when (currentValues.click_On_Marque) {
                            ActiveCentralValues.Click_On_Marque.Standart -> "Standard"
                            ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Add Ciblage"
                            ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Vent Period"
                        },
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = currentModeColor.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Box {
                    FloatingActionButton(
                        modifier = Modifier
                            .getSemanticsTag(updatedButtonState, "buttonState")
                            .size(48.dp),
                        onClick = {
                            expanded = true
                        },
                        containerColor = currentModeColor
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Click On Marque Mode",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ActiveCentralValues.Click_On_Marque.entries.forEach { clickMode ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Color indicator
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(
                                                    color = clickMode.couleur,
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                        )
                                        Text(
                                            text = when (clickMode) {
                                                ActiveCentralValues.Click_On_Marque.Standart -> "Standard"
                                                ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Add Au Ciblage Clients"
                                                ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Affiche OnCommand VentPeriod Transaction"
                                            }
                                        )
                                    }
                                },
                                onClick = {
                                    val newValues = currentValues.copy(
                                        click_On_Marque = clickMode
                                    )
                                    focusedValuesGetter.update_activeCentralValues(newValues)

                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
