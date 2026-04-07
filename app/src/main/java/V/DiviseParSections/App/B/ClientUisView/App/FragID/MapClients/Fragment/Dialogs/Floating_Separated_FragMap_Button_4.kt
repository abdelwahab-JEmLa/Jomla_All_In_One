package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.find_its_Confirmation_de_Transaction
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
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
    val filter_marqueClient_enum_entrie = mapClientsViewModel.active_Datas.filter_marqueClient_enum_entries
    val keyID_currentActiveFocused_M14VentPeriode = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
    val isAdmin = focusedValuesGetter.currentApp_Est_Admin

    val isShowingAll = filter_marqueClient_enum_entrie == MapClientsViewModel.VisibleClientsNow.showAll
            || filter_marqueClient_enum_entrie == null

    val updatedButtonState = buttonState.copy(its_Active = isShowingAll)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 200f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
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
                        text = getFilterLabelForMode(
                            filter_marqueClient_enum_entrie
                                ?: MapClientsViewModel.VisibleClientsNow.showAll
                        ),
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
                    onClick = { showDropdown = true },
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
                    val filterLabel1 = "Show All Clients"
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = filterLabel1,
                                color = if (filter_marqueClient_enum_entrie == MapClientsViewModel.VisibleClientsNow.showAll
                                    || filter_marqueClient_enum_entrie == null)
                                    Color.Blue else Color.Black
                            )
                        },
                        onClick = {
                            mapClientsViewModel.update_filter_marqueClient(MapClientsViewModel.VisibleClientsNow.showAll)
                            showDropdown = false
                        }
                    )

                    // Filter for A_COMMANDE_CONFIRME
                    val filterLabel2 = "A_COMMANDE_CONFIRME Filter"
                    val visibleClientsNow1 = MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = filterLabel2,
                                color = if (filter_marqueClient_enum_entrie == visibleClientsNow1)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            mapClientsViewModel.update_filter_marqueClient(visibleClientsNow1)
                            mapClientsViewModel.clearProximityFilter()
                            showDropdown = false
                        }
                    )

                    // Filter for COMMANDE_LIVRAI
                    val visibleClientsNow2 = MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
                    val bons = repositorysMainGetter.repo8BonVent.datasValue

                    val acceptedBons = bons.filter { bon ->
                        val lastTransaction = find_its_Confirmation_de_Transaction(aCentralFacade.repositorysMainGetter, bon)
                        (bon.etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI)
                                && (lastTransaction?.parent_M14VentPeriod_KeyId ?: "") == keyID_currentActiveFocused_M14VentPeriode
                    }

                    val filteredClients = filterClientsBasedOnMode(
                        viewModel = mapClientsViewModel,
                        currentFilterMode = visibleClientsNow2
                    )

                    val filterLabel3 = "COMMANDE_LIVRAI Filter (${acceptedBons.size})"
                    DropdownMenuItem(
                        modifier = Modifier
                        ,
                        text = {
                            Text(
                                text = filterLabel3,
                                color = if (filter_marqueClient_enum_entrie == visibleClientsNow2)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            mapClientsViewModel.update_filter_marqueClient(visibleClientsNow2)
                            showDropdown = false
                        }
                    )

                    // Filter for Credit Transactions
                    val visibleClientsNow3 = MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit

                    val creditBons = bons.filter { bon ->
                        val lastTransaction = find_its_Confirmation_de_Transaction(aCentralFacade.repositorysMainGetter, bon)
                        (bon.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit)
                                && (lastTransaction?.parent_M14VentPeriod_KeyId ?: "") == keyID_currentActiveFocused_M14VentPeriode
                    }

                    val creditFilteredClients = filterClientsBasedOnMode(
                        viewModel = mapClientsViewModel,
                        currentFilterMode = visibleClientsNow3
                    )

                    val filterLabel4 = "Credit Filter (${creditBons.size})"
                    DropdownMenuItem(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                set(
                                    value = creditBons,
                                    key = SemanticsPropertyKey("creditBons")
                                )
                            }
                            .semantics(mergeDescendants = true) {
                                set(
                                    value = creditFilteredClients,
                                    key = SemanticsPropertyKey("creditFilteredClients")
                                )
                            }
                            .semantics(mergeDescendants = true) {
                                set(
                                    value = bons.lastOrNull { bon ->
                                        bon.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                                    }?.let { bon ->
                                        find_its_Confirmation_de_Transaction(
                                            repositorysMainGetter,
                                            bon
                                        )
                                    },
                                    key = SemanticsPropertyKey("lastCreditTransaction")
                                )
                            },
                        text = {
                            Text(
                                text = filterLabel4,
                                color = if (filter_marqueClient_enum_entrie == visibleClientsNow3)
                                    Color.Red else Color.Black
                            )
                        },
                        onClick = {
                            mapClientsViewModel.update_filter_marqueClient(visibleClientsNow3)
                            showDropdown = false
                        }
                    )
                }
            }
        }
    }
}

fun getFilterLabelForMode(mode: MapClientsViewModel.VisibleClientsNow, count: Int? = null): String {
    return when (mode) {
        MapClientsViewModel.VisibleClientsNow.showAll -> "Show All Clients"
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME ->
            "A_COMMANDE_CONFIRME Filter"
        MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter ->
            count?.let { "COMMANDE_LIVRAI Filter ($it)" } ?: "COMMANDE_LIVRAI Filter"
        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit ->
            count?.let { "Credit Filter ($it)" } ?: "Credit Filter"
        MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR ->
            "Targeted Clients"
        else -> "Unknown Filter"
    }
}
