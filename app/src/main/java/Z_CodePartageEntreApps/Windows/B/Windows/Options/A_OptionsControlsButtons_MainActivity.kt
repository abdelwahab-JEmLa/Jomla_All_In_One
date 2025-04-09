package Z_CodePartageEntreApps.Windows.B.Windows.Options

import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Windows.B.Windows.UI.LoadingContent
import Z_CodePartageEntreApps.Windows.B.Windows.ViewModel.ViewModelFragment_StartUpScreen
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.LabelsButton
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.ControlButton
import android.os.Build
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

const val TAG = "A_OptionsControlsButtons_Main"

@Composable
fun A_OptionsControlsButtons_Main(
    viewModel: ViewModelFragment_StartUpScreen = koinViewModel(),
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),

    ) {
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Collect UI state to checkADD_1_4_PeriodeVent if filtering is active
    val uiState by viewModel.uiStateFlow.collectAsState()
    val isFilterActive = uiState.isFilteringActive

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display loading content if data is loading
        if (uiState.isDataLoading) {
            LoadingContent(
                message = "Loading data...",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showMenu) {
                    val repositorysModel =
                        _0_0_HeadOfRepositorys_Repository.repositorys_Model

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                repositorysModel
                                    ._1_5_Vendeur_Repository
                                    .addDataAndReturneItVID(
                                        _1_5_Vendeur(
                                            deviceModelNom = Build.MODEL,
                                        )
                                    ) { newVID ->
                                        repositorysModel.activeVidRepository_1_5= newVID
                                    }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor =  Color(0xFF9C27B0)
                        ) {
                            Icon(
                                Icons.Filled.PhoneAndroid,
                                contentDescription = "Add New "
                            )
                        }

                        if (showLabels) {
                            Text(
                                "Add New Period",
                                modifier = Modifier
                                    .background(
                                        if (isFilterActive) Color(0xFF2196F3) else Color(
                                            0xFF4CAF50
                                        )
                                    )
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                repositorysModel
                                    ._1_4_PeriodeVent_Repository
                                    .addDataAndReturneItVID(
                                        _1_4_PeriodeVent(
                                            startDateInString = LocalDate.now()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                            vendeur_ParentVID =
                                                repositorysModel._1_5_Vendeur_Repository.modelDatasSnapList
                                                    .last { it.nom== }
                                            )
                                    )

                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = if (isFilterActive) Color(0xFF2196F3) else Color(
                                0xFF4CAF50
                            )
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add New Period"
                            )
                        }

                        if (showLabels) {
                            Text(
                                "Add New Period",
                                modifier = Modifier
                                    .background(
                                        if (isFilterActive) Color(0xFF009688) else Color(
                                            0xFF4CAF50
                                        )
                                    )
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Labels Button
                    LabelsButton(
                        showLabels = showLabels,
                        onShowLabelsChange = { showLabels = it }
                    )
                }

                // Menu Button
                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }
    }
}


@Composable
fun LabelsButton(
    showLabels: Boolean,
    onShowLabelsChange: (Boolean) -> Unit,
) {
    ControlButton(
        onClick = { onShowLabelsChange(!showLabels) },
        icon = Icons.Default.Info,
        contentDescription = if (showLabels) "Hide labels" else "Show labels",
        showLabels = showLabels,
        labelText = if (showLabels) "Hide labels" else "Show labels",
        containerColor = Color(0xFF3F51B5)
    )
}

@Composable
fun MenuButton(
    showLabels: Boolean,
    showMenu: Boolean,
    onShowMenuChange: (Boolean) -> Unit,
) {
    ControlButton(
        onClick = { onShowMenuChange(!showMenu) },
        icon = if (showMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
        contentDescription = if (showMenu) "Hide menu" else "Show menu",
        showLabels = showLabels,
        labelText = if (showMenu) "Hide" else "Achats Optiones",
        containerColor = Color(0xFF3F51B5)
    )
}
