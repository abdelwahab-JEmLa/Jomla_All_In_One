package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraFABProtoJuin3
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ViewList
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import kotlin.math.roundToInt

data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color, Color> = Pair(Color.White, Color.White),
    val icons: Pair<ImageVector, ImageVector> = Pair(Icons.Default.Remove, Icons.Default.Add),
    val description_Functionement: String = "",
) {
    companion object {
        fun get_Default(): Button_State {
            return Button_State()
        }
    }
}

@Composable
fun Floating_Separated_FragMap_Button_5(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons = Pair(Icons.Default.FilterList, Icons.Default.ViewList),
        colors = Pair(Color.Red, Color.Green)
    )
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val currentModeEditesProduit = currentValues.active_ModeEditesProduit
    val isModeEditeActive = currentModeEditesProduit != null

    val updatedButtonState = buttonState.copy(its_Active = isModeEditeActive)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 250f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 350f) }
    var showDropdown by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        offsetX = offsetX.coerceIn(0f, screenWidth.value - 150f)
                        offsetY = offsetY.coerceIn(0f, screenHeightDp.value - 150f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (updatedButtonState.showLabels) {
                    Text(
                        text = when (currentModeEditesProduit) {
                            ActiveCentralValues.ModeEditesProduit.PrixHanled -> "Prix Mode"
                            ActiveCentralValues.ModeEditesProduit.Standart -> "Standard"
                            null -> "No Mode"
                        },
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = when (currentModeEditesProduit) {
                                    ActiveCentralValues.ModeEditesProduit.PrixHanled ->
                                        currentModeEditesProduit.couleur.copy(alpha = 0.8f)
                                    ActiveCentralValues.ModeEditesProduit.Standart ->
                                        currentModeEditesProduit.couleur.copy(alpha = 0.8f)
                                    null -> Color.Gray.copy(alpha = 0.8f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                FloatingActionButton(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(
                                value = currentModeEditesProduit ?: "NoMode",
                                key = SemanticsPropertyKey("activeMode")
                            )
                        }
                        .size(48.dp),
                    onClick = { showDropdown = true },
                    containerColor = when (currentModeEditesProduit) {
                        ActiveCentralValues.ModeEditesProduit.PrixHanled ->
                            currentModeEditesProduit.couleur
                        ActiveCentralValues.ModeEditesProduit.Standart ->
                            currentModeEditesProduit.couleur
                        null -> Color.Gray
                    }
                ) {
                    Icon(
                        imageVector = if (updatedButtonState.its_Active)
                            updatedButtonState.icons.second
                        else
                            updatedButtonState.icons.first,
                        contentDescription = if (isModeEditeActive) "Switch to Normal Mode" else "Switch to Edit Mode",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                CameraFABProtoJuin3()

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    // ModeEditesProduit Standard option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Mode Standard",
                                color = if (currentModeEditesProduit == ActiveCentralValues.ModeEditesProduit.Standart)
                                    ActiveCentralValues.ModeEditesProduit.Standart.couleur
                                else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                active_ModeEditesProduit = if (currentModeEditesProduit == ActiveCentralValues.ModeEditesProduit.Standart)
                                    null
                                else
                                    ActiveCentralValues.ModeEditesProduit.Standart
                            )
                            focusedValuesGetter.update_activeCentralValues(newValues)
                            showDropdown = false
                        }
                    )

                    // ModeEditesProduit Prix option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Mode Prix",
                                color = if (currentModeEditesProduit == ActiveCentralValues.ModeEditesProduit.PrixHanled)
                                    ActiveCentralValues.ModeEditesProduit.PrixHanled.couleur
                                else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                active_ModeEditesProduit = if (currentModeEditesProduit == ActiveCentralValues.ModeEditesProduit.PrixHanled)
                                    null
                                else
                                    ActiveCentralValues.ModeEditesProduit.PrixHanled
                            )
                            focusedValuesGetter.update_activeCentralValues(newValues)
                            showDropdown = false
                        }
                    )

                    // Clear mode option
                    if (currentModeEditesProduit != null) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Clear Mode",
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                val newValues = currentValues.copy(
                                    active_ModeEditesProduit = null
                                )
                                focusedValuesGetter.update_activeCentralValues(newValues)
                                showDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}
