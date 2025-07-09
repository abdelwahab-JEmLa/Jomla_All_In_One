package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ScreenM14VentPeriod
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin.Partage.Views.AnimatedIconLottieJsonFileFF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

class PanelsGroupeButtonHandler {
    private var _showDialogeControleFabs = mutableStateOf(false)
    private var _showVendeursDialog = mutableStateOf(false)
    private var _show_Dialog_M9ComptApp_List = mutableStateOf(true)

    var _paneleGroupeButtonList = mutableStateOf(
        listOf(
            PanelsGroupeButtonDeClasse(
                PanelsGroupeButtonDeClasse.Keys.A_OptionsControlsButtons_A1FragID_3,
            ),
            PanelsGroupeButtonDeClasse(
                PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons, isVisible = false
            ),
            PanelsGroupeButtonDeClasse(PanelsGroupeButtonDeClasse.Keys.autres, isVisible = false),
        )
    )

    @Composable
    fun GroupeButtonsActivePanelsWindows() {
        var showMenu by remember { mutableStateOf(false) }
        var showLabels by remember { mutableStateOf(true) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
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
                    .padding(16.dp)) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showMenu) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val couleurButton2 = Color(0xFFF44336)
                            FloatingActionButton(
                                onClick = {
                                    setShowDialogControleFabs(!_showDialogeControleFabs.value)
                                }, modifier = Modifier.size(40.dp), containerColor = couleurButton2
                            ) {
                                Icon(Icons.Filled.Shop, "setShowDialogControleFabs")
                            }
                            if (showLabels) {
                                Text(
                                    "ShowDialogControleFabs", modifier = Modifier
                                        .background(
                                            couleurButton2
                                        )
                                        .padding(4.dp), color = Color.White
                                )
                            }
                        }

                        Button_Affiche_Dialog_List_AppsCompts(showLabels)
                        Button_Affiche_Dialog_ListVentPeriodes(showLabels)

                        ControlButton(
                            onClick = { showLabels = !showLabels },
                            icon = Icons.Default.Info,
                            contentDescription = if (showLabels) "Hide labels" else "Show labels",
                            showLabels = showLabels,
                            labelText = if (showLabels) "Hide labels" else "Show labels",
                            containerColor = Color(0xFF3F51B5)
                        )
                    }

                    // Menu ButtonAutreEtates - Inlined from MenuButton function
                    ControlButton(
                        onClick = { showMenu = !showMenu },
                        icon = if (showMenu) Icons.Default.ExpandLess else Icons.Default.Warning,
                        contentDescription = if (showMenu) "Hide menu" else "Show menu",
                        showLabels = showLabels,
                        labelText = if (showMenu) "Hide" else "خاص بمدير التطبيق",
                        containerColor = Color(0xFFF44336)
                    )
                }
                Affiche_VentPeriod_Manager()
            }

            LaunchedEffect(_showDialogeControleFabs.value) {
            }

            AfficheDialogesHeadApps()
        }
    }

    @Composable
    private fun Button_Affiche_Dialog_List_AppsCompts(
        showLabels: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val couleur = Color(0xFFF44336)
            FloatingActionButton(
                onClick = {
                    _show_Dialog_M9ComptApp_List.value = true
                }, modifier = Modifier.size(40.dp), containerColor = couleur
            ) {
                Icon(
                    Icons.Filled.PhoneAndroid, contentDescription = null
                )
            }
            if (showLabels) {
                Text(
                    "Button_Affiche_Dialog_List_AppsCompts", modifier = Modifier
                        .background(
                            couleur
                        )
                        .padding(4.dp), color = Color.White
                )
            }
        }
    }
    @Composable
    private fun Button_Affiche_Dialog_ListVentPeriodes(showLabels: Boolean) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val couleur = Color(0xFF9C27B0)
            FloatingActionButton(
                onClick = {
                    _showVendeursDialog.value = true
                }, modifier = Modifier.size(40.dp), containerColor = couleur
            ) {
                Icon(
                    Icons.Filled.PhoneAndroid, contentDescription = "View Vendeurs"
                )
            }
            if (showLabels) {
                Text(
                    "View M14PeriodesVent", modifier = Modifier
                        .background(
                            couleur
                        )
                        .padding(4.dp), color = Color.White
                )
            }
        }
    }

    @Composable
    fun ControlButton(
        onClick: () -> Unit,
        icon: Any,
        contentDescription: String,
        showLabels: Boolean,
        labelText: String,
        containerColor: Color,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (icon) {
                is ImageVector -> {
                    FloatingActionButton(
                        onClick = {
                            if (enabled) {
                                onClick()
                            }
                        },
                        modifier = modifier.size(40.dp),
                        containerColor = containerColor,
                    ) {
                        Icon(icon, contentDescription)
                    }
                }

                is LottieJsonGetterR_Raw_Icons -> {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = enabled) {
                                onClick()
                            }
                            .background(
                                color = if (enabled) containerColor else Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedIconLottieJsonFileFF(
                            ressourceXml = icon, onClick = if (enabled) onClick else ({})
                        )
                    }
                }

                is Int -> {
                    // Support for direct resource IDs like R.raw.categ
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = enabled) {
                                onClick()
                            }
                            .background(
                                color = if (enabled) containerColor else Color.Gray,
                                shape = CircleShape
                            ), contentAlignment = Alignment.Center) {
                    }
                }

                else -> {
                    throw IllegalArgumentException("Unsupported icon type")
                }
            }

            if (showLabels) {
                Text(
                    labelText,
                    modifier = Modifier
                        .background(if (enabled) containerColor else Color.Gray)
                        .padding(4.dp),
                    color = Color.White
                )
            }
        }
    }

    @Composable
    private fun Dialog_M9_Manager() {
        if (_show_Dialog_M9ComptApp_List.value) {
            Dialog(
                onDismissRequest = {
                    _show_Dialog_M9ComptApp_List.value = false
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = true
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 2.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ScreenM14VentPeriod()
                    }
                }
            }
        }
    }
    @Composable
    private fun Affiche_VentPeriod_Manager() {
        if (_showVendeursDialog.value) {
            Dialog(
                onDismissRequest = {
                    _showVendeursDialog.value = false
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = true
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 2.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ScreenM14VentPeriod()
                    }
                }
            }
        }
    }

    @Composable
    fun AfficheDialogesHeadApps() {
        // Get the current values from state
        val isDialogVisible = _showDialogeControleFabs.value

        if (isDialogVisible) {
            Dialog(
                onDismissRequest = {
                    setShowDialogControleFabs(false)
                },
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            "Control Panel", modifier = Modifier.padding(bottom = 16.dp)
                        )

                        _paneleGroupeButtonList.value.forEach { fabHandler ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        val updatedState = fabHandler.copy(
                                            isVisible = !fabHandler.isVisible
                                        )
                                        updatedStateFabGroupVisibility(updatedState)
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (fabHandler.isVisible) Color.Green else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(fabHandler.key.name)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {
                                setShowDialogControleFabs(false)
                            }, modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

    fun updatedStateFabGroupVisibility(updatedState: PanelsGroupeButtonDeClasse) {
        try {
            // Get current list value
            val currentList = _paneleGroupeButtonList.value
            val index = currentList.indexOfFirst { it.key == updatedState.key }

            if (index == -1) {
                return
            }

            // Create addNew new list with the updated item
            val updatedList = currentList.toMutableList()
            updatedList[index] = updatedState

            // Update the state (directly on the main thread for UI updates)
            _paneleGroupeButtonList.value = updatedList
        } catch (e: Exception) {
            // Silent exception handling (removed logging)
        }
    }

    data class PanelsGroupeButtonDeClasse(
        val key: Keys,
        val isVisible: Boolean = false,
    ) {
        enum class Keys {
            A_OptionsControlsButtons_A1FragID_3, MapSecteursPolygenHandelButtons, autres,
        }
    }

    fun setShowDialogControleFabs(show: Boolean) {
        _showDialogeControleFabs.value = show
    }
}
