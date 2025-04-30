package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.A_APP4FragID1_MainScreen
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Z_AppsFather.Kotlin.Partage.Views.AnimatedIconLottieJsonFile
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import kotlin.math.roundToInt

class PanelsGroupeButtonHandler {
    private var _showDialogeControleFabs = mutableStateOf(false)
    private var _showVendeursDialog = mutableStateOf(false)

    private var _paneleGroupeButtonList = mutableStateOf(
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
            Box(modifier = Modifier
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
                        }

                        val couleurButton2 = Color(0xFF3F51B5)
                        FloatingActionButton(
                            onClick = {
                                // Toggle dialog state
                                setShowDialogControleFabs(!_showDialogeControleFabs.value)
                            }, modifier = Modifier.size(40.dp), containerColor = couleurButton2
                        ) {
                            // Change icon to indicate polygon creation
                            Icon(Icons.Filled.Shop, "setShowDialogControleFabs")
                        }

                        if (showLabels) {
                            Text(
                                "View Vendeurs", modifier = Modifier
                                    .background(
                                        Color(0xFF009688)
                                    )
                                    .padding(4.dp), color = Color.White
                            )
                        }
                        ControlButton(
                            onClick = { showLabels = !showLabels },
                            icon = Icons.Default.Info,
                            contentDescription = if (showLabels) "Hide labels" else "Show labels",
                            showLabels = showLabels,
                            labelText = if (showLabels) "Hide labels" else "Show labels",
                            containerColor = Color(0xFF3F51B5)
                        )
                    }

                    // Menu Button - Inlined from MenuButton function
                    ControlButton(
                        onClick = { showMenu = !showMenu },
                        icon = if (showMenu) Icons.Default.ExpandLess else Icons.Default.Warning,
                        contentDescription = if (showMenu) "Hide menu" else "Show menu",
                        showLabels = showLabels,
                        labelText = if (showMenu) "Hide" else "خاص بمدير التطبيق",
                        containerColor = Color(0xFFF44336)
                    )
                }
                AfficheComptsVendeursManager()
            }

            // Ensure the dialog state is being tracked properly
            LaunchedEffect(_showDialogeControleFabs.value) {
                // Dialog state tracking effect maintained with no logging
            }

            // Add the dialog display here to ensure it's in the composition hierarchy
            AfficheDialogesHeadApps()
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
                        AnimatedIconLottieJsonFile(
                            ressourceXml = icon, onClick = if (enabled) onClick else ({})
                        )
                    }
                }

                is Int -> {
                    // Support for direct resource IDs like R.raw.categ
                    Box(modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = enabled) {
                            onClick()
                        }
                        .background(
                            color = if (enabled) containerColor else Color.Gray, shape = CircleShape
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
    private fun AfficheComptsVendeursManager() {
        if (_showVendeursDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    _showVendeursDialog.value = false
                },
                title = { Text("Manage Vendeurs") },
                text = {
                    A_APP4FragID1_MainScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        _showVendeursDialog.value = false
                    }) {
                        Text("Close")
                    }
                }
            )
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

            // Create a new list with the updated item
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
