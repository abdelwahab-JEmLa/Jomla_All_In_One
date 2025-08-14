package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.find_its_Confirmation_de_Transaction
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_FragMap_Button_4(
    mapClientsViewModel: MapClientsViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "Client Filter Mode",
        icons = Pair(Icons.Default.FilterList, Icons.Default.ViewList),
        colors = Pair(Color.Red, Color.Green)
    )
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val currentVisibleClientsMode = currentValues.visibleClientsNow
    val keyID_currentActiveFocused_M14VentPeriode = focusedValuesGetter.currentActiveFocuced_M14VentPeriode.keyID

    // Determine if we're in "show all" mode (for admin) or targeted mode
    val isShowingAll = currentVisibleClientsMode == MapClientsViewModel.VisibleClientsNow.showAll
    val isAdmin = focusedValuesGetter.currentApp_Est_Admin

    val updatedButtonState = buttonState.copy(its_Active = isShowingAll)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 200f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }

    // State for dropdown menu - FIXED: Initialize as false
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
                            MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> "A_COMMANDE_CONFIRME"
                            MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> "COMMANDE_LIVRAI"
                            else -> "Show All"
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
                        showDropdown = true
                    },
                    containerColor = if (updatedButtonState.its_Active)
                        updatedButtonState.colors.second
                    else
                        updatedButtonState.colors.first
                ) {
                    Icon(
                        imageVector = if (updatedButtonState.its_Active)
                            updatedButtonState.icons.second
                        else
                            updatedButtonState.icons.first,
                        contentDescription = if (isShowingAll) "Switch to Targeted View" else "Switch to Show All",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    // Show All Clients option
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

                    // Filter for A_COMMANDE_CONFIRME
                    val visibleClientsNow1 = MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "A_COMMANDE_CONFIRME Filter",
                                color = if (currentVisibleClientsMode == visibleClientsNow1)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                visibleClientsNow = visibleClientsNow1
                            )
                            focusedValuesGetter.update_activeCentralValues(newValues)
                            showDropdown = false
                        }
                    )

                    // Filter for COMMANDE_LIVRAI
                    val visibleClientsNow2 = MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
                    val bons = repositorysMainGetter.repo8BonVent.datasValue

                    // Get accepted orders for current period
                    val acceptedBons = bons.filter { bon ->
                        val lastTransaction = find_its_Confirmation_de_Transaction(aCentralFacade.repositorysMainGetter, bon)
                        (bon.etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI)
                                && (lastTransaction?.parent_M14VentPeriod_KeyId ?: "") == keyID_currentActiveFocused_M14VentPeriode
                    }

                    // Get filtered clients based on mode
                    val filteredClients = filterClientsBasedOnMode(
                        viewModel = mapClientsViewModel,
                        currentFilterMode = visibleClientsNow2
                    )

                    DropdownMenuItem(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                // Add semantics for accepted orders
                                set(
                                    value = acceptedBons,
                                    key = SemanticsPropertyKey("acceptedBons")
                                )
                            }
                            .semantics(mergeDescendants = true) {
                                // Add semantics for filtered clients
                                set(
                                    value = filteredClients,
                                    key = SemanticsPropertyKey("filteredClients")
                                )
                            }
                            .semantics(mergeDescendants = true) {
                                // Add semantics for last COMMANDE_LIVRAI transaction
                                set(
                                    value = bons.lastOrNull { bon ->
                                        bon.etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                                    }?.let { bon ->
                                        find_its_Confirmation_de_Transaction(repositorysMainGetter, bon)
                                    },
                                    key = SemanticsPropertyKey("lastCommandeLivraiTransaction")
                                )
                            },
                        text = {
                            Text(
                                text = "COMMANDE_LIVRAI Filter (${acceptedBons.size})",
                                color = if (currentVisibleClientsMode == visibleClientsNow2)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            val newValues = currentValues.copy(
                                visibleClientsNow = visibleClientsNow2
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
