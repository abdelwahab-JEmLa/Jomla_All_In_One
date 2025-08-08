package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
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
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_FragMap_Button_4(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "Client Filter Mode",
        icons = Pair(Icons.Default.FilterList, Icons.Default.ViewList),
        colors = Pair(Color.Red, Color.Green)
    )
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val currentVisibleClientsMode = currentValues.visibleClientsNow

    // Determine if we're in "show all" mode (for admin) or targeted mode
    val isShowingAll = currentVisibleClientsMode == MapClientsViewModel.VisibleClientsNow.showAll
    val isAdmin = focusedValuesGetter.currentApp_Est_Admin

    val updatedButtonState = buttonState.copy(its_Active = isShowingAll)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 200f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) } // Different Y position

    // State for dropdown menu
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
                        text = when (currentVisibleClientsMode) {
                            MapClientsViewModel.VisibleClientsNow.showAll -> "Show All"
                            MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> "Targeted"
                            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> "A_COMMANDE_CONFIRME "
                            else -> {"Show All"}
                        },
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = if (updatedButtonState.its_Active)
                                    updatedButtonState.colors.second.copy(alpha = 0.8f)
                                else
                                    updatedButtonState.colors.first.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                FloatingActionButton(
                    modifier = Modifier
                        .getSemanticsTag(updatedButtonState, "clientFilterButtonState")
                        .size(48.dp),
                    onClick = {
                        // FIXED: Show dropdown menu on click
                        showDropdown = true
                    },
                    containerColor = if (updatedButtonState.its_Active)
                        updatedButtonState.colors.second
                    else
                        updatedButtonState.colors.first
                ) {
                    Icon(
                        imageVector = if (updatedButtonState.its_Active)
                            updatedButtonState.icons.second // ViewList when showing all
                        else
                            updatedButtonState.icons.first, // FilterList when filtered/targeted
                        contentDescription = if (isShowingAll) "Switch to Targeted View" else "Switch to Show All",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    // Show All option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Show All Clients",
                                color = if (currentVisibleClientsMode == MapClientsViewModel.VisibleClientsNow.showAll)
                                    Color.Blue else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                visibleClientsNow = MapClientsViewModel.VisibleClientsNow.showAll
                            )
                            focusedValuesGetter.update_activeCentralValues(newValues)
                            showDropdown = false
                        }
                    )

                    val VisibleClientsNow_1 =
                        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = VisibleClientsNow_1.name,
                                color = if (currentVisibleClientsMode == VisibleClientsNow_1)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                visibleClientsNow = VisibleClientsNow_1
                            )
                            focusedValuesGetter.update_activeCentralValues(newValues)
                            showDropdown = false
                        }
                    )

                    val VisibleClientsNow_2 =
                        MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = VisibleClientsNow_2.name,
                                color = if (currentVisibleClientsMode == VisibleClientsNow_2)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                visibleClientsNow = VisibleClientsNow_2
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
